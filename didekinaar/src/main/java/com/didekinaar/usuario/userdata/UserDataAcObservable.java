package com.didekinaar.usuario.userdata;

import android.content.Intent;

import com.didekin.common.exception.ErrorBean;
import com.didekin.oauth2.SpringOauthToken;
import com.didekin.usuario.Usuario;
import com.didekinaar.ActivitySubscriber;
import com.didekinaar.R;
import com.didekinaar.exception.UiException;

import java.util.concurrent.Callable;

import rx.Single;
import rx.Single.Transformer;
import rx.functions.Func1;
import rx.functions.Func2;
import timber.log.Timber;

import static com.didekin.common.exception.DidekinExceptionMsg.BAD_REQUEST;
import static com.didekinaar.security.OauthTokenObservable.getOauthToken;
import static com.didekinaar.security.TokenIdentityCacher.TKhandler;
import static com.didekinaar.usuario.UsuarioDaoRemote.usuarioDaoRemote;
import static com.didekinaar.utils.UIutils.makeToast;
import static rx.Single.fromCallable;

/**
 * User: pedro@didekin
 * Date: 20/12/16
 * Time: 18:57
 */

@SuppressWarnings({"WeakerAccess", "AnonymousInnerClassMayBeStatic"})
class UserDataAcObservable {

    // .................................... OBSERVABLES .................................

    static Single<Usuario> getUserDataSingle()
    {
        return fromCallable(new Callable<Usuario>() {
            @Override
            public Usuario call() throws Exception
            {
                return usuarioDaoRemote.getUserData();
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
                return usuarioDaoRemote.modifyUser(
                        new Usuario.UsuarioBuilder()
                                .alias(newUser.getAlias())
                                .uId(newUser.getuId())
                                .build()
                );
            }
        });
    }

    static Single<Integer> userModified(final Usuario newUser)
    {
        return fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception
            {
                return usuarioDaoRemote.modifyUser(newUser);
            }
        });
    }

    static Single<Integer> deletedTokenInBd(final String accessToken)
    {
        return fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception
            {
                return (usuarioDaoRemote.deleteAccessToken(accessToken) ? 1 : 0);
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

    // ............................ SUBSCRIBERS ..................................

    static class UserDataUpdateSubscriber extends ActivitySubscriber<Object, UserDataAc> {

        UserDataUpdateSubscriber(UserDataAc activity)
        {
            super(activity);
        }

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onError");
            if (e instanceof UiException) {
                UiException ui = (UiException) e;
                if (ui.getErrorBean().getMessage().equals(BAD_REQUEST.getHttpMessage())) {
                    makeToast(activity, R.string.password_wrong);
                    if (activity.isDestroyed() || activity.isFinishing()) {
                        activity.recreate();
                    }
                } else {
                    ui.processMe(activity, new Intent());
                }

            } else {
                new UiException(ErrorBean.GENERIC_ERROR).processMe(activity, new Intent());
            }
        }
    }

    // ............................ TRANSFORMERS ..................................

    static Transformer<SpringOauthToken, Integer> fromTokenGetUserModified(final Usuario newUser)
    {
        return new Transformer<SpringOauthToken, Integer>() {
            @Override
            public Single<Integer> call(Single<SpringOauthToken> oauthToken)
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

    static Transformer<Integer, SpringOauthToken> fromUserModifiedGetToken(final Usuario newUser)
    {
        return new Transformer<Integer, SpringOauthToken>() {
            @Override
            public Single<SpringOauthToken> call(Single<Integer> integerSingle)
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
