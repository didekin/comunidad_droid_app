package com.didekindroid.router;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 17/04/17
 * Time: 11:12
 */

public class FragmentInitiator<T extends Fragment> {

    private final AppCompatActivity activity;
    private final int containerId;

    public FragmentInitiator(AppCompatActivity activity)
    {
        this.activity = activity;
        containerId = 0;
    }

    public FragmentInitiator(AppCompatActivity activity, int containerId)
    {
        this.activity = activity;
        this.containerId = containerId;
    }

    public void initReplaceFragmentTx(@Nullable Bundle bundle, @NonNull Fragment fragment, String fragmentTag)
    {
        Timber.d("initReplaceFragmentTx()");
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        activity.getSupportFragmentManager().beginTransaction()
                .replace(containerId, fragment, fragmentTag)
                .addToBackStack(fragment.getClass().getName())
                .commit();
    }

    public void initFragmentTx(@NonNull Fragment fragment, String fragmentTag)
    {
        Timber.d("initFragmentTx()");
        activity.getSupportFragmentManager().beginTransaction()
                .add(containerId, fragment, fragmentTag)
                .commit();
    }

    @SuppressWarnings("unchecked")
    public T initFragmentById(Bundle bundle, int fragmentId)
    {
        Timber.d("initFragmentById()");
        T fragment = (T) activity.getSupportFragmentManager().findFragmentById(fragmentId);
        fragment.setArguments(bundle);
        return fragment;
    }
}
