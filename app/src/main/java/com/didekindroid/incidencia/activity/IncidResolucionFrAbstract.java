package com.didekindroid.incidencia.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.didekindroid.common.activity.FechaPickerUser;
import com.didekindroid.incidencia.dominio.ResolucionBean;

/**
 * User: pedro@didekin
 * Date: 10/03/16
 * Time: 14:25
 */
public abstract class IncidResolucionFrAbstract extends Fragment implements FechaPickerUser {

    private static final String TAG = IncidResolucionRegFr.class.getCanonicalName();

    ResolucionBean mResolucionBean;
    IncidenciaDataSupplier mActivitySupplier;
    View mFragmentView;
    TextView mFechaView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Log.d(TAG, "onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
        mActivitySupplier = (IncidenciaDataSupplier) getActivity();
    }

    //  ======================== INTERFACE COMMUNICATIONS METHODS ==========================

    @Override
    public View getFragmentView()
    {
        return mFragmentView;
    }

    @Override
    public TextView getFechaView()
    {
        return mFechaView;
    }

    @Override
    public ResolucionBean getBean()
    {
        return mResolucionBean;
    }
}
