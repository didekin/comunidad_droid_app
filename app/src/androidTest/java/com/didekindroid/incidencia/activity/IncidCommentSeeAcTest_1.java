package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.incidencia.dominio.IncidImportancia;
import com.didekin.incidencia.dominio.IncidenciaUser;
import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekinaar.exception.UiException;
import com.didekinaar.usuario.testutil.UsuarioDataTestUtils;
import com.didekindroid.R;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekinaar.security.TokenIdentityCacher.TKhandler;
import static com.didekinaar.testutil.AarActivityTestUtils.checkUp;
import static com.didekinaar.testutil.AarActivityTestUtils.clickNavigateUp;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.incidencia.IncidService.IncidenciaServ;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_COMMENT_REG_AC;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doIncidencia;
import static com.didekindroid.usuariocomunidad.UserComuService.AppUserComuServ;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.signUpAndUpdateTk;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 08/02/16
 * Time: 10:28
 */

/**
 * Tests sin comentarios registrados.
 */
@RunWith(AndroidJUnit4.class)
public class IncidCommentSeeAcTest_1 {

    IncidCommentSeeAc mActivity;
    IncidImportancia incidJuanReal1;
    int activityLayoutId = R.id.incid_comments_see_ac;
    int fragmentLayoutId = R.id.incid_comments_see_fr_layout;

    @Rule
    public IntentsTestRule<IncidCommentSeeAc> activityRule = new IntentsTestRule<IncidCommentSeeAc>(IncidCommentSeeAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
        }

        @Override
        protected Intent getActivityIntent()
        {
            try {
                signUpAndUpdateTk(COMU_REAL_JUAN);
                UsuarioComunidad juanReal = AppUserComuServ.seeUserComusByUser().get(0);
                incidJuanReal1 = new IncidImportancia.IncidImportanciaBuilder(
                        doIncidencia(juanReal.getUsuario().getUserName(), "Incidencia Real One", juanReal.getComunidad().getC_Id(), (short) 43))
                        .usuarioComunidad(juanReal)
                        .importancia((short) 3).build();
                IncidenciaServ.regIncidImportancia(incidJuanReal1);
                IncidenciaUser incidenciaUser = IncidenciaServ.seeIncidsOpenByComu(juanReal.getComunidad().getC_Id()).get(0);
                incidJuanReal1 = IncidenciaServ.seeIncidImportancia(incidenciaUser.getIncidencia().getIncidenciaId()).getIncidImportancia();
            } catch ( IOException | UiException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent();
            intent.putExtra(INCIDENCIA_OBJECT.key, incidJuanReal1.getIncidencia());
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
        mActivity = activityRule.getActivity();
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN);
    }

    @Test
    public void testOnCreate_1() throws Exception
    {
        assertThat(TKhandler.isRegisteredUser(), is(true));
        assertThat(mActivity, notNullValue());
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        onView(withId(activityLayoutId)).check(matches(isDisplayed()));
        onView(withId(fragmentLayoutId)).check(matches(isDisplayed()));

        // Verificamos visibilidad del menú cuando la incidencia está abierta.
        assertThat(incidJuanReal1.getIncidencia().getFechaCierre(), nullValue());
        onView(withId(R.id.incid_comment_reg_ac_mn)).check(matches(isDisplayed()));

        // No hay comentarios registrados.
        onView(withId(android.R.id.list)).check(matches(not(isDisplayed())));
        onView(withId(android.R.id.empty)).check(matches(isDisplayed()));

        clickNavigateUp();
    }

//  ============================ MENU ======================================

    @Test
    public void testIncidCommentRegMn() throws InterruptedException
    {

        INCID_COMMENT_REG_AC.checkMenuItem_WTk(mActivity);
        intended(hasExtra(INCIDENCIA_OBJECT.key, incidJuanReal1.getIncidencia()));
        checkUp(activityLayoutId, fragmentLayoutId);
    }
}