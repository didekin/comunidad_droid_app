package com.didekindroid.usuario.password;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.didekindroid.api.ManagerIf;
import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf.ActionForUiExceptionIf;
import com.didekindroid.usuario.UsuarioBean;

import timber.log.Timber;

import static com.didekindroid.usuario.UsuarioAssertionMsg.user_name_should_be_initialized;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_be_registered;
import static com.didekindroid.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.util.ConnectionUtils.isInternetConnected;
import static com.didekindroid.util.DefaultNextAcRouter.routerMap;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.doToolBar;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.util.UIutils.makeToast;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_NAME_NOT_FOUND;

/**
 * Preconditions:
 * 1. Registered user.
 * 2. An intent is received with the userName.
 * Postconditions:
 * 1. Password changed and tokenCache updated.
 * 2. It goes to UserDataAc activity.
 */
public class PasswordChangeAc extends AppCompatActivity implements ManagerIf<Object>, ViewerPasswordChangeIf<View, Object> {

    UsuarioBean usuarioBean;
    String userName;
    private View acView;
    ControllerPasswordChangeIf controller;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        acView = getLayoutInflater().inflate(R.layout.password_change_ac, null);
        setContentView(acView);
        doToolBar(this, true);
        controller = new ControllerPasswordChange(this);

        // Preconditions.
        userName = getIntent().getStringExtra(user_name.key);
        assertTrue(userName != null, user_name_should_be_initialized);
        assertTrue(controller.isRegisteredUser(), user_should_be_registered);

        Button mModifyButton = (Button) findViewById(R.id.password_change_ac_button);
        mModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("mModifyButton.OnClickListener().onClick()");
                if (checkLoginData()) {
                    controller.changePasswordInRemote(usuarioBean.getUsuario());
                }
            }
        });
    }

    @Override
    protected void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
        clearControllerSubscriptions();
    }

    // ============================================================
    //    ............. VIEWER IMPLEMENTATION ...............
    // ============================================================

    @Override
    public ManagerIf<Object> getManager()
    {
        Timber.d("getContext()");
        return this;
    }

    @Override
    public ActionForUiExceptionIf processControllerError(UiException e)
    {
        Timber.d("processControllerError()");
        ActionForUiExceptionIf action = null;

        if (e.getErrorBean().getMessage().equals(USER_NAME_NOT_FOUND.getHttpMessage())) {
            makeToast(this, R.string.username_wrong_in_login);
        } else {
            action = processViewerError(e);
        }
        return action;
    }

    @Override
    public int clearControllerSubscriptions()
    {
        Timber.d("clearControllerSubscriptions()");
        return controller.clearSubscriptions() ;
    }

    @Override
    public View getViewInViewer()
    {
        Timber.d("getViewInViewer()");
        return acView;
    }

    // ============================================================
    //    .......... VIEWER PASSWORD IMPLEMENTATION ...........
    // ============================================================

    @Override
    public String[] getPswdDataFromView()
    {
        Timber.d("getPswdDataFromView()");
        return new String[]{
                ((EditText) acView.findViewById(R.id.reg_usuario_password_ediT)).getText().toString(),
                ((EditText) acView.findViewById(R.id.reg_usuario_password_confirm_ediT)).getText().toString()
        };
    }

    @Override
    public boolean checkLoginData()
    {
        Timber.i("checkUserData()");

        usuarioBean = new UsuarioBean(userName, null, getPswdDataFromView()[0], getPswdDataFromView()[1]);
        StringBuilder errorBuilder = getErrorMsgBuilder(this);

        if (!usuarioBean.validateWithoutAlias(getResources(), errorBuilder)) {
            makeToast(this, errorBuilder.toString());
            return false;
        }
        if (!isInternetConnected(this)) {
            makeToast(this, R.string.no_internet_conn_toast);
            return false;
        }
        return true;
    }

    // ============================================================
    //    ........... ManagerIf .........
    // ============================================================

    @Override
    public Activity getActivity()
    {
        return this;
    }

    @Override
    public ActionForUiExceptionIf processViewerError(UiException ui)
    {
        Timber.d("processViewerError()");
        return ui.processMe(this, new Intent());
    }

    @Override
    public void replaceRootView(Object initParamsForView)
    {
        Timber.d("replaceRootView()");
        makeToast(this, R.string.password_remote_change);
        Intent intent = new Intent(this, routerMap.get(this.getClass()));
        startActivity(intent);
    }
}
