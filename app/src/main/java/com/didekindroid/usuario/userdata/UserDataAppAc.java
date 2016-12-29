package com.didekindroid.usuario.userdata;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.didekinaar.usuario.password.PasswordChangeAc;
import com.didekinaar.usuario.userdata.UserDataAc;
import com.didekindroid.R;
import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.usuario.delete.DeleteMeAppAc;
import com.didekindroid.usuariocomunidad.SeeUserComuByUserAc;

import timber.log.Timber;

import static com.didekinaar.usuario.ItemMenu.mn_handler;
import static com.didekinaar.utils.UIutils.doUpMenu;
import static com.didekindroid.incidencia.activity.utils.IncidenciaMenu.INCID_SEE_BY_COMU_AC;
import static com.didekindroid.usuariocomunidad.UserComuMenu.SEE_USERCOMU_BY_USER_AC;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Timber.d("onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.user_data_ac_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");

        int resourceId = item.getItemId();

        if (resourceId == android.R.id.home) {
            doUpMenu(this);
            return true;
        } else if (resourceId == R.id.password_change_ac_mn) {
            mn_handler.doMenuItem(this, PasswordChangeAc.class);
            return true;
        } else if (resourceId == R.id.delete_me_ac_mn) {
            mn_handler.doMenuItem(this, DeleteMeAppAc.class);
            return true;
        } else if (resourceId == R.id.see_usercomu_by_user_ac_mn) {
            SEE_USERCOMU_BY_USER_AC.doMenuItem(this, SeeUserComuByUserAc.class);
            return true;
        } else if (resourceId == R.id.comu_search_ac_mn) {
            mn_handler.doMenuItem(this, ComuSearchAc.class);
            return true;
        } else if (resourceId == R.id.incid_see_open_by_comu_ac_mn) {
            INCID_SEE_BY_COMU_AC.doMenuItem(this);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}