package com.didekindroid.usuario.password;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.didekindroid.R;
import com.didekindroid.api.ViewBean;
import com.didekindroid.api.Viewer;
import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf;
import com.didekindroid.usuario.UsuarioBean;

import java.util.concurrent.atomic.AtomicReference;

import timber.log.Timber;

import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_be_registered;
import static com.didekindroid.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.util.CommonAssertionMsg.bean_fromView_should_be_initialized;
import static com.didekindroid.util.ConnectionUtils.isInternetConnected;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.util.UIutils.makeToast;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_NAME_NOT_FOUND;

/**
 * User: pedro@didekin
 * Date: 21/03/17
 * Time: 20:08
 */

class ViewerPasswordChange extends Viewer<View, CtrlerPasswordChangeIf> implements
        ViewerPasswordChangeIf {

    @SuppressWarnings("WeakerAccess")
    final AtomicReference<UsuarioBean> usuarioBean;

    ViewerPasswordChange(PasswordChangeAc activity)
    {
        super(activity.acView, activity, null);
        usuarioBean = new AtomicReference<>(null);
    }

    static ViewerPasswordChangeIf newViewerPswdChange(PasswordChangeAc activity)
    {
        ViewerPasswordChangeIf instance = new ViewerPasswordChange(activity);
        instance.setController(new CtrlerPasswordChange(instance));
        return instance;
    }

    @Override
    public void doViewInViewer(Bundle savedState, ViewBean viewBean)
    {
        // Precondition.
        assertTrue(controller.isRegisteredUser(), user_should_be_registered);

        Button mModifyButton = (Button) view.findViewById(R.id.password_change_ac_button);
        mModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("mModifyButton.OnClickListener().onClick()");
                if (checkLoginData()) {
                    assertTrue(usuarioBean.get() != null, bean_fromView_should_be_initialized);
                    controller.changePasswordInRemote(usuarioBean.get().getUsuario());
                }
            }
        });
    }

    @NonNull
    String initUserName()
    {
        return activity.getIntent().getStringExtra(user_name.key);
    }

    @Override
    public boolean checkLoginData()
    {
        Timber.i("checkLoginData()");

        usuarioBean.set(new UsuarioBean(initUserName(), null, getPswdDataFromView()[0], getPswdDataFromView()[1]));
        StringBuilder errorBuilder = getErrorMsgBuilder(activity);

        if (!usuarioBean.get().validateWithoutAlias(activity.getResources(), errorBuilder)) {
            makeToast(activity, errorBuilder.toString());
            return false;
        }
        if (!isInternetConnected(activity)) {
            makeToast(activity, R.string.no_internet_conn_toast);
            return false;
        }
        return true;
    }

    @NonNull
    String[] getPswdDataFromView()
    {
        Timber.d("getPswdDataFromView()");
        return new String[]{
                ((EditText) view.findViewById(R.id.reg_usuario_password_ediT)).getText().toString(),
                ((EditText) view.findViewById(R.id.reg_usuario_password_confirm_ediT)).getText().toString()
        };
    }

    @Override
    public UiExceptionIf.ActionForUiExceptionIf processControllerError(UiException ui)
    {
        Timber.d("processControllerError()");
        UiExceptionIf.ActionForUiExceptionIf action = null;

        if (ui.getErrorBean().getMessage().equals(USER_NAME_NOT_FOUND.getHttpMessage())) {
            makeToast(activity, R.string.username_wrong_in_login);
        } else {
            action = super.processControllerError(ui);
        }
        return action;
    }
}
