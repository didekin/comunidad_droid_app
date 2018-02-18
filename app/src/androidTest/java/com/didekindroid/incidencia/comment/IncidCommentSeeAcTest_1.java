package com.didekindroid.incidencia.comment;

import android.content.Intent;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

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
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.incidencia.IncidenciaDao.incidenciaDao;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetResolucionNoAdvances;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidCommentRegAcLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidCommentsSeeFrLayout;
import static com.didekindroid.incidencia.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtil.isViewDisplayedAndPerform;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_ESCORIAL_PEPE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;

/**
 * User: pedro@didekin
 * Date: 08/02/16
 * Time: 10:28
 * <p>
 * Tests sin comentarios registrados.
 */
@RunWith(AndroidJUnit4.class)
public class IncidCommentSeeAcTest_1 {

    IncidCommentSeeAc activity;
    IncidImportancia incidPepeEscorial;
    Intent intent;

    @Before
    public void setUp() throws Exception
    {
        incidPepeEscorial = insertGetIncidImportancia(COMU_ESCORIAL_PEPE);
        intent = new Intent(getTargetContext(), IncidCommentSeeAc.class)
                .putExtra(INCIDENCIA_OBJECT.key, incidPepeEscorial.getIncidencia())
                .setFlags(FLAG_ACTIVITY_NEW_TASK);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public void testOnCreate_1() throws Exception
    {
        // Precondition: incidenica is open.
        assertThat(incidPepeEscorial.getIncidencia().getFechaCierre(), nullValue());
        // Run.
        activity = (IncidCommentSeeAc) getInstrumentation().startActivitySync(intent);
        // Check.
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        onView(withId(incidCommentsSeeFrLayout)).check(matches(isDisplayed()));
        // FloatingButton
        onView(withId(R.id.incid_new_comment_fab)).check(matches(isDisplayed()));

        // No hay comentarios registrados.
        onView(withId(android.R.id.list)).check(matches(not(isDisplayed())));
        onView(withId(android.R.id.empty)).check(matches(isDisplayed()));
    }

    @Test
    public void testOnCreate_2() throws Exception
    {
        // Precondition: incidencia is closed.
        Resolucion resolucion = insertGetResolucionNoAdvances(incidPepeEscorial);
        assertThat(incidenciaDao.closeIncidencia(resolucion), is(2));
        // Run.
        activity = (IncidCommentSeeAc) getInstrumentation().startActivitySync(intent);
        // Check.
        onView(withId(incidCommentsSeeFrLayout)).check(matches(isDisplayed()));
        // FloatingButton
        onView(withId(R.id.incid_new_comment_fab)).check(matches(isDisplayed()));
    }

    @Test
    public void test_newCommentButton() throws InterruptedException
    {
        // Run.
        activity = (IncidCommentSeeAc) getInstrumentation().startActivitySync(intent);
        waitAtMost(6, SECONDS).until(isViewDisplayedAndPerform(withId(R.id.incid_new_comment_fab), click()));
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(incidCommentRegAcLayout));
        checkUp(incidCommentsSeeFrLayout);
    }
}