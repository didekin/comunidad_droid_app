package com.didekindroid.lib_one.security;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.usuario.firebase.CtrlerFirebaseToken;
import com.didekindroid.usuario.firebase.CtrlerFirebaseTokenIf;
import com.didekinlib.http.auth.SpringOauthToken;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.lib_one.security.SecurityTestUtils.doSpringOauthToken;
import static com.didekindroid.lib_one.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.lib_one.security.TokenIdentityCacher.cleanTokenAndUnregisterFunc;
import static com.didekindroid.lib_one.security.TokenIdentityCacher.cleanTkCacheConsumer;
import static com.didekindroid.lib_one.security.TokenIdentityCacher.initTokenAction;
import static com.didekindroid.lib_one.security.TokenIdentityCacher.initTokenAndRegisterFunc;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_TK_HANDLER;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.lib_one.util.IoHelper.readStringFromFile;
import static com.didekinlib.http.auth.AuthClient.doBearerAccessTkHeader;
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
@RunWith(AndroidJUnit4.class)
public class TokenIdentityCacherTest_1 {

    @Rule
    public ActivityTestRule<? extends Activity> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    Activity activity;

    @Before
    public void getFixture()
    {
        activity = activityRule.getActivity();
    }

    @After
    public void cleanFileToken() throws UiException
    {
        cleanOptions(CLEAN_TK_HANDLER);
    }

    // ===================================== TESTS ==========================================

    @Test
    public void testInitTokenAndBackFile() throws Exception
    {
        // Precondition: no file with refreshToken. We receive a fully initialized token instance.
        assertThat(TKhandler.getRefreshTokenFile(), notNullValue());
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(false));
        assertThat(TKhandler.getRefreshTokenValue(), nullValue());
        assertThat(TKhandler.getTokenCache().get(), nullValue());

        SpringOauthToken springOauthToken = doSpringOauthToken();
        TKhandler.initIdentityCache(springOauthToken);
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(true));
        assertThat(TKhandler.getRefreshTokenValue(), is(springOauthToken.getRefreshToken().getValue()));
        assertThat(TKhandler.getTokenCache().get(), is(springOauthToken));
    }

    @Test
    public void testCleanCacheAndBckFile() throws UiException
    {
        // Preconditions: there exist token data and file.
        SpringOauthToken springOauthToken = doSpringOauthToken();
        TKhandler.initIdentityCache(springOauthToken);

        TKhandler.cleanIdentityCache();
        // Assertions.
        assertThat(TKhandler.getRefreshTokenValue(), nullValue());
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(false));
        assertThat(TKhandler.getTokenCache().get(), nullValue());
    }

    @Test
    public void testDoBearerAccessTkHeader()
    {
        // Precondition: no file with refreshToken.
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(false));

        SpringOauthToken springOauthToken = doSpringOauthToken();
        String bearerTk = doBearerAccessTkHeader(springOauthToken);
        assertThat(bearerTk, equalTo("Bearer " + springOauthToken.getValue()));
    }

    @Test
    public void testUpdateIsRegistered_1() throws Exception
    {
        TKhandler.updateIsRegistered(false);
        assertThat(TKhandler.isRegisteredUser(), is(false));
        TKhandler.updateIsRegistered(true);
        assertThat(TKhandler.isRegisteredUser(), is(true));
    }

    @Test
    public void testUpdateIsRegistered_2() throws Exception
    {
        CtrlerFirebaseTokenIf controller = new CtrlerFirebaseToken();
        TKhandler.updateIsRegistered(false);
        // Actualiza a falso.
        assertThat(controller.isGcmTokenSentServer(), is(false));
        TKhandler.updateIsRegistered(true);
        // No actualizamos a verdadero.
        assertThat(controller.isGcmTokenSentServer(), is(false));
    }

    @Test
    public void testUpdateIsRegistered() throws Exception
    {
        CtrlerFirebaseTokenIf controller = new CtrlerFirebaseToken();
        TKhandler.updateIsRegistered(true);
        controller.updateIsGcmTokenSentServer(true);

        TKhandler.updateIsRegistered(false);
        // Check the change in the flag.
        assertThat(controller.isGcmTokenSentServer(), is(false));

        TKhandler.updateIsRegistered(true);
        // No change in flag.
        assertThat(controller.isGcmTokenSentServer(), is(false));
    }

    // ===================================== ACTIONS TESTS =========================================

    @Test
    public void testInitTokenAction() throws Exception
    {
        SpringOauthToken token = doSpringOauthToken();
        initTokenAction.accept(token);
        checkSiCacheAndFile(token);
    }

    @Test
    public void testCleanTokenCacheAction() throws Exception
    {
        // No user registered. We test for the non-nullity of refreshTokenFile.
        cleanTkCacheConsumer.accept(true);
        checkNoCacheAndFile();
    }

// ===================================== FUNCTIONS TESTS =========================================

    @Test
    public void testInitTokenAndRegisterFunc_1() throws Exception
    {
        SpringOauthToken token = doSpringOauthToken();
        assertThat(initTokenAndRegisterFunc.apply(true, token), is(true));
        checkSiCacheAndFile(token);
        assertThat(TKhandler.isRegisteredUser(), is(true));
    }

    @Test
    public void testInitTokenAndRegisterFunc_2() throws Exception
    {
        SpringOauthToken token = doSpringOauthToken();
        initTokenAndRegisterFunc.apply(false, token);
        checkNoCacheAndFile();
        assertThat(TKhandler.isRegisteredUser(), is(false));
    }

    @Test
    public void testCleanTokenAndUnregisterFunc_1() throws Exception
    {
        initTokenHelper();
        cleanTokenAndUnregisterFunc.apply(true);
        checkNoCacheAndFile();
        assertThat(TKhandler.isRegisteredUser(), is(false));
    }

    @Test
    public void testCleanTokenAndUnregisterFunc_2() throws Exception
    {
        SpringOauthToken token = initTokenHelper();
        cleanTokenAndUnregisterFunc.apply(false);
        checkSiCacheAndFile(token);
        assertThat(TKhandler.isRegisteredUser(), is(true));
    }

    // ............................... HELPERS ..............................

    private SpringOauthToken initTokenHelper() throws Exception
    {
        SpringOauthToken token = doSpringOauthToken();
        initTokenAndRegisterFunc.apply(true, token);
        assertThat(TKhandler.getTokenCache().get(), is(token));
        assertThat(TKhandler.isRegisteredUser(), is(true));
        return token;
    }

    private void checkNoCacheAndFile()
    {
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(false));
        assertThat(TKhandler.getTokenCache().get(), is(nullValue()));
    }

    private void checkSiCacheAndFile(SpringOauthToken token)
    {
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(true));
        assertThat(readStringFromFile(TKhandler.getRefreshTokenFile()), is(token.getRefreshToken().getValue()));
        assertThat(TKhandler.getTokenCache().get().getValue(), is(token.getValue()));
    }
}