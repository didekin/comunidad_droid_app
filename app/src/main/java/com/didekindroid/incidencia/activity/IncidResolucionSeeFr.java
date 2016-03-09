package com.didekindroid.incidencia.activity;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.didekindroid.R;

/**
 * User: pedro@didekin
 * Date: 13/11/15
 * Time: 15:52
 */
public class IncidResolucionSeeFr extends Fragment {

    private static final String TAG = IncidResolucionSeeFr.class.getCanonicalName();

    View mFragmentView;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        Log.d(TAG, "onAttach()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView()");
        mFragmentView = inflater.inflate(R.layout.incid_resolucion_see_fr, container, false);

        return mFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    public ResolucionBean makeResolucionBeanFromView(StringBuilder errorMsg)
    {
        Log.d(TAG, "makeResolucionBeanFromView()");
        return null;
    }

//    ============================== INTERFACE COMMUNICATIONS METHODS ==========================


//    ============================================================================================
//    .................................... INNER CLASSES .................................
//    ============================================================================================

    class ResolucionBean {


    }
}
