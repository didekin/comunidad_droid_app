package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.common.utils.ConnectionUtils;
import com.didekindroid.usuario.dominio.ComunidadBean;
import com.didekindroid.usuario.dominio.UsuarioComunidadBean;

import timber.log.Timber;

import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.common.utils.UIutils.getErrorMsgBuilder;
import static com.didekindroid.common.utils.UIutils.makeToast;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeComunidadBeanFromView;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeUserComuBeanFromView;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static com.google.common.base.Preconditions.checkState;

/**
 * Preconditions:
 * 1. The user is registered with a different comunidad.
 */
@SuppressWarnings("ConstantConditions")
public class RegComuAndUserComuAc extends AppCompatActivity {

    private RegComuFr mRegComuFrg;
    private RegUserComuFr mRegUserComuFrg;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_comu_and_usercomu_ac);
        doToolBar(this, true);

        mRegComuFrg = (RegComuFr) getFragmentManager().findFragmentById(R.id.reg_comunidad_frg);
        mRegUserComuFrg = (RegUserComuFr) getFragmentManager().findFragmentById(R.id
                .reg_usercomu_frg);

        Button mRegistroButton = (Button) findViewById(R.id.reg_comu_usuariocomunidad_button);
        mRegistroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("View.OnClickListener().onClick()");
                registerComuAndUserComu();
            }
        });
    }

    private void registerComuAndUserComu()
    {
        Timber.d("registerComuAndUserComu()");

        ComunidadBean comunidadBean = mRegComuFrg.getComunidadBean();
        makeComunidadBeanFromView(mRegComuFrg.getFragmentView(), comunidadBean);
        UsuarioComunidadBean usuarioComunidadBean = makeUserComuBeanFromView(mRegUserComuFrg
                .getFragmentView(), comunidadBean, null);

        // Validation of data.
        StringBuilder errorMsg = getErrorMsgBuilder(this);

        if (!usuarioComunidadBean.validate(getResources(), errorMsg)) {
            makeToast(this, errorMsg.toString(), Toast.LENGTH_SHORT);
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            makeToast(this, R.string.no_internet_conn_toast, Toast.LENGTH_LONG);
        } else {
            new ComuAndUserComuRegister().execute(usuarioComunidadBean.getUsuarioComunidad());
            Intent intent = new Intent(this, SeeUserComuByUserAc.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy()
    {
        Timber.d("onDestroy()");
        super.onDestroy();
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    class ComuAndUserComuRegister extends AsyncTask<UsuarioComunidad, Void, Boolean> {

        UiException uiException;

        @Override
        protected Boolean doInBackground(UsuarioComunidad... usuarioComunidad)
        {
            Timber.d("doInBackground()");
            boolean isRegistered = false;
            try {
                isRegistered = ServOne.regComuAndUserComu(usuarioComunidad[0]);
            } catch (UiException e) {
                uiException = e;
            }
            return isRegistered;
        }

        @Override
        protected void onPostExecute(Boolean rowInserted)
        {
            Timber.d("onPostExecute()");
            if (uiException != null) {
                uiException.processMe(RegComuAndUserComuAc.this, new Intent());
            } else {
                checkState(rowInserted);
            }
        }
    }
}

