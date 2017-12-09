package com.didekindroid.usuariocomunidad.register;

import android.app.Activity;
import android.os.Build;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.comunidad.spinner.TipoViaValueObj;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.comunidad.ComunidadAutonoma;
import com.didekinlib.model.comunidad.Municipio;
import com.didekinlib.model.comunidad.Provincia;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.app.TaskStackBuilder.create;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.typeComunidadData;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.testutil.ActivityTestUtils.checkChildInViewer;
import static com.didekindroid.testutil.ActivityTestUtils.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.cleanTasks;
import static com.didekindroid.testutil.ActivityTestUtils.focusOnView;
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.usuario.testutil.UserEspressoTestUtil.typeUserDataFull;
import static com.didekindroid.usuario.testutil.UserItemMenuTestUtils.LOGIN_AC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN2;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanWithTkhandler;
import static com.didekindroid.usuariocomunidad.RolUi.INQ;
import static com.didekindroid.usuariocomunidad.RolUi.PRE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.typeUserComuData;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.regComu_User_UserComuAcLayout;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro
 * Date: 07/07/15
 * Time: 10:26
 */
@SuppressWarnings({"unchecked", "ConstantConditions"})
@RunWith(AndroidJUnit4.class)
public class RegComuAndUserAndUserComuAcTest {

    @Rule
    public ActivityTestRule<RegComuAndUserAndUserComuAc> activityRule =
            new ActivityTestRule<RegComuAndUserAndUserComuAc>(RegComuAndUserAndUserComuAc.class, true, true) {
                @Override
                protected void beforeActivityLaunched()
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        create(getTargetContext()).addParentStack(RegComuAndUserAndUserComuAc.class).startActivities();
                    }
                }
            };

    RegComuAndUserAndUserComuAc activity;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        // Precondition:
        assertThat(activity.viewer.getController().isRegisteredUser(), is(false));
    }

    @After
    public void cleanUp()
    {
        cleanWithTkhandler();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cleanTasks(activity);
        }
    }

    //    ================================================================================

    @Test
    public void testRegComuAndUserComuAndUser_NotOk() throws InterruptedException
    {
        typeComunidad();
        focusOnView(activity, R.id.reg_usercomu_portal_ed);
        execCheckRegisterError(activity);
    }

    //    =================================== Life cycle ===================================

    @Test
    public void test_OnCreate() throws Exception
    {
        assertThat(activity, notNullValue());
        assertThat(activity.regComuFr, notNullValue());
        assertThat(activity.regUserComuFr, notNullValue());
        assertThat(activity.regUserFr, notNullValue());
        assertThat(activity.acView, notNullValue());
        assertThat(activity.viewer, isA(ViewerRegComuUserUserComuAc.class));

        onView(withId(regComu_User_UserComuAcLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_comunidad_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usercomu_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_user_frg)).perform(scrollTo()).check(matches(isDisplayed()));

        onView(withId(R.id.appbar)).perform(scrollTo()).check(matches(isDisplayed()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkUp(comuSearchAcLayout);
        }
    }

    @Test
    public void test_OnStop() throws Exception
    {
        checkSubscriptionsOnStop(activity, activity.viewer.getController());
    }

    @Test
    public void test_SetChildInViewer()
    {
        checkChildInViewer(activity);
    }

    /*    =================================== MENU ===================================*/

    @Test
    public void testLoginMn_NoToken() throws InterruptedException, UiException
    {
        // Precondition.
        assertThat(activity.viewer.getController().isRegisteredUser(), is(false));
        // Exec and check.
        LOGIN_AC.checkItemNoRegisterUser(activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Exec and check navigate-up.
            checkUp(comuSearchAcLayout);
        }
    }

    /*    =================================== HELPERS ===================================*/

    private static void typeComunidad() throws InterruptedException
    {
        final ComunidadAutonoma comunidadAutonoma = new ComunidadAutonoma((short) 10, "Valencia");
        final Provincia provincia = new Provincia(comunidadAutonoma, (short) 12, "Castellón/Castelló");
        final Municipio municipio = new Municipio((short) 53, "Chilches/Xilxes", provincia);
        final TipoViaValueObj tipoVia = new TipoViaValueObj(54, "Callejon");
        typeComunidadData(municipio, tipoVia, "nombre via One", "123", "Tris");
    }

    static void execCheckRegisterError(Activity activity)
    {
        typeUserComuData("WRONG**", "escale_b", "planta-N", "puerta5", PRE, INQ);
        typeUserDataFull(USER_JUAN2.getUserName(), USER_JUAN2.getAlias());
        // Exec.
        onView(withId(R.id.reg_user_plus_button)).perform(scrollTo(), click());
        // Check
        waitAtMost(6, SECONDS).until(isToastInView(R.string.error_validation_msg, activity,
                R.string.reg_usercomu_portal_rot));
    }
}