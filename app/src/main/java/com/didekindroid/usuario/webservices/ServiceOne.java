package com.didekindroid.usuario.webservices;

import android.util.Base64;
import android.util.Log;
import com.didekindroid.R;
import com.didekindroid.usuario.comunidad.dominio.Comunidad;
import com.didekindroid.usuario.comunidad.dominio.Usuario;
import com.didekindroid.usuario.comunidad.dominio.UsuarioComunidad;
import com.didekindroid.usuario.login.dominio.AccessToken;

import java.util.List;

import static com.didekindroid.DidekindroidApp.getContext;
import static com.didekindroid.common.RetrofitRestBuilder.getService;
import static com.didekindroid.usuario.login.TokenHandler.TKhandler;
import static com.didekindroid.usuario.webservices.ServiceOneEndPoints.*;

/**
 * User: pedro@didekin
 * Date: 07/06/15
 * Time: 15:06
 */
public enum ServiceOne {

    ServOne,;

    private static final String TAG = ServiceOne.class.getCanonicalName();

    private final String hostAndPort;
    private final ServiceOneEndPoints endPoints;

    ServiceOne()
    {
        hostAndPort = getContext().getResources().getString(R.string.service_one_host)
                .concat(getContext().getResources().getString(R.string.service_one_port));
        endPoints = getService(ServiceOneEndPoints.class, hostAndPort);
    }

    public boolean deleteUser()
    {
        Log.d(TAG, "deleteUser()");
        return endPoints.deleteUser(TKhandler.doBearerAccessTkHeader());
    }

    public boolean deleteComunidad(long comunidadId)
    {
        Log.d(TAG, "deleteComunidad()");
        return endPoints.deleteComunidad(TKhandler.doBearerAccessTkHeader(), comunidadId);
    }

    public AccessToken getPasswordUserToken(String userName, String password)
    {
        Log.d(TAG, "getPasswordUserToken()");
        return endPoints.getPasswordUserToken(
                doAuthBasicHeader(OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET),
                userName,
                password,
                PASSWORD_GRANT);
    }

    public AccessToken getRefreshUserToken(String refreshTokenKey)
    {
        Log.d(TAG, "getRefreshUserToken()");
        return endPoints.getRefreshUserToken(
                doAuthBasicHeader(OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET),
                refreshTokenKey,
                REFRESH_TOKEN_GRANT);
    }

    public List<UsuarioComunidad> getUsuariosComunidad()
    {
        Log.d(TAG, "getUsuariosComunidad()");
        String bearerAccessTkHeader = TKhandler.doBearerAccessTkHeader();
        return (bearerAccessTkHeader != null ? endPoints.getUsuariosComunidad(bearerAccessTkHeader) : null);
    }

    public Usuario getUserData()
    {
        Log.d(TAG, ("getUserData()"));
        return endPoints.getUserData(TKhandler.doBearerAccessTkHeader());
    }

    public Usuario insertUserOldComunidadNew(UsuarioComunidad usuarioComunidad)
    {
        Log.d(TAG, "insertUserOldComunidadNew()");
        return endPoints.insertUserOldComunidadNew(TKhandler.doBearerAccessTkHeader(), usuarioComunidad);
    }

    public List<Comunidad> searchComunidades(Comunidad comunidad)
    {
        Log.d(TAG,"searchComunidades()");
        return endPoints.searchComunidades(comunidad);
    }

    public Usuario signUp(UsuarioComunidad usuarioComunidad)
    {
        Log.d(TAG, ("signUp()"));
        return endPoints.signUp(usuarioComunidad);
    }

//    .......... Utilities ..........

    String doAuthBasicHeader(String clientId, String clientSecret)
    {
        Log.d(TAG, "doAuthBasicHeader()");

        String base64AuthData = Base64.encodeToString(new String(clientId + ":" + clientSecret)
                .getBytes(), Base64.DEFAULT);
        // We cut the final \n character of the base64AuthData string.
        String header = new String("Basic " + base64AuthData.substring(0, base64AuthData.length() - 1));
        return header;
    }

    public String getHostAndPort()
    {
        return hostAndPort;
    }
}