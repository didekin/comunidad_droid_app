package com.didekindroid.security;

import com.didekin.http.oauth2.SpringOauthToken;
import com.didekin.usuario.Usuario;

import java.util.concurrent.Callable;

import io.reactivex.Single;

import static com.didekindroid.security.Oauth2DaoRemote.Oauth2;
import static io.reactivex.Single.fromCallable;

/**
 * User: pedro@didekin
 * Date: 27/11/16
 * Time: 14:35
 */
public final class OauthTokenObservable {

    //  =====================================================================================================
    //    .................................... OBSERVABLES .................................
    //  =====================================================================================================

    public static Single<SpringOauthToken> getOauthToken(final Usuario usuario)
    { // TODO: test.
        return fromCallable(new Callable<SpringOauthToken>() {
            @Override
            public SpringOauthToken call() throws Exception
            {
                return Oauth2.getPasswordUserToken(usuario.getUserName(), usuario.getPassword());
            }
        });
    }

}
