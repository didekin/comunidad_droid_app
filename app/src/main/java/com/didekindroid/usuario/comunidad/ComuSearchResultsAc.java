package com.didekindroid.usuario.comunidad;

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
import com.didekindroid.usuario.comunidad.dominio.ComunidadBean;
import com.didekindroid.usuario.comunidad.dominio.UsuarioComunidad;

import java.util.List;

import static com.didekindroid.common.ui.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.common.UserIntentExtras.COMUNIDAD_BEAN_LIST;
import static com.didekindroid.usuario.common.UserIntentExtras.COMUNIDAD_INDEX_LIST;
import static com.didekindroid.usuario.common.UserMenu.*;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class ComuSearchResultsAc extends Activity implements ComuListFr.ComunidadListenerSelected {

    private static final String TAG = ComuSearchResultsAc.class.getCanonicalName();

    // The fragment where the summary data are displayed.
    ComuListFr mComunidadesSummaryFrg;

    // The comunidad index currently being displayed.
    int mIndex;

    @Nullable
    List<UsuarioComunidad> mUsuarioComunidades;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate().");
        super.onCreate(savedInstanceState);

        if (isRegisteredUser(this)){
            new ComunidadesUsuarioGetter().execute();
        }

        setContentView(R.layout.comunidades_see_layout);

        // Find our fragments.
        mComunidadesSummaryFrg = (ComuListFr) getFragmentManager()
                .findFragmentById(R.id.comunidades_summary_frg);
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
        savedInstanceState.putInt(COMUNIDAD_INDEX_LIST.name(), mIndex);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Log.d(TAG, "onRestoreInstanceState()");
        if (savedInstanceState != null) {
            mIndex = savedInstanceState.getInt(COMUNIDAD_INDEX_LIST.name(), 0);
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
    public void onComunidadSelected(ComunidadBean comunidadBean, int lineItemIndex)
    {
        Log.d(TAG, "onComunidadSelected().");
        mIndex = lineItemIndex;

        if (!isRegisteredUser(this)) {
            Log.d(TAG, "onComunidadSelected(). User is not registered.");
            Intent intent = new Intent(this, RegUserAndUserComuAc.class);
            intent.putExtra(COMUNIDAD_BEAN_LIST.extra, comunidadBean);
            startActivity(intent);
        } else {
            if (mUsuarioComunidades.contains(comunidadBean)) {
                Log.d(TAG, "onComunidadSelected(). User is registered and associated to the comunidad.");
                Intent intent = new Intent(this, SeeComuAndUserComuAc.class);
                intent.putExtra(COMUNIDAD_BEAN_LIST.extra, comunidadBean);
                startActivity(intent);
            } else {
                Log.d(TAG, "onComunidadSelected(). User is registered and not associated to the comunidad.");
                // Comunidad data are shown as not modifiable.
                Intent intent = new Intent(this, RegUserComuAc.class);
                intent.putExtra(COMUNIDAD_BEAN_LIST.extra, comunidadBean);
                startActivity(intent);
            }
        }
    }

//    ============================================================
//    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
//    ============================================================

    private class ComunidadesUsuarioGetter extends AsyncTask<Void, Void, List<UsuarioComunidad>> {

        @Override
        @Nullable
        protected List<UsuarioComunidad> doInBackground(Void... params)
        {
            Log.d(ComuSearchResultsAc.TAG, ".ComunidadesUsuarioGetter.doInBackground()");
            return ServOne.getUsuariosComunidad();
        }

        @Override
        protected void onPostExecute(List<UsuarioComunidad> usuarioComunidades)
        {
            Log.d(TAG, ".ComunidadesUsuarioGetter.onPostExecute(), size comunidades = " +
                    (usuarioComunidades != null ? usuarioComunidades.size() : 0));
            mUsuarioComunidades = usuarioComunidades;
        }
    }
}