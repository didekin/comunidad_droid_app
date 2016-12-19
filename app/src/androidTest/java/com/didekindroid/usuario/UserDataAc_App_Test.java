package com.didekindroid.usuario;

import android.app.Activity;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekinaar.usuario.UserDataAcTest;
import com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static com.didekinaar.testutil.AarActivityTestUtils.checkUp;
import static com.didekinaar.usuario.testutil.UserItemMenuTestUtils.DELETE_ME_AC;
import static com.didekinaar.usuario.testutil.UserItemMenuTestUtils.PASSWORD_CHANGE_AC;
import static com.didekinaar.usuario.testutil.UsuarioTestUtils.USER_JUAN;
import static com.didekindroid.comunidad.testutil.ComuMenuTestUtil.COMU_SEARCH_AC;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_SEE_OPEN_BY_COMU_AC;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.SEE_USERCOMU_BY_USER_AC;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.signUpAndUpdateTk;

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
    public boolean registerUser() throws Exception
    {
        return signUpAndUpdateTk(UserComuTestUtil.COMU_REAL_JUAN) != null;
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
        // Verificamos navegación.
        onView(ViewMatchers.withId(com.didekinaar.R.id.see_usercomu_by_user_frg)).check(matches(isDisplayed()));
        checkUp(activityLayoutId);
    }

    //    =====================================  TESTS  ==========================================

    /* Alias y userName sin cambios. */
    @Test
    public void testModifyUserData_1() throws InterruptedException
    {
        onView(ViewMatchers.withId(com.didekinaar.R.id.user_data_ac_password_ediT))
                .perform(typeText(USER_JUAN.getPassword()), closeSoftKeyboard());
        onView(ViewMatchers.withId(com.didekinaar.R.id.user_data_modif_button)).perform(scrollTo())
                .check(matches(isDisplayed())).perform(click());
        onView(ViewMatchers.withId(com.didekinaar.R.id.see_usercomu_by_user_frg)).check(matches(isDisplayed()));
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
        DELETE_ME_AC.checkMenuItem_WTk(mActivity);
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
