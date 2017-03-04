package com.didekindroid.incidencia.spinner;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.didekindroid.ControllerAbsTest;
import com.didekindroid.ManagerIf.ControllerIf;
import com.didekindroid.ViewerDumbImp;
import com.didekindroid.ViewerWithSelectIf;
import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf;
import com.didekindroid.incidencia.spinner.ControllerComuSpinner.ReactorComuSpinner;
import com.didekindroid.incidencia.spinner.ManagerComuSpinnerIf.ReactorComuSpinnerIf;
import com.didekindroid.incidencia.spinner.ManagerComuSpinnerIf.ViewerComuSpinnerIf;
import com.didekindroid.testutil.MockActivity;
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
import java.util.concurrent.atomic.AtomicInteger;

import timber.log.Timber;

import static com.didekindroid.incidencia.spinner.ControllerComuSpinner.newControllerComuSpinner;
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
public class ControllerComuSpinnerTest extends ControllerAbsTest<ControllerComuSpinner> {

    final static AtomicInteger flagForExecution = new AtomicInteger(0);

    @Rule
    public ActivityTestRule<MockActivity> activityRule = new ActivityTestRule<>(MockActivity.class, true, true);

    UsuarioComunidad pepeUserComu;
    ReactorComuSpinnerIf reactor;
    ViewerComuSpinnerIf controllerViewer;
    Activity activity;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                controllerViewer = new ViewerComuSpinnerForTest(activity);
                // Default initialization of field in ControllerAbsTest.
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
                assertThat(flagForExecution.getAndSet(19), is(0));
                return flagForExecution.get() == 19;
            }
        };
        controller = (ControllerComuSpinner) newControllerComuSpinner(controllerViewer, reactor);
        controller.loadDataInSpinner();
        assertThat(flagForExecution.getAndSet(0), is(19));
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

        assertThat(flagForExecution.getAndSet(0), is(13));

        ArrayAdapter<Comunidad> postAdapter = (ArrayAdapter<Comunidad>) controller.comuSpinner.getAdapter();
        assertThat(postAdapter, is(controller.spinnerAdapter));
        assertThat(postAdapter.getCount(), is(2));
        // Assertions based on viewer.getComunidadSelectedId(): 123L.
        assertThat(controller.comuSpinner.getSelectedItemPosition(), is(1));
    }

    @Override
    @Test
    public void testProcessReactorError()
    {
        controller.processReactorError(new Throwable());
        assertThat(flagForExecution.getAndSet(0), is(23));
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
                assertThat(flagForExecution.getAndSet(119), is(0));
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
        assertThat(flagForExecution.getAndSet(0), is(119));
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
                assertThat(flagForExecution.getAndSet(113), is(0));
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
        assertThat(flagForExecution.getAndSet(0), is(113));
    }

    // ............................ HELPERS ..................................

    class ViewerComuSpinnerForTest extends ViewerDumbImp<Spinner, Object> implements
            ViewerComuSpinnerIf {

        protected ViewerComuSpinnerForTest(Activity activity)
        {
            super(activity);
        }

        @Override
        public ViewerComuSpinnerIf setDataInView(Bundle savedState)
        {
            return this;
        }

        // Used in testProcessBackLoadComusInSpinner().
        public long getComunidadSelectedId()
        {
            assertThat(flagForExecution.getAndSet(13), is(0));
            return 123L;
        }

        @Override
        public ViewerWithSelectIf initSelectedIndex(Bundle savedState)
        {
            return null;
        }

        @Override
        public void saveSelectedIndex(Bundle savedState)
        {
        }

        @Override
        public Spinner doViewInViewer(Activity activity)
        {
            return new Spinner(getManager());
        }

        @Override
        public void replaceView(Object initParams)
        {
        }

        @Override  // Used in testProcessReactorError()
        public UiExceptionIf.ActionForUiExceptionIf processControllerError(UiException ui)
        {
            Timber.d("====================== processControllerError() ====================");
            assertThat(flagForExecution.getAndSet(23), is(0));
            return null;
        }
    }
}