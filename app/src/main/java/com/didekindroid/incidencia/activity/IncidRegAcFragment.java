package com.didekindroid.incidencia.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.didekindroid.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class IncidRegAcFragment extends Fragment {

    public IncidRegAcFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.incid_reg_frg, container, false);
    }
}
