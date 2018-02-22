package com.didekindroid.lib_one.api;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.didekindroid.R;
import com.didekindroid.lib_one.usuario.ViewerUserDrawer;

import timber.log.Timber;

import static com.didekindroid.lib_one.util.DrawerConstant.drawer_decorator_layout;
import static com.didekindroid.lib_one.util.UiUtil.doToolBar;

/**
 * User: pedro@didekin
 * Date: 21/02/2018
 * Time: 11:53
 */

public class ActivityDrawerMock extends AppCompatActivity implements DrawerDecoratedIf {

    DrawerLayout acView;
    ViewerUserDrawer viewerDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        acView = (DrawerLayout) getLayoutInflater().inflate(drawer_decorator_layout, null, false);
        acView.addView(getLayoutInflater().inflate(R.layout.drawer_mock_ac, acView, false), 0);
        setContentView(acView);
        doToolBar(this, true).setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");
        int resourceId = item.getItemId();

        switch (resourceId) {
            case android.R.id.home:
                viewerDrawer.openDrawer();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setViewerDrawer(ViewerUserDrawer viewerDrawer)
    {
        this.viewerDrawer = viewerDrawer;
    }

    /* ==================================== DrawerDecoratedIf ====================================*/

    @Override
    public ViewerUserDrawer getViewerDrawer()
    {
        return viewerDrawer;
    }

    @Override
    public DrawerLayout getDrawerDecoratedView()
    {
        return acView;
    }

    @Override
    public int getDrawerMnRsId()
    {
        return R.menu.drawer_mock_user_mn;
    }
}
