package com.didekindroid.usuario.activity;

import android.content.res.Resources;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.didekin.retrofitcl.OauthToken;
import com.didekindroid.R;
import com.didekindroid.usuario.activity.utils.CleanEnum;
import org.junit.*;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.didekindroid.security.TokenHandler.TKhandler;
import static com.didekindroid.uiutils.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.activity.utils.CleanEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.activity.utils.CleanEnum.CLEAN_NOTHING;
import static com.didekindroid.usuario.activity.utils.UserMenuTestUtils.*;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.*;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_REAL_JUAN;
import static com.didekindroid.usuario.dominio.DomainDataUtils.USER_JUAN;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static org.hamcrest.Matchers.*;
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
    CleanEnum whatToClean = CLEAN_JUAN;

    @Rule
    public ActivityTestRule<UserDataAc> mActivityRule = new ActivityTestRule<UserDataAc>(UserDataAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            // Precondition: the user is registered.
            signUpAndUpdateTk(COMU_REAL_JUAN);
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

        onView(withId(R.id.user_data_ac_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usuario_email_editT)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usuario_alias_ediT)).check(matches(isDisplayed()));

        onView(allOf(
                withId(R.id.user_data_ac_password_ediT),
                withHint(R.string.user_data_ac_password_hint)))
                .check(matches(isDisplayed()));

        onView(withId(R.id.user_data_modif_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testOncreate_2()
    {
        // Aserciones sobre los datos mostrados en función del usuario en sesión.
        onView(withId(R.id.reg_usuario_email_editT))
                .check(matches(withText(containsString(USER_JUAN.getUserName()))));
        onView(withId(R.id.reg_usuario_alias_ediT))
                .check(matches(withText(containsString(USER_JUAN.getAlias()))));
        onView(withId(R.id.user_data_ac_password_ediT))
                .check(matches(withText(containsString(""))));
    }

    @Test
    public void testComuSearchMn_withToken() throws InterruptedException
    {
        COMU_SEARCH_AC.checkMenuItem_WTk(mActivity);
    }

    @Test
    public void testDeleteMeMn_withToken() throws InterruptedException
    {
        DELETE_ME_AC.checkMenuItem_WTk(mActivity);
    }

    @Test
    public void testPasswordChangeMn_withToken() throws InterruptedException
    {
        PASSWORD_CHANGE_AC.checkMenuItem_WTk(mActivity);
    }

    @Test
    public void testUserComuByUserMn_withToken() throws InterruptedException
    {
        SEE_USERCOMU_BY_USER_AC.checkMenuItem_WTk(mActivity);
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
        onView(withId(R.id.see_usercomu_by_user_ac_frg_container)).check(matches(isDisplayed()));

        Thread.sleep(3000);
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
    }

    /* Change alias.*/
    @Test
    public void testModifyUserData_4()
    {
        // Check security data: old data.
        OauthToken.AccessToken tokenBefore = TKhandler.getAccessTokenInCache();
        String accessTkValue = tokenBefore.getValue();
        String refreshTkValue = tokenBefore.getRefreshToken().getValue();

        onView(withId(R.id.reg_usuario_alias_ediT)).perform(replaceText("new_alias_juan"));
        onView(withId(R.id.user_data_ac_password_ediT)).perform(typeText(USER_JUAN.getPassword()), closeSoftKeyboard());

        onView(withId(R.id.user_data_modif_button)).perform(scrollTo())
                .check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.see_usercomu_by_user_ac_frg_container)).check(matches(isDisplayed()));

        // New security data: same as the old one.
        OauthToken.AccessToken tokenAfter = TKhandler.getAccessTokenInCache();
        assertThat(tokenAfter.getValue(), is(accessTkValue));  // same accessToken.
        assertThat(tokenAfter.getRefreshToken().getValue(), is(refreshTkValue));  //same refreshToken.
    }

    @Test
    public void testModifyUserData_5()
    {
        whatToClean = CLEAN_NOTHING;

        // Check security data: old data.
        OauthToken.AccessToken tokenBefore = TKhandler.getAccessTokenInCache();
        String accessTkValue = tokenBefore.getValue();
        String refreshTkValue = tokenBefore.getRefreshToken().getValue();

        onView(withId(R.id.reg_usuario_alias_ediT)).perform(replaceText("new_alias_juan"));
        onView(withId(R.id.reg_usuario_email_editT)).perform(replaceText("new_juan@mail.org"));
        onView(withId(R.id.user_data_ac_password_ediT)).perform(typeText(USER_JUAN.getPassword()), closeSoftKeyboard());

        onView(withId(R.id.user_data_modif_button)).perform(scrollTo())
                .check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.see_usercomu_by_user_ac_frg_container)).check(matches(isDisplayed()));

        // New security data.
        OauthToken.AccessToken tokenAfter = TKhandler.getAccessTokenInCache();
        assertThat(tokenAfter, notNullValue());
        assertThat(tokenAfter.getValue(), not(is(accessTkValue)));  // differtent accessToken.
        assertThat(tokenAfter.getRefreshToken().getValue(), not(is(refreshTkValue)));  //different refreshToken.

        ServOne.deleteUser();
    }
}