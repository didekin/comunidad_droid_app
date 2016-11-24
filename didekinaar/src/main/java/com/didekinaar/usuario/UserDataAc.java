package com.didekinaar.usuario;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.didekin.oauth2.SpringOauthToken;
import com.didekin.usuario.Usuario;
import com.didekinaar.R;
import com.didekinaar.exception.UiAarException;
import com.didekinaar.usuariocomunidad.SeeUserComuByUserAc;
import com.didekinaar.usuariocomunidad.UserAndComuFiller;
import com.didekinaar.utils.ConnectionUtils;

import java.util.Objects;

import timber.log.Timber;

import static com.didekin.common.exception.DidekinExceptionMsg.BAD_REQUEST;
import static com.didekinaar.security.Oauth2Service.Oauth2;
import static com.didekinaar.security.TokenHandler.TKhandler;
import static com.didekinaar.usuario.AarUsuarioService.AarUserServ;
import static com.didekinaar.utils.UIutils.doToolBar;
import static com.didekinaar.utils.UIutils.getErrorMsgBuilder;
import static com.didekinaar.utils.UIutils.isRegisteredUser;
import static com.didekinaar.utils.UIutils.makeToast;

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

        UsuarioBean usuarioBean = UserAndComuFiller.makeUserBeanFromUserDataAcView(mAcView);

        StringBuilder errorBuilder = getErrorMsgBuilder(this);

        if (!usuarioBean.validateWithOnePassword(getResources(), errorBuilder)) {
            makeToast(this, errorBuilder.toString(), com.didekinaar.R.color.deep_purple_100);
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            makeToast(this, R.string.no_internet_conn_toast);
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

        if (resourceId == android.R.id.home) {
            UserMenu.doUpMenu(this);
            return true;
        } else if (resourceId == R.id.password_change_ac_mn) {
            UserMenu.PASSWORD_CHANGE_AC.doMenuItem(this);
            return true;
        } else if (resourceId == R.id.delete_me_ac_mn) {
            UserMenu.DELETE_ME_AC.doMenuItem(this);
            return true;
        } else if (resourceId == R.id.see_usercomu_by_user_ac_mn) {
            UserMenu.SEE_USERCOMU_BY_USER_AC.doMenuItem(this);
            return true;
        } else if (resourceId == R.id.comu_search_ac_mn) {
            UserMenu.COMU_SEARCH_AC.doMenuItem(this);
            return true;
//        } else if (resourceId == R.id.incid_see_open_by_comu_ac_mn) {
//            IncidenciaMenu.INCID_SEE_BY_COMU_AC.doMenuItem(this);
//            return true;     // TODO: quitar esta opción de menú.
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    class UserDataGetter extends AsyncTask<Void, Void, Void> {

        UiAarException uiException;

        protected Void doInBackground(Void... aVoid)
        {
            Timber.d("UserDataGetter.doInBackground()");

            try {
                mOldUser = AarUserServ.getUserData();
            } catch (UiAarException e) {
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

        UiAarException uiException;

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
            } catch (UiAarException e) {
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
                    AarUserServ.modifyUser(usuarioIn);
                    TKhandler.cleanTokenAndBackFile();
                } catch (UiAarException e) {
                    uiException = e;
                    Timber.d((e.getErrorBean() != null ?
                            e.getErrorBean().getMessage() : "token null in cache"));
                    return false; // Authentication error with old credentials.
                }

                try {
                    SpringOauthToken token_2 = Oauth2.getPasswordUserToken(usuarioIn.getUserName(), usuarios[0].getPassword());
                    TKhandler.initTokenAndBackupFile(token_2);
                } catch (UiAarException e) {
                    // Authentication error with new credentials.
                    Timber.d(e.getErrorBean().getMessage());
                }
                try {
                    AarUserServ.deleteAccessToken(token_1.getValue());
                } catch (UiAarException e) {
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
                AarUserServ.modifyUser(usuarioIn);
            } catch (UiAarException e) {
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
                makeToast(UserDataAc.this, R.string.password_wrong);
            }
            if (uiException != null) {
                Objects.equals(passwordWrong,false);
                uiException.processMe(UserDataAc.this, new Intent());
            }
        }
    }
}
