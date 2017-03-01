package com.didekindroid.incidencia.resolucion;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.didekindroid.util.FechaPickerUser;

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
