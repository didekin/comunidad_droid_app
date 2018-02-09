package com.didekindroid.lib_one.api.router;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * User: pedro@didekin
 * Date: 17/11/2017
 * Time: 15:54
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface ActivityInitiatorIf {

    ActivityRouterIf getRouter();

    void initFromMenuItem(@NonNull Activity activity, @Nullable Bundle bundle, int resourceId);

    void initFromLeadWithFlag(@NonNull Activity activity, @Nullable Bundle bundle, @NonNull int flags);
}
