package com.didekindroid.router;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.AbstractSingleObserver;
import com.didekindroid.lib_one.api.Viewer;
import com.didekindroid.lib_one.usuario.dao.CtrlerUsuario;
import com.didekinlib.model.usuario.Usuario;

import java.io.Serializable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import timber.log.Timber;

import static android.view.Gravity.START;
import static android.view.View.VISIBLE;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.user_alias;
import static com.didekindroid.router.MnRouterAction.incid_see_closed_by_comu_mn;
import static com.didekindroid.router.MnRouterAction.incid_see_open_by_comu_mn;
import static com.didekindroid.router.MnRouterAction.see_usercomu_by_user_mn;
import static com.didekindroid.router.MnRouterAction.user_data_mn;
import static java.util.EnumSet.of;

/**
 * User: pedro@didekin
 * Date: 15/09/17
 * Time: 18:58
 */

public final class ViewerDrawerMain extends Viewer<DrawerLayout, CtrlerUsuario> {

    private EnumSet<MnRouterAction> menuItemsToDraw;
    @SuppressWarnings("WeakerAccess")
    TextView drawerHeaderRot;
    NavigationView navView;

    private ViewerDrawerMain(DrawerLayout view, AppCompatActivity activity)
    {
        super(view, activity, null);
        menuItemsToDraw = of(user_data_mn, see_usercomu_by_user_mn, incid_see_open_by_comu_mn, incid_see_closed_by_comu_mn);
        navView = view.findViewById(R.id.drawer_main_nav_view);
        drawerHeaderRot = navView.getHeaderView(0).findViewById(R.id.drawer_main_header_text);
    }

    public static ViewerDrawerMain newViewerDrawerMain(AppCompatActivity activity)
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

    public void openDrawer()
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
        for (MnRouterAction menuItem : menuItemsToDraw) {
            drawerMenu.findItem(menuItem.getMnItemRsId()).setVisible(isRegistered).setEnabled(isRegistered);
        }
    }

    Set<MnRouterAction> getMenuItemsToDraw()
    {
        return Collections.unmodifiableSet(menuItemsToDraw);
    }

    class DrawerMainMnItemSelListener implements NavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            Timber.d("onNavigationItemSelected()");
            item.setChecked(true);
            routerInitializer.get().getMnRouter().getActionFromMnItemId(item.getItemId())
                    .initActivity(getActivity());
            /* Closing drawer on item click*/
            view.closeDrawer(START);
            return true;
        }
    }
}
