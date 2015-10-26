package com.didekindroid.usuario.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.didekindroid.R;
import com.didekindroid.utils.ConnectionUtils;
import com.didekindroid.usuario.dominio.UsuarioBean;
import com.google.common.base.Preconditions;

import static com.didekindroid.utils.UIutils.*;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static com.google.common.base.Preconditions.checkState;

/**
 * Preconditions:
 * 1. Registered user.
 * Postconditions:
 * 1. Password changed and tokenCache updated.
 * 2. It goes to UserDataAc activity.
 */
public class PasswordChangeAc extends AppCompatActivity {

    private static final String TAG = PasswordChangeAc.class.getCanonicalName();

    private View mAcView;
    private Button mModifyButton;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        // Preconditions.
        checkState(isRegisteredUser(this));

        mAcView = getLayoutInflater().inflate(R.layout.password_change_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);

        mModifyButton = (Button) findViewById(R.id.password_change_ac_button);
        mModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "mModifyButton.OnClickListener().onClick()");
                modifyPassword();
            }
        });
    }

    private void modifyPassword()
    {
        Log.d(TAG, "modifyPassword()");

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
            makeToast(this, errorBuilder.toString());
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            makeToast(this, R.string.no_internet_conn_toast, Toast.LENGTH_LONG);
        } else {
            new PasswordModifyer().execute(usuarioBean.getPassword());
        }

        Intent intent = new Intent(this, UserDataAc.class);
        startActivity(intent);
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    class PasswordModifyer extends AsyncTask<String, Void, Integer> {

        private final String TAG = PasswordModifyer.class.getCanonicalName();

        @Override
        protected Integer doInBackground(String... params)
        {
            Log.d(TAG, "doInBackground()");
            return ServOne.passwordChange(params[0]);
        }

        @Override
        protected void onPostExecute(Integer passwordUpdate)
        {
            Log.d(TAG, "onPostExecute(): DONE");
            Preconditions.checkState(passwordUpdate == 1);
        }
    }
}
