package com.didekindroid.usuario.notification;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.usuario.notification.CtrlerNotifyToken;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.didekindroid.lib_one.usuario.UserTestData.cleanOneUser;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real_rodrigo;
import static com.didekindroid.lib_one.usuario.UserTestData.regUserComuWithTkCache;
import static com.didekindroid.lib_one.usuario.UserTestData.user_crodrigo;

/**
 * User: pedro@didekin
 * Date: 06/03/17
 * Time: 17:26
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerNotifyToken_app_Test {

    private CtrlerNotifyToken controller;

    @Before
    public void setUp() throws IOException, UiException
    {
        regUserComuWithTkCache(comu_real_rodrigo);
        controller = new CtrlerNotifyToken();
    }

    @After
    public void cleanUp() throws UiException
    {
        cleanOneUser(user_crodrigo);
    }

    //    ................................ OBSERVABLES/SUBSCRIBERS .................................

    /**
     * Synchronous execution: no scheduler specified, everything runs in the test runner thread.
     */
    @Test
    public void testUpdatedGcmTkSingle() throws Exception
    {
        controller.updatedGcmTkSingle().test().assertResult(1);
    }
}