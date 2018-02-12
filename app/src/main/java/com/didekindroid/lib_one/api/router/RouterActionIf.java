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
 */
@FunctionalInterface
public interface RouterActionIf {

    Class<? extends Activity> getAcToGo();

    default void initActivity(@NonNull Activity activity)
    {
        Timber.d("initActivity(), default one parameter");
        initActivity(activity, null);
    }

    default void initActivity(@NonNull Activity activity, @Nullable Bundle bundle)
    {
        Timber.d("initActivity(), default two parameters");
        initActivity(activity, bundle, 0);
    }

    default void initActivity(@NonNull Activity activity, @Nullable Bundle bundle, int flags)
    {
        Timber.d("initActivity(), default three parameters");
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
