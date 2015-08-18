package com.didekindroid.usuario.comunidad;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import com.didekindroid.R;
import com.didekindroid.usuario.comunidad.dominio.Usuario;
import com.didekindroid.usuario.comunidad.dominio.UsuarioComunidad;

import java.util.List;

import static com.didekindroid.usuario.common.UserIntentExtras.USUARIO_COMUNIDAD_REG;
import static com.didekindroid.common.ui.ViewsIDs.COMUNIDADES_USER;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;

public class ComusByUserListAc extends ListActivity {

    private static final String TAG = ComusByUserListAc.class.getCanonicalName();
    private ComusByUserListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        UsuarioComunidad usuarioComunidad = (UsuarioComunidad) getIntent().getSerializableExtra(USUARIO_COMUNIDAD_REG
                .toString());
        new RegComuAndUserComuHttp().execute(usuarioComunidad);

        final ListView listView = getListView();
        //listView.setEmptyView();   Asumimos que, al ser un usuario registrado, tiene comuniades asociadas.
        //noinspection ResourceType . Asignamos un id arbitrario para facilitar los tests.
        listView.setId(COMUNIDADES_USER.idView);
        /* listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.d(TAG, "onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.comunidades_usuario_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.d(TAG, "onOptionsItemSelected()");
        return super.onOptionsItemSelected(item);
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    private class RegComuAndUserComuHttp extends AsyncTask<UsuarioComunidad, Void, List<UsuarioComunidad>> {

        @Override
        protected List<UsuarioComunidad> doInBackground(UsuarioComunidad... usuarioComunidad)
        {
            Log.d(TAG, "RegComuAndUserComuHttp.doInBackground()");
            List<UsuarioComunidad> usuarioComunidades;

            // El usuario está registrando una nueva comunidad.
            if (usuarioComunidad[0] != null) {
                Usuario usuarioDb = ServOne.insertUserOldComunidadNew(usuarioComunidad[0]);
                usuarioComunidades = usuarioDb.getUsuariosComunidad();
            } else {
                usuarioComunidades = ServOne.getUsuariosComunidad();
            }
            return usuarioComunidades;
        }

        @Override
        protected void onPostExecute(List<UsuarioComunidad> usuarioComunidades)
        {
            Log.d(TAG, "RegComuAndUserComuHttp.onPostExecute()");

            listAdapter = new ComusByUserListAdapter(ComusByUserListAc.this);
            listAdapter.addAll(usuarioComunidades);
            setListAdapter(listAdapter);
        }
    }
}
