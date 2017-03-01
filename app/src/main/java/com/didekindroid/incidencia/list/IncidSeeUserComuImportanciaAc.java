package com.didekindroid.incidencia.list;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.didekindroid.R;
import com.didekinlib.model.incidencia.dominio.Incidencia;

import timber.log.Timber;

import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidenciaAssertionMsg.incidenciaId_should_be_initialized;
import static com.didekindroid.util.MenuRouter.doUpMenu;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.doToolBar;

/**
 * Preconditions:
 * 1. An intent is received witth an incidencia extra, which is passed to the list fragment as an argument.
 * Postconditions:
 */
public class IncidSeeUserComuImportanciaAc extends AppCompatActivity {

    IncidSeeUserComuImportanciaFr mFragment;
    Incidencia mIncidencia;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate().");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incid_see_usercomu_importancia_ac);
        doToolBar(this, true);

        mIncidencia = (Incidencia) getIntent().getExtras().getSerializable(INCIDENCIA_OBJECT.key);
        assertTrue(mIncidencia != null && mIncidencia.getIncidenciaId() > 0, incidenciaId_should_be_initialized);

        mFragment = (IncidSeeUserComuImportanciaFr) getSupportFragmentManager().findFragmentById(R.id.incid_see_usercomu_importancia_frg);
    }

    // ============================================================
    //    ..... ACTION BAR ....
    // ============================================================

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");

        int resourceId = item.getItemId();

        switch (resourceId) {
            case android.R.id.home:
                doUpMenu(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
