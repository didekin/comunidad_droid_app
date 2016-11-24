package com.didekinaar.comunidad;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekinaar.R;
import com.didekinaar.exception.UiAarException;
import com.didekinaar.testutil.AarActivityTestUtils;
import com.didekinaar.testutil.CleanUserEnum;
import com.didekinaar.testutil.UserMenuTestUtils;
import com.didekinaar.testutil.UsuarioTestUtils;

import junit.framework.AssertionFailedError;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static com.didekinaar.security.TokenHandler.TKhandler;
import static com.didekinaar.testutil.AarActivityTestUtils.checkUp;
import static com.didekinaar.testutil.AarActivityTestUtils.cleanOptions;
import static com.didekinaar.utils.UIutils.isRegisteredUser;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 15/05/15
 * Time: 09:53
 */
@RunWith(AndroidJUnit4.class)
public class ComuSearchAc_3_SlowTest {

    private ComuSearchAc activity;
    Context context;
    File refreshTkFile;
    CleanUserEnum whatClean;

    // Navigate-up activity layout.
    int activityLayoutId = R.id.comu_search_ac_linearlayout;

    @Rule
    public ActivityTestRule<ComuSearchAc> mActivityRule = new ActivityTestRule<>(ComuSearchAc.class, true, false);

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(4000);
    }

    @Before
    public void getFixture() throws Exception
    {
        Thread.sleep(4000);
        context = InstrumentationRegistry.getTargetContext();
        refreshTkFile = TKhandler.getRefreshTokenFile();
        whatClean = CleanUserEnum.CLEAN_NOTHING;
    }

    @After
    public void cleanData() throws UiAarException
    {
        cleanOptions(whatClean);

    }

    @Test
    public void testComunidadesByUsuario_withToken() throws InterruptedException, UiAarException, IOException
    {
        whatClean = CleanUserEnum.CLEAN_JUAN;

        AarActivityTestUtils.signUpAndUpdateTk(UsuarioTestUtils.COMU_REAL_JUAN);
        activity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(activity), is(true));
        UserMenuTestUtils.SEE_USERCOMU_BY_USER_AC.checkMenuItem_WTk(activity);

        checkUp(activityLayoutId);
    }

    @Test
    public void tesComunidadesByUsuario_noToken() throws InterruptedException
    {
        assertThat(refreshTkFile.exists(), is(false));
        activity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(activity), is(false));
        UserMenuTestUtils.SEE_USERCOMU_BY_USER_AC.checkMenuItem_NTk(activity);
    }

    @Test
    public void testLogin_withToken() throws InterruptedException, UiAarException, IOException
    {
        whatClean = CleanUserEnum.CLEAN_JUAN;
        AarActivityTestUtils.signUpAndUpdateTk(UsuarioTestUtils.COMU_REAL_JUAN);
        activity = mActivityRule.launchActivity(new Intent());

        onView(ViewMatchers.withId(R.id.login_ac_mn)).check(doesNotExist());
        openActionBarOverflowOrOptionsMenu(activity);
        onView(ViewMatchers.withId(R.id.login_ac_mn)).check(doesNotExist());
    }

    @Test
    public void testLogin_withoutToken() throws InterruptedException
    {
        activity = mActivityRule.launchActivity(new Intent());

        try {
            onView(ViewMatchers.withId(R.id.login_ac_mn)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            openActionBarOverflowOrOptionsMenu(activity);
            onView(ViewMatchers.withId(R.id.login_ac_mn)).check(matches(isDisplayed()));
        }

        UserMenuTestUtils.LOGIN_AC.checkMenuItem_NTk(activity);
        checkUp(activityLayoutId);
    }

    @Test
    public void testGetDatosUsuarioNoToken() throws InterruptedException
    {
        assertThat(refreshTkFile.exists(), is(false));
        activity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(activity), is(false));
        UserMenuTestUtils.USER_DATA_AC.checkMenuItem_NTk(activity);
    }

    @Test
    public void testGetDatosUsuarioWithToken() throws InterruptedException, UiAarException, IOException
    {
        whatClean = CleanUserEnum.CLEAN_JUAN;

        //With token.
        AarActivityTestUtils.signUpAndUpdateTk(UsuarioTestUtils.COMU_REAL_JUAN);
        activity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(activity), is(true));
        UserMenuTestUtils.USER_DATA_AC.checkMenuItem_WTk(activity);

        checkUp(activityLayoutId);
    }

    @Test
    public void testMenuNuevaComunidad_noToken() throws InterruptedException
    {
        assertThat(refreshTkFile.exists(), is(false));
        activity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(activity), is(false));
        UserMenuTestUtils.REG_COMU_USER_USERCOMU_AC.checkMenuItem_NTk(activity);

        checkUp(activityLayoutId);
    }

    @Test
    public void testMenuNuevaComunidad_withToken() throws InterruptedException, UiAarException, IOException
    {
        whatClean = CleanUserEnum.CLEAN_JUAN;

        AarActivityTestUtils.signUpAndUpdateTk(UsuarioTestUtils.COMU_REAL_JUAN);
        activity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(activity), is(true));
        UserMenuTestUtils.REG_COMU_USERCOMU_AC.checkMenuItem_WTk(activity);

        checkUp(activityLayoutId);
    }
}