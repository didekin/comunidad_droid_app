package com.didekindroid.usuario.activity;

import android.content.res.Resources;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.oauth2.SpringOauthToken;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.usuario.testutils.CleanUserEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.common.activity.TokenHandler.TKhandler;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkUp;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_SEE_OPEN_BY_COMU_AC;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_NOTHING;
import static com.didekindroid.usuario.testutils.UserMenuTestUtils.COMU_SEARCH_AC;
import static com.didekindroid.usuario.testutils.UserMenuTestUtils.DELETE_ME_AC;
import static com.didekindroid.usuario.testutils.UserMenuTestUtils.PASSWORD_CHANGE_AC;
import static com.didekindroid.usuario.testutils.UserMenuTestUtils.SEE_USERCOMU_BY_USER_AC;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_REAL_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.USER_JUAN;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro
 * Date: 16/07/15
 * Time: 14:25
 */
@RunWith(AndroidJUnit4.class)
public class UserDataAcTest {

    UserDataAc mActivity;
    Resources resources;
    CleanUserEnum whatToClean = CLEAN_JUAN;
    int activityLayoutId = R.id.user_data_ac_layout;

    @Rule
    public ActivityTestRule<UserDataAc> mActivityRule = new ActivityTestRule<UserDataAc>(UserDataAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            // Precondition: the user is registered.
            try {
                signUpAndUpdateTk(COMU_REAL_JUAN);
            } catch (UiException | IOException e) {
                e.printStackTrace();
            }
        }
    };

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(5000);
    }

    @Before
    public void setUp() throws Exception
    {
        mActivity = mActivityRule.getActivity();
        resources = mActivity.getResources();
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(whatToClean);
    }

    @Test
    public void testOncreate_1()
    {
        assertThat(mActivity, notNullValue());
        assertThat(isRegisteredUser(mActivity), is(true));

        onView(withId(activityLayoutId)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usuario_email_editT)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usuario_alias_ediT)).check(matches(isDisplayed()));

        onView(allOf(
                withId(R.id.user_data_ac_password_ediT),
                withHint(R.string.user_data_ac_password_hint)))
                .check(matches(isDisplayed()));

        onView(withId(R.id.user_data_modif_button)).check(matches(isDisplayed()));

        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        clickNavigateUp();
    }

    @Test
    public void testOncreate_2()
    {
        // Aserciones sobre los datos mostrados en función del userComu en sesión.
        onView(withId(R.id.reg_usuario_email_editT))
                .check(matches(withText(containsString(USER_JUAN.getUserName()))));
        onView(withId(R.id.reg_usuario_alias_ediT))
                .check(matches(withText(containsString(USER_JUAN.getAlias()))));
        onView(withId(R.id.user_data_ac_password_ediT))
                .check(matches(withText(containsString(""))));
    }

    /* Datos erróneos: formato de email y password vacío. */
    @Test
    public void testModifyUserData_1() throws InterruptedException
    {
        onView(withId(R.id.reg_usuario_alias_ediT)).perform(replaceText("new_alias"));
        onView(withId(R.id.reg_usuario_email_editT)).perform(replaceText("new_user_wrong"), closeSoftKeyboard());
        onView(withId(R.id.user_data_modif_button)).perform(scrollTo())
                .check(matches(isDisplayed())).perform(click());

        checkToastInTest(R.string.error_validation_msg, mActivity,
                R.string.password,
                R.string.email_hint);

        Thread.sleep(3000);
    }

    /* Alias y userName sin cambios. */
    @Test
    public void testModifyUserData_2() throws InterruptedException
    {
        onView(withId(R.id.user_data_ac_password_ediT)).perform(typeText(USER_JUAN.getPassword()), closeSoftKeyboard());
        onView(withId(R.id.user_data_modif_button)).perform(scrollTo())
                .check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.see_usercomu_by_user_frg)).check(matches(isDisplayed()));
    }

    /* Password erróneo en servidor.*/
    @Test
    public void testModifyUserData_3() throws InterruptedException
    {
        onView(withId(R.id.reg_usuario_email_editT)).perform(replaceText("new_juan@juan.es"));
        onView(withId(R.id.user_data_ac_password_ediT)).perform(typeText("wrong_password"), closeSoftKeyboard());
        onView(withId(R.id.user_data_modif_button)).perform(scrollTo())
                .check(matches(isDisplayed())).perform(click());

        checkToastInTest(R.string.password_wrong, mActivity);
        Thread.sleep(3000);
    }

    /* Change alias.*/
    @Test
    public void testModifyUserData_4() throws UiException
    {
        // Check security data: old data.
        SpringOauthToken tokenBefore = TKhandler.getAccessTokenInCache();
        String accessTkValue = tokenBefore != null ? tokenBefore.getValue() : null;
        String refreshTkValue = tokenBefore != null ? tokenBefore.getRefreshToken().getValue() : null;

        onView(withId(R.id.reg_usuario_alias_ediT)).perform(replaceText("new_alias_juan"));
        onView(withId(R.id.user_data_ac_password_ediT)).perform(typeText(USER_JUAN.getPassword()), closeSoftKeyboard());

        onView(withId(R.id.user_data_modif_button)).perform(scrollTo())
                .check(matches(isDisplayed())).perform(click());

        // Verificamos navegación.
        onView(withId(R.id.see_usercomu_by_user_frg)).check(matches(isDisplayed()));
        checkUp(activityLayoutId);

        // New security data: same as the old one.
        SpringOauthToken tokenAfter = TKhandler.getAccessTokenInCache();
        assertThat(tokenAfter != null ? tokenAfter.getValue() : null, is(accessTkValue));  // same accessToken.
        assertThat(tokenAfter != null ? tokenAfter.getRefreshToken().getValue() : null, is(refreshTkValue));  //same refreshToken.
    }

    @Test
    public void testModifyUserData_5() throws UiException
    {
        whatToClean = CLEAN_NOTHING;

        // Check security data: old data.
        SpringOauthToken tokenBefore = TKhandler.getAccessTokenInCache();
        String accessTkValue = tokenBefore != null ? tokenBefore.getValue() : null;
        String refreshTkValue = tokenBefore != null ? tokenBefore.getRefreshToken().getValue() : null;

        onView(withId(R.id.reg_usuario_alias_ediT)).perform(replaceText("new_alias_juan"));
        onView(withId(R.id.reg_usuario_email_editT)).perform(replaceText("new_juan@mail.org"));
        onView(withId(R.id.user_data_ac_password_ediT)).perform(typeText(USER_JUAN.getPassword()), closeSoftKeyboard());

        onView(withId(R.id.user_data_modif_button)).perform(scrollTo())
                .check(matches(isDisplayed())).perform(click());

        // Verificamos navegación.
        onView(withId(R.id.see_usercomu_by_user_frg)).check(matches(isDisplayed()));
        checkUp(activityLayoutId);

        // New security data.
        SpringOauthToken tokenAfter = TKhandler.getAccessTokenInCache();
        assertThat(tokenAfter, notNullValue());
        assertThat(tokenAfter.getValue(), not(is(accessTkValue)));  // differtent accessToken.
        assertThat(tokenAfter.getRefreshToken().getValue(), not(is(refreshTkValue)));  //different refreshToken.

        // Borramos al usuario en BD.
        ServOne.deleteUser();
    }

//    =================================  MENU ==================================

    @Test
    public void testComuSearchMn() throws InterruptedException
    {
        COMU_SEARCH_AC.checkMenuItem_WTk(mActivity);
        // NO navigate-up.
    }

    @Test
    public void testDeleteMeMn() throws InterruptedException
    {
        DELETE_ME_AC.checkMenuItem_WTk(mActivity);
        checkUp(activityLayoutId);
    }

    @Test
    public void testPasswordChangeMn() throws InterruptedException
    {
        PASSWORD_CHANGE_AC.checkMenuItem_WTk(mActivity);
        checkUp(activityLayoutId);
    }

    @Test
    public void testUserComuByUserMn() throws InterruptedException
    {
        SEE_USERCOMU_BY_USER_AC.checkMenuItem_WTk(mActivity);
        checkUp(activityLayoutId);
    }

    @Test
    public void testIncidSeeByComuMn() throws InterruptedException
    {
        INCID_SEE_OPEN_BY_COMU_AC.checkMenuItem_WTk(mActivity);
        checkUp(activityLayoutId);
    }
}