package com.didekindroid.usuario.delete;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.util.MenuRouter;

import timber.log.Timber;

public class DeleteMeAppAc extends DeleteMeAc {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setDefaultActivityClassToGo(ComuSearchAc.class);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setDefaultActivityClassToGo(Class<? extends Activity> activityClassToGo)
    {
        defaultActivityClassToGo = activityClassToGo;
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
                MenuRouter.doUpMenu(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
