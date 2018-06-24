package com.didekindroid.usuario;

import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Build;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.lib_one.usuario.UserDataAc;
import com.didekindroid.usuariocomunidad.listbyuser.SeeUserComuByUserAc;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.app.TaskStackBuilder.create;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.lib_one.testutil.UiTestUtil.cleanTasks;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.regUserComuGetAuthTk;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_REAL_JUAN;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro
 * Date: 16/07/15
 * Time: 14:25
 */
@RunWith(AndroidJUnit4.class)
public class UserData_App_Test {

    private UserDataAc activity;
    private TaskStackBuilder stackBuilder;

    @Rule
    public IntentsTestRule<? extends Activity> mActivityRule = new IntentsTestRule<UserDataAc>(UserDataAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            try {
                regUserComuGetAuthTk(COMU_REAL_JUAN);
            } catch (Exception e) {
                fail();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                stackBuilder = create(getTargetContext());
                stackBuilder.addParentStack(UserDataAc.class).startActivities();
            }
        }
    };

    @Before
    public void setUp()
    {
        activity = (UserDataAc) mActivityRule.getActivity();
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_JUAN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cleanTasks(activity);
        }
    }

    // ============================================================
    //    ................ TESTS ..............
    // ============================================================

    @Test
    public void testOncreate()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            testBackStack();
            checkUp(seeUserComuByUserFrRsId);
        }
    }

    public void testBackStack()
    {
        List<Intent> intents = asList(stackBuilder.getIntents());
        assertThat(intents.size(), is(2));
        // El intent con posición inferior es el primero que hemos añadido.
        assertThat(requireNonNull(intents.get(0).getComponent()).getClassName(), is(ComuSearchAc.class.getName()));
        assertThat(requireNonNull(intents.get(1).getComponent()).getClassName(), is(SeeUserComuByUserAc.class.getName()));
    }
}