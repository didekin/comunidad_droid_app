package com.didekinaar.usuario;

import com.didekin.oauth2.SpringOauthToken;
import com.didekin.usuario.Usuario;

import java.util.concurrent.Callable;

import rx.Single;
import rx.functions.Func2;

import static com.didekinaar.PrimalCreator.creator;
import static com.didekinaar.security.Oauth2Service.Oauth2;
import static com.didekinaar.security.TokenHandler.TKhandler;
import static com.didekinaar.usuario.UsuarioService.AarUserServ;
import static com.didekinaar.utils.UIutils.updateIsRegistered;
import static rx.Single.fromCallable;

/**
 * User: pedro@didekin
 * Date: 27/11/16
 * Time: 14:35
 */
public class UsuarioObservables {

//  ==============================  ACCESSOR METHODS =====================================

    public static Single<Boolean> getDeleteMeSingle()
    {
        return fromCallable(new DeleteMeCallable());
    }

    private static Single<Boolean> getLoginValidateSingle(Usuario usuario)
    {
        return fromCallable(new LoginValidateCallable(usuario));
    }

    // TODO: extraer un Completable para TKhandler.initTokenAndBackupFile(token);
    public static Single<Boolean> getZipLoginSingle(Usuario usuario)
    {
        return Single.zip(
                getLoginValidateSingle(usuario),
                getOauthTokenGetSingle(usuario),
                new Func2<Boolean, SpringOauthToken, Boolean>() {
                    @Override
                    public Boolean call(final Boolean isLoginValid, final SpringOauthToken token)
                    {
                        if (isLoginValid && token != null) {
                            TKhandler.initTokenAndBackupFile(token);
                            updateIsRegistered(true, creator.get().getContext());
                        }
                        return isLoginValid;
                    }
                });
    }

    public static Single<Boolean> getLoginMailSingle(String email)
    {
        return fromCallable(new LoginMailCallable(email));
    }

    private static Single<SpringOauthToken> getOauthTokenGetSingle(Usuario usuario)
    {
        return fromCallable(new OauthTokenGetCallable(usuario));
    }

    public static Single<Usuario> getUserDataSingle()
    {
        return fromCallable(new Callable<Usuario>() {
            @Override
            public Usuario call() throws Exception
            {
                return AarUserServ.getUserData();
            }
        });
    }

//  ================================  STATIC CLASSES =====================================

    private static class DeleteMeCallable implements Callable<Boolean> {

        DeleteMeCallable()
        {
        }

        @Override
        public Boolean call() throws Exception
        {
            boolean isDeleted = AarUserServ.deleteUser();
            TKhandler.cleanTokenAndBackFile();
            updateIsRegistered(false, creator.get().getContext());
            return isDeleted;
        }
    }

    private static class LoginValidateCallable implements Callable<Boolean> {

        private final Usuario usuario;

        LoginValidateCallable(Usuario usuario)
        {
            this.usuario = usuario;
        }

        @Override
        public Boolean call() throws Exception
        {
            return AarUserServ.loginInternal(usuario.getUserName(), usuario.getPassword());
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
            return AarUserServ.passwordSend(email).execute().body();
        }
    }

    private static class OauthTokenGetCallable implements Callable<SpringOauthToken> {

        private final Usuario usuario;

        OauthTokenGetCallable(Usuario usuario)
        {
            this.usuario = usuario;
        }

        @Override
        public SpringOauthToken call() throws Exception
        {
            return Oauth2.getPasswordUserToken(usuario.getUserName(), usuario.getPassword());
        }
    }
}
