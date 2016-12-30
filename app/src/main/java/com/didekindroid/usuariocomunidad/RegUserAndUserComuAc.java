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

import com.didekin.common.exception.ErrorBean;
import com.didekin.comunidad.Comunidad;
import com.didekin.oauth2.SpringOauthToken;
import com.didekin.usuario.Usuario;
import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekinaar.exception.UiException;
import com.didekinaar.usuario.RegUserFr;
import com.didekinaar.usuario.UsuarioBean;
import com.didekinaar.utils.ConnectionUtils;
import com.didekinaar.utils.UIutils;
import com.didekindroid.R;
import com.didekindroid.comunidad.ComuBundleKey;
import com.didekindroid.comunidad.ComunidadBean;

import java.io.IOException;
import java.util.Objects;

import timber.log.Timber;

import static com.didekinaar.security.Oauth2DaoRemote.Oauth2;
import static com.didekinaar.security.TokenIdentityCacher.TKhandler;
import static com.didekinaar.utils.AarItemMenu.mn_handler;
import static com.didekinaar.utils.UIutils.checkPostExecute;
import static com.didekinaar.utils.UIutils.doToolBar;
import static com.didekinaar.utils.UIutils.getErrorMsgBuilder;
import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.usuariocomunidad.UserComuService.AppUserComuServ;
import static com.didekindroid.util.AppMenuRouter.doUpMenu;
import static com.didekindroid.util.AppMenuRouter.routerMap;

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
        Objects.equals(TKhandler.isRegisteredUser(), false);
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

        UsuarioBean usuarioBean = RegUserFr.makeUserBeanFromRegUserFrView(mRegUserFr.getFragmentView());
        UsuarioComunidadBean usuarioComunidadBean = RegUserComuFr.makeUserComuBeanFromView(
                mRegUserComuFrg.getFragmentView(),
                new ComunidadBean(mComunidad.getC_Id(), null, null, null, null, null),
                usuarioBean);

        StringBuilder errorBuilder = getErrorMsgBuilder(this);

        if (!usuarioComunidadBean.validate(getResources(), errorBuilder)) {
            UIutils.makeToast(this, errorBuilder.toString(), R.color.deep_purple_100);
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            UIutils.makeToast(this, R.string.no_internet_conn_toast);
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
        switch (resourceId){
            case android.R.id.home:
                doUpMenu(this);
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

    class UserAndUserComuRegister extends AsyncTask<UsuarioComunidad, Void, Void> {

        UiException uiException;

        @Override
        protected Void doInBackground(UsuarioComunidad... usuarioComunidad)
        {
            Timber.d("UserAndUserComuRegister.doInBackground()");
            Usuario newUser = usuarioComunidad[0].getUsuario();
            try {
                AppUserComuServ.regUserAndUserComu(usuarioComunidad[0]).execute();
            } catch (IOException e) {
                uiException = new UiException(ErrorBean.GENERIC_ERROR);
                return null;
            }
            SpringOauthToken token;
            try {
                token = Oauth2.getPasswordUserToken(newUser.getUserName(), newUser.getPassword());
                TKhandler.initIdentityCache(token);
            } catch (UiException e) {
                uiException = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            if (checkPostExecute(RegUserAndUserComuAc.this)) return;

            Timber.d("UserAndUserComuRegister.onPostExecute()");

            if (uiException != null) {
                uiException.processMe(RegUserAndUserComuAc.this, new Intent());
            } else {
                Intent intent = new Intent(RegUserAndUserComuAc.this, SeeUserComuByComuAc.class);
                intent.putExtra(ComuBundleKey.COMUNIDAD_ID.key, mComunidad.getC_Id());
                startActivity(intent);
               TKhandler.updateIsRegistered(true);
            }

        }
    }
}
