package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;

import timber.log.Timber;

import static com.didekindroid.common.activity.TokenHandler.TKhandler;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.common.utils.UIutils.updateIsRegistered;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static com.google.common.base.Preconditions.checkState;

/**
 * Preconditions:
 * 1. Registered user.
 * Postconditions:
 * 1. Unregistered user, if she chooses so. ComuSearchAc is to be showed.
 */
@SuppressWarnings("ConstantConditions")
public class DeleteMeAc extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate()");

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
                Timber.d("mUnregisterButton.OnClickListener().onClick()");
                unregisterUser();
            }
        });
    }

    private void unregisterUser()
    {
        Timber.d("unregisterUser()");
        new UserDataEraser().execute();
        Intent intent = new Intent(this, ComuSearchAc.class);
        startActivity(intent);
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    class UserDataEraser extends AsyncTask<Void, Void, Boolean> {

        UiException uiException;

        @Override
        protected Boolean doInBackground(Void... params)
        {
            Timber.d("doInBackground()");

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
            Timber.d("onPostExecute()");

            if (uiException != null) {
                uiException.processMe(DeleteMeAc.this, new Intent());
            } else {
                checkState(isDeleted);
            }
        }
    }
}
