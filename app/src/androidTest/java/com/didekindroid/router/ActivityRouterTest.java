package com.didekindroid.router;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.accesorio.ConfidencialidadAc;
import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.ActivityNextMock;
import com.didekindroid.comunidad.ComuDataAc;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.list.IncidSeeByComuAc;
import com.didekindroid.usuariocomunidad.listbyuser.SeeUserComuByUserAc;
import com.didekindroid.usuariocomunidad.register.RegComuAndUserAndUserComuAc;
import com.didekindroid.usuariocomunidad.register.RegComuAndUserComuAc;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.support.test.runner.lifecycle.Stage.RESUMED;
import static com.didekindroid.router.ActivityRouter.IntrospectRouterToAc.defaultNoRegUser;
import static com.didekindroid.router.ActivityRouter.IntrospectRouterToAc.defaultRegUser;
import static com.didekindroid.router.ActivityRouter.acRouter;
import static com.didekindroid.router.ActivityRouter.acRouterMap;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.getActivitesInTaskByStage;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_TK_HANDLER;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 02/05/17
 * Time: 16:39
 */
@RunWith(AndroidJUnit4.class)
public class ActivityRouterTest {

    @Rule
    public IntentsTestRule<ActivityMock> activityRule = new IntentsTestRule<ActivityMock>(ActivityMock.class) {
        @Override
        protected Intent getActivityIntent()
        {
            Intent intent = new Intent();
            intent.putExtra("keyTest_2", "Value_keyTest_2");
            return intent;
        }
    };

    @After
    public void cleanFileToken() throws UiException
    {
        cleanOptions(CLEAN_TK_HANDLER);
    }

    @Test
    public void test_NextActivityFromMn() throws Exception
    {
        // No registered user.
        assertThat(TKhandler.isRegisteredUser(), is(false));
        assertThat(acRouter.nextActivityFromMn(-1).equals(defaultNoRegUser.activityToGo), is(true));
        assertThat(acRouter.nextActivityFromMn(R.id.reg_nueva_comunidad_ac_mn).equals(RegComuAndUserAndUserComuAc.class), is(true));
        assertThat(acRouter.nextActivityFromMn(R.id.confidencialidad_ac_mn).equals(ConfidencialidadAc.class), is(true));

        // Registered user.
        TKhandler.updateIsRegistered(true);
        assertThat(TKhandler.isRegisteredUser(), is(true));
        assertThat(acRouter.nextActivityFromMn(-1).equals(defaultRegUser.activityToGo), is(true));
        assertThat(acRouter.nextActivityFromMn(R.id.reg_nueva_comunidad_ac_mn).equals(RegComuAndUserComuAc.class), is(true));
    }

    @Test
    public void test_NextActivity() throws Exception
    {
        assertThat(acRouter.nextActivity(ComuDataAc.class).equals(SeeUserComuByUserAc.class), is(true));
        // Precodition no mappping for an activity.
        assertThat(acRouterMap.get(IncidSeeByComuAc.class), nullValue());
        // Case: no registered user.
        assertThat(acRouter.identityCacher.isRegisteredUser(), is(false));
        // Check
        assertThat(acRouter.nextActivity(IncidSeeByComuAc.class).equals(defaultNoRegUser.activityToGo), is(true));
        // Case: registered user.
        acRouter.identityCacher.updateIsRegistered(true);
        // Check
        assertThat(acRouter.nextActivity(IncidSeeByComuAc.class).equals(defaultRegUser.activityToGo), is(true));
    }

    @Test
    public void test_DoUpMenu_1() throws Exception
    {
        ActivityMock activityMock = activityRule.getActivity();
        ActivityManager manager = (ActivityManager) activityMock.getSystemService(ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            manager.getAppTasks().get(0).startActivity(activityMock, new Intent(activityMock, ActivityNextMock.class), new Bundle(0));
            // Calling indirectly the method to test and check new activity layout.
            checkUp(R.id.mock_ac_layout);
            // Check that the up activity is resumed and has the original intent.
            Collection<Activity> activities = getActivitesInTaskByStage(RESUMED);
            assertThat(activities.size(), is(1));
            for (Activity next : activities) {
                assertThat(next.getComponentName().getClassName(), is(ActivityMock.class.getCanonicalName()));
                assertThat(next.getIntent().getStringExtra("keyTest_2"), is("Value_keyTest_2"));
            }
        }
    }
}