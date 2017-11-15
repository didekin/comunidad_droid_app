package com.didekindroid.comunidad.spinner;

import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.didekindroid.R;
import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.ObserverSingleSelectList;
import com.didekindroid.api.SpinnerTextMockFr;
import com.didekinlib.model.comunidad.Municipio;
import com.didekinlib.model.comunidad.Provincia;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableSingleObserver;

import static com.didekindroid.comunidad.spinner.ViewerMunicipioSpinner.newViewerMunicipioSpinner;
import static com.didekindroid.comunidad.spinner.ViewerMunicipioSpinner.spinnerEvent_default;
import static com.didekindroid.comunidad.utils.ComuBundleKey.MUNICIPIO_SPINNER_EVENT;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 07/05/17
 * Time: 13:47
 */
@RunWith(AndroidJUnit4.class)
public class ViewerMunicipioSpinnerTest {

    final AtomicReference<String> flagLocalExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    ViewerMunicipioSpinner viewer;
    ActivityMock activity;
    Spinner spinner;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();

        final AtomicReference<ViewerMunicipioSpinner> atomicViewer = new AtomicReference<>(null);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.getSupportFragmentManager().beginTransaction()
                        .add(R.id.mock_ac_layout, new SpinnerTextMockFr(), null)
                        .commitNow();
                spinner = (Spinner) activity.findViewById(R.id.municipio_spinner);
                atomicViewer.compareAndSet(null, newViewerMunicipioSpinner(spinner, activity, null));
            }
        });
        waitAtMost(2, SECONDS).untilAtomic(atomicViewer, notNullValue());
        viewer = atomicViewer.get();
    }

    @Test
    public void test_NewViewerMunicipioSpinner() throws Exception
    {
        assertThat(newViewerMunicipioSpinner(spinner, activity, null).getController(), notNullValue());
    }

    @Test
    public void test_InitSelectedItemId() throws Exception
    {
        Bundle bundle = new Bundle();

        // Case 0: no previous initialization
        viewer.initSelectedItemId(bundle);
        assertThat(viewer.getSelectedItemId(), is(0L));
        // Case 1: initialization in savedState
        bundle.putSerializable(MUNICIPIO_SPINNER_EVENT.key, new MunicipioSpinnerEventItemSelect(new Municipio((short) 22, new Provincia((short) 1))));
        viewer.initSelectedItemId(bundle);
        assertThat(viewer.getSelectedItemId(), is(22L));
        // Case 2: initialization in both savedState and municipioIn
        viewer.spinnerEvent = new MunicipioSpinnerEventItemSelect(new Municipio((short) 33, new Provincia((short) 2)));
        viewer.initSelectedItemId(bundle);
        assertThat(viewer.getSelectedItemId(), is(22L));
        // Case 3: initialization in municipioIn
        bundle = null;
        viewer.initSelectedItemId(bundle);
        assertThat(viewer.getSelectedItemId(), is(33L));
    }

    @Test
    public void test_GetSelectedPositionFromItemId() throws Exception
    {
        final List<Municipio> municipios = Arrays.asList(
                new Municipio((short) 11, new Provincia((short) 3)),
                new Municipio((short) 33, new Provincia((short) 1)),
                new Municipio((short) 22, new Provincia((short) 2)));

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.onSuccessLoadItemList(municipios);
                assertThat(viewer.getSelectedPositionFromItemId(11L), is(0));
                assertThat(viewer.getSelectedPositionFromItemId(33L), is(1));
                assertThat(viewer.getSelectedPositionFromItemId(22L), is(2));
                assertThat(viewer.getSelectedPositionFromItemId(122L), is(0));
            }
        });
    }

    @Test
    public void test_DoViewInViewer() throws Exception
    {
        // State
        final String keyBundle = MUNICIPIO_SPINNER_EVENT.key;
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(keyBundle, new MunicipioSpinnerEventItemSelect(new Municipio((short) 11, new Provincia((short) 3))));

        viewer.setController(new CtrlerMunicipioSpinner() {
            @Override
            public boolean loadItemsByEntitiyId(DisposableSingleObserver<List<Municipio>> observer, Long... entityId)
            {
                assertThat(flagLocalExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                return false;
            }
        });
        viewer.doViewInViewer(bundle, null);

        assertThat(viewer.spinnerEvent, is(spinnerEvent_default));
        assertThat(viewer.getSelectedItemId(), is(11L));
        // Check NO call to controller.loadItemsByEntitiyId();
        assertThat(flagLocalExec.get(), is(BEFORE_METHOD_EXEC));
        // Check call to view.setOnItemSelectedListener().
        assertThat(viewer.getViewInViewer().getOnItemSelectedListener(),
                instanceOf(ViewerMunicipioSpinner.MunicipioSelectedListener.class));
    }

    /* Provincia de Las Palmas: pr_id 35, nº de municipios 34. */
    @Test
    public void test_MunicipioSelectedListener()
    {
        // Initial state.
        assertThat(viewer.spinnerEvent, is(spinnerEvent_default));
        assertThat(viewer.getSelectedItemId(), is(0L));
        // Preconditions
        final Municipio municipio = new Municipio((short) 11, new Provincia((short) 35));
        viewer.doViewInViewer(new Bundle(0), new MunicipioSpinnerEventItemSelect(municipio));
        assertThat(viewer.spinnerEvent.getMunicipio(), is(municipio));
        // ItemId is initialized.
        assertThat(viewer.getSelectedItemId(), is(11L));
        // Check controller.loadItemsByEntitiyId() --> onSuccessLoadItemList() --> view.setSelection() ... {--> MunicipioSelectedListener.onItemSelected() }
        // We initialize to 0 the itemSelectedId to checkMenu the call to MunicipioSelectedListener.onItemSelected().
        viewer.setItemSelectedId(2L);
        viewer.getController().loadItemsByEntitiyId(new ObserverSingleSelectList<>(viewer), 35L);
        waitAtMost(3, SECONDS).until((Callable<Adapter>) ((AdapterView<? extends Adapter>) viewer.getViewInViewer())::getAdapter, notNullValue());
        assertThat(viewer.getViewInViewer().getCount(), is(34));
        // MunicipioSelectedListener.onItemSelected() modify municipioIn.
        assertThat(viewer.spinnerEvent.getMunicipio().getProvincia().getProvinciaId(), is((short) 35));
        assertThat(viewer.spinnerEvent.getMunicipio().getCodInProvincia(), is((short) 2));
    }

    @Test
    public void test_SaveState() throws Exception
    {
        Bundle bundle = new Bundle(1);
        viewer.saveState(bundle);
        assertThat(bundle.getLong(MUNICIPIO_SPINNER_EVENT.key), is(0L));

        viewer.spinnerEvent = new MunicipioSpinnerEventItemSelect(new Municipio((short) 11, new Provincia((short) 1)));
        viewer.saveState(bundle);
        assertThat(MunicipioSpinnerEventItemSelect.class.cast(bundle.getSerializable(MUNICIPIO_SPINNER_EVENT.key)), is(viewer.spinnerEvent));
    }
}