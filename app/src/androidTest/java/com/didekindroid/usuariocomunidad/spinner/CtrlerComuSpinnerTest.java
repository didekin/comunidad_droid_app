package com.didekindroid.usuariocomunidad.spinner;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.exception.UiException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.didekindroid.lib_one.testutil.UiTestUtil.checkSpinnerCtrlerLoadItems;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOneUser;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpAndUpdateTk;

/**
 * User: pedro@didekin
 * Date: 16/03/17
 * Time: 17:53
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerComuSpinnerTest {

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    private CtrlerComuSpinner controller;

    @Before
    public void setUp()
    {
        controller = new CtrlerComuSpinner();
    }

    @Test
    public void testLoadDataInSpinner() throws IOException, UiException
    {
        signUpAndUpdateTk(COMU_REAL_JUAN);

        checkSpinnerCtrlerLoadItems(controller);

        cleanOneUser(USER_JUAN);
    }
}