package com.didekindroid.api;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.didekindroid.R;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 15/02/17
 * Time: 10:28
 */
@SuppressWarnings("AbstractClassExtendsConcreteClass")
public abstract class CtrlerSpinner<E> extends Controller<Spinner> implements CtrlerSpinnerIf<E> {

    final ArrayAdapter<E> spinnerAdapter;
    protected final Spinner spinnerView;
    protected final AtomicReference<ObserverSpinner<E>> atomicObserver;
    private ViewerSelectableIf<Spinner, CtrlerSpinnerIf> viewerSelect;

    protected CtrlerSpinner(ViewerSelectableIf<Spinner, CtrlerSpinnerIf> viewerIn)
    {
        super(viewerIn);
        viewerSelect = viewerIn;
        atomicObserver = new AtomicReference<>(null);
        spinnerAdapter = new ArrayAdapter<>(viewer.getActivity(), R.layout.app_spinner_1_dropdown_item, R.id.app_spinner_1_dropdown_item);
        spinnerView = viewer.getViewInViewer();
    }

    @Override
    public void onSuccessLoadDataInSpinner(Collection<E> comunidades)
    {
        Timber.d("onSuccessLoadDataInSpinner()");
        spinnerAdapter.clear();
        spinnerAdapter.addAll(comunidades);
        spinnerView.setAdapter(spinnerAdapter);
        spinnerView.setSelection(getSelectedFromItemId(viewerSelect.getSelectedItemId()));
    }

    public ArrayAdapter<E> getSpinnerAdapter()
    {
        Timber.d("getSpinnerAdapter()");
        return spinnerAdapter;
    }

    public Spinner getSpinnerView()
    {
        Timber.d("getSpinnerView()");
        return spinnerView;
    }

    @Override
    public abstract boolean loadDataInSpinner();

    @Override
    public abstract int getSelectedFromItemId(final long itemId);
}
