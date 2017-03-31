package com.didekindroid.api;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.didekindroid.R;

import java.util.Collection;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 15/02/17
 * Time: 10:28
 */
@SuppressWarnings("AbstractClassExtendsConcreteClass")
public abstract class CtrlerSpinner<E> extends Controller<Spinner> implements CtrlerSpinnerIf<E> {

    private final Spinner spinnerView;
    private final ArrayAdapter<E> spinnerAdapter;
    private final ViewerSelectableIf<Spinner, CtrlerSpinnerIf> viewerSelect;

    protected CtrlerSpinner(ViewerSelectableIf<Spinner, CtrlerSpinnerIf> viewerIn)
    {
        super(viewerIn);
        viewerSelect = viewerIn;
        spinnerView = viewer.getViewInViewer();
        spinnerAdapter = new ArrayAdapter<>(viewer.getActivity(), R.layout.app_spinner_1_dropdown_item, R.id.app_spinner_1_dropdown_item);
    }

    @Override
    public void onSuccessLoadDataInSpinner(Collection<E> items)
    {
        Timber.d("onSuccessLoadDataInSpinner()");
        spinnerAdapter.clear();
        spinnerAdapter.addAll(items);
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
