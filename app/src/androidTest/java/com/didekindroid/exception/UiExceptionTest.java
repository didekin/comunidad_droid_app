package com.didekindroid.exception;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.api.ActivityMock;
import com.didekindroid.usuario.testutil.UsuarioDataTestUtils;
import com.didekinlib.http.ErrorBean;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

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
import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.doIncidencia;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static com.didekinlib.model.comunidad.ComunidadExceptionMsg.COMUNIDAD_NOT_FOUND;
import static com.didekinlib.model.incidencia.dominio.IncidenciaExceptionMsg.INCIDENCIA_COMMENT_WRONG_INIT;
import static com.didekinlib.model.incidencia.dominio.IncidenciaExceptionMsg.INCIDENCIA_NOT_REGISTERED;
import static com.didekinlib.model.incidencia.dominio.IncidenciaExceptionMsg.INCIDENCIA_USER_WRONG_INIT;
import static com.didekinlib.model.incidencia.dominio.IncidenciaExceptionMsg.RESOLUCION_DUPLICATE;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_DATA_NOT_MODIFIED;
import static com.didekinlib.model.usuariocomunidad.UsuarioComunidadExceptionMsg.ROLES_NOT_FOUND;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
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
public class UiExceptionTest {

    IncidImportancia mIncidJuanPlazuelas;
    @Rule
    public IntentsTestRule<ActivityMock> intentRule = new IntentsTestRule<ActivityMock>(ActivityMock.class) {

        @Override
        protected Intent getActivityIntent()
        {
            try {
                signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
                UsuarioComunidad juanPlazuelas = userComuDaoRemote.seeUserComusByUser().get(0);
                mIncidJuanPlazuelas = new IncidImportancia.IncidImportanciaBuilder(
                        doIncidencia(juanPlazuelas.getUsuario().getUserName(), "Incidencia Plazueles", juanPlazuelas.getComunidad().getC_Id(), (short) 43))
                        .usuarioComunidad(juanPlazuelas)
                        .importancia((short) 3).build();
                incidenciaDao.regIncidImportancia(mIncidJuanPlazuelas);
                IncidenciaUser incidenciaUserDb = incidenciaDao.seeIncidsOpenByComu(juanPlazuelas.getComunidad().getC_Id()).get(0);
                mIncidJuanPlazuelas = incidenciaDao.seeIncidImportancia(incidenciaUserDb.getIncidencia().getIncidenciaId()).getIncidImportancia();
            } catch (IOException | UiException e) {
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
    ActivityMock mActivity;

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(2000);
    }

    @Before
    public void setUp() throws Exception
    {
        mActivity = intentRule.getActivity();
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN);
    }

    //  ===========================================================================

    @Test
    public void testSetUp()
    {
        assertThat(mActivity, notNullValue());
    }

    @Test
    public void testGeneric() throws Exception
    {
        final UiException ue = new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(mActivity, new Intent());
            }
        });

        waitAtMost(5, SECONDS).until(isToastInView(R.string.exception_generic_app_message, mActivity));
        onView(withId(R.id.comu_search_ac_linearlayout)).check(matches(isDisplayed()));
    }

    @Test
    public void testLogin() throws Exception
    {
        final UiException ue = new UiException(new ErrorBean(ROLES_NOT_FOUND));

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(mActivity, new Intent());
            }
        });

        waitAtMost(5, SECONDS).until(isToastInView(R.string.user_without_signedUp, mActivity));
        onView(withId(R.id.login_ac_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void testUserDataAc() throws Exception
    {
        // Preconditions.
        assertThat(TKhandler.isRegisteredUser(), is(true));
        assertThat(TKhandler.getTokenCache().get(), notNullValue());

        final UiException ue = new UiException(new ErrorBean(USER_DATA_NOT_MODIFIED));
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(mActivity, new Intent());
            }
        });

        waitAtMost(5, SECONDS).until(isToastInView(R.string.user_data_not_modified_msg, mActivity));
        onView(withId(R.id.user_data_ac_layout)).check(matches(isDisplayed()));
    }


    @Test
    public void testSearchComu() throws Exception
    {
        final UiException ue = new UiException(new ErrorBean(COMUNIDAD_NOT_FOUND));

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(mActivity, new Intent());
            }
        });
        waitAtMost(5, SECONDS).until(isToastInView(R.string.comunidad_not_found_message, mActivity));
        onView(withId(R.id.comu_search_ac_linearlayout)).check(matches(isDisplayed()));
    }

    @Test
    public void testIncidReg() throws UiException
    {
        // Preconditions.
        assertThat(TKhandler.isRegisteredUser(), is(true));
        assertThat(TKhandler.getTokenCache().get(), notNullValue());

        final UiException ue = new UiException(new ErrorBean(INCIDENCIA_NOT_REGISTERED));
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(mActivity, new Intent());
            }
        });

        waitAtMost(5, SECONDS).until(isToastInView(R.string.incidencia_not_registered, mActivity));
        onView(withId(R.id.incid_reg_ac_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginIncid() throws Exception
    {
        final UiException ue = new UiException(new ErrorBean(INCIDENCIA_USER_WRONG_INIT));

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(mActivity, new Intent());
            }
        });

        waitAtMost(5, SECONDS).until(isToastInView(R.string.user_without_powers, mActivity));
        onView(withId(R.id.login_ac_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void testIncidSeeByComu() throws Exception
    {
        // Preconditions.
        assertThat(TKhandler.isRegisteredUser(), is(true));
        assertThat(TKhandler.getTokenCache().get(), notNullValue());

        final UiException ue = new UiException(new ErrorBean(INCIDENCIA_COMMENT_WRONG_INIT));
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(mActivity, new Intent());
            }
        });

        waitAtMost(5, SECONDS).until(isToastInView(R.string.incidencia_wrong_init, mActivity));
        onView(withId(R.id.incid_see_open_by_comu_ac)).check(matches(isDisplayed()));
    }

    @Test
    public void testResolucionDup() throws Exception
    {
        // Preconditions.
        final Intent intentIn = new Intent();
        intentIn.putExtra(INCID_IMPORTANCIA_OBJECT.key, mActivity.getIntent().getSerializableExtra(INCID_IMPORTANCIA_OBJECT.key));

        final UiException ue = new UiException(new ErrorBean(RESOLUCION_DUPLICATE));

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(mActivity, intentIn);
            }
        });
        waitAtMost(5, SECONDS).until(isToastInView(R.string.resolucion_duplicada, mActivity));
        onView(withId(R.id.incid_edit_fragment_container_ac)).check(matches(isDisplayed()));
    }

    //  ============================== HELPERS  ===================================
}