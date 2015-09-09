package com.didekindroid.usuario.webservices;

import android.support.test.runner.AndroidJUnit4;
import com.didekin.security.OauthToken;
import com.didekindroid.usuario.CleanEnum;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekin.security.OauthClient.CL_USER;
import static com.didekindroid.usuario.CleanEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.UsuarioTestUtils.cleanOptions;
import static com.didekindroid.usuario.UsuarioTestUtils.signUpAndUpdateTk;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_REAL_JUAN;
import static com.didekindroid.usuario.dominio.DomainDataUtils.USER_JUAN;
import static com.didekindroid.usuario.webservices.Oauth2Service.Oauth2;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

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
    }

    @Test
    public void testGetHello() throws Exception
    {
        // TODO: implementar.
    }

    @Test
    public void testGetHelloUserRead() throws Exception
    {
        // TODO: implementar.
    }

    @Test
    public void testGetPasswordUserToken() throws Exception
    {
        //Inserta usuario, comunidad, usuariocomunidad y actuliza tokenCache.
        signUpAndUpdateTk(COMU_REAL_JUAN);

        OauthToken.AccessToken token = Oauth2.getPasswordUserToken(USER_JUAN.getUserName(), USER_JUAN.getPassword());
        assertThat(token, notNullValue());
        assertThat(token.getValue(), notNullValue());
        assertThat(token.getRefreshToken().getValue(), notNullValue());

        whatClean = CLEAN_JUAN;
    }

    @Test
    public void testGetRefreshUserToken() throws Exception
    {
        // TODO: implementar.
    }

    @Test
    public void testDoAuthBasicHeade()
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