package com.didekindroid.comunidad;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.api.Viewer;
import com.didekindroid.router.ActivityInitiator;
import com.didekindroid.security.CtrlerAuthToken;

import java.io.Serializable;

import timber.log.Timber;

import static android.view.Gravity.START;
import static com.didekindroid.comunidad.ViewerDrawerMain.DynamicMenuItem.default_menu;
import static com.didekindroid.comunidad.ViewerDrawerMain.DynamicMenuItem.rsIdToMenuItem;
import static com.didekindroid.util.UIutils.doWrongMenuItem;

/**
 * User: pedro@didekin
 * Date: 15/09/17
 * Time: 18:58
 */

final class ViewerDrawerMain extends
        Viewer<DrawerLayout, CtrlerAuthToken> {

    private NavigationView navView;
    private TextView drawerHeaderRot;

    private ViewerDrawerMain(DrawerLayout view, AppCompatActivity activity)
    {
        super(view, activity, null);
        navView = view.findViewById(R.id.drawer_main_nav_view);
        drawerHeaderRot = view.findViewById(R.id.drawer_main_header_text);
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
//        drawerHeaderRot.setText();
        navView.setNavigationItemSelectedListener(new DrawerMainMnItemSelListener());
        buildMenu(navView);
    }

    @Override
    public void saveState(Bundle savedState)
    {
        super.saveState(savedState);

    }

    /* ==================================== Helpers ====================================*/

    void openDrawer()
    {
        Timber.d("openDrawer()");
        buildMenu(navView);
        view.openDrawer(GravityCompat.START);
    }

    private void buildMenu(NavigationView navView)
    {
        Timber.d("buildMenu()");
        Menu drawerMenu = navView.getMenu();
        boolean isRegistered = controller.isRegisteredUser();
        for (DynamicMenuItem menuItem : DynamicMenuItem.menuItemsToDraw) {
            drawerMenu.findItem(menuItem.resourceId).setVisible(isRegistered).setEnabled(isRegistered);
        }
    }

    enum DynamicMenuItem {

        user_data(R.id.user_data_ac_mn),
        user_comus(R.id.see_usercomu_by_user_ac_mn),
        incid_open(R.id.incid_see_open_by_comu_ac_mn),
        incid_closed(R.id.incid_see_closed_by_comu_ac_mn),
        confidencialidad(R.id.confidencialidad_ac_mn),
        default_menu(-11) {
            @Override
            void processMenu(Activity activity, MenuItem menuItem)
            {
                doWrongMenuItem(menuItem);
            }
        },;

        // ................. Static methods ................

        static final SparseArray<DynamicMenuItem> rsIdToMenuItem = new SparseArray<>();
        static final DynamicMenuItem[] menuItemsToDraw = new DynamicMenuItem[]{user_data, user_comus, incid_open, incid_closed};

        static {
            for (DynamicMenuItem menuItem : values()) {
                rsIdToMenuItem.put(menuItem.resourceId, menuItem);
            }
        }

        // ................. Instance methods ...............

        final int resourceId;

        DynamicMenuItem(int itemRsId)
        {
            resourceId = itemRsId;
        }

        void processMenu(Activity activity, MenuItem menuItem)
        {
            new ActivityInitiator(activity).initAcFromMnNewIntent(resourceId);
        }
    }

    class DrawerMainMnItemSelListener implements NavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            Timber.d("onNavigationItemSelected()");
            item.setChecked(true);

            rsIdToMenuItem.get(item.getItemId(), default_menu).processMenu(activity, item);
            /* Closing drawer on item click*/
            view.closeDrawer(START);
            return true;
        }
    }
}
