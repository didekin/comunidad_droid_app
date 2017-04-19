package com.didekindroid.incidencia.core;

import com.didekindroid.R;
import com.didekindroid.api.CtrlerSelectionList;

import java.util.Arrays;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 29/03/17
 * Time: 12:01
 */
class CtrlerImportanciaSpinner extends CtrlerSelectionList<String> {


    CtrlerImportanciaSpinner(ViewerImportanciaSpinner viewer)
    {
        super(viewer);
    }

    @Override
    public boolean loadItemsByEntitiyId(Long... entityId)
    {
        Timber.d("loadItemsByEntitiyId()");
        String[] strings = viewer.getActivity().getResources().getStringArray(R.array.IncidImportanciaArray);
        onSuccessLoadItemsInList((Arrays.asList(strings)));
        return strings.length > 0;
    }
}
