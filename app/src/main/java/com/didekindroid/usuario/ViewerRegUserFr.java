package com.didekindroid.usuario;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.didekindroid.R;
import com.didekindroid.api.Controller;
import com.didekindroid.api.Viewer;
import com.didekindroid.api.ViewerIf;
import com.didekinlib.model.usuario.Usuario;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 23/05/17
 * Time: 09:56
 */

public class ViewerRegUserFr extends Viewer<View, Controller> {

    ViewerRegUserFr(View view, AppCompatActivity activity, ViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
    }

    // ==================================== ViewerIf ===================================

    // ==================================== Helpers ====================================

    public Usuario getUserFromViewer(StringBuilder errorBuilder)
    {
        Timber.d("getUserFromViewer()");

        UsuarioBean usuarioBean = new UsuarioBean(
                ((EditText) view.findViewById(R.id.reg_usuario_email_editT)).getText()
                        .toString(),
                ((EditText) view.findViewById(R.id.reg_usuario_alias_ediT)).getText()
                        .toString(),
                ((EditText) view.findViewById(R.id.reg_usuario_password_ediT)).getText()
                        .toString(),
                ((EditText) view.findViewById(R.id.reg_usuario_password_confirm_ediT)).getText()
                        .toString()
        );
        if (usuarioBean.validate(activity.getResources(), errorBuilder)) {
            return usuarioBean.getUsuario();
        } else {
            return null;
        }
    }
}