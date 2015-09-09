package com.didekindroid.usuario.activity;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.didekin.serviceone.domain.Comunidad;
import com.didekindroid.R;

import static com.didekindroid.usuario.activity.utils.UserIntentExtras.COMUNIDAD_LIST_OBJECT;

/**
 * Preconditions:
 * 1. The activity associated receives a comunidad object, as an intent extra, with the following fields:
 * -- comunidadId.
 * -- nombreComunidad (with tipoVia,nombreVia, numero and sufijoNumero).
 * -- municipio, with codInProvincia and nombre.
 * -- provincia, with provinciaId and nombre.
 */
public class RegUserComuFr extends Fragment {

    private static final String TAG = RegUserComuFr.class.getCanonicalName();
    private View mRegUserComuFrView;
    private Comunidad mComunidad;

    public RegUserComuFr()
    {
    }

    @Override
    public void onAttach(Context context)
    {
        Log.d(TAG, "onAttach()");
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView()");

        mRegUserComuFrView = inflater.inflate(R.layout.reg_usercomu_fr, container, false);
        return mRegUserComuFrView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Log.d(TAG, "onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
        mComunidad = (Comunidad) getActivity().getIntent().getExtras()
                .getSerializable(COMUNIDAD_LIST_OBJECT.extra);
    }

    @Override
    public void onStart()
    {
        Log.d(TAG, "onStart()");
        super.onStart();
    }

    @Override
    public void onResume()
    {
        Log.d(TAG, "onResume()");
        super.onResume();
    }

    @Override
    public void onPause()
    {
        Log.d(TAG, "onPause()");
        super.onPause();
    }

    @Override
    public void onStop()
    {
        Log.d(TAG, "onStop()");
        super.onStop();
    }

    @Override
    public void onDestroyView()
    {
        Log.d(TAG, "onDestroyView()");
        super.onDestroyView();
    }

    @Override
    public void onDestroy()
    {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onDetach()
    {
        Log.d(TAG, "onDetach()");
        super.onDetach();
    }

//    ........... AUXILIARY METHODS ...........

    public Comunidad getmComunidad()
    {
        return mComunidad;
    }

    public View getFragmentView()
    {
        Log.d(TAG, "getFragmentView()");
        return mRegUserComuFrView;
    }
}


