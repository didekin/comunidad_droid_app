package com.didekindroid.security;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.testutil.UsuarioDataTestUtils;
import com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum;
import com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil;
import com.didekinlib.http.ErrorBean;
import com.didekinlib.http.oauth2.SpringOauthToken;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import retrofit2.Response;

import static com.didekindroid.AppInitializer.creator;
import static com.didekindroid.security.Oauth2DaoRemote.Oauth2;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.security.SecurityTestUtils.updateSecurityData;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_NOTHING;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.repository.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_DROID;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.http.GenericExceptionMsg.BAD_REQUEST;
import static com.didekinlib.http.GenericExceptionMsg.NOT_FOUND;
import static com.didekinlib.http.oauth2.OauthClient.CL_USER;
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
public class Oauth2DaoRemoteTest {

    private CleanUserEnum whatClean = CLEAN_NOTHING;

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(4000);
    }

    @After
    public void cleaningUp() throws UiException
    {
        cleanOptions(whatClean);
    }

    @Test
    public void testGetNotFoundMsg() throws IOException
    {
        Response<ErrorBean> response = Oauth2.getNotFoundMsg().execute();
        assertThat(response.isSuccessful(), is(false));
        assertThat(creator.get().getRetrofitHandler().getErrorBean(response).getMessage(), is(NOT_FOUND.getHttpMessage()));
    }

    @Test
    public void testDoAuthBasicHeader()
    {
        String encodedHeader = Oauth2.doAuthBasicHeader(CL_USER);
        assertThat(encodedHeader, equalTo("Basic dXNlcjo="));
    }

    @Test
    public void testGetPasswordUserToken_1() throws Exception
    {
        whatClean = CLEAN_JUAN;

        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        boolean isRegistered = userComuDaoRemote.regComuAndUserAndUserComu(COMU_REAL_JUAN).execute().body();
        assertThat(isRegistered, is(true));
        // Solicita token.
        SpringOauthToken token = Oauth2.getPasswordUserToken(UsuarioDataTestUtils.USER_JUAN.getUserName(), UsuarioDataTestUtils.USER_JUAN.getPassword());
        assertThat(token, notNullValue());
        assertThat(token.getValue(), notNullValue());
        assertThat(token.getRefreshToken().getValue(), notNullValue());
    }

    @Test
    public void testGetPasswordUserToken_2() throws Exception
    {
        whatClean = CLEAN_JUAN;

        //Inserta userComu, comunidad y usuariocomunidad.
        boolean isRegistered = userComuDaoRemote.regComuAndUserAndUserComu(COMU_REAL_JUAN).execute().body();
        assertThat(isRegistered, is(true));
        // Solicita token y actuliza tokenCache.
        updateSecurityData(COMU_REAL_JUAN.getUsuario().getUserName(), COMU_REAL_JUAN.getUsuario().getPassword());
        // Vuelve a solicitar token.
        SpringOauthToken token = Oauth2.getPasswordUserToken(UsuarioDataTestUtils.USER_JUAN.getUserName(), UsuarioDataTestUtils.USER_JUAN.getPassword());
        assertThat(token, notNullValue());
        assertThat(token.getValue(), notNullValue());
        assertThat(token.getRefreshToken().getValue(), notNullValue());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testGetRefreshUserToken_1() throws Exception
    {
        whatClean = CLEAN_PEPE;

        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        signUpAndUpdateTk(UserComuDataTestUtil.COMU_REAL_PEPE);
        SpringOauthToken tokenOld = TKhandler.getTokenCache().get();
        String accessTkOldValue = tokenOld.getValue();
        String refreshTkOldValue = tokenOld.getRefreshToken().getValue();

        SpringOauthToken tokenNew = Oauth2.getRefreshUserToken(TKhandler.getRefreshTokenValue());
        assertThat(tokenNew, notNullValue());
        // Return mew access and refresh tokens.
        assertThat(tokenNew.getRefreshToken().getValue(), not(is(refreshTkOldValue)));
        assertThat(tokenNew.getValue(), not(is(accessTkOldValue)));
    }

    @Test
    public void testGetRefreshUserToken_2() throws Exception
    {
        whatClean = CLEAN_DROID;

        userComuDaoRemote.regComuAndUserAndUserComu(COMU_REAL_DROID).execute().body();

        SpringOauthToken token0 = Oauth2.getPasswordUserToken(USER_DROID.getUserName(), USER_DROID.getPassword());
        assertThat(token0.getRefreshToken(), notNullValue());

        SpringOauthToken token1 = Oauth2.getPasswordUserToken(USER_DROID.getUserName(), USER_DROID.getPassword());
        assertThat(token1.getRefreshToken(), notNullValue());

        try {
            Oauth2.getRefreshUserToken(token1.getRefreshToken().getValue());
            fail();
        } catch (UiException e) {
            assertThat(e.getErrorBean().getMessage(), is(BAD_REQUEST.getHttpMessage()));
        }
    }
}