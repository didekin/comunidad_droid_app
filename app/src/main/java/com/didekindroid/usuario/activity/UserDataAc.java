package com.didekindroid.usuario.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.didekin.serviceone.domain.Usuario;
import com.didekindroid.R;

import static com.didekindroid.uiutils.UIutils.isRegisteredUser;
import static com.didekindroid.uiutils.UIutils.updateIsRegistered;
import static com.didekindroid.usuario.activity.utils.UserMenu.COMU_SEARCH_AC;
import static com.didekindroid.usuario.activity.utils.UserMenu.SEE_USERCOMU_BY_USER_AC;
import static com.didekindroid.usuario.security.TokenHandler.TKhandler;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Preconditions:
 * 1. Registered user.
 * Postconditions:
 * 1a. Unregistered user, if she chooses so. ComuSearchAc is to be showed.
 * 1b. Regitered user with modified data. Once done, it goes to SeeUserComuByUserAc.
 */
public class UserDataAc extends Activity {

    private static final String TAG = UserDataAc.class.getCanonicalName();

    RegUserFr mRegUserFr;
    private Button mModifyButton;
    private Button mUnregisterButton;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        // Preconditions.
        checkState(isRegisteredUser(this));
        new UserDataGetter().execute();
        setContentView(R.layout.user_data_ac);
        mRegUserFr = (RegUserFr) getFragmentManager().findFragmentById(R.id.reg_user_frg);

        mModifyButton = (Button) findViewById(R.id.user_data_modif_button);
        mModifyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "mModifyButton.OnClickListener().onClick()");
                modifyUserData();
            }
        });

        mUnregisterButton = (Button) findViewById(R.id.user_data_unreg_button);
        mUnregisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "mUnregisterButton.OnClickListener().onClick()");
                unregisterUser();
            }
        });
    }

    private void modifyUserData()
    {
        // TODO: send an email with a number, once the user hass pressed Modify,
        // and show in the activity an EditField to introduce it.
        // Only for changes of password.

        Log.d(TAG, "modifyUserData()");
        Usuario usuario = null;
        new UserDataModifyer().execute(usuario);
        Intent intent = new Intent(this, SeeUserComuByComuAc.class);
        startActivity(intent);
    }

    private void unregisterUser()
    {
        Log.d(TAG, "unregisterUser()");
        new UserDataEraser().execute();
        Intent intent = new Intent(this, ComuSearchAc.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.d(TAG, "onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.user_data_ac_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.d(TAG, "onOptionsItemSelected()");

        int resourceId = checkNotNull(item.getItemId());

        switch (resourceId) {
            case R.id.see_usercomu_by_user_ac_mn:
                SEE_USERCOMU_BY_USER_AC.doMenuItem(this);
                return true;
            case R.id.comu_search_ac_mn:
                COMU_SEARCH_AC.doMenuItem(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    private class UserDataGetter extends AsyncTask<Void, Void, Usuario> {

        @Override
        protected Usuario doInBackground(Void... aVoid)
        {
            Log.d(TAG, "UserDataGetter.doInBackground()");

            Usuario usuarioBack = ServOne.getUserData();
            return usuarioBack;
        }

        @Override
        protected void onPostExecute(Usuario usuario)
        {
            Log.d(TAG, "UserDataGetter.onPostExecute()");
            //TODO: pinto los datos del usuario.
        }
    }

    private class UserDataModifyer extends AsyncTask<Usuario, Void, Integer> {

        final String TAG = UserDataModifyer.class.getCanonicalName();

        @Override
        protected Integer doInBackground(Usuario... usuarios)
        {
            Log.d(TAG, "doInBackground()");
            return ServOne.modifyUser(usuarios[0]);
        }

        @Override
        protected void onPostExecute(Integer rowsUpdated)
        {
            Log.d(TAG, "onPostExecute()");
            checkState(rowsUpdated > 0);
        }
    }

    private class UserDataEraser extends AsyncTask<Void, Void, Boolean> {

        final String TAG = UserDataEraser.class.getCanonicalName();

        @Override
        protected Boolean doInBackground(Void... params)
        {
            Log.d(TAG, "doInBackground()");
            boolean isDeleted = ServOne.deleteUser();
            TKhandler.cleanCacheAndBckFile();
            updateIsRegistered(false, UserDataAc.this);
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
