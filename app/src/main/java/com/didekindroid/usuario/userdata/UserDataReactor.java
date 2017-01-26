package com.didekindroid.usuario.userdata;

import com.didekin.usuario.Usuario;
import com.didekindroid.security.OauthTokenReactorIf;
import com.didekindroid.usuario.userdata.UserDataControllerIf.UserChangeToMake;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.security.OauthTokenReactor.oauthTokenAndInitCache;
import static com.didekindroid.security.OauthTokenReactor.tokenReactor;
import static com.didekindroid.security.TokenIdentityCacher.cleanTokenCacheAction;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_name_password_should_be_initialized;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.usuario.userdata.UserDataControllerIf.UserChangeToMake.alias_only;
import static com.didekindroid.usuario.userdata.UserDataControllerIf.UserChangeToMake.userName;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.bean_fromView_should_be_initialized;
import static io.reactivex.Single.fromCallable;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 20/12/16
 * Time: 18:57
 */
@SuppressWarnings({"AnonymousInnerClassMayBeStatic", "WeakerAccess"})
final class UserDataReactor implements UserDataReactorIf {

    static final UserDataReactorIf userDataReactor = new UserDataReactor(tokenReactor);
    private OauthTokenReactorIf oauth2reactor;

    private UserDataReactor(OauthTokenReactorIf oauth2reactor)
    {
        this.oauth2reactor = oauth2reactor;
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

    static Single<Integer> userModified(final Usuario newUser)
    {
        return fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception
            {
                return usuarioDao.modifyUser(newUser);
            }
        });
    }

    static SingleSource<Integer> deletedTokenInBd(final String accessToken)
    {
        return fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception
            {
                return (usuarioDao.deleteAccessToken(accessToken) ? 1 : 0);
            }
        });
    }

    /**
     * It obtains the current oauthTokenFromUserPswd in remote and, once updated token in cache just for double check, it modifies user data.
     * If successful, it clears token in local cache.
     * Preconditions: olsUser name and password, and newUser name should be not null.
     * Postconditions: user name is updated, oauth token is deleted in database and token in cache is erased.
     */
    static Single<Integer> userNameModified(Usuario oldUser, final Usuario newUser)
    {
        assertTrue(oldUser.getUserName() != null
                && oldUser.getPassword() != null
                && newUser.getUserName() != null, user_name_password_should_be_initialized);
        return oauthTokenAndInitCache(oldUser)
                .andThen(userModified(newUser))
                .doOnSuccess(cleanTokenCacheAction);

        // TODO: en el servicio remoto hay que borrar el token en su tabla. Ver UsuarioSErvicio.
    }

    /**
     * Preconditions: user name must be null.
     * Postconditions: if alias is updated, an integer > 0 is returned (1).
     */
    static Single<Integer> aliasModified(final Usuario newUser)
    {
        assertTrue(newUser.getUserName() == null, bean_fromView_should_be_initialized);
        return fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception
            {
                return usuarioDao.modifyUser(
                        new Usuario.UsuarioBuilder()
                                .alias(newUser.getAlias())
                                .uId(newUser.getuId())
                                .build()
                );
            }
        });
    }

    // ............................ SUBSCRIPTIONS ..................................

    @Override
    public boolean getUserInRemote(UserDataControllerIf controller)
    {
        return controller.getSubscriptions().add(
                getUserDataSingle()
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new UserDataObserver<Usuario>(controller) {
                            @Override
                            public void onSuccess(Usuario usuario)
                            {
                                Timber.d("onSuccess(), Thread for subscriber: %s", Thread.currentThread().getName());
                                controller.processBackUserDataLoaded(usuario);
                            }
                        })
        );
    }

    @Override
    public boolean modifyUserInRemote(UserDataControllerIf controller, UserChangeToMake changeToMake, Usuario oldUser, Usuario newUser)
    {
        if (changeToMake == alias_only) {
            return controller.getSubscriptions().add(
                    aliasModified(newUser)
                            .subscribeOn(io())
                            .observeOn(mainThread())
                            .subscribeWith(new UserDataObserver<Integer>(controller) {
                                @Override
                                public void onSuccess(Integer aliasUpdated)
                                {
                                    Timber.d("onSuccess(), Thread for subscriber: %s", Thread.currentThread().getName());
                                    controller.processBackGenericUpdated();
                                }
                            })
            );
        }
        return changeToMake == userName
                && controller.getSubscriptions().add(
                userNameModified(oldUser, newUser)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new UserDataObserver<Integer>(controller) {
                            @Override
                            public void onSuccess(Integer updatedUsuario)
                            {
                                Timber.d("onSuccess(), Thread for subscriber: %s", Thread.currentThread().getName());
                                if (updatedUsuario > 0) {
                                    controller.processBackUserDataUpdated(true);
                                }
                            }
                        }));
    }

    @Override
    public void updateAndInitTokenCache(Usuario newUser)
    {
        oauth2reactor.updateTkAndCacheFromUser(newUser);
    }

    // .............................. SUBSCRIBERS ..................................

    static abstract class UserDataObserver<T> extends DisposableSingleObserver<T> {

        final UserDataControllerIf controller;

        UserDataObserver(UserDataControllerIf controller)
        {
            this.controller = controller;
        }

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onError(), Thread for subscriber: %s", Thread.currentThread().getName());
            controller.processBackErrorInReactor(e);
        }
    }
}
