package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.didekindroid.R;
import com.didekindroid.common.UiException;

import static com.didekindroid.common.TokenHandler.TKhandler;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.common.utils.UIutils.updateIsRegistered;
import static com.google.common.base.Preconditions.checkState;

/**
 * Preconditions:
 * 1. Registered user.
 * Postconditions:
 * 1. Unregistered user, if she chooses so. ComuSearchAc is to be showed.
 */
public class DeleteMeAc extends AppCompatActivity {

    private static final String TAG = DeleteMeAc.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        // Preconditions.
        checkState(isRegisteredUser(this));

        View mAcView = getLayoutInflater().inflate(R.layout.delete_me_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);

        Button mUnregisterButton = (Button) findViewById(R.id.delete_me_ac_unreg_button);
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

    class UserDataEraser extends AsyncTask<Void, Void, Boolean> {

        final String TAG = UserDataEraser.class.getCanonicalName();
        UiException uiException;

        @Override
        protected Boolean doInBackground(Void... params)
        {
            Log.d(TAG, "doInBackground()");

            boolean isDeleted = false;
            try {
                isDeleted = ServOne.deleteUser();
                TKhandler.cleanCacheAndBckFile();
                updateIsRegistered(false, DeleteMeAc.this);
            } catch (UiException e) {
                uiException = e;
            }
            return isDeleted;
        }

        @Override
        protected void onPostExecute(Boolean isDeleted)
        {
            Log.d(TAG, "onPostExecute()");

            if (uiException != null) {
                Log.d(TAG, "onPostExecute(): uiException " + (uiException.getInServiceException() != null ?
                        uiException.getInServiceException().getHttpMessage() : "Token null"));
                uiException.getAction().doAction(DeleteMeAc.this, uiException.getResourceId());
            } else {
                checkState(isDeleted);
            }
        }
    }
}
