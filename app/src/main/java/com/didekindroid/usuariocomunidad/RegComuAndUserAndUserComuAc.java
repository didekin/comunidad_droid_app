package com.didekindroid.usuariocomunidad;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.didekindroid.R;
import com.didekindroid.comunidad.ComunidadBean;
import com.didekindroid.comunidad.RegComuFr;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.RegUserFr;
import com.didekindroid.usuario.UsuarioBean;
import com.didekindroid.util.ConnectionUtils;
import com.didekinlib.http.ErrorBean;
import com.didekinlib.http.oauth2.SpringOauthToken;
import com.didekinlib.model.usuario.Usuario;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.io.IOException;

import timber.log.Timber;

import static com.didekindroid.security.Oauth2DaoRemote.Oauth2;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.api.ItemMenu.mn_handler;
import static com.didekindroid.MenuRouter.doUpMenuWithIntent;
import static com.didekindroid.MenuRouter.getRegisterDependentClass;
import static com.didekindroid.MenuRouter.routerMap;
import static com.didekindroid.util.UIutils.checkPostExecute;
import static com.didekindroid.util.UIutils.doToolBar;
import static com.didekindroid.util.UIutils.makeToast;
import static com.didekinlib.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static com.didekinlib.model.common.dominio.ValidDataPatterns.LINE_BREAK;

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
        RegComuFr.makeComunidadBeanFromView(mRegComuFrg.getFragmentView(), comunidadBean);
        UsuarioBean usuarioBean = RegUserFr.makeUserBeanFromRegUserFrView(mRegUserFr.getFragmentView());
        UsuarioComunidadBean usuarioComunidadBean = RegUserComuFr.makeUserComuBeanFromView(mRegUserComuFrg
                .getFragmentView(), comunidadBean, usuarioBean);

        // Validation of data.
        StringBuilder errorMsg = new StringBuilder(getResources().getText(R.string.error_validation_msg))
                .append(LINE_BREAK.getRegexp());

        if (!usuarioComunidadBean.validate(getResources(), errorMsg)) {
            makeToast(this, errorMsg.toString());
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
        switch (resourceId) {
            case android.R.id.home:
                Intent intent = new Intent(this, getRegisterDependentClass(resourceId));
                doUpMenuWithIntent(this, intent);
                return true;
            case R.id.login_ac_mn:
                mn_handler.doMenuItem(this, routerMap.get(resourceId));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    @SuppressWarnings("WeakerAccess")
    class ComuAndUserComuAndUserRegister extends AsyncTask<UsuarioComunidad, Void, Void> {

        UiException uiException;

        @Override
        protected Void doInBackground(UsuarioComunidad... usuarioComunidad)
        {
            Timber.d("ComuAndUserComuAndUserRegister.doInBackground()");
            Usuario newUser = usuarioComunidad[0].getUsuario();

            try {
                userComuDaoRemote.regComuAndUserAndUserComu(usuarioComunidad[0]).execute();
                SpringOauthToken token = Oauth2.getPasswordUserToken(newUser.getUserName(), newUser.getPassword());
                TKhandler.initIdentityCache(token);
            } catch (IOException e) {
                uiException = new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
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
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            if (checkPostExecute(RegComuAndUserAndUserComuAc.this)) return;

            Timber.d("RegComuAndUserComuHttp.onPostExecute()");

            if (uiException != null) {
                uiException.processMe(RegComuAndUserAndUserComuAc.this, new Intent());
            } else {
                Intent intent = new Intent(RegComuAndUserAndUserComuAc.this, SeeUserComuByUserAc.class);
                startActivity(intent);
                TKhandler.updateIsRegistered(true);
            }
        }
    }
}
