package com.didekindroid.lib_one.api;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.didekindroid.R;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 20/06/17
 * Time: 12:29
 */

public class ListMockFr extends Fragment {

    View frView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedState)
    {
        Timber.d("onCreateView()");
        frView = inflater.inflate(R.layout.mock_list_fr, container, false);
        return frView;
    }
}
