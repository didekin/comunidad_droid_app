package com.didekindroid.usuario;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.Controller;
import com.didekindroid.lib_one.api.ParentViewerInjectedIf;
import com.didekindroid.lib_one.api.Viewer;
import com.didekindroid.lib_one.api.exception.UiExceptionRouterIf;
import com.didekinlib.model.usuario.Usuario;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 23/05/17
 * Time: 09:56
 */

public class ViewerRegUserFr extends Viewer<View, Controller> {

    ViewerRegUserFr(View view, AppCompatActivity activity, @NonNull ParentViewerInjectedIf parentViewer)
    {
        super(view, activity, parentViewer);
    }

    // ==================================== ViewerIf ===================================

    @Override
    public UiExceptionRouterIf getExceptionRouter()
    {
        Timber.d("getExceptionRouter()");
        return getParentViewer().getExceptionRouter();
    }

    // ==================================== Helpers ====================================

    public Usuario getUserFromViewer(StringBuilder errorBuilder)
    {
        Timber.d("getUserFromViewer()");

        UsuarioBean usuarioBean = new UsuarioBean(
                ((EditText) view.findViewById(R.id.reg_usuario_email_editT)).getText()
                        .toString(),
                ((EditText) view.findViewById(R.id.reg_usuario_alias_ediT)).getText()
                        .toString(),
                null, null
        );
        if (usuarioBean.validateUserNameAlias(activity.getResources(), errorBuilder)) {
            return usuarioBean.getUsuario();
        } else {
            return null;
        }
    }
}
