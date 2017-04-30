package com.didekindroid.usuario.userdata;

import android.os.Bundle;

import com.didekindroid.api.Controller;
import com.didekindroid.security.IdentityCacher;
import com.didekinlib.http.oauth2.SpringOauthToken;
import com.didekinlib.model.usuario.Usuario;

import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
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
 * Date: 23/02/17
 * Time: 10:58
 */
@SuppressWarnings("WeakerAccess")
class CtrlerUserData extends Controller implements CtrlerUserDataIf {

    CtrlerUserData(ViewerUserDataIf viewer)
    {
        this(viewer, TKhandler);
    }

    CtrlerUserData(ViewerUserDataIf viewer, IdentityCacher identityCacher)
    {
        super(viewer, identityCacher);
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

    static Completable userModifiedTkUpdated(final SpringOauthToken oldUserToken, final Usuario newUser)
    {
        return fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception
            {
                return usuarioDao.modifyUserWithToken(oldUserToken, newUser);
            }
        }).flatMap(new Function<Integer, SingleSource<SpringOauthToken>>() {
            @Override
            public SingleSource<SpringOauthToken> apply(Integer modifiedUser) throws Exception
            {
                assertTrue(modifiedUser == 1, user_should_have_been_modified);
                return oauthTokenFromUserPswd(newUser);
            }
        }).doOnSuccess(initTokenAction).toCompletable();
    }

    static Single<Boolean> userModifiedWithPswdValidation(Usuario oldUser, final Usuario newUser)
    {
        return oauthTokenFromUserPswd(oldUser)
                .flatMap(new Function<SpringOauthToken, Single<Boolean>>() {
                    @Override
                    public Single<Boolean> apply(SpringOauthToken oldUserToken) throws Exception
                    {
                        return userModifiedTkUpdated(oldUserToken, newUser).toSingleDefault(Boolean.TRUE);
                    }
                });
    }

    // .................................... INSTANCE METHODS .................................

    @Override
    public boolean loadUserData()
    {
        Timber.d("loadUserData()");
        return subscriptions.add(
                userDataLoaded()
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new LoadedUserObserver())
        );
    }

    @Override
    public boolean modifyUser(Usuario oldUser, Usuario newUser)
    {
        Timber.d("modifyUser()");
        return subscriptions.add(
                userModifiedWithPswdValidation(oldUser, newUser)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new ModifyUserObserver()));
    }

    // .............................. SUBSCRIBERS ..................................

    class ModifyUserObserver extends DisposableSingleObserver<Boolean> {

        @Override
        public void onSuccess(Boolean isCompleted)
        {
            Timber.d("onSuccess(), isCompleted == %s", isCompleted.toString());
            assertTrue(isCompleted, "ModifyUserObserver.onSuccess() should be TRUE");
            ViewerUserData.class.cast(viewer).replaceComponent(new Bundle());
        }

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onErrorCtrl(), Thread for subscriber: %s", Thread.currentThread().getName());
            onErrorCtrl(e);
        }
    }

    class LoadedUserObserver extends DisposableSingleObserver<Usuario> {

        @Override
        public void onSuccess(Usuario usuario)
        {
            Timber.d("onSuccess(), Thread for subscriber: %s", Thread.currentThread().getName());
            assertTrue(usuario.getuId() > 0L && usuario.getUserName() != null, user_name_uID_should_be_initialized);
            ViewerUserData.class.cast(viewer).processBackUserDataLoaded(usuario);
        }

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onErrorCtrl(), Thread for subscriber: %s", Thread.currentThread().getName());
            onErrorCtrl(e);
        }
    }
}

