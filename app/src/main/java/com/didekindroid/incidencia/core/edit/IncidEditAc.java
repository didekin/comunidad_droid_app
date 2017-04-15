package com.didekindroid.incidencia.core.edit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.didekindroid.R;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerParentInjectorIf;
import com.didekindroid.router.ActivityInitiator;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import timber.log.Timber;

import static com.didekindroid.incidencia.core.edit.ViewerIncidEditAc.newViewerIncidEditAc;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.incidencia.utils.IncidFragmentTags.incid_edit_ac_frgs_tag;
import static com.didekindroid.incidencia.utils.IncidenciaAssertionMsg.incid_importancia_should_be_initialized;
import static com.didekindroid.router.ActivityRouter.doUpMenu;
import static com.didekindroid.util.CommonAssertionMsg.fragment_should_be_initialized;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.doToolBar;

/**
 * Preconditions:
 * 1. An intent key is received with the IncidImportancia instance to be edited.
 * -- Users with maximum powers can modify description and ambito of the incidencia. Users with max powers
 * are those with adm function or users who register the incidencia in the first time.
 * Users with adm function can also erase an incidencia if there is not resolucion open.
 * -- Users with minimum powers can only modify the importance assigned by them.
 * 2. An intent key is received with a flag signalling if the incidencia has an open resolucion.
 * Postconditions:
 * 1. An incidencia is updated in BD, once edited.
 * 3. An updated incidencias list of the comunidad is showed.
 */
public class IncidEditAc extends AppCompatActivity implements ViewerParentInjectorIf {

    View acView;
    ViewerIncidEditAc viewer;
    IncidImportancia incidImportancia;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        // Extras in intent.
        incidImportancia = (IncidImportancia) getIntent().getSerializableExtra(INCID_IMPORTANCIA_OBJECT.key);
        boolean flagResolucion = getIntent().getBooleanExtra(INCID_RESOLUCION_FLAG.key, false);
        // Preconditions.
        assertTrue(incidImportancia.getUserComu() != null
                && incidImportancia.getIncidencia().getIncidenciaId() > 0, incid_importancia_should_be_initialized);

        acView = getLayoutInflater().inflate(R.layout.incid_edit_ac, null);
        setContentView(acView);
        doToolBar(this, true);

        Fragment fragmentToAdd;

        if (savedInstanceState != null) {
            fragmentToAdd = getSupportFragmentManager().findFragmentByTag(incid_edit_ac_frgs_tag);
            assertTrue(fragmentToAdd != null, fragment_should_be_initialized);
            if(viewer == null){
                initViewer(fragmentToAdd);
            }
            return;
        }

        Bundle argsFragment = new Bundle();

        if (incidImportancia.isIniciadorIncidencia() || incidImportancia.getUserComu().hasAdministradorAuthority()) {
            argsFragment.putBoolean(INCID_RESOLUCION_FLAG.key, flagResolucion);
            fragmentToAdd = new IncidEditMaxFr();
        } else {
            fragmentToAdd = new IncidEditMinFr();
        }

        argsFragment.putSerializable(INCID_IMPORTANCIA_OBJECT.key, incidImportancia);
        fragmentToAdd.setArguments(argsFragment);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.incid_edit_fragment_container_ac, fragmentToAdd, incid_edit_ac_frgs_tag)
                .commit();

        initViewer(fragmentToAdd);
    }

    @Override
    public void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
        viewer.clearSubscriptions();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Timber.d("onSaveInstanceState()");
        super.onSaveInstanceState(outState);
        viewer.saveState(outState);
    }

    @Override
    public ViewerIf getViewerAsParent()
    {
        Timber.d("getViewerAsParent()");
        return viewer;
    }

    @Override
    public void setChildInViewer(ViewerIf childInViewer)
    {
        Timber.d("setChildInViewer()");
        throw new UnsupportedOperationException();
    }

//    ......................... HELPERS ..........................

    private void initViewer(Fragment fragmentToAdd)
    {
        viewer = newViewerIncidEditAc(this, fragmentToAdd.getView());
        viewer.doViewInViewer(null, incidImportancia);
    }

//    ============================================================
//    ......................... MENU .............................
//    ============================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Timber.d("onCreateOptionsMenu()");
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.incid_edit_ac_mn, menu);
        return true;
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
            case R.id.incid_comment_reg_ac_mn:
            case R.id.incid_comments_see_ac_mn:
                Intent intent = new Intent();
                intent.putExtra(INCIDENCIA_OBJECT.key, incidImportancia.getIncidencia());
                setIntent(intent);
                new ActivityInitiator(this).initActivityFromMn(resourceId);
                return true;
            case R.id.incid_resolucion_reg_ac_mn:
                // We don't reuse flag for resolucion: the state might have changed. We checked DB.
                viewer.checkResolucion(resourceId);
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


