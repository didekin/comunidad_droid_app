package com.didekindroid.usuario.login;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import com.didekindroid.R;
import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.util.MenuRouter;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 29/11/16
 * Time: 13:19
 */

public class LoginAppAc extends LoginAc {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setDefaultActivityClassToGo(ComuSearchAc.class);
    }

    @Override
    protected void setDefaultActivityClassToGo(Class<? extends Activity> activityClassToGo)
    {
         defaultActivityClassToGo = activityClassToGo;
    }

    @Override
    public int getDialogThemeId()
    {
        return R.style.alertDialogTheme;
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
