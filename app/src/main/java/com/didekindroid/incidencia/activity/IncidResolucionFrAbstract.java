package com.didekindroid.incidencia.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.didekindroid.common.activity.FechaPickerUser;
import com.didekindroid.incidencia.dominio.ResolucionBean;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 10/03/16
 * Time: 14:25
 */
@SuppressWarnings("AbstractClassExtendsConcreteClass")
public abstract class IncidResolucionFrAbstract extends Fragment implements FechaPickerUser {

    ResolucionBean mResolucionBean;
    View mFragmentView;
    TextView mFechaView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Timber.d("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
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
