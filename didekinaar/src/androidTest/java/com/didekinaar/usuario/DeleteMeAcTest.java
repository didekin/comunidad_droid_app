package com.didekinaar.usuario;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekinaar.R;
import com.didekinaar.exception.UiAarException;
import com.didekinaar.testutil.AarActivityTestUtils;
import com.didekinaar.testutil.CleanUserEnum;
import com.didekinaar.testutil.UsuarioTestUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static com.didekinaar.security.TokenHandler.TKhandler;
import static com.didekinaar.testutil.AarActivityTestUtils.cleanOptions;
import static com.didekinaar.testutil.AarActivityTestUtils.clickNavigateUp;
import static com.didekinaar.testutil.CleanUserEnum.CLEAN_NOTHING;
import static com.didekinaar.testutil.CleanUserEnum.CLEAN_PEPE;
import static com.didekinaar.utils.UIutils.isRegisteredUser;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 25/09/15
 * Time: 16:24
 */
@RunWith(AndroidJUnit4.class)
public class DeleteMeAcTest {

    DeleteMeAc mActivity;
    CleanUserEnum whatToClean = CLEAN_PEPE;

    @Rule
    public ActivityTestRule<DeleteMeAc> mActivityRule = new ActivityTestRule<DeleteMeAc>(DeleteMeAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            // Precondition: the user is registered.
            try {
                AarActivityTestUtils.signUpAndUpdateTk(UsuarioTestUtils.COMU_REAL_PEPE);
            } catch (UiAarException | IOException e) {
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
        onView(ViewMatchers.withId(R.id.delete_me_ac_layout)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.delete_me_ac_unreg_button)).check(matches(isDisplayed()));

        onView(ViewMatchers.withId(R.id.appbar)).check(matches(isDisplayed()));
        clickNavigateUp();
    }

    @Test
    public void testUnregisterUser() throws UiAarException
    {
        whatToClean = CLEAN_NOTHING;

        onView(ViewMatchers.withId(R.id.delete_me_ac_unreg_button)).check(matches(isDisplayed())).perform(click());
        assertThat(isRegisteredUser(mActivity), is(false));
        assertThat(TKhandler.getAccessTokenInCache(), nullValue());
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(false));

        onView(ViewMatchers.withId(R.id.comu_search_ac_linearlayout)).check(matches(isDisplayed()));
        // No hay Navigate-up.
        onView(ViewMatchers.withContentDescription(R.string.navigate_up_txt)).check(doesNotExist());
    }
}