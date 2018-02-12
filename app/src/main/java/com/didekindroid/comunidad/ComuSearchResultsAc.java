package com.didekindroid.comunidad;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.didekindroid.R;

import timber.log.Timber;

import static com.didekindroid.comunidad.ViewerComuSearchResultAc.newViewerComuSearchResultAc;
import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_SEARCH;
import static com.didekindroid.lib_one.util.CommonAssertionMsg.intent_extra_should_be_initialized;
import static com.didekindroid.lib_one.util.UIutils.assertTrue;
import static com.didekindroid.lib_one.util.UIutils.doToolBar;
import static com.didekindroid.router.MnRouterAction.resourceIdToMnItem;

/**
 * Preconditions:
 * 1. An intent extra with a comunidad object encapsulating the comunidad to search is received,
 * with the following fields:
 * -- tipoVia.
 * -- nombreVia.
 * -- numero.
 * -- sufijoNumero (it can be an empty string).
 * -- municipio with codInProvincia and provinciaId.
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
public class ComuSearchResultsAc extends AppCompatActivity {

    ViewerComuSearchResultAc viewer;
    ComuSearchResultsListFr comuSearchResultListFr;
    View acView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate().");
        super.onCreate(savedInstanceState);
        // Precondition
        assertTrue(getIntent().getSerializableExtra(COMUNIDAD_SEARCH.key) != null, intent_extra_should_be_initialized);

        acView = getLayoutInflater().inflate(R.layout.comu_search_results_layout, null);
        setContentView(acView);
        doToolBar(this, true);

        viewer = newViewerComuSearchResultAc(this);
        viewer.doViewInViewer(savedInstanceState, null);
        comuSearchResultListFr = (ComuSearchResultsListFr) getSupportFragmentManager().findFragmentById(R.id.comu_list_fragment);
    }

    @Override
    public void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
        viewer.clearSubscriptions();
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
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        Timber.d("onPrepareOptionsMenu()");
        viewer.updateActivityMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");
        int resourceId = item.getItemId();
        switch (resourceId) {
            case android.R.id.home:
            case R.id.reg_nueva_comunidad_ac_mn:
            case R.id.see_usercomu_by_user_ac_mn:
                resourceIdToMnItem.get(resourceId).initActivity(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}