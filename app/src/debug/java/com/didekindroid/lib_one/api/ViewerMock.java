package com.didekindroid.lib_one.api;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.didekindroid.lib_one.api.router.UiExceptionRouterIf;
import com.didekindroid.lib_one.api.router.RouterActionIf;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 24/02/17
 * Time: 17:23
 */
public class ViewerMock<T extends View, C extends ControllerIf> extends Viewer<T, C> implements
        SpinnerEventListener {

    public ViewerMock(AppCompatActivity activity)
    {
        this(null, activity);
    }

    ViewerMock(T view, AppCompatActivity activity)
    {
        super(view, activity, null);
    }

    @Override
    public UiExceptionRouterIf getExceptionRouter()
    {
        return httpMsg -> (RouterActionIf) () -> ActivityNextMock.class;
    }

    @Override
    public int clearSubscriptions()
    {
        return 0;
    }

    @Override
    public void doOnClickItemId(@NonNull SpinnerEventItemSelectIf spinnerEventItemSelect)
    {
        Timber.d("doOnClickItemId()");
    }
}
