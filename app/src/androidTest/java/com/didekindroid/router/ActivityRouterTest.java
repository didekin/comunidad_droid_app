package com.didekindroid.router;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.ActivityNextMock;
import com.didekindroid.exception.UiException;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.Context.ACTIVITY_SERVICE;
import static com.didekindroid.router.ActivityRouter.NULL_MENU_ITEM;
import static com.didekindroid.router.ActivityRouter.acByDefaultNoRegUser;
import static com.didekindroid.router.ActivityRouter.acByDefaultRegUser;
import static com.didekindroid.router.ActivityRouter.acRouter;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.checkIntentParentActivity;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_TK_HANDLER;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static org.hamcrest.CoreMatchers.is;
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
        assertThat(acRouter.nextActivityFromMn(NULL_MENU_ITEM).equals(acByDefaultNoRegUser), is(true));

        // Registered user.
        TKhandler.updateIsRegistered(true);
        assertThat(TKhandler.isRegisteredUser(), is(true));
        assertThat(acRouter.nextActivityFromMn(NULL_MENU_ITEM).equals(acByDefaultRegUser), is(true));
    }

    @Test
    public void test_DoUpMenu() throws Exception
    {
        ActivityMock activityMock = activityRule.getActivity();
        ActivityManager manager = (ActivityManager) activityMock.getSystemService(ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            manager.getAppTasks().get(0).startActivity(activityMock, new Intent(activityMock, ActivityNextMock.class), new Bundle(0));
            // Calling indirectly the method to test.
            checkUp(R.id.mock_ac_layout);
            checkIntentParentActivity();
        }
    }
}