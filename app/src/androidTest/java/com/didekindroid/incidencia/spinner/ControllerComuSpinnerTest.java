package com.didekindroid.incidencia.spinner;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.didekindroid.api.ManagerIf;
import com.didekindroid.api.ManagerIf.ControllerIf;
import com.didekindroid.api.ManagerMock;
import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.ViewerMock;
import com.didekindroid.api.ViewerWithSelectIf;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.spinner.ControllerComuSpinner.ReactorComuSpinner;
import com.didekindroid.incidencia.spinner.ManagerComuSpinnerIf.ReactorComuSpinnerIf;
import com.didekindroid.incidencia.spinner.ManagerComuSpinnerIf.ViewerComuSpinnerIf;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.comunidad.Municipio;
import com.didekinlib.model.comunidad.Provincia;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.incidencia.spinner.ControllerComuSpinner.newControllerComuSpinner;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.makeListTwoUserComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.regTwoUserComuSameUser;
import static com.didekinlib.http.GenericExceptionMsg.TOKEN_NULL;
import static io.reactivex.plugins.RxJavaPlugins.reset;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.fieldIn;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 16/02/17
 * Time: 14:13
 */
@RunWith(AndroidJUnit4.class)
public class ControllerComuSpinnerTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);
    static final String AFTER_loadComunidades_EXEC = "after loadComunidades";
    static final String AFTER_getComunidadSelectedId_EXEC = "after getComunidadSelectedId";
    static final String AFTER_processBackLoadComusInSpinner_EXEC = "after processBackLoadComusInSpinner";
    static final String AFTER_processReactorError_EXEC = "after processReactorError";

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    UsuarioComunidad pepeUserComu;
    ReactorComuSpinnerIf reactor;
    ViewerComuSpinnerIf controllerViewer;
    Activity activity;
    ManagerIf<?> manager;
    ControllerComuSpinner controller;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        manager = new ManagerMock<>(activity);
        activity.runOnUiThread(new Runnable() {
            @SuppressWarnings("unchecked")
            @Override
            public void run()
            {
                controllerViewer = new ViewerComuSpinnerForTest((ManagerIf<Object>) manager);
                // Default initialization of field in ControllerIdentityAbsTest.
                controller = (ControllerComuSpinner) newControllerComuSpinner(controllerViewer);
            }
        });
        waitAtMost(1L, SECONDS).until(fieldIn(this).ofType(ControllerIf.class), notNullValue());
    }

    // ............................ CONTROLLER ...............................

    @Test
    public void testLoadDataInSpinner()
    {
        reactor = new ReactorComuSpinnerIf() {
            @Override
            public boolean loadComunidades()
            {
                assertThat(flagMethodExec.getAndSet(AFTER_loadComunidades_EXEC), is(BEFORE_METHOD_EXEC));
                return flagMethodExec.get().equals(AFTER_METHOD_EXEC);
            }
        };
        controller = (ControllerComuSpinner) newControllerComuSpinner(controllerViewer, reactor);
        controller.loadDataInSpinner();
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_loadComunidades_EXEC));
    }

    @Test
    public void getSelectedFromComunidadId() throws Exception
    {
        List<Comunidad> comunidades = makeComunidadesList();
        controller.spinnerAdapter.addAll(comunidades);
        controller.comuSpinner.setAdapter(controller.spinnerAdapter);
        assertThat(controller.getSelectedFromComunidadId(321L), is(0));
        assertThat(controller.getSelectedFromComunidadId(123L), is(1));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testProcessBackLoadComusInSpinner() throws IOException, UiException
    {
        List<Comunidad> comunidades = makeComunidadesList();
        controller.processBackLoadComusInSpinner(comunidades);

        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_getComunidadSelectedId_EXEC));

        ArrayAdapter<Comunidad> postAdapter = (ArrayAdapter<Comunidad>) controller.comuSpinner.getAdapter();
        assertThat(postAdapter, is(controller.spinnerAdapter));
        assertThat(postAdapter.getCount(), is(2));
        // Assertions based on viewer.getComunidadSelectedId(): 123L.
        assertThat(controller.comuSpinner.getSelectedItemPosition(), is(1));
    }

    @NonNull
    private List<Comunidad> makeComunidadesList()
    {
        List<Comunidad> comunidades = new ArrayList<>(2);
        comunidades.add(new Comunidad.ComunidadBuilder().c_id(321L).nombreVia("AAAAAA")
                .municipio(new Municipio((short) 1, new Provincia((short) 11)))
                .build());
        comunidades.add(new Comunidad.ComunidadBuilder().c_id(123L).nombreVia("ZZZZZ")
                .municipio(new Municipio((short) 2, new Provincia((short) 22)))
                .build());
        return comunidades;
    }

    // ............................ REACTOR ..................................

    @SuppressWarnings("unchecked")
    @Test
    public void testLoadComunidades() throws IOException, UiException
    {
        // Mock injection.
        controller = new ControllerComuSpinner(controllerViewer) {
            @Override
            public void processBackLoadComusInSpinner(Collection<Comunidad> comunidades)
            {
                assertThat(comunidades.size(), is(2));
                assertThat(flagMethodExec.getAndSet(AFTER_processBackLoadComusInSpinner_EXEC), is(BEFORE_METHOD_EXEC));
            }
        };

        reactor = new ReactorComuSpinner(controller);

        regTwoUserComuSameUser(makeListTwoUserComu());

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(reactor.loadComunidades(), is(true));
        } finally {
            reset();
        }
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_processBackLoadComusInSpinner_EXEC));
        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void testLoadComunidades_ERROR() throws IOException, UiException
    {
        // NO user registered and no token in cache.

        controller = new ControllerComuSpinner(controllerViewer) {
            @Override
            public void processReactorError(Throwable e)
            {
                UiException ue = (UiException) e;
                assertThat(ue.getErrorBean().getMessage(), is(TOKEN_NULL.getHttpMessage()));
                assertThat(flagMethodExec.getAndSet(AFTER_processReactorError_EXEC), is(BEFORE_METHOD_EXEC));
            }
        };

        reactor = new ReactorComuSpinner(controller);

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(reactor.loadComunidades(), is(true));
        } finally {
            reset();
        }
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_processReactorError_EXEC));
    }

    // ............................ HELPERS ..................................

    class ViewerComuSpinnerForTest extends ViewerMock<Spinner, Object> implements
            ViewerComuSpinnerIf<Object> {

        protected ViewerComuSpinnerForTest(ManagerIf<Object> manager)
        {
            super(manager);
            viewInViewer = new Spinner(manager.getActivity());
        }

        @Override
        public ViewerComuSpinnerIf<Object> setDataInView(Bundle savedState)
        {
            return this;
        }

        // Used in testProcessBackLoadComusInSpinner().
        public long getComunidadSelectedId()
        {
            assertThat(flagMethodExec.getAndSet(AFTER_getComunidadSelectedId_EXEC), is(BEFORE_METHOD_EXEC));
            return 123L;
        }

        @Override
        public ViewerWithSelectIf<Spinner, Object> initSelectedIndex(Bundle savedState)
        {
            return null;
        }

        @Override
        public void saveSelectedIndex(Bundle savedState)
        {
        }
    }
}