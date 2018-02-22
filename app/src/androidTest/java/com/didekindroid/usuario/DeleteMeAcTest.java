package com.didekindroid.usuario;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.v7.app.AppCompatActivity;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.ActivityNextMock;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.api.router.ContextualRouterIf;
import com.didekindroid.lib_one.api.router.RouterActionIf;
import com.didekindroid.lib_one.api.router.RouterInitializerMock;
import com.didekindroid.lib_one.usuario.dao.CtrlerUsuario;
import com.didekindroid.lib_one.usuario.dao.CtrlerUsuarioIf;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasFlag;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.testutil.MockTestConstant.nextMockAcLayout;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_RODRIGO;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real_rodrigo;
import static com.didekindroid.lib_one.usuario.UserTestData.regGetUserComu;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
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

    @Rule
    public IntentsTestRule<? extends AppCompatActivity> mActivityRule = new IntentsTestRule<DeleteMeAc>(DeleteMeAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            // Precondition: the user is registered.
            try {
                regGetUserComu(comu_real_rodrigo);
            } catch (Exception e) {
                fail();
            }
        }
    };
    private CtrlerUsuarioIf controller;

    @Before
    public void setUp() throws Exception
    {
        activity = (DeleteMeAc) mActivityRule.getActivity();
        // Default initialization.
        controller = new CtrlerUsuario();
    }

    //    =====================================  TESTS  ==========================================

    @Test
    public void testOnCreate() throws Exception
    {
        assertThat(controller.getTkCacher().isRegisteredUser(), is(true));
        assertThat(activity, notNullValue());

        onView(withId(R.id.delete_me_ac_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.delete_me_ac_unreg_button)).check(matches(isDisplayed()));

        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        onView(allOf(withContentDescription(R.string.navigate_up_txt), isClickable()))
                .check(matches(isDisplayed()));

        cleanOptions(CLEAN_RODRIGO);
    }

    @Test
    public final void testOnStop() throws Exception
    {
        activity.runOnUiThread(() -> getInstrumentation().callActivityOnStop(activity));
        // Check.
        assertThat(controller.getSubscriptions().size(), CoreMatchers.is(0));
        cleanOptions(CLEAN_RODRIGO);
    }

    @Test
    public final void testOnStart() throws Exception
    {
        activity.runOnUiThread(() -> {
            getInstrumentation().callActivityOnStart(activity);
            // Check.
            assertThat(controller, notNullValue());
        });
        cleanOptions(CLEAN_RODRIGO);
    }

    @Test
    public void testUnregisterUser() throws UiException, InterruptedException
    {
        routerInitializer.set(new RouterInitializerMock() {
            @Override
            public ContextualRouterIf getContextRouter()
            {
                return contextualName -> (RouterActionIf) () -> ActivityNextMock.class;
            }
        });

        onView(withId(R.id.delete_me_ac_unreg_button)).check(matches(isDisplayed())).perform(click());
        waitAtMost(4, SECONDS).until(() -> controller.isRegisteredUser(), is(false));
        onView(withId(nextMockAcLayout)).check(matches(isDisplayed()));
        intended(hasFlag(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK));
    }
}