package com.didekindroid.incidencia.core.edit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.lib_one.incidencia.IncidenciaDataDbHelper;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkDataEditMaxPowerFr;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkScreenEditMaxPowerFrErase;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkScreenEditMaxPowerFrNotErase;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidResolucionEditFrLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidResolucionRegFrLayout;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetResolucionNoAdvances;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_COMMENTS_SEE_AC;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_COMMENT_REG_AC;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_RESOLUCION_REG_EDIT_AC;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.testutil.ActivityTestUtil.checkAppBarMnNotExist;
import static com.didekindroid.testutil.ActivityTestUtil.checkBack;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_TRAV_PLAZUELA_PEPE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 19/01/16
 * Time: 16:57
 */
@RunWith(AndroidJUnit4.class)
public class IncidEditAc_Mn1_Test {

    private IncidEditAc activity;
    private IncidenciaDataDbHelper dbHelper;

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
        dbHelper.close();
        cleanOptions(CLEAN_PEPE);
    }

//    ============================  TESTS  ===================================

    @SuppressWarnings("RedundantThrows")
    @Test
    public void testIncidCommentReg_Mn() throws Exception
    {
        activity = doIntentStartActivity(initDbData(COMU_ESCORIAL_PEPE, true));
        dbHelper = new IncidenciaDataDbHelper(activity);
        checkDataEditMaxPowerFr(dbHelper, activity, activity.resolBundle.getIncidImportancia());
        checkScreenEditMaxPowerFrNotErase(activity.resolBundle);

        INCID_COMMENT_REG_AC.checkItem(activity);
        checkUp();
        checkDataEditMaxPowerFr(dbHelper, activity, activity.resolBundle.getIncidImportancia());
    }

    @SuppressWarnings("RedundantThrows")
    @Test
    public void testIncidCommentsSee_Mn() throws Exception
    {
        activity = doIntentStartActivity(initDbData(COMU_ESCORIAL_PEPE, true));
        dbHelper = new IncidenciaDataDbHelper(activity);
        checkDataEditMaxPowerFr(dbHelper, activity, activity.resolBundle.getIncidImportancia());
        checkScreenEditMaxPowerFrNotErase(activity.resolBundle);

        INCID_COMMENTS_SEE_AC.checkItem(activity);
        checkUp();
        checkDataEditMaxPowerFr(dbHelper, activity, activity.resolBundle.getIncidImportancia());
    }

    @Test
    public void testIncidResolucionReg_Mn_1() throws Exception
    {
        activity = doIntentStartActivity(initDbData(COMU_ESCORIAL_PEPE, true));
        dbHelper = new IncidenciaDataDbHelper(activity);
        checkDataEditMaxPowerFr(dbHelper, activity, activity.resolBundle.getIncidImportancia());
        checkScreenEditMaxPowerFrNotErase(activity.resolBundle);

        // Preconditions: usuario ADM, with resolucion.
        assertThat(activity.resolBundle.hasResolucion(), is(true));
        assertThat(activity.resolBundle.getIncidImportancia().getUserComu().hasAdministradorAuthority(), is(true));

        onView(withText(R.string.incid_resolucion_ac_mn)).check(matches(isDisplayed())).perform(click());
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(incidResolucionEditFrLayout));

        checkBack(onView(withId(incidResolucionEditFrLayout)));
        checkScreenEditMaxPowerFrNotErase(activity.resolBundle);
    }

    @SuppressWarnings("RedundantThrows")
    @Test
    public void testIncidResolucionReg_Mn_2() throws Exception
    {
        activity = doIntentStartActivity(initDbData(COMU_ESCORIAL_PEPE, false));
        dbHelper = new IncidenciaDataDbHelper(activity);
        checkDataEditMaxPowerFr(dbHelper, activity, activity.resolBundle.getIncidImportancia());
        checkScreenEditMaxPowerFrErase(activity.resolBundle);  // No hay resolución. La incidencia se puede borrar.

        // Preconditions: usuario ADM, without resolucion.
        assertThat(activity.resolBundle.hasResolucion(), is(false));
        assertThat(activity.resolBundle.getIncidImportancia().getUserComu().hasAdministradorAuthority(), is(true));

        INCID_RESOLUCION_REG_EDIT_AC.checkItem(activity);
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(incidResolucionRegFrLayout));

        checkBack(onView(withId(incidResolucionRegFrLayout)));
        checkScreenEditMaxPowerFrErase(activity.resolBundle);
    }

    @Test
    public void testIncidResolucionReg_Mn_3() throws Exception
    {
        activity = doIntentStartActivity(initDbData(COMU_TRAV_PLAZUELA_PEPE, false));
        dbHelper = new IncidenciaDataDbHelper(activity);
        checkDataEditMaxPowerFr(dbHelper, activity, activity.resolBundle.getIncidImportancia());
        checkScreenEditMaxPowerFrErase(activity.resolBundle);  // No hay resolución. La incidencia se puede borrar.

        // Preconditions: usuario NO ADM, without resolucion.
        assertThat(activity.resolBundle.hasResolucion(), is(false));
        assertThat(activity.resolBundle.getIncidImportancia().getUserComu().hasAdministradorAuthority(), is(false));
        // Check.
        checkAppBarMnNotExist(activity, R.string.incid_resolucion_ac_mn);
    }

    //    ============================  HELPER  ===================================

    @NonNull
    private IncidAndResolBundle initDbData(UsuarioComunidad usuarioComunidad, boolean hasResolucion) throws Exception
    {
        /* Perfil adm, inicidador de la incidencia.*/
        final IncidImportancia incidImportancia = insertGetIncidImportancia(usuarioComunidad);
        if (hasResolucion) {
            insertGetResolucionNoAdvances(incidImportancia);
            return new IncidAndResolBundle(incidImportancia, true);
        } else {
            return new IncidAndResolBundle(incidImportancia, false);
        }
    }

    private IncidEditAc doIntentStartActivity(IncidAndResolBundle newResolBundle)
    {
        Intent intent = new Intent(getTargetContext(), IncidEditAc.class);
        intent.putExtra(INCID_RESOLUCION_BUNDLE.key, newResolBundle);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        // Run
        return (IncidEditAc) getInstrumentation().startActivitySync(intent);
    }
}