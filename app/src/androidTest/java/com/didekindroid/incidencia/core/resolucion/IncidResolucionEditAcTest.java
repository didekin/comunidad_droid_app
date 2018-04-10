package com.didekindroid.incidencia.core.resolucion;

import android.content.Intent;
import android.os.Build;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.incidencia.list.IncidSeeByComuAc;
import com.didekindroid.lib_one.usuario.notification.CtrlerNotifyToken;
import com.didekindroid.lib_one.usuario.notification.CtrlerNotifyTokenIf;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static android.app.TaskStackBuilder.create;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.incidencia.IncidenciaDao.incidenciaDao;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetResolucionNoAdvances;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkScreenResolucionEditFr;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkScreenResolucionRegFr;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkScreenResolucionSeeFr;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeByComuAcLayout;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.lib_one.testutil.UiTestUtil.cleanTasks;
import static com.didekindroid.testutil.ActivityTestUtil.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_PLAZUELA5_JUAN;
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 17/12/2017
 * Time: 14:29
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class IncidResolucionEditAcTest {

    private IncidResolucionEditAc activity;
    private IncidImportancia incidImportancia;

    @Before
    public void setUp() throws Exception
    {
        try {
            // Precondition: ADM user.
            incidImportancia = insertGetIncidImportancia(COMU_PLAZUELA5_JUAN);
            assertThat(incidImportancia.getUserComu().hasAdministradorAuthority(), is(true));
        } catch (IOException | UiException e) {
            fail();
        }

        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            Intent intent1 = new Intent(getTargetContext(), IncidSeeByComuAc.class).putExtra(INCID_CLOSED_LIST_FLAG.key, false);
            create(getTargetContext()).addNextIntentWithParentStack(intent1).startActivities();
        }
    }

    @After
    public void tearDown() throws Exception
    {
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            cleanTasks(activity);
        }
        cleanOptions(CLEAN_JUAN);
    }

    /*  ================================ TESTS ===================================*/

    @Test
    public void test_OnCreate_1()
    {
        // Preconditions: only incidImportancia in intent.
        Intent intent = new Intent(getTargetContext(), IncidResolucionEditAc.class).setFlags(FLAG_ACTIVITY_NEW_TASK)
                .putExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia);
        activity = (IncidResolucionEditAc) getInstrumentation().startActivitySync(intent);
        // Check screen of IncidResolucionRegFr.
        checkScreenResolucionRegFr();
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            checkUp(incidSeeByComuAcLayout);
        }
    }

    @Test
    public void test_OnCreate_2() throws Exception
    {
        // Preconditions: resolucion in intent != null; incidencia.getFechaCierre() == null.
        Resolucion resolucion = insertGetResolucionNoAdvances(incidImportancia);
        Intent intent = new Intent(getTargetContext(), IncidResolucionEditAc.class).setFlags(FLAG_ACTIVITY_NEW_TASK)
                .putExtra(INCID_RESOLUCION_OBJECT.key, resolucion);
        activity = (IncidResolucionEditAc) getInstrumentation().startActivitySync(intent);
        // Check screen of IncidResolucionSeeFr.
        checkScreenResolucionSeeFr(resolucion);
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            checkUp(incidSeeByComuAcLayout);
        }
    }

    @Test
    public void test_OnCreate_3() throws Exception
    {
        // Preconditions: resolucion in intent != null; incidencia.getFechaCierre() == null; incidImportancia in intent.
        Resolucion resolucion = insertGetResolucionNoAdvances(incidImportancia);
        Intent intent = new Intent(getTargetContext(), IncidResolucionEditAc.class).setFlags(FLAG_ACTIVITY_NEW_TASK)
                .putExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia)
                .putExtra(INCID_RESOLUCION_OBJECT.key, resolucion);
        activity = (IncidResolucionEditAc) getInstrumentation().startActivitySync(intent);
        // Check screen of IncidResolucionEditFr.
        checkScreenResolucionEditFr(resolucion);
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            checkUp(incidSeeByComuAcLayout);
        }
    }

    @Test
    public void test_OnCreate_4() throws Exception
    {
        // Preconditions: resolucion in intent != null; incidencia.getFechaCierre() != null; incidImportancia in intent (usuario ADM).
        // Cierre incidencias..
        incidenciaDao.closeIncidencia(insertGetResolucionNoAdvances(incidImportancia));
        Resolucion resolucion = incidenciaDao.seeResolucion(incidImportancia.getIncidencia().getIncidenciaId());
        assertThat(resolucion.getIncidencia().getFechaCierre(), notNullValue());
        Intent intent = new Intent(getTargetContext(), IncidResolucionEditAc.class).setFlags(FLAG_ACTIVITY_NEW_TASK)
                .putExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia)
                .putExtra(INCID_RESOLUCION_OBJECT.key, resolucion);
        activity = (IncidResolucionEditAc) getInstrumentation().startActivitySync(intent);
        // Check screen of IncidResolucionSeeFr.
        checkScreenResolucionSeeFr(resolucion);
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            checkUp(incidSeeByComuAcLayout);
        }
    }

    @Test
    public void test_OnCreate_5() throws Exception
    {
        // Preconditions: resolucion in intent != null; incidencia.getFechaCierre() == null; NO incidImportancia in intent (no powers ADM).
        Resolucion resolucion = insertGetResolucionNoAdvances(incidImportancia);
        assertThat(resolucion.getIncidencia().getFechaCierre(), nullValue());
        Intent intent = new Intent(getTargetContext(), IncidResolucionEditAc.class).setFlags(FLAG_ACTIVITY_NEW_TASK)
                .putExtra(INCID_RESOLUCION_OBJECT.key, resolucion);
        activity = (IncidResolucionEditAc) getInstrumentation().startActivitySync(intent);
        // Check screen of IncidResolucionSeeFr.
        checkScreenResolucionSeeFr(resolucion);
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            checkUp(incidSeeByComuAcLayout);
        }
    }

    @Test
    public void test_OnStart() throws Exception
    {
        // Preconditions: only incidImportancia in intent.
        Intent intent = new Intent(getTargetContext(), IncidResolucionEditAc.class).setFlags(FLAG_ACTIVITY_NEW_TASK)
                .putExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia);
        activity = (IncidResolucionEditAc) getInstrumentation().startActivitySync(intent);
        // Run
        CtrlerNotifyTokenIf controller = CtrlerNotifyToken.class.cast(activity.viewerFirebaseToken.getController());
        TimeUnit.SECONDS.sleep(4);
        // Check.
        assertThat(controller.getTkCacher().isGcmTokenSentServer(), is(true));
    }

    @Test
    public void test_OnStop()
    {
        // Preconditions: only incidImportancia in intent.
        Intent intent = new Intent(getTargetContext(), IncidResolucionEditAc.class).setFlags(FLAG_ACTIVITY_NEW_TASK)
                .putExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia);
        activity = (IncidResolucionEditAc) getInstrumentation().startActivitySync(intent);
        // Run/check.
        checkSubscriptionsOnStop(activity, activity.viewerFirebaseToken.getController());
    }
}