package com.didekinaar.usuario.login;

import android.content.Intent;

import com.didekin.oauth2.SpringOauthToken;
import com.didekin.usuario.Usuario;
import com.didekinaar.R;
import com.didekinaar.exception.UiException;

import java.util.concurrent.Callable;

import rx.Single;
import rx.Subscriber;
import rx.functions.Func2;
import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.didekinaar.PrimalCreator.creator;
import static com.didekinaar.security.OauthTokenObservable.getOauthTokenGetSingle;
import static com.didekinaar.security.TokenHandler.TKhandler;
import static com.didekinaar.usuario.UsuarioDaoRemote.usuarioDaoRemote;
import static com.didekinaar.utils.UIutils.makeToast;
import static com.didekinaar.utils.UIutils.updateIsRegistered;
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
    {
        return fromCallable(new LoginValidateCallable(usuario));
    }

    static Single<Boolean> getZipLoginSingle(Usuario usuario)
    {
        return getLoginValidateSingle(usuario)
                .zipWith(
                        getOauthTokenGetSingle(usuario),
                        initTokenFunc
                );
    }

    static Single<Boolean> getLoginMailSingle(String email)
    {
        return fromCallable(new LoginMailCallable(email));
    }

    //  ======================================================================================
    //    .................................... FUNCTIONS .................................
    //  ======================================================================================

    private static final Func2<Boolean, SpringOauthToken, Boolean> initTokenFunc =

            new Func2<Boolean, SpringOauthToken, Boolean>() {

                @Override
                public Boolean call(Boolean isLoginValid, SpringOauthToken token)
                {
                    boolean isUpdatedTokenData = isLoginValid && token != null;
                    if (isUpdatedTokenData) {
                        Timber.d("Updating token data ...");
                        TKhandler.initTokenAndBackupFile(token);
                        updateIsRegistered(true, creator.get().getContext());
                    }
                    return isUpdatedTokenData;
                }
            };

    //  =======================================================================================
    // ............................ SUBSCRIBERS ..................................
    //  =======================================================================================

    static class LoginValidateSubscriber extends LoginSubscriber {

        private final Usuario usuario;

        LoginValidateSubscriber(final LoginAc activity, final Usuario usuario)
        {
            super(activity);
            this.usuario = usuario;
        }

        @Override
        public void onNext(Boolean isLoginOk)
        {
            Timber.d("onNext");
            if (isLoginOk) {
                Timber.d("login OK");
                Intent intent = new Intent(activity, activity.defaultActivityClassToGo);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                activity.finish();
            } else if (++activity.counterWrong > 3) { // Password wrong
                activity.showDialog(usuario.getUserName());
            } else {
                Timber.d("Password wrong, counterWrong = %d%n", activity.counterWrong);
                makeToast(activity, R.string.password_wrong_in_login);
            }
        }
    }

    static class LoginMailSubscriber extends LoginSubscriber {

        LoginMailSubscriber(LoginAc activity)
        {
            super(activity);
        }

        @Override
        public void onNext(Boolean isMailOk)
        {
            Timber.d("onNext()");
            if (isMailOk) {
                makeToast(activity, R.string.password_new_in_login);
                activity.recreate();
            }
        }
    }

    abstract static class LoginSubscriber extends Subscriber<Boolean> {

        final LoginAc activity;

        LoginSubscriber(LoginAc activity)
        {
            this.activity = activity;
        }

        @Override
        public void onCompleted()
        {
            Timber.d("onCompleted()");
            unsubscribe();
        }

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onError");
            if (e instanceof UiException) {
                ((UiException) e).processMe(activity, new Intent());
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