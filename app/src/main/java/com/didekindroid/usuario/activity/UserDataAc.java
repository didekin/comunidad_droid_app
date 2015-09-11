package com.didekindroid.usuario.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.didekin.serviceone.domain.Usuario;
import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.R;
import com.google.common.base.Preconditions;

import static com.didekindroid.usuario.activity.utils.UserIntentExtras.USUARIO_COMUNIDAD_REG;
import static com.didekindroid.usuario.security.TokenHandler.TKhandler;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;

public class UserDataAc extends Activity {

    private static final String TAG = UserDataAc.class.getCanonicalName();

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        new UserDataGetter().execute();
        setContentView(R.layout.reg_usuario);
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

        switch (item.getItemId()) {
            /*case R.id.recordar_contraseña_mn:
                //TODO: implementar.
                return true;
            case R.id.nueva_contraseña_mn:
                //TODO: implementar.
                return true;*/
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
            Log.d(TAG, "RegComuAndUserComuHttp.doInBackground()");

            Usuario usuarioBack = ServOne.getUserData();
            return usuarioBack;
        }

        @Override
        protected void onPostExecute(Usuario usuario)
        {
            Log.d(TAG, "RegComuAndUserComuHttp.onPostExecute()");
            //TODO: pinto los datos del usuario.
        }
    }
}
