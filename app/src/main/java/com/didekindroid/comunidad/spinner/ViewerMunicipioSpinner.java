package com.didekindroid.comunidad.spinner;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Spinner;

import com.didekindroid.api.CtrlerSelectionList;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerSelectionList;
import com.didekinlib.model.comunidad.Municipio;

import timber.log.Timber;

import static com.didekindroid.comunidad.ComuBundleKey.MUNICIPIO_ID;
import static com.didekindroid.comunidad.spinner.CtrlerMunicipioSpinner.newCtrlerMunicipioSpinner;

/**
 * User: pedro@didekin
 * Date: 05/05/17
 * Time: 16:32
 */

public class ViewerMunicipioSpinner extends
        ViewerSelectionList<Spinner, CtrlerSelectionList<Municipio>, Municipio> {

    Municipio municipioIn;

    protected ViewerMunicipioSpinner(Spinner view, Activity activity, ViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
    }

    static ViewerMunicipioSpinner newViewerMunicipioSpinner(Spinner spinner, Activity activity, ViewerIf parentViewer)
    {
        Timber.d("newViewerMunicipioSpinner()");
        ViewerMunicipioSpinner instance = new ViewerMunicipioSpinner(spinner, activity, parentViewer);
        instance.setController(newCtrlerMunicipioSpinner(instance));
        return instance;
    }

    // ==================================== ViewerSelectionListIf ====================================

    @Override
    public void initSelectedItemId(Bundle savedState)
    {
        Timber.d("initSelectedItemId()");
        if (savedState != null && savedState.containsKey(MUNICIPIO_ID.key)) {
            itemSelectedId = savedState.getLong(MUNICIPIO_ID.key, 0);
        } else if (municipioIn != null && municipioIn.getmId() > 0) {
            itemSelectedId = municipioIn.getmId();
        } else {
            itemSelectedId = 0;
        }
    }

    @Override
    public int getSelectedPositionFromItemId(long itemId)
    {
        return 0; // TODO: implementar. MunicipioId != position.
    }
}
