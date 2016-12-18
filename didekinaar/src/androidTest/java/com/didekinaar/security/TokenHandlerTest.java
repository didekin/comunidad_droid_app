package com.didekinaar.security;

import android.content.Context;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.oauth2.SpringOauthToken;
import com.didekin.oauth2.SpringOauthToken.OauthToken;
import com.didekinaar.exception.UiException;
import com.didekinaar.testutil.AarActivityTestUtils.CleanUserEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Timestamp;
import java.util.Date;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekin.oauth2.OauthTokenHelper.HELPER;
import static com.didekinaar.security.TokenHandler.TKhandler;
import static com.didekinaar.testutil.AarActivityTestUtils.CleanUserEnum.CLEAN_NOTHING;
import static com.didekinaar.testutil.AarActivityTestUtils.cleanOptions;
import static com.didekinaar.testutil.AarActivityTestUtils.cleanWithTkhandler;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro
 * Date: 29/06/15
 * Time: 08:11
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class TokenHandlerTest {

    protected Context context;
    protected CleanUserEnum whatClean = CLEAN_NOTHING;

    @Before
    public void getFixture()
    {
        cleanWithTkhandler();
        context = getTargetContext();
    }

    @After
    public void cleanFileToken() throws UiException
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
    public void testCleanCacheAndBckFile() throws UiException
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