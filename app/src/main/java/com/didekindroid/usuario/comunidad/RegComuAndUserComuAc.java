package com.didekindroid.usuario.comunidad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.didekindroid.R;
import com.didekindroid.common.ui.CommonPatterns;
import com.didekindroid.common.ConnectionUtils;
import com.didekindroid.common.ui.UIutils;
import com.didekindroid.usuario.comunidad.dominio.ComunidadBean;
import com.didekindroid.usuario.comunidad.dominio.UsuarioComunidadBean;

import static com.didekindroid.usuario.common.UserIntentExtras.USUARIO_COMUNIDAD_REG;
import static com.didekindroid.usuario.common.UsuarioComunidadFiller.makeComunidadBeanFromView;
import static com.didekindroid.usuario.common.UsuarioComunidadFiller.makeUsuarioComunidadBeanFromView;

public class RegComuAndUserComuAc extends Activity {

    private static final String TAG = RegComuAndUserComuAc.class.getCanonicalName();

    RegComuFr mRegComuFrg;
    RegUserComuFr mRegUserComuFrg;
    private Button mRegistroButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_comunidad_and_usuariocomunidad_ac);

        mRegComuFrg = (RegComuFr) getFragmentManager().findFragmentById(R.id.reg_comunidad_frg);
        mRegUserComuFrg = (RegUserComuFr) getFragmentManager().findFragmentById(R.id
                .reg_usuariocomunidad_frg);

        mRegistroButton = (Button) findViewById(R.id.reg_comu_usuariocomunidad_button);

        mRegistroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "View.OnClickListener().onClick()");
                registerComuAndUsuarioComu();
            }
        });
    }

    private void registerComuAndUsuarioComu()
    {
        Log.d(TAG, "registerComuAndUsuarioComu()");

        ComunidadBean comunidadBean = mRegComuFrg.getComunidadBean();
        makeComunidadBeanFromView(mRegComuFrg.getFragmentView(), comunidadBean);
        UsuarioComunidadBean usuarioComunidadBean = makeUsuarioComunidadBeanFromView(mRegUserComuFrg
                .getFragmentView(), comunidadBean, null);

        // Validation of data.
        StringBuilder errorMsg = new StringBuilder(getResources().getText(R.string.error_validation_msg))
                .append(CommonPatterns.LINE_BREAK.literal);

        if (!usuarioComunidadBean.validate(getResources(), errorMsg)) {
            UIutils.makeToast(this, errorMsg.toString());

        } else if (ConnectionUtils.isInternetConnected(this)) {
            UIutils.makeToast(this, R.string.no_internet_conn_toast);
        } else {
            Intent intent = new Intent(this, ComusByUserListAc.class);
            intent.putExtra(USUARIO_COMUNIDAD_REG.toString(),usuarioComunidadBean.getUsuarioComunidad());
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy()
    {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }
}

