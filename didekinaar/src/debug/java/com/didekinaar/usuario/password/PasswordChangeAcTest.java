package com.didekinaar.usuario.password;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;

import com.didekin.oauth2.SpringOauthToken;
import com.didekin.usuario.Usuario;

import com.didekinaar.R;
import com.didekinaar.exception.UiException;
import com.didekinaar.testutil.ExtendableTestAc;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekinaar.security.Oauth2DaoRemote.Oauth2;
import static com.didekinaar.security.TokenIdentityCacher.TKhandler;
import static com.didekinaar.testutil.AarActivityTestUtils.checkToastInTest;
import static com.didekinaar.testutil.AarActivityTestUtils.checkUp;
import static com.didekinaar.testutil.AarActivityTestUtils.clickNavigateUp;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 25/09/15
 * Time: 17:45
 */
@SuppressWarnings("OverriddenMethodCallDuringObjectConstruction")
public abstract class PasswordChangeAcTest implements ExtendableTestAc {

    private PasswordChangeAc mActivity;
    private int activityLayoutId = R.id.password_change_ac_layout;
    protected Usuario registeredUser;

    @Rule
    public ActivityTestRule<? extends Activity> mActivityRule =  getActivityRule();


    @Before
    public void setUp() throws Exception
    {
        mActivity = (PasswordChangeAc) mActivityRule.getActivity();
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOneUser(registeredUser);
    }

    //    =====================================  TESTS  ==========================================

    @Test
    public void testOnCreate() throws Exception
    {
        assertThat(mActivity, notNullValue());
        assertThat(TKhandler.isRegisteredUser(), is(true));

        onView(withId(activityLayoutId)).check(matches(isDisplayed()));

        onView(withId(R.id.reg_usuario_password_ediT)).check(matches(withText(containsString(""))))
                .check(matches(withHint(R.string.usuario_password_hint)))
                .check(matches(isDisplayed()));
        onView(withId(R.id.reg_usuario_password_confirm_ediT)).check(matches(withText(containsString(""))))
                .check(matches(withHint(R.string.usuario_password_confirm_hint)))
                .check(matches(isDisplayed()));
        onView(withId(R.id.password_change_ac_button)).check(matches(withText(R.string.password_change_ac_button_txt)))
                .check(matches(isDisplayed()));

        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        clickNavigateUp();
    }

    @Test
    public void testPasswordChange_NotOK() throws InterruptedException
    {
        onView(withId(R.id.reg_usuario_password_ediT)).perform(replaceText("new_pepe_password"));
        onView(withId(R.id.reg_usuario_password_confirm_ediT)).perform(replaceText("new_wrong_password"));
        onView(withId(R.id.password_change_ac_button)).check(matches(isDisplayed())).perform(click());

        checkToastInTest(R.string.error_validation_msg, mActivity, R.string.password_different);
        // Se queda en la misma actividad.
        onView(withId(R.id.password_change_ac_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void testPasswordChange_OK() throws UiException
    {
        // Check security data: old data.
        SpringOauthToken tokenBefore = TKhandler.getAccessTokenInCache();
        String accessTkValue = tokenBefore != null ? tokenBefore.getValue() : null;
        String refreshTkValue = tokenBefore != null ? tokenBefore.getRefreshToken().getValue() : null;

        onView(withId(R.id.reg_usuario_password_ediT)).perform(replaceText("new_pepe_password"));
        onView(withId(R.id.reg_usuario_password_confirm_ediT)).perform(replaceText("new_pepe_password"));
        onView(withId(R.id.password_change_ac_button)).check(matches(isDisplayed())).perform(click());

        onView(withId(getNextViewResourceId())).check(matches(isDisplayed()));
        checkUp(activityLayoutId);

        // Check security data: new data.
        SpringOauthToken tokenAfter = Oauth2.getRefreshUserToken(refreshTkValue);
        assertThat(tokenAfter.getValue(), not(is(accessTkValue)));  // new accessToken.
        assertThat(tokenAfter.getRefreshToken().getValue(), not(is(refreshTkValue)));  // new refreshToken.

        cleanOneUser(new Usuario.UsuarioBuilder()
                .userName(registeredUser.getUserName())
                .password("new_pepe_password")
                .build());
    }
}