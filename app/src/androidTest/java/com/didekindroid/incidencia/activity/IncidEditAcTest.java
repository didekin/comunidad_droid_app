package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.incidservice.domain.Incidencia;
import com.didekin.incidservice.domain.IncidenciaUser;
import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.UiException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.common.utils.AppKeysForBundle.INCIDENCIA_USER_OBJECT;
import static com.didekindroid.incidencia.dominio.IncidenciaDomainTestUtils.doIncidencia;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.activity.utils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.cleanOptions;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.signUpAndUpdateTk;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_REAL_JUAN;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * User: pedro@didekin
 * Date: 19/01/16
 * Time: 16:57
 */
@RunWith(AndroidJUnit4.class)
public class IncidEditAcTest {

    IncidEditAc mActivity;
    UsuarioComunidad juanReal;
    IncidenciaUser incidJuanReal1;
    IncidEditMaxPowerFr mFragment;

    @Rule
    public IntentsTestRule<IncidEditAc> intentRule = new IntentsTestRule<IncidEditAc>(IncidEditAc.class){
        @Override
        protected void beforeActivityLaunched()
        {

            super.beforeActivityLaunched();
        }

        /**
         * Preconditions:
         * 1. An incidenciaUser id with powers to erase and modify is passed.
         * */
        @Override
        protected Intent getActivityIntent()
        {
            try {
                signUpAndUpdateTk(COMU_REAL_JUAN);
                juanReal = ServOne.seeUserComusByUser().get(0);
                incidJuanReal1 = new IncidenciaUser.IncidenciaUserBuilder(doIncidencia("Incidencia Real One", juanReal.getComunidad().getC_Id(), (short) 43))
                        .usuario(juanReal.getUsuario())
                        .importancia((short) 3).build();
                IncidenciaServ.regIncidenciaUser(incidJuanReal1);
                Incidencia incidenciaDb = IncidenciaServ.incidSeeByComu(juanReal.getComunidad().getC_Id()).get(0);
                incidJuanReal1 = IncidenciaServ.getIncidenciaUserWithPowers(incidenciaDb.getIncidenciaId());
            } catch (UiException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent();
            intent.putExtra(INCIDENCIA_USER_OBJECT.extra, incidJuanReal1);
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
    public void testOnCreate_1() throws Exception
    {
        assertThat(mActivity, notNullValue());
        onView(withId(R.id.incid_edit_fragment_container_ac)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_edit_maxpower_fr_layout)).check(matches(isDisplayed()));
    }
}