package com.didekindroid.usuario.delete;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.didekindroid.api.ManagerIf;
import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf;
import com.didekindroid.util.MenuRouter;

import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.didekindroid.api.ManagerIf.ViewerIf;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_be_registered;
import static com.didekindroid.util.DefaultNextAcRouter.routerMap;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.doToolBar;

/**
 * Preconditions:
 * 1. Registered user.
 * Postconditions:
 * 1. Unregistered user, if she chooses so. ComuSearchAc is to be showed.
 */
public class DeleteMeAc extends AppCompatActivity implements ViewerIf<View,Object>, ManagerIf<Object> {

    View acView;
    ControllerDeleteMeIf controller;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        acView = getLayoutInflater().inflate(R.layout.delete_me_ac, null);
        setContentView(acView);
        doToolBar(this, true);
        // Controller initialization.
        controller = new ControllerDeleteMe(this);

        // Preconditions.
        assertTrue(controller.isRegisteredUser(), user_should_be_registered);

        Button mUnregisterButton = (Button) findViewById(R.id.delete_me_ac_unreg_button);
        mUnregisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("mUnregisterButton.OnClickListener().onClick()");
                controller.unregisterUser();
            }
        });
    }

    @Override
    public void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
        // Viewer lifecycle call.
        clearControllerSubscriptions();
    }

    // ============================================================
    //    .................... ACTION BAR ........................
    // ============================================================

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");

        int resourceId = item.getItemId();

        switch (resourceId) {
            case android.R.id.home:
                MenuRouter.doUpMenu(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // ============================================================
    //  ................. VIEWER IMPLEMENTATION ...............
    // ============================================================

    @Override
    public ManagerIf<Object> getManager()
    {
        Timber.d("getContext()");
        return this;
    }

    @Override
    public UiExceptionIf.ActionForUiExceptionIf processControllerError(UiException ui)
    {
        Timber.d("processControllerError()");
        return ui.processMe(this, new Intent());
    }

    @Override
    public int clearControllerSubscriptions()
    {
        return controller.clearSubscriptions();
    }

    @Override
    public View getViewInViewer()
    {
        Timber.d("getViewInViewer()");
        return acView;
    }

    // ============================================================
    //   .............. ManagerIf ...............
    // ============================================================

    @Override
    public Activity getActivity()
    {
        return this;
    }

    @Override
    public UiExceptionIf.ActionForUiExceptionIf processViewerError(UiException ui)
    {
        Timber.d("processViewerError()");
        return ui.processMe(this, new Intent());
    }

    @Override
    public void replaceRootView(Object initParamsForView)
    {
        Timber.d("replaceRootView()");
        Intent intent = new Intent(this, routerMap.get(this.getClass()));
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
