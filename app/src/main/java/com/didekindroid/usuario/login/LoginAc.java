package com.didekindroid.usuario.login;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.didekindroid.usuario.UsuarioBean;
import com.didekindroid.util.ConnectionUtils;
import com.didekindroid.R;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.didekindroid.usuario.login.LoginAc.PasswordMailDialog.newInstance;
import static com.didekindroid.util.UIutils.doToolBar;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.util.UIutils.makeToast;

/**
 * User: pedro
 * Date: 15/12/14
 * Time: 10:04
 */

/**
 * Preconditions:
 * 1. The user is not necessarily registered: she might have erased the security app data.
 * Results:
 * 1a. If successful, the activity ComuSearchAc is presented and the security data are updated.
 * 1b. If the userName doesn't exist, the user is invited to register.
 * 1c. If the userName exists, but the passowrd is not correct, after three failed intents,  a new passord is sent
 * by mail, after her confirmation.
 */
@SuppressWarnings("AbstractClassExtendsConcreteClass")
public abstract class LoginAc extends AppCompatActivity implements LoginViewIf, LoginControllerIf {

    protected Class<? extends Activity> defaultActivityClassToGo;
    View mAcView;
    CompositeSubscription subscriptions;
    private volatile int counterWrong;

    // Template method to be overwritten in the apps.
    protected abstract void setDefaultActivityClassToGo(Class<? extends Activity> activityClassToGo);

    // Template method to be overwritten in the apps.
    protected abstract int getDialogThemeId();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Timber.i("Entered onCreate()");
        super.onCreate(savedInstanceState);

        mAcView = getLayoutInflater().inflate(R.layout.login_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);
        subscriptions = new CompositeSubscription();

        Button mLoginButton = (Button) findViewById(R.id.login_ac_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("View.OnClickListener().onClick()");
                doLoginValidate();
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        Timber.d("onDestroy()");
        super.onDestroy();
        if (subscriptions != null && subscriptions.hasSubscriptions()) {
            subscriptions.unsubscribe();
        }
    }

    // ============================================================
    //    ..... VIEW IMPLEMENTATION ....
    // ============================================================

    @Override
    public void showDialog(String userName)
    {
        Timber.d("showDialog()");
        DialogFragment newFragment = newInstance(userName);
        newFragment.show(getFragmentManager(), "passwordMailDialog");
    }

    // ============================================================
    //    ..... CONTROLLER IMPLEMENTATION ....
    /* ============================================================*/

    @Override
    public int getCounterWrong()
    {
        Timber.d("getCounterWrong()");
        return counterWrong;
    }

    @Override
    public void setCounterWrong(int counterWrong)
    {
        Timber.d("setCounterWrong()");
        this.counterWrong = counterWrong;
    }

    @Override
    public void doLoginValidate()
    {
        Timber.i("doLoginValidate()");

        UsuarioBean usuarioBean = new UsuarioBean(
                ((EditText) mAcView.findViewById(R.id.reg_usuario_email_editT)).getText().toString(),
                null,
                ((EditText) mAcView.findViewById(R.id.reg_usuario_password_ediT)).getText().toString(),
                null
        );

        StringBuilder errorBuilder = getErrorMsgBuilder(this);
        if (!usuarioBean.validateLoginData(getResources(), errorBuilder)) {
            makeToast(this, errorBuilder.toString(), R.color.deep_purple_100);
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            makeToast(this, R.string.no_internet_conn_toast);
        } else {
            subscriptions.add(
                    LoginAcObservable.getZipLoginSingle(usuarioBean.getUsuario())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new LoginAcObservable.LoginValidateSubscriber(this, usuarioBean.getUsuario()))
            );
        }
    }

    @Override
    public void doDialogPositiveClick(String email)
    {
        Timber.d("doDialogPositiveClick()");
        subscriptions.add(
                LoginAcObservable.getLoginMailSingle(email)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new LoginAcObservable.LoginMailSubscriber(this))
        );
    }

    @Override
    public void doDialogNegativeClick()
    {
        Timber.d("doDialogNegativeClick()");

        Intent intent = new Intent(this, defaultActivityClassToGo);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    //  =====================================================================================================
    //    .................................... INNER CLASSES .................................
    //  =====================================================================================================

    // ............................... DIALOG ................................

    public static class PasswordMailDialog extends DialogFragment {

        public static PasswordMailDialog newInstance(String emailUser)
        {
            Timber.d("newInstance()");

            PasswordMailDialog mailDialog = new PasswordMailDialog();
            Bundle args = new Bundle();
            args.putString(EMAIL_DIALOG_ARG, emailUser);
            mailDialog.setArguments(args);
            return mailDialog;
        }

        @Override
        public AppCompatDialog onCreateDialog(Bundle savedInstanceState)
        {
            Timber.d("onCreateDialog()");

            int message = R.string.send_password_by_mail_dialog;
            Builder builder = new Builder(getActivity(), ((LoginAc) getActivity()).getDialogThemeId());

            builder.setMessage(message)
                    .setPositiveButton(R.string.send_password_by_mail_YES, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            dismiss();
                            ((LoginAc) getActivity()).doDialogPositiveClick(getArguments().getString("email"));
                        }
                    })
                    .setNegativeButton(R.string.send_password_by_mail_NO, new DialogInterface.OnClickListener() {
                        @SuppressWarnings("unchecked")
                        public void onClick(DialogInterface dialog, int id)
                        {
                            dismiss();
                            makeToast(getActivity(), R.string.login_wrong_no_mail);
                            ((LoginAc) getActivity()).doDialogNegativeClick();
                        }
                    });
            return builder.create();
        }
    }
}