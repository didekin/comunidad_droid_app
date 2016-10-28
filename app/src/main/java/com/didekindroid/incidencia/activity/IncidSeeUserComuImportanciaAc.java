package com.didekindroid.incidencia.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.didekin.incidservice.dominio.Incidencia;
import com.didekindroid.R;

import java.util.Objects;

import timber.log.Timber;

import static com.didekindroid.common.activity.BundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.usuario.activity.utils.UserMenu.doUpMenuClearSingleTop;

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
        Objects.equals(mIncidencia != null && mIncidencia.getIncidenciaId() > 0, true);

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
                doUpMenuClearSingleTop(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
