package com.didekindroid.usuario.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.didekindroid.R;
import com.didekindroid.common.ui.UIutils;
import com.didekindroid.usuario.common.UserMenu;
import com.didekindroid.usuario.dominio.Comunidad;

import java.util.List;

import static com.didekindroid.common.ui.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.common.UserIntentExtras.COMUNIDAD_LIST_INDEX;
import static com.didekindroid.usuario.common.UserIntentExtras.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.usuario.common.UserMenu.*;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static com.google.common.base.Preconditions.checkNotNull;

public class ComuSearchResultsAc extends Activity implements ComuListFr.ComuListListener {

    private static final String TAG = ComuSearchResultsAc.class.getCanonicalName();

    // The fragment where the summary data are displayed.
    ComuListFr mComunidadesSummaryFrg;

    // The comunidad index currently being displayed.
    int mIndex;

    @Nullable
    List<Comunidad> mUsuarioComunidades;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate().");
        super.onCreate(savedInstanceState);

        if (isRegisteredUser(this)) {
            new ComunidadesUsuarioGetter().execute();
        }

        setContentView(R.layout.comu_search_results_layout);

        // Find our fragments.
        mComunidadesSummaryFrg = (ComuListFr) getFragmentManager()
                .findFragmentById(R.id.comu_list_frg);
    }

    @Override
    protected void onStart()
    {
        Log.d(TAG, "onStart()");
        super.onStart();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState)
    {
        Log.d(TAG, "onSaveInstanceState()");
        savedInstanceState.putInt(COMUNIDAD_LIST_INDEX.name(), mIndex);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Log.d(TAG, "onRestoreInstanceState()");
        if (savedInstanceState != null) {
            mIndex = savedInstanceState.getInt(COMUNIDAD_LIST_INDEX.name(), 0);
            mComunidadesSummaryFrg.setSelection(mIndex); // Only for linearFragments.
        }
    }

    // ============================================================
    //    ..... ACTION BAR ....
    // ============================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.d(TAG, "onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.comu_search_results_ac_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.d(TAG, "onOptionsItemSelected()");

        int resourceId = checkNotNull(item.getItemId());

        switch (resourceId) {
            case R.id.user_data_ac_mn:
                USER_DATA_AC.doMenuItem(this);
                return true;
            case R.id.comu_by_user_list_ac_mn:
                COMU_BY_USER_LIST_AC.doMenuItem(this);
                return true;
            case R.id.reg_comu_user_usercomu_ac_mn:
                REG_COMU_USER_USERCOMU_AC.doMenuItem(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //  .... HELPER INTERFACES AND CLASSES ....

    @Override
    public void onComunidadSelected(Comunidad comunidad, int lineItemIndex)
    {
        Log.d(TAG, "onComunidadSelected().");
        mIndex = lineItemIndex;

        if (!isRegisteredUser(this)) {
            Log.d(TAG, "onComunidadSelected(). User is not registered.");
            Intent intent = new Intent(this, RegUserAndUserComuAc.class);
            intent.putExtra(COMUNIDAD_LIST_OBJECT.extra, comunidad);
            startActivity(intent);
        } else {
            if (mUsuarioComunidades.contains(comunidad)) {
                Log.d(TAG, "onComunidadSelected(). User is registered and associated to the comunidad.");
                Intent intent = new Intent(this, SeeComuAndUserComuAc.class);
                intent.putExtra(COMUNIDAD_LIST_OBJECT.extra, comunidad);
                startActivity(intent);
            } else {
                Log.d(TAG, "onComunidadSelected(). User is registered and not associated to the comunidad.");
                Intent intent = new Intent(this, RegUserComuAc.class);
                intent.putExtra(COMUNIDAD_LIST_OBJECT.extra, comunidad);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onComunidadListLoaded(int listSize)
    {
        if (listSize == 0) {
            UIutils.makeToast(this, R.string.no_result_search_comunidad);
            UserMenu.REG_COMU_USER_USERCOMU_AC.doMenuItem(this);
        }
    }

//    ============================================================
//    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
//    ============================================================

    private class ComunidadesUsuarioGetter extends AsyncTask<Void, Void, List<Comunidad>> {

        @Override
        @Nullable
        protected List<Comunidad> doInBackground(Void... params)
        {
            Log.d(ComuSearchResultsAc.TAG, ".ComunidadesUsuarioGetter.doInBackground()");
            return ServOne.getComunidadesByUser();
        }

        @Override
        protected void onPostExecute(List<Comunidad> comunidades)
        {
            Log.d(TAG, ".ComunidadesUsuarioGetter.onPostExecute(), size comunidades = " +
                    (comunidades != null ? comunidades.size() : 0));
            mUsuarioComunidades = comunidades;
        }
    }

    List<Comunidad> getmUsuarioComunidades()
    {
        Log.d(TAG, "getmUsuarioComunidades()");
        return mUsuarioComunidades;
    }

}