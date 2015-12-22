package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.didekin.serviceone.domain.Comunidad;
import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.UiException;
import com.didekindroid.usuario.activity.utils.UserMenu;
import com.didekindroid.common.utils.UIutils;
import com.didekindroid.usuario.dominio.FullComunidadIntent;
import com.didekindroid.usuario.dominio.FullUsuarioComuidadIntent;

import static com.didekindroid.common.utils.AppKeysForBundle.COMUNIDAD_LIST_INDEX;
import static com.didekindroid.common.utils.AppKeysForBundle.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.common.utils.AppKeysForBundle.USERCOMU_LIST_OBJECT;
import static com.didekindroid.usuario.activity.utils.UserMenu.REG_COMU_USER_USERCOMU_AC;
import static com.didekindroid.usuario.activity.utils.UserMenu.SEE_USERCOMU_BY_USER_AC;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Postconditions:
 * <p/>
 * 1. If the user is not registered, an object comunidad is passed as an intent extra with the fields:
 * -- comunidadId of the comunidad selected.
 * -- nombreComunidad (with tipoVia,nombreVia, numero and sufijoNumero).
 * -- municipio, with codInProvincia and nombre.
 * -- provincia, with provinciaId and nombre.
 * 2. If the user is registered but not with the comunidad selected, an object comunidad is passed
 * as an intent extra.
 * 3. If the user is registered with the comunidad selected, an object usuarioComunidad is passed
 * with its data fully initialized:
 * -- usuario: id, alias, userName.
 * -- comunidad: id, tipoVia, nombreVia, numero, sufijoNumero, fechaAlta,
 * ---- municipio: codInProvincia, nombre.
 * ------ provincia: provinciaId, nombre.
 * -- usuarioComunidad: portal, escalera, planta, puerta, roles.
 */
public class ComuSearchResultsAc extends AppCompatActivity implements
        ComuSearchResultsListFr.ComuListListener {

    private static final String TAG = ComuSearchResultsAc.class.getCanonicalName();

    // The fragment where the summary data are displayed.
    ComuSearchResultsListFr mComunidadesSummaryFrg;

    // The comunidad index currently being displayed.
    int mIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate().");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.comu_search_results_layout);
        doToolBar(this, true);

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
            intent.putExtra(COMUNIDAD_LIST_OBJECT.extra, new FullComunidadIntent(comunidad));
            startActivity(intent);
        } else {
            new UsuarioComunidadGetter().execute(comunidad);
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

    class UsuarioComunidadGetter extends AsyncTask<Comunidad, Void, UsuarioComunidad> {

        private Comunidad comunidadSelected;
        UiException uiException;

        @Override
        protected UsuarioComunidad doInBackground(Comunidad... comunidades)
        {
            Log.d(ComuSearchResultsAc.TAG, ".UsuarioComunidadGetter.doInBackground()");

            comunidadSelected = comunidades[0];
            UsuarioComunidad userComuByUserAndComu = null;
            try {
                userComuByUserAndComu = ServOne.getUserComuByUserAndComu(comunidadSelected.getC_Id());
            } catch (UiException e) {
                uiException = e;
            }
            return userComuByUserAndComu;
        }

        @Override
        protected void onPostExecute(UsuarioComunidad userComu)
        {
            boolean isUserComuNull = (userComu == null);

            Log.d(TAG, ".UsuarioComunidadGetter.onPostExecute(), isUserComu == null : " +
                    isUserComuNull);

            if (uiException != null) {
                Log.d(TAG, ".UsuarioComunidadGetter.onPostExecute(), uiException " +
                        (uiException.getInServiceException() != null ? uiException.getInServiceException().getHttpMessage() : UiException.TOKEN_NULL));
                uiException.getAction().doAction(ComuSearchResultsAc.this, uiException.getResourceId());
            } else if (isUserComuNull) {
                Intent intent = new Intent(ComuSearchResultsAc.this, RegUserComuAc.class);
                intent.putExtra(COMUNIDAD_LIST_OBJECT.extra, new FullComunidadIntent(comunidadSelected));
                startActivity(intent);
            } else {
                Intent intent = new Intent(ComuSearchResultsAc.this, UserComuDataAc.class);
                intent.putExtra(USERCOMU_LIST_OBJECT.extra, new FullUsuarioComuidadIntent(userComu));
                startActivity(intent);
            }
        }
    }
}