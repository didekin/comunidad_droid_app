package com.didekindroid.api.router;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import timber.log.Timber;

import static com.didekindroid.router.ActivityRouter.acRouter;

/**
 * User: pedro@didekin
 * Date: 17/11/2017
 * Time: 15:54
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface ActivityInitiatorIf {

    Activity getActivity();

    default ActivityRouterIf getRouter()
    {
        Timber.d("getRouter()");
        return acRouter;
    }

    default void initAcFromMenu(@Nullable Bundle bundle, int resourceId)
    {
        Timber.d("initAcFromMenu()");
        Intent intent = new Intent(getActivity(), getRouter().nextActivityFromMn(resourceId));
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        getActivity().startActivity(intent);
    }

    default void initAcFromRouter(@Nullable Bundle bundle, IntrospectRouterToAcIf router)
    {
        Timber.d("initAcFromRouter()");
        Intent intent = new Intent(getActivity(), router.getActivityToGo());
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        getActivity().startActivity(intent);
    }

    default void initAcFromActivity(@Nullable Bundle bundle)
    {
        Timber.d("initAcFromActivity()");
        Intent intent = new Intent(getActivity(), getRouter().nextActivity(getActivity().getClass()));
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        getActivity().startActivity(intent);
    }

    default void initAcWithFlag(@Nullable Bundle bundle, @NonNull int flags)
    {
        Intent intent = new Intent(getActivity(), getRouter().nextActivity(getActivity().getClass()));
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.setFlags(flags);
        getActivity().startActivity(intent);
    }
}
