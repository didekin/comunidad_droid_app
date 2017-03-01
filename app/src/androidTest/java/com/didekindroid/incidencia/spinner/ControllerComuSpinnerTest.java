package com.didekindroid.incidencia.spinner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.didekindroid.ControllerAbsTest;
import com.didekindroid.ViewerDumbImp;
import com.didekindroid.ViewerWithSelectIf;
import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf;
import com.didekindroid.incidencia.spinner.ControllerComuSpinner.ReactorComuSpinner;
import com.didekindroid.incidencia.spinner.ManagerComuSpinnerIf.ReactorComuSpinnerIf;
import com.didekindroid.incidencia.spinner.ManagerComuSpinnerIf.ViewerComuSpinnerIf;
import com.didekindroid.testutil.MockActivity;
import com.didekinlib.model.comunidad.Comunidad;
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

import static com.didekindroid.comunidad.testutil.ComuDataTestUtil.COMU_EL_ESCORIAL;
import static com.didekindroid.comunidad.testutil.ComuDataTestUtil.COMU_LA_FUENTE;
import static com.didekindroid.incidencia.spinner.ControllerComuSpinner.newControllerComuSpinner;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.makeListTwoUserComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.regTwoUserComuSameUser;
import static com.didekinlib.http.GenericExceptionMsg.TOKEN_NULL;
import static io.reactivex.plugins.RxJavaPlugins.reset;
import static org.hamcrest.CoreMatchers.is;
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

    ControllerComuSpinner controller;
    UsuarioComunidad pepeUserComu;
    ReactorComuSpinnerIf reactor;
    ViewerComuSpinnerIf controllerViewer;

    @Before
    public void setUp()
    {
        controllerViewer = new ViewerComuSpinnerForTest(activityRule.getActivity());
        // Default initialization.
        controller = (ControllerComuSpinner) newControllerComuSpinner(controllerViewer);
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

    @SuppressWarnings("unchecked")
    @Test
    public void testProcessBackLoadComusInSpinner() throws IOException, UiException
    {
        List<Comunidad> comunidades = new ArrayList<>(2);
        comunidades.add(COMU_EL_ESCORIAL);
        comunidades.add(COMU_LA_FUENTE);
        controller.processBackLoadComusInSpinner(comunidades);

        ArrayAdapter<Comunidad> postAdapter = (ArrayAdapter<Comunidad>) controller.comuSpinner.getAdapter();
        assertThat(postAdapter, is(controller.spinnerAdapter));
        assertThat(postAdapter.getCount(), is(2));
        // Assertions based on viewer.getComunidadSelectedIndex().
        assertThat(controller.comuSpinner.getSelectedItemId(), is(1L));
    }

    @Override
    @Test
    protected void testProcessReactorError()
    {
        controller.processReactorError(new Throwable());
        assertThat(flagForExecution.getAndSet(0), is(23));
    }

    // ............................ REACTOR ..................................

    @SuppressWarnings("unchecked")
    @Test
    public void testLoadComunidades() throws IOException, UiException
    {
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
        public ViewerComuSpinnerIf setDataInView()
        {
            return this;
        }

        @Override // Used in testProcessBackLoadComusInSpinner().
        public int getComunidadSelectedIndex()
        {
            return 1;
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
            return new Spinner(getActivity());
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
            assertThat(ui.getErrorBean().getMessage(), is(TOKEN_NULL.getHttpMessage()));
            return ui.processMe(activity, new Intent());
        }
    }
}