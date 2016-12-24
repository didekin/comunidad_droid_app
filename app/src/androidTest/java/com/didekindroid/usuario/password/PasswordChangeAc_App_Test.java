package com.didekindroid.usuario.password;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.usuario.Usuario;
import com.didekinaar.usuario.password.PasswordChangeAcTest;
import com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import static com.didekinaar.R.id.user_data_ac_layout;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.signUpAndUpdateTk;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

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
    public Usuario registerUser() throws Exception
    {
        return signUpAndUpdateTk(UserComuTestUtil.COMU_TRAV_PLAZUELA_PEPE);
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
                    registeredUser = registerUser();
                    assertThat(registeredUser, notNullValue());
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

    @Override
    public int getNextViewResourceId()
    {
        return user_data_ac_layout;
    }
}
