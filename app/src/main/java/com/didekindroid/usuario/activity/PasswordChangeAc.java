package com.didekindroid.usuario.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.common.utils.ConnectionUtils;
import com.didekindroid.usuario.dominio.UsuarioBean;

import timber.log.Timber;

import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.common.utils.UIutils.getErrorMsgBuilder;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.common.utils.UIutils.makeToast;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static com.google.common.base.Preconditions.checkState;

/**
 * Preconditions:
 * 1. Registered user.
 * Postconditions:
 * 1. Password changed and tokenCache updated.
 * 2. It goes to UserDataAc activity.
 */
@SuppressWarnings("ConstantConditions")
public class PasswordChangeAc extends AppCompatActivity {

    private View mAcView;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate()");

        // Preconditions.
        checkState(isRegisteredUser(this));

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

    void modifyPassword()
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
            makeToast(this, errorBuilder.toString(), Toast.LENGTH_SHORT);
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            makeToast(this, R.string.no_internet_conn_toast, Toast.LENGTH_LONG);
        } else {
            new PasswordModifyer().execute(usuarioBean.getPassword());
        }
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    class PasswordModifyer extends AsyncTask<String, Void, Integer> {

        UiException uiException;

        @Override
        protected Integer doInBackground(String... params)
        {
            Timber.d("doInBackground()");
            int passwordChange = 0;
            try {
                passwordChange = ServOne.passwordChange(params[0]);
            } catch (UiException e) {
                uiException = e;
            }
            return passwordChange;
        }

        @Override
        protected void onPostExecute(Integer passwordUpdate)
        {
            Timber.d("onPostExecute(): DONE");
            if (uiException != null) {
                uiException.processMe(PasswordChangeAc.this, new Intent());
            } else {
                checkState(passwordUpdate == 1);
                Intent intent = new Intent(PasswordChangeAc.this, UserDataAc.class);
                startActivity(intent);
            }
        }
    }
}
