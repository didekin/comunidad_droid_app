package com.didekindroid.usuario;

import android.support.test.runner.AndroidJUnit4;

import com.didekinaar.usuario.PasswordChangeAcTest;
import com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.signUpAndUpdateTk;

/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 20:22
 */
@RunWith(AndroidJUnit4.class)
public class PasswordChangeAc_App_Test extends PasswordChangeAcTest {

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(5000);
    }

    @Override
    protected void registerUser() throws Exception
    {
        signUpAndUpdateTk(UserComuTestUtil.COMU_TRAV_PLAZUELA_PEPE);
    }
}
