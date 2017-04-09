package com.didekindroid.incidencia.core;

import android.support.annotation.NonNull;
import android.widget.Spinner;

import com.didekindroid.R;
import com.didekindroid.api.CtrlerSpinner;
import com.didekindroid.api.CtrlerSpinnerIf;
import com.didekindroid.api.ViewerSelectableIf;

import java.util.Arrays;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 29/03/17
 * Time: 12:01
 */
public class CtrlerImportanciaSpinner extends CtrlerSpinner<String> {

    public CtrlerImportanciaSpinner(@NonNull ViewerSelectableIf<Spinner, CtrlerSpinnerIf> viewerIn)
    {
        super(viewerIn);
    }

    @Override
    public boolean loadDataInSpinner()
    {
        String[] strings = viewer.getActivity().getResources().getStringArray(R.array.IncidImportanciaArray);
        onSuccessLoadDataInSpinner(Arrays.asList(strings));
        return getSpinnerAdapter().getCount() > 0;
    }

    @Override
    public int getSelectedFromItemId(long positionInArray)
    {
        Timber.d("getSelectedFromItemId(itemId)");
        // The id of the string shown is its position.
        return (short) positionInArray;
    }
}
