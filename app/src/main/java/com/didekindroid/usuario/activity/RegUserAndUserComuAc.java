package com.didekindroid.usuario.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.didekin.security.OauthToken;
import com.didekin.serviceone.domain.Comunidad;
import com.didekin.serviceone.domain.Usuario;
import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.ioutils.ConnectionUtils;
import com.didekindroid.uiutils.UIutils;
import com.didekindroid.usuario.dominio.ComunidadBean;
import com.didekindroid.usuario.dominio.UsuarioBean;
import com.didekindroid.usuario.dominio.UsuarioComunidadBean;

import static com.didekindroid.uiutils.UIutils.getErrorMsgBuilder;
import static com.didekindroid.uiutils.UIutils.isRegisteredUser;
import static com.didekindroid.uiutils.UIutils.updateIsRegistered;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeUsuarioBeanFromRegUserFrView;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeUsuarioComunidadBeanFromView;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.COMUNIDAD_ID;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.usuario.security.TokenHandler.TKhandler;
import static com.didekindroid.usuario.webservices.Oauth2Service.Oauth2;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static com.google.common.base.Preconditions.checkState;

/**
 * User: pedro@didekin
 * Date: 11/05/15
 * Time: 19:13
 */

/**
 * Preconditions:
 * 1. The user is not registered.
 * 2. The activity receives a comunidad object, as an intent extra, with the following fields:
 * -- comunidadId.
 * -- nombreComunidad (with tipoVia,nombreVia, numero and sufijoNumero).
 * -- municipio, with codInProvincia and nombre.
 * -- provincia, with provinciaId and nombre.
 * The comunidad already exists in BD.
 * <p/>
 * Postconditions:
 * 1. A long comunidadId is passed as an intent extra.
 * 2. The activity SeeUserComuByComuAc is started.
 */
public class RegUserAndUserComuAc extends Activity {
    private static final String TAG = RegComuAndUserAndUserComuAc.class.getCanonicalName();

    RegUserComuFr mRegUserComuFrg;
    RegUserFr mRegUserFr;
    private Comunidad mComunidad;
    private Button mRegistroButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        // Preconditions.
        checkState(!isRegisteredUser(this));
        mComunidad = (Comunidad) getIntent().getExtras()
                .getSerializable(COMUNIDAD_LIST_OBJECT.extra);

        setContentView(R.layout.reg_user_and_usercomu_ac);
        mRegUserComuFrg = (RegUserComuFr) getFragmentManager().findFragmentById(R.id
                .reg_usercomu_frg);
        mRegUserFr = (RegUserFr) getFragmentManager().findFragmentById(R.id.reg_user_frg);
        mRegistroButton = (Button) findViewById(R.id.reg_user_usercomu_button);

        mRegistroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "View.OnClickListener().onClick()");
                registerUserAndUserComu();
            }
        });
    }

    private void registerUserAndUserComu()
    {
        Log.d(TAG, "registerComuAndUsuarioComu()");

        UsuarioBean usuarioBean = makeUsuarioBeanFromRegUserFrView(mRegUserFr.getFragmentView());
        UsuarioComunidadBean usuarioComunidadBean = makeUsuarioComunidadBeanFromView(
                mRegUserComuFrg.getFragmentView(),
                new ComunidadBean(mComunidad.getC_Id(), null, null, null, null, null),
                usuarioBean);

        StringBuilder errorBuilder =  getErrorMsgBuilder(this);

        if (!usuarioComunidadBean.validate(getResources(), errorBuilder)) {
            UIutils.makeToast(this, errorBuilder.toString());
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            UIutils.makeToast(this, R.string.no_internet_conn_toast);
        } else {
            new UserAndUserComuRegister().execute(usuarioComunidadBean.getUsuarioComunidad());
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

    private class UserAndUserComuRegister extends AsyncTask<UsuarioComunidad, Void, Void> {

        @Override
        protected Void doInBackground(UsuarioComunidad... usuarioComunidad)
        {
            Log.d(TAG, "UserAndUserComuRegister.doInBackground()");
            Usuario newUser = usuarioComunidad[0].getUsuario();
            Log.d(TAG, "UserAndUserComuRegister.doInBackground(): calling ServOne.regComuAndUserAndUserComu()");
            ServOne.regUserAndUserComu(usuarioComunidad[0]);
            Log.d(TAG, "UserAndUserComuRegister.doInBackground(): calling Oauth2.getPasswordUserToken()");
            OauthToken.AccessToken token = Oauth2.getPasswordUserToken(newUser.getUserName(), newUser.getPassword());
            TKhandler.initKeyCacheAndBackupFile(token);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            Log.d(TAG, "UserAndUserComuRegister.onPostExecute()");
            Intent intent = new Intent(RegUserAndUserComuAc.this, SeeUserComuByComuAc.class);
            intent.putExtra(COMUNIDAD_ID.extra, mComunidad.getC_Id());
            startActivity(intent);
            updateIsRegistered(true, RegUserAndUserComuAc.this);
        }
    }
}
