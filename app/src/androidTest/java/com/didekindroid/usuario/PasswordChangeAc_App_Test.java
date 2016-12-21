package com.didekindroid.usuario;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekinaar.usuario.password.PasswordChangeAcTest;
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
    public boolean registerUser() throws Exception
    {
        return signUpAndUpdateTk(UserComuTestUtil.COMU_TRAV_PLAZUELA_PEPE) != null;
    }

    @Override
    public ActivityTestRule<? extends Activity> getActivityRule()
    {
        return new ActivityTestRule<PasswordChangeAppAc>(PasswordChangeAppAc.class) {
            @Override
            protected void beforeActivityLaunched()
            {
                // Precondition: the user is registered.
                try {
                    registerUser();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void checkNavigateUp()
    {
        throw new UnsupportedOperationException("NO NAVIGATE-UP in PasswordChange activity");
    }
}
