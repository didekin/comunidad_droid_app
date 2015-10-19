package com.didekindroid.usuario.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.didekin.serviceone.domain.Comunidad;
import com.didekindroid.R;
import com.didekindroid.uiutils.UIutils;
import com.didekindroid.usuario.activity.utils.UserMenu;

import java.util.List;

import static com.didekindroid.uiutils.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.COMUNIDAD_LIST_INDEX;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.usuario.activity.utils.UserMenu.*;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Postconditions:
 * <p/>
 * 1. An object comunidad is passed as an intent extra with the fields:
 * -- comunidadId of the comunidad selected.
 * -- nombreComunidad (with tipoVia,nombreVia, numero and sufijoNumero).
 * -- municipio, with codInProvincia and nombre.
 * -- provincia, with provinciaId and nombre.
 */
public class ComuSearchResultsAc extends Activity implements ComuSearchResultsListFr.ComuListListener {

    private static final String TAG = ComuSearchResultsAc.class.getCanonicalName();

    // The fragment where the summary data are displayed.
    ComuSearchResultsListFr mComunidadesSummaryFrg;

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
        mComunidadesSummaryFrg = (ComuSearchResultsListFr) getFragmentManager()
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
            case R.id.see_usercomu_by_user_ac_mn:
                SEE_USERCOMU_BY_USER_AC.doMenuItem(this);
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
                Intent intent = new Intent(this, SeeUserComuByUserAc.class);
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
        Log.d(TAG, "onComunidadListLoaded. ListSize = " + listSize);
        if (listSize == 0) {
            UIutils.makeToast(this, R.string.no_result_search_comunidad, Toast.LENGTH_LONG);
            UserMenu.REG_COMU_USER_USERCOMU_AC.doMenuItem(this);
        }
    }

//    ============================================================
//    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
//    ============================================================

    /**
     * Task to obtain the comunidades associtated to a user.
     * Those are used when she selects a comunidad: the actions performed by the app are
     * different following the different possibilities.
     */
    private class ComunidadesUsuarioGetter extends AsyncTask<Void, Void, List<Comunidad>> {

        @Override
        @Nullable
        protected List<Comunidad> doInBackground(Void... params)
        {
            Log.d(ComuSearchResultsAc.TAG, ".ComunidadesUsuarioGetter.doInBackground()");
            return ServOne.getComusByUser();
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