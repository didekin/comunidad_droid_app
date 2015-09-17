package com.didekindroid.usuario.activity;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.didekindroid.R;
import com.didekindroid.uiutils.UIutils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.usuario.activity.utils.CleanEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.activity.utils.UserMenuTestUtils.COMU_SEARCH_AC;
import static com.didekindroid.usuario.activity.utils.UserMenuTestUtils.SEE_USERCOMU_BY_USER_AC;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.cleanOptions;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.signUpAndUpdateTk;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_REAL_JUAN;
import static org.hamcrest.Matchers.is;
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
    RegUserFr mRegUserFr;

    @Rule
    public ActivityTestRule<UserDataAc> mActivityRule = new ActivityTestRule<UserDataAc>(UserDataAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            // Precondition: the user is registered.
            signUpAndUpdateTk(COMU_REAL_JUAN);
        }
    };

    @Before
    public void setUp() throws Exception
    {
        mActivity = mActivityRule.getActivity();
        mRegUserFr = (RegUserFr) mActivity.getFragmentManager().findFragmentById(R.id.reg_user_frg);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void testOncreate_1()
    {
        assertThat(mActivity, notNullValue());
        assertThat(mRegUserFr, notNullValue());
        assertThat(UIutils.isRegisteredUser(mActivity), is(true));

        onView(withId(R.id.user_data_ac_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_user_frg)).check(matches(isDisplayed()));
    }

    @Test
    public void testOncreate_2()
    {
        // Aserciones sobre los datos mostrados en función del usuario en sesión. Primera asyncTask.
    }

    @Test
    public void testUserComuByUserMn_withToken() throws InterruptedException
    {
        SEE_USERCOMU_BY_USER_AC.checkMenuItem_WTk(mActivity);
    }

    @Test
    public void testComuSearchMn_withToken() throws InterruptedException
    {
        COMU_SEARCH_AC.checkMenuItem_WTk(mActivity);
    }

    @Test
    public void testModifyUserData()
    {

    }

    @Test
    public void testUnregisterUser()
    {

    }
}