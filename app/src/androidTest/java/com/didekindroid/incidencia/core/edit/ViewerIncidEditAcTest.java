package com.didekindroid.incidencia.core.edit;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.incidencia.core.CtrlerIncidenciaCore;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Maybe;
import io.reactivex.observers.DisposableMaybeObserver;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetResolucionNoAdvances;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.lib_one.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.lib_one.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static io.reactivex.Maybe.just;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 07/04/17
 * Time: 10:10
 */
@RunWith(AndroidJUnit4.class)
public class ViewerIncidEditAcTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);
    IncidAndResolBundle resolBundle;

    @Rule
    public IntentsTestRule<IncidEditAc> activityRule = new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            try {
                // Perfil adm.
                resolBundle = new IncidAndResolBundle(insertGetIncidImportancia(COMU_PLAZUELA5_JUAN), false);
            } catch (IOException | UiException e) {
                fail();
            }

            Intent intent = new Intent();
            intent.putExtra(INCID_RESOLUCION_BUNDLE.key, resolBundle);
            return intent;
        }
    };

    ViewerIncidEditAc viewer;
    IncidEditAc activity;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        AtomicReference<CtrlerIncidenciaCore> atomicController = new AtomicReference<>(null);
        atomicController.compareAndSet(null, activity.viewer.getController());
        waitAtMost(4, SECONDS).untilAtomic(atomicController, notNullValue());
        viewer = activity.viewer;
    }

    @After
    public void tearDown() throws Exception
    {
        viewer.clearSubscriptions();
        cleanOptions(CLEAN_JUAN);
    }

    //    ============================  TESTS  ===================================

    @Test
    public void test_DoViewInViewer() throws Exception
    {
        assertThat(viewer.resolBundle.getIncidImportancia().equals(resolBundle.getIncidImportancia()), is(true));
    }

    @Test
    public void testCheckResolucion() throws Exception
    {
        CtrlerIncidenciaCore controllerLocal = new CtrlerIncidenciaCore() {
            @Override
            public boolean seeResolucion(DisposableMaybeObserver<Resolucion> observer, long incidenciaId)
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                return false;
            }
        };
        // Preconditions.
        viewer.setController(controllerLocal);
        // Execute.
        viewer.checkResolucion();
        // Check.
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }

    @Test
    public void test_ResolucionObserver_1() throws UiException
    {
        // Usuario ADM, con resolución.
        Resolucion resolucion = insertGetResolucionNoAdvances(resolBundle.getIncidImportancia());
        just(resolucion).subscribeWith(viewer.new ResolucionObserver());
        onView(withId(R.id.incid_resolucion_edit_fr_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void test_ResolucionObserver_2() throws UiException
    {
        // Usuario ADM, sin resolución.
        Maybe.<Resolucion>empty().subscribeWith(viewer.new ResolucionObserver());
        onView(withId(R.id.incid_resolucion_reg_frg_layout)).check(matches(isDisplayed()));
    }
}