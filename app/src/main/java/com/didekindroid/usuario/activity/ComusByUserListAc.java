package com.didekindroid.usuario.activity;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.R;

import java.util.List;

import static com.didekindroid.uiutils.ViewsIDs.SEE_COMU_AND_USER_COMU_BY_USER;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.USERCOMU_LIST_OBJECT;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;

public class ComusByUserListAc extends ListActivity {

    private static final String TAG = ComusByUserListAc.class.getCanonicalName();
    private ComuAndUserComuListByUserAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        UsuarioComunidad usuarioComunidad = (UsuarioComunidad) getIntent().getSerializableExtra(USERCOMU_LIST_OBJECT
                .toString());
        new RegComuAndUserComuHttp().execute(usuarioComunidad);

        final ListView listView = getListView();
        //listView.setEmptyView();   Asumimos que, al ser un usuario registrado, tiene comuniades asociadas.
        //noinspection ResourceType . Asignamos un id arbitrario para facilitar los tests.
        listView.setId(SEE_COMU_AND_USER_COMU_BY_USER.idView);
        /* listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.d(TAG, "onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.see_usercomu_by_user_ac_menu, menu);
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
                ServOne.regComuAndUserComu(usuarioComunidad[0]);
            }
            usuarioComunidades = ServOne.getUserComusByUser();
            return usuarioComunidades;
        }

        @Override
        protected void onPostExecute(List<UsuarioComunidad> usuarioComunidades)
        {
            Log.d(TAG, "RegComuAndUserComuHttp.onPostExecute()");

            listAdapter = new ComuAndUserComuListByUserAdapter(ComusByUserListAc.this);
            listAdapter.addAll(usuarioComunidades);
            setListAdapter(listAdapter);
        }
    }
}
