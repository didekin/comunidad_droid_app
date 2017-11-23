package com.didekindroid.router;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.didekindroid.api.RouterListener;

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

    default void initAcFromMenu(int resourceId)
    {
        Timber.d("initAcFromMenu()");
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            intent = new Intent();
        }
        intent.setClass(getActivity(), getRouter().nextActivityFromMn(resourceId));
        getActivity().startActivity(intent);
    }

    default void initAcFromListener(@Nullable Bundle bundle, RouterListener listener)
    {
        Timber.d("initAcFromListener()");
        Intent intent = new Intent(getActivity(), listener.getActivityToGo());
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
