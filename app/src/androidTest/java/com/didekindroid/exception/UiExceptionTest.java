package com.didekindroid.exception;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.api.ActivityMock;
import com.didekindroid.incidencia.core.edit.IncidEditAc;
import com.didekinlib.http.ErrorBean;

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
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.makeRegGetIncidImportancia;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.repository.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static com.didekinlib.http.GenericExceptionMsg.TOKEN_NULL;
import static com.didekinlib.model.comunidad.ComunidadExceptionMsg.COMUNIDAD_NOT_FOUND;
import static com.didekinlib.model.incidencia.dominio.IncidenciaExceptionMsg.INCIDENCIA_COMMENT_WRONG_INIT;
import static com.didekinlib.model.incidencia.dominio.IncidenciaExceptionMsg.INCIDENCIA_NOT_FOUND;
import static com.didekinlib.model.incidencia.dominio.IncidenciaExceptionMsg.INCIDENCIA_NOT_REGISTERED;
import static com.didekinlib.model.incidencia.dominio.IncidenciaExceptionMsg.INCIDENCIA_USER_WRONG_INIT;
import static com.didekinlib.model.incidencia.dominio.IncidenciaExceptionMsg.RESOLUCION_DUPLICATE;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_DATA_NOT_MODIFIED;
import static com.didekinlib.model.usuariocomunidad.UsuarioComunidadExceptionMsg.ROLES_NOT_FOUND;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
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

    @Rule
    public IntentsTestRule<ActivityMock> intentRule = new IntentsTestRule<>(ActivityMock.class, true, true);
    ActivityMock activity;

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(2000);
    }

    @Before
    public void setUp() throws Exception
    {
        activity = intentRule.getActivity();
    }

    //  ===========================================================================

    @Test
    public void testSetUp()
    {
        assertThat(activity, notNullValue());
    }

    @Test
    public void test_GENERIC_INTERNAL_ERROR() throws Exception
    {
        final UiException ue = new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(activity);
            }
        });

        waitAtMost(5, SECONDS).until(isToastInView(R.string.exception_generic_app_message, activity));
        onView(withId(R.id.comu_search_ac_linearlayout)).check(matches(isDisplayed()));
    }

    @Test
    public void test_TOKEN_NULL() throws Exception
    {
        final UiException ue = new UiException(new ErrorBean(TOKEN_NULL));

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(activity);
            }
        });
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed((R.id.login_ac_layout)));
        waitAtMost(4, SECONDS).until(isToastInView(R.string.user_without_signedUp, activity));
    }

    @Test
    public void test_ROLES_NOT_FOUND() throws Exception
    {
        final UiException ue = new UiException(new ErrorBean(ROLES_NOT_FOUND));

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(activity);
            }
        });

        waitAtMost(5, SECONDS).until(isToastInView(R.string.user_without_signedUp, activity));
        onView(withId(R.id.login_ac_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void test_USER_DATA_NOT_MODIFIED() throws Exception
    {
        // Preconditions.
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);

        final UiException ue = new UiException(new ErrorBean(USER_DATA_NOT_MODIFIED));
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(activity);
            }
        });

        waitAtMost(5, SECONDS).until(isToastInView(R.string.user_data_not_modified_msg, activity));
        onView(withId(R.id.user_data_ac_layout)).check(matches(isDisplayed()));

        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void test_COMUNIDAD_NOT_FOUND() throws Exception
    {
        final UiException ue = new UiException(new ErrorBean(COMUNIDAD_NOT_FOUND));

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(activity);
            }
        });
        waitAtMost(5, SECONDS).until(isToastInView(R.string.comunidad_not_found_message, activity));
        onView(withId(R.id.comu_search_ac_linearlayout)).check(matches(isDisplayed()));
    }

    @Test
    public void test_INCIDENCIA_NOT_FOUND() throws UiException, IOException
    {
        // Preconditions.
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);

        final UiException ue = new UiException(new ErrorBean(INCIDENCIA_NOT_FOUND));
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(activity);
            }
        });

        waitAtMost(5, SECONDS).until(isToastInView(R.string.incidencia_wrong_init, activity));
        onView(withId(R.id.incid_see_generic_layout)).check(matches(isDisplayed())); // Lista de incidencias abiertas.

        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void test_INCIDENCIA_NOT_REGISTERED() throws UiException, IOException
    {
        // Preconditions.
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);

        final UiException ue = new UiException(new ErrorBean(INCIDENCIA_NOT_REGISTERED));
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(activity);
            }
        });

        waitAtMost(5, SECONDS).until(isToastInView(R.string.incidencia_not_registered, activity));
        onView(withId(R.id.incid_reg_ac_layout)).check(matches(isDisplayed()));

        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void test_INCIDENCIA_USER_WRONG_INIT() throws Exception
    {
        final UiException ue = new UiException(new ErrorBean(INCIDENCIA_USER_WRONG_INIT));

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(activity);
            }
        });

        waitAtMost(5, SECONDS).until(isToastInView(R.string.user_without_powers, activity));
        onView(withId(R.id.login_ac_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void test_INCIDENCIA_COMMENT_WRONG_INIT() throws Exception
    {
        // Preconditions.
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);

        final UiException ue = new UiException(new ErrorBean(INCIDENCIA_COMMENT_WRONG_INIT));
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(activity);
            }
        });

        waitAtMost(5, SECONDS).until(isToastInView(R.string.incidencia_wrong_init, activity));
        onView(withId(R.id.incid_see_open_by_comu_ac)).check(matches(isDisplayed()));

        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void test_RESOLUCION_DUPLICATE() throws Exception
    {
        // Preconditions.
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        final Intent intentIn = new Intent(activity, IncidEditAc.class);
        intentIn.putExtra(INCID_IMPORTANCIA_OBJECT.key, makeRegGetIncidImportancia(userComuDaoRemote.seeUserComusByUser().get(0), (short) 3));

        final UiException ue = new UiException(new ErrorBean(RESOLUCION_DUPLICATE));

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(activity, intentIn);
            }
        });
        waitAtMost(5, SECONDS).until(isToastInView(R.string.resolucion_duplicada, activity));
        onView(withId(R.id.incid_edit_fragment_container_ac)).check(matches(isDisplayed()));

        cleanOptions(CLEAN_JUAN);
    }

    //  ============================== HELPERS  ===================================
}