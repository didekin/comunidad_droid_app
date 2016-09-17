package com.didekindroid.usuario.activity;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;

import timber.log.Timber;

import static com.didekindroid.usuario.activity.utils.RolUi.ADM;
import static com.didekindroid.usuario.activity.utils.RolUi.INQ;
import static com.didekindroid.usuario.activity.utils.RolUi.PRE;
import static com.didekindroid.usuario.activity.utils.RolUi.PRO;

public class RegUserComuFr extends Fragment {

    private View mRegUserComuFrView;

    public RegUserComuFr()
    {
    }

    @Override
    public void onAttach(Context context)
    {
        Timber.d("onAttach()");
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        mRegUserComuFrView = inflater.inflate(R.layout.reg_usercomu_fr, container, false);
        return mRegUserComuFrView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Timber.d("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
    }

//    ........... AUXILIARY METHODS ...........

    public View getFragmentView()
    {
        Timber.d("getFragmentView()");
        return mRegUserComuFrView;
    }

    void paintUserComuView(UsuarioComunidad initUserComu)
    {
        Timber.d("paintUserComuView()");

        ((EditText) mRegUserComuFrView.findViewById(R.id.reg_usercomu_portal_ed)).setText(initUserComu.getPortal());
        ((EditText) mRegUserComuFrView.findViewById(R.id.reg_usercomu_escalera_ed)).setText(initUserComu.getEscalera());
        ((EditText) mRegUserComuFrView.findViewById(R.id.reg_usercomu_planta_ed)).setText(initUserComu.getPlanta());
        ((EditText) mRegUserComuFrView.findViewById(R.id.reg_usercomu_puerta_ed)).setText(initUserComu.getPuerta());

        ((CheckBox) mRegUserComuFrView.findViewById(R.id.reg_usercomu_checbox_pre))
                .setChecked(initUserComu.getRoles().contains(PRE.function));
        ((CheckBox) mRegUserComuFrView.findViewById(R.id.reg_usercomu_checbox_admin))
                .setChecked(initUserComu.getRoles().contains(ADM.function));
        ((CheckBox) mRegUserComuFrView.findViewById(R.id.reg_usercomu_checbox_pro))
                .setChecked(initUserComu.getRoles().contains(PRO.function));
        ((CheckBox) mRegUserComuFrView.findViewById(R.id.reg_usercomu_checbox_inq))
                .setChecked(initUserComu.getRoles().contains(INQ.function));
    }
}


