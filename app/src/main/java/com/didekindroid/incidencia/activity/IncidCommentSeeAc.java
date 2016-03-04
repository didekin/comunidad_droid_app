package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekindroid.R;

import static com.didekindroid.common.utils.AppKeysForBundle.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.incidencia.activity.utils.IncidenciaMenu.INCID_COMMENT_REG_AC;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Preconditions:
 * 1. The user is registered.
 * 1. An intent extra is received with an IncidenciaUser instance.
 * Postconditions:
 * 1. An intent extra is passed with an IncidenciaUser instance on to the option menu 'incid_comment_reg_mn'.
 */
public class IncidCommentSeeAc extends AppCompatActivity implements
        IncidCommentSeeListFr.IncidImportanciaGiver {

    private static final String TAG = IncidCommentSeeAc.class.getCanonicalName();

    IncidCommentSeeListFr mFragment;
    IncidImportancia mIncidImportancia;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate().");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.incid_comments_see_ac);
        doToolBar(this, true);

        mIncidImportancia = (IncidImportancia) getIntent().getExtras().getSerializable(INCID_IMPORTANCIA_OBJECT.extra);

        mFragment = (IncidCommentSeeListFr) getFragmentManager()
                .findFragmentById(R.id.incid_comments_see_frg);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState)
    {
        Log.d(TAG, "onSaveInstanceState()");
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Log.d(TAG, "onRestoreInstanceState()");
        super.onRestoreInstanceState(savedInstanceState);
    }

    // ============================================================
    //    ..... ACTION BAR ....
    // ============================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.d(TAG, "onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.incid_comments_see_ac_mn, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.d(TAG, "onOptionsItemSelected()");

        int resourceId = checkNotNull(item.getItemId());

        switch (resourceId) {
            case R.id.incid_comment_reg_ac_mn:
                Intent intent = new Intent();
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.extra, mIncidImportancia);
                INCID_COMMENT_REG_AC.doMenuItem(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    ============================================================
//    .......... INTERFACE METHODS .......
//    ============================================================

    @Override
    public IncidImportancia giveIncidImportancia()
    {
        Log.d(TAG, "giveIncidImportancia()");
        return mIncidImportancia;
    }

//    ============================================================
//    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
//    ============================================================


}
