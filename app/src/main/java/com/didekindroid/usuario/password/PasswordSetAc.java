package com.didekindroid.usuario.password;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.didekindroid.R;

import timber.log.Timber;

import static com.didekindroid.usuario.password.ViewerPasswordChange.newViewerPswdChange;
import static com.didekindroid.util.UIutils.doToolBar;

/**
 * Preconditions:
 * 1. Registered user.
 * 2. An intent is received with the userName.
 * Postconditions:
 * 1. Password changed and tokenCache updated.
 * 2. It goes to UserDataAc activity.
 */
public class PasswordSetAc extends AppCompatActivity {

    ViewerPasswordSet viewer;
    View acView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        acView = getLayoutInflater().inflate(R.layout.password_change_ac, null);
        setContentView(acView);
        doToolBar(this, true);

        /*viewer = newViewerPswdChange(this);
        viewer.doViewInViewer(savedInstanceState, null);*/
    }

    @Override
    protected void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
        viewer.clearSubscriptions();
    }
}
