package com.didekindroid.common.webservices;

import android.support.test.runner.AndroidJUnit4;

import com.didekin.common.exception.InServiceException;
import com.didekin.common.oauth2.OauthToken.AccessToken;
import com.didekindroid.common.UiException;
import com.didekindroid.usuario.activity.utils.CleanEnum;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekin.common.exception.DidekinExceptionMsg.BAD_REQUEST;
import static com.didekin.common.exception.DidekinExceptionMsg.NOT_FOUND;
import static com.didekin.common.oauth2.OauthClient.CL_USER;
import static com.didekin.common.oauth2.OauthTokenHelper.HELPER;
import static com.didekindroid.common.TokenHandler.TKhandler;
import static com.didekindroid.usuario.activity.utils.CleanEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.activity.utils.CleanEnum.CLEAN_NOTHING;
import static com.didekindroid.usuario.activity.utils.CleanEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.cleanOptions;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.cleanWithTkhandler;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.signUpAndUpdateTk;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.updateSecurityData;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_REAL_JUAN;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_REAL_PEPE;
import static com.didekindroid.usuario.dominio.DomainDataUtils.USER_JUAN;
import static com.didekindroid.usuario.dominio.DomainDataUtils.USER_PEPE;
import static com.didekindroid.common.webservices.Oauth2Service.Oauth2;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
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
    public void testGetNotFoundMsg()
    {
        try {
            Oauth2.getNotFoundMsg();
            fail();
        } catch (InServiceException e) {
            assertThat(e.getHttpMessage(), is(NOT_FOUND.getHttpMessage()));
            assertThat(e.getHttpStatus(), is(NOT_FOUND.getHttpStatus()));
        }
    }

    @Test
    public void testGetPasswordUserToken_1() throws Exception
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
    public void testGetPasswordUserToken_2() throws Exception
    {
        //Inserta usuario, comunidad, usuariocomunidad.
        ServOne.regComuAndUserAndUserComu(COMU_REAL_PEPE);
        updateSecurityData(USER_PEPE.getUserName(), USER_PEPE.getPassword());
        // Envía correo.
        ServOne.passwordSend(USER_PEPE.getUserName());

        // Old pair userName/password is invalid.
        try {
            Oauth2.getPasswordUserToken(USER_PEPE.getUserName(), USER_PEPE.getPassword());
            fail();
        } catch (UiException e) {
            assertThat(e.getInServiceException(), CoreMatchers.notNullValue());
            assertThat(e.getInServiceException().getHttpMessage(), is(BAD_REQUEST.getHttpMessage()));
        }

        // Es necesario conseguir un nuevo token.
        AccessToken token = Oauth2.getRefreshUserToken(TKhandler.getRefreshTokenKey());
        assertThat(token, notNullValue());
        assertThat(token.getValue(), notNullValue());
        assertThat(token.getRefreshToken().getValue(), notNullValue());

        ServOne.deleteUser(HELPER.doBearerAccessTkHeader(token));
        cleanWithTkhandler();
    }

    @Test
    public void testGetRefreshUserToken() throws Exception
    {
        whatClean = CLEAN_PEPE;

        //Inserta usuario, comunidad, usuariocomunidad y actuliza tokenCache.
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