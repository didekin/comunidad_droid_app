package com.didekindroid.incidencia.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.didekin.incidservice.domain.IncidenciaUser;
import com.didekindroid.R;
import com.didekindroid.common.utils.AppKeysForBundle;
import com.didekindroid.incidencia.activity.utils.IncidenciaMenu;

import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Preconditions:
 * 1. The user is registered.
 * 1. An intent extra is received with an IncidenciaUser instance.
 * Postconditions:
 * 1. An intent extra is passed with an IncidenciaUser instance.
 */
public class IncidCommentSeeAc extends AppCompatActivity {

    private static final String TAG = IncidCommentSeeAc.class.getCanonicalName();

    IncidCommentSeeListFr mFragment;
    IncidenciaUser mIncidenciaUser;

    /**
     * This activity is a point of registration for receiving GCM notifications of new incidents.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate().");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.incid_comment_see_ac);
        doToolBar(this, true);

        mIncidenciaUser = (IncidenciaUser) getIntent().getExtras().getSerializable(AppKeysForBundle.INCIDENCIA_USER_OBJECT.extra);
        mFragment = (IncidCommentSeeListFr) getFragmentManager()
                .findFragmentById(R.id.incid_comment_see_frg);
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
                IncidenciaMenu.INCID_COMMENT_REG_AC.doMenuItem(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//  ........... HELPER INTERFACES AND CLASSES ....................



//    ============================================================
//    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
//    ============================================================


}
