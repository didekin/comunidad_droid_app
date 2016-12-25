package com.didekinaar.security;

import com.didekin.oauth2.SpringOauthToken;
import com.didekin.usuario.Usuario;

import java.util.concurrent.Callable;

import rx.Single;

import static com.didekinaar.security.Oauth2DaoRemote.Oauth2;
import static rx.Single.fromCallable;

/**
 * User: pedro@didekin
 * Date: 27/11/16
 * Time: 14:35
 */
public final class OauthTokenObservable {

    //  =====================================================================================================
    //    .................................... OBSERVABLES .................................
    //  =====================================================================================================

    public static Single<SpringOauthToken> getOauthTokenGetSingle(Usuario usuario)
    {
        // TODO: test.
        return fromCallable(new OauthTokenGetCallable(usuario));
    }

    //  =====================================================================================================
    //    .................................... HELPER INNER CLASSES .................................
    //  =====================================================================================================

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
