package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.didekin.oauth2.SpringOauthToken;
import com.didekin.usuario.dominio.Usuario;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.common.utils.ConnectionUtils;
import com.didekindroid.usuario.activity.utils.UserMenu;
import com.didekindroid.usuario.dominio.UsuarioBean;

import java.util.Objects;

import timber.log.Timber;

import static com.didekin.common.exception.DidekinExceptionMsg.BAD_REQUEST;
import static com.didekindroid.common.activity.TokenHandler.TKhandler;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.common.utils.UIutils.getErrorMsgBuilder;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.common.utils.UIutils.makeToast;
import static com.didekindroid.common.webservices.Oauth2Service.Oauth2;
import static com.didekindroid.incidencia.activity.utils.IncidenciaMenu.INCID_SEE_BY_COMU_AC;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeUserBeanFromUserDataAcView;
import static com.didekindroid.usuario.activity.utils.UserMenu.COMU_SEARCH_AC;
import static com.didekindroid.usuario.activity.utils.UserMenu.SEE_USERCOMU_BY_USER_AC;
import static com.didekindroid.usuario.activity.utils.UserMenu.doUpMenuClearSingleTop;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;

/**
 * Preconditions:
 * 1. Registered user.
 * Postconditions:
 * 1. Regitered user with modified data. Once done, it goes to SeeUserComuByUserAc.
 */
@SuppressWarnings("ConstantConditions")
public class UserDataAc extends AppCompatActivity {

    View mAcView;
    Usuario mOldUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate()");

        // Preconditions.
        Objects.equals(isRegisteredUser(this), true);
        new UserDataGetter().execute();

        mAcView = getLayoutInflater().inflate(R.layout.user_data_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);

        Button mModifyButton = (Button) findViewById(R.id.user_data_modif_button);
        mModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("mModifyButton.OnClickListener().onClick()");
                modifyUserData();
            }
        });
    }

    void modifyUserData()
    {
        // TODO: send an email with a number, once the user hass pressed Modify,
        // and show in the activity an EditField to introduce it.
        // Only for changes of password.

        Timber.d("modifyUserData()");

        UsuarioBean usuarioBean = makeUserBeanFromUserDataAcView(mAcView);

        StringBuilder errorBuilder = getErrorMsgBuilder(this);

        if (!usuarioBean.validateWithOnePassword(getResources(), errorBuilder)) {
            makeToast(this, errorBuilder.toString(), Toast.LENGTH_SHORT);
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            makeToast(this, R.string.no_internet_conn_toast, Toast.LENGTH_LONG);
        } else {
            Usuario newUser = new Usuario.UsuarioBuilder().copyUsuario(usuarioBean.getUsuario())
                    .uId(mOldUser.getuId())
                    .build();
            new UserDataModifyer().execute(newUser);
            Intent intent = new Intent(this, SeeUserComuByUserAc.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Timber.d("onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.user_data_ac_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");

        int resourceId = item.getItemId();

        switch (resourceId) {
            case android.R.id.home:
                doUpMenuClearSingleTop(this);
                return true;
            case R.id.password_change_ac_mn:
                UserMenu.PASSWORD_CHANGE_AC.doMenuItem(this);
                return true;
            case R.id.delete_me_ac_mn:
                UserMenu.DELETE_ME_AC.doMenuItem(this);
                return true;
            case R.id.see_usercomu_by_user_ac_mn:
                SEE_USERCOMU_BY_USER_AC.doMenuItem(this);
                return true;
            case R.id.comu_search_ac_mn:
                COMU_SEARCH_AC.doMenuItem(this);
                return true;
            case R.id.incid_see_open_by_comu_ac_mn:
                INCID_SEE_BY_COMU_AC.doMenuItem(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    class UserDataGetter extends AsyncTask<Void, Void, Void> {

        UiException uiException;

        protected Void doInBackground(Void... aVoid)
        {
            Timber.d("UserDataGetter.doInBackground()");

            try {
                mOldUser = ServOne.getUserData();
            } catch (UiException e) {
                uiException = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            Timber.d("UserDataGetter.onPostExecute()");

            if (uiException != null) {
                uiException.processMe(UserDataAc.this, new Intent());
            } else {
                ((EditText) mAcView.findViewById(R.id.reg_usuario_email_editT)).setText(mOldUser.getUserName());
                ((EditText) mAcView.findViewById(R.id.reg_usuario_alias_ediT)).setText(mOldUser.getAlias());
                ((EditText) mAcView.findViewById(R.id.user_data_ac_password_ediT))
                        .setHint(R.string.user_data_ac_password_hint);
            }
        }
    }

    class UserDataModifyer extends AsyncTask<Usuario, Void, Boolean> {

        UiException uiException;

        @Override
        protected Boolean doInBackground(Usuario... usuarios)
        {
            Timber.d("doInBackground()");

            boolean isSameUserName = mOldUser.getUserName().equals(usuarios[0].getUserName());
            boolean isSameAlias = mOldUser.getAlias().equals(usuarios[0].getAlias());

            if (isSameAlias && isSameUserName) {
                Timber.d("sameAlias && sameUserName");
                return false;
            }

            // Token with the old credentials.
            SpringOauthToken token_1;
            try {
                token_1 = Oauth2.getPasswordUserToken(mOldUser.getUserName(), usuarios[0].getPassword());
                TKhandler.initTokenAndBackupFile(token_1);
            } catch (UiException e) {
                if (e.getErrorBean().getMessage().equals(BAD_REQUEST.getHttpMessage())) {
                    Timber.d(" exception: %s%n", BAD_REQUEST.getHttpMessage());
                    return true;  // Password/user matching error.
                } else {
                    uiException = e;
                    Timber.d(e.getErrorBean().getMessage());
                    return false; // Other authentication error.
                }
            }

            if (!isSameUserName) {
                Usuario usuarioIn = new Usuario.UsuarioBuilder()
                        .userName(usuarios[0].getUserName())
                        .alias(usuarios[0].getAlias())
                        .uId(usuarios[0].getuId())
                        .build();

                try {
                    ServOne.modifyUser(usuarioIn);
                    TKhandler.cleanTokenAndBackFile();
                } catch (UiException e) {
                    uiException = e;
                    Timber.d((e.getErrorBean() != null ?
                            e.getErrorBean().getMessage() : "token null in cache"));
                    return false; // Authentication error with old credentials.
                }

                try {
                    SpringOauthToken token_2 = Oauth2.getPasswordUserToken(usuarioIn.getUserName(), usuarios[0].getPassword());
                    TKhandler.initTokenAndBackupFile(token_2);
                } catch (UiException e) {
                    // Authentication error with new credentials.
                    Timber.d(e.getErrorBean().getMessage());
                }
                try {
                    ServOne.deleteAccessToken(token_1.getValue());
                } catch (UiException e) {
                    // No token in cache
                    Timber.d(e.getErrorBean().getMessage());
                    e.processMe(UserDataAc.this, new Intent());
                }
                return false;
            }

            Usuario usuarioIn = new Usuario.UsuarioBuilder()
                    .alias(usuarios[0].getAlias())
                    .uId(usuarios[0].getuId())
                    .build();
            try {
                ServOne.modifyUser(usuarioIn);
            } catch (UiException e) {
                uiException = e;
                Timber.d((e.getErrorBean() != null ?
                        e.getErrorBean().getMessage() : "token null in cache"));
                return false;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean passwordWrong)
        {
            Timber.d("onPostExecute()");

            if (passwordWrong) {
                Timber.d("onPostExecute(): password wrong");
                Objects.equals(uiException == null, true);
                makeToast(UserDataAc.this, R.string.password_wrong, Toast.LENGTH_LONG);
            }
            if (uiException != null) {
                Objects.equals(passwordWrong,false);
                uiException.processMe(UserDataAc.this, new Intent());
            }
        }
    }
}
