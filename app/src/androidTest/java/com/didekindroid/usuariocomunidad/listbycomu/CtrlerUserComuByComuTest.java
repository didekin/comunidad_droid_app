package com.didekindroid.usuariocomunidad.listbycomu;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.DidekinApp;
import com.didekindroid.lib_one.api.SingleObserverMock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.app.Instrumentation.newApplication;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.execCheckSchedulersTest;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOneUser;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpGetComu;

/**
 * User: pedro@didekin
 * Date: 26/03/17
 * Time: 14:53
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerUserComuByComuTest {

    private CtrlerUserComuByComuList controller;

    @Before
    public void setUp() throws Exception
    {
        getInstrumentation().callApplicationOnCreate(newApplication(DidekinApp.class, getTargetContext()));
        controller = new CtrlerUserComuByComuList();
    }

    @After
    public void clearUp()
    {
        controller.clearSubscriptions();
        resetAllSchedulers();
        cleanOneUser(USER_JUAN.getUserName());
    }

    // .................................... INSTANCE METHODS .................................

    @Test
    public void testLoadItemsByEntitiyId() throws Exception
    {
        execCheckSchedulersTest(
                ctrler -> ctrler.loadItemsByEntitiyId(new SingleObserverMock<>(), signUpGetComu(COMU_REAL_JUAN).getC_Id()),
                controller
        );
    }

    @Test
    public void testComunidadData() throws Exception
    {
        execCheckSchedulersTest(
                ctrler -> ctrler.comunidadData(new SingleObserverMock<>(), signUpGetComu(COMU_REAL_JUAN).getC_Id()),
                controller
        );
    }
}
