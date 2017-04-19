package com.didekindroid.router;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 17/04/17
 * Time: 11:12
 */

public class FragmentInitiator {

    private final AppCompatActivity activity;
    private final int containerId;

    public FragmentInitiator(AppCompatActivity activity, int containerId)
    {
        this.activity = activity;
        this.containerId = containerId;
    }

    public void initFragment(@NonNull Bundle bundle, Fragment fragment, String fragmentTag)
    {
        {
            Timber.d("initActivityWithBundle()");
            fragment.setArguments(bundle);
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(containerId, fragment, fragmentTag)
                    .addToBackStack(fragment.getClass().getName())
                    .commit();
        }
    }
}
