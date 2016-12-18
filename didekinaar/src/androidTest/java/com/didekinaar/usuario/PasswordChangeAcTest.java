package com.didekinaar.usuario;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;

import com.didekin.oauth2.SpringOauthToken;
import com.didekin.usuario.Usuario;
import com.didekinaar.R;
import com.didekinaar.exception.UiException;
import com.didekinaar.testutil.AarActivityTestUtils.CleanUserEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekinaar.security.Oauth2Service.Oauth2;
import static com.didekinaar.security.TokenHandler.TKhandler;
import static com.didekinaar.testutil.AarActivityTestUtils.CleanUserEnum.CLEAN_NOTHING;
import static com.didekinaar.testutil.AarActivityTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekinaar.testutil.AarActivityTestUtils.checkToastInTest;
import static com.didekinaar.testutil.AarActivityTestUtils.checkUp;
import static com.didekinaar.testutil.AarActivityTestUtils.cleanOneUser;
import static com.didekinaar.testutil.AarActivityTestUtils.cleanOptions;
import static com.didekinaar.testutil.AarActivityTestUtils.clickNavigateUp;
import static com.didekinaar.usuario.testutil.UsuarioTestUtils.USER_PEPE;
import static com.didekinaar.utils.UIutils.isRegisteredUser;
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
public abstract class PasswordChangeAcTest {

    PasswordChangeAc mActivity;
    CleanUserEnum whatToClean = CLEAN_PEPE;
    int activityLayoutId = R.id.password_change_ac_layout;

    @Rule
    public ActivityTestRule<PasswordChangeAc> mActivityRule =
            new ActivityTestRule<PasswordChangeAc>(PasswordChangeAc.class) {
                @Override
                protected void beforeActivityLaunched()
                {
                    // Precondition: the user is registered.
                    try {
                        registerUser();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

    @Before
    public void setUp() throws Exception
    {
        mActivity = mActivityRule.getActivity();
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(whatToClean);
    }

    //  ............................. METHODS TO BE OVERWRITTEN ..................................

    protected abstract void registerUser() throws Exception;

    //    =====================================  TESTS  ==========================================

    @Test
    public void testOnCreate() throws Exception
    {
        assertThat(mActivity, notNullValue());
        assertThat(isRegisteredUser(mActivity), is(true));

        onView(withId(activityLayoutId)).check(matches(isDisplayed()));

        onView(ViewMatchers.withId(R.id.reg_usuario_password_ediT)).check(matches(withText(containsString(""))))
                .check(matches(ViewMatchers.withHint(R.string.usuario_password_hint)))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.reg_usuario_password_confirm_ediT)).check(matches(withText(containsString(""))))
                .check(matches(ViewMatchers.withHint(R.string.usuario_password_confirm_hint)))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.password_change_ac_button)).check(matches(ViewMatchers.withText(R.string.password_change_ac_button_txt)))
                .check(matches(isDisplayed()));

        onView(ViewMatchers.withId(R.id.appbar)).check(matches(isDisplayed()));
        clickNavigateUp();
    }

    @Test
    public void testPasswordChange_NotOK() throws InterruptedException
    {
        onView(ViewMatchers.withId(R.id.reg_usuario_password_ediT)).perform(replaceText("new_pepe_password"));
        onView(ViewMatchers.withId(R.id.reg_usuario_password_confirm_ediT)).perform(replaceText("new_wrong_password"));
        onView(ViewMatchers.withId(R.id.password_change_ac_button)).check(matches(isDisplayed())).perform(click());

        checkToastInTest(R.string.error_validation_msg, mActivity, R.string.password_different);
        // Se queda en la misma actividad.
        onView(ViewMatchers.withId(R.id.password_change_ac_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void testPasswordChange_OK() throws UiException
    {
        whatToClean = CLEAN_NOTHING;

        // Check security data: old data.
        SpringOauthToken tokenBefore = TKhandler.getAccessTokenInCache();
        String accessTkValue = tokenBefore != null ? tokenBefore.getValue() : null;
        String refreshTkValue = tokenBefore != null ? tokenBefore.getRefreshToken().getValue() : null;

        onView(ViewMatchers.withId(R.id.reg_usuario_password_ediT)).perform(replaceText("new_pepe_password"));
        onView(ViewMatchers.withId(R.id.reg_usuario_password_confirm_ediT)).perform(replaceText("new_pepe_password"));
        onView(ViewMatchers.withId(R.id.password_change_ac_button)).check(matches(isDisplayed())).perform(click());

        onView(ViewMatchers.withId(R.id.user_data_ac_layout)).check(matches(isDisplayed()));
        checkUp(activityLayoutId);

        // Check security data: new data.
        SpringOauthToken tokenAfter = Oauth2.getRefreshUserToken(refreshTkValue);
        assertThat(tokenAfter.getValue(), not(is(accessTkValue)));  // new accessToken.
        assertThat(tokenAfter.getRefreshToken().getValue(), not(is(refreshTkValue)));  // new refreshToken.

        cleanOneUser(new Usuario.UsuarioBuilder()
                .userName(USER_PEPE.getUserName())
                .password("new_pepe_password")
                .build());
    }
}