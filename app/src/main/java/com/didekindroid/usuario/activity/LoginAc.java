package com.didekindroid.usuario.activity;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.didekin.common.exception.ErrorBean;
import com.didekin.oauth2.SpringOauthToken;
import com.didekin.usuario.dominio.Usuario;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.common.utils.ConnectionUtils;
import com.didekindroid.usuario.dominio.UsuarioBean;

import java.io.IOException;

import timber.log.Timber;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;
import static com.didekindroid.common.activity.TokenHandler.TKhandler;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.common.utils.UIutils.getErrorMsgBuilder;
import static com.didekindroid.common.utils.UIutils.makeToast;
import static com.didekindroid.common.utils.UIutils.updateIsRegistered;
import static com.didekindroid.common.webservices.Oauth2Service.Oauth2;
import static com.didekindroid.usuario.activity.utils.UserMenu.doUpMenuClearSingleTop;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;

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
@SuppressWarnings("ConstantConditions")
public class LoginAc extends AppCompatActivity {

    private View mAcView;
    short counterWrong;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Timber.i("Entered onCreate()");
        super.onCreate(savedInstanceState);

        mAcView = getLayoutInflater().inflate(R.layout.login_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);

        Button mLoginButton = (Button) findViewById(R.id.login_ac_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("View.OnClickListener().onClick()");
                doLogin();
            }
        });
    }

    @Override
    protected void onPause()
    {
        Timber.i("Entered onPause()");
        super.onPause();
    }

    void doLogin()
    {
        Timber.i("doLogin()");

        UsuarioBean usuarioBean = new UsuarioBean(
                ((EditText) mAcView.findViewById(R.id.reg_usuario_email_editT)).getText().toString(),
                null,
                ((EditText) mAcView.findViewById(R.id.reg_usuario_password_ediT)).getText().toString(),
                null
        );

        StringBuilder errorBuilder = getErrorMsgBuilder(this);
        if (!usuarioBean.validateLoginData(getResources(), errorBuilder)) {
            makeToast(this, errorBuilder.toString(), Toast.LENGTH_SHORT);
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            makeToast(this, R.string.no_internet_conn_toast, LENGTH_SHORT);
        } else {
            new LoginValidator().execute(usuarioBean.getUsuario());
        }
    }

//    ........................ Helper methods in the activity for the dialog .............................

    void showDialog(String userName)
    {
        Timber.d("showDialog()");
        DialogFragment newFragment = PasswordMailDialog.newInstance(userName);
        newFragment.show(getFragmentManager(), "passwordMailDialog");
    }

    void doPositiveClick(String email)
    {
        Timber.d("doPositiveClick()");
        new LoginMailSender().execute(email);
    }

    void doNegativeClick()
    {
        Timber.d("doNegativeClick()");

        makeToast(this, R.string.login_wrong_no_mail, LENGTH_SHORT);
        Intent intent = new Intent(this, ComuSearchAc.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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
                doUpMenuClearSingleTop(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    =====================================================================================================
//    .................................... INNER CLASSES .................................
//    =====================================================================================================

    class LoginValidator extends AsyncTask<Usuario, Void, Boolean> {


        UiException uiException;
        private String userName;
        private String password;


        @Override
        protected Boolean doInBackground(Usuario... usuarios)
        {
            Timber.d("LoginValidator.doInBackground()");

            // Activity field mUserName is initialized. It is reused by LoginMailSender.
            userName = usuarios[0].getUserName();
            password = usuarios[0].getPassword();
            boolean isLoginOk = false;

            try {
                if (ServOne.loginInternal(userName, password)) {
                    SpringOauthToken token = Oauth2.getPasswordUserToken(userName, password);
                    TKhandler.initTokenAndBackupFile(token);
                    updateIsRegistered(true, LoginAc.this);
                    isLoginOk = true;
                }
            } catch (UiException e) {
                uiException = e;
            }
            return isLoginOk;
        }

        @Override
        protected void onPostExecute(Boolean isOk)
        {
            Timber.d("LoginValidator.onPostExecute()");
            if (isOk) {
                Timber.d("LoginValidator.onPostExecute(): login OK");
                Intent intent = new Intent(LoginAc.this, ComuSearchAc.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                LoginAc.this.finish();
            } else if (uiException != null) {  // userName no existe en BD. Action: LOGIN.
                Timber.d("LoginValidator.onPostExecute(): UiException");
                uiException.processMe(LoginAc.this, new Intent());
            } else if (++counterWrong > 3) { // Password wrong
                showDialog(userName);
            } else {
                Timber.d("LoginValidator.onPostExecute(): password wrong, counterWrong = %d%n", counterWrong);
                makeToast(LoginAc.this, R.string.password_wrong_in_login, Toast.LENGTH_SHORT);
            }
        }
    }


//  ======================================================================================================

    public static class PasswordMailDialog extends DialogFragment {

        public static PasswordMailDialog newInstance(String emailUser)
        {
            Timber.d("newInstance()");

            PasswordMailDialog mailDialog = new PasswordMailDialog();
            Bundle args = new Bundle();
            args.putString("email", emailUser);
            mailDialog.setArguments(args);
            return mailDialog;
        }

        @Override
        public AppCompatDialog onCreateDialog(Bundle savedInstanceState)
        {
            Timber.d("onCreateDialog()");

            int message = R.string.send_password_by_mail_dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.alertDialogTheme);

            builder.setMessage(message)
                    .setPositiveButton(R.string.send_password_by_mail_YES, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            dismiss();
                            ((LoginAc) getActivity()).doPositiveClick(getArguments().getString("email"));
                        }
                    })
                    .setNegativeButton(R.string.send_password_by_mail_NO, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            dismiss();
                            ((LoginAc) getActivity()).doNegativeClick();
                        }
                    });
            return builder.create();
        }
    }

//  ======================================================================================================

    class LoginMailSender extends AsyncTask<String, Void, Boolean> {

        private UiException uiException;

        @Override
        protected Boolean doInBackground(String... emails)
        {
            Timber.d("LoginMailSender.doInBackground()");
            boolean isPasswordSend = false;
            try {
                isPasswordSend = ServOne.passwordSend(emails[0]).execute().body();
            } catch (IOException e) {
                uiException = new UiException(ErrorBean.GENERIC_ERROR);
            }
            return isPasswordSend;
        }

        @Override
        protected void onPostExecute(Boolean isOk)
        {
            Timber.d("LoginMailSender.onPostExecute()");

            if (uiException != null) {
                uiException.processMe(LoginAc.this, new Intent());
                return;
            }
            if (isOk) {
                makeToast(LoginAc.this, R.string.password_new_in_login, LENGTH_LONG);
                LoginAc.this.recreate();
            }
        }
    }
}
