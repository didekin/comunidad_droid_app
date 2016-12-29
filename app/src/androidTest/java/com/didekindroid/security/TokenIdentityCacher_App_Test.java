package com.didekindroid.security;

import android.content.Context;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.oauth2.SpringOauthToken;
import com.didekin.oauth2.SpringOauthToken.OauthToken;
import com.didekinaar.exception.UiException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekinaar.security.TokenIdentityCacher.TKhandler;
import static com.didekinaar.security.TokenIdentityCacher.refresh_token_filename;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.cleanWithTkhandler;
import static com.didekinaar.utils.IoHelper.writeFileFromString;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.signUpAndUpdateTk;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.junit.Assert.assertThat;

/**
 * User: pedro
 * Date: 29/06/15
 * Time: 08:11
 */
@RunWith(AndroidJUnit4.class)
public class TokenIdentityCacher_App_Test {

    private Context context;

    @Before
    public void getFixture()
    {
        cleanWithTkhandler();
        context = getTargetContext();
    }

    @After
    public void cleanFileToken() throws UiException
    {
        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void testGetAccessTokenInCache_1() throws IOException, UiException
    {
        // Precondition: a user in DB and there exists file with refreshToken.
        signUpAndUpdateTk(COMU_REAL_JUAN);
        SpringOauthToken springOauthTokenIn = TKhandler.getAccessTokenInCache();
        String refreshTkOriginal = springOauthTokenIn.getRefreshToken().getValue();
        // Borramos datos.
        TKhandler.cleanIdentityCache();
        // We make out preconditions: file exists, tokenInCache initialized ONLY with refreshTokenValue.
        File refreshTkFile = new File(context.getFilesDir(), refresh_token_filename);
        writeFileFromString(refreshTkOriginal, refreshTkFile);
        TKhandler.tokenInCache.set(new SpringOauthToken(refreshTkOriginal));
        assertThat(refreshTkFile.exists(), is(true));

        // Call to the method.
        SpringOauthToken fullTkNew =  TKhandler.getAccessTokenInCache();
        assertThat(fullTkNew, notNullValue());
        assertThat(fullTkNew.getValue(), not(isEmptyOrNullString()));
        OauthToken refreshTkNew =  fullTkNew.getRefreshToken();
        assertThat(refreshTkNew.getValue(), allOf(
                not(refreshTkOriginal), // Return a different refresh token.
                is(TKhandler.getRefreshTokenValue()),
                not(isEmptyOrNullString())
        ));

        // Volvemos a llamar y comprobamos que ahora devuelve los mismos valores.
        SpringOauthToken oauthTokenInCache = TKhandler.getAccessTokenInCache();
        assertThat(oauthTokenInCache, allOf(
                notNullValue(),
                is(fullTkNew)
        ));
        assertThat(oauthTokenInCache.getValue(), allOf(
                not(isEmptyOrNullString()),
                is(fullTkNew.getValue())
        ));
        OauthToken refreshTkInCache =  oauthTokenInCache.getRefreshToken();
        assertThat(refreshTkInCache.getValue(), allOf(
                is(refreshTkNew.getValue()),
                is(TKhandler.getRefreshTokenValue()),
                not(isEmptyOrNullString())
        ));
    }

    @Test
    public void testGetAccessTokenInCache_2() throws IOException, UiException
    {
        // Precondition: file exists, tokenInCache initialized with a fully initialized token.
        signUpAndUpdateTk(COMU_REAL_JUAN);
        SpringOauthToken springOauthTokenIn = TKhandler.tokenInCache.get();
        // Call to method.
        SpringOauthToken springOauthTokenOut = TKhandler.getAccessTokenInCache();
        // Assertions.
        assertThat(TKhandler.getAccessTokenInCache(), allOf(
                notNullValue(),
                is(springOauthTokenIn)
        ));
        assertThat(TKhandler.getRefreshTokenValue(), not(isEmptyOrNullString()));
        assertThat(TKhandler.getAccessTokenInCache().getValue(), not(isEmptyOrNullString()));
    }
}