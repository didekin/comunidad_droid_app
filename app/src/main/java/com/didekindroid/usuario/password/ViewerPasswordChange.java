package com.didekindroid.usuario.password;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.didekindroid.R;
import com.didekindroid.api.Viewer;
import com.didekindroid.exception.ActionForUiException;
import com.didekindroid.exception.UiException;
import com.didekindroid.router.ActivityInitiator;
import com.didekindroid.usuario.UsuarioBean;
import com.didekindroid.usuario.login.CtrlerUsuario;
import com.didekindroid.usuario.userdata.UserDataAc;
import com.didekinlib.model.usuario.Usuario;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_be_registered;
import static com.didekindroid.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.util.ConnectionUtils.isInternetConnected;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.util.UIutils.getUiExceptionFromThrowable;
import static com.didekindroid.util.UIutils.makeToast;
import static com.didekinlib.http.GenericExceptionMsg.BAD_REQUEST;
import static com.didekinlib.model.common.dominio.ValidDataPatterns.PASSWORD;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.PASSWORD_NOT_SENT;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_NAME_NOT_FOUND;

/**
 * User: pedro@didekin
 * Date: 21/03/17
 * Time: 20:08
 */

@SuppressWarnings("ClassWithOnlyPrivateConstructors")
public class ViewerPasswordChange extends Viewer<View, CtrlerUsuario> {

    @SuppressWarnings("WeakerAccess")
    final AtomicReference<UsuarioBean> usuarioBean;
    @SuppressWarnings("WeakerAccess")
    final AtomicReference<Usuario> oldUserPswd;
    final String userName;

    private ViewerPasswordChange(PasswordChangeAc activity)
    {
        super(activity.acView, activity, null);
        usuarioBean = new AtomicReference<>(null);
        oldUserPswd = new AtomicReference<>(null);
        userName = activity.getIntent().getStringExtra(user_name.key);
    }

    static ViewerPasswordChange newViewerPswdChange(PasswordChangeAc activity)
    {
        ViewerPasswordChange instance = new ViewerPasswordChange(activity);
        instance.setController(new CtrlerUsuario());
        return instance;
    }

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        // Precondition.
        assertTrue(controller.isRegisteredUser(), user_should_be_registered);

        Button modifyButton = view.findViewById(R.id.password_change_ac_button);
        modifyButton.setOnClickListener(new ModifyPswdButtonListener());

        Button sendPswdButton = view.findViewById(R.id.password_send_ac_button);
        sendPswdButton.setOnClickListener(new SendNewPswdButtonListener());
    }

    boolean checkLoginData()
    {
        Timber.i("checkLoginData()");

        usuarioBean.set(new UsuarioBean(userName, null, getPswdDataFromView()[0], getPswdDataFromView()[1]));
        oldUserPswd.set(new Usuario.UsuarioBuilder().userName(userName).password(getPswdDataFromView()[2]).build());
        StringBuilder errorBuilder = getErrorMsgBuilder(activity);

        if (!usuarioBean.get().validateWithoutAlias(activity.getResources(), errorBuilder)) {
            makeToast(activity, errorBuilder.toString());
            return false;
        }
        if (!PASSWORD.isPatternOk(oldUserPswd.get().getPassword())) {
            makeToast(activity, R.string.password_wrong);
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
                ((EditText) view.findViewById(R.id.reg_usuario_password_confirm_ediT)).getText().toString(),
                ((EditText) view.findViewById(R.id.password_validation_ediT)).getText().toString()
        };
    }

    @Override
    public void onErrorInObserver(Throwable error)
    {
        Timber.d("onErrorInObserver()");
        UiException uiException = getUiExceptionFromThrowable(error);
        String errorMsg = uiException.getErrorBean().getMessage();

        if (errorMsg.equals(USER_NAME_NOT_FOUND.getHttpMessage())
                || errorMsg.equals(PASSWORD_NOT_SENT.getHttpMessage())) {
            uiException.processMe(activity, new ActionForUiException(UserDataAc.class, R.string.user_email_wrong));
        } else if (errorMsg.equals(BAD_REQUEST.getHttpMessage())) {
            makeToast(activity, R.string.password_wrong);
        } else {
            uiException.processMe(activity);
        }
    }

    // ............................ SUBSCRIBERS ..................................

    @SuppressWarnings("WeakerAccess")
    class PswdChangeCompletableObserver extends DisposableCompletableObserver {

        View.OnClickListener listener;

        public PswdChangeCompletableObserver(ModifyPswdButtonListener modifyPswdButtonListener)
        {
            listener = modifyPswdButtonListener;
        }

        @Override
        public void onComplete()
        {
            Timber.d("onComplete(), Thread: %s", Thread.currentThread().getName());
            makeToast(activity, R.string.password_remote_change);
            new ActivityInitiator(activity).initAcFromListener(new Bundle(0), listener.getClass());
        }

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onErrorObserver, Thread: %s", Thread.currentThread().getName());
            onErrorInObserver(e);
        }
    }

    @SuppressWarnings("WeakerAccess")
    class PswdSendSingleObserver extends DisposableSingleObserver<Boolean> {

        View.OnClickListener listener;

        public PswdSendSingleObserver(SendNewPswdButtonListener sendNewPswdButtonListener)
        {
            listener = sendNewPswdButtonListener;
        }

        @Override
        public void onSuccess(@io.reactivex.annotations.NonNull Boolean isSendPassword)
        {
            Timber.d("onSuccess(), isSentPassword = %b", isSendPassword);
            if (isSendPassword) {
                makeToast(activity, R.string.password_new_in_login);
            }
            new ActivityInitiator(activity).initAcFromListener(new Bundle(0), listener.getClass());
        }

        @Override
        public void onError(@io.reactivex.annotations.NonNull Throwable e)
        {
            Timber.d("onError");
            onErrorInObserver(e);
        }
    }

    // ............................ HELPER CLASSES AND METHODS ..................................

    @SuppressWarnings("WeakerAccess")
    public class ModifyPswdButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v)
        {
            Timber.d("onClick()");
            if (checkLoginData()) {
                controller.changePasswordInRemote(
                        new PswdChangeCompletableObserver(this),
                        oldUserPswd.get(),
                        usuarioBean.get().getUsuario()
                );
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    public class SendNewPswdButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view)
        {
            Timber.d("onClick()");
            controller.sendNewPassword(
                    new PswdSendSingleObserver(this),
                    new Usuario.UsuarioBuilder().userName(userName).build());
        }
    }
}
