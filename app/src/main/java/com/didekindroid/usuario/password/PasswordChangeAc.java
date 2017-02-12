package com.didekindroid.usuario.password;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf;
import com.didekindroid.security.IdentityCacher;
import com.didekindroid.usuario.UsuarioBean;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_name_should_be_initialized;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_be_registered;
import static com.didekindroid.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.usuario.password.PswdChangeReactor.pswdChangeReactor;
import static com.didekindroid.util.ConnectionUtils.isInternetConnected;
import static com.didekindroid.util.DefaultNextAcRouter.routerMap;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.destroySubscriptions;
import static com.didekindroid.util.UIutils.doToolBar;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.util.UIutils.makeToast;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_DATA_NOT_MODIFIED;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_NAME_NOT_FOUND;

/**
 * Preconditions:
 * 1. Registered user.
 * 2. An intent is received with the userName.
 * Postconditions:
 * 1. Password changed and tokenCache updated.
 * 2. It goes to UserDataAc activity.
 */
public class PasswordChangeAc extends AppCompatActivity implements PasswordChangeControllerIf,
        PasswordChangeViewIf {

    CompositeDisposable subscriptions;
    UsuarioBean usuarioBean;
    String userName;
    PswdChangeReactorIf reactor;
    IdentityCacher identityCacher;
    private View mAcView;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        // Preconditions and tokenCacher initialization.
        userName = getIntent().getStringExtra(user_name.key);
        assertTrue(userName != null, user_name_should_be_initialized);
        identityCacher = TKhandler;
        assertTrue(identityCacher.isRegisteredUser(), user_should_be_registered);
        // Initialize reactor.
        reactor = pswdChangeReactor;

        mAcView = getLayoutInflater().inflate(R.layout.password_change_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);

        Button mModifyButton = (Button) findViewById(R.id.password_change_ac_button);
        mModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("mModifyButton.OnClickListener().onClick()");
                if (checkLoginData()) {
                    changePasswordInRemote();
                }
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        Timber.d("onDestroy()");
        super.onDestroy();
        destroySubscriptions(subscriptions);
    }

    // ============================================================
    //    ..... VIEW IMPLEMENTATION ....
    // ============================================================

    @Override
    public String[] getPswdDataFromView()
    {
        Timber.d("getPswdDataFromView()");
        return new String[]{
                ((EditText) mAcView.findViewById(R.id.reg_usuario_password_ediT)).getText().toString(),
                ((EditText) mAcView.findViewById(R.id.reg_usuario_password_confirm_ediT)).getText().toString()
        };
    }

    // ============================================================
    //    ..... CONTROLLER IMPLEMENTATION ....
    // ============================================================

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

    @Override
    public void changePasswordInRemote()
    {
        Timber.d("changePasswordInRemote()");
        reactor.passwordChange(this, usuarioBean.getUsuario());
    }

    @Override
    public void processBackChangedPswdRemote()
    {
        Timber.d("processBackChangedPswdRemote(), Thread: %s", Thread.currentThread().getName());
        makeToast(this, R.string.password_remote_change);
        Intent intent = new Intent(this, routerMap.get(this.getClass()));
        startActivity(intent);
    }

    @Override
    public void processErrorInReactor(Throwable e)
    {
        Timber.d("processErrorInReactor(), Thread: %s, message: %s", Thread.currentThread().getName(), e.getMessage());
        if (e instanceof UiExceptionIf) {
            String ueMessage = ((UiExceptionIf) e).getErrorBean().getMessage();
            if (ueMessage.equals(USER_NAME_NOT_FOUND.getHttpMessage()) || ueMessage.equals(USER_DATA_NOT_MODIFIED.getHttpMessage())) {
                makeToast(this, R.string.username_wrong_in_login);
            } else {
                ((UiException) e).processMe(this, new Intent());
            }
        }
    }

    @Override
    public CompositeDisposable getSubscriptions()
    {
        if (subscriptions == null) {
            subscriptions = new CompositeDisposable();
        }
        return subscriptions;
    }
}
