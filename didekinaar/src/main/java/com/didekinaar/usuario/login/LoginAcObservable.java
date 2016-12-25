package com.didekinaar.usuario.login;

import android.content.Intent;

import com.didekin.usuario.Usuario;
import com.didekinaar.ActivitySubscriber;
import com.didekinaar.R;

import java.util.concurrent.Callable;

import rx.Single;
import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.didekinaar.security.OauthTokenObservable.getOauthTokenGetSingle;
import static com.didekinaar.security.TokenIdentityCacher.TKhandler;
import static com.didekinaar.usuario.UsuarioDaoRemote.usuarioDaoRemote;
import static com.didekinaar.utils.UIutils.makeToast;
import static rx.Single.fromCallable;

/**
 * User: pedro@didekin
 * Date: 19/12/16
 * Time: 16:32
 */

final class LoginAcObservable {

    //  =====================================================================================================
    //    .................................... OBSERVABLES .................................
    //  =====================================================================================================

    private static Single<Boolean> getLoginValidateSingle(Usuario usuario)
    {   // TODO: test.
        return fromCallable(new LoginValidateCallable(usuario));
    }

    static Single<Boolean> getZipLoginSingle(Usuario usuario)
    {   // TODO: test.
        return getLoginValidateSingle(usuario)
                .zipWith(
                        getOauthTokenGetSingle(usuario),
                        TKhandler.initTokenFunc
                );
    }

    static Single<Boolean> getLoginMailSingle(String email)
    {   // TODO: test.
        return fromCallable(new LoginMailCallable(email));
    }

    //  =======================================================================================
    // ............................ SUBSCRIBERS ..................................
    //  =======================================================================================

    static class LoginValidateSubscriber extends ActivitySubscriber<Boolean, LoginAc> {

        private final Usuario usuario;

        LoginValidateSubscriber(final LoginAc activity, final Usuario usuario)
        {
            super(activity);
            this.usuario = usuario;
        }

        @Override
        public void onNext(Boolean isLoginOk)
        {   // TODO: test.
            Timber.d("onNext");
            if (isLoginOk) {
                Timber.d("login OK");
                Intent intent = new Intent(activity, activity.defaultActivityClassToGo);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                activity.finish();
            } else if (activity.getCounterWrong() > 2) { // Password wrong
                activity.setCounterWrong(activity.getCounterWrong() + 1);
                activity.showDialog(usuario.getUserName());
            } else {
                Timber.d("Password wrong, counterWrong = %d%n", activity.getCounterWrong());
                makeToast(activity, R.string.password_wrong_in_login);
            }
        }
    }

    static class LoginMailSubscriber extends ActivitySubscriber<Boolean, LoginAc> {

        LoginMailSubscriber(final LoginAc activity)
        {
            super(activity);
        }

        @Override
        public void onNext(Boolean isMailOk)
        { // TODO: test.
            Timber.d("onNext()");
            if (isMailOk) {
                makeToast(activity, R.string.password_new_in_login);
                activity.recreate();
            }
        }
    }

    //  =========================================================================================
    //    .................................... CALLABLES .................................
    //  =========================================================================================

    private static class LoginValidateCallable implements Callable<Boolean> {

        private final Usuario usuario;

        LoginValidateCallable(Usuario usuario)
        {
            this.usuario = usuario;
        }

        @Override
        public Boolean call() throws Exception
        {
            return usuarioDaoRemote.loginInternal(usuario.getUserName(), usuario.getPassword());
        }
    }

    private static class LoginMailCallable implements Callable<Boolean> {

        private final String email;

        LoginMailCallable(String email)
        {
            this.email = email;
        }

        @Override
        public Boolean call() throws Exception
        {
            return usuarioDaoRemote.sendPassword(email);
        }
    }
}