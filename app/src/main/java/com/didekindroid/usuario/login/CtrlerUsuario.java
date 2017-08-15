package com.didekindroid.usuario.login;

import android.support.annotation.NonNull;

import com.didekindroid.api.Controller;
import com.didekinlib.http.oauth2.SpringOauthToken;
import com.didekinlib.model.usuario.Usuario;

import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.security.OauthTokenObservable.oauthTokenAndInitCache;
import static com.didekindroid.security.OauthTokenObservable.oauthTokenFromUserPswd;
import static com.didekindroid.security.TokenIdentityCacher.cleanTkCacheAction;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static io.reactivex.Single.fromCallable;
import static io.reactivex.Single.just;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 21/02/17
 * Time: 12:53
 */
@SuppressWarnings({"AnonymousInnerClassMayBeStatic", "WeakerAccess"})
public class CtrlerUsuario extends Controller {

    //    .................................... OBSERVABLES .................................

    static Single<Boolean> loginSingle(final Usuario usuario)
    {
        return fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception
            {
                return usuarioDao.loginInternal(usuario.getUserName(), usuario.getPassword());
            }
        });
    }

    static Single<Boolean> loginUpdateTkCache(final Usuario usuario)
    {
        return loginSingle(usuario).flatMap(new Function<Boolean, Single<Boolean>>() {
            @Override
            public Single<Boolean> apply(Boolean isLoginValid) throws Exception
            {
                if (isLoginValid) {
                    return oauthTokenAndInitCache(usuario).toSingleDefault(true);
                }
                return just(false);
            }
        });
    }

    /**
     * It has a mock test implementation. It clears token in cache.
     */
    static Single<Boolean> loginPswdSendSingle(final Callable<Boolean> sendPswdCall)
    {
        return fromCallable(sendPswdCall).doFinally(cleanTkCacheAction);
    }

    /**
     * Password change submitting the current password.
     */
    public static Completable passwordChangeWithPswdValidation(final Usuario oldUser, final Usuario newUser)
    {
        Timber.d("passwordChangeWithPswdValidation()");
        return oauthTokenFromUserPswd(oldUser)

                .flatMapCompletable(new Function<SpringOauthToken, CompletableSource>() {
                    @Override
                    public CompletableSource apply(@io.reactivex.annotations.NonNull final SpringOauthToken oldOauthToken) throws Exception
                    {
                        Timber.d("passwordChangeWithPswdValidation()");
                        return fromCallable(new Callable<Integer>() {
                            @Override
                            public Integer call() throws Exception
                            {
                                return usuarioDao.passwordChange(oldOauthToken, newUser.getPassword());
                            }
                        })
                                .flatMapCompletable(new Function<Integer, CompletableSource>() {
                                    @Override
                                    public CompletableSource apply(Integer passwordUpdated) throws Exception
                                    {
                                        Timber.d("apply()");
                                        return oauthTokenAndInitCache(newUser);
                                    }
                                });
                    }
                });
    }

    //    ................................. INSTANCE METHODS .................................

    public boolean validateLogin(@NonNull DisposableSingleObserver<Boolean> observer, @NonNull Usuario usuario)
    {
        Timber.i("validateLogin()");
        return subscriptions.add(
                loginUpdateTkCache(usuario)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    public boolean sendNewPassword(@NonNull DisposableSingleObserver<Boolean> observer, @NonNull final Usuario usuario)
    {
        Timber.d("sendNewPassword()");
        Callable<Boolean> sendPswdCallable = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception
            {
                return usuarioDao.sendPassword(usuario.getUserName());
            }
        };
        return sendNewPassword(sendPswdCallable, observer);
    }

    /**
     * Test friendly variant.
     */
    public boolean sendNewPassword(@NonNull Callable<Boolean> sendPswdCall,
                                   @NonNull DisposableSingleObserver<Boolean> observer)
    {
        Timber.d("sendNewPassword()");

        return subscriptions.add(
                loginPswdSendSingle(sendPswdCall)    // Borra token in cache.
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    public boolean changePasswordInRemote(DisposableCompletableObserver observer, final Usuario oldUser, final Usuario newUser)
    {
        Timber.d("changePasswordInRemote()");
        return subscriptions.add(
                passwordChangeWithPswdValidation(oldUser, newUser)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }
}
