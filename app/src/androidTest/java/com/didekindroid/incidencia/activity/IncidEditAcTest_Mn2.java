package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.UiException;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.common.utils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.utils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.utils.AppKeysForBundle.INCIDENCIA_USER_OBJECT;
import static com.didekindroid.incidencia.IncidenciaTestUtils.doIncidencia;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.activity.utils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * User: pedro@didekin
 * Date: 19/01/16
 * Time: 16:57
 */

/**
 * Tests sobre menús, con un usuario administrador.
 */
@RunWith(AndroidJUnit4.class)
public class IncidEditAcTest_Mn2 {

    IncidEditAc mActivity;
    UsuarioComunidad juanPlazuelas;
    IncidenciaUser incidJuanPlazuelas;

    @Rule
    public IntentsTestRule<IncidEditAc> intentRule = new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {

            super.beforeActivityLaunched();
        }

        /**
         * Preconditions:
         * 1. An fIncidenciaUser with powers to erase and modify is received.
         * */
        @Override
        protected Intent getActivityIntent()
        {
            try {
                signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
                juanPlazuelas = ServOne.seeUserComusByUser().get(0);
                incidJuanPlazuelas = new IncidenciaUser.IncidenciaUserBuilder(doIncidencia("Incidencia Plazueles One", juanPlazuelas.getComunidad().getC_Id(), (short) 43))
                        .usuario(juanPlazuelas)
                        .importancia((short) 3).build();
                IncidenciaServ.regIncidenciaUser(incidJuanPlazuelas);
                IncidenciaUser incidenciaUserDb = IncidenciaServ.incidSeeByComu(juanPlazuelas.getComunidad().getC_Id()).get(0);
                incidJuanPlazuelas = IncidenciaServ.getIncidenciaUserWithPowers(incidenciaUserDb.getIncidencia().getIncidenciaId());
            } catch (UiException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent();
            intent.putExtra(INCIDENCIA_USER_OBJECT.extra, incidJuanPlazuelas);
            return intent;
        }
    };

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(4000);
    }

    @Before
    public void setUp() throws Exception
    {
        mActivity = intentRule.getActivity();
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_JUAN);
    }

//    ============================  TESTS  ===================================

    @Test
    public void testOnCreate() throws Exception
    {
        assertThat(incidJuanPlazuelas.getUsuarioComunidad().hasRoleAdministrador(), Matchers.is(true));
    }

    @Test
    public void testIncidResolucionReg_Mn() throws Exception
    {
        onView(withText(R.string.incid_resolucion_ac_mn)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.incid_resolucion_reg_ac_layout)).check(matches(isDisplayed()));
        intended(hasExtra(INCIDENCIA_USER_OBJECT.extra, incidJuanPlazuelas));
    }
}