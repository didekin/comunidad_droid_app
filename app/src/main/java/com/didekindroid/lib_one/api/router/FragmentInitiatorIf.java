package com.didekindroid.lib_one.api.router;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 30/11/2017
 * Time: 11:00
 */

@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface FragmentInitiatorIf<T extends Fragment> {

    AppCompatActivity getActivity();

    default int getContainerId(){
        return 0;
    }

    default void initReplaceFragmentTx(@Nullable Bundle bundle, @NonNull T fragment)
    {
        Timber.d("initReplaceFragmentTx()");
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(getContainerId(), fragment, fragment.getClass().getName())
                .addToBackStack(fragment.getClass().getName())
                .commit();
    }

    default void initReplaceFragmentTx(@NonNull T fragment)
    {
        Timber.d("initReplaceFragmentTx()");
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(getContainerId(), fragment, fragment.getClass().getName())
                .addToBackStack(fragment.getClass().getName())
                .commit();
    }

    default void initFragmentTx(@NonNull T fragment)
    {
        Timber.d("initFragmentTx()");
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(getContainerId(), fragment, fragment.getClass().getName())
                .commit();
    }

    @SuppressWarnings("unchecked")
    default T initFragmentById(Bundle bundle, int fragmentId)
    {
        Timber.d("initFragmentById()");
        T fragment = (T) getActivity().getSupportFragmentManager().findFragmentById(fragmentId);
        fragment.setArguments(bundle);
        return fragment;
    }
}
