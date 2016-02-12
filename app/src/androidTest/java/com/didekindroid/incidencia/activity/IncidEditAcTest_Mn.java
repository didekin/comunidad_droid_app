package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.incidservice.domain.IncidenciaUser;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.common.UiException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static com.didekindroid.common.utils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.utils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.utils.AppKeysForBundle.INCIDENCIA_USER_OBJECT;
import static com.didekindroid.incidencia.IncidenciaTestUtils.doIncidencia;
import static com.didekindroid.incidencia.activity.utils.IncidenciaMenuTestUtils.INCID_COMMENTS_SEE_AC;
import static com.didekindroid.incidencia.activity.utils.IncidenciaMenuTestUtils.INCID_COMMENT_REG_AC;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.activity.utils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_REAL_JUAN;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;

/**
 * User: pedro@didekin
 * Date: 19/01/16
 * Time: 16:57
 */

/**
 * Tests sobre opciones de menú.
 */
@RunWith(AndroidJUnit4.class)
public class IncidEditAcTest_Mn {

    IncidEditAc mActivity;
    UsuarioComunidad juanReal;
    IncidenciaUser incidJuanReal1;

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
                signUpAndUpdateTk(COMU_REAL_JUAN);
                juanReal = ServOne.seeUserComusByUser().get(0);
                incidJuanReal1 = new IncidenciaUser.IncidenciaUserBuilder(doIncidencia("Incidencia Real One", juanReal.getComunidad().getC_Id(), (short) 43))
                        .usuario(juanReal)
                        .importancia((short) 3).build();
                IncidenciaServ.regIncidenciaUser(incidJuanReal1);
                IncidenciaUser incidenciaUserDb = IncidenciaServ.incidSeeByComu(juanReal.getComunidad().getC_Id()).get(0);
                incidJuanReal1 = IncidenciaServ.getIncidenciaUserWithPowers(incidenciaUserDb.getIncidencia().getIncidenciaId());
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
    public void testIncidCommentReg_Mn() throws Exception
    {
        INCID_COMMENT_REG_AC.checkMenuItem_WTk(mActivity);
        intended(hasExtra(INCIDENCIA_USER_OBJECT.extra, incidJuanReal1));
    }

    @Test
    public void testIncidCommentsSee_Mn() throws Exception
    {
        INCID_COMMENTS_SEE_AC.checkMenuItem_WTk(mActivity);
        intended(hasExtra(INCIDENCIA_USER_OBJECT.extra, incidJuanReal1));
    }
}