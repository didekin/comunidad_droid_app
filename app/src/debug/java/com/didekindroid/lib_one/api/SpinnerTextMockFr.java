package com.didekindroid.lib_one.api;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.didekindroid.R;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 02/04/17
 * Time: 10:47
 */

public class SpinnerTextMockFr extends Fragment {

    View rootFrgView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedState)
    {
        Timber.d("onCreateView()");
        rootFrgView = inflater.inflate(R.layout.mock_spinners_texts_fr, container, false);
        return rootFrgView;
    }
}
