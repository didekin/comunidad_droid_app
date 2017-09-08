package com.didekindroid.incidencia.core.reg;

import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.core.AmbitoIncidValueObj;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.testutil.ComuDataTestUtil.COMU_LA_FUENTE;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.doAmbitoAndDescripcion;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.doComunidadSpinner;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.doImportanciaSpinner;
import static com.didekindroid.testutil.ActivityTestUtils.checkBack;
import static com.didekindroid.testutil.ActivityTestUtils.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.testutil.ActivityTestUtils.isViewDisplayed;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_LA_FUENTE_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.regSeveralUserComuSameUser;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 17/11/15
 * Time: 10:07
 */
@RunWith(AndroidJUnit4.class)
public class IncidRegAcTest {

    final static AtomicReference<String> flagMethodExec_1 = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public IntentsTestRule<IncidRegAc> intentRule = new IntentsTestRule<IncidRegAc>(IncidRegAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            try {
                regSeveralUserComuSameUser(COMU_ESCORIAL_PEPE, COMU_REAL_PEPE, COMU_LA_FUENTE_PEPE);
            } catch (IOException | UiException e) {
                fail();
            }
        }
    };

    int activityLayoutId = R.id.incid_reg_ac_layout;
    int fragmentLayoutId = R.id.incid_reg_frg;
    AmbitoIncidValueObj ambitoObj = new AmbitoIncidValueObj((short) 10, "Calefacción comunitaria");
    IncidRegAc activity;

    @Before
    public void setUp() throws Exception
    {
        activity = intentRule.getActivity();
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_PEPE);
    }

    /*  ================================ INTEGRATION ===================================*/

    @Test
    public void testRegisterIncidencia_1() throws InterruptedException
    {
        /* Caso NOT OK: descripción de incidencia no válida.*/
        doImportanciaSpinner(activity, 4);
        SECONDS.sleep(1);
        doAmbitoAndDescripcion(ambitoObj, "descripcion = not valid");
        onView(withId(R.id.incid_reg_ac_button)).perform(scrollTo(), click());

        waitAtMost(4, SECONDS).until(isToastInView(R.string.error_validation_msg, activity, R.string.incid_reg_descripcion));
    }

    @Test
    public void testRegisterIncidencia_2() throws UiException, InterruptedException
    {
        // Caso OK: incidencia con datos de importancia.
        doImportanciaSpinner(activity, 4);
        SECONDS.sleep(1);
        doAmbitoAndDescripcion(ambitoObj, "descripcion es valida");

        onView(withId(R.id.incid_reg_ac_button)).perform(scrollTo(), click());
        waitAtMost(3, SECONDS).until(isViewDisplayed(withId(R.id.incid_see_open_by_comu_ac)));
        checkUp(activityLayoutId, fragmentLayoutId);
    }

    @Test
    public void testRegisterIncidencia_3() throws UiException
    {
        // Caso OK: no cubro importancia.
        doAmbitoAndDescripcion(ambitoObj, "descripcion is valid");
        onView(withId(R.id.incid_reg_ac_button)).perform(scrollTo(), click());

        waitAtMost(3, SECONDS).until(isViewDisplayed(withId(R.id.incid_see_open_by_comu_ac)));
        checkBack(onView(withId(R.id.incid_see_open_by_comu_ac)), activityLayoutId, fragmentLayoutId);
    }

    @Test
    public void testRegisterIncidencia_4() throws UiException, InterruptedException
    {
        // Probamos cambio de comunidad en spinner: Calle La Fuente.
        doComunidadSpinner(COMU_LA_FUENTE);
        SECONDS.sleep(1);
        // Registro de incidencia con importancia.
        doImportanciaSpinner(activity, 4);
        SECONDS.sleep(1);
        doAmbitoAndDescripcion(ambitoObj, "Incidencia La Fuente");
        onView(withId(R.id.incid_reg_ac_button)).perform(scrollTo(), click());

        waitAtMost(4, SECONDS).until(isViewDisplayed(withId(R.id.incid_see_open_by_comu_ac)));
        checkUp(activityLayoutId, fragmentLayoutId);
    }

    //    =======================   UNIT TESTS ========================

    @Test
    public void testOnCreate()
    {
        assertThat(activity.getParentViewer(), notNullValue());
        IncidRegFr fragment = (IncidRegFr) activity.getSupportFragmentManager().findFragmentById(R.id.incid_reg_frg);
        assertThat(fragment.viewerInjector, instanceOf(IncidRegAc.class));
        assertThat(fragment.viewer.getParentViewer(), CoreMatchers.<ViewerIf>is(activity.viewer));
    }

    @Test
    public void testOnStop()
    {
        checkSubscriptionsOnStop(activity, activity.viewer.getController());
    }

    @Test
    public void testOnSaveInstanceState()
    {
        activity.viewer = new ViewerIncidRegAc(activity) {
            @Override
            public void saveState(Bundle savedState)
            {
                assertThat(flagMethodExec_1.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
            }

            @Override
            public int clearSubscriptions()  // It is called from onStop() and gives problems.
            {
                return 0;
            }
        };
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                getInstrumentation().callActivityOnSaveInstanceState(activity, new Bundle(0));
            }
        });
        waitAtMost(1, SECONDS).untilAtomic(flagMethodExec_1, is(AFTER_METHOD_EXEC_A));
    }
}