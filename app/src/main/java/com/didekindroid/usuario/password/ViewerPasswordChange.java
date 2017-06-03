package com.didekindroid.usuario.password;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.didekindroid.R;
import com.didekindroid.api.Viewer;
import com.didekindroid.exception.UiExceptionIf;
import com.didekindroid.router.ActivityInitiator;
import com.didekindroid.usuario.UsuarioBean;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableCompletableObserver;
import timber.log.Timber;

import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_be_registered;
import static com.didekindroid.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.util.CommonAssertionMsg.bean_fromView_should_be_initialized;
import static com.didekindroid.util.ConnectionUtils.isInternetConnected;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.util.UIutils.getUiExceptionFromThrowable;
import static com.didekindroid.util.UIutils.makeToast;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_NAME_NOT_FOUND;

/**
 * User: pedro@didekin
 * Date: 21/03/17
 * Time: 20:08
 */

@SuppressWarnings("ClassWithOnlyPrivateConstructors")
class ViewerPasswordChange extends Viewer<View, CtrlerPasswordChangeIf>
        implements ViewerPasswordChangeIf {

    @SuppressWarnings("WeakerAccess")
    final AtomicReference<UsuarioBean> usuarioBean;
    final String userName;

    ViewerPasswordChange(PasswordChangeAc activity)
    {
        super(activity.acView, activity, null);
        usuarioBean = new AtomicReference<>(null);
        userName = activity.getIntent().getStringExtra(user_name.key);
    }

    static ViewerPasswordChangeIf newViewerPswdChange(PasswordChangeAc activity)
    {
        ViewerPasswordChangeIf instance = new ViewerPasswordChange(activity);
        instance.setController(new CtrlerPasswordChange());
        return instance;
    }

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        // Precondition.
        assertTrue(controller.isRegisteredUser(), user_should_be_registered);

        Button mModifyButton = (Button) view.findViewById(R.id.password_change_ac_button);
        mModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("onClick()");
                if (checkLoginData()) {
                    assertTrue(usuarioBean.get() != null, bean_fromView_should_be_initialized);
                    controller.changePasswordInRemote(new PswdChangeSingleObserver(), usuarioBean.get().getUsuario());
                }
            }
        });
    }

    @Override
    public boolean checkLoginData()
    {
        Timber.i("checkLoginData()");

        usuarioBean.set(new UsuarioBean(userName, null, getPswdDataFromView()[0], getPswdDataFromView()[1]));
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

    public void replaceComponent(@NonNull Bundle bundle)
    {
        Timber.d("initActivityWithBundle()");
        new ActivityInitiator(activity).initActivityWithBundle(bundle);
    }

    // ............................ SUBSCRIBERS ..................................

    @SuppressWarnings("WeakerAccess")
    class PswdChangeSingleObserver extends DisposableCompletableObserver {

        @Override
        public void onComplete()
        {
            Timber.d("onComplete(), Thread: %s", Thread.currentThread().getName());
            replaceComponent(new Bundle());
        }

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onErrorObserver, Thread: %s", Thread.currentThread().getName());
            onErrorInObserver(e);
        }
    }
}
