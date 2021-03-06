package com.didekindroid.usuariocomunidad.data;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.comunidad.testutil.ComuMenuTestUtil.COMU_DATA_AC;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_REG_AC;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_SEE_CLOSED_BY_COMU_AC;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_SEE_OPEN_BY_COMU_AC;
import static com.didekindroid.lib_one.testutil.UiTestUtil.checkChildInViewer;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.testutil.ActivityTestUtil.checkToastInTest;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.usuariocomunidad.UserComuBundleKey.USERCOMU_LIST_OBJECT;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.SEE_USERCOMU_BY_COMU_AC;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.userComuDataLayout;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpGetComu;
import static com.didekinlib.model.usuariocomunidad.Rol.PROPIETARIO;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 30/09/15
 * Time: 11:32
 */
@RunWith(AndroidJUnit4.class)
public class UserComuDataAcTest {

    private static UsuarioComunidad usuarioComunidad;
    private UserComuDataAc activity;

    @Rule
    public IntentsTestRule<UserComuDataAc> intentRule = new IntentsTestRule<UserComuDataAc>(UserComuDataAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            return new Intent().putExtra(USERCOMU_LIST_OBJECT.key, usuarioComunidad);
        }
    };

    @BeforeClass
    public static void setUpStatic() throws Exception
    {
        usuarioComunidad = new UsuarioComunidad.UserComuBuilder(signUpGetComu(COMU_TRAV_PLAZUELA_PEPE), USER_PEPE)
                .planta("One")
                .roles(PROPIETARIO.function)
                .build();
    }

    @Before
    public void setUp() throws Exception
    {
        activity = intentRule.getActivity();
    }

    @AfterClass
    public static void tearDown()
    {
        cleanOptions(CLEAN_PEPE);
    }

//  ===========================================================================

    @Test
    public void testModifyUserComu_1()
    {
        // test_OnCreate
        assertThat(activity.acView, notNullValue());
        assertThat(activity.viewer, notNullValue());
        assertThat(activity.regUserComuFr, notNullValue());
        // Check call to viewer.doViewInViewer().
        assertThat(activity.viewer.userComuIntent, notNullValue());
        // test_SetChildInViewer
        checkChildInViewer(activity);

        // Error.
        onView(withId(R.id.reg_usercomu_portal_ed)).perform(replaceText("??=portalNew"));
        // Data wrong: pro rol is not compatible with inq.
        onView(withId(R.id.reg_usercomu_checbox_pro)).check(matches(isChecked()));
        onView(withId(R.id.reg_usercomu_checbox_inq)).perform(click()).check(matches(isChecked()));

        onView(withId(R.id.usercomu_data_ac_modif_button)).perform(click());
        checkToastInTest(R.string.error_validation_msg, activity,
                R.string.reg_usercomu_role_rot, R.string.reg_usercomu_portal_rot);

        // OK.
        onView(withId(R.id.reg_usercomu_checbox_pro)).check(matches(isChecked()))
                .perform(click()).check(matches(isNotChecked()));
        onView(withId(R.id.reg_usercomu_portal_ed)).perform(replaceText("portalOK"));

        onView(withId(R.id.usercomu_data_ac_modif_button)).perform(click());
        // Verificación.
        waitAtMost(6, SECONDS).until(isResourceIdDisplayed(seeUserComuByUserFrRsId));
        checkUp(userComuDataLayout);
    }

//    ======================= MENU =========================

    @Test
    public void testSeeUserComuByComuMn()
    {
        SEE_USERCOMU_BY_COMU_AC.checkItem(activity);
        intended(hasExtra(COMUNIDAD_ID.key, usuarioComunidad.getComunidad().getC_Id()));
        checkUp(userComuDataLayout);
    }

    @Test
    public void testComuDataMn()
    {
        // Only one user associated to the comunidad: the menu shows the item.
        waitAtMost(8, SECONDS).untilTrue(activity.viewer.showMnOldestAdmonUser);
        COMU_DATA_AC.checkItem(activity);
        intended(hasExtra(COMUNIDAD_ID.key, usuarioComunidad.getComunidad().getC_Id()));
        checkUp(userComuDataLayout);
    }

    @Test
    public void testIncidSeeOpenByComuMn()
    {
        INCID_SEE_OPEN_BY_COMU_AC.checkItem(activity);
        onView(withText(R.string.incid_see_by_user_ac_label)).check(matches(isDisplayed()));
        checkUp(userComuDataLayout);
    }

    @Test
    public void testIncidSeeCloseByComuMn()
    {
        INCID_SEE_CLOSED_BY_COMU_AC.checkItem(activity);
        onView(withText(R.string.incid_closed_by_user_ac_label)).check(matches(isDisplayed()));
        checkUp(userComuDataLayout);
    }

    @Test
    public void testIncidRegMn()
    {
        INCID_REG_AC.checkItem(activity);
        checkUp(userComuDataLayout);
    }
}