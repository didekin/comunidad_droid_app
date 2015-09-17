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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.usuario.activity.utils.RolCheckBox.PRESIDENTE;
import static com.didekindroid.usuario.activity.utils.RolCheckBox.PROPIETARIO;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.*;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.COMUNIDAD_ID;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.usuario.dominio.DomainDataUtils.*;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 30/08/15
 * Time: 17:38
 */
@RunWith(AndroidJUnit4.class)
public class RegUserComuAcTest_intent {

    private RegUserComuAc activity;
    private Intent intent;
    Comunidad comunidad;

    @Rule
    public IntentsTestRule<RegUserComuAc> intentRule = new IntentsTestRule<RegUserComuAc>(RegUserComuAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            // Segundo usuarioComunidad, con user2 y comunidad2.
            signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        }

        @Override
        protected Intent getActivityIntent()
        {
            signUpAndUpdateTk(COMU_REAL_JUAN);
            List<Comunidad> comunidadesUserOne = ServOne.getComunidadesByUser();
            comunidad = comunidadesUserOne.get(0);

            // We use that comunidad as the one to associate to the present user.
            intent = new Intent();
            intent.putExtra(COMUNIDAD_LIST_OBJECT.extra, comunidad);
            return intent;
        }
    };

    @Test
    public void testOnCreate() throws Exception
    {
        activity = intentRule.getActivity();

        assertThat(UIutils.isRegisteredUser(activity), is(true));
        List<Comunidad> comunidadesUserOne = ServOne.getComunidadesByUser();
        assertThat(comunidadesUserOne.size(), is(1));
        Comunidad comunidad2 = comunidadesUserOne.get(0);
        assertThat(comunidad2, not(is(COMU_REAL_JUAN.getComunidad())));
        assertThat(comunidad2, is(COMU_TRAV_PLAZUELA_PEPE.getComunidad()));

        assertThat(activity, notNullValue());
        assertThat(activity.getFragmentManager().findFragmentById(R.id.reg_usercomu_frg), notNullValue());
        assertThat(((Comunidad) intent.getSerializableExtra(COMUNIDAD_LIST_OBJECT.extra)).getC_Id(), is(comunidad
                .getC_Id()));
        onView(withId(R.id.reg_usercomu_ac_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usercomu_frg)).check(matches(isDisplayed()));
    }

    @Test
    public void testWithExtra_1()
    {
        typeRegUserComuData("portalA", "escC", "plantaB", "puerta_1", PROPIETARIO, PRESIDENTE);
        onView(withId(R.id.reg_usercomu_button)).check(matches(isDisplayed())).perform(click());

        intended(hasExtra(COMUNIDAD_ID.extra,comunidad.getC_Id()));
        onView(withId(R.id.see_usercomu_by_comu_ac_frg_container)).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() throws Exception
    {
        cleanTwoUsers(USER_JUAN, USER_PEPE);
    }
}
