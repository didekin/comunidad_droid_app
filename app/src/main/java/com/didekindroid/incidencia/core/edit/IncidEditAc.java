package com.didekindroid.incidencia.core.edit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.didekindroid.R;
import com.didekindroid.api.ChildViewersInjectorIf;
import com.didekindroid.api.ParentViewerInjectedIf;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.router.ActivityInitiatorIf;
import com.didekindroid.api.router.FragmentInitiatorIf;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import timber.log.Timber;

import static com.didekindroid.incidencia.core.edit.ViewerIncidEditAc.newViewerIncidEditAc;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.incidencia.utils.IncidenciaAssertionMsg.incid_importancia_should_be_initialized;
import static com.didekindroid.router.ActivityRouter.doUpMenu;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.doToolBar;

/**
 * Preconditions:
 * 1. An intent key is received with a IncidAndResolBundle instance.
 * -- Users with maximum powers can modify description and ambito of the incidencia; they can also
 * erase an incidencia if there is not resolucion open. Users with max powers
 * are those with adm function or users who register the incidencia in the first time.
 * -- Users with minimum powers can only modify the importance assigned by them.
 * Postconditions:
 * 1. An incidencia is updated in BD, once edited.
 * 3. An updated incidencias list of the comunidad is showed.
 */
public class IncidEditAc extends AppCompatActivity implements ChildViewersInjectorIf, ActivityInitiatorIf,
        FragmentInitiatorIf<IncidEditFr> {

    View acView;
    ViewerIncidEditAc viewer;
    IncidAndResolBundle resolBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        // Extras in intent.
        resolBundle = (IncidAndResolBundle) getIntent().getSerializableExtra(INCID_RESOLUCION_BUNDLE.key);
        IncidImportancia incidImportancia = resolBundle.getIncidImportancia();
        // Preconditions.
        assertTrue(incidImportancia.getUserComu() != null && incidImportancia.getIncidencia().getIncidenciaId() > 0, incid_importancia_should_be_initialized);

        acView = getLayoutInflater().inflate(R.layout.incid_edit_ac, null);
        setContentView(acView);
        doToolBar(this, true);

        if (savedInstanceState != null && viewer == null) {
            initViewer();
            return;
        }

        if (incidImportancia.isIniciadorIncidencia() || incidImportancia.getUserComu().hasAdministradorAuthority()) {
            initFragmentTx(IncidEditMaxFr.newInstance(resolBundle));
        } else {
            initFragmentTx(IncidEditMinFr.newInstance(resolBundle));
        }
        initViewer();
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

    // ==================================  ChildViewersInjectorIf  =================================

    @Override
    public ParentViewerInjectedIf getParentViewer()
    {
        Timber.d("getParentViewer()");
        return viewer;
    }

    @Override
    public void setChildInParentViewer(ViewerIf childViewer)
    {
        Timber.d("setChildInParentViewer()");
        viewer.setChildViewer(childViewer);
    }

// ================  ActivityInitiatorIf  ====================

    @Override
    public AppCompatActivity getActivity()
    {
        return this;
    }

// ================  FragmentInitiatorIf  ====================

    @Override
    public int getContainerId()
    {
        return R.id.incid_edit_fragment_container_ac;
    }

//    ......................... HELPERS ..........................

    private void initViewer()
    {
        viewer = newViewerIncidEditAc(this);
        viewer.doViewInViewer(null, resolBundle);
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
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        Timber.d("onPrepareOptionsMenu()");
        // Visible for ADM users always and for the rest only when there is resolucion.
        boolean hasToBeShown = resolBundle.getIncidImportancia().getUserComu().hasAdministradorAuthority()
                || resolBundle.hasResolucion();
        menu.findItem(R.id.incid_resolucion_reg_ac_mn)
                .setVisible(hasToBeShown)
                .setEnabled(hasToBeShown);
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
                initAcFromMenu(INCIDENCIA_OBJECT.getBundleForKey(resolBundle.getIncidImportancia().getIncidencia()), resourceId);
                return true;
            case R.id.incid_resolucion_reg_ac_mn:
                viewer.checkResolucion();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


