package com.didekindroid.usuario.delete;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import com.didekindroid.ExtendableTestAc;
import com.didekindroid.R;
import com.didekindroid.api.ViewerMock;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.usuario.Usuario;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.R.id.comu_search_ac_linearlayout;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.checkViewerReplaceView;
import static com.didekindroid.testutil.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.testutil.ActivityTestUtils.hasRegisteredFlag;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 25/09/15
 * Time: 16:24
 */
public class DeleteMeAcTest implements ExtendableTestAc {

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
                e.printStackTrace();
            }
        }
    };
    CtrlerDeleteMeIf controller;

    @BeforeClass
    public static void relax() throws InterruptedException
    {
        MILLISECONDS.sleep(2000);
    }

    @Before
    public void setUp() throws Exception
    {
        activity = (DeleteMeAc) mActivityRule.getActivity();
        // Default initialization.
        controller = new CtrlerDeleteMe(activity);
    }

    @Override
    public void checkNavigateUp()
    {
        throw new UnsupportedOperationException("NO NAVIGATE-UP in DeleteMeAc manager");
    }

    @Override
    public int getNextViewResourceId()
    {
        return comu_search_ac_linearlayout;
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
        InstrumentationRegistry.getInstrumentation().callActivityOnStop(activity);
        // Check.
        assertThat(controller.getSubscriptions().size(), CoreMatchers.is(0));
        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public final void testOnStart() throws Exception
    {
        InstrumentationRegistry.getInstrumentation().callActivityOnStart(activity);
        // Check.
        assertThat(controller, notNullValue());
        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public void testUnregisterUser() throws UiException
    {
        onView(withId(R.id.delete_me_ac_unreg_button)).check(matches(isDisplayed())).perform(click());
        await().atMost(4, SECONDS).until(hasRegisteredFlag(controller), is(false));

        onView(withId(getNextViewResourceId())).check(matches(isDisplayed()));
    }

    @Test
    public void testReplaceView() throws Exception
    {
        checkViewerReplaceView(new ViewerMock<>(null, activity, null), getNextViewResourceId());
        cleanOptions(CLEAN_PEPE);
    }
}