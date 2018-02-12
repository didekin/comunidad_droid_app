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

import static com.didekindroid.testutil.ActivityTestUtils.checkSpinnerCtrlerLoadItems;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;

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