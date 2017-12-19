package com.didekindroid.usuario.password;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.didekindroid.R;
import com.didekindroid.api.AbstractSingleObserver;
import com.didekindroid.api.Viewer;
import com.didekindroid.api.router.ActivityInitiatorIf;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.UsuarioBean;
import com.didekindroid.usuario.dao.CtrlerUsuario;
import com.didekinlib.model.usuario.Usuario;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.exception.UiExceptionRouter.ActionsForRouter.show_userData_wrongMail_action;
import static com.didekindroid.router.ActivityRouter.IntrospectRouterToAc.modifyPswd;
import static com.didekindroid.router.ActivityRouter.IntrospectRouterToAc.sendNewPswd;
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
 * <p>
 * Preconditions:
 * 1. An intent is received with the userName. If not, the viewer accessed user data directly.
 */
public final class ViewerPasswordChange extends Viewer<View, CtrlerUsuario> implements ActivityInitiatorIf {

    final AtomicReference<UsuarioBean> usuarioBean;
    @SuppressWarnings("WeakerAccess")
    final AtomicReference<Usuario> oldUserPswd;
    final AtomicReference<String> userName;

    private ViewerPasswordChange(PasswordChangeAc activity)
    {
        super(activity.acView, activity, null);
        usuarioBean = new AtomicReference<>(null);
        oldUserPswd = new AtomicReference<>(null);
        userName = new AtomicReference<>(null);
        if (activity.getIntent().hasExtra(user_name.key)) {
            userName.set(activity.getIntent().getStringExtra(user_name.key));
        }
    }

    static ViewerPasswordChange newViewerPswdChange(PasswordChangeAc activity)
    {
        Timber.d("newViewerPswdChange()");
        final ViewerPasswordChange instance = new ViewerPasswordChange(activity);
        instance.setController(new CtrlerUsuario());
        // Check for userName
        if (instance.userName.get() == null) {
            instance.controller.loadUserData(new AbstractSingleObserver<Usuario>(instance) {
                @Override
                public void onSuccess(Usuario usuario)
                {
                    instance.processBackUserDataLoaded(usuario);
                }
            });
        }
        return instance;
    }

    @SuppressWarnings("WeakerAccess")
    void processBackUserDataLoaded(Usuario usuario)
    {
        Timber.d("processBackUserDataLoaded()");
        userName.compareAndSet(null, usuario.getUserName());
    }

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        // Precondition.
        assertTrue(controller.isRegisteredUser(), user_should_be_registered);

        Button modifyButton = view.findViewById(R.id.password_change_ac_button);
        modifyButton.setOnClickListener(
                v -> {
                    if (checkLoginData()) {
                        controller.changePassword(
                                new PswdChangeCompletableObserver(),
                                oldUserPswd.get(),
                                usuarioBean.get().getUsuario()
                        );
                    }
                }
        );

        Button sendPswdButton = view.findViewById(R.id.password_send_ac_button);
        sendPswdButton.setOnClickListener(
                v -> controller.sendNewPassword(new PswdSendSingleObserver(), new Usuario.UsuarioBuilder().userName(userName.get()).build())
        );
    }

    boolean checkLoginData()
    {
        Timber.i("checkLoginData()");

        usuarioBean.set(new UsuarioBean(userName.get(), null, getPswdDataFromView()[0], getPswdDataFromView()[1]));
        oldUserPswd.set(new Usuario.UsuarioBuilder().userName(userName.get()).password(getPswdDataFromView()[2]).build());
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
            uiException.processMe(activity, show_userData_wrongMail_action);
        } else if (errorMsg.equals(BAD_REQUEST.getHttpMessage())) {
            makeToast(activity, R.string.password_wrong);
        } else {
            uiException.processMe(activity);
        }
    }

    // ...................... SUBSCRIBERS .........................

    class PswdChangeCompletableObserver extends DisposableCompletableObserver {
        @Override
        public void onComplete()
        {
            Timber.d("onComplete(), Thread: %s", Thread.currentThread().getName());
            makeToast(activity, R.string.password_remote_change);
            initAcFromRouter(null, modifyPswd);
        }

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onErrorObserver, Thread: %s", Thread.currentThread().getName());
            onErrorInObserver(e);
        }
    }

    class PswdSendSingleObserver extends DisposableSingleObserver<Boolean> {

        @Override
        public void onSuccess(@io.reactivex.annotations.NonNull Boolean isSendPassword)
        {
            Timber.d("onSuccess(), isSentPassword = %b", isSendPassword);
            if (isSendPassword) {
                makeToast(activity, R.string.password_new_in_login);
            }
            initAcFromRouter(null, sendNewPswd);
        }

        @Override
        public void onError(@io.reactivex.annotations.NonNull Throwable e)
        {
            Timber.d("onError");
            onErrorInObserver(e);
        }
    }
}
