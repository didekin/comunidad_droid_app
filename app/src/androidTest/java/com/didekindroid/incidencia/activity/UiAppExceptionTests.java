package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.common.exception.ErrorBean;
import com.didekin.incidencia.dominio.IncidImportancia;
import com.didekin.incidencia.dominio.IncidenciaUser;
import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekinaar.exception.UiAarException;
import com.didekinaar.mock.MockActivity;
import com.didekinaar.testutil.CleanUserEnum;
import com.didekindroid.R;
import com.didekindroid.incidencia.exception.UiAppException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekin.common.exception.DidekinExceptionMsg.INCIDENCIA_COMMENT_WRONG_INIT;
import static com.didekin.common.exception.DidekinExceptionMsg.INCIDENCIA_NOT_REGISTERED;
import static com.didekin.common.exception.DidekinExceptionMsg.INCIDENCIA_USER_WRONG_INIT;
import static com.didekin.common.exception.DidekinExceptionMsg.RESOLUCION_DUPLICATE;
import static com.didekinaar.security.TokenHandler.TKhandler;
import static com.didekinaar.testutil.AarActivityTestUtils.checkToastInTest;
import static com.didekinaar.testutil.AarActivityTestUtils.cleanOptions;
import static com.didekinaar.testutil.AarActivityTestUtils.signUpAndUpdateTk;
import static com.didekinaar.testutil.UsuarioTestUtils.COMU_PLAZUELA5_JUAN;
import static com.didekinaar.usuariocomunidad.AarUserComuService.AarUserComuServ;
import static com.didekinaar.utils.UIutils.isRegisteredUser;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doIncidencia;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 17/11/15
 * Time: 10:07
 */
@SuppressWarnings({"ThrowableInstanceNeverThrown", "ThrowableResultOfMethodCallIgnored"})
@RunWith(AndroidJUnit4.class)
public class UiAppExceptionTests  {

    IncidImportancia mIncidJuanPlazuelas;
    MockActivity mActivity;

    @Rule
    public IntentsTestRule<MockActivity> intentRule = new IntentsTestRule<MockActivity>(MockActivity.class) {

        @Override
        protected Intent getActivityIntent()
        {
            try {
                signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
                UsuarioComunidad juanPlazuelas = AarUserComuServ.seeUserComusByUser().get(0);
                mIncidJuanPlazuelas = new IncidImportancia.IncidImportanciaBuilder(
                        doIncidencia(juanPlazuelas.getUsuario().getUserName(), "Incidencia Plazueles", juanPlazuelas.getComunidad().getC_Id(), (short) 43))
                        .usuarioComunidad(juanPlazuelas)
                        .importancia((short) 3).build();
                IncidenciaServ.regIncidImportancia(mIncidJuanPlazuelas);
                IncidenciaUser incidenciaUserDb = IncidenciaServ.seeIncidsOpenByComu(juanPlazuelas.getComunidad().getC_Id()).get(0);
                mIncidJuanPlazuelas = IncidenciaServ.seeIncidImportancia(incidenciaUserDb.getIncidencia().getIncidenciaId()).getIncidImportancia();
            } catch (UiAppException | IOException | UiAarException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent();
            intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, mIncidJuanPlazuelas);
            return intent;
        }
        @Override
        protected void beforeActivityLaunched()
        {
            super.beforeActivityLaunched();
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
        cleanOptions(CleanUserEnum.CLEAN_JUAN);
        Thread.sleep(3000);
    }

    //  ===========================================================================

    @Test
    public void testIncidReg()
    {
        // Preconditions.
        assertThat(isRegisteredUser(mActivity), is(true));
        assertThat(TKhandler.getTokenInCache(), notNullValue());

        final UiAppException ue = new UiAppException(new ErrorBean(INCIDENCIA_NOT_REGISTERED));
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(mActivity, new Intent());
            }
        });

        checkToastInTest(R.string.incidencia_not_registered, mActivity);
        onView(withId(R.id.incid_reg_ac_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginIncid() throws Exception
    {
        final UiAppException ue = new UiAppException(new ErrorBean(INCIDENCIA_USER_WRONG_INIT));

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(mActivity, new Intent());
            }
        });

        checkToastInTest(R.string.user_without_powers, mActivity);
        onView(withId(R.id.login_ac_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void testIncidSeeByComu() throws Exception
    {
        // Preconditions.
        assertThat(isRegisteredUser(mActivity), is(true));
        assertThat(TKhandler.getTokenInCache(), notNullValue());

        final UiAppException ue = new UiAppException(new ErrorBean(INCIDENCIA_COMMENT_WRONG_INIT));
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(mActivity, new Intent());
            }
        });

        checkToastInTest(R.string.incidencia_wrong_init, mActivity);
        onView(withId(R.id.incid_see_open_by_comu_ac)).check(matches(isDisplayed()));
    }

    @Test
    public void testResolucionDup() throws Exception
    {
        // Preconditions.
        final Intent intentIn = new Intent();
        intentIn.putExtra(INCID_IMPORTANCIA_OBJECT.key, mActivity.getIntent().getSerializableExtra(INCID_IMPORTANCIA_OBJECT.key));

        final UiAppException ue = new UiAppException(new ErrorBean(RESOLUCION_DUPLICATE));

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(mActivity, intentIn);
            }
        });
        checkToastInTest(R.string.resolucion_duplicada, mActivity);
        onView(withId(R.id.incid_edit_fragment_container_ac)).check(matches(isDisplayed()));
    }

    //  ============================== HELPERS  ===================================
}