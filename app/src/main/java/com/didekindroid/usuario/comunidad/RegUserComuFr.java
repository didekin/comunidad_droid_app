package com.didekindroid.usuario.comunidad;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.didekindroid.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegUserComuFr extends Fragment {

    private static final String TAG = RegUserComuFr.class.getCanonicalName();
    private View mRegUsuarioComuFrView;

    public RegUserComuFr()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.d(TAG,"onCreateView()");
        mRegUsuarioComuFrView = inflater.inflate(R.layout.reg_usuariocomunidad,container,false);
        return mRegUsuarioComuFrView;
    }

    public View getFragmentView()
    {
        Log.d(TAG,"getFragmentView()");
        return mRegUsuarioComuFrView;
    }
}


