package com.didekindroid.usuario.userdata;

import android.content.Intent;

import com.didekin.http.ErrorBean;
import com.didekin.http.oauth2.SpringOauthToken;
import com.didekin.usuario.Usuario;
import com.didekindroid.R;
import com.didekindroid.exception.UiException;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekin.http.GenericExceptionMsg.BAD_REQUEST;
import static com.didekindroid.security.OauthTokenObservable.getOauthToken;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.util.UIutils.makeToast;
import static io.reactivex.Single.fromCallable;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 20/12/16
 * Time: 18:57
 */
final class UserDataReactor implements UserDataReactorIf {

    static final UserDataReactorIf userDataReactor = new UserDataReactor();

    private UserDataReactor()
    {
    }
    // .................................... OBSERVABLES .................................

    private static Single<Usuario> getUserDataSingle()
    {
        return fromCallable(new Callable<Usuario>() {
            @Override
            public Usuario call() throws Exception
            {
                return usuarioDao.getUserData();
            }
        });
    }

    static Single<Integer> aliasModified(final Usuario newUser)
    {
        return fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception
            {
                // UserName must be null to detect an alias change only.
                return usuarioDao.modifyUser(
                        new Usuario.UsuarioBuilder()
                                .alias(newUser.getAlias())
                                .uId(newUser.getuId())
                                .build()
                );
            }
        });
    }

    private static Single<Integer> userModified(final Usuario newUser)
    {
        return fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception
            {
                return usuarioDao.modifyUser(newUser);
            }
        });
    }

    private static SingleSource<Integer> deletedTokenInBd(final String accessToken)
    {
        return fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception
            {
                return (usuarioDao.deleteAccessToken(accessToken) ? 1 : 0);
            }
        });
    }

    static Single<SpringOauthToken> tokenAndUserModified(Usuario oldUser, final Usuario newUser)
    {
        return getOauthToken(oldUser)
                .doOnSuccess(TKhandler.initTokenAction)
                .compose(fromTokenGetUserModified(newUser))
                .doOnSuccess(TKhandler.cleanTokenCacheAction)
                .compose(fromUserModifiedGetToken(newUser))
                .doOnSuccess(TKhandler.initTokenAction);
    }

    // ............................ SUBSCRIPTIONS ..................................

    @Override
    public boolean getUserDataRemote(UserDataControllerIf controller)
    {
        return controller.getSubscriptions().add(
                UserDataReactor.getUserDataSingle()
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new UserDataUpdateSingleObserver(controller))
        );
    }

    // .............................. SUBSCRIBERS ..................................

    static class UserDataUpdateSingleObserver extends DisposableSingleObserver<Usuario> {

        private final UserDataControllerIf controller;

        UserDataUpdateSingleObserver(UserDataControllerIf controller)
        {
            this.controller = controller;
        }

        @Override
        public void onSuccess(Usuario usuario)
        {
           Timber.d("onSuccess()");
           controller.processBackGetUserData(usuario);
        }

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onError");
            controller.processBackErrorInReactor(e);
        }
    }

    // ............................ TRANSFORMERS ..................................

    private static SingleTransformer<SpringOauthToken, Integer> fromTokenGetUserModified(final Usuario newUser)
    {
        return new SingleTransformer<SpringOauthToken, Integer>() {
            @Override
            public Single<Integer> apply(Single<SpringOauthToken> oauthToken)
            {
                return oauthToken
                        .flatMap(
                                new Func1<SpringOauthToken, Single<Integer>>() {
                                    @Override
                                    public Single<Integer> call(SpringOauthToken token)
                                    {
                                        return userModified(newUser)
                                                .zipWith(
                                                        deletedTokenInBd(token.getValue()),
                                                        new Func2<Integer, Integer, Integer>() {
                                                            @Override
                                                            public Integer call(Integer userModified, Integer deletedToken)
                                                            {
                                                                return userModified * deletedToken; // It must be > 0.
                                                            }
                                                        }
                                                );
                                    }
                                }
                        );
            }
        };
    }

    private static SingleTransformer<Integer, SpringOauthToken> fromUserModifiedGetToken(final Usuario newUser)
    {
        return new SingleTransformer<Integer, SpringOauthToken>() {
            @Override
            public Single<SpringOauthToken> apply(Single<Integer> integerSingle)
            {
                return integerSingle
                        .flatMap(
                                new Func1<Integer, Single<SpringOauthToken>>() {
                                    @Override
                                    public Single<SpringOauthToken> call(Integer integer)
                                    {
                                        return getOauthToken(newUser);
                                    }
                                }
                        );
            }
        };
    }
}
