package com.didekindroid.incidencia.core.edit;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableMaybeObserver;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetResolucionNoAdvances;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static io.reactivex.Maybe.just;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
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
    IncidImportancia incidImportancia;

    @Rule
    public IntentsTestRule<IncidEditAc> activityRule = new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            try {
                // Perfil adm.
                incidImportancia = insertGetIncidImportancia(COMU_PLAZUELA5_JUAN);
            } catch (IOException | UiException e) {
                fail();
            }

            Intent intent = new Intent();
            intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia);
            intent.putExtra(INCID_RESOLUCION_FLAG.key, false);
            return intent;
        }
    };

    ViewerIncidEditAc viewer;
    IncidEditAc activity;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        AtomicReference<CtrlerIncidEditAc> atomicController = new AtomicReference<>(null);
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
    public void testCheckResolucion() throws Exception
    {
        CtrlerIncidEditAc controllerLocal = new CtrlerIncidEditAc() {
            @Override
            boolean seeResolucion(DisposableMaybeObserver<Resolucion> observer, long incidenciaId, int resourceIdItemMn)
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                return false;
            }
        };
        // Preconditions.
        viewer.setController(controllerLocal);
        viewer.doViewInViewer(null, incidImportancia);
        // Execute.
        viewer.checkResolucion(123);
        // Check.
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }

    @Test
    public void test_OnSuccessSeeResolucion() throws Exception
    {
        // Preconditions.
        assertThat(viewer.incidImportancia, notNullValue());

        Resolucion resolucion = insertGetResolucionNoAdvances(viewer.incidImportancia);
        viewer.onSuccessSeeResolucion(resolucion, R.id.incid_resolucion_reg_ac_mn);
        onView(withId(R.id.incid_resolucion_fragment_container_ac)).check(matches(isDisplayed()));
        intended(allOf(
                hasExtra(INCID_IMPORTANCIA_OBJECT.key, viewer.incidImportancia),
                hasExtra(INCID_RESOLUCION_OBJECT.key, resolucion)
        ));
    }

    @Test
    public void test_OnCompleteSeeResolucion() throws Exception
    {
        // Preconditions.
        assertThat(viewer.incidImportancia, notNullValue());

        viewer.onCompleteSeeResolucion(R.id.incid_resolucion_reg_ac_mn);
        onView(withId(R.id.incid_resolucion_fragment_container_ac)).check(matches(isDisplayed()));
        intended(allOf(
                hasExtra(INCID_IMPORTANCIA_OBJECT.key, viewer.incidImportancia),
                not(hasExtraWithKey(INCID_RESOLUCION_OBJECT.key))
        ));
    }

    @Test
    public void test_ResolucionObserver() throws UiException
    {
        Resolucion resolucion = insertGetResolucionNoAdvances(incidImportancia);
        just(resolucion).subscribeWith(viewer.new ResolucionObserver(R.id.incid_resolucion_reg_ac_mn));
        onView(withId(R.id.incid_resolucion_fragment_container_ac)).check(matches(isDisplayed()));
    }
}