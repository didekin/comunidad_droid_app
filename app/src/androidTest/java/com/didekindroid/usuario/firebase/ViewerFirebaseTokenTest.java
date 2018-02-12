package com.didekindroid.usuario.firebase;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AppCompatActivity;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.http.exception.ErrorBean;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import io.reactivex.Single;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.firebase.ViewerFirebaseToken.newViewerFirebaseToken;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.USER_DATA_NOT_INSERTED;
import static io.reactivex.Single.just;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 30/05/17
 * Time: 20:18
 */
@RunWith(AndroidJUnit4.class)
public class ViewerFirebaseTokenTest {

    @Rule
    public ActivityTestRule<? extends Activity> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);
    ViewerFirebaseToken viewer;
    AppCompatActivity activity;

    @Before
    public void setUp() throws IOException, UiException
    {
        activity = (AppCompatActivity) activityRule.getActivity();
        viewer = (ViewerFirebaseToken) newViewerFirebaseToken(activity);
        viewer.getController().getIdentityCacher().updateIsRegistered(false);
    }

    @Test
    public void test_NewViewerFirebaseToken() throws Exception
    {
        assertThat(CtrlerFirebaseToken.class.isInstance(viewer.getController()), is(true));
    }

    @Test
    public void test_CheckGcmTokenAsync() throws Exception
    {
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        // Precondition
        assertThat(viewer.getController().isGcmTokenSentServer(), is(false));
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            viewer.checkGcmTokenAsync();
        } finally {
            resetAllSchedulers();
        }
        assertThat(viewer.getController().isGcmTokenSentServer(), is(true));
        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void test_RegGcmTokenObserver_Success() throws Exception
    {
        // Preconditions.
        viewer.getController().updateIsRegistered(true);
        assertThat(viewer.getController().isGcmTokenSentServer(), is(false));

        just(11).subscribeWith(viewer.new RegGcmTokenObserver());
        assertThat(viewer.getController().isGcmTokenSentServer(), is(true));
    }

    @Test
    public void test_RegGcmTokenObserver_Error() throws Exception
    {
        // Preconditions.
        viewer.getController().updateIsRegistered(true);
        assertThat(viewer.getController().isGcmTokenSentServer(), is(false));

        activity.runOnUiThread(
                () -> Single.<Integer>error(
                        new UiException(new ErrorBean(USER_DATA_NOT_INSERTED))
                ).subscribeWith(viewer.new RegGcmTokenObserver()));

        assertThat(viewer.getController().isGcmTokenSentServer(), is(false));
        onView(withId(R.id.login_ac_layout)).check(matches(isDisplayed()));
    }
}