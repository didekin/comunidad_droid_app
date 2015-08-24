package com.didekindroid.usuario.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.didekindroid.R;

/**
 * User: pedro
 * Date: 15/12/14
 * Time: 10:04
 */
public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";

    public static final String EMAIL_PREF = "email";
    public static final String PASSWORD_PREF = "password";
    private static final String HAS_LOGIN_DATA_PREF = "loginData";
    private TextView textLogin;
    private EditText mEmail;
    private EditText mPassword;
    private Button mLoginButton;
    private SharedPreferences sharedPref;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.i(TAG, "Entered onCreate()");
        super.onCreate(savedInstanceState);
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        initializeView();
    }

    @Override
    protected void onPause()
    {
        Log.i(TAG, "Entered onPause()");
        super.onPause();
    }

    private void initializeView()
    {
        Log.i(TAG, "Entered doLogin()");

        setContentView(R.layout.login_activity);

        textLogin = (TextView) findViewById(R.id.textLogin);

        if (sharedPref.getBoolean(HAS_LOGIN_DATA_PREF, false)) {
            textLogin.setText(R.string.textLogin_yes);
        } else {
            textLogin.setText(R.string.textLogin_no);
        }

        mEmail = (EditText) findViewById(R.id.mailAddress);
        mPassword = (EditText) findViewById(R.id.password);
        mLoginButton = (Button) findViewById(R.id.loginButton);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                doLogin();
            }
        });
    }

    private void doLogin()
    {
        Log.i(TAG, "Entered doLogin()");
        SharedPreferences.Editor editor = sharedPref.edit();
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        String emailWrong = "";
        String passwordWrong = "";

        boolean isValid = true;

        if (email.isEmpty() || !email.contains("@")) {
            emailWrong = "Invalid email address.\n";
            isValid = false;
        }
        if (password.isEmpty() || password.length() > 25) {
            passwordWrong = "Invalid password: number of characteres must be" +
                    " in the range 1-25.\n";
        }

        if (!isValid) {
            Toast clickToast = new Toast(this)
                    .makeText(
                            this,
                            new StringBuilder().append(emailWrong).append(passwordWrong),
                            Toast.LENGTH_LONG);

            clickToast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
            clickToast.show();
        } else if (sharedPref.getBoolean(HAS_LOGIN_DATA_PREF, false)) {
            Log.i(TAG, "Entered doLogin() with login data.");
            if (email.contentEquals(sharedPref.getString(EMAIL_PREF, ""))
                    && password.contentEquals(sharedPref.getString(PASSWORD_PREF, ""))) {
                goToInvoiceActivity();
                finish();
            } else {
                Toast clickToast = new Toast(this).makeText(
                        this,
                        new StringBuilder("Invalid email or password: authentication error"),
                        Toast.LENGTH_LONG);
                clickToast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                clickToast.show();
            }
        } else {
            editor.putString(EMAIL_PREF, mEmail.getText().toString());
            editor.putString(PASSWORD_PREF, mPassword.getText().toString());
            editor.putBoolean(HAS_LOGIN_DATA_PREF, true);
            editor.apply();
            goToInvoiceActivity();
            finish();
        }
    }

    private void goToInvoiceActivity()
    {
        Log.i(TAG, "Entered goToInvoiceActivity()");

        /*Intent invoiceIntent = new Intent(this, SearchActivity.class);
        invoiceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        invoiceIntent.setAction(Intent.ACTION_VIEW);
        startActivity(invoiceIntent);*/
        finish();
    }
}

/* Cada vez que haga login (introduzca password) cambiamos su refresh token en el servidor.*/
/*Intent.FLAG_ACTIVITY_NEW_TASK*/