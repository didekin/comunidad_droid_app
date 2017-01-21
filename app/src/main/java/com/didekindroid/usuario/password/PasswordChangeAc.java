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
import com.didekindroid.usuario.UsuarioBean;

import java.util.Objects;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.usuario.password.PswdChangeReactor.pswdChangeReactor;
import static com.didekindroid.util.ConnectionUtils.isInternetConnected;
import static com.didekindroid.util.DefaultNextAcRouter.routerMap;
import static com.didekindroid.util.UIutils.doToolBar;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.util.UIutils.makeToast;

/**
 * Preconditions:
 * 1. Registered user.
 * Postconditions:
 * 1. Password changed and tokenCache updated.
 * 2. It goes to UserDataAc activity.
 */
public class PasswordChangeAc extends AppCompatActivity implements PasswordChangeControllerIf,
        PasswordChangeViewIf {

    CompositeDisposable subscriptions;
    private View mAcView;
    UsuarioBean usuarioBean;
    PswdChangeReactorIf reactor;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate()");

        // Preconditions.
        Objects.equals(TKhandler.isRegisteredUser(), true);
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
                    changePasswordInRemote() ;
                }
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (subscriptions != null) {
            subscriptions.clear();
        }
    }

    // ============================================================
    //    ..... VIEW IMPLEMENTATION ....
    // ============================================================

    @Override
    public String[] getPswdDataFromView()
    {
        // TODO: test.
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
    {  // TODO: test.
        Timber.i("doLoginValidate()");

        usuarioBean = new UsuarioBean(null, null, getPswdDataFromView()[0], getPswdDataFromView()[1]);
        StringBuilder errorBuilder = getErrorMsgBuilder(this);

        if (!usuarioBean.validateLoginData(getResources(), errorBuilder)) {
            makeToast(this, errorBuilder.toString(), R.color.deep_purple_100);
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
        reactor.passwordChangeRemote(this, usuarioBean.getUsuario());
    }

    @Override
    public void processBackChangedPswdRemote(int changedPassword)
    {
        Timber.d("processBackChangedPswdRemote()");
        if (changedPassword > 0) {
            Intent intent = new Intent(this, routerMap.get(this.getClass()));
            startActivity(intent);
        } else {
            makeToast(this, R.string.password_remote_no_change);
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

    @Override
    public void processErrorInReactor(Throwable e)
    {
        Timber.d("processBackErrorInReactor(), %s", e.getMessage());
        if (e instanceof UiException) {
            ((UiException) e).processMe(this, new Intent());
        }
    }
}
