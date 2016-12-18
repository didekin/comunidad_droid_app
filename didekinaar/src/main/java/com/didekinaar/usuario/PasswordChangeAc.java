package com.didekinaar.usuario;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.didekinaar.R;
import com.didekinaar.exception.UiException;
import com.didekinaar.usuario.userdata.UserDataAc;
import com.didekinaar.utils.ConnectionUtils;

import java.util.Objects;

import timber.log.Timber;

import static com.didekinaar.usuario.UsuarioService.AarUserServ;
import static com.didekinaar.utils.UIutils.checkPostExecute;
import static com.didekinaar.utils.UIutils.doToolBar;
import static com.didekinaar.utils.UIutils.getErrorMsgBuilder;
import static com.didekinaar.utils.UIutils.isRegisteredUser;
import static com.didekinaar.utils.UIutils.makeToast;

/**
 * Preconditions:
 * 1. Registered user.
 * Postconditions:
 * 1. Password changed and tokenCache updated.
 * 2. It goes to UserDataAc activity.
 */
@SuppressWarnings("ConstantConditions")
public class PasswordChangeAc extends AppCompatActivity {

    // TODO: implementación en app, extendiendo esta clase.

    private View mAcView;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate()");

        // Preconditions.
        Objects.equals(isRegisteredUser(this), true);

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
            makeToast(this, errorBuilder.toString(), com.didekinaar.R.color.deep_purple_100);
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            makeToast(this, R.string.no_internet_conn_toast);
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
                passwordChange = AarUserServ.passwordChange(params[0]);
            } catch (UiException e) {
                uiException = e;
            }
            return passwordChange;
        }

        @Override
        protected void onPostExecute(Integer passwordUpdate)
        {
            if (checkPostExecute(PasswordChangeAc.this)) return;

            Timber.d("onPostExecute(): DONE");
            if (uiException != null) {
                // TODO: en todos los 'processMe' de didekinaar hay que verificar que el mensaje en UiException es GENERIC_ERROR.
                uiException.processMe(PasswordChangeAc.this, new Intent());
            } else {
                Objects.equals(passwordUpdate == 1, true);
                Intent intent = new Intent(PasswordChangeAc.this, UserDataAc.class);
                startActivity(intent);
            }
        }
    }
}
