package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.didekin.serviceone.domain.Comunidad;
import com.didekindroid.R;
import com.didekindroid.uiutils.UIutils;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.usuario.activity.utils.RolCheckBox.PRESIDENTE;
import static com.didekindroid.usuario.activity.utils.RolCheckBox.PROPIETARIO;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.COMUNIDAD_ID;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.*;
import static com.didekindroid.usuario.dominio.DomainDataUtils.*;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 13/09/15
 * Time: 11:30
 */
@RunWith(AndroidJUnit4.class)
public class RegUserAndUserComuAcTest_intent {

    private RegUserAndUserComuAc activity;
    private Intent intent;
    Comunidad comunidad;

    @Rule
    public IntentsTestRule<RegUserAndUserComuAc> intentRule = new IntentsTestRule<RegUserAndUserComuAc>
            (RegUserAndUserComuAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            // Precondition 2: the comunidad already exists.
            signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
            List<Comunidad> comunidadesUserOne = ServOne.getComunidadesByUser();
            comunidad = comunidadesUserOne.get(0);
            // Erase identification data.
            cleanWithTkhandler();
        }

        @Override
        protected Intent getActivityIntent()
        {
            // We pass the comunidad as an intent.
            intent = new Intent();
            intent.putExtra(COMUNIDAD_LIST_OBJECT.extra, comunidad);
            return intent;
        }
    };

    @Test
    public void testOnCreate() throws Exception
    {
        activity = intentRule.getActivity();

        assertThat(UIutils.isRegisteredUser(activity), is(false));
        assertThat(((Comunidad) intent.getSerializableExtra(COMUNIDAD_LIST_OBJECT.extra)), is(comunidad));
        assertThat(((Comunidad) intent.getSerializableExtra(COMUNIDAD_LIST_OBJECT.extra)).getC_Id(), is(comunidad
                .getC_Id()));

        assertThat(activity, notNullValue());
        assertThat(activity.getFragmentManager().findFragmentById(R.id.reg_usercomu_fr), notNullValue());

        onView(withId(R.id.reg_user_and_usercomu_ac_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_user_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usercomu_fr)).check(matches(isDisplayed()));
    }

    @Test
    public void testRegisterUserAndUserComu_1()
    {
        // Usuario data.
        onView(withId(R.id.reg_usuario_email_editT)).perform(scrollTo(), typeText(USER_JUAN_with_TF.getUserName()));
        onView(withId(R.id.reg_usuario_alias_ediT)).perform(scrollTo(), typeText(USER_JUAN_with_TF.getAlias()));
        onView(withId(R.id.reg_usuario_password_ediT)).perform(scrollTo(), typeText(USER_JUAN_with_TF.getPassword()));
        onView(withId(R.id.reg_usuario_password_confirm_ediT)).perform(scrollTo(), typeText(USER_JUAN_with_TF.getPassword()));
        onView(withId(R.id.reg_usuario_phone_prefix_ediT)).perform(scrollTo(),
                typeText(String.valueOf(USER_JUAN_with_TF.getPrefixTf())));
        onView(withId(R.id.reg_usuario_phone_editT)).perform(scrollTo(),
                typeText(String.valueOf(USER_JUAN_with_TF.getNumeroTf())),
                closeSoftKeyboard());

        // UsurioComunidad data.
        typeRegUserComuData("portalA", "escC", "plantaB", "puerta_1", PROPIETARIO, PRESIDENTE);
        onView(withId(R.id.reg_user_usercomu_button)).check(matches(isDisplayed())).perform(click());

        intended(hasExtra(COMUNIDAD_ID.extra, comunidad.getC_Id()));
        onView(withId(R.id.see_usercomu_by_comu_ac_frg_container)).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() throws Exception
    {
        cleanTwoUsers(USER_JUAN_with_TF, USER_PEPE);
    }
}

