package com.didekindroid.accesorio;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.app.TaskStackBuilder.create;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.cleanTasks;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;

/**
 * User: pedro@didekin
 * Date: 18/09/17
 * Time: 13:06
 */
@RunWith(AndroidJUnit4.class)
public class ConfidencialidadAcTest {

    @Rule
    public ActivityTestRule<ConfidencialidadAc> activityRule = new ActivityTestRule<ConfidencialidadAc>(ConfidencialidadAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            create(getTargetContext()).addParentStack(ConfidencialidadAc.class).startActivities();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @After
    public void tearDown() throws Exception
    {
        cleanTasks(activityRule.getActivity());
    }

    @Test
    public void test_OnCreate_UP()
    {
        onView(withId(R.id.proteccion_datos_textview)).check(matches(withText(R.string.proteccion_datos_txt)));
        checkUp(comuSearchAcLayout);
    }

    @Test
    public void test_ButtonOk()
    {
        onView(withId(R.id.confidencialidad_ac_button)).check(matches(isDisplayed())).perform(click());
        waitAtMost(6, SECONDS).until(isResourceIdDisplayed(comuSearchAcLayout));
    }
}