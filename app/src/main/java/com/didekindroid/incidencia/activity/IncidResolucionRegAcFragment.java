package com.didekindroid.incidencia.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.didekindroid.R;

/**
 * User: pedro@didekin
 * Date: 13/11/15
 * Time: 15:52
 */
public class IncidResolucionRegAcFragment extends Fragment {

    public IncidResolucionRegAcFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.incid_resolucion_reg_frg, container, false);
    }

}
