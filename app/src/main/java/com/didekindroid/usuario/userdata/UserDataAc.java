package com.didekindroid.usuario.userdata;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.didekindroid.R;
import com.didekindroid.api.RootViewReplacer;
import com.didekindroid.api.RootViewReplacerIf;
import com.didekindroid.util.MenuRouter;

import timber.log.Timber;

import static com.didekindroid.util.ItemMenu.mn_handler;
import static com.didekindroid.util.MenuRouter.doUpMenu;
import static com.didekindroid.util.UIutils.doToolBar;

/**
 * Preconditions:
 * 1. Registered user.
 * Postconditions:
 * 1. Registered user with modified data.
 * 2. An intent is created for menu options with the old user data, once they have been loaded.
 */
public class UserDataAc extends AppCompatActivity implements RootViewReplacerIf {

    ViewerUserDataIf viewer;
    View acView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        acView = getLayoutInflater().inflate(R.layout.user_data_ac, null);
        setContentView(acView);
        doToolBar(this, true);
        viewer = new ViewerUserData(acView, this);
    }

    @Override
    protected void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
        viewer.clearSubscriptions();
    }

    @Override
    public void replaceRootView(Bundle bundle)
    {
        Timber.d("replaceView()");
        new RootViewReplacer(this).replaceRootView(bundle);
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
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        Timber.d("onPrepareOptionsMenu()");
        // Update intent in activity with user data.
        if (viewer.getIntentForMenu().get() != null){
            setIntent(viewer.getIntentForMenu().getAndSet(null));
        }
        return true;
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
            case R.id.password_change_ac_mn:
            case R.id.delete_me_ac_mn:
            case R.id.see_usercomu_by_user_ac_mn:
            case R.id.comu_search_ac_mn:
            case R.id.incid_see_open_by_comu_ac_mn:
                mn_handler.doMenuItem(this, MenuRouter.routerMap.get(resourceId));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
