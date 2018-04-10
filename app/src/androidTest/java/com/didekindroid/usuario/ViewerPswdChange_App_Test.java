package com.didekindroid.usuario;

import android.app.Activity;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.usuario.PasswordChangeAc;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.didekindroid.lib_one.usuario.UserTestData.USER_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOneUser;
import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtil.isToastInView;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpAndUpdateTk;
import static io.reactivex.Completable.complete;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 25/03/17
 * Time: 13:09
 */
public class ViewerPswdChange_App_Test {

    @Rule
    public ActivityTestRule<? extends Activity> mActivityRule = new ActivityTestRule<PasswordChangeAc>(PasswordChangeAc.class) {

        @Override
        protected Intent getActivityIntent()
        {
            try {
                signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
            } catch (Exception e) {
                fail();
            }
            return new Intent().putExtra(user_name.key, USER_PEPE.getUserName());
        }
    };
    private PasswordChangeAc activity;

    @Before
    public void setUp()
    {
        activity = (PasswordChangeAc) mActivityRule.getActivity();
    }

    @After
    public void clearUp() throws UiException
    {
        cleanOneUser(USER_PEPE);
    }

    //    ============================  TESTS OBSERVERS  ===================================

    @Test
    public void test_PswdChangeCompletableObserver_Complete()
    {
        activity.runOnUiThread(() -> complete().subscribeWith(activity.getViewer().new PswdChangeCompletableObserver()));
        waitAtMost(2, SECONDS).until(isToastInView(com.didekindroid.lib_one.R.string.password_remote_change, activity));
        waitAtMost(2, SECONDS).until(isResourceIdDisplayed(seeUserComuByUserFrRsId));
    }
}