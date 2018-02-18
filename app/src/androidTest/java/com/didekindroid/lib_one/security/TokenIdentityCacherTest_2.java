package com.didekindroid.lib_one.security;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.ActivityMock;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.security.TokenIdentityCacher.refresh_token_filename;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanWithTkhandler;
import static com.didekindroid.lib_one.util.IoHelper.writeFileFromString;
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
public class TokenIdentityCacherTest_2 {

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class);

    Activity activity;
    TokenIdentityCacher tkCacher;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        tkCacher = (TokenIdentityCacher) secInitializer.get().getTkCacher();
    }

    @After
    public void cleanUp(){
        cleanWithTkhandler();
    }

    // ===================================== TESTS ==========================================

    @Test
    public void test_InitClass_1()
    {
        // Non-empty file.
        File refreshTkFile = new File(getTargetContext().getFilesDir(), refresh_token_filename);
        writeFileFromString("test_refreshToken", refreshTkFile);
        assertThat(tkCacher.getTokenCache().get(), notNullValue());
        assertThat(tkCacher.getTokenCache().get().getRefreshToken().getValue(), is("test_refreshToken"));
    }

    @Test
    public void test_InitClass_2()
    {
        // Empty file.
        File refreshTkFile = new File(getTargetContext().getFilesDir(), refresh_token_filename);
        writeFileFromString("", refreshTkFile);
        assertThat(tkCacher.getTokenCache().get(), nullValue());
    }

    @Test
    public void test_InitClass_3()
    {
        // No file.
        assertThat(tkCacher.getTokenCache().get(), nullValue());
    }
}