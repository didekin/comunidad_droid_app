package com.didekindroid.usuario;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.signUpAndUpdateTk;

/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 15:31
 */
@RunWith(AndroidJUnit4.class)
public class DeleteMeAppAc_App_Test extends DeleteMeAcTest {

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(5000);
    }

    @Override
    protected void registerUser() throws Exception
    {
        signUpAndUpdateTk(UserComuTestUtil.COMU_REAL_PEPE);
    }
}
