package com.didekindroid.usuario.login;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.UsuarioBean;
import com.didekindroid.util.ConnectionUtils;
import com.didekindroid.util.MenuRouter;

import java.util.Objects;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.didekindroid.usuario.login.LoginAc.PasswordMailDialog.newInstance;
import static com.didekindroid.usuario.login.LoginAcReactor.loginReactor;
import static com.didekindroid.util.DefaultNextAcRouter.routerMap;
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
public class LoginAc extends AppCompatActivity implements LoginViewIf, LoginControllerIf {

    View mAcView;
    CompositeDisposable subscriptions;
    private volatile int counterWrong;
    private UsuarioBean usuarioBean;
    LoginReactorIf reactor;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Timber.i("Entered onCreate()");
        super.onCreate(savedInstanceState);

        mAcView = getLayoutInflater().inflate(R.layout.login_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);
        reactor = loginReactor;
        subscriptions = new CompositeDisposable();

        Button mLoginButton = (Button) findViewById(R.id.login_ac_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("View.OnClickListener().onClick()");
                if (checkLoginData()) {
                   validateLoginRemote() ;
                }
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        Timber.d("onDestroy()");
        super.onDestroy();
        if (subscriptions != null) {
            subscriptions.clear();
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

    @Override
    public String[] getLoginDataFromView(){
        // TODO: test.
        Timber.d("getLoginDataFromView()");
        return new String[]{
                ((EditText) mAcView.findViewById(R.id.reg_usuario_email_editT)).getText().toString(),
                ((EditText) mAcView.findViewById(R.id.reg_usuario_password_ediT)).getText().toString()
        };
    }

    // ============================================================
    //    ..... CONTROLLER IMPLEMENTATION ....
    /* ============================================================*/

    @Override
    public boolean checkLoginData()
    {  // TODO: test.
        Timber.i("checkLoginData()");
        usuarioBean = new UsuarioBean(getLoginDataFromView()[0], null, getLoginDataFromView()[1], null);

        StringBuilder errorBuilder = getErrorMsgBuilder(this);
        if (!usuarioBean.validateLoginData(getResources(), errorBuilder)) {
            makeToast(this, errorBuilder.toString(), R.color.deep_purple_100);
            return false;
        }
        if (!ConnectionUtils.isInternetConnected(this)) {
            makeToast(this, R.string.no_internet_conn_toast);
            return false;
        }
        return true;
    }

    @Override
    public void validateLoginRemote()
    {
        Timber.i("validateLoginRemote()");
        Objects.equals(usuarioBean != null, true);
        reactor.validateLogin(this, usuarioBean.getUsuario());
    }

    @Override
    public void processBackLoginRemote(Boolean isLoginOk)
    {
        Timber.d("onNext");
        if (isLoginOk) {
            Timber.d("login OK");
            Intent intent = new Intent(this, routerMap.get(this.getClass()));
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else if (counterWrong > 2) { // Password wrong
            ++counterWrong;
            showDialog(usuarioBean.getUserName());
        } else {
            Timber.d("Password wrong, counterWrong = %d%n",counterWrong);
            makeToast(this, R.string.password_wrong_in_login);
        }
    }

    @Override
    public void processBackSendPassword(Boolean isSendPassword)
    {
        Timber.d("processBackSendPassword()");
        if (isSendPassword) {
            makeToast(this, R.string.password_new_in_login);
            recreate();
        }
    }

    @Override
    public void processBackErrorInReactor(Throwable e)
    {
        Timber.d("processBackErrorInReactor()");
        if (e instanceof UiException) {
            ((UiException) e).processMe(this, new Intent());
        }
    }

    @Override
    public void doDialogPositiveClick(String email)
    {
        Timber.d("doDialogPositiveClick()");
        Objects.equals(usuarioBean != null, true);
        reactor.sendPasswordToUser(this, usuarioBean.getUsuario());
    }

    @Override
    public void doDialogNegativeClick()
    {
        Timber.d("doDialogNegativeClick()");

        Intent intent = new Intent(this, routerMap.get(this.getClass()));
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public CompositeDisposable getSubscriptions()
    {
        if (subscriptions == null){
            subscriptions = new CompositeDisposable();
        }
        return subscriptions;
    }

    // ============================================================
    //    ..... ACTION BAR ....
    // ============================================================

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");
        int resourceId = item.getItemId();

        switch (resourceId) {
            case android.R.id.home:
                MenuRouter.doUpMenu(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            Builder builder = new Builder(getActivity(), R.style.alertDialogTheme);

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
