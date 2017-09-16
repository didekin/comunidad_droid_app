package com.didekindroid.comunidad;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.didekindroid.R;
import com.didekindroid.api.Viewer;
import com.didekindroid.router.ActivityInitiator;
import com.didekindroid.security.CtrlerAuthToken;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.util.UIutils.doWrongMenuItem;

/**
 * User: pedro@didekin
 * Date: 15/09/17
 * Time: 18:58
 */

final class ViewerDrawerMain extends
        Viewer<DrawerLayout, CtrlerAuthToken> {

    private ViewerDrawerMain(DrawerLayout view, AppCompatActivity activity)
    {
        super(view, activity, null);
    }

    static ViewerDrawerMain newViewerDrawerMain(AppCompatActivity activity)
    {
        Timber.d("newViewerDrawerMain()");
        ViewerDrawerMain instance = new ViewerDrawerMain(activity.<DrawerLayout>findViewById(R.id.drawer_main_layout), activity);
        instance.setController(new CtrlerAuthToken());
        return instance;
    }

    /* ==================================== ViewerIf ====================================*/

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        NavigationView navView = view.findViewById(R.id.drawer_main_nav_view);
        navView.setNavigationItemSelectedListener(new DrawerMainMnItemSelListener());
        buildMenu(navView);
    }

    /* ==================================== Helpers ====================================*/

    void openDrawer()
    {
        Timber.d("openDrawer()");
        view.openDrawer(GravityCompat.START);
    }

    void buildMenu(NavigationView navView)
    {
        Timber.d("buildMenu()");
        Menu drawerMenu = navView.getMenu();
        boolean isRegistered = controller.isRegisteredUser();
        drawerMenu.findItem(R.id.see_usercomu_by_user_ac_mn).setVisible(isRegistered).setEnabled(isRegistered);
        drawerMenu.findItem(R.id.user_data_ac_mn).setVisible(isRegistered).setEnabled(isRegistered);
        drawerMenu.findItem(R.id.incid_see_open_by_comu_ac_mn).setVisible(isRegistered).setEnabled(isRegistered);
    }

    class DrawerMainMnItemSelListener implements
            NavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            Timber.d("onNavigationItemSelected()");
            item.setChecked(true);
            int resourceId = item.getItemId();

            switch (resourceId) {
                case R.id.user_data_ac_mn:
                case R.id.see_usercomu_by_user_ac_mn:
                case R.id.incid_see_open_by_comu_ac_mn:
                case R.id.confidencialidad_ac_mn:
                    new ActivityInitiator(activity).initAcFromMnNewIntent(resourceId);
                    break;
                default:
                    doWrongMenuItem(item);
            }
            // Closing drawer on item click
            view.closeDrawers();
            return true;
        }
    }
}
