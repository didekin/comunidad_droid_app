package com.didekindroid.usuariocomunidad.data;

import android.content.Intent;
import android.support.test.espresso.NoActivityResumedException;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

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
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_REG_AC;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_SEE_CLOSED_BY_COMU_AC;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_SEE_OPEN_BY_COMU_AC;
import static com.didekindroid.testutil.ActivityTestUtils.checkBack;
import static com.didekindroid.testutil.ActivityTestUtils.checkChildInViewer;
import static com.didekindroid.testutil.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpWithTkGetComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.SEE_USERCOMU_BY_COMU_AC;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.userComuDataLayout;
import static com.didekindroid.usuariocomunidad.util.UserComuBundleKey.USERCOMU_LIST_OBJECT;
import static com.didekinlib.model.usuariocomunidad.Rol.PROPIETARIO;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 30/09/15
 * Time: 11:32
 */
@RunWith(AndroidJUnit4.class)
public class UserComuDataAcTest {

    UsuarioComunidad usuarioComunidad;

    @Rule
    public IntentsTestRule<UserComuDataAc> intentRule = new IntentsTestRule<UserComuDataAc>(UserComuDataAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            Comunidad comunidad = null;
            try {
                comunidad = signUpWithTkGetComu(COMU_TRAV_PLAZUELA_PEPE);
            } catch (IOException | UiException e) {
                fail();
            }
            usuarioComunidad = new UsuarioComunidad.UserComuBuilder(comunidad, USER_PEPE).planta("One").roles(PROPIETARIO.function).build();
            Intent intent = new Intent();
            intent.putExtra(USERCOMU_LIST_OBJECT.key, usuarioComunidad);
            return intent;
        }
    };

    private UserComuDataAc activity;
    private boolean toClean;

    @Before
    public void setUp() throws Exception
    {
        activity = intentRule.getActivity();
        toClean = true;
    }

    @After
    public void tearDown() throws Exception
    {
        if (!toClean) {
            return;
        }
        cleanOptions(CLEAN_PEPE);
    }

//  ===========================================================================

    @Test
    public void test_OnCreate() throws Exception
    {
        assertThat(activity.acView, notNullValue());
        assertThat(activity.viewer, notNullValue());
        assertThat(activity.regUserComuFr, notNullValue());
        // Check call to viewer.doViewInViewer().
        assertThat(activity.viewer.userComuIntent, notNullValue());
    }

    @Test
    public void test_SetChildInViewer() throws Exception
    {
        checkChildInViewer(activity);
    }

    @Test
    public void testModifyUserComu_1() throws InterruptedException
    {
        onView(withId(R.id.reg_usercomu_portal_ed)).perform(replaceText("??=portalNew"));
        // Data wrong: pro rol is not compatible with inq.
        onView(withId(R.id.reg_usercomu_checbox_pro)).check(matches(isChecked()));
        onView(withId(R.id.reg_usercomu_checbox_inq)).perform(click()).check(matches(isChecked()));

        onView(withId(R.id.usercomu_data_ac_modif_button)).perform(click());
        checkToastInTest(R.string.error_validation_msg, activity,
                R.string.reg_usercomu_role_rot, R.string.reg_usercomu_portal_rot);
    }

    @Test
    public void testModifyUserComu_2()
    {
        onView(withId(R.id.reg_usercomu_checbox_pro)).check(matches(isChecked()))
                .perform(click()).check(matches(isNotChecked()));
        onView(withId(R.id.reg_usercomu_checbox_inq)).perform(click()).check(matches(isChecked()));

        onView(withId(R.id.usercomu_data_ac_modif_button)).perform(click());
        // Verificación.
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(seeUserComuByUserFrRsId));
        checkUp(userComuDataLayout);
    }

    @Test
    public void testDeleteUserComu_1() throws UiException
    {
        toClean = false;

        onView(withId(R.id.usercomu_data_ac_delete_button)).perform(click());
        waitAtMost(6, SECONDS).until(isResourceIdDisplayed(comuSearchAcLayout));
        // Sale de la aplicación.
        try {
            checkBack(onView(withId(comuSearchAcLayout)));
        } catch (NoActivityResumedException e) {
            assertThat(e, isA(NoActivityResumedException.class));
        }
    }

//    ======================= MENU =========================

    @Test
    public void testSeeUserComuByComuMn() throws InterruptedException
    {
        SEE_USERCOMU_BY_COMU_AC.checkItemRegisterUser(activity);
        intended(hasExtra(COMUNIDAD_ID.key, usuarioComunidad.getComunidad().getC_Id()));
        checkUp(userComuDataLayout);
    }

    @Test
    public void testComuDataMn() throws InterruptedException
    {
        // Only one user associated to the comunidad: the menu shows the item.
        waitAtMost(6, SECONDS).untilTrue(activity.viewer.showComuDataMn);
        COMU_DATA_AC.checkItemRegisterUser(activity);
        intended(hasExtra(COMUNIDAD_ID.key, usuarioComunidad.getComunidad().getC_Id()));
        checkUp(userComuDataLayout);
    }

    @Test
    public void testIncidSeeOpenByComuMn() throws InterruptedException
    {
        INCID_SEE_OPEN_BY_COMU_AC.checkMenuItem(activity);
        onView(withText(R.string.incid_see_by_user_ac_label)).check(matches(isDisplayed()));
        checkUp(userComuDataLayout);
    }

    @Test
    public void testIncidSeeCloseByComuMn() throws InterruptedException
    {
        INCID_SEE_CLOSED_BY_COMU_AC.checkMenuItem(activity);
        onView(withText(R.string.incid_closed_by_user_ac_label)).check(matches(isDisplayed()));
        checkUp(userComuDataLayout);
    }

    @Test
    public void testIncidRegMn() throws InterruptedException
    {
        INCID_REG_AC.checkMenuItem(activity);
        checkUp(userComuDataLayout);
    }
}