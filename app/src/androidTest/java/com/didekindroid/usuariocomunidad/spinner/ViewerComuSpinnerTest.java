package com.didekindroid.usuariocomunidad.spinner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.Spinner;

import com.didekindroid.R;
import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.CtrlerSelectionListIf;
import com.didekindroid.api.SpinnerMockFr;
import com.didekindroid.api.ViewerMock;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.core.IncidenciaBean;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.comunidad.Municipio;
import com.didekinlib.model.comunidad.Provincia;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekindroid.usuariocomunidad.spinner.ViewerComuSpinner.newViewerComuSpinner;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.makeListTwoUserComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.regTwoUserComuSameUser;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 16/03/17
 * Time: 19:13
 */
@RunWith(AndroidJUnit4.class)
public class ViewerComuSpinnerTest {

    final AtomicReference<String> flagLocalExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<ActivityMock>(ActivityMock.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            try {
                regTwoUserComuSameUser(makeListTwoUserComu()); // TODO: para qué dos comunidades.
            } catch (IOException | UiException e) {
                fail();
            }
        }
    };

    ViewerComuSpinner viewer;
    ActivityMock activity;
    Spinner spinner;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();

        final AtomicReference<ViewerComuSpinner> atomicViewer = new AtomicReference<>(null);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.getSupportFragmentManager().beginTransaction()
                        .add(R.id.mock_ac_layout, new SpinnerMockFr(), null)
                        .commitNow();
                spinner = (Spinner) activity.findViewById(R.id.comunidad_spinner);
                atomicViewer.compareAndSet(null, newViewerComuSpinner(spinner, activity, new ViewerMock<View, CtrlerSelectionListIf>(activity)));
            }
        });
        waitAtMost(2, SECONDS).untilAtomic(atomicViewer, notNullValue());
        viewer = atomicViewer.get();
    }

    @After
    public void cleanUp() throws UiException
    {
        cleanOneUser(USER_JUAN);
    }

    // ======================================= TESTS ===============================================

    @Test
    public void testNewViewerComuSpinner() throws Exception
    {
        ViewerComuSpinner viewer = newViewerComuSpinner(spinner, activity, null);
        assertThat(viewer, notNullValue());
        assertThat(viewer.getController(), notNullValue());
    }

    @Test
    public void testOnSuccessLoadItems()
    {
        // TODO
    }

    @Test
    public void testInitSelectedItemId() throws Exception
    {
        viewer.spinnerBean = new IncidenciaBean();
        Bundle savedState = new Bundle();

        viewer.initSelectedItemId(savedState);
        assertThat(viewer.getSelectedItemId(), is(0L));

        savedState = null;
        viewer.spinnerBean.setComunidadId((short) 13);
        viewer.initSelectedItemId(savedState);
        assertThat(viewer.getSelectedItemId(), is(13L));

        savedState = new Bundle();
        savedState.putLong(COMUNIDAD_ID.key, 8L);
        assertThat(viewer.spinnerBean.getComunidadId(), is(13L));
        viewer.initSelectedItemId(savedState);
        assertThat(viewer.getSelectedItemId(), is(8L));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetSelectedViewFromItemId() throws Exception
    {
        /*List<Comunidad> comunidades = makeListComu();
        ArrayAdapter<Comunidad> adapterIn = (ArrayAdapter<Comunidad>) ViewerComuSpinner.class.cast(viewer).getViewInViewer().getAdapter();
        adapterIn.addAll(comunidades);
        controller.getSpinnerView().setAdapter(controller.getSpinnerAdapter());
        assertThat(controller.getSelectedFromItemId(321L), is(0));
        assertThat(controller.getSelectedFromItemId(123L), is(1));*/
        // TODO: revisar entero.
    }

    @Test
    public void testDoViewInViewer() throws Exception
    {
        /*final String keyBundle = COMUNIDAD_ID.key;
        IncidenciaBean incidenciaBean = new IncidenciaBean();
        Bundle bundleTest = new Bundle(1);
        bundleTest.putLong(keyBundle, 122L);

        // Inject mockController.
        AtomicReference<String> flagExec = doCtrlerInSpinnerViewer(viewer);
        viewer.doViewInViewer(bundleTest, incidenciaBean);

        // Check call to initSelectedItemId().
        assertThat(viewer.getSelectedItemId(), allOf(
                is(bundleTest.getLong(keyBundle)),
                is(122L)
        ));
        // Check call to controller.loadDataInSpinner();
        assertThat(flagExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
        // Check call to view.setOnItemSelectedListener().
        ViewerComuSpinner.ComuSelectedListener listener =
                (ViewerComuSpinner.ComuSelectedListener) viewer.getViewInViewer().getOnItemSelectedListener();
        assertThat(listener, notNullValue());*/  // TODO: revisar
    }

    @Test
    public void testSavedState() throws Exception
    {
        viewer.setItemSelectedId(111L);
        Bundle newBundle = new Bundle();
        viewer.saveState(newBundle);
        assertThat(newBundle.getLong(COMUNIDAD_ID.key), is(111L));
    }

    // ======================================= HELPERS ===============================================

    @NonNull
    private List<Comunidad> makeListComu()
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

}