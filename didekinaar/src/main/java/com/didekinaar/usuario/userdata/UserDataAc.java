package com.didekinaar.usuario.userdata;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.didekin.oauth2.SpringOauthToken;
import com.didekin.usuario.Usuario;
import com.didekinaar.R;
import com.didekinaar.exception.UiException;

import java.util.Objects;

import timber.log.Timber;

import static com.didekin.common.exception.DidekinExceptionMsg.BAD_REQUEST;
import static com.didekinaar.security.Oauth2Service.Oauth2;
import static com.didekinaar.security.TokenHandler.TKhandler;
import static com.didekinaar.usuario.UsuarioService.AarUserServ;
import static com.didekinaar.utils.UIutils.doToolBar;
import static com.didekinaar.utils.UIutils.isRegisteredUser;
import static com.didekinaar.utils.UIutils.makeToast;

/**
 * Preconditions:
 * 1. Registered user.
 * Postconditions:
 * 1. Regitered user with modified data. Once done, it goes to SeeUserComuByUserAc.
 */
@SuppressWarnings({"ConstantConditions", "AbstractClassExtendsConcreteClass"})
public abstract class UserDataAc extends AppCompatActivity implements UserDataViewIf {

    View mAcView;
    Usuario mOldUser;
    UserDataController controller;
    protected Class<? extends Activity> activityClassToGo;

    protected abstract void setDefaultActivityClassToGo(Class<? extends Activity> activityClassToGo);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate()");

        // Preconditions.
        Objects.equals(isRegisteredUser(this), true);
        controller = new UserDataController(this);
        mAcView = getLayoutInflater().inflate(R.layout.user_data_ac, null);
        setContentView(mAcView);
        controller.loadUserData();
        doToolBar(this, true);

        Button mModifyButton = (Button) findViewById(R.id.user_data_modif_button);
        mModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("mModifyButton.OnClickListener().onClick()");
                controller.modifyUserData();
            }
        });
    }

    @Override
    public void initUserDataInView()
    {
        ((EditText) mAcView.findViewById(R.id.reg_usuario_email_editT)).setText(mOldUser.getUserName());
        ((EditText) mAcView.findViewById(R.id.reg_usuario_alias_ediT)).setText(mOldUser.getAlias());
        ((EditText) mAcView.findViewById(R.id.user_data_ac_password_ediT))
                .setHint(R.string.user_data_ac_password_hint);
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    class UserDataModifyer extends AsyncTask<Usuario, Void, Boolean> {

        UiException uiException;

        @Override
        protected Boolean doInBackground(Usuario... usuarios)
        {


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

            /*if (!isSameUserName) {
                Usuario usuarioIn = new Usuario.UsuarioBuilder()
                        .userName(usuarios[0].getUserName())
                        .alias(usuarios[0].getAlias())
                        .uId(usuarios[0].getuId())
                        .build();

                try {
                    AarUserServ.modifyUser(usuarioIn);
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
                    AarUserServ.deleteAccessToken(token_1.getValue());
                } catch (UiException e) {
                    // No token in cache
                    Timber.d(e.getErrorBean().getMessage());
                    e.processMe(UserDataAc.this, new Intent());
                }
                return false;
            }*/

            Usuario usuarioIn = new Usuario.UsuarioBuilder()
                    .alias(usuarios[0].getAlias())
                    .uId(usuarios[0].getuId())
                    .build();
            try {
                AarUserServ.modifyUser(usuarioIn);
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
                makeToast(UserDataAc.this, R.string.password_wrong);
            }
            if (uiException != null) {
                Objects.equals(passwordWrong,false);
                uiException.processMe(UserDataAc.this, new Intent());
            }
        }
    }
}
