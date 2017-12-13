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
import com.didekindroid.api.AbstractSingleObserver;
import com.didekindroid.api.Viewer;
import com.didekindroid.api.router.ActivityInitiatorIf;
import com.didekindroid.usuario.dao.CtrlerUsuario;
import com.didekinlib.model.usuario.Usuario;

import java.io.Serializable;

import timber.log.Timber;

import static android.view.Gravity.START;
import static android.view.View.VISIBLE;
import static com.didekindroid.comunidad.ViewerDrawerMain.DynamicMenuItem.default_menu;
import static com.didekindroid.comunidad.ViewerDrawerMain.DynamicMenuItem.rsIdToMenuItem;
import static com.didekindroid.usuario.UsuarioBundleKey.user_alias;
import static com.didekindroid.util.UIutils.doWrongMenuItem;

/**
 * User: pedro@didekin
 * Date: 15/09/17
 * Time: 18:58
 */

final class ViewerDrawerMain extends Viewer<DrawerLayout, CtrlerUsuario> {

    @SuppressWarnings("WeakerAccess")
    TextView drawerHeaderRot;
    NavigationView navView;

    private ViewerDrawerMain(DrawerLayout view, AppCompatActivity activity)
    {
        super(view, activity, null);
        navView = view.findViewById(R.id.drawer_main_nav_view);
        drawerHeaderRot = navView.getHeaderView(0).findViewById(R.id.drawer_main_header_text);
    }

    static ViewerDrawerMain newViewerDrawerMain(AppCompatActivity activity)
    {
        Timber.d("newViewerDrawerMain()");
        ViewerDrawerMain instance = new ViewerDrawerMain(activity.findViewById(R.id.drawer_main_layout), activity);
        instance.setController(new CtrlerUsuario());
        return instance;
    }

    /* ==================================== ViewerIf ====================================*/

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");

        if (controller.isRegisteredUser()) {
            doViewForRegUser(savedState);
        } else {
            drawerHeaderRot.setVisibility(VISIBLE);
            drawerHeaderRot.setText(R.string.app_name);
        }

        navView.setNavigationItemSelectedListener(new DrawerMainMnItemSelListener());
        buildMenu(navView);
    }

    void doViewForRegUser(Bundle savedState)
    {
        if (savedState != null && savedState.containsKey(user_alias.key)) {
            drawerHeaderRot.setText(savedState.getString(user_alias.key));
        } else {
            controller.loadUserData(new AbstractSingleObserver<Usuario>(this) {
                @Override
                public void onSuccess(Usuario usuario)
                {
                    drawerHeaderRot.setText(usuario.getAlias());
                    drawerHeaderRot.setVisibility(VISIBLE);
                }
            });
        }
    }

    @Override
    public void saveState(Bundle savedState)
    {
        super.saveState(savedState);
        if (controller.isRegisteredUser()) {
            savedState.putString(user_alias.key, drawerHeaderRot.getText().toString());
        }
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
            void processMenu(DrawerMainMnItemSelListener activity, MenuItem menuItem)
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

        void processMenu(DrawerMainMnItemSelListener listener, MenuItem menuItem)
        {
            Timber.d("processMenu()");
            listener.initAcFromMenu(null, resourceId);
        }
    }

    class DrawerMainMnItemSelListener implements NavigationView.OnNavigationItemSelectedListener, ActivityInitiatorIf {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            Timber.d("onNavigationItemSelected()");
            item.setChecked(true);

            rsIdToMenuItem.get(item.getItemId(), default_menu).processMenu(this, item);
            /* Closing drawer on item click*/
            view.closeDrawer(START);
            return true;
        }

        // ====================  ActivityInitiatorIf  ===============

        @Override
        public Activity getActivity()
        {
            return activity;
        }
    }
}
