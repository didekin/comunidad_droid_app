package com.didekindroid.security;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.exception.UiException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.security.TokenIdentityCacher.refresh_token_filename;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_TK_HANDLER;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.util.IoHelper.writeFileFromString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro
 * Date: 29/06/15
 * Time: 08:11
 */
@RunWith(AndroidJUnit4.class)
public class TokenIdentityCacherTest_2 {

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<ActivityMock>(ActivityMock.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                fail();
            }
        }
    };

    Activity activity;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
    }

    @After
    public void tearUp() throws UiException
    {
        cleanOptions(CLEAN_TK_HANDLER);
    }

    // ===================================== TESTS ==========================================

    @Test
    public void test_InitClass_1()
    {
        // Non-empty file.
        File refreshTkFile = new File(getTargetContext().getFilesDir(), refresh_token_filename);
        writeFileFromString("test_refreshToken", refreshTkFile);
        assertThat(TKhandler.getTokenCache().get(), notNullValue());
        assertThat(TKhandler.getTokenCache().get().getRefreshToken().getValue(), is("test_refreshToken"));
    }

    @Test
    public void test_InitClass_2()
    {
        // Empty file.
        File refreshTkFile = new File(getTargetContext().getFilesDir(), refresh_token_filename);
        writeFileFromString("", refreshTkFile);
        assertThat(TKhandler.getTokenCache().get(), nullValue());
    }

    @Test
    public void test_InitClass_3()
    {
        // No file.
        assertThat(TKhandler.getTokenCache().get(), nullValue());
    }
}