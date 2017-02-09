package com.didekindroid.usuario.login;

import android.app.Activity;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import com.didekindroid.ExtendableTestAc;
import com.didekindroid.R;
import com.didekinlib.http.oauth2.SpringOauthToken;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.R.id.comu_search_ac_linearlayout;
import static com.didekindroid.R.id.login_ac_button;
import static com.didekindroid.R.id.reg_usuario_email_editT;
import static com.didekindroid.R.id.reg_usuario_password_ediT;
import static com.didekindroid.R.string.send_password_by_mail_dialog;
import static com.didekindroid.security.Oauth2DaoRemote.Oauth2;
import static com.didekindroid.testutil.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanWithTkhandler;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_DROID;
import static com.didekinlib.http.oauth2.OauthTokenHelper.HELPER;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 26/10/15
 * Time: 13:55
 */
public class LoginAcTest implements ExtendableTestAc {

    @Rule
    public ActivityTestRule<? extends Activity> mActivityRule = new ActivityTestRule<>(LoginAc.class, true, false);
    protected LoginAc mActivity;
    protected int activityLayoutId = R.id.login_ac_layout;

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(2000);
    }

    @Override
    public void checkNavigateUp()
    {
        throw new UnsupportedOperationException("NO NAVIGATE-UP in LoginAc activity");
    }

    @Override
    public int getNextViewResourceId()
    {
        return comu_search_ac_linearlayout;
    }

    //    =====================================  TESTS  ==========================================

    @Test
    public void testValidate_6() throws Exception
    {
        // User in DB: wrong password three consecutive times. Choice "yes mail" in dialog.
        userComuDaoRemote.regComuAndUserAndUserComu(COMU_REAL_DROID).execute().body();
        SpringOauthToken token = Oauth2.getPasswordUserToken(USER_DROID.getUserName(), USER_DROID.getPassword());

        mActivity = (LoginAc) mActivityRule.launchActivity(new Intent());

        getDialogFragment(USER_DROID.getUserName());
        MILLISECONDS.sleep(2000);

        onView(withText(send_password_by_mail_dialog)).inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText(R.string.send_password_by_mail_YES)).inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withId(reg_usuario_email_editT)).check(matches(isDisplayed()));
        onView(withId(reg_usuario_password_ediT)).check(matches(isDisplayed()));
        onView(withId(login_ac_button)).check(matches(isDisplayed()));

        MILLISECONDS.sleep(5000);
        checkToastInTest(R.string.password_new_in_login, mActivity);


        token = Oauth2.getRefreshUserToken(token.getRefreshToken().getValue());
        // Verificamos cambio de password.
        String newPassword = usuarioDao.getEndPoint().getUserData(HELPER.doBearerAccessTkHeader(token)).execute().body().getPassword();
        assertThat(newPassword, notNullValue());
        assertThat(newPassword.length() > 12, is(true));

        usuarioDao.getEndPoint().deleteUser(HELPER.doBearerAccessTkHeader(token)).execute();
        cleanWithTkhandler();
    }

    //    ========================== Utility methods ============================

    private void typeCheckClickPswdWrong(String userName) throws InterruptedException
    {
        if (userName != null) {
            onView(withId(reg_usuario_email_editT)).perform(typeText(userName));
        }
        onView(withId(reg_usuario_password_ediT)).perform(typeText("pasword_wrong"));
        onView(withId(login_ac_button)).check(matches(isDisplayed())).perform(click());

        MILLISECONDS.sleep(1000);
        checkToastInTest(R.string.password_wrong, mActivity);
        onView(withId(activityLayoutId)).check(matches(isDisplayed()));
    }

    private void reTypeCheckClickPswdWrong(String userName)
    {
        onView(withId(reg_usuario_email_editT)).perform(replaceText(userName));
        onView(withId(reg_usuario_password_ediT)).perform(replaceText("pasword_wrong"));
        onView(withId(login_ac_button)).check(matches(isDisplayed())).perform(click());

        checkToastInTest(R.string.password_wrong, mActivity);
        onView(withId(activityLayoutId)).check(matches(isDisplayed()));
    }

    private void getDialogFragment(String userName) throws InterruptedException
    {
        typeCheckClickPswdWrong(userName);
        Thread.sleep(1000);
        reTypeCheckClickPswdWrong(userName);
        Thread.sleep(1000);
        reTypeCheckClickPswdWrong(userName);

        onView(withId(reg_usuario_email_editT)).perform(replaceText(userName));
        onView(withId(reg_usuario_password_ediT)).perform(replaceText("pasword_wrong"));
        onView(withId(login_ac_button)).check(matches(isDisplayed())).perform(click());
    }
}
