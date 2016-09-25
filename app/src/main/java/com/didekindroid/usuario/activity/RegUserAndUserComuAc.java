package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.didekin.common.exception.ErrorBean;
import com.didekin.oauth2.SpringOauthToken;
import com.didekin.usuario.dominio.Comunidad;
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
import java.util.Objects;

import timber.log.Timber;

import static com.didekindroid.common.activity.BundleKey.COMUNIDAD_ID;
import static com.didekindroid.common.activity.BundleKey.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.common.activity.TokenHandler.TKhandler;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.common.utils.UIutils.getErrorMsgBuilder;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.common.utils.UIutils.updateIsRegistered;
import static com.didekindroid.common.webservices.Oauth2Service.Oauth2;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeUserBeanFromRegUserFrView;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeUserComuBeanFromView;
import static com.didekindroid.usuario.activity.utils.UserMenu.LOGIN_AC;
import static com.didekindroid.usuario.activity.utils.UserMenu.doUpMenu;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;

/**
 * User: pedro@didekin
 * Date: 11/05/15
 * Time: 19:13
 */

/**
 * Preconditions:
 * 1. The user is not registered.
 * 2. The activity receives a comunidad object, as an intent key, with the following fields:
 * -- comunidadId.
 * -- nombreComunidad (with tipoVia,nombreVia, numero and sufijoNumero).
 * -- municipio, with codInProvincia and nombre.
 * -- provincia, with provinciaId and nombre.
 * The comunidad already exists in BD.
 * <p/>
 * Postconditions:
 * 1. A long comunidadId is passed as an intent key.
 * 2. The activity SeeUserComuByComuAc is started.
 */
@SuppressWarnings("ConstantConditions")
public class RegUserAndUserComuAc extends AppCompatActivity {

    RegUserComuFr mRegUserComuFrg;
    RegUserFr mRegUserFr;
    Comunidad mComunidad;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate()");

        // Preconditions.
        Objects.equals(isRegisteredUser(this), false);
        Comunidad comunidad = (Comunidad) getIntent().getExtras().getSerializable(COMUNIDAD_LIST_OBJECT.key);
        mComunidad = comunidad != null ? comunidad : null;

        setContentView(R.layout.reg_user_and_usercomu_ac);
        doToolBar(this, true);

        mRegUserComuFrg = (RegUserComuFr) getFragmentManager().findFragmentById(R.id
                .reg_usercomu_frg);
        mRegUserFr = (RegUserFr) getFragmentManager().findFragmentById(R.id.reg_user_frg);

        Button mRegistroButton = (Button) findViewById(R.id.reg_user_usercomu_button);
        mRegistroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("View.OnClickListener().onClick()");
                registerUserAndUserComu();
            }
        });
    }

    void registerUserAndUserComu()
    {
        Timber.d("registerComuAndUsuarioComu()");

        UsuarioBean usuarioBean = makeUserBeanFromRegUserFrView(mRegUserFr.getFragmentView());
        UsuarioComunidadBean usuarioComunidadBean = makeUserComuBeanFromView(
                mRegUserComuFrg.getFragmentView(),
                new ComunidadBean(mComunidad.getC_Id(), null, null, null, null, null),
                usuarioBean);

        StringBuilder errorBuilder = getErrorMsgBuilder(this);

        if (!usuarioComunidadBean.validate(getResources(), errorBuilder)) {
            UIutils.makeToast(this, errorBuilder.toString(), Toast.LENGTH_SHORT);
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            UIutils.makeToast(this, R.string.no_internet_conn_toast, Toast.LENGTH_LONG);
        } else {
            new UserAndUserComuRegister().execute(usuarioComunidadBean.getUsuarioComunidad());
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
        inflater.inflate(R.menu.reg_user_activities_mn, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");

        int resourceId = item.getItemId();

        switch (resourceId) {
            case android.R.id.home:
                doUpMenu(this);
                return true;
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

    class UserAndUserComuRegister extends AsyncTask<UsuarioComunidad, Void, Void> {

        UiException uiException;

        @Override
        protected Void doInBackground(UsuarioComunidad... usuarioComunidad)
        {
            Timber.d("UserAndUserComuRegister.doInBackground()");
            Usuario newUser = usuarioComunidad[0].getUsuario();
            try {
                ServOne.regUserAndUserComu(usuarioComunidad[0]).execute();
            } catch (IOException e) {
                uiException = new UiException(ErrorBean.GENERIC_ERROR);
                return null;
            }
            SpringOauthToken token;
            try {
                token = Oauth2.getPasswordUserToken(newUser.getUserName(), newUser.getPassword());
                TKhandler.initTokenAndBackupFile(token);
            } catch (UiException e) {
                uiException = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            Timber.d("UserAndUserComuRegister.onPostExecute()");

            if (uiException != null) {
                uiException.processMe(RegUserAndUserComuAc.this, new Intent());
            } else {
                Intent intent = new Intent(RegUserAndUserComuAc.this, SeeUserComuByComuAc.class);
                intent.putExtra(COMUNIDAD_ID.key, mComunidad.getC_Id());
                startActivity(intent);
                updateIsRegistered(true, RegUserAndUserComuAc.this);
            }

        }
    }
}
