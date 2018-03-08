package com.didekindroid.lib_one.usuario;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.AbstractSingleObserver;
import com.didekindroid.lib_one.api.DrawerDecoratedIf;
import com.didekindroid.lib_one.api.Viewer;
import com.didekindroid.lib_one.usuario.dao.CtrlerUsuario;
import com.didekinlib.model.usuario.Usuario;

import java.io.Serializable;

import timber.log.Timber;

import static android.view.Gravity.START;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.api.DrawerDecoratedIf.getHeaderText;
import static com.didekindroid.lib_one.util.DrawerConstant.default_header_no_reg_user;
import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.user_alias;
import static com.didekindroid.lib_one.util.DrawerConstant.header_textview_rsId;
import static com.didekindroid.lib_one.util.DrawerConstant.nav_view_rsId;

/**
 * User: pedro@didekin
 * Date: 15/09/17
 * Time: 18:58
 */

public final class ViewerUserDrawer extends Viewer<DrawerLayout, CtrlerUsuario> {


    private TextView drawerHeaderRot;
    private NavigationView navView;

    private ViewerUserDrawer(DrawerDecoratedIf drawerDecorated)
    {
        super(drawerDecorated.getDrawerDecoratedView(), (Activity) drawerDecorated, null);
        navView = drawerDecorated.getNavView(nav_view_rsId);
        // Inflate menu.
        navView.inflateMenu(drawerDecorated.getDrawerMnRsId());
        drawerHeaderRot = getHeaderText(navView, header_textview_rsId);
    }

    public static ViewerUserDrawer newViewerDrawerMain(DrawerDecoratedIf drawerDecorated)
    {
        Timber.d("newViewerDrawerMain()");
        ViewerUserDrawer instance = new ViewerUserDrawer(drawerDecorated);
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
            drawerHeaderRot.setText(default_header_no_reg_user);
        }
        navView.setNavigationItemSelectedListener(new DrawerMainMnItemSelListener());
    }

    public void doViewForRegUser(Bundle savedState)
    {
        Timber.d("doViewForRegUser()");

        if (savedState != null && savedState.containsKey(user_alias.key)) {
            drawerHeaderRot.setText(savedState.getString(user_alias.key));
        } else {
            controller.loadUserData(new AbstractSingleObserver<Usuario>(this) {
                @Override
                public void onSuccess(Usuario usuario)
                {
                    drawerHeaderRot.setText(usuario.getAlias());
                }
            });
        }
    }

    @Override
    public void saveState(Bundle savedState)
    {
        Timber.d("savedState()");
        super.saveState(savedState);
        if (controller.isRegisteredUser()) {
            savedState.putString(user_alias.key, drawerHeaderRot.getText().toString());
        }
    }

    /* ==================================== Helpers ====================================*/

    public TextView getDrawerHeaderRot()
    {
        return drawerHeaderRot;
    }

    public NavigationView getNavView()
    {
        return navView;
    }

    public void openDrawer()
    {
        Timber.d("openDrawer()");
        buildMenu(navView, controller.isRegisteredUser());
        view.openDrawer(GravityCompat.START);
    }

    public void buildMenu(NavigationView navView, boolean isRegisteredUser)
    {
        Timber.d("buildMenu()");
        Menu drawerMenu = navView.getMenu();
        for (int i = 0; i < drawerMenu.size(); ++i) {
            drawerMenu.getItem(i).setVisible(isRegisteredUser).setEnabled(isRegisteredUser);
        }
        activity.getMenuInflater().inflate(R.menu.confidec_item_mn, drawerMenu);
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
