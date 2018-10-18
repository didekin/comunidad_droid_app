package com.didekindroid.incidencia.core.edit;

import android.content.Intent;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.lib_one.incidencia.IncidenciaDataDbHelper;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
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
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkScreenEditMaxPowerFrNotErase;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidResolucionEditFrLayout;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetResolucionNoAvances;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_COMMENTS_SEE_AC;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_COMMENT_REG_AC;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.testutil.ActivityTestUtil.checkBack;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_ESCORIAL_PEPE;
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
    private static IncidAndResolBundle resolBundle;

    @BeforeClass
    public static void setUp() throws Exception
    {
        // Perfil adm, inicidador de la incidencia.
        final IncidImportancia incidImportancia = insertGetIncidImportancia(COMU_ESCORIAL_PEPE);
        insertGetResolucionNoAvances(incidImportancia);
        resolBundle = new IncidAndResolBundle(incidImportancia, true);
    }

    @After
    public void tearDown() throws Exception
    {
        dbHelper.close();
    }

    @AfterClass
    public static void cleanStatic()
    {
        cleanOptions(CLEAN_PEPE);
    }

//    ============================  TESTS  ===================================

    @Test
    public void testIncidCommentReg_Mn()
    {
        activity = doIntentStartActivity();
        dbHelper = new IncidenciaDataDbHelper(activity);
        checkDataEditMaxPowerFr(dbHelper, activity, activity.resolBundle.getIncidImportancia());
        checkScreenEditMaxPowerFrNotErase(activity.resolBundle);

        INCID_COMMENT_REG_AC.checkItem(activity);
        checkUp();
        checkDataEditMaxPowerFr(dbHelper, activity, activity.resolBundle.getIncidImportancia());
    }

    @Test
    public void testIncidCommentsSee_Mn()
    {
        activity = doIntentStartActivity();
        dbHelper = new IncidenciaDataDbHelper(activity);
        checkDataEditMaxPowerFr(dbHelper, activity, activity.resolBundle.getIncidImportancia());
        checkScreenEditMaxPowerFrNotErase(activity.resolBundle);

        INCID_COMMENTS_SEE_AC.checkItem(activity);
        checkUp();
        checkDataEditMaxPowerFr(dbHelper, activity, activity.resolBundle.getIncidImportancia());
    }

    @Test
    public void testIncidResolucionReg_Mn_1()
    {
        activity = doIntentStartActivity();
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

    //    ============================  HELPER  ===================================

    private IncidEditAc doIntentStartActivity()
    {
        Intent intent = new Intent(getTargetContext(), IncidEditAc.class);
        intent.putExtra(INCID_RESOLUCION_BUNDLE.key, resolBundle);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        // Run
        return (IncidEditAc) getInstrumentation().startActivitySync(intent);
    }
}