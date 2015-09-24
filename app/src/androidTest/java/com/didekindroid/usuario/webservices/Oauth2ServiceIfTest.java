package com.didekindroid.usuario.webservices;

import android.support.test.runner.AndroidJUnit4;
import com.didekin.retrofitcl.ServiceOneException;
import com.didekin.security.OauthEndPointsIf;
import com.didekin.security.OauthToken.AccessToken;
import com.didekindroid.usuario.activity.utils.CleanEnum;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import retrofit.client.Response;

import static com.didekin.exception.ExceptionMessage.NOT_FOUND;
import static com.didekin.security.OauthClient.CL_USER;
import static com.didekindroid.usuario.activity.utils.CleanEnum.*;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.cleanOptions;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.signUpAndUpdateTk;
import static com.didekindroid.usuario.dominio.DomainDataUtils.*;
import static com.didekindroid.usuario.security.TokenHandler.TKhandler;
import static com.didekindroid.usuario.webservices.Oauth2Service.Oauth2;
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

    CleanEnum whatClean;

    @Before
    public void setUp() throws Exception
    {
        whatClean = CLEAN_NOTHING;
    }

    @Test
    public void testGetHello() throws Exception
    {
        OauthEndPointsIf.BodyText textJson = Oauth2.getHello();
        assertThat(textJson.getText(), equalTo("Hello Open"));
    }

    @Test
    public void testGetHelloUserRead() throws Exception
    {
        //Inserta usuario, comunidad, usuariocomunidad y actuliza tokenCache.
        signUpAndUpdateTk(COMU_REAL_PEPE);
        OauthEndPointsIf.BodyText bodyText = Oauth2.getHelloUserRead(TKhandler.doBearerAccessTkHeader());
        assertThat(bodyText.getText(), equalTo("Hello UserRead"));

        whatClean = CLEAN_PEPE;
    }

    @Test
    public void testGetNotFoundMsg()
    {
        try {
            Response errorMessage = Oauth2.getNotFoundMsg();
            fail();
        } catch (ServiceOneException e) {
            assertThat(e.getMessage(), is(NOT_FOUND.getMessage()));
            assertThat(e.getHttpStatus(), is(NOT_FOUND.getHttpStatus()));
        }
    }

    @Test
    public void testGetPasswordUserToken() throws Exception
    {
        whatClean = CLEAN_JUAN;

        //Inserta usuario, comunidad, usuariocomunidad y actuliza tokenCache.
        signUpAndUpdateTk(COMU_REAL_JUAN);

        AccessToken token = Oauth2.getPasswordUserToken(USER_JUAN.getUserName(), USER_JUAN.getPassword());
        assertThat(token, notNullValue());
        assertThat(token.getValue(), notNullValue());
        assertThat(token.getRefreshToken().getValue(), notNullValue());
    }

    @Test
    public void testGetRefreshUserToken() throws Exception
    {
        whatClean = CLEAN_PEPE;

        //Inserta usuario, comunidad, usuariocomunidad y actuliza tokenCache.
        signUpAndUpdateTk(COMU_REAL_PEPE);
        AccessToken tokenOld = TKhandler.getAccessTokenInCache();
        String accessTkOldValue = tokenOld.getValue();
        String refreshTkOldValue = tokenOld.getRefreshToken().getValue();

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
    public void cleaningUp()
    {
        cleanOptions(whatClean);
    }
}