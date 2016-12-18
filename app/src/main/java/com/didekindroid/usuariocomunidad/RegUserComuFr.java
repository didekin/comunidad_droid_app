package com.didekindroid.usuariocomunidad;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekinaar.R;
import com.didekinaar.usuario.UsuarioBean;
import com.didekindroid.comunidad.ComunidadBean;

import timber.log.Timber;

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

//  ===================== STATIC HELPER METHODS ==========================

    public static UsuarioComunidadBean makeUserComuBeanFromView(View usuarioComunidadRegView
            , ComunidadBean comunidadBean, UsuarioBean usuarioBean)
    {
        return new UsuarioComunidadBean(
                comunidadBean,
                usuarioBean,
                ((TextView) usuarioComunidadRegView.findViewById(R.id.reg_usercomu_portal_ed)).getText()
                        .toString(),
                ((TextView) usuarioComunidadRegView.findViewById(R.id.reg_usercomu_escalera_ed)).getText()
                        .toString(),
                ((TextView) usuarioComunidadRegView.findViewById(R.id.reg_usercomu_planta_ed)).getText()
                        .toString(),
                ((TextView) usuarioComunidadRegView.findViewById(R.id.reg_usercomu_puerta_ed)).getText()
                        .toString(),
                ((CheckBox) usuarioComunidadRegView.findViewById(R.id.reg_usercomu_checbox_pre))
                        .isChecked(),
                ((CheckBox) usuarioComunidadRegView.findViewById(R.id.reg_usercomu_checbox_admin))
                        .isChecked(),
                ((CheckBox) usuarioComunidadRegView.findViewById(R.id.reg_usercomu_checbox_pro))
                        .isChecked(),
                ((CheckBox) usuarioComunidadRegView.findViewById(R.id.reg_usercomu_checbox_inq))
                        .isChecked()
        );
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
                .setChecked(initUserComu.getRoles().contains(RolUi.PRE.function));
        ((CheckBox) mRegUserComuFrView.findViewById(R.id.reg_usercomu_checbox_admin))
                .setChecked(initUserComu.getRoles().contains(RolUi.ADM.function));
        ((CheckBox) mRegUserComuFrView.findViewById(R.id.reg_usercomu_checbox_pro))
                .setChecked(initUserComu.getRoles().contains(RolUi.PRO.function));
        ((CheckBox) mRegUserComuFrView.findViewById(R.id.reg_usercomu_checbox_inq))
                .setChecked(initUserComu.getRoles().contains(RolUi.INQ.function));
    }
}


