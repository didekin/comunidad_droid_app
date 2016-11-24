package com.didekinaar.security;

import android.support.test.runner.AndroidJUnit4;

import com.didekin.common.exception.ErrorBean;
import com.didekin.oauth2.SpringOauthToken;
import com.didekinaar.exception.UiAarException;
import com.didekinaar.testutil.AarActivityTestUtils;
import com.didekinaar.testutil.CleanUserEnum;
import com.didekinaar.testutil.UsuarioTestUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import retrofit2.Response;

import static com.didekin.common.exception.DidekinExceptionMsg.BAD_REQUEST;
import static com.didekin.common.exception.DidekinExceptionMsg.NOT_FOUND;
import static com.didekin.oauth2.OauthClient.CL_USER;
import static com.didekin.oauth2.OauthTokenHelper.HELPER;
import static com.didekinaar.PrimalCreator.creator;
import static com.didekinaar.security.Oauth2Service.Oauth2;
import static com.didekinaar.security.TokenHandler.TKhandler;
import static com.didekinaar.testutil.AarActivityTestUtils.cleanWithTkhandler;
import static com.didekinaar.testutil.AarActivityTestUtils.signUpAndUpdateTk;
import static com.didekinaar.testutil.AarActivityTestUtils.updateSecurityData;
import static com.didekinaar.usuario.AarUsuarioService.AarUserServ;
import static com.didekinaar.usuariocomunidad.AarUserComuService.AarUserComuServ;
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

    CleanUserEnum whatClean = CleanUserEnum.CLEAN_NOTHING;

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(4000);
    }

    @Before
    public void setUp() throws Exception
    {
        Thread.sleep(1000);
    }

    @After
    public void cleaningUp() throws UiAarException
    {
        AarActivityTestUtils.cleanOptions(whatClean);
    }

    @Test
    public void testGetNotFoundMsg() throws IOException
    {
        Response<ErrorBean> response = Oauth2.getNotFoundMsg().execute();
        assertThat(response.isSuccessful(), is(false));
        assertThat(creator.get().getRetrofitHandler().getErrorBean(response).getMessage(), is(NOT_FOUND.getHttpMessage()));
    }

    @Test
    public void testGetPasswordUserToken_1() throws Exception
    {
        whatClean = CleanUserEnum.CLEAN_JUAN;

        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        boolean isRegistered = AarUserComuServ.regComuAndUserAndUserComu(UsuarioTestUtils.COMU_REAL_JUAN).execute().body();
        assertThat(isRegistered, is(true));
        // Solicita token.
        SpringOauthToken token = Oauth2.getPasswordUserToken(UsuarioTestUtils.USER_JUAN.getUserName(), UsuarioTestUtils.USER_JUAN.getPassword());
        assertThat(token, notNullValue());
        assertThat(token.getValue(), notNullValue());
        assertThat(token.getRefreshToken().getValue(), notNullValue());
    }

    @Test
    public void testGetPasswordUserToken_2() throws Exception
    {
        whatClean = CleanUserEnum.CLEAN_JUAN;

        //Inserta userComu, comunidad y usuariocomunidad.
        boolean isRegistered = AarUserComuServ.regComuAndUserAndUserComu(UsuarioTestUtils.COMU_REAL_JUAN).execute().body();
        assertThat(isRegistered, is(true));
        // Solicita token y actuliza tokenCache.
        updateSecurityData(UsuarioTestUtils.COMU_REAL_JUAN.getUsuario().getUserName(), UsuarioTestUtils.COMU_REAL_JUAN.getUsuario().getPassword());
        // Vuelve a solicitar token.
        SpringOauthToken token = Oauth2.getPasswordUserToken(UsuarioTestUtils.USER_JUAN.getUserName(), UsuarioTestUtils.USER_JUAN.getPassword());
        assertThat(token, notNullValue());
        assertThat(token.getValue(), notNullValue());
        assertThat(token.getRefreshToken().getValue(), notNullValue());
    }

    @Test
    public void testGetPasswordUserToken_3() throws Exception
    {
        //Inserta userComu, comunidad, usuariocomunidad.
        boolean isRegistered = AarUserComuServ.regComuAndUserAndUserComu(UsuarioTestUtils.COMU_REAL_DROID).execute().body();
        assertThat(isRegistered, is(true));
        updateSecurityData(UsuarioTestUtils.USER_DROID.getUserName(), UsuarioTestUtils.USER_DROID.getPassword());
        // Envía correo.
        boolean isPasswordSend = AarUserServ.passwordSend(UsuarioTestUtils.USER_DROID.getUserName()).execute().body();
        assertThat(isPasswordSend,is(true));

        // Old pair userName/password is invalid: passwordSend implies new password in BD.
        try {
            Oauth2.getPasswordUserToken(UsuarioTestUtils.USER_DROID.getUserName(), UsuarioTestUtils.USER_DROID.getPassword());
            fail();
        } catch (UiAarException e) {
            assertThat(e.getErrorBean().getMessage(), is(BAD_REQUEST.getHttpMessage()));
        }

        // Es necesario conseguir un nuevo token.
        SpringOauthToken token = Oauth2.getRefreshUserToken(TKhandler.getRefreshTokenValue());
        assertThat(token, notNullValue());
        assertThat(token.getValue(), notNullValue());
        assertThat(token.getRefreshToken().getValue(), notNullValue());

        AarUserServ.deleteUser(HELPER.doBearerAccessTkHeader(token)).execute();
        cleanWithTkhandler();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testGetRefreshUserToken() throws Exception
    {
        whatClean = CleanUserEnum.CLEAN_PEPE;

        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        signUpAndUpdateTk(UsuarioTestUtils.COMU_REAL_PEPE);
        SpringOauthToken tokenOld = TKhandler.getAccessTokenInCache();
        String accessTkOldValue = tokenOld.getValue();
        String refreshTkOldValue = tokenOld.getRefreshToken().getValue();

        SpringOauthToken tokenNew = Oauth2.getRefreshUserToken(TKhandler.getRefreshTokenValue());
        assertThat(tokenNew, notNullValue());
        // Return mew access and refresh tokens.
        assertThat(tokenNew.getRefreshToken().getValue(), not(is(refreshTkOldValue)));
        assertThat(tokenNew.getValue(), not(is(accessTkOldValue)));
    }

    @Test
    public void testDoAuthBasicHeader()
    {
        String encodedHeader = Oauth2.doAuthBasicHeader(CL_USER);
        assertThat(encodedHeader, equalTo("Basic dXNlcjo="));
    }
}