package com.didekindroid.usuario.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.didekindroid.R;
import com.didekindroid.usuario.dominio.Usuario;
import com.didekindroid.usuario.dominio.UsuarioComunidad;
import com.didekindroid.usuario.dominio.AccessToken;

import static com.didekindroid.usuario.common.UserIntentExtras.USUARIO_COMUNIDAD_REG;
import static com.didekindroid.usuario.common.TokenHandler.TKhandler;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;

public class UserDataAc extends Activity {

    private static final String TAG = UserDataAc.class.getCanonicalName();

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        /* usuarioComunidad == null is the activity is called from the menu option USER_DATA_AC*/
        UsuarioComunidad usuarioComunidad = (UsuarioComunidad) getIntent().getSerializableExtra(USUARIO_COMUNIDAD_REG
                .toString());
        new RegComuAndUserComuAndUserHttp().execute(usuarioComunidad);
        setContentView(R.layout.reg_usuario);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.d(TAG, "onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.usuario_datos_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.d(TAG, "onOptionsItemSelected()");

        switch (item.getItemId()) {
            case R.id.recordar_contraseña_mn:
                //TODO: implementar.
                return true;
            case R.id.nueva_contraseña_mn:
                //TODO: implementar.
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    private class RegComuAndUserComuAndUserHttp extends AsyncTask<UsuarioComunidad, Void, Usuario> {

        UserDataAc mActivity = UserDataAc.this;
        boolean isNewUser = false;

        @Override
        protected Usuario doInBackground(UsuarioComunidad... usuarioComunidad)
        {
            Log.d(TAG, "RegComuAndUserComuHttp.doInBackground()");

            Usuario usuarioBack;

            if (usuarioComunidad[0] == null) { // El usuario está registrado y utilizo su token.
                Log.d(TAG, "RegComuAndUserComuHttp.doInBackground(), usuario registrado.");
                usuarioBack = ServOne.getUserData();
            } else {
                Log.d(TAG, "RegComuAndUserComuHttp.doInBackground(), usuario no registrado signing up.");
                String passwordOrigin = usuarioComunidad[0].getUsuario().getPassword();
                usuarioBack = ServOne.signUp(usuarioComunidad[0]);
                isNewUser = true;
                usuarioBack.setPassword(passwordOrigin); // Password comes back from DB encrypted.
            }
            return usuarioBack;
        }

        @Override
        protected void onPostExecute(Usuario usuario)
        {
            Log.d(TAG, "RegComuAndUserComuHttp.onPostExecute()");
            if (isNewUser){
                new TkCacheActivatorHttp().execute(usuario);
            }

            //TODO: pinto los datos del usuario.
        }
    }

    private class TkCacheActivatorHttp extends AsyncTask<Usuario, Void, Void> {

        UserDataAc mActivity = UserDataAc.this;

        @Override
        protected Void doInBackground(Usuario... params)
        {
            Log.d(TAG, "TkCacheActivatorHttp.doInBackground()");
            String userName = params[0].getUserName();
            String password = params[0].getPassword();
            AccessToken token = ServOne.getPasswordUserToken(userName, password);
            TKhandler.initKeyCacheAndBackupFile(token);
            return null;
        }

        @Override
        protected void onPostExecute(Void nothing)
        {
            Log.d(TAG, "TkCacheActivatorHttp.onPostExecute()");
        }
    }
}
