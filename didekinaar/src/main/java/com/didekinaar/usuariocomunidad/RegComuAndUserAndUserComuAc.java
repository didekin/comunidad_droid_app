package com.didekinaar.usuariocomunidad;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.didekin.common.exception.ErrorBean;
import com.didekin.oauth2.SpringOauthToken;
import com.didekin.usuario.Usuario;
import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekinaar.R;
import com.didekinaar.comunidad.ComuSearchAc;
import com.didekinaar.comunidad.ComunidadBean;
import com.didekinaar.comunidad.RegComuFr;
import com.didekinaar.exception.UiAarException;
import com.didekinaar.usuario.RegUserFr;
import com.didekinaar.usuario.UserMenu;
import com.didekinaar.usuario.UsuarioBean;
import com.didekinaar.utils.ConnectionUtils;

import java.io.IOException;

import timber.log.Timber;

import static com.didekin.common.dominio.ValidDataPatterns.LINE_BREAK;
import static com.didekinaar.security.Oauth2Service.Oauth2;
import static com.didekinaar.security.TokenHandler.TKhandler;
import static com.didekinaar.usuariocomunidad.AarUserComuService.AarUserComuServ;
import static com.didekinaar.utils.UIutils.doToolBar;
import static com.didekinaar.utils.UIutils.makeToast;
import static com.didekinaar.utils.UIutils.updateIsRegistered;

/**
 * Preconditions:
 * 1. The user is not registered.
 * 2. The comunidad has not been registered either, by other users.
 * 3. There is not extras in the activity intent.
 */
@SuppressWarnings("ConstantConditions")
public class RegComuAndUserAndUserComuAc extends AppCompatActivity {

    private RegComuFr mRegComuFrg;
    private RegUserComuFr mRegUserComuFrg;
    private RegUserFr mRegUserFr;

    // TODO: añadir un campo de número de vecinos en la comunidad (aprox.).

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate()");

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
                Timber.d("View.OnClickListener().onClick()");
                registerComuAndUserComuAndUser();
            }
        });
    }

    void registerComuAndUserComuAndUser()
    {
        Timber.d("registerComuAndUsuarioComu()");

        ComunidadBean comunidadBean = mRegComuFrg.getComunidadBean();
        UserAndComuFiller.makeComunidadBeanFromView(mRegComuFrg.getFragmentView(), comunidadBean);
        UsuarioBean usuarioBean = UserAndComuFiller.makeUserBeanFromRegUserFrView(mRegUserFr.getFragmentView());
        UsuarioComunidadBean usuarioComunidadBean = UserAndComuFiller.makeUserComuBeanFromView(mRegUserComuFrg
                .getFragmentView(), comunidadBean, usuarioBean);

        // Validation of data.
        StringBuilder errorMsg = new StringBuilder(getResources().getText(R.string.error_validation_msg))
                .append(LINE_BREAK.getRegexp());

        if (!usuarioComunidadBean.validate(getResources(), errorMsg)) {
            makeToast(this, errorMsg.toString(), com.didekinaar.R.color.deep_purple_100);
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            makeToast(this, R.string.no_internet_conn_toast);
        } else {
            new ComuAndUserComuAndUserRegister().execute(usuarioComunidadBean.getUsuarioComunidad());
        }
    }

    @Override
    protected void onDestroy()
    {
        Timber.d("onDestroy()");
        super.onDestroy();
    }

//    ============================================================
//    ..... ACTION BAR ....
//    ============================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Timber.d("onCreateOptionsMenu()");

        MenuInflater inflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.reg_user_activities_mn, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");

        int resourceId = item.getItemId();

        if (resourceId == android.R.id.home) {
            Intent intent = new Intent(this, ComuSearchAc.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            NavUtils.navigateUpTo(this, intent);
            return true;
        } else if (resourceId == R.id.login_ac_mn) {
            UserMenu.LOGIN_AC.doMenuItem(this);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    class ComuAndUserComuAndUserRegister extends AsyncTask<UsuarioComunidad, Void, Void> {

        UiAarException uiException;

        @Override
        protected Void doInBackground(UsuarioComunidad... usuarioComunidad)
        {
            Timber.d("ComuAndUserComuAndUserRegister.doInBackground()");
            Usuario newUser = usuarioComunidad[0].getUsuario();

            try {
                AarUserComuServ.regComuAndUserAndUserComu(usuarioComunidad[0]).execute();
                SpringOauthToken token = Oauth2.getPasswordUserToken(newUser.getUserName(), newUser.getPassword());
                TKhandler.initTokenAndBackupFile(token);
            }catch (IOException e) {
                uiException = new UiAarException(ErrorBean.GENERIC_ERROR);
                return null;
            } catch (UiAarException e) {
                uiException = e;
            }
            return null;

            // TODO: si la comunidad ya existe y el userComu no, hacer un regUserAndUserComu.
            // TODO: si el userComu existe y la comunidad no, hacer un regComuAndUserComu.
            // TODO: si existen ambos, pero el userComu no pertenece a la comunidad, hacer un RegUserComu.
            // TODO: algo similar hay que hacer en el resto de acciones de registro.
            // TODO: hay que validar que una comunidad no tiene más de un presidente o más de un administrador.
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            Timber.d("RegComuAndUserComuHttp.onPostExecute()");

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
