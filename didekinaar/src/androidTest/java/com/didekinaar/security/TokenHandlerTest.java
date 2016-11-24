package com.didekinaar.security;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.oauth2.SpringOauthToken;
import com.didekin.oauth2.SpringOauthToken.OauthToken;
import com.didekinaar.exception.UiAarException;
import com.didekinaar.testutil.AarActivityTestUtils;
import com.didekinaar.testutil.CleanUserEnum;
import com.didekinaar.testutil.UsuarioTestUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import static com.didekin.oauth2.OauthTokenHelper.HELPER;
import static com.didekinaar.security.TokenHandler.TKhandler;
import static com.didekinaar.security.TokenHandler.refresh_token_filename;
import static com.didekinaar.testutil.AarActivityTestUtils.cleanOptions;
import static com.didekinaar.testutil.AarActivityTestUtils.cleanWithTkhandler;
import static com.didekinaar.utils.IoHelper.writeFileFromString;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.junit.Assert.assertThat;

/**
 * User: pedro
 * Date: 29/06/15
 * Time: 08:11
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class TokenHandlerTest {

    private Context context;
    CleanUserEnum whatClean = CleanUserEnum.CLEAN_NOTHING;

    @Before
    public void getFixture()
    {
        cleanWithTkhandler();
        context = InstrumentationRegistry.getTargetContext();
    }

    @After
    public void cleanFileToken() throws UiAarException
    {
        cleanOptions(whatClean);
    }

    @Test
    public void testInitTokenAndBackFile() throws Exception
    {
        // Precondition: no file with refreshToken. We receive a fully initialized token instance.
        assertThat(TKhandler.getRefreshTokenFile(), notNullValue());
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(false));
        assertThat(TKhandler.getRefreshTokenValue(), nullValue());
        assertThat(TKhandler.getAccessTokenInCache(), nullValue());

        SpringOauthToken springOauthToken = doSpringOauthToken();
        TKhandler.initTokenAndBackupFile(springOauthToken);
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(true));
        assertThat(TKhandler.getRefreshTokenValue(), is(springOauthToken.getRefreshToken().getValue()));
        assertThat(TKhandler.getAccessTokenInCache(), is(springOauthToken));
    }

    @Test
    public void testCleanCacheAndBckFile() throws UiAarException
    {
        // Preconditions: there exist token data and file.
        SpringOauthToken springOauthToken = doSpringOauthToken();
        TKhandler.initTokenAndBackupFile(springOauthToken);

        TKhandler.cleanTokenAndBackFile();
        // Assertions.
        assertThat(TKhandler.getRefreshTokenValue(), nullValue());
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(false));
        assertThat(TKhandler.getAccessTokenInCache(), nullValue());
    }

    @Test
    public void testGetAccessTokenInCache_1() throws IOException, UiAarException
    {
        whatClean = CleanUserEnum.CLEAN_JUAN;

        // Precondition: a user in DB and there exists file with refreshToken.
        AarActivityTestUtils.signUpAndUpdateTk(UsuarioTestUtils.COMU_REAL_JUAN);
        SpringOauthToken springOauthTokenIn = TKhandler.getAccessTokenInCache();
        String refreshTkOriginal = springOauthTokenIn.getRefreshToken().getValue();
        // Borramos datos.
        TKhandler.cleanTokenAndBackFile();
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
    public void testGetAccessTokenInCache_2() throws IOException, UiAarException
    {
        whatClean = CleanUserEnum.CLEAN_JUAN;

        // Precondition: file exists, tokenInCache initialized with a fully initialized token.
        AarActivityTestUtils.signUpAndUpdateTk(UsuarioTestUtils.COMU_REAL_JUAN);
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

    @Test
    public void testDoBearerAccessTkHeader()
    {
        // Precondition: no file with refreshToken.
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(false));

        SpringOauthToken springOauthToken = doSpringOauthToken();
        String bearerTk = HELPER.doBearerAccessTkHeader(springOauthToken);
        assertThat(bearerTk, equalTo("Bearer " + springOauthToken.getValue()));
    }

    @Test
    public void testDoBearerAccessTkHeaderNull() throws Exception
    {
        // Precondition for initialization of the enum: no file with refreshToken.
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(false));
        assertThat(TKhandler.getRefreshTokenValue(), nullValue());

        String bearerHeader = TKhandler.doBearerAccessTkHeader();
        assertThat(bearerHeader, nullValue());
    }

//    .................... UTILITIES .......................

    private SpringOauthToken doSpringOauthToken()
    {
        return new SpringOauthToken(
                "50d3cdaa-0d2e-4cfd-b259-82b3a0b1edef",
                new Timestamp(new Date().getTime() + 7200000),
                "bearer",
                new OauthToken("50d3cdaa-0d2e-4cfd-b259-82b3a0b1edef", new Timestamp(new Date().getTime() + 7200000)),
                new String[]{"readwrite"}
        );
    }
}