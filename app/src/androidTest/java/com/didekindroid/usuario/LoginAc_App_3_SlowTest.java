package com.didekindroid.usuario;

import android.content.Intent;
import android.support.test.runner.AndroidJUnit4;

import com.didekinaar.usuario.LoginAcTest;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.usuariocomunidad.UserComuService.AppUserComuServ;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.COMU_REAL_DROID;

/**
 * User: pedro@didekin
 * Date: 26/10/15
 * Time: 13:51
 */
@RunWith(AndroidJUnit4.class)
public class LoginAc_App_3_SlowTest extends LoginAcTest {

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(5000);
    }

    @Override
    protected boolean registerUser() throws Exception
    {
        return AppUserComuServ.regComuAndUserAndUserComu(COMU_REAL_DROID).execute().body();
    }

    @Test
    public void testValidate_1() throws Exception
    {
        mActivity = mActivityRule.launchActivity(new Intent());
        checkLoginWithThreeErrors();
    }
}
