package com.didekindroid.usuario.delete;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.usuario.Usuario;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.Callable;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 25/09/15
 * Time: 16:24
 */
public class DeleteMeAcTest {

    protected DeleteMeAc activity;
    protected Usuario registeredUser;

    @Rule
    public ActivityTestRule<? extends Activity> mActivityRule = new ActivityTestRule<DeleteMeAc>(DeleteMeAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            // Precondition: the user is registered.
            try {
                registeredUser = signUpAndUpdateTk(COMU_REAL_PEPE);
            } catch (Exception e) {
                fail();
            }
        }
    };
    CtrlerDeleteMeIf controller;

    @Before
    public void setUp() throws Exception
    {
        activity = (DeleteMeAc) mActivityRule.getActivity();
        // Default initialization.
        controller = new CtrlerDeleteMe();
    }

    //    =====================================  TESTS  ==========================================

    @Test
    public void testOnCreate() throws Exception
    {
        assertThat(TKhandler.isRegisteredUser(), is(true));
        assertThat(activity, notNullValue());

        onView(withId(R.id.delete_me_ac_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.delete_me_ac_unreg_button)).check(matches(isDisplayed()));

        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        clickNavigateUp();

        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public final void testOnStop() throws Exception
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                getInstrumentation().callActivityOnStop(activity);
            }
        });
        // Check.
        assertThat(controller.getSubscriptions().size(), CoreMatchers.is(0));
        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public final void testOnStart() throws Exception
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                getInstrumentation().callActivityOnStart(activity);
                // Check.
                assertThat(controller, notNullValue());
            }
        });
        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public void testUnregisterUser() throws UiException
    {
        onView(withId(R.id.delete_me_ac_unreg_button)).check(matches(isDisplayed())).perform(click());
        waitAtMost(4, SECONDS).until(new Callable<Boolean>() {
            public Boolean call() throws Exception
            {
                return controller.isRegisteredUser();
            }
        }, is(false));

        onView(withId(comuSearchAcLayout)).check(matches(isDisplayed()));
    }
}