package com.didekinaar.testutil;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.support.test.espresso.IdlingResource;

import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;
import static java.lang.Integer.MAX_VALUE;

/**
 * User: pedro@didekin
 * Date: 27/11/15
 * Time: 12:02
 */
public final class IdlingResourceForIntentServ implements IdlingResource {

    ResourceCallback resourceCallback;
    private final Context context;
    private final IntentService intentService;

    public IdlingResourceForIntentServ(final Context context, IntentService intentService)
    {
        this.context = context;
        this.intentService = intentService;
    }

    @Override
    public final String getName() {
        return this.getClass().getName();
    }

    @Override
    public final void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
        this.resourceCallback = resourceCallback;
    }

    @Override
    public final boolean isIdleNow() {
        boolean idle = !isIntentServiceRunning();
        if (idle && resourceCallback != null) {
            resourceCallback.onTransitionToIdle();
        }
        return idle;
    }

    private boolean isIntentServiceRunning(){
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(MAX_VALUE);
        for (ActivityManager.RunningServiceInfo info : runningServices) {
            if (intentService.getClass().getName().equals(info.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
