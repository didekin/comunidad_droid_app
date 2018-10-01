package com.didekindroid.usuariocomunidad.listbyuser;

import com.didekindroid.DidekinApp;
import com.didekindroid.lib_one.api.SingleObserverMock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static android.app.Instrumentation.newApplication;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.execCheckSchedulersTest;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOneUser;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real_rodrigo;
import static com.didekindroid.lib_one.usuario.UserTestData.regComuUserUserComuGetAuthTk;
import static com.didekindroid.lib_one.usuario.UserTestData.user_crodrigo;

public class CtrlerSeeUserComuByUserTest {

    private CtrlerSeeUserComuByUser controller;

    @Before
    public void setUp() throws Exception
    {
        getInstrumentation().callApplicationOnCreate(newApplication(DidekinApp.class, getTargetContext()));
        controller = new CtrlerSeeUserComuByUser();
    }

    @After
    public void clearUp()
    {
        controller.clearSubscriptions();
        resetAllSchedulers();
        cleanOneUser(user_crodrigo.getUserName());
    }

    @Test
    public void test_LoadItemsByEntitiyId() throws Exception
    {
        regComuUserUserComuGetAuthTk(comu_real_rodrigo);
        execCheckSchedulersTest(ctrler -> ctrler.loadItemsByEntitiyId(new SingleObserverMock<>()), controller);
    }
}