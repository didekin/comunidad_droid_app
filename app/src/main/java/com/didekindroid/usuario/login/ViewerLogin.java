package com.didekindroid.usuario.login;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.didekindroid.R;
import com.didekindroid.api.Viewer;
import com.didekindroid.exception.UiExceptionIf;
import com.didekindroid.router.ActivityInitiator;
import com.didekindroid.usuario.UsuarioBean;
import com.didekinlib.model.usuario.Usuario;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.didekindroid.usuario.UsuarioBundleKey.login_counter_atomic_int;
import static com.didekindroid.usuario.UsuarioBundleKey.usuario_object;
import static com.didekindroid.usuario.login.LoginAc.PasswordMailDialog.newInstance;
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

final class ViewerLogin extends Viewer<View, CtrlerUsuario> implements ViewerLoginIf {

    @SuppressWarnings("WeakerAccess")
    final AtomicReference<UsuarioBean> usuarioBean;
    private AtomicInteger counterWrong;

    private ViewerLogin(LoginAc activity)
    {
        super(activity.acView, activity, null);
        counterWrong = new AtomicInteger(0);
        usuarioBean = new AtomicReference<>(null);
    }

    static ViewerLoginIf newViewerLogin(LoginAc activity)
    {
        Timber.d("newViewerLogin()");
        ViewerLoginIf instance = new ViewerLogin(activity);
        instance.setController(new CtrlerUsuario());
        return instance;
    }

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");

        Button mLoginButton = view.findViewById(R.id.login_ac_button);
        mLoginButton.setOnClickListener(new LoginButtonListener());
        if (savedState != null) {
            counterWrong.set(savedState.getInt(login_counter_atomic_int.key));
        }
    }

    @Override
    public boolean checkLoginData()
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

    @Override
    public void processLoginBackInView(boolean isLoginOk)
    {
        Timber.d("processLoginBackInView()");

        if (isLoginOk) {
            Timber.d("login OK");
            replaceComponent(new Bundle());
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

    private void showDialogAfterErrors()
    {
        Timber.d("showDialogAfterErrors()");
        DialogFragment newFragment = newInstance(usuarioBean.get().getUsuario());
        newFragment.show(activity.getFragmentManager(), "passwordMailDialog");
    }

    @Override
    public AppCompatDialog doDialogInViewer(final LoginAc.PasswordMailDialog dialogFragment)
    {
        Timber.d("doDialogInViewer()");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.alertDialogTheme);

        builder.setMessage(R.string.send_password_by_mail_dialog)
                .setPositiveButton(
                        R.string.send_password_by_mail_YES,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.dismiss();
                                doDialogPositiveClick((Usuario) dialogFragment.getArguments().getSerializable(usuario_object.key));
                            }
                        }
                )
                .setNegativeButton(
                        R.string.send_password_by_mail_NO,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.dismiss();
                                makeToast(getActivity(), R.string.login_wrong_no_mail);
                                doDialogNegativeClick();
                            }
                        }
                );
        return builder.create();
    }

    @Override
    public void doDialogNegativeClick()
    {
        Timber.d("doDialogNegativeClick()");
        replaceComponent(new Bundle());
    }

    @Override
    public void doDialogPositiveClick(Usuario usuario)
    {
        Timber.d("sendNewPassword()");
        controller.sendNewPassword(new LoginObserver() {
            @Override
            public void onSuccess(Boolean isSentPassword)
            {
                Timber.d("onSuccess()");
                processBackSendPswdInView(isSentPassword);
            }
        }, usuario);
    }

    @Override
    public void processBackSendPswdInView(boolean isSendPassword)
    {
        Timber.d("processBackSendPswdInView()");
        if (isSendPassword) {
            makeToast(activity, R.string.password_new_in_login);
            activity.recreate();
        }
    }

    @Override
    public UiExceptionIf.ActionForUiExceptionIf onErrorInObserver(Throwable error)
    {
        Timber.d("onErrorInObserver()");
        UiExceptionIf.ActionForUiExceptionIf action = null;
        if (getUiExceptionFromThrowable(error).getErrorBean().getMessage().equals(USER_NAME_NOT_FOUND.getHttpMessage())) {
            makeToast(activity, R.string.username_wrong_in_login);
        } else {
            action = super.onErrorInObserver(error);
        }
        return action;
    }

    @Override
    public void saveState(Bundle savedState)
    {
        Timber.d("saveState()");
        if (savedState == null) {
            savedState = new Bundle(1);
        }
        savedState.putInt(login_counter_atomic_int.key, counterWrong.get());
    }

    @Override
    public AtomicInteger getCounterWrong()
    {
        Timber.d("getCounterWrong()");
        return counterWrong;
    }

    // ==================================  HELPERS  =================================

    public void replaceComponent(@NonNull Bundle bundle)
    {
        Timber.d("replaceComponent()");
        new ActivityInitiator(activity).initAcWithFlag(bundle, FLAG_ACTIVITY_NEW_TASK);
        activity.finish();
    }

    @SuppressWarnings("WeakerAccess")
    class LoginButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v)
        {
            Timber.d("View.OnClickListener().onClickLinkToImportanciaUsers()");
            if (checkLoginData()) {
                controller.validateLogin(new LoginObserver() {
                    @Override
                    public void onSuccess(Boolean isLoginOk)
                    {
                        Timber.d("onSuccess()");
                        processLoginBackInView(isLoginOk);
                    }
                }, usuarioBean.get().getUsuario());
            }
        }
    }

    // ............................ SUBSCRIBERS ..................................

    abstract class LoginObserver extends DisposableSingleObserver<Boolean> {

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onError, message: %s", e.getMessage());
            onErrorInObserver(e);
        }
    }
}
