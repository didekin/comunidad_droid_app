package com.didekindroid.usuario.delete;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.usuario.Usuario;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import static com.didekindroid.R.id.comu_search_ac_linearlayout;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 15:31
 */
@RunWith(AndroidJUnit4.class)
public class DeleteMeAppAc_App_Test extends DeleteMeAcTest {

    @Override
    public Usuario registerUser() throws Exception
    {
        return signUpAndUpdateTk(COMU_REAL_PEPE);
    }

    @Override
    public ActivityTestRule<? extends Activity> getActivityRule()
    {
        return new ActivityTestRule<DeleteMeAppAc>(DeleteMeAppAc.class) {
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
        throw new UnsupportedOperationException("NO NAVIGATE-UP in DeleteMeAppAc activity");
    }

    @Override
    public int getNextViewResourceId()
    {
        return comu_search_ac_linearlayout;
    }

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(5000);
    }
}
