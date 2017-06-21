package com.didekindroid.router;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import timber.log.Timber;

import static com.didekindroid.router.ActivityRouter.acRouter;

/**
 * User: pedro@didekin
 * Date: 22/03/17
 * Time: 10:47
 */

public class ActivityInitiator {

    private final Activity activity;
    private final ActivityRouterIf router;

    public ActivityInitiator(Activity activity)
    {
        this(activity, acRouter);
    }

    ActivityInitiator(Activity activity, ActivityRouterIf router)
    {
        this.activity = activity;
        this.router = router;
    }

    public void initAcFromMnKeepIntent(int resourceId)
    {
        Timber.d("initAcFromMnKeepIntent()");
        Intent intent = activity.getIntent();
        if (intent == null) {
            intent = new Intent();
        }
        intent.setClass(activity, router.nextActivityFromMn(resourceId));
        activity.startActivity(intent);
    }

    public void initAcFromMnNewIntent(int resourceId)
    {
        Timber.d("initAcFromMnKeepIntent()");
        Intent intent = new Intent();
        intent.setClass(activity, router.nextActivityFromMn(resourceId));
        activity.startActivity(intent);
    }

    public void initAcFromListener(Bundle bundle, Class<? extends View.OnClickListener> listener)
    {
        Timber.d("initAcFromListener()");
        Intent intent = new Intent(activity, router.nextActivityFromClick(listener));
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public void initAcWithBundle(@NonNull Bundle bundle)
    {
        Timber.d("initAcWithBundle()");
        Intent intent = new Intent(activity, router.nextActivity(activity.getClass()));
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public <T extends Activity> void initAcWithBundle(Bundle bundle, @NonNull Class<T> nextActivityClass)
    {
        Timber.d("initAcWithBundle()");
        Intent intent = new Intent(activity, nextActivityClass);
        if (bundle != null){
            intent.putExtras(bundle);
        }
        activity.startActivity(intent);
    }

    public void initAcWithFlag(@NonNull Bundle bundle, @NonNull int flags)
    {
        Intent intent = new Intent(activity, router.nextActivity(activity.getClass()));
        intent.putExtras(bundle);
        intent.setFlags(flags);
        activity.startActivity(intent);
    }
}
