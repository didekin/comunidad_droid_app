package com.didekindroid.usuario.login;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.usuario.Usuario;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import static com.didekindroid.R.id.comu_search_ac_linearlayout;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_DROID;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;

/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 14:39
 */
@RunWith(AndroidJUnit4.class)
public class LoginAc_App_1_Test extends LoginAcTest {

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(4000);
    }

    @Override
    public Usuario registerUser() throws Exception
    {
        return signUpAndUpdateTk(COMU_REAL_DROID);
    }

    @Override
    public ActivityTestRule<? extends Activity> getActivityRule()
    {
        return new ActivityTestRule<>(LoginAppAc.class, true, false);
    }

    @Override
    public void checkNavigateUp()
    {
        throw new UnsupportedOperationException("NO NAVIGATE-UP in LoginAppAc activity");
    }

    @Override
    public int getNextViewResourceId()
    {
        return comu_search_ac_linearlayout;
    }
}
