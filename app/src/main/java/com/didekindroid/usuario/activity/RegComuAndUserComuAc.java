package com.didekindroid.usuario.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.ioutils.ConnectionUtils;
import com.didekindroid.uiutils.CommonPatterns;
import com.didekindroid.uiutils.UIutils;
import com.didekindroid.usuario.dominio.ComunidadBean;
import com.didekindroid.usuario.dominio.UsuarioComunidadBean;

import static com.didekindroid.uiutils.UIutils.makeToast;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeComunidadBeanFromView;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeUserComuBeanFromView;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static com.google.common.base.Preconditions.checkState;

/**
 * Preconditions:
 * 1. The user is registered with a different comunidad.
 */
public class RegComuAndUserComuAc extends Activity {

    private static final String TAG = RegComuAndUserComuAc.class.getCanonicalName();

    RegComuFr mRegComuFrg;
    RegUserComuFr mRegUserComuFrg;
    private Button mRegistroButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_comu_and_usercomu_ac);

        mRegComuFrg = (RegComuFr) getFragmentManager().findFragmentById(R.id.reg_comunidad_frg);
        mRegUserComuFrg = (RegUserComuFr) getFragmentManager().findFragmentById(R.id
                .reg_usercomu_frg);

        mRegistroButton = (Button) findViewById(R.id.reg_comu_usuariocomunidad_button);

        mRegistroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "View.OnClickListener().onClick()");
                registerComuAndUserComu();
            }
        });
    }

    private void registerComuAndUserComu()
    {
        Log.d(TAG, "registerComuAndUserComu()");

        ComunidadBean comunidadBean = mRegComuFrg.getComunidadBean();
        makeComunidadBeanFromView(mRegComuFrg.getFragmentView(), comunidadBean);
        UsuarioComunidadBean usuarioComunidadBean = makeUserComuBeanFromView(mRegUserComuFrg
                .getFragmentView(), comunidadBean, null);

        // Validation of data.
        StringBuilder errorMsg = new StringBuilder(getResources().getText(R.string.error_validation_msg))
                .append(CommonPatterns.LINE_BREAK.literal);

        if (!usuarioComunidadBean.validate(getResources(), errorMsg)) {
            makeToast(this, errorMsg.toString());
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            makeToast(this, R.string.no_internet_conn_toast);
        } else {
            new ComuAndUserComuRegister().execute(usuarioComunidadBean.getUsuarioComunidad());
            Intent intent = new Intent(this, SeeUserComuByUserAc.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy()
    {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    private class ComuAndUserComuRegister extends AsyncTask<UsuarioComunidad, Void, Boolean> {

        private final String TAG = ComuAndUserComuRegister.class.getCanonicalName();

        @Override
        protected Boolean doInBackground(UsuarioComunidad... usuarioComunidad)
        {
            Log.d(TAG, "doInBackground()");
            return ServOne.regComuAndUserComu(usuarioComunidad[0]);
        }

        @Override
        protected void onPostExecute(Boolean rowInserted)
        {
            Log.d(TAG, "onPostExecute()");
            checkState(rowInserted);
        }
    }
}

