package com.didekindroid.api;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.didekindroid.ActivityRouter;

import static com.didekindroid.DefaultNextAcRouter.acRouter;

/**
 * User: pedro@didekin
 * Date: 22/03/17
 * Time: 10:47
 */

public class RootViewReplacer implements RootViewReplacerIf {

    private final Activity activity;
    private final ActivityRouter router;

    public RootViewReplacer(Activity activity)
    {
        this(activity, acRouter);
    }

    RootViewReplacer(Activity activity, ActivityRouter router)
    {
        this.activity = activity;
        this.router = router;
    }

    @Override
    public void replaceRootView(@NonNull Bundle bundle)
    {
        Intent intent = new Intent(activity, router.getNextActivity(activity.getClass()));
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public void replaceRootView(@NonNull Bundle bundle, @NonNull int flags)
    {
        Intent intent = new Intent(activity, router.getNextActivity(activity.getClass()));
        intent.putExtras(bundle);
        intent.setFlags(flags);
        activity.startActivity(intent);
    }
}
