package com.didekindroid.incidencia.comment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.didekindroid.R;
import com.didekindroid.router.ActivityInitiatorIf;
import com.didekindroid.router.FragmentInitiator;
import com.didekinlib.model.incidencia.dominio.Incidencia;

import timber.log.Timber;

import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidFragmentTags.incid_comments_see_list_fr_tag;
import static com.didekindroid.incidencia.utils.IncidenciaAssertionMsg.incidencia_should_be_initialized;
import static com.didekindroid.router.ActivityRouter.doUpMenu;
import static com.didekindroid.util.CommonAssertionMsg.fragment_should_be_initialized;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.doToolBar;

/**
 * Preconditions:
 * 1. The user is registered.
 * 1. An intent key is received with an IncidenciaUser instance.
 * Postconditions:
 * 1. An intent key is passed with an IncidenciaUser instance on to the option menu 'incid_comment_reg_mn'.
 */
public class IncidCommentSeeAc extends AppCompatActivity implements ActivityInitiatorIf {

    IncidCommentSeeListFr mFragment;
    Incidencia mIncidencia;

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
        mIncidencia = (Incidencia) getIntent().getExtras().getSerializable(INCIDENCIA_OBJECT.key);

        if (savedInstanceState != null) {
            assertTrue((mFragment = (IncidCommentSeeListFr) getSupportFragmentManager().
                    findFragmentByTag(incid_comments_see_list_fr_tag)) != null, fragment_should_be_initialized);
            return;
        }

        mFragment = IncidCommentSeeListFr.newInstance(mIncidencia);
        new FragmentInitiator(this, R.id.incid_comments_see_ac).initFragment(mFragment, incid_comments_see_list_fr_tag);
    }

    // ==================================  ActivityInitiatorIf  =================================

    @Override
    public Activity getActivity()
    {
        return this;
    }

// ============================================================
//    ..... ACTION BAR ....
/* ============================================================*/

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        Timber.d("onPrepareOptionsMenu()");
        // Mostramos el menú si la incidencia está abierta.
        if (mIncidencia.getFechaCierre() == null) {
            menu.findItem(R.id.incid_comment_reg_ac_mn).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Timber.d("onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.incid_comments_see_ac_mn, menu);
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
            case R.id.incid_comment_reg_ac_mn:
                Intent intent = new Intent();
                intent.putExtra(INCIDENCIA_OBJECT.key, mIncidencia);
                initAcFromMenu(resourceId);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
