package com.didekindroid.incidencia.core.resolucion;

import android.content.Intent;
import android.os.Build;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.incidencia.list.IncidSeeByComuAc;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.app.TaskStackBuilder.create;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkScreenResolucionEditFr;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkScreenResolucionRegFr;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkScreenResolucionSeeFr;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeByComuAcLayout;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetResolucionNoAdvances;
import static com.didekindroid.lib_one.testutil.UiTestUtil.cleanTasks;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_PLAZUELA5_JUAN;
import static org.hamcrest.CoreMatchers.is;
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
        // Precondition: ADM user.
        incidImportancia = insertGetIncidImportancia(COMU_PLAZUELA5_JUAN);
        assertThat(incidImportancia.getUserComu().hasAdministradorAuthority(), is(true));

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
        // Preconditions: resolucion == null (only incidImportancia in intent).
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
    public void test_OnCreate_2()
    {
        // Preconditions: resolucion in intent != null;  NO hasAdmRole (because no INCID_IMPORTANCIA_OBJECT in intent).
        Resolucion resolucion = insertGetResolucionNoAdvances(incidImportancia);
        Intent intent = new Intent(getTargetContext(), IncidResolucionEditAc.class).setFlags(FLAG_ACTIVITY_NEW_TASK)
                .putExtra(INCID_RESOLUCION_OBJECT.key, resolucion);
        activity = (IncidResolucionEditAc) getInstrumentation().startActivitySync(intent);
        // Check screen of IncidResolucionSeeFr.
        checkScreenResolucionSeeFr();
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            checkUp(incidSeeByComuAcLayout);
        }
    }

    @Test
    public void test_OnCreate_3()
    {
        // Preconditions: resolucion in intent != null; hasAdmRole.
        Resolucion resolucion = insertGetResolucionNoAdvances(incidImportancia);
        Intent intent = new Intent(getTargetContext(), IncidResolucionEditAc.class).setFlags(FLAG_ACTIVITY_NEW_TASK)
                .putExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia)
                .putExtra(INCID_RESOLUCION_OBJECT.key, resolucion);
        activity = (IncidResolucionEditAc) getInstrumentation().startActivitySync(intent);
        // Check screen of IncidResolucionEditFr.
        checkScreenResolucionEditFr();
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            checkUp(incidSeeByComuAcLayout);
        }
    }
}