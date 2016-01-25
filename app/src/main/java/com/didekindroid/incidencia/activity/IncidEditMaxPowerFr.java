package com.didekindroid.incidencia.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.didekin.incidservice.domain.IncidenciaUser;
import com.didekindroid.R;

/**
 * User: pedro@didekin
 * Date: 22/01/16
 * Time: 16:16
 */
public class IncidEditMaxPowerFr extends Fragment {

    private static final String TAG = IncidEditMaxPowerFr.class.getCanonicalName();
    View mFragmentView;
    IncidenciaUser mIncidenciaUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView()");
        mFragmentView = inflater.inflate(R.layout.incid_edit_maxpower_fr, container, false);
        return mFragmentView;
    }
}
