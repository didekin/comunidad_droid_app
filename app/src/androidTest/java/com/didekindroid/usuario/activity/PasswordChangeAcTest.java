package com.didekindroid.usuario.activity;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.didekin.retrofitcl.OauthToken.AccessToken;
import com.didekin.serviceone.domain.Usuario;
import com.didekindroid.R;
import com.didekindroid.usuario.activity.utils.CleanEnum;
import org.junit.*;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.didekindroid.security.TokenHandler.TKhandler;
import static com.didekindroid.uiutils.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.activity.utils.CleanEnum.CLEAN_NOTHING;
import static com.didekindroid.usuario.activity.utils.CleanEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.*;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuario.dominio.DomainDataUtils.USER_PEPE;
import static com.didekindroid.usuario.webservices.Oauth2Service.Oauth2;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 25/09/15
 * Time: 17:45
 */
@RunWith(AndroidJUnit4.class)
public class PasswordChangeAcTest {

    PasswordChangeAc mActivity;
    CleanEnum whatToClean = CLEAN_PEPE;

    @Rule
    public ActivityTestRule<PasswordChangeAc> mActivityRule =
            new ActivityTestRule<PasswordChangeAc>(PasswordChangeAc.class) {
                @Override
                protected void beforeActivityLaunched()
                {
                    // Precondition: the user is registered.
                    signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
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
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(whatToClean);
    }

    @Test
    public void testOnCreate() throws Exception
    {
        assertThat(mActivity, notNullValue());
        assertThat(isRegisteredUser(mActivity), is(true));

        onView(withId(R.id.reg_usuario_password_ediT)).check(matches(withText(containsString(""))))
                .check(matches(withHint(R.string.usuario_password_hint)))
                .check(matches(isDisplayed()));
        onView(withId(R.id.reg_usuario_password_confirm_ediT)).check(matches(withText(containsString(""))))
                .check(matches(withHint(R.string.usuario_password_confirm_hint)))
                .check(matches(isDisplayed()));
        onView(withId(R.id.password_change_ac_button)).check(matches(withText(R.string.password_change_ac_button_txt)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testPasswordChange_1() throws InterruptedException
    {
        onView(withId(R.id.reg_usuario_password_ediT)).perform(replaceText("new_pepe_password"));
        onView(withId(R.id.reg_usuario_password_confirm_ediT)).perform(replaceText("new_wrong_password"));
        onView(withId(R.id.password_change_ac_button)).check(matches(isDisplayed())).perform(click());

        checkToastInTest(R.string.error_validation_msg, mActivity, R.string.password_different);
        Thread.sleep(3000);
    }

    @Test
    public void testPasswordChange_2()
    {
        whatToClean = CLEAN_NOTHING;

        // Check security data: old data.
        AccessToken tokenBefore = TKhandler.getAccessTokenInCache();
        String accessTkValue = tokenBefore.getValue();
        String refreshTkValue = tokenBefore.getRefreshToken().getValue();

        onView(withId(R.id.reg_usuario_password_ediT)).perform(replaceText("new_pepe_password"));
        onView(withId(R.id.reg_usuario_password_confirm_ediT)).perform(replaceText("new_pepe_password"));
        onView(withId(R.id.password_change_ac_button)).check(matches(isDisplayed())).perform(click());

        onView(withId(R.id.user_data_ac_layout)).check(matches(isDisplayed()));

        // Check security data: new data.
        AccessToken tokenAfter = Oauth2.getRefreshUserToken(refreshTkValue);
        assertThat(tokenAfter.getValue(), not(is(accessTkValue)));  // new accessToken.
        assertThat(tokenAfter.getRefreshToken().getValue(), not(is(refreshTkValue)));  // new refreshToken.

        cleanOneUser(new Usuario.UsuarioBuilder()
                .userName(USER_PEPE.getUserName())
                .password("new_pepe_password")
                .build());
    }
}