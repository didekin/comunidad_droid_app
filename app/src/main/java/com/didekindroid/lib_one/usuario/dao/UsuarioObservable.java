package com.didekindroid.lib_one.usuario.dao;

import com.didekindroid.lib_one.security.IdentityCacherIf;
import com.didekinlib.model.usuario.Usuario;

import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Single;
import timber.log.Timber;

import static com.didekindroid.lib_one.security.OauthTokenObservable.oauthTokenAndInitCache;
import static com.didekindroid.lib_one.security.OauthTokenObservable.oauthTokenFromUserPswd;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.usuario.UsuarioAssertionMsg.user_name_should_be_initialized;
import static com.didekindroid.lib_one.usuario.dao.UsuarioDao.usuarioDaoRemote;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;
import static io.reactivex.Single.fromCallable;
import static io.reactivex.Single.just;
import static java.lang.Boolean.TRUE;

/**
 * User: pedro@didekin
 * Date: 31/10/2017
 * Time: 11:19
 */

final class UsuarioObservable {

    private static final UsuarioDaoIf userDao = usuarioDaoRemote;
    private static final IdentityCacherIf tkCacher = secInitializer.get().getTkCacher();

    // ===============================  Observables  =================================

    static final Single<Boolean> deleteMeSingle = fromCallable(userDao::deleteUser).map(tkCacher.getCleanIdentityFunc());
    static final Single<Usuario> userDataSingle = fromCallable(userDao::getUserData);

    /**
     * It has a mock test implementation. It clears token in cache.
     */
    static Single<Boolean> loginPswdSendSingle(final Callable<Boolean> sendPswdCall)
    {
        Timber.d("loginPswdSendSingle()");
        return fromCallable(sendPswdCall).doOnSuccess(isUserModified -> {
            if (isUserModified) {
                tkCacher.cleanIdentityCache();
            }
        });
    }

    static Single<Boolean> loginUpdateTkCache(final Usuario usuario)
    {
        Timber.d("loginUpdateTkCache()");
        return fromCallable(
                () -> userDao.loginInternal(usuario.getUserName(), usuario.getPassword())
        ).flatMap(
                isLoginValid -> {
                    if (isLoginValid) {
                        return oauthTokenAndInitCache(usuario).toSingleDefault(true);
                    }
                    return just(false);
                }
        );
    }

    /**
     * Password change submitting the current password.
     */
    static Completable passwordChangeWithPswdValidation(final Usuario oldUser, final Usuario newUser)
    {
        Timber.d("passwordChangeWithPswdValidation()");
        return oauthTokenFromUserPswd(oldUser)
                .flatMapCompletable(
                        oldOauthToken ->
                                fromCallable(() -> userDao.passwordChange(oldOauthToken, newUser.getPassword()))
                                        .flatMapCompletable(passwordUpdated -> oauthTokenAndInitCache(newUser))
                );
    }

    static Single<Boolean> userAliasModified(Usuario oldUser, final Usuario newUser)
    {
        Timber.d("userAliasModified()");
        assertTrue(newUser.getUserName() != null, user_name_should_be_initialized);
        return oauthTokenFromUserPswd(oldUser)
                .flatMap(oldUserToken -> Completable.fromCallable(() -> userDao.modifyUserWithToken(oldUserToken, newUser))
                        .toSingleDefault(TRUE));
    }

    static Single<Boolean> userNameModified(Usuario oldUser, final Usuario newUser)
    {
        Timber.d("userNameModified()");
        assertTrue(newUser.getUserName() != null, user_name_should_be_initialized);
        return oauthTokenFromUserPswd(oldUser)
                .flatMap(oldUserToken -> Completable.fromCallable(() -> userDao.modifyUserWithToken(oldUserToken, newUser))
                        .doOnComplete(((UsuarioDao) userDao).getTkCacher()::cleanIdentityCache)
                        .toSingleDefault(TRUE));
    }
}
