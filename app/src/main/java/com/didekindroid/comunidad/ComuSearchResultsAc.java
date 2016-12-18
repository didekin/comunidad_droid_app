package com.didekindroid.comunidad;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.didekin.comunidad.Comunidad;
import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekinaar.R;
import com.didekinaar.exception.UiException;
import com.didekinaar.utils.UIutils;
import com.didekindroid.usuariocomunidad.RegUserAndUserComuAc;
import com.didekindroid.usuariocomunidad.RegUserComuAc;
import com.didekindroid.usuariocomunidad.UserComuDataAc;

import java.util.Collections;
import java.util.List;

import timber.log.Timber;

import static com.didekinaar.utils.UIutils.checkPostExecute;
import static com.didekinaar.utils.UIutils.doToolBar;
import static com.didekinaar.utils.UIutils.isRegisteredUser;
import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_SEARCH;
import static com.didekindroid.usuariocomunidad.UserComuService.AppUserComuServ;
import static com.didekindroid.usuariocomunidad.UserComuBundleKey.USERCOMU_LIST_OBJECT;
import static com.didekindroid.usuariocomunidad.UserComuMenu.REG_COMU_USERCOMU_AC;
import static com.didekindroid.usuariocomunidad.UserComuMenu.REG_COMU_USER_USERCOMU_AC;
import static com.didekindroid.usuariocomunidad.UserComuMenu.SEE_USERCOMU_BY_USER_AC;

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
    List<Comunidad> mResultsList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate().");
        super.onCreate(savedInstanceState);

        mComunidad = (Comunidad) getIntent().getSerializableExtra(COMUNIDAD_SEARCH.key);
        setContentView(R.layout.comu_search_results_layout);
        doToolBar(this, true);

        mComunidadesSummaryFrg = (ComuSearchResultsListFr) getSupportFragmentManager().findFragmentById(R.id.comu_list_fragment);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState)
    {
        Timber.d("onSaveInstanceState()");
        savedInstanceState.putSerializable(COMUNIDAD_SEARCH.key, mComunidad);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Timber.d("onRestoreInstanceState()");
        if (savedInstanceState != null && mComunidad == null) {
                mComunidad = (Comunidad) savedInstanceState.getSerializable(COMUNIDAD_SEARCH.key);
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

        if (resourceId == android.R.id.home) {
            UIutils.doUpMenu(this);
            return true;
        } else if (resourceId == R.id.see_usercomu_by_user_ac_mn) {
            SEE_USERCOMU_BY_USER_AC.doMenuItem(this);
            return true;
        } else if (resourceId == R.id.reg_nueva_comunidad_ac_mn) {
            if (isRegisteredUser(this)) {
                REG_COMU_USERCOMU_AC.doMenuItem(this);
            } else {
                REG_COMU_USER_USERCOMU_AC.doMenuItem(this);
            }
            return true;
        } else {
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

    @Override
    public Comunidad getComunidadToSearch(){
        return mComunidad;
    }

    @Override
    public Activity getActivity()
    {
        return this;
    }

//    ============================================================
//    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
/*    ============================================================*/

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
                userComuByUserAndComu = AppUserComuServ.getUserComuByUserAndComu(comunidadSelected.getC_Id());
            } catch (UiException e) {
                uiException = e;
            }
            return userComuByUserAndComu;
        }

        @Override
        protected void onPostExecute(UsuarioComunidad userComu)
        {
            if (checkPostExecute(ComuSearchResultsAc.this)) return;

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