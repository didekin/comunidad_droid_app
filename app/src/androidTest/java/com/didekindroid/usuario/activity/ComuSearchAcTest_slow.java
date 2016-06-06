package com.didekindroid.usuario.activity;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.usuario.testutils.CleanUserEnum;

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
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.common.activity.TokenHandler.TKhandler;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.testutils.UserMenuTestUtils.LOGIN_AC;
import static com.didekindroid.usuario.testutils.UserMenuTestUtils.REG_COMU_USER_USERCOMU_AC;
import static com.didekindroid.usuario.testutils.UserMenuTestUtils.SEE_USERCOMU_BY_USER_AC;
import static com.didekindroid.usuario.testutils.UserMenuTestUtils.USER_DATA_AC;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_REAL_JUAN;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 15/05/15
 * Time: 09:53
 */
@RunWith(AndroidJUnit4.class)
public class ComuSearchAcTest_slow {

    private ComuSearchAc activity;
    Context context;
    File refreshTkFile;
    CleanUserEnum whatClean;

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
        Thread.sleep(3000);
        context = InstrumentationRegistry.getTargetContext();
        refreshTkFile = TKhandler.getRefreshTokenFile();
        whatClean = CleanUserEnum.CLEAN_NOTHING;
    }

    @Test
    public void testGetDatosUsuarioNoToken() throws InterruptedException
    {
        assertThat(refreshTkFile.exists(), is(false));
        activity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(activity), is(false));
        USER_DATA_AC.checkMenuItem_NTk(activity);
    }

    @Test
    public void testGetDatosUsuarioWithToken() throws InterruptedException, UiException, IOException
    {
        whatClean = CleanUserEnum.CLEAN_JUAN;

        //With token.
        signUpAndUpdateTk(COMU_REAL_JUAN);
        activity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(activity), is(true));
        USER_DATA_AC.checkMenuItem_WTk(activity);
    }

    @Test
    public void testMenuNuevaComunidad_noToken() throws InterruptedException
    {
        assertThat(refreshTkFile.exists(), is(false));
        activity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(activity), is(false));
        REG_COMU_USER_USERCOMU_AC.checkMenuItem_NTk(activity);
    }

    @Test
    public void testMenuNuevaComunidad_withToken() throws InterruptedException, UiException, IOException
    {
        whatClean = CleanUserEnum.CLEAN_JUAN;

        signUpAndUpdateTk(COMU_REAL_JUAN);
        activity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(activity), is(true));
        REG_COMU_USER_USERCOMU_AC.checkMenuItem_WTk(activity);
    }

    @Test
    public void testComunidadesByUsuario_withToken() throws InterruptedException, UiException, IOException
    {
        whatClean = CleanUserEnum.CLEAN_JUAN;

        signUpAndUpdateTk(COMU_REAL_JUAN);
        activity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(activity), is(true));
        SEE_USERCOMU_BY_USER_AC.checkMenuItem_WTk(activity);
    }

    @Test
    public void tesComunidadesByUsuario_noToken() throws InterruptedException
    {
        assertThat(refreshTkFile.exists(), is(false));
        activity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(activity), is(false));
        SEE_USERCOMU_BY_USER_AC.checkMenuItem_NTk(activity);
    }

    @Test
    public void testLogin_withToken() throws InterruptedException, UiException, IOException
    {

        whatClean = CleanUserEnum.CLEAN_JUAN;
        signUpAndUpdateTk(COMU_REAL_JUAN);
        activity = mActivityRule.launchActivity(new Intent());

        onView(withId(R.id.login_ac_mn)).check(doesNotExist());
        openActionBarOverflowOrOptionsMenu(activity);
        onView(withId(R.id.login_ac_mn)).check(doesNotExist());
    }

    @Test
    public void testLogin_withoutToken() throws InterruptedException
    {
        activity = mActivityRule.launchActivity(new Intent());

        try {
            onView(withId(R.id.login_ac_mn)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            openActionBarOverflowOrOptionsMenu(activity);
            onView(withId(R.id.login_ac_mn)).check(matches(isDisplayed()));
        }

        LOGIN_AC.checkMenuItem_NTk(activity);
    }


    @After
    public void cleanData() throws UiException
    {
        cleanOptions(whatClean);

    }

//    ................ UTILIDADES .....................

}