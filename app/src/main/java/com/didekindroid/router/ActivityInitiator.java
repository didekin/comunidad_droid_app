package com.didekindroid.router;

import android.app.Activity;

import com.didekindroid.api.router.ActivityInitiatorIf;
import com.didekindroid.api.router.ActivityRouterIf;

import static com.didekindroid.router.ActivityRouter.acRouter;

/**
 * User: pedro@didekin
 * Date: 22/03/17
 * Time: 10:47
 */

public class ActivityInitiator implements ActivityInitiatorIf {

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

    @Override
    public Activity getActivity()
    {
        return activity;
    }

    @Override
    public ActivityRouterIf getRouter()
    {
        return router;
    }
}
