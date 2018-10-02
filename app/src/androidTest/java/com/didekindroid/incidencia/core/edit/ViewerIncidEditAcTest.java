package com.didekindroid.incidencia.core.edit;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.incidencia.core.CtrlerIncidenciaCore;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Maybe;
import io.reactivex.observers.DisposableMaybeObserver;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetResolucionNoAvances;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_PLAZUELA5_JUAN;
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

    private IncidAndResolBundle resolBundle;
    private ViewerIncidEditAc viewer;

    @Rule
    public IntentsTestRule<IncidEditAc> activityRule = new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            // Perfil adm.
            try {
                resolBundle = new IncidAndResolBundle(insertGetIncidImportancia(COMU_PLAZUELA5_JUAN), false);
            } catch (Exception e) {
                fail();
            }
            return new Intent().putExtra(INCID_RESOLUCION_BUNDLE.key, resolBundle);
        }
    };

    @Before
    public void setUp() throws Exception
    {
        IncidEditAc activity = activityRule.getActivity();

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
    public void test_DoViewInViewer()
    {
        assertThat(viewer.resolBundle.getIncidImportancia().equals(resolBundle.getIncidImportancia()), is(true));
    }

    @Test
    public void testCheckResolucion()
    {
        AtomicBoolean isDone = new AtomicBoolean(false);
        CtrlerIncidenciaCore controllerLocal = new CtrlerIncidenciaCore() {
            @Override
            public boolean seeResolucion(DisposableMaybeObserver<Resolucion> observer, long incidenciaId)
            {
                assertThat(isDone.getAndSet(true), is(false));
                return false;
            }
        };
        // Preconditions.
        viewer.setController(controllerLocal);
        // Execute.
        viewer.checkResolucion();
        // Check.
        assertThat(isDone.get(), is(true));
    }

    @Test
    public void test_ResolucionObserver_1()
    {
        // Usuario ADM, con resolución.
        Resolucion resolucion = insertGetResolucionNoAvances(resolBundle.getIncidImportancia());
        just(resolucion).subscribeWith(viewer.new ResolucionObserver());
        onView(withId(R.id.incid_resolucion_edit_fr_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void test_ResolucionObserver_2()
    {
        // Usuario ADM, sin resolución.
        Maybe.<Resolucion>empty().subscribeWith(viewer.new ResolucionObserver());
        onView(withId(R.id.incid_resolucion_reg_frg_layout)).check(matches(isDisplayed()));
    }
}