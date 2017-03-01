package com.didekindroid.incidencia.spinner;

import android.os.Bundle;
import android.support.test.runner.AndroidJUnit4;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.didekindroid.ManagerDumbImp;
import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf;
import com.didekindroid.incidencia.spinner.ManagerComuSpinnerIf.ControllerComuSpinnerIf;
import com.didekindroid.incidencia.spinner.ManagerComuSpinnerIf.ViewerComuSpinnerIf;
import com.didekinlib.http.ErrorBean;
import com.didekinlib.model.comunidad.Comunidad;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_LIST_INDEX;
import static com.didekindroid.incidencia.spinner.ViewerComuSpinner.newComuSpinnerViewer;
import static com.didekindroid.testutil.ActivityTestUtils.testClearCtrlSubscriptions;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.makeListTwoUserComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.regTwoUserComuSameUser;
import static com.didekinlib.http.GenericExceptionMsg.TOKEN_NOT_DELETED;
import static io.reactivex.plugins.RxJavaPlugins.reset;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 18/02/17
 * Time: 12:36
 */
@RunWith(AndroidJUnit4.class)
public class ViewerComuSpinnerTest {

    ViewerComuSpinner viewer;
    boolean flagIntent;
    AtomicLong comunidadId = new AtomicLong(0);
    AtomicInteger flagForExecution = new AtomicInteger(0);
    ControllerComuSpinnerIf controller;

    @Before
    public void setUp()
    {
        viewer = newComuSpinnerViewer(new ManagerComuSpinnerForTest());
    }

    // ............................ TESTS ..................................

    @Test
    public void testSetDataInView()
    {
        controller = new ControllerComuSpinnerForTest(viewer);
        // Inject mock controller.
        viewer.injectController(controller);
        assertThat(viewer.setDataInView(), CoreMatchers.<ViewerComuSpinnerIf>is(viewer));
        assertThat(flagForExecution.getAndSet(0), is(19));
    }

    @Test
    public void testInitComuSelectedIndex_NoIntent() throws Exception
    {
        Bundle savedState = new Bundle();
        savedState.putInt(COMUNIDAD_LIST_INDEX.key, 8);
        viewer.initSelectedIndex(savedState);
        assertThat(viewer.getComunidadSelectedIndex(), is(8));
    }

    @Test
    public void testInitComuSelectedIndex_WithIntent() throws Exception
    {
        regTwoUserComuSameUser(makeListTwoUserComu());

        List<Comunidad> comunidades = userComuDaoRemote.getComusByUser();
        // Primera comunidad en la lista: COMU_REAL.
        assertThat(comunidadId.getAndSet(comunidades.get(1).getC_Id()), is(0L));

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            viewer.getController().loadDataInSpinner();
        } finally {
            reset();
        }

        viewer.initSelectedIndex(null);
        // Segunda comunidad en la lista: el orden en spinner es el mismo que la lista devuelta por reactor/dao.
        assertThat(viewer.getComunidadSelectedIndex(), is(1));

        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void testProcessControllerError()
    {
        viewer.processControllerError(new UiException(new ErrorBean(TOKEN_NOT_DELETED)));
        assertThat(flagForExecution.getAndSet(0), is(13));
    }

    @Test
    public void testSaveComuSelectedIndex()
    {
        viewer.comunidadSelectedIndex = 111;
        Bundle newBundle = new Bundle();
        viewer.saveSelectedIndex(newBundle);
        assertThat(newBundle.getInt(COMUNIDAD_LIST_INDEX.key), is(111));
    }

    @Test
    public void testClearControllerSubscriptions(){
        testClearCtrlSubscriptions(controller, viewer);
    }

    // ............................ HELPERS ..................................

    class ManagerComuSpinnerForTest extends ManagerDumbImp implements ManagerComuSpinnerIf {

        @Override
        public long getComunidadIdInIntent()
        {
            // Devuelve comunidadIntent in id: segunda en la lista (ver test).
            return comunidadId.get();
        }

        @Override
        public Spinner initSpinnerView()
        {
            Spinner spinner = getSpinnerViewInManager();
            getSpinnerViewInManager().setOnItemSelectedListener(getSpinnerListener());
            return spinner;
        }

        @Override
        public Spinner getSpinnerViewInManager()
        {
            return new Spinner(getActivity());
        }

        @Override
        public AdapterView.OnItemSelectedListener getSpinnerListener()
        {
            return null;
        }

        @Override   // Used in testProcessReactorErrorInView().
        public UiExceptionIf.ActionForUiExceptionIf processViewerError(UiException ui)
        {
            assertThat(ui.getErrorBean().getMessage(), is(TOKEN_NOT_DELETED.getHttpMessage()));
            assertThat(flagForExecution.getAndSet(13), is(0));
            return null;
        }
    }

    class ControllerComuSpinnerForTest extends ControllerComuSpinner implements
            ControllerComuSpinnerIf {

        ControllerComuSpinnerForTest(ViewerComuSpinnerIf viewerIn)
        {
            super(viewerIn);
        }

        @Override // Used in testSetDataInView().
        public void loadDataInSpinner()
        {
            assertThat(flagForExecution.getAndSet(19), is(0));
        }
    }
}