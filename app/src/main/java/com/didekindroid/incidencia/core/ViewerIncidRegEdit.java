package com.didekindroid.incidencia.core;

import android.app.Activity;
import android.view.View;

import com.didekindroid.api.Viewer;
import com.didekindroid.api.ViewerIf;

/**
 * User: pedro@didekin
 * Date: 13/04/17
 * Time: 11:33
 */

@SuppressWarnings("AbstractClassExtendsConcreteClass")
public abstract class ViewerIncidRegEdit extends Viewer<View, CtrlerIncidRegEditFr> {

    protected ViewerIncidRegEdit(View view, Activity activity, ViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
    }

    public void onSuccessRegisterIncidImportancia(int rowInserted){
        throw new UnsupportedOperationException();
    }
    public void onSuccessModifyIncidImportancia(int rowInserted){
        throw new UnsupportedOperationException();
    }
    public void onSuccessEraseIncidencia(int rowsDeleted){
        throw  new UnsupportedOperationException();
    }
}
