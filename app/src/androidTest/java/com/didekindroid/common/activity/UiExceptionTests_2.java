package com.didekindroid.common.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.usuario.testutils.CleanUserEnum;

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
import static com.didekin.common.exception.DidekinExceptionMsg.RESOLUCION_DUPLICATE;
import static com.didekin.common.exception.DidekinExceptionMsg.USER_DATA_NOT_MODIFIED;
import static com.didekindroid.common.activity.BundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.common.activity.TokenHandler.TKhandler;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doIncidencia;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
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
public class UiExceptionTests_2 extends UiExceptionAbstractTest {

    IncidImportancia mIncidJuanPlazuelas;
    private MockActivity mActivity;

    @Rule
    public IntentsTestRule<MockActivity> intentRule = new IntentsTestRule<MockActivity>(MockActivity.class) {

        @Override
        protected Intent getActivityIntent()
        {
            try {
                signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
                UsuarioComunidad juanPlazuelas = ServOne.seeUserComusByUser().get(0);
                mIncidJuanPlazuelas = new IncidImportancia.IncidImportanciaBuilder(
                        doIncidencia(juanPlazuelas.getUsuario().getUserName(), "Incidencia Plazueles", juanPlazuelas.getComunidad().getC_Id(), (short) 43))
                        .usuarioComunidad(juanPlazuelas)
                        .importancia((short) 3).build();
                IncidenciaServ.regIncidImportancia(mIncidJuanPlazuelas);
                IncidenciaUser incidenciaUserDb = IncidenciaServ.seeIncidsOpenByComu(juanPlazuelas.getComunidad().getC_Id()).get(0);
                mIncidJuanPlazuelas = IncidenciaServ.seeIncidImportancia(incidenciaUserDb.getIncidencia().getIncidenciaId()).getIncidImportancia();
            } catch (UiException | IOException e) {
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
        Thread.sleep(4000);
    }

    //  ===========================================================================

    @Test
    public void testIncidReg()
    {
        // Preconditions.
        assertThat(isRegisteredUser(mActivity), is(true));
        assertThat(TKhandler.getTokensCache(), notNullValue());

        final UiException ue = getUiException(INCIDENCIA_NOT_REGISTERED);
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
    public void testIncidSeeByComu() throws Exception
    {
        // Preconditions.
        assertThat(isRegisteredUser(mActivity), is(true));
        assertThat(TKhandler.getTokensCache(), notNullValue());

        final UiException ue = getUiException(INCIDENCIA_COMMENT_WRONG_INIT);
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

        final UiException ue = getUiException(RESOLUCION_DUPLICATE);

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

    @Test
    public void testUserDataAc() throws Exception
    {
        // Preconditions.
        assertThat(isRegisteredUser(mActivity), is(true));
        assertThat(TKhandler.getTokensCache(), notNullValue());

        final UiException ue = getUiException(USER_DATA_NOT_MODIFIED);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(mActivity, new Intent());
            }
        });

        checkToastInTest(R.string.user_data_not_modified_msg, mActivity);
        onView(withId(R.id.user_data_ac_layout)).check(matches(isDisplayed()));
    }

    //  ============================== HELPERS  ===================================
}