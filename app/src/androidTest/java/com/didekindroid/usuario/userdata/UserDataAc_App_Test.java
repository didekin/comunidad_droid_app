package com.didekindroid.usuario.userdata;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.usuario.Usuario;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.R.id.see_usercomu_by_user_frg;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.usuario.testutil.UserItemMenuTestUtils.PASSWORD_CHANGE_AC;
import static com.didekindroid.usuario.testutil.UserItemMenuTestUtils.USER_DATA_AC;
import static com.didekindroid.comunidad.testutil.ComuMenuTestUtil.COMU_SEARCH_AC;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_SEE_OPEN_BY_COMU_AC;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.SEE_USERCOMU_BY_USER_AC;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 20:02
 */
@RunWith(AndroidJUnit4.class)
public class UserDataAc_App_Test extends UserDataAcTest {

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(4000);
    }

    @Override
    public Usuario registerUser() throws Exception
    {
        return signUpAndUpdateTk(COMU_REAL_JUAN);
    }

    @Override
    public ActivityTestRule<? extends Activity> getActivityRule()
    {
        return new ActivityTestRule<UserDataAppAc>(UserDataAppAc.class) {
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
        checkUp(activityLayoutId);
    }

    @Override
    public int getNextViewResourceId()
    {
        return see_usercomu_by_user_frg;
    }

    //    =================================  MENU TESTS ==================================

    @Test
    public void testComuSearchMn() throws InterruptedException
    {
        COMU_SEARCH_AC.checkMenuItem_WTk(mActivity);
        // NO navigate-up.
    }

    @Test
    public void testDeleteMeMn() throws InterruptedException
    {
        USER_DATA_AC.checkMenuItem_WTk(mActivity);
        checkUp(activityLayoutId);
    }

    @Test
    public void testPasswordChangeMn() throws InterruptedException
    {
        PASSWORD_CHANGE_AC.checkMenuItem_WTk(mActivity);
        checkUp(activityLayoutId);
    }

    @Test
    public void testUserComuByUserMn() throws InterruptedException
    {
        SEE_USERCOMU_BY_USER_AC.checkMenuItem_WTk(mActivity);
        checkUp(activityLayoutId);
    }

    @Test
    public void testIncidSeeByComuMn() throws InterruptedException
    {
        INCID_SEE_OPEN_BY_COMU_AC.checkMenuItem_WTk(mActivity);
        checkUp(activityLayoutId);
    }
}
