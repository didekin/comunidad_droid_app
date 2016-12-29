package com.didekinaar.usuario.delete;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;

import com.didekin.usuario.Usuario;

import com.didekinaar.R;
import com.didekinaar.exception.UiException;
import com.didekinaar.testutil.ExtendableTestAc;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekinaar.security.TokenIdentityCacher.TKhandler;
import static com.didekinaar.testutil.AarActivityTestUtils.clickNavigateUp;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 25/09/15
 * Time: 16:24
 */
@SuppressWarnings("OverriddenMethodCallDuringObjectConstruction")
public abstract class DeleteMeAcTest implements ExtendableTestAc{

    protected DeleteMeAc mActivity;
    protected Usuario registeredUser;

    @Rule
    public ActivityTestRule<? extends Activity> mActivityRule = getActivityRule();

    @Before
    public void setUp() throws Exception
    {
        mActivity = (DeleteMeAc) mActivityRule.getActivity();
    }

    //    =====================================  TESTS  ==========================================

    @Test
    public void testOnCreate() throws Exception
    {
        assertThat(TKhandler.isRegisteredUser(), is(true));
        assertThat(mActivity, notNullValue());

        onView(withId(R.id.delete_me_ac_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.delete_me_ac_unreg_button)).check(matches(isDisplayed()));

        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        clickNavigateUp();

        cleanOneUser(registeredUser);
    }

    @Test
    public void testUnregisterUser() throws UiException
    {
        onView(withId(R.id.delete_me_ac_unreg_button)).check(matches(isDisplayed())).perform(click());
        assertThat(TKhandler.isRegisteredUser(), is(false));
        assertThat(TKhandler.getAccessTokenInCache(), nullValue());
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(false));

        onView(withId(getNextViewResourceId())).check(matches(isDisplayed()));
    }
}