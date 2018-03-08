package com.didekindroid.lib_one.security;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.http.HttpHandler;
import com.didekinlib.http.auth.SpringOauthToken;
import com.didekinlib.http.exception.ErrorBean;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import retrofit2.Response;

import static com.didekindroid.lib_one.HttpInitializer.httpInitializer;
import static com.didekindroid.lib_one.security.AuthDao.authDao;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.security.SecurityTestUtils.updateSecurityData;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOneUser;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanWithTkhandler;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real_rodrigo;
import static com.didekindroid.lib_one.usuario.UserTestData.user_crodrigo;
import static com.didekindroid.lib_one.usuario.UsuarioMockDao.usuarioMockDao;
import static com.didekinlib.http.auth.AuthClient.CL_USER;
import static com.didekinlib.http.exception.GenericExceptionMsg.NOT_FOUND;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.BAD_REQUEST;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 07/09/15
 * Time: 11:07
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class AuthDaoTest {

    private HttpHandler httpHandler = httpInitializer.get().getHttpHandler();

    @BeforeClass
    public static void slowSeconds()
    {
        cleanWithTkhandler();
    }

    @Test
    public void testGetNotFoundMsg() throws IOException
    {
        Response<ErrorBean> response = authDao.getNotFoundMsg().execute();
        assertThat(response.isSuccessful(), is(false));
        assertThat(httpHandler.getErrorBean(response).getMessage(), is(NOT_FOUND.getHttpMessage()));
    }

    @Test
    public void testDoAuthBasicHeader()
    {
        String encodedHeader = authDao.doAuthBasicHeader(CL_USER);
        assertThat(encodedHeader, equalTo("Basic dXNlcjo="));
    }

    @Test
    public void testGetPasswordUserToken_1() throws Exception
    {
        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        boolean isRegistered = usuarioMockDao.regComuAndUserAndUserComu(comu_real_rodrigo).execute().body();
        assertThat(isRegistered, is(true));
        // Solicita token.
        SpringOauthToken token = authDao.getPasswordUserToken(user_crodrigo.getUserName(), user_crodrigo.getPassword());
        assertThat(token, notNullValue());
        assertThat(token.getValue(), notNullValue());
        assertThat(token.getRefreshToken().getValue(), notNullValue());

        cleanOneUser(user_crodrigo);
    }

    @Test
    public void testGetPasswordUserToken_2() throws Exception
    {
        //Inserta userComu, comunidad y usuariocomunidad.
        assertThat(usuarioMockDao.regComuAndUserAndUserComu(comu_real_rodrigo).execute().body(), is(true));
        // Solicita token y actuliza tokenCache.
        updateSecurityData(comu_real_rodrigo.getUsuario().getUserName(), comu_real_rodrigo.getUsuario().getPassword());
        // Vuelve a solicitar token.
        SpringOauthToken token = authDao.getPasswordUserToken(user_crodrigo.getUserName(), user_crodrigo.getPassword());
        assertThat(token, notNullValue());
        assertThat(token.getValue(), notNullValue());
        assertThat(token.getRefreshToken().getValue(), notNullValue());

        cleanOneUser(user_crodrigo);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testGetRefreshUserToken_1() throws Exception
    {
        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        assertThat(usuarioMockDao.regComuAndUserAndUserComu(comu_real_rodrigo).execute().body(), is(true));
        updateSecurityData(user_crodrigo.getUserName(), user_crodrigo.getPassword());
        // Initial state.
        SpringOauthToken tokenOld = secInitializer.get().getTkCacher().getTokenCache().get();
        String accessTkOldValue = tokenOld.getValue();
        String refreshTkOldValue = tokenOld.getRefreshToken().getValue();
        // Execution change in state: new access and refresh tokens.
        SpringOauthToken tokenNew = authDao.getRefreshUserToken(refreshTkOldValue);
        assertThat(tokenNew, notNullValue());
        assertThat(tokenNew.getRefreshToken().getValue(), not(is(refreshTkOldValue)));
        assertThat(tokenNew.getValue(), not(is(accessTkOldValue)));

        cleanOneUser(user_crodrigo);
    }

    @Test
    public void testGetRefreshUserToken_2() throws Exception
    {
        usuarioMockDao.regComuAndUserAndUserComu(comu_real_rodrigo).execute().body();

        // Extraños efectos de 2 llamadas consecutivas a getPasswordUserToken().
        SpringOauthToken token0 = authDao.getPasswordUserToken(user_crodrigo.getUserName(), user_crodrigo.getPassword());
        assertThat(token0.getRefreshToken(), notNullValue());
        // Segunda llamada devuelve los mismos token y refreshToken.
        SpringOauthToken token1 = authDao.getPasswordUserToken(user_crodrigo.getUserName(), user_crodrigo.getPassword());
        assertThat(token1.getValue().equals(token0.getValue()), is(true));
        assertThat(token1.getRefreshToken().getValue().equals(token0.getRefreshToken().getValue()), is(true));
        // Da error al solicitar un nuevo token con getRefreshUserToken(): "detailMessage":"Invalid refresh token"
        try {
            authDao.getRefreshUserToken(token0.getRefreshToken().getValue());
            fail();
        } catch (UiException e) {
            assertThat(e.getErrorBean().getMessage(), is(BAD_REQUEST.getHttpMessage()));
        }

        try {
            authDao.getRefreshUserToken(token1.getRefreshToken().getValue());
            fail();
        } catch (UiException e) {
            assertThat(e.getErrorBean().getMessage(), is(BAD_REQUEST.getHttpMessage()));
        }

        cleanOneUser(user_crodrigo);
    }
}