package com.didekinaar.usuario;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.didekinaar.R;

import timber.log.Timber;

public class RegUserFr extends Fragment {

    private View mRegUsuarioFrView;

    public RegUserFr()
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
        mRegUsuarioFrView = inflater.inflate(R.layout.reg_user_fr, container, false);
        return mRegUsuarioFrView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Timber.d("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart()
    {
        Timber.d("onStart()");
        super.onStart();
    }

    @Override
    public void onResume()
    {
        Timber.d("onResume()");
        super.onResume();
    }

    @Override
    public void onPause()
    {
        Timber.d("onPause()");
        super.onPause();
    }

    @Override
    public void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
    }

    @Override
    public void onDestroyView()
    {
        Timber.d("onDestroyView()");
        super.onDestroyView();
    }

    @Override
    public void onDestroy()
    {
        Timber.d("onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onDetach()
    {
        Timber.d("onDetach()");
        super.onDetach();
    }

    //    ......... GETTER/ SETTER METHODS ..........

    public View getFragmentView()
    {
        Timber.d("getFragmentView()");
        return mRegUsuarioFrView;
    }

//  ===================== STATIC HELPER METHODS ==========================

    public static UsuarioBean makeUserBeanFromRegUserFrView(View usuarioRegView)
    {
        return new UsuarioBean(
                ((EditText) usuarioRegView.findViewById(R.id.reg_usuario_email_editT)).getText()
                        .toString(),
                ((EditText) usuarioRegView.findViewById(R.id.reg_usuario_alias_ediT)).getText()
                        .toString(),
                ((EditText) usuarioRegView.findViewById(R.id.reg_usuario_password_ediT)).getText()
                        .toString(),
                ((EditText) usuarioRegView.findViewById(R.id.reg_usuario_password_confirm_ediT)).getText()
                        .toString()
        );
    }

}
