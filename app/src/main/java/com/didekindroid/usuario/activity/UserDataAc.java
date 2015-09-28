package com.didekindroid.usuario.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.didekin.retrofitcl.ServiceOneException;
import com.didekin.security.OauthToken.AccessToken;
import com.didekin.serviceone.domain.Usuario;
import com.didekindroid.R;
import com.didekindroid.ioutils.ConnectionUtils;
import com.didekindroid.usuario.activity.utils.UserMenu;
import com.didekindroid.usuario.dominio.UsuarioBean;

import static com.didekin.exception.ExceptionMessage.BAD_REQUEST;
import static com.didekindroid.uiutils.UIutils.*;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeUserBeanFromUserDataAcView;
import static com.didekindroid.usuario.activity.utils.UserMenu.COMU_SEARCH_AC;
import static com.didekindroid.usuario.activity.utils.UserMenu.SEE_USERCOMU_BY_USER_AC;
import static com.didekindroid.usuario.security.TokenHandler.TKhandler;
import static com.didekindroid.usuario.webservices.Oauth2Service.Oauth2;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Preconditions:
 * 1. Registered user.
 * Postconditions:
 * 1. Regitered user with modified data. Once done, it goes to SeeUserComuByUserAc.
 */
public class UserDataAc extends Activity {

    private static final String TAG = UserDataAc.class.getCanonicalName();

    private View mAcView;
    private Button mModifyButton;
    private Usuario mOldUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        // Preconditions.
        checkState(isRegisteredUser(this));
        new UserDataGetter().execute();

        mAcView = getLayoutInflater().inflate(R.layout.user_data_ac, null);
        setContentView(mAcView);

        mModifyButton = (Button) findViewById(R.id.user_data_modif_button);
        mModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "mModifyButton.OnClickListener().onClick()");
                modifyUserData();
            }
        });
    }

    private void modifyUserData()
    {
        // TODO: send an email with a number, once the user hass pressed Modify,
        // and show in the activity an EditField to introduce it.
        // Only for changes of password.

        Log.d(TAG, "modifyUserData()");

        UsuarioBean usuarioBean = makeUserBeanFromUserDataAcView(mAcView);

        StringBuilder errorBuilder = getErrorMsgBuilder(this);

        if (!usuarioBean.validateWithOnePassword(getResources(), errorBuilder)) {
            makeToast(this, errorBuilder.toString());
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            makeToast(this, R.string.no_internet_conn_toast);
        } else {
            Usuario newUser = new Usuario.UsuarioBuilder().copyUsuario(usuarioBean.getUsuario())
                    .uId(mOldUser.getuId())
                    .build();
            new UserDataModifyer().execute(newUser);
        }
        Intent intent = new Intent(this, SeeUserComuByUserAc.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.d(TAG, "onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.user_data_ac_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.d(TAG, "onOptionsItemSelected()");

        int resourceId = checkNotNull(item.getItemId());

        switch (resourceId) {
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    private class UserDataGetter extends AsyncTask<Void, Void, Void> {

        final String TAG = UserDataGetter.class.getCanonicalName();

        protected Void doInBackground(Void... aVoid)
        {
            Log.d(TAG, "UserDataGetter.doInBackground()");
            mOldUser = ServOne.getUserData();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            Log.d(TAG, "UserDataGetter.onPostExecute()");

            ((EditText) mAcView.findViewById(R.id.reg_usuario_email_editT)).setText(mOldUser.getUserName());
            ((EditText) mAcView.findViewById(R.id.reg_usuario_alias_ediT)).setText(mOldUser.getAlias());
            ((EditText) mAcView.findViewById(R.id.user_data_ac_password_ediT))
                    .setHint(R.string.user_data_ac_password_hint);
        }
    }

    private class UserDataModifyer extends AsyncTask<Usuario, Void, Boolean> {

        final String TAG = UserDataModifyer.class.getCanonicalName();

        @Override
        protected Boolean doInBackground(Usuario... usuarios)
        {
            Log.d(TAG, "doInBackground()");

            boolean isSameUserName = mOldUser.getUserName().equals(usuarios[0].getUserName());
            boolean isSameAlias = mOldUser.getAlias().equals(usuarios[0].getAlias());
            boolean isPasswordWrong = false;

            if (isSameAlias && isSameUserName) {
                return isPasswordWrong;
            }

            AccessToken token_1 = null;
            try {
                token_1 = Oauth2.getPasswordUserToken(mOldUser.getUserName(), usuarios[0].getPassword());
                TKhandler.initKeyCacheAndBackupFile(token_1);
            } catch (ServiceOneException e) {
                checkState(e.getMessage().equals(BAD_REQUEST.getMessage()));
                isPasswordWrong = true;
                return isPasswordWrong;
            }

            if (!isSameUserName) {
                Usuario usuarioIn = new Usuario.UsuarioBuilder()
                        .userName(usuarios[0].getUserName())
                        .alias(usuarios[0].getAlias())
                        .uId(usuarios[0].getuId())
                        .build();
                checkState(ServOne.modifyUser(usuarioIn) > 0);
                AccessToken token_2 = Oauth2.getPasswordUserToken(usuarioIn.getUserName(), usuarios[0].getPassword());
                TKhandler.initKeyCacheAndBackupFile(token_2);
                checkState(ServOne.deleteAccessToken(token_1.getValue()));
                return isPasswordWrong;
            }

            Usuario usuarioIn = new Usuario.UsuarioBuilder()
                    .alias(usuarios[0].getAlias())
                    .uId(usuarios[0].getuId())
                    .build();
            checkState(ServOne.modifyUser(usuarioIn) > 0);
            return isPasswordWrong;
        }

        @Override
        protected void onPostExecute(Boolean passwordWrong)
        {
            Log.d(TAG, "onPostExecute(): DONE");
            if (passwordWrong) {
                makeToast(UserDataAc.this, R.string.password_not_valid);
            }
        }
    }
}
