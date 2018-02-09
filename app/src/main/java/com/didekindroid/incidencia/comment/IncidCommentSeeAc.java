package com.didekindroid.incidencia.comment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.router.FragmentInitiatorIf;
import com.didekinlib.model.incidencia.dominio.Incidencia;

import timber.log.Timber;

import static com.didekindroid.incidencia.comment.IncidCommentSeeListFr.newInstance;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidenciaAssertionMsg.incidencia_should_be_initialized;
import static com.didekindroid.lib_one.util.CommonAssertionMsg.fragment_should_be_initialized;
import static com.didekindroid.lib_one.util.UIutils.assertTrue;
import static com.didekindroid.lib_one.util.UIutils.doToolBar;
import static com.didekindroid.router.MnRouter.resourceIdToMnItem;

/**
 * Preconditions:
 * 1. The user is registered.
 * 1. An intent key is received with an Incidencia instance.
 * Postconditions:
 * 1. An intent key is passed with an IncidenciaUser instance on to the option menu 'incid_comment_reg_mn'.
 */
public class IncidCommentSeeAc extends AppCompatActivity implements FragmentInitiatorIf<IncidCommentSeeListFr> {

    IncidCommentSeeListFr fragment;
    Incidencia incidencia;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate().");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incid_comments_see_ac);
        doToolBar(this, true);

        // Preconditions.
        assertTrue(getIntent().hasExtra(INCIDENCIA_OBJECT.key), incidencia_should_be_initialized);
        incidencia = (Incidencia) getIntent().getExtras().getSerializable(INCIDENCIA_OBJECT.key);

        if (savedInstanceState != null) {
            assertTrue((fragment = (IncidCommentSeeListFr) getSupportFragmentManager().
                    findFragmentByTag(IncidCommentSeeListFr.class.getName())) != null, fragment_should_be_initialized);
            return;
        }
        initFragmentTx(newInstance(incidencia));
    }

// =====================  FragmentInitiatorIf  ===================

    @Override
    public AppCompatActivity getActivity()
    {
        return this;
    }

    @Override
    public int getContainerId()
    {
        return R.id.incid_comments_see_ac;
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
                resourceIdToMnItem.get(resourceId).initActivity(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
