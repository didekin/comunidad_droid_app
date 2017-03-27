package com.didekindroid.api;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import static com.didekindroid.util.DefaultNextAcRouter.acRouter;

/**
 * User: pedro@didekin
 * Date: 22/03/17
 * Time: 10:47
 */

public class RootViewReplacer implements RootViewReplacerIf {

    private final Activity activity;

    public RootViewReplacer(Activity activity)
    {
        this.activity = activity;
    }

    @Override
    public void replaceRootView(@NonNull Bundle bundle)
    {
        Intent intent = new Intent(activity, acRouter.getNextActivity(activity.getClass()));
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public void replacerRootView(@NonNull Bundle bundle, @NonNull int flags)
    {
        Intent intent = new Intent(activity, acRouter.getNextActivity(activity.getClass()));
        intent.putExtras(bundle);
        intent.setFlags(flags);
        activity.startActivity(intent);
    }
}
