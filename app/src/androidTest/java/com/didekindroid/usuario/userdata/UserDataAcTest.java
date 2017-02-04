package com.didekindroid.usuario.userdata;

import android.app.Activity;
import android.content.res.Resources;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;

import com.didekindroid.ExtendableTestAc;
import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekinlib.http.oauth2.SpringOauthToken;
import com.didekinlib.model.usuario.Usuario;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
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
import static com.didekindroid.R.id.reg_usuario_alias_ediT;
import static com.didekindroid.R.id.reg_usuario_email_editT;
import static com.didekindroid.R.id.see_usercomu_by_user_frg;
import static com.didekindroid.R.id.user_data_ac_password_ediT;
import static com.didekindroid.R.id.user_data_modif_button;
import static com.didekindroid.comunidad.testutil.ComuMenuTestUtil.COMU_SEARCH_AC;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_SEE_OPEN_BY_COMU_AC;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.usuario.testutil.UserItemMenuTestUtils.PASSWORD_CHANGE_AC;
import static com.didekindroid.usuario.testutil.UserItemMenuTestUtils.USER_DATA_AC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.SEE_USERCOMU_BY_USER_AC;
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
public class UserDataAcTest implements ExtendableTestAc {

    protected UserDataAc mActivity;
    protected Resources resources;
    protected Usuario registeredUser;
    protected int activityLayoutId = R.id.user_data_ac_layout;
    private boolean mustCleanUser;

    @Rule
    public ActivityTestRule<? extends Activity> mActivityRule = new ActivityTestRule<UserDataAc>(UserDataAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            // Precondition: the user is registered.
            try {
                registeredUser = signUpAndUpdateTk(COMU_REAL_JUAN);
                MatcherAssert.assertThat(registeredUser, CoreMatchers.notNullValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(2000);
    }

    @Before
    public void setUp() throws Exception
    {
        mActivity = (UserDataAc) mActivityRule.getActivity();
        resources = mActivity.getResources();
        mustCleanUser = true;
    }

    @After
    public void tearDown() throws Exception
    {
        if (mustCleanUser) {
            cleanOneUser(registeredUser);
        }
    }


    @Override
    public void checkNavigateUp()
    {
        checkUp(activityLayoutId);
    }

    @Override
    public int getNextViewResourceId()
    {
        return see_usercomu_by_user_frg;
    }

    //    =====================================  TESTS  ==========================================

    @Test
    public void testOncreate_1()
    {
        assertThat(mActivity, notNullValue());
        assertThat(TKhandler.isRegisteredUser(), is(true));

        onView(withId(activityLayoutId)).check(matches(isDisplayed()));
        onView(withId(reg_usuario_email_editT)).check(matches(isDisplayed()));
        onView(withId(reg_usuario_alias_ediT)).check(matches(isDisplayed()));

        onView(allOf(
                withId(user_data_ac_password_ediT),
                ViewMatchers.withHint(R.string.user_data_ac_password_hint)))
                .check(matches(isDisplayed()));

        onView(withId(user_data_modif_button)).check(matches(isDisplayed()));

        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        clickNavigateUp();
    }

    @Test
    public void testOncreate_2()
    {
        // Aserciones sobre los datos mostrados en función del userComu en sesión.
        onView(withId(reg_usuario_email_editT))
                .check(matches(withText(containsString(registeredUser.getUserName()))));
        onView(withId(reg_usuario_alias_ediT))
                .check(matches(withText(containsString(registeredUser.getAlias()))));
        onView(withId(user_data_ac_password_ediT))
                .check(matches(withText(containsString(""))));
    }

    /* Datos erróneos: formato de email y password vacío. */
    @Test
    public void testModifyUserData_1() throws InterruptedException
    {
        onView(withId(reg_usuario_alias_ediT)).perform(replaceText("new_alias"));
        onView(withId(reg_usuario_email_editT)).perform(replaceText("new_user_wrong"), closeSoftKeyboard());
        onView(withId(user_data_modif_button)).perform(scrollTo())
                .check(matches(isDisplayed())).perform(click());

        checkToastInTest(R.string.error_validation_msg, mActivity,
                R.string.password,
                R.string.email_hint);

        Thread.sleep(2000);
    }

    /* Password erróneo en servidor.*/
    @Test
    public void testModifyUserData_2() throws InterruptedException
    {
        onView(withId(reg_usuario_email_editT)).perform(replaceText("new_juan@juan.es"));
        onView(withId(user_data_ac_password_ediT)).perform(typeText("wrong_password"), closeSoftKeyboard());
        onView(withId(user_data_modif_button)).perform(scrollTo())
                .check(matches(isDisplayed())).perform(click());

        checkToastInTest(R.string.password_wrong, mActivity);
        Thread.sleep(2000);
    }

    /* Alias y userName sin cambios. */
    @Test
    public void testModifyUserData_3() throws InterruptedException
    {
        onView(withId(user_data_ac_password_ediT)).perform(typeText(registeredUser.getPassword()), closeSoftKeyboard());
        onView(withId(user_data_modif_button)).perform(scrollTo()).check(matches(isDisplayed())).perform(click());
        onView(withId(getNextViewResourceId())).check(matches(isDisplayed()));
    }

    /* Change alias.*/
    @Test
    public void testModifyUserData_4() throws UiException
    {
        // Check security data: old data.
        SpringOauthToken tokenBefore = TKhandler.getAccessTokenInCache();
        String accessTkValue = tokenBefore != null ? tokenBefore.getValue() : null;
        String refreshTkValue = tokenBefore != null ? tokenBefore.getRefreshToken().getValue() : null;

        onView(withId(reg_usuario_alias_ediT)).perform(replaceText("new_alias_juan"));
        onView(withId(user_data_ac_password_ediT)).perform(typeText(registeredUser.getPassword()), closeSoftKeyboard());

        onView(withId(user_data_modif_button)).perform(scrollTo())
                .check(matches(isDisplayed())).perform(click());

        // Verificamos navegación.
        onView(withId(getNextViewResourceId())).check(matches(isDisplayed()));
        checkNavigateUp();

        // New security data: same as the old one.
        SpringOauthToken tokenAfter = TKhandler.getAccessTokenInCache();
        assertThat(tokenAfter != null ? tokenAfter.getValue() : null, is(accessTkValue));  // same accessToken.
        assertThat(tokenAfter != null ? tokenAfter.getRefreshToken().getValue() : null, is(refreshTkValue));  //same refreshToken.
    }

    @Test
    public void testModifyUserData_5() throws UiException
    {
        mustCleanUser = false;

        // Check security data: old data.
        SpringOauthToken tokenBefore = TKhandler.getAccessTokenInCache();
        String accessTkValue = tokenBefore != null ? tokenBefore.getValue() : null;
        String refreshTkValue = tokenBefore != null ? tokenBefore.getRefreshToken().getValue() : null;

        onView(withId(reg_usuario_alias_ediT)).perform(replaceText("new_alias_juan"));
        onView(withId(reg_usuario_email_editT)).perform(replaceText("new_juan@mail.org"));
        onView(withId(user_data_ac_password_ediT)).perform(typeText(registeredUser.getPassword()), closeSoftKeyboard());

        onView(withId(user_data_modif_button)).perform(scrollTo())
                .check(matches(isDisplayed())).perform(click());

        // Verificamos navegación.
        checkNavigateUp();

        // New security data.
        SpringOauthToken tokenAfter = TKhandler.getAccessTokenInCache();
        assertThat(tokenAfter, notNullValue());
        assertThat(tokenAfter.getValue(), not(is(accessTkValue)));  // differtent accessToken.
        assertThat(tokenAfter.getRefreshToken().getValue(), not(is(refreshTkValue)));  //different refreshToken.

        // Borramos al usuario en BD.
        usuarioDao.deleteUser();
    }

    //    =================================  MENU TESTS ==================================

    @Test
    public void testComuSearchMn() throws InterruptedException
    {
        COMU_SEARCH_AC.checkMenuItem_WTk(mActivity);
        // NO navigate-up.
    }

    @Test
    public void testDeleteMeMn() throws InterruptedException
    {
        USER_DATA_AC.checkMenuItem_WTk(mActivity);
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