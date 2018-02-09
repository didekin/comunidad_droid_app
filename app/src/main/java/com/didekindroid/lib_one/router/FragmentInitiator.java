package com.didekindroid.lib_one.router;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.didekindroid.lib_one.api.router.FragmentInitiatorIf;

/**
 * User: pedro@didekin
 * Date: 17/04/17
 * Time: 11:12
 */

public class FragmentInitiator<T extends Fragment> implements FragmentInitiatorIf<T> {

    private final AppCompatActivity activity;
    private final int containerId;

    FragmentInitiator(AppCompatActivity activity)
    {
        this.activity = activity;
        containerId = 0;
    }

    FragmentInitiator(AppCompatActivity activity, int containerId)
    {
        this.activity = activity;
        this.containerId = containerId;
    }

    public AppCompatActivity getActivity()
    {
        return activity;
    }

    public int getContainerId()
    {
        return containerId;
    }
}
