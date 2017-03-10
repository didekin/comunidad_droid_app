package com.didekindroid.usuario.userdata;

import android.view.View;

import com.didekindroid.api.ControllerIdentityAbs;
import com.didekindroid.incidencia.list.ManagerIncidSeeIf;
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
import static com.didekindroid.usuario.userdata.ControllerUserData.ReactorUserData.userDataReactor;
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
class ControllerUserData extends ControllerIdentityAbs implements ControllerUserDataIf {

    private final ViewerUserDataIf<View,Object> viewer;
    private final ReactorUserDataIf reactor;

    ControllerUserData(ViewerUserDataIf<View, Object> viewer)
    {
        this(viewer, userDataReactor, TKhandler);
    }

    ControllerUserData(ViewerUserDataIf<View, Object> viewer, ReactorUserDataIf reactor, IdentityCacher identityCacher)
    {
        super(identityCacher);
        this.viewer = viewer;
        this.reactor = reactor;
    }

    @Override
    public void loadUserData()
    {
        Timber.d("loadUserData()");
        reactor.loadUserData(this);
    }

    @Override
    public boolean modifyUser(Usuario oldUser, Usuario newUser)
    {
        Timber.d("modifyUser()");
        return reactor.modifyUser(this, oldUser, newUser);
    }

    @Override
    public void processBackUserDataLoaded(Usuario usuario)
    {
        Timber.d("processBackUserDataLoaded()");
        viewer.processBackUsuarioInView(usuario);
    }

    @Override
    public void processBackUserModified()
    {
        Timber.d("processBackUserModified()");
        viewer.getManager().replaceRootView(null);
    }

    @Override
    public ManagerIncidSeeIf.ViewerIf getViewer()
    {
        Timber.d("getViewer()");
        return viewer;
    }


    // ============================================================================================
    // ......................................... REACTOR ..........................................
    // ============================================================================================

    static final class ReactorUserData implements ControllerUserDataIf.ReactorUserDataIf {

        static final ReactorUserDataIf userDataReactor = new ReactorUserData();
        static final IdentityCacher tokenCacher = TKhandler;

        private ReactorUserData()
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

        static Single<SpringOauthToken> userModifiedUpdatedToken(final SpringOauthToken oauthToken, final Usuario newUser)
        {
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
        public boolean loadUserData(ControllerUserDataIf controller)
        {
            return controller.getSubscriptions().add(
                    userDataLoaded()
                            .subscribeOn(io())
                            .observeOn(mainThread())
                            .subscribeWith(new LoadedUserObserver(controller))
            );
        }

        @Override
        public boolean modifyUser(ControllerUserDataIf controller, Usuario oldUser, Usuario newUser)
        {
            return controller.getSubscriptions().add(
                    userModifiedCacheUpdated(oldUser, newUser)
                            .subscribeOn(io())
                            .observeOn(mainThread())
                            .subscribeWith(new ModifyUserObserver(controller)));
        }

        // .............................. SUBSCRIBERS ..................................

        private static class ModifyUserObserver extends DisposableCompletableObserver {

            final ControllerUserDataIf controller;

            ModifyUserObserver(ControllerUserDataIf controller)
            {
                this.controller = controller;
            }

            @Override
            public void onError(Throwable e)
            {
                Timber.d("onError(), Thread for subscriber: %s", Thread.currentThread().getName());
                controller.processReactorError(e);
            }

            @Override
            public void onComplete()
            {
                Timber.d("onSuccess(), Thread for subscriber: %s", Thread.currentThread().getName());
                controller.processBackUserModified();
            }
        }

        private static class LoadedUserObserver extends DisposableSingleObserver<Usuario> {

            final ControllerUserDataIf controller;

            LoadedUserObserver(ControllerUserDataIf controller)
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
                controller.processReactorError(e);
            }
        }
    }
}
