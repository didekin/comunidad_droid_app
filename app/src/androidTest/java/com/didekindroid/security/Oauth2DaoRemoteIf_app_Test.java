package com.didekindroid.security;

import android.support.test.runner.AndroidJUnit4;

import com.didekin.oauth2.SpringOauthToken;
import com.didekinaar.exception.UiException;
import com.didekinaar.usuario.testutil.UsuarioDataTestUtils;
import com.didekinaar.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum;
import com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekin.common.exception.DidekinExceptionMsg.BAD_REQUEST;
import static com.didekin.oauth2.OauthTokenHelper.HELPER;
import static com.didekinaar.security.Oauth2DaoRemote.Oauth2;
import static com.didekinaar.security.TokenIdentityCacher.TKhandler;
import static com.didekinaar.testutil.AarTestUtil.updateSecurityData;
import static com.didekinaar.usuario.UsuarioDaoRemote.usuarioDaoRemote;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.UserComuService.AppUserComuServ;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.signUpAndUpdateTk;
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
public class Oauth2DaoRemoteIf_app_Test {

    private CleanUserEnum whatClean;

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
    public void testGetPasswordUserToken_1() throws Exception
    {
        whatClean = CLEAN_JUAN;

        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        boolean isRegistered = AppUserComuServ.regComuAndUserAndUserComu(COMU_REAL_JUAN).execute().body();
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
        boolean isRegistered = AppUserComuServ.regComuAndUserAndUserComu(COMU_REAL_JUAN).execute().body();
        assertThat(isRegistered, is(true));
        // Solicita token y actuliza tokenCache.
        updateSecurityData(COMU_REAL_JUAN.getUsuario().getUserName(), COMU_REAL_JUAN.getUsuario().getPassword());
        // Vuelve a solicitar token.
        SpringOauthToken token = Oauth2.getPasswordUserToken(UsuarioDataTestUtils.USER_JUAN.getUserName(), UsuarioDataTestUtils.USER_JUAN.getPassword());
        assertThat(token, notNullValue());
        assertThat(token.getValue(), notNullValue());
        assertThat(token.getRefreshToken().getValue(), notNullValue());
    }

    @Test
    public void testGetPasswordUserToken_3() throws Exception
    {
        //Inserta userComu, comunidad, usuariocomunidad.
        boolean isRegistered = AppUserComuServ.regComuAndUserAndUserComu(UserComuTestUtil.COMU_REAL_DROID).execute().body();
        assertThat(isRegistered, is(true));
        updateSecurityData(UsuarioDataTestUtils.USER_DROID.getUserName(), UsuarioDataTestUtils.USER_DROID.getPassword());
        // Envía correo.
        boolean isPasswordSend = usuarioDaoRemote.passwordSend(UsuarioDataTestUtils.USER_DROID.getUserName()).execute().body();
        assertThat(isPasswordSend,is(true));

        // Old pair userName/password is invalid: passwordSend implies new password in BD.
        try {
            Oauth2.getPasswordUserToken(UsuarioDataTestUtils.USER_DROID.getUserName(), UsuarioDataTestUtils.USER_DROID.getPassword());
            fail();
        } catch (UiException e) {
            assertThat(e.getErrorBean().getMessage(), is(BAD_REQUEST.getHttpMessage()));
        }

        // Es necesario conseguir un nuevo token.
        SpringOauthToken token = Oauth2.getRefreshUserToken(TKhandler.getRefreshTokenValue());
        assertThat(token, notNullValue());
        assertThat(token.getValue(), notNullValue());
        assertThat(token.getRefreshToken().getValue(), notNullValue());

        usuarioDaoRemote.deleteUser(HELPER.doBearerAccessTkHeader(token)).execute();
        UsuarioDataTestUtils.cleanWithTkhandler();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testGetRefreshUserToken() throws Exception
    {
        whatClean = CLEAN_PEPE;

        //Inserta userComu, comunidad, usuariocomunidad y actuliza tokenCache.
        signUpAndUpdateTk(UserComuTestUtil.COMU_REAL_PEPE);
        SpringOauthToken tokenOld = TKhandler.getAccessTokenInCache();
        String accessTkOldValue = tokenOld.getValue();
        String refreshTkOldValue = tokenOld.getRefreshToken().getValue();

        SpringOauthToken tokenNew = Oauth2.getRefreshUserToken(TKhandler.getRefreshTokenValue());
        assertThat(tokenNew, notNullValue());
        // Return mew access and refresh tokens.
        assertThat(tokenNew.getRefreshToken().getValue(), not(is(refreshTkOldValue)));
        assertThat(tokenNew.getValue(), not(is(accessTkOldValue)));
    }
}