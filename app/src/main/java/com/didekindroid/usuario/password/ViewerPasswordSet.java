package com.didekindroid.usuario.password;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.didekindroid.api.Viewer;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.usuario.dao.CtrlerUsuario;

/**
 * User: pedro@didekin
 * Date: 01/11/2017
 * Time: 12:30
 */

class ViewerPasswordSet extends Viewer<View, CtrlerUsuario> {

    public ViewerPasswordSet(View view, AppCompatActivity activity, ViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
    }
}
