package com.didekindroid.usuario.userdata;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.didekindroid.R;
import com.didekindroid.usuariocomunidad.SeeUserComuByUserAc;

import timber.log.Timber;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.util.ItemMenu.mn_handler;
import static com.didekindroid.util.MenuRouter.doUpMenu;
import static com.didekindroid.util.MenuRouter.routerMap;

public class UserDataAppAc extends UserDataAc {


    @Override
    protected void setDefaultActivityClassToGo(Class<? extends Activity> activityClassToGo)
    {
        this.activityClassToGo = activityClassToGo;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setDefaultActivityClassToGo(SeeUserComuByUserAc.class);
    }

//    ============================================================
//    ..... ACTION BAR ....
/*    ============================================================*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Timber.d("onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.user_data_ac_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        Timber.d("onPrepareOptionsMenu()");
        // Mostramos el menú si el usuario está registrado. TODO: probar.
        if (TKhandler.isRegisteredUser()) {
            menu.findItem(R.id.see_usercomu_by_user_ac_mn).setVisible(true).setEnabled(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");

        int resourceId = item.getItemId();
        switch (resourceId){
            case android.R.id.home:
                doUpMenu(this);
                return true;
            case R.id.password_change_ac_mn:
            case R.id.delete_me_ac_mn:
            case R.id.see_usercomu_by_user_ac_mn:
            case R.id.comu_search_ac_mn:
            case R.id.incid_see_open_by_comu_ac_mn:
                mn_handler.doMenuItem(this, routerMap.get(resourceId));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
