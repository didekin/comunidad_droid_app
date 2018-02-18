package com.didekindroid.usuario;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.router.MnRouterIf;

import timber.log.Timber;

import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.lib_one.util.UiUtil.doToolBar;

/**
 * Preconditions:
 * 1. Registered user.
 * Postconditions:
 * 1. Registered user with modified data.
 * 2. An intent is created for menu options with the old user data, once they have been loaded.
 */
public class UserDataAc extends AppCompatActivity {

    ViewerUserData viewer;
    View acView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        acView = getLayoutInflater().inflate(R.layout.user_data_ac, null);
        setContentView(acView);
        doToolBar(this, true);
        viewer = ViewerUserData.newViewerUserData(this);
        viewer.doViewInViewer(savedInstanceState, null);
    }

    @Override
    protected void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
        viewer.clearSubscriptions();
    }

//    ============================================================
//    ..... ACTION BAR ....
//    ============================================================

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

        MnRouterIf router = routerInitializer.get().getMnRouter();
        int resourceId = item.getItemId();
        switch (resourceId) {
            case android.R.id.home:
                router.getActionFromMnItemId(resourceId).initActivity(this);
                return true;
            case R.id.password_change_ac_mn:
                router.getActionFromMnItemId(resourceId)
                        .initActivity(this, user_name.getBundleForKey(viewer.oldUser.get().getUserName()));
            case R.id.delete_me_ac_mn:
            case R.id.see_usercomu_by_user_ac_mn:
            case R.id.comu_search_ac_mn:
                router.getActionFromMnItemId(resourceId).initActivity(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
