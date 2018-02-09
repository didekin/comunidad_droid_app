package com.didekindroid.lib_one.api.router;

import android.app.Activity;
import android.support.annotation.NonNull;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 11/03/17
 * Time: 14:52
 */

public interface ActivityRouterIf {

    void doUpMenuActivity(@NonNull Activity activity);

    @SuppressWarnings("unused")


    Class<? extends Activity> nextActivityFromMn(int resourceId);

    Class<? extends Activity> nextActivity(Class<? extends Activity> previousActivity);
}
