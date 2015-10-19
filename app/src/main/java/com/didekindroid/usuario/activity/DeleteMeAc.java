package com.didekindroid.usuario.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.didekindroid.R;

import static com.didekindroid.uiutils.UIutils.isRegisteredUser;
import static com.didekindroid.uiutils.UIutils.updateIsRegistered;
import static com.didekindroid.security.TokenHandler.TKhandler;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static com.google.common.base.Preconditions.checkState;

/**
 * Preconditions:
 * 1. Registered user.
 * Postconditions:
 * 1. Unregistered user, if she chooses so. ComuSearchAc is to be showed.
 */
public class DeleteMeAc extends Activity {

    private static final String TAG = DeleteMeAc.class.getCanonicalName();

    private View mAcView;
    private Button mUnregisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        // Preconditions.
        checkState(isRegisteredUser(this));

        mAcView = getLayoutInflater().inflate(R.layout.delete_me_ac, null);
        setContentView(mAcView);

        mUnregisterButton = (Button) findViewById(R.id.delete_me_ac_unreg_button);
        mUnregisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "mUnregisterButton.OnClickListener().onClick()");
                unregisterUser();
            }
        });
    }

    private void unregisterUser()
    {
        Log.d(TAG, "unregisterUser()");
        new UserDataEraser().execute();
        Intent intent = new Intent(this, ComuSearchAc.class);
        startActivity(intent);
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    private class UserDataEraser extends AsyncTask<Void, Void, Boolean> {

        final String TAG = UserDataEraser.class.getCanonicalName();

        @Override
        protected Boolean doInBackground(Void... params)
        {
            Log.d(TAG, "doInBackground()");
            boolean isDeleted = ServOne.deleteUser();
            TKhandler.cleanCacheAndBckFile();
            updateIsRegistered(false, DeleteMeAc.this);
            return isDeleted;
        }

        @Override
        protected void onPostExecute(Boolean isDeleted)
        {
            Log.d(TAG, "onPostExecute()");
            checkState(isDeleted);
        }
    }
}
