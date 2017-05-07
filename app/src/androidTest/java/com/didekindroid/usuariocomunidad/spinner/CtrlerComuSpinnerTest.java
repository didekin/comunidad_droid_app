package com.didekindroid.usuariocomunidad.spinner;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Spinner;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.exception.UiException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.testutil.ActivityTestUtils.checkSpinnerCtrlerLoadItems;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekindroid.usuariocomunidad.spinner.CtrlerComuSpinner.newControllerComuSpinner;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * User: pedro@didekin
 * Date: 16/03/17
 * Time: 17:53
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerComuSpinnerTest {

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    CtrlerComuSpinner controller;

    @Before
    public void setUp() throws IOException, UiException
    {
        final Activity activity = activityRule.getActivity();
        final AtomicReference<CtrlerComuSpinner> atomicController = new AtomicReference<>(null);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                atomicController.compareAndSet(
                        null,
                        newControllerComuSpinner(
                                new ViewerComuSpinner(new Spinner(activity), activity, null))
                );
            }
        });
        waitAtMost(2, SECONDS).untilAtomic(atomicController, notNullValue());
        controller = atomicController.get();
    }

    @Test
    public void testLoadDataInSpinner() throws IOException, UiException
    {
        signUpAndUpdateTk(COMU_REAL_JUAN);

        checkSpinnerCtrlerLoadItems(controller);

        cleanOneUser(USER_JUAN);
    }
}