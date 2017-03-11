package com.didekindroid.comunidad;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.testutil.UsuarioDataTestUtils;
import com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil;

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
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.usuario.testutil.UserItemMenuTestUtils.LOGIN_AC;
import static com.didekindroid.usuario.testutil.UserItemMenuTestUtils.USER_DATA_AC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.REG_COMU_USERCOMU_AC;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.REG_COMU_USER_USERCOMU_AC;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.SEE_USERCOMU_BY_USER_AC;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 15/05/15
 * Time: 09:53
 */
@RunWith(AndroidJUnit4.class)
public class ComuSearchAc_3_Test {

    private ComuSearchAc activity;
    Context context;
    File refreshTkFile;
    UsuarioDataTestUtils.CleanUserEnum whatClean;

    // Navigate-up manager layout.
    int activityLayoutId = R.id.comu_search_ac_linearlayout;

    @Rule
    public ActivityTestRule<ComuSearchAc> mActivityRule = new ActivityTestRule<>(ComuSearchAc.class, true, false);

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(3000);
    }

    @Before
    public void getFixture() throws Exception
    {
        context = InstrumentationRegistry.getTargetContext();
        refreshTkFile = TKhandler.getRefreshTokenFile();
        whatClean = UsuarioDataTestUtils.CleanUserEnum.CLEAN_NOTHING;
    }

    @After
    public void cleanData() throws UiException
    {
        cleanOptions(whatClean);

    }

    @Test
    public void testComunidadesByUsuario() throws InterruptedException, UiException, IOException
    {
        whatClean = UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;

        UserComuDataTestUtil.signUpAndUpdateTk(UserComuDataTestUtil.COMU_REAL_JUAN);
        activity = mActivityRule.launchActivity(new Intent());
        assertThat(TKhandler.isRegisteredUser(), is(true));
        SEE_USERCOMU_BY_USER_AC.checkMenuItem_WTk(activity);

        checkUp(activityLayoutId);
    }

    @Test
    public void testLogin_withToken() throws InterruptedException, UiException, IOException
    {
        whatClean = UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
        UserComuDataTestUtil.signUpAndUpdateTk(UserComuDataTestUtil.COMU_REAL_JUAN);
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

        LOGIN_AC.checkMenuItem_NTk(activity);
        checkUp(activityLayoutId);
    }

    @Test
    public void testGetDatosUsuarioNoToken() throws InterruptedException
    {
        activity = mActivityRule.launchActivity(new Intent());
        assertThat(refreshTkFile.exists(), is(false));
        assertThat(TKhandler.isRegisteredUser(), is(false));
        USER_DATA_AC.checkMenuItem_NTk(activity);
    }

    @Test
    public void testGetDatosUsuarioWithToken() throws InterruptedException, UiException, IOException
    {
        whatClean = UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;

        //With token.
        UserComuDataTestUtil.signUpAndUpdateTk(UserComuDataTestUtil.COMU_REAL_JUAN);
        activity = mActivityRule.launchActivity(new Intent());
        assertThat(TKhandler.isRegisteredUser(), is(true));
        USER_DATA_AC.checkMenuItem_WTk(activity);

        checkUp(activityLayoutId);
    }

    @Test
    public void testMenuNuevaComunidad_noToken() throws InterruptedException
    {
        assertThat(refreshTkFile.exists(), is(false));
        activity = mActivityRule.launchActivity(new Intent());
        assertThat(TKhandler.isRegisteredUser(), is(false));
        REG_COMU_USER_USERCOMU_AC.checkMenuItem_NTk(activity);

        checkUp(activityLayoutId);
    }

    @Test
    public void testMenuNuevaComunidad_withToken() throws InterruptedException, UiException, IOException
    {
        whatClean = UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;

        UserComuDataTestUtil.signUpAndUpdateTk(UserComuDataTestUtil.COMU_REAL_JUAN);
        activity = mActivityRule.launchActivity(new Intent());
        assertThat(TKhandler.isRegisteredUser(), is(true));
        REG_COMU_USERCOMU_AC.checkMenuItem_WTk(activity);

        checkUp(activityLayoutId);
    }
}