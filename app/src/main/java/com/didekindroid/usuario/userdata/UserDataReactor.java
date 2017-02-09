package com.didekindroid.usuario.userdata;


import com.didekindroid.security.IdentityCacher;
import com.didekinlib.http.oauth2.SpringOauthToken;
import com.didekinlib.model.usuario.Usuario;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.security.OauthTokenReactor.oauthTokenFromUserPswd;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.security.TokenIdentityCacher.initTokenAction;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_name_uID_should_be_initialized;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_have_been_modified;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.util.UIutils.assertTrue;
import static io.reactivex.Single.fromCallable;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 20/12/16
 * Time: 18:57
 */
@SuppressWarnings("WeakerAccess")
final class UserDataReactor implements UserDataReactorIf {

    static final UserDataReactorIf userDataReactor = new UserDataReactor();
    static final IdentityCacher tokenCacher = TKhandler;

    private UserDataReactor()
    {
    }

    // .................................... OBSERVABLES .................................

    static Single<Usuario> userDataLoaded()
    {
        return fromCallable(new Callable<Usuario>() {
            @Override
            public Usuario call() throws Exception
            {
                return usuarioDao.getUserData();
            }
        });
    }

    static Single<Integer> userDataModified(final SpringOauthToken oauthToken, final Usuario newUser)
    {
        return fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception
            {
                return usuarioDao.modifyUserWithToken(oauthToken, newUser);
            }
        });
    }

    static Single<SpringOauthToken> userModifiedUpdatedToken(final SpringOauthToken oauthToken, final Usuario newUser){
        return userDataModified(oauthToken, newUser)
                .flatMap(new Function<Integer, SingleSource<SpringOauthToken>>() {
                    @Override
                    public SingleSource<SpringOauthToken> apply(Integer modifiedUser) throws Exception
                    {
                        assertTrue(modifiedUser == 1, user_should_have_been_modified);
                        return oauthTokenFromUserPswd(newUser);
                    }
                });
    }

    static Completable userModifiedCacheUpdated(Usuario oldUser, final Usuario newUser)
    {
        final AtomicReference<SpringOauthToken> atomicToken = new AtomicReference<>();

        return oauthTokenFromUserPswd(oldUser)
                .flatMap(new Function<SpringOauthToken, SingleSource<SpringOauthToken>>() {
                    @Override
                    public SingleSource<SpringOauthToken> apply(SpringOauthToken oauthToken) throws Exception
                    {
                        return userModifiedUpdatedToken(oauthToken, newUser);
                    }
                }).doOnSuccess(initTokenAction).toCompletable();
    }

    // ............................ SUBSCRIPTIONS ..................................

    @Override
    public boolean loadUserData(UserDataControllerIf controller)
    {
        return controller.getSubscriptions().add(
                userDataLoaded()
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new LoadedUserObserver(controller))
        );
    }

    @Override
    public boolean modifyUser(UserDataControllerIf controller, Usuario oldUser, Usuario newUser)
    {
        return controller.getSubscriptions().add(
                userModifiedCacheUpdated(oldUser, newUser)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new ModifyUserObserver(controller)));
    }

    // .............................. SUBSCRIBERS ..................................

    private static class ModifyUserObserver extends DisposableCompletableObserver {

        final UserDataControllerIf controller;

        ModifyUserObserver(UserDataControllerIf controller)
        {
            this.controller = controller;
        }

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onError(), Thread for subscriber: %s", Thread.currentThread().getName());
            controller.processBackErrorInReactor(e);
        }

        @Override
        public void onComplete()
        {
            Timber.d("onSuccess(), Thread for subscriber: %s", Thread.currentThread().getName());
            controller.processBackUserModified();
        }
    }

    private static class LoadedUserObserver extends DisposableSingleObserver<Usuario> {

        final UserDataControllerIf controller;

        LoadedUserObserver(UserDataControllerIf controller)
        {
            this.controller = controller;
        }

        @Override
        public void onSuccess(Usuario usuario)
        {
            Timber.d("onSuccess(), Thread for subscriber: %s", Thread.currentThread().getName());
            assertTrue(usuario.getuId() > 0L && usuario.getUserName() != null, user_name_uID_should_be_initialized);
            controller.processBackUserDataLoaded(usuario);
        }

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onError(), Thread for subscriber: %s", Thread.currentThread().getName());
            controller.processBackErrorInReactor(e);
        }
    }
}
