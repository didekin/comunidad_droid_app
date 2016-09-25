package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.didekin.common.exception.ErrorBean;
import com.didekin.usuario.dominio.Comunidad;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.common.utils.UIutils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

import static com.didekindroid.common.activity.BundleKey.COMUNIDAD_LIST_INDEX;
import static com.didekindroid.common.activity.BundleKey.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.common.activity.BundleKey.COMUNIDAD_SEARCH;
import static com.didekindroid.common.activity.BundleKey.USERCOMU_LIST_OBJECT;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.activity.utils.UserMenu.REG_COMU_USERCOMU_AC;
import static com.didekindroid.usuario.activity.utils.UserMenu.REG_COMU_USER_USERCOMU_AC;
import static com.didekindroid.usuario.activity.utils.UserMenu.SEE_USERCOMU_BY_USER_AC;
import static com.didekindroid.usuario.activity.utils.UserMenu.doUpMenu;
import static com.didekindroid.usuario.activity.utils.UsuarioFragmentTags.comu_search_results_list_fr_tag;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;

/**
 * Preconditions:
 * 1. An intent extra with a comunidad object encapsulating the comunidad to search is received.
 * <p/>
 * Postconditions:
 * <p/>
 * FRAGMENTS:
 * 1. If there are results, a fragment with a list is presented.
 * 2. If not, the user is presented with the activity to register the comunidad.
 * INTENTS:
 * When there are results and the user select one of them:
 * 1. If the user is not registered, an object comunidad is passed as an intent key with the fields:
 * -- comunidadId of the comunidad selected.
 * -- nombreComunidad (with tipoVia,nombreVia, numero and sufijoNumero).
 * -- municipio, with codInProvincia and nombre.
 * -- provincia, with provinciaId and nombre.
 * 2. If the user is registered but not with the comunidad selected, an object comunidad is passed
 * as an intent key.
 * 3. If the user is registered with the comunidad selected, an object usuarioComunidad is passed
 * with its data fully initialized:
 * -- userComu: id, alias, userName.
 * -- comunidad: id, tipoVia, nombreVia, numero, sufijoNumero, fechaAlta,
 * ---- municipio: codInProvincia, nombre.
 * ------ provincia: provinciaId, nombre.
 * -- usuarioComunidad: portal, escalera, planta, puerta, roles.
 */
public class ComuSearchResultsAc extends AppCompatActivity implements
        ComuSearchResultsListFr.ComuListListener {

    // The fragment where the summary data are displayed.
    ComuSearchResultsListFr mComunidadesSummaryFrg;
    // The comunidad searched.
    Comunidad mComunidad;
    // The comunidad index selected.
    int mIndex;
    List<Comunidad> mResultsList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate().");
        super.onCreate(savedInstanceState);

        Comunidad comunidad = (Comunidad) getIntent().getSerializableExtra(COMUNIDAD_SEARCH.key);
        new SearchComunidadesLoader().execute(comunidad);

        setContentView(R.layout.comu_search_results_layout);
        doToolBar(this, true);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState)
    {
        Timber.d("onSaveInstanceState()");
        savedInstanceState.putInt(COMUNIDAD_LIST_INDEX.name(), mIndex);
        savedInstanceState.putSerializable(COMUNIDAD_SEARCH.key, mComunidad);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Timber.d("onRestoreInstanceState()");
        if (savedInstanceState != null) {
            mIndex = savedInstanceState.getInt(COMUNIDAD_LIST_INDEX.name(), 0);
            mComunidadesSummaryFrg.mListView.setSelection(mIndex);
            if (mComunidad == null) {
                mComunidad = (Comunidad) savedInstanceState.getSerializable(COMUNIDAD_SEARCH.key);
            }
        }
    }

    // ============================================================
    //    ..... ACTION BAR ....
    // ============================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Timber.d("onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.comu_search_results_ac_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");

        int resourceId = item.getItemId();

        switch (resourceId) {
            case android.R.id.home:
                doUpMenu(this);
                return true;
            case R.id.see_usercomu_by_user_ac_mn:
                SEE_USERCOMU_BY_USER_AC.doMenuItem(this);
                return true;
            case R.id.reg_nueva_comunidad_ac_mn:
                if (isRegisteredUser(this)) {
                    REG_COMU_USERCOMU_AC.doMenuItem(this);
                } else {
                    REG_COMU_USER_USERCOMU_AC.doMenuItem(this);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //===========================================
    //  .... COMMUNICATION INTERFACES ....
    //===========================================

    @Override
    public void onComunidadSelected(Comunidad comunidad, int lineItemIndex)
    {
        Timber.d("onComunidadSelected().");
        mIndex = lineItemIndex;

        if (!isRegisteredUser(this)) {
            Timber.d("onComunidadSelected(). User is not registered.");
            Intent intent = new Intent(this, RegUserAndUserComuAc.class);
            intent.putExtra(COMUNIDAD_LIST_OBJECT.key, comunidad);
            startActivity(intent);
        } else {
            new UsuarioComunidadGetter().execute(comunidad);
        }
    }

    @Override
    public List<Comunidad> getResultsList()
    {
        Timber.d("getResultsList()");
        return Collections.unmodifiableList(mResultsList);
    }

//    ============================================================
//    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
//    ============================================================

    class SearchComunidadesLoader extends AsyncTask<Comunidad, Void, List<Comunidad>> {

        private UiException uiException;

        @Override
        protected List<Comunidad> doInBackground(Comunidad... comunidades)
        {
            Timber.d("doInBackground()");
            List<Comunidad> comunidadesList = null;
            try {
                comunidadesList = ServOne.searchComunidades(comunidades[0]).execute().body();
            } catch (IOException e) {
                uiException = new UiException(ErrorBean.GENERIC_ERROR);
            }
            return comunidadesList;
        }

        @Override
        protected void onPostExecute(List<Comunidad> comunidadList)
        {
            Timber.d("onPostExecute(); comunidadList.size = %s%n", comunidadList != null ? String.valueOf(comunidadList.size()) : "null");

            if (uiException != null) {
                Timber.d("onPostExecute(), uiException = %s%n", uiException.getErrorBean().getMessage());
                uiException.processMe(ComuSearchResultsAc.this, new Intent());
                return;
            }
            if (comunidadList != null && comunidadList.size() > 0) {
                mResultsList = comunidadList;
                mComunidadesSummaryFrg = new ComuSearchResultsListFr();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.comu_search_results_frg_container_ac, mComunidadesSummaryFrg, comu_search_results_list_fr_tag)
                        .commit();
            } else {
                UIutils.makeToast(ComuSearchResultsAc.this, R.string.no_result_search_comunidad, Toast.LENGTH_LONG);
                if (isRegisteredUser(ComuSearchResultsAc.this)) {
                    REG_COMU_USERCOMU_AC.doMenuItem(ComuSearchResultsAc.this);
                } else {
                    REG_COMU_USER_USERCOMU_AC.doMenuItem(ComuSearchResultsAc.this);
                }
            }
        }
    }


    // TODO: to persist the task during restarts and properly cancel the task when the activity is destroyed. (Example in Shelves)
    class UsuarioComunidadGetter extends AsyncTask<Comunidad, Void, UsuarioComunidad> {

        private Comunidad comunidadSelected;
        UiException uiException;

        @Override
        protected UsuarioComunidad doInBackground(Comunidad... comunidades)
        {
            Timber.d("doInBackground()");

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

            Timber.d(".UsuarioComunidadGetter.onPostExecute(), isUserComu == null : %b%n", isUserComuNull);

            if (uiException != null) {
                uiException.processMe(ComuSearchResultsAc.this, new Intent());
            } else if (isUserComuNull) {
                Intent intent = new Intent(ComuSearchResultsAc.this, RegUserComuAc.class);
                intent.putExtra(COMUNIDAD_LIST_OBJECT.key, comunidadSelected);
                startActivity(intent);
            } else {
                Intent intent = new Intent(ComuSearchResultsAc.this, UserComuDataAc.class);
                intent.putExtra(USERCOMU_LIST_OBJECT.key, userComu);
                startActivity(intent);
            }
        }
    }
}