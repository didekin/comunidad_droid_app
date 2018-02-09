package com.didekindroid.lib_one.api.router;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 17/11/2017
 * Time: 14:13
 * <p>
 * Mark interface to be used by ActivityRouter.
 */
@FunctionalInterface
public interface RouterTo {

    Class<? extends Activity> getAcToGo();

    @SuppressWarnings("unused")
    default void doUpInFragment(Activity activity)
    {
        Timber.d("doUpInFragment()");
        if (activity.getFragmentManager().getBackStackEntryCount() > 0) {
            activity.getFragmentManager().popBackStack();
        }
    }

    default void initActivity(@NonNull Activity activity)
    {
        initActivity(activity, null);
    }

    default void initActivity(@NonNull Activity activity, @Nullable Bundle bundle)
    {
        initActivity(activity, bundle, 0);
    }

    default void initActivity(@NonNull Activity activity, @Nullable Bundle bundle, int flags)
    {
        Timber.d("initActivity()");
        if (getAcToGo() == null) {
            return;
        }
        Intent intent = new Intent(activity, getAcToGo());
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        if (flags > 0) {
            intent.setFlags(flags);
        }
        activity.startActivity(intent);
    }
}
