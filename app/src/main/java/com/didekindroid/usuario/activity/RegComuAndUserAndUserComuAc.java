package com.didekindroid.usuario.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.didekin.security.OauthToken.AccessToken;
import com.didekin.serviceone.domain.Comunidad;
import com.didekin.serviceone.domain.Usuario;
import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.ioutils.ConnectionUtils;
import com.didekindroid.uiutils.CommonPatterns;
import com.didekindroid.uiutils.UIutils;
import com.didekindroid.usuario.dominio.ComunidadBean;
import com.didekindroid.usuario.dominio.UsuarioBean;
import com.didekindroid.usuario.dominio.UsuarioComunidadBean;
import com.didekindroid.usuario.security.TokenHandler;

import static com.didekindroid.uiutils.UIutils.updateIsRegistered;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.*;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.USUARIO_COMUNIDAD_REG;
import static com.didekindroid.usuario.security.TokenHandler.TKhandler;
import static com.didekindroid.usuario.webservices.Oauth2Service.Oauth2;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;

/**
 * Preconditions:
 * 1. The user is not registered.
 * 2. The comunidad has not been registered either, by other users.
 * 3. There is not extras in the activity intent.
 */
public class RegComuAndUserAndUserComuAc extends Activity {

    private static final String TAG = RegComuAndUserAndUserComuAc.class.getCanonicalName();

    RegComuFr mRegComuFrg;
    RegUserComuFr mRegUserComuFrg;
    RegUserFr mRegUserFr;
    private Button mRegistroButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        setContentView(R.layout.reg_comu_and_user_and_usercomu_ac);
        mRegComuFrg = (RegComuFr) getFragmentManager().findFragmentById(R.id.reg_comunidad_frg);
        mRegUserComuFrg = (RegUserComuFr) getFragmentManager().findFragmentById(R.id
                .reg_usuariocomunidad_frg);
        mRegUserFr = (RegUserFr) getFragmentManager().findFragmentById(R.id.reg_usuario_frg);
        mRegistroButton = (Button) findViewById(R.id.reg_com_usuario_usuariocomu_button);

        mRegistroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "View.OnClickListener().onClick()");
                registerComuAndUserComuAndUser();
            }
        });
    }

    private void registerComuAndUserComuAndUser()
    {
        Log.d(TAG, "registerComuAndUsuarioComu()");

        ComunidadBean comunidadBean = mRegComuFrg.getComunidadBean();
        makeComunidadBeanFromView(mRegComuFrg.getFragmentView(), comunidadBean);
        UsuarioBean usuarioBean = makeUsuarioBeanFromView(mRegUserFr.getFragmentView());
        UsuarioComunidadBean usuarioComunidadBean = makeUsuarioComunidadBeanFromView(mRegUserComuFrg
                .getFragmentView(), comunidadBean, usuarioBean);

        // Validation of data.
        StringBuilder errorMsg = new StringBuilder(getResources().getText(R.string.error_validation_msg))
                .append(CommonPatterns.LINE_BREAK.literal);

        if (!usuarioComunidadBean.validate(getResources(), errorMsg)) {
            UIutils.makeToast(this, errorMsg.toString());
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            UIutils.makeToast(this, R.string.no_internet_conn_toast);
        } else {
            new ComuAndUserComuAndUserRegister().execute(usuarioComunidadBean.getUsuarioComunidad());
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

    private class ComuAndUserComuAndUserRegister extends AsyncTask<UsuarioComunidad, Void, Void> {

        @Override
        protected Void doInBackground(UsuarioComunidad... usuarioComunidad)
        {
            Log.d(TAG, "ComuAndUserComuAndUserRegister.doInBackground()");
            Usuario newUser = usuarioComunidad[0].getUsuario();
            Log.d(TAG, "ComuAndUserComuAndUserRegister.doInBackground(): calling ServOne.regComuAndUserAndUserComu()");
            ServOne.regComuAndUserAndUserComu(usuarioComunidad[0]);
            Log.d(TAG, "ComuAndUserComuAndUserRegister.doInBackground(): calling Oauth2.getPasswordUserToken()");
            AccessToken token = Oauth2.getPasswordUserToken(newUser.getUserName(), newUser.getPassword());
            TKhandler.initKeyCacheAndBackupFile(token);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            Log.d(TAG, "RegComuAndUserComuHttp.onPostExecute()");
            Intent intent = new Intent(RegComuAndUserAndUserComuAc.this, SeeUserComuByUserAc.class);
            startActivity(intent);
            updateIsRegistered(true, RegComuAndUserAndUserComuAc.this);
        }
    }
}
