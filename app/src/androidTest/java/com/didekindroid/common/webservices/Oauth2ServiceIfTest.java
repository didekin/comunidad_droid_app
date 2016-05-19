package com.didekindroid.common.webservices;

import android.support.test.runner.AndroidJUnit4;

import com.didekin.common.exception.ErrorBean;
import com.didekin.common.oauth2.OauthToken.AccessToken;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.usuario.testutils.CleanUserEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import retrofit2.Response;

import static com.didekin.common.exception.DidekinExceptionMsg.BAD_REQUEST;
import static com.didekin.common.exception.DidekinExceptionMsg.NOT_FOUND;
import static com.didekin.common.oauth2.OauthClient.CL_USER;
import static com.didekin.common.oauth2.OauthTokenHelper.HELPER;
import static com.didekindroid.common.TokenHandler.TKhandler;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanWithTkhandler;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.testutils.ActivityTestUtils.updateSecurityData;
import static com.didekindroid.common.webservices.Oauth2Service.Oauth2;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_NOTHING;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_REAL_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_REAL_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.USER_PEPE;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
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
@RunWith(AndroidJUnit4.class)
public class Oauth2ServiceIfTest {

    CleanUserEnum whatClean;

    @Before
    public void setUp() throws Exception
    {
        whatClean = CLEAN_NOTHING;
    }

    @Test
    public void testGetNotFoundMsg() throws IOException
    {
        Response<ErrorBean> response = Oauth2.getNotFoundMsg().execute();
        assertThat(response.isSuccessful(), is(false));
        assertThat(Oauth2.getRetrofitHandler().getErrorBean(response).getMessage(), is(NOT_FOUND.getHttpMessage()));
    }

    @Test
    public void testGetPasswordUserToken_1() throws Exception
    {
        whatClean = CLEAN_JUAN;

        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        boolean isRegistered = ServOne.regComuAndUserAndUserComu(COMU_REAL_JUAN).execute().body();
        assertThat(isRegistered, is(true));
        // Solicita token.
        AccessToken token = Oauth2.getPasswordUserToken(USER_JUAN.getUserName(), USER_JUAN.getPassword());
        assertThat(token, notNullValue());
        assertThat(token.getValue(), notNullValue());
        assertThat(token.getRefreshToken().getValue(), notNullValue());
    }

    @Test
    public void testGetPasswordUserToken_2() throws Exception
    {
        whatClean = CLEAN_JUAN;

        //Inserta userComu, comunidad y usuariocomunidad.
        boolean isRegistered = ServOne.regComuAndUserAndUserComu(COMU_REAL_JUAN).execute().body();
        assertThat(isRegistered, is(true));
        // Solicita token y actuliza tokenCache.
        updateSecurityData(COMU_REAL_JUAN.getUsuario().getUserName(), COMU_REAL_JUAN.getUsuario().getPassword());
        // Vuelve a solicitar token.
        AccessToken token = Oauth2.getPasswordUserToken(USER_JUAN.getUserName(), USER_JUAN.getPassword());
        assertThat(token, notNullValue());
        assertThat(token.getValue(), notNullValue());
        assertThat(token.getRefreshToken().getValue(), notNullValue());
    }

    @Test
    public void testGetPasswordUserToken_3() throws Exception
    {
        //Inserta userComu, comunidad, usuariocomunidad.
        boolean isRegistered = ServOne.regComuAndUserAndUserComu(COMU_REAL_PEPE).execute().body();
        assertThat(isRegistered, is(true));
        updateSecurityData(USER_PEPE.getUserName(), USER_PEPE.getPassword());
        // Envía correo.
        boolean isPasswordSend = ServOne.passwordSend(USER_PEPE.getUserName()).execute().body();
        assertThat(isPasswordSend,is(true));

        // Old pair userName/password is invalid: passwordSend implies new password in BD.
        try {
            Oauth2.getPasswordUserToken(USER_PEPE.getUserName(), USER_PEPE.getPassword());
            fail();
        } catch (UiException e) {
            assertThat(e.getErrorBean().getMessage(), is(BAD_REQUEST.getHttpMessage()));
        }

        // Es necesario conseguir un nuevo token.
        AccessToken token = Oauth2.getRefreshUserToken(TKhandler.getRefreshTokenKey());
        assertThat(token, notNullValue());
        assertThat(token.getValue(), notNullValue());
        assertThat(token.getRefreshToken().getValue(), notNullValue());

        ServOne.deleteUser(HELPER.doBearerAccessTkHeader(token)).execute();
        cleanWithTkhandler();
    }

    @Test
    public void testGetRefreshUserToken() throws Exception
    {
        whatClean = CLEAN_PEPE;

        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        signUpAndUpdateTk(COMU_REAL_PEPE);
        AccessToken tokenOld = TKhandler.getAccessTokenInCache();
        String accessTkOldValue = tokenOld != null ? tokenOld.getValue() : null;
        String refreshTkOldValue = tokenOld != null ? tokenOld.getRefreshToken().getValue() : null;

        AccessToken tokenNew = Oauth2.getRefreshUserToken(TKhandler.getRefreshTokenKey());
        assertThat(tokenNew, notNullValue());
        assertThat(tokenNew.getRefreshToken().getValue(), not(is(refreshTkOldValue)));
        assertThat(tokenNew.getValue(), not(is(accessTkOldValue)));

    }

    @Test
    public void testDoAuthBasicHeader()
    {
        String encodedHeader = Oauth2.doAuthBasicHeader(CL_USER);
        assertThat(encodedHeader, equalTo("Basic dXNlcjo="));
    }

    @After
    public void cleaningUp() throws UiException
    {
        cleanOptions(whatClean);
    }
}