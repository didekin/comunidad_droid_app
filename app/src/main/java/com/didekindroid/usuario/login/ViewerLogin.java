package com.didekindroid.usuario.login;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.didekindroid.R;
import com.didekindroid.api.Viewer;
import com.didekindroid.api.router.ActivityInitiatorIf;
import com.didekindroid.usuario.UsuarioBean;
import com.didekindroid.usuario.dao.CtrlerUsuario;
import com.didekinlib.model.usuario.Usuario;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.usuario.UsuarioBundleKey.login_counter_atomic_int;
import static com.didekindroid.usuario.login.PasswordMailDialog.newInstance;
import static com.didekindroid.util.ConnectionUtils.isInternetConnected;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.util.UIutils.getUiExceptionFromThrowable;
import static com.didekindroid.util.UIutils.makeToast;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_NAME_NOT_FOUND;

/**
 * User: pedro@didekin
 * Date: 21/03/17
 * Time: 12:05
 */

public final class ViewerLogin extends Viewer<View, CtrlerUsuario> implements ActivityInitiatorIf {

    final AtomicReference<UsuarioBean> usuarioBean;
    private AtomicInteger counterWrong;

    private ViewerLogin(LoginAc activity)
    {
        super(activity.acView, activity, null);
        counterWrong = new AtomicInteger(0);
        usuarioBean = new AtomicReference<>(null);
    }

    static ViewerLogin newViewerLogin(LoginAc activity)
    {
        Timber.d("newViewerLogin()");
        ViewerLogin instance = new ViewerLogin(activity);
        instance.setController(new CtrlerUsuario());
        return instance;
    }

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");

        if (viewBean != null) {
            ((EditText) view.findViewById(R.id.reg_usuario_email_editT)).setText(String.class.cast(viewBean));
        }

        Button mLoginButton = view.findViewById(R.id.login_ac_button);
        mLoginButton.setOnClickListener(
                v -> {
                    Timber.d("onClick()");
                    if (checkLoginData()) {
                        controller.validateLogin(
                                new LoginObserver() {
                                    @Override
                                    public void onSuccess(Boolean isLoginOk)
                                    {
                                        processLoginBackInView(isLoginOk);
                                    }
                                },
                                usuarioBean.get().getUsuario()
                        );
                    }
                }
        );

        FloatingActionButton fab = view.findViewById(R.id.login_help_fab);
        fab.setOnClickListener(v -> showDialogAfterErrors());

        if (savedState != null) {
            counterWrong.set(savedState.getInt(login_counter_atomic_int.key));
        }
    }

    boolean checkLoginData()
    {
        Timber.i("checkLoginData()");
        usuarioBean.set(new UsuarioBean(getLoginDataFromView()[0], null, getLoginDataFromView()[1], null));

        StringBuilder errorBuilder = getErrorMsgBuilder(activity);
        if (!usuarioBean.get().validateLoginData(activity.getResources(), errorBuilder)) {
            makeToast(activity, errorBuilder.toString());
            return false;
        }
        if (!isInternetConnected(activity)) {
            makeToast(activity, R.string.no_internet_conn_toast);
            return false;
        }
        return true;
    }

    String[] getLoginDataFromView()
    {
        Timber.d("getLoginDataFromView()");
        return new String[]{
                ((EditText) view.findViewById(R.id.reg_usuario_email_editT)).getText().toString(),
                ((EditText) view.findViewById(R.id.reg_usuario_password_ediT)).getText().toString()
        };
    }

    void processLoginBackInView(boolean isLoginOk)
    {
        Timber.d("processLoginBackInView()");

        if (isLoginOk) {
            Timber.d("login OK");
            initAcFromActivity(null);
            activity.finish();
        } else {
            int counter = counterWrong.addAndGet(1);
            Timber.d("Password wrong, counterWrong = %d%n", counter - 1);
            if (counter > 3) { /* Password wrong*/
                showDialogAfterErrors();
            } else {
                makeToast(activity, R.string.password_wrong);
            }
        }
    }

    void showDialogAfterErrors()
    {
        Timber.d("showDialogAfterErrors()");
        DialogFragment newFragment = newInstance(usuarioBean.get());
        newFragment.show(activity.getFragmentManager(), "passwordMailDialog");
    }

    void doDialogPositiveClick(Usuario usuario)
    {
        Timber.d("sendNewPassword()");
        if (usuario == null) {
            makeToast(activity, R.string.username_wrong_in_login);
        }
        controller.sendNewPassword(new LoginObserver() {
            @Override
            public void onSuccess(Boolean isSentPassword)
            {
                processBackSendPswdInView(isSentPassword);
            }
        }, usuario);
    }

    void processBackSendPswdInView(boolean isSendPassword)
    {
        Timber.d("processBackSendPswdInView()");
        if (isSendPassword) {
            makeToast(activity, R.string.password_new_in_login);
            activity.recreate();
        }
    }

    @SuppressWarnings("ThrowableNotThrown")
    @Override
    public void onErrorInObserver(Throwable error)
    {
        Timber.d("onErrorInObserver()");
        if (getUiExceptionFromThrowable(error).getErrorBean().getMessage().equals(USER_NAME_NOT_FOUND.getHttpMessage())) {
            makeToast(activity, R.string.username_wrong_in_login);
        } else {
            super.onErrorInObserver(error);
        }
    }

    // =========================  LyfeCicle  =========================

    @Override
    public void saveState(Bundle savedState)
    {
        Timber.d("saveState()");
        if (savedState == null) {
            savedState = new Bundle(1);
        }
        savedState.putInt(login_counter_atomic_int.key, counterWrong.get());
    }

    // =========================  HELPERS  =========================

    AtomicInteger getCounterWrong()
    {
        Timber.d("getCounterWrong()");
        return counterWrong;
    }

    // ============================================================
    // ....................... SUBSCRIBERS ...................
    // ============================================================

    abstract class LoginObserver extends DisposableSingleObserver<Boolean> {

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onError, message: %s", e.getMessage());
            onErrorInObserver(e);
        }
    }
}
