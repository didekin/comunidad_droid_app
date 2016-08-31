package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.usuario.dominio.Comunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.common.activity.BundleKey.COMUNIDAD_ID;
import static com.didekindroid.common.activity.BundleKey.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanTwoUsers;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.activity.utils.RolUi.PRE;
import static com.didekindroid.usuario.activity.utils.RolUi.PRO;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_REAL_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.typeUserComuData;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 30/08/15
 * Time: 17:38
 */
@RunWith(AndroidJUnit4.class)
public class RegUserComuAc_1_Test {

    private Intent intent;
    Comunidad comunidad;

    @Rule
    public IntentsTestRule<RegUserComuAc> intentRule = new IntentsTestRule<RegUserComuAc>(RegUserComuAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            // Segundo usuarioComunidad, con user2 y comunidad2.
            try {
                signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
            } catch (UiException | IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Intent getActivityIntent()
        {
            try {
                signUpAndUpdateTk(COMU_REAL_JUAN);
            } catch (UiException | IOException e) {
                e.printStackTrace();
            }
            List<Comunidad> comunidadesUserOne = null;
            try {
                comunidadesUserOne = ServOne.getComusByUser();
            } catch (UiException e) {
                e.printStackTrace();
            }
            comunidad = comunidadesUserOne != null ? comunidadesUserOne.get(0) : null;

            // We use that comunidad as the one to associate to the present user.
            intent = new Intent();
            intent.putExtra(COMUNIDAD_LIST_OBJECT.key, comunidad);
            return intent;
        }
    };

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(5000);
    }

    @Test
    public void testOnCreate() throws Exception
    {
        RegUserComuAc activity = intentRule.getActivity();

        assertThat(isRegisteredUser(activity), is(true));
        List<Comunidad> comunidadesUserOne = ServOne.getComusByUser();
        assertThat(comunidadesUserOne.size(), is(1));
        Comunidad comunidad2 = comunidadesUserOne.get(0);
        assertThat(comunidad2, not(is(COMU_REAL_JUAN.getComunidad())));
        assertThat(comunidad2, is(COMU_TRAV_PLAZUELA_PEPE.getComunidad()));

        assertThat(activity, notNullValue());
        assertThat(activity.getFragmentManager().findFragmentById(R.id.reg_usercomu_frg), notNullValue());
        Comunidad comunidadIntent = (Comunidad) intent.getSerializableExtra(COMUNIDAD_LIST_OBJECT.key);
        assertThat(comunidadIntent.getC_Id(), is(comunidad.getC_Id()));
        onView(withId(R.id.reg_usercomu_ac_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usercomu_frg)).check(matches(isDisplayed()));
    }

    @Test
    public void testWithExtra_1()
    {
        typeUserComuData("portalA", "escC", "plantaB", "puerta_1", PRO, PRE);
        onView(withId(R.id.reg_usercomu_button)).check(matches(isDisplayed())).perform(click());

        intended(hasExtra(COMUNIDAD_ID.key, comunidad.getC_Id()));
        onView(withId(R.id.see_usercomu_by_comu_frg)).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() throws Exception
    {
        cleanTwoUsers(USER_JUAN, USER_PEPE);
    }
}