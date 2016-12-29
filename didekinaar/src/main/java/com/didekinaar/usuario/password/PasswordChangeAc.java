package com.didekinaar.usuario.password;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.didekinaar.R;
import com.didekinaar.usuario.UsuarioBean;
import com.didekinaar.usuario.password.PswdChangeAcObservable.PasswordChangeSubscriber;
import com.didekinaar.utils.ConnectionUtils;

import java.util.Objects;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.didekinaar.security.TokenIdentityCacher.TKhandler;
import static com.didekinaar.usuario.password.PswdChangeAcObservable.isPasswordChanged;
import static com.didekinaar.utils.UIutils.doToolBar;
import static com.didekinaar.utils.UIutils.getErrorMsgBuilder;
import static com.didekinaar.utils.UIutils.makeToast;

/**
 * Preconditions:
 * 1. Registered user.
 * Postconditions:
 * 1. Password changed and tokenCache updated.
 * 2. It goes to UserDataAc activity.
 */
@SuppressWarnings({"ConstantConditions", "AbstractClassExtendsConcreteClass"})
public abstract class PasswordChangeAc extends AppCompatActivity implements PasswordChangeControllerIf {

    private View mAcView;
    Subscription subscription;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate()");

        // Preconditions.
        Objects.equals(TKhandler.isRegisteredUser(), true);

        mAcView = getLayoutInflater().inflate(R.layout.password_change_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);

        Button mModifyButton = (Button) findViewById(R.id.password_change_ac_button);
        mModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("mModifyButton.OnClickListener().onClick()");
                modifyPassword();
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    // ============================================================
    //    ..... CONTROLLER IMPLEMENTATION ....
    // ============================================================

    @Override
    public void modifyPassword()
    {
        Timber.d("modifyPassword()");

        UsuarioBean usuarioBean = new UsuarioBean(
                null,
                null,
                ((EditText) mAcView.findViewById(R.id.reg_usuario_password_ediT)).getText()
                        .toString(),
                ((EditText) mAcView.findViewById(R.id.reg_usuario_password_confirm_ediT)).getText()
                        .toString()
        );

        StringBuilder errorBuilder = getErrorMsgBuilder(this);

        if (!usuarioBean.validateDoublePassword(getResources(), errorBuilder)) {
            makeToast(this, errorBuilder.toString(), R.color.deep_purple_100);
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            makeToast(this, R.string.no_internet_conn_toast);
        } else {
            subscription = isPasswordChanged(usuarioBean.getPassword())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new PasswordChangeSubscriber(this));
        }
    }
}
