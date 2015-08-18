package com.didekindroid.usuario.webservices;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.didekindroid.common.IoHelper;
import com.didekindroid.usuario.comunidad.dominio.Usuario;
import com.didekindroid.usuario.comunidad.dominio.UsuarioComunidad;
import com.didekindroid.usuario.login.dominio.AccessToken;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;

import static com.didekindroid.usuario.DataUsuarioTestUtils.*;
import static com.didekindroid.usuario.login.TokenHandler.TKhandler;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static com.didekindroid.usuario.webservices.ServiceOneEndPoints.OAUTH_CLIENT_ID;
import static com.didekindroid.usuario.webservices.ServiceOneEndPoints.OAUTH_CLIENT_SECRET;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 06/08/15
 * Time: 12:07
 */
@RunWith(AndroidJUnit4.class)
public class ServiceOneTest {

    Context context;
    File refreshTkFile;

    @Before
    public void setUp() throws Exception
    {
        context = InstrumentationRegistry.getTargetContext();
        refreshTkFile = TKhandler.getRefreshTokenFile();
    }

    @Test
    public void testDeleteComunidadWithOneUser()
    {
        assertThat(refreshTkFile.exists(), is(false));
        insertOneUserOneComu();

        List<UsuarioComunidad> usuarioComunidades = ServOne.getUsuariosComunidad();

        boolean isDeleted = ServOne.deleteComunidad(usuarioComunidades.get(0).getComunidad().getC_Id());
        assertThat(isDeleted, is(true));

        // User cleanup.
        isDeleted = ServOne.deleteUser();
        assertThat(isDeleted, is(true));
    }

    @Test
    public void testDeleteUser()
    {
        // No file with refreshToken.
        assertThat(refreshTkFile.exists(), is(false));
        //Inserta usuario, comunidad, usuariocomunidad y actuliza tokenCache.
        insertOneUserOneComu();

        boolean isDeleted = ServOne.deleteUser();
        assertThat(isDeleted, is(true));
    }

    @Test
    public void testDoAuthBasicHeader() throws Exception
    {
        String encodedHeader = ServOne.doAuthBasicHeader(OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET);
        assertThat(encodedHeader, equalTo("Basic dXNlcjo="));
    }

    @Test
    public void testGetHostAndPort() throws Exception
    {
        String hostAndPort = ServOne.getHostAndPort();
        assertThat(hostAndPort, equalTo("http://10.0.3.2:9000"));
    }

    @Test
    public void testGetPswUserToken()
    {
        //Inserta usuario, comunidad, usuariocomunidad y actuliza tokenCache.
        insertOneUserOneComu();

        AccessToken token = ServOne.getPasswordUserToken(USUARIO.getUserName(), USUARIO.getPassword());
        assertThat(token, notNullValue());
        assertThat(token.getAccess_token(), notNullValue());
        assertThat(token.getRefresh_token(), notNullValue());

        // User cleanup.
        boolean isDeleted = ServOne.deleteUser();
        assertThat(isDeleted, is(true));
    }

    @Test
    public void testGetUserDataByNameOk()
    {
        //Inserta usuario, comunidad, usuariocomunidad y actuliza tokenCache.
        insertOneUserOneComu();

        Usuario usuario = ServOne.getUserData();
        assertThat(usuario.getUserName(), is(USUARIO.getUserName()));
        assertThat(usuario.getPrefixTf(), is(USUARIO.getPrefixTf()));
        assertThat(usuario.getNumeroTf(), is(USUARIO.getNumeroTf()));

        // User cleanup.
        boolean isDeleted = ServOne.deleteUser();
        assertThat(isDeleted, is(true));
    }

    @Test
    public void testGetUsuariosComunidad_1() throws Exception
    {
        // No token in cache.
        assertThat(TKhandler.doBearerAccessTkHeader(), nullValue());
        assertThat(ServOne.getUsuariosComunidad(), nullValue());
    }

    @Test
    public void testGetUsuariosComunidad_2() throws Exception
    {
        //Inserta usuario, comunidad, usuariocomunidad y actuliza tokenCache.
        insertOneUserOneComu();

        List<UsuarioComunidad> comunidadesUser = ServOne.getUsuariosComunidad();
        assertThat(comunidadesUser.size(), is(1));
        assertThat(comunidadesUser, hasItem(USUARIO_COMUNIDAD_1));

        ServOne.insertUserOldComunidadNew(USUARIO_COMUNIDAD_2);
        comunidadesUser = ServOne.getUsuariosComunidad();
        assertThat(comunidadesUser.size(), is(2));

        // User cleanup.
        boolean isDeleted = ServOne.deleteUser();
        assertThat(isDeleted, is(true));
    }

    @Test
    public void testInsertUserOldComunidadNew() throws Exception
    {
        assertThat(refreshTkFile.exists(), is(false));
        //Inserta usuario, comunidad, usuariocomunidad y actuliza tokenCache.
        insertOneUserOneComu();

        Usuario usuario = ServOne.insertUserOldComunidadNew(USUARIO_COMUNIDAD_2);
        assertThat(usuario.getUserName(), is(USUARIO.getUserName()));
        assertThat(usuario.getUsuariosComunidad(), hasItems(USUARIO_COMUNIDAD_1, USUARIO_COMUNIDAD_2));

        // User cleanup.
        boolean isDeleted = ServOne.deleteUser();
        assertThat(isDeleted, is(true));
    }

    @Test
    public void testSignedUpRight()
    {
        assertThat(refreshTkFile.exists(), is(false));

        //Inserta usuario, comunidad, usuariocomunidad y actuliza tokenCache.
        insertOneUserOneComu();

        assertThat(refreshTkFile.exists(), is(true));
        AccessToken tokenJuan = TKhandler.getAccessTokenInCache();
        assertThat(tokenJuan, notNullValue());
        assertThat(tokenJuan.getAccess_token(), not(isEmptyOrNullString()));
        assertThat(IoHelper.readStringFromFile(refreshTkFile), is(tokenJuan.getRefresh_token()));

        // User cleanup.
        boolean isDeleted = ServOne.deleteUser();
        assertThat(isDeleted, is(true));
    }

    @After
    public void cleaningUp()
    {
        if (refreshTkFile.exists()) {
            refreshTkFile.delete();
        }
        TKhandler.getTokensCache().invalidateAll();
        TKhandler.updateRefreshToken(null);
    }
}