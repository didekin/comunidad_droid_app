package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.didekin.common.exception.ErrorBean;
import com.didekin.oauth2.OauthToken.AccessToken;
import com.didekin.usuario.dominio.Usuario;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.common.utils.ConnectionUtils;
import com.didekindroid.common.utils.UIutils;
import com.didekindroid.usuario.dominio.ComunidadBean;
import com.didekindroid.usuario.dominio.UsuarioBean;
import com.didekindroid.usuario.dominio.UsuarioComunidadBean;

import java.io.IOException;

import static com.didekin.common.dominio.DataPatterns.LINE_BREAK;
import static com.didekindroid.common.TokenHandler.TKhandler;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.common.utils.UIutils.updateIsRegistered;
import static com.didekindroid.common.webservices.Oauth2Service.Oauth2;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeComunidadBeanFromView;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeUserBeanFromRegUserFrView;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeUserComuBeanFromView;
import static com.didekindroid.usuario.activity.utils.UserMenu.LOGIN_AC;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Preconditions:
 * 1. The user is not registered.
 * 2. The comunidad has not been registered either, by other users.
 * 3. There is not extras in the activity intent.
 */
public class RegComuAndUserAndUserComuAc extends AppCompatActivity {

    private static final String TAG = RegComuAndUserAndUserComuAc.class.getCanonicalName();

    RegComuFr mRegComuFrg;
    RegUserComuFr mRegUserComuFrg;
    RegUserFr mRegUserFr;

    // TODO: añadir un campo de número de vecinos en la comunidad (aprox.).
    // TODO: recoger dato de localización en el alta. Control de altas masivas y remotas. Excluir Administrador.

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        setContentView(R.layout.reg_comu_and_user_and_usercomu_ac);
        doToolBar(this, true);

        mRegComuFrg = (RegComuFr) getFragmentManager().findFragmentById(R.id.reg_comunidad_frg);
        mRegUserComuFrg = (RegUserComuFr) getFragmentManager().findFragmentById(R.id
                .reg_usercomu_frg);
        mRegUserFr = (RegUserFr) getFragmentManager().findFragmentById(R.id.reg_user_frg);

        Button mRegistroButton = (Button) findViewById(R.id.reg_com_usuario_usuariocomu_button);
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
        UsuarioBean usuarioBean = makeUserBeanFromRegUserFrView(mRegUserFr.getFragmentView());
        UsuarioComunidadBean usuarioComunidadBean = makeUserComuBeanFromView(mRegUserComuFrg
                .getFragmentView(), comunidadBean, usuarioBean);

        // Validation of data.
        StringBuilder errorMsg = new StringBuilder(getResources().getText(R.string.error_validation_msg))
                .append(LINE_BREAK.getRegexp());

        if (!usuarioComunidadBean.validate(getResources(), errorMsg)) {
            UIutils.makeToast(this, errorMsg.toString(), Toast.LENGTH_SHORT);
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            UIutils.makeToast(this, R.string.no_internet_conn_toast, Toast.LENGTH_LONG);
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
//    ..... ACTION BAR ....
//    ============================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.d(TAG, "onCreateOptionsMenu()");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.reg_user_activities_mn, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.d(TAG, "onOptionsItemSelected()");

        int resourceId = checkNotNull(item.getItemId());

        switch (resourceId) {
            case R.id.login_ac_mn:
                LOGIN_AC.doMenuItem(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    class ComuAndUserComuAndUserRegister extends AsyncTask<UsuarioComunidad, Void, Void> {

        UiException uiException;

        @Override
        protected Void doInBackground(UsuarioComunidad... usuarioComunidad)
        {
            Log.d(TAG, "ComuAndUserComuAndUserRegister.doInBackground()");
            Usuario newUser = usuarioComunidad[0].getUsuario();

            try {
                ServOne.regComuAndUserAndUserComu(usuarioComunidad[0]).execute();
                AccessToken token = Oauth2.getPasswordUserToken(newUser.getUserName(), newUser.getPassword());
                TKhandler.initKeyCacheAndBackupFile(token);
            }catch (IOException e) {
                uiException = new UiException(ErrorBean.GENERIC_ERROR);
                return null;
            } catch (UiException e) {
                uiException = e;
            }
            return null;

            // TODO: si la comunidad ya existe y el userComu no, hacer un regUserAndUserComu.
            // TODO: si el userComu existe y la comunidad no, hacer un regComuAndUserComu.
            // TODO: si existen ambos, pero el userComu no pertenece a la comunidad, hacer un RegUserComu.
            // TODO: algo similar hay que hacer en el resto de acciones de registro.
            // TODO: hay que validar que una comunidad no tiene más de un presidente o más de un administrador.
            // TODO: relajar la restricción de solo un userComu por vivienda. Está en el modelo lógico.
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            Log.d(TAG, "RegComuAndUserComuHttp.onPostExecute()");

            if (uiException != null) {
                uiException.processMe(RegComuAndUserAndUserComuAc.this, new Intent());
            } else {
                Intent intent = new Intent(RegComuAndUserAndUserComuAc.this, SeeUserComuByUserAc.class);
                startActivity(intent);
                updateIsRegistered(true, RegComuAndUserAndUserComuAc.this);
            }
        }
    }
}
