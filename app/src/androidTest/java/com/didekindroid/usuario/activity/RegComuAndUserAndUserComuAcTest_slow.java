package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.common.UiException;
import com.didekindroid.usuario.activity.utils.CleanUserEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.common.TokenHandler.TKhandler;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.activity.utils.CleanUserEnum.CLEAN_NOTHING;
import static com.didekindroid.usuario.activity.utils.UserMenuTestUtils.LOGIN_AC;
import static com.didekindroid.usuario.activity.utils.UserMenuTestUtils.REQUIRES_USER_NO_TOKEN;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.cleanOptions;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.signUpAndUpdateTk;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_REAL_JUAN;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro
 * Date: 07/07/15
 * Time: 10:26
 */
@SuppressWarnings({"unchecked", "ConstantConditions"})
@RunWith(AndroidJUnit4.class)
public class RegComuAndUserAndUserComuAcTest_slow {

    @Rule
    public ActivityTestRule<RegComuAndUserAndUserComuAc> mActivityRule = new ActivityTestRule<>(RegComuAndUserAndUserComuAc.class, true, false);

    RegComuAndUserAndUserComuAc mActivity;
    Resources resources;
    CleanUserEnum whatToClean;

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(5000);
    }

    @Before
    public void setUp() throws Exception
    {
        whatToClean = CLEAN_NOTHING;
        resources = InstrumentationRegistry.getTargetContext().getResources();
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(whatToClean);
        Thread.sleep(1000);
    }

    @Test
    public void testLoginMn_1() throws InterruptedException, UiException
    {
        mActivity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(mActivity),is(false));
        assertThat(TKhandler.getAccessTokenInCache(),nullValue());

        LOGIN_AC.checkMenuItem_NTk(mActivity);
    }

    @Test
    public void testLoginMn_2() throws InterruptedException, UiException
    {
        whatToClean = CleanUserEnum.CLEAN_JUAN;
        //With token.
        signUpAndUpdateTk(COMU_REAL_JUAN);

        mActivity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(mActivity), is(true));
        assertThat(TKhandler.getAccessTokenInCache(),not(nullValue()));

        try {
            LOGIN_AC.checkMenuItem_WTk(mActivity);
            fail();
        } catch (UnsupportedOperationException e) {
            assertThat(e.getMessage(),is(LOGIN_AC.name() + REQUIRES_USER_NO_TOKEN));
        }
    }
}