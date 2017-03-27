package com.didekindroid.security;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.exception.UiException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableCompletableObserver;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.security.TokenIdentityCacher.refresh_token_filename;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanWithTkhandler;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekindroid.util.IoHelper.writeFileFromString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro
 * Date: 29/06/15
 * Time: 08:11
 */
@RunWith(AndroidJUnit4.class)
public class TokenIdentityCacherTest_2 {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Before
    public void getFixture()
    {
        cleanWithTkhandler();
    }

    @After
    public void cleanFileToken() throws UiException
    {
        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void testGetAccessTokenInCache_1() throws IOException, UiException
    {
        // Precondition: a fully initialized cache.

        signUpAndUpdateTk(COMU_REAL_JUAN);
        assertThat(TKhandler.getTokenCache().get() != null, is(true));

        TKhandler.refreshAccessToken(new OauthReactorForTest());
        // No hay llamada al método del reactor.
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(BEFORE_METHOD_EXEC));
    }

    @Test
    public void testGetAccessTokenInCache_2() throws IOException, UiException
    {
        // Precondition: a user in DB and there exists file with refreshToken, but cache is not initialized.
        signUpAndUpdateTk(COMU_REAL_JUAN);
        String refreshTkOriginal = TKhandler.getRefreshTokenValue();
        // Borramos cache.
        TKhandler.cleanIdentityCache();
        // Escribimos fichero.
        File refreshTkFile = new File(getTargetContext().getFilesDir(), refresh_token_filename);
        writeFileFromString(refreshTkOriginal, refreshTkFile);

        // Initial state.
        assertThat(TKhandler.isRegisteredUser(), is(true));
        assertThat(TKhandler.getTokenCache().get(), nullValue());
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(true));

        TKhandler.refreshAccessToken(new OauthReactorForTest());
        // Hay llamada al método del reactor.
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }

    static class OauthReactorForTest extends OauthTokenReactor {
        @Override
        public DisposableCompletableObserver updateTkCacheFromRefreshTk(String refreshToken)
        {
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
            return null;
        }
    }
}