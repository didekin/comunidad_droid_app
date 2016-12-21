package com.didekinaar.usuario.userdata;

import android.app.Activity;
import android.content.res.Resources;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;

import com.didekin.oauth2.SpringOauthToken;
import com.didekinaar.R;
import com.didekinaar.exception.UiException;
import com.didekinaar.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum;
import com.didekinaar.testutil.ExtendableTestAc;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekinaar.security.TokenHandler.TKhandler;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_NOTHING;
import static com.didekinaar.testutil.AarActivityTestUtils.checkToastInTest;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekinaar.testutil.AarActivityTestUtils.clickNavigateUp;
import static com.didekinaar.usuario.UsuarioDaoRemote.usuarioDaoRemote;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekinaar.utils.UIutils.isRegisteredUser;
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
public abstract class UserDataAcTest implements ExtendableTestAc {

    protected UserDataAc mActivity;
    protected Resources resources;
    protected CleanUserEnum whatToClean = CLEAN_JUAN;
    protected int activityLayoutId = R.id.user_data_ac_layout;

    @Rule
    public ActivityTestRule<? extends Activity> mActivityRule = getActivityRule();

    @Before
    public void setUp() throws Exception
    {
        mActivity = (UserDataAc) mActivityRule.getActivity();
        resources = mActivity.getResources();
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(whatToClean);
    }

    //    =====================================  TESTS  ==========================================

    @Test
    public void testOncreate_A()
    {
        assertThat(mActivity, notNullValue());
        assertThat(isRegisteredUser(mActivity), is(true));

        onView(withId(activityLayoutId)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.reg_usuario_email_editT)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.reg_usuario_alias_ediT)).check(matches(isDisplayed()));

        onView(allOf(
                ViewMatchers.withId(R.id.user_data_ac_password_ediT),
                ViewMatchers.withHint(R.string.user_data_ac_password_hint)))
                .check(matches(isDisplayed()));

        onView(ViewMatchers.withId(R.id.user_data_modif_button)).check(matches(isDisplayed()));

        onView(ViewMatchers.withId(R.id.appbar)).check(matches(isDisplayed()));
        clickNavigateUp();
    }

    @Test
    public void testOncreate_B()
    {
        // Aserciones sobre los datos mostrados en función del userComu en sesión.
        onView(ViewMatchers.withId(R.id.reg_usuario_email_editT))
                .check(matches(withText(containsString(USER_JUAN.getUserName()))));
        onView(ViewMatchers.withId(R.id.reg_usuario_alias_ediT))
                .check(matches(withText(containsString(USER_JUAN.getAlias()))));
        onView(ViewMatchers.withId(R.id.user_data_ac_password_ediT))
                .check(matches(withText(containsString(""))));
    }

    /* Datos erróneos: formato de email y password vacío. */
    @Test
    public void testModifyUserData_A() throws InterruptedException
    {
        onView(ViewMatchers.withId(R.id.reg_usuario_alias_ediT)).perform(replaceText("new_alias"));
        onView(ViewMatchers.withId(R.id.reg_usuario_email_editT)).perform(replaceText("new_user_wrong"), closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.user_data_modif_button)).perform(scrollTo())
                .check(matches(isDisplayed())).perform(click());

        checkToastInTest(R.string.error_validation_msg, mActivity,
                R.string.password,
                R.string.email_hint);

        Thread.sleep(2000);
    }

    /* Password erróneo en servidor.*/
    @Test
    public void testModifyUserData_B() throws InterruptedException
    {
        onView(ViewMatchers.withId(R.id.reg_usuario_email_editT)).perform(replaceText("new_juan@juan.es"));
        onView(ViewMatchers.withId(R.id.user_data_ac_password_ediT)).perform(typeText("wrong_password"), closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.user_data_modif_button)).perform(scrollTo())
                .check(matches(isDisplayed())).perform(click());

        checkToastInTest(R.string.password_wrong, mActivity);
        Thread.sleep(2000);
    }

    /* Change alias.*/
    @Test
    public void testModifyUserData_4() throws UiException
    {
        // Check security data: old data.
        SpringOauthToken tokenBefore = TKhandler.getAccessTokenInCache();
        String accessTkValue = tokenBefore != null ? tokenBefore.getValue() : null;
        String refreshTkValue = tokenBefore != null ? tokenBefore.getRefreshToken().getValue() : null;

        onView(ViewMatchers.withId(R.id.reg_usuario_alias_ediT)).perform(replaceText("new_alias_juan"));
        onView(ViewMatchers.withId(R.id.user_data_ac_password_ediT)).perform(typeText(USER_JUAN.getPassword()), closeSoftKeyboard());

        onView(ViewMatchers.withId(R.id.user_data_modif_button)).perform(scrollTo())
                .check(matches(isDisplayed())).perform(click());

        // Verificamos navegación.
        checkNavigateUp();

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

        onView(ViewMatchers.withId(R.id.reg_usuario_alias_ediT)).perform(replaceText("new_alias_juan"));
        onView(ViewMatchers.withId(R.id.reg_usuario_email_editT)).perform(replaceText("new_juan@mail.org"));
        onView(ViewMatchers.withId(R.id.user_data_ac_password_ediT)).perform(typeText(USER_JUAN.getPassword()), closeSoftKeyboard());

        onView(ViewMatchers.withId(R.id.user_data_modif_button)).perform(scrollTo())
                .check(matches(isDisplayed())).perform(click());

        // Verificamos navegación.
        checkNavigateUp();

        // New security data.
        SpringOauthToken tokenAfter = TKhandler.getAccessTokenInCache();
        assertThat(tokenAfter, notNullValue());
        assertThat(tokenAfter.getValue(), not(is(accessTkValue)));  // differtent accessToken.
        assertThat(tokenAfter.getRefreshToken().getValue(), not(is(refreshTkValue)));  //different refreshToken.

        // Borramos al usuario en BD.
        usuarioDaoRemote.deleteUser();
    }
}