package com.didekindroid.usuario.comunidad;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.didekindroid.R;

public class RegUserFr extends Fragment {

    private static final String TAG = RegUserFr.class.getCanonicalName();
    private View mRegUsuarioFrView;

    public RegUserFr()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView()");
        mRegUsuarioFrView = inflater.inflate(R.layout.reg_usuario, container, false);
        return mRegUsuarioFrView;
    }

    public View getFragmentView()
    {
        Log.d(TAG, "getFragmentView()");
        return mRegUsuarioFrView;
    }


}
