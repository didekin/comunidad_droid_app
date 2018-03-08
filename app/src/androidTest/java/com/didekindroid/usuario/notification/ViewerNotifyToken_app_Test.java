package com.didekindroid.usuario.notification;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.security.IdentityCacherIf;
import com.didekindroid.lib_one.usuario.notification.ViewerNotifyToken;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOneUser;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real_rodrigo;
import static com.didekindroid.lib_one.usuario.UserTestData.regUserComuWithTkCache;
import static com.didekindroid.lib_one.usuario.UserTestData.user_crodrigo;
import static com.didekindroid.lib_one.usuario.notification.ViewerNotifyToken.newViewerFirebaseToken;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 30/05/17
 * Time: 20:18
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class ViewerNotifyToken_app_Test {

    @Rule
    public ActivityTestRule<? extends Activity> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);
    private ViewerNotifyToken viewer;
    private IdentityCacherIf identityCacher;

    @Before
    public void setUp() throws IOException, UiException
    {
        viewer = (ViewerNotifyToken) newViewerFirebaseToken(activityRule.getActivity());
        identityCacher = viewer.getController().getTkCacher();
        identityCacher.updateIsRegistered(false);
    }

    @Test
    public void test_CheckGcmTokenAsync() throws Exception
    {
        regUserComuWithTkCache(comu_real_rodrigo);
        // Precondition
        assertThat(viewer.getController().getTkCacher().isGcmTokenSentServer(), is(false));
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            viewer.checkGcmTokenAsync();
        } finally {
            resetAllSchedulers();
        }
        waitAtMost(4, SECONDS).until(() -> identityCacher.isGcmTokenSentServer());
        cleanOneUser(user_crodrigo);
    }
}