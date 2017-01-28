package com.didekindroid.usuariocomunidad;

import android.content.Intent;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.comunidad.ComuBundleKey;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.testutil.UsuarioDataTestUtils;
import com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.comunidad.testutil.ComuMenuTestUtil.COMU_DATA_AC;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_REG_AC;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_SEE_OPEN_BY_COMU_AC;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.UserComuBundleKey.USERCOMU_LIST_OBJECT;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.SEE_USERCOMU_BY_COMU_AC;
import static com.didekinlib.model.usuariocomunidad.Rol.ADMINISTRADOR;
import static com.didekinlib.model.usuariocomunidad.Rol.INQUILINO;
import static com.didekinlib.model.usuariocomunidad.Rol.PRESIDENTE;
import static com.didekinlib.model.usuariocomunidad.Rol.PROPIETARIO;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 30/09/15
 * Time: 11:32
 */
@RunWith(AndroidJUnit4.class)
public class UserComuDataAc_1_Test {

    UsuarioComunidad mUsuarioComunidad;
    @Rule
    public IntentsTestRule<UserComuDataAc> intentRule = new IntentsTestRule<UserComuDataAc>(UserComuDataAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
        }

        @Override
        protected Intent getActivityIntent()
        {
            try {
                UserComuDataTestUtil.regTwoUserComuSameUser(UserComuDataTestUtil.makeListTwoUserComu());
            } catch (UiException | IOException e) {
                e.printStackTrace();
            }
            List<UsuarioComunidad> comunidadesUserOne = null;
            try {
                comunidadesUserOne = userComuDaoRemote.seeUserComusByUser();
            } catch (UiException e) {
            }
            mUsuarioComunidad = comunidadesUserOne != null ? comunidadesUserOne.get(0) : null;

            // We use that comunidad as the one to associate to the present user.
            Intent intent = new Intent();
            intent.putExtra(USERCOMU_LIST_OBJECT.key, mUsuarioComunidad);
            return intent;
        }
    };
    UsuarioDataTestUtils.CleanUserEnum whatToClean = CLEAN_JUAN;
    int activityLayoutId = R.id.usercomu_data_ac_layout;
    private UserComuDataAc mActivity;

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(5000);
    }

    @Before
    public void setUp() throws Exception
    {
        mActivity = intentRule.getActivity();
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(whatToClean);
    }

//  ===========================================================================

    @Test
    public void testOnCreate_1() throws Exception
    {
        UsuarioComunidad usuarioComunidad = (UsuarioComunidad) mActivity.getIntent().getSerializableExtra(USERCOMU_LIST_OBJECT.key);
        assertThat(usuarioComunidad, is(mUsuarioComunidad));
        assertThat(mActivity.mRegUserComuFr, notNullValue());

        onView(withId(activityLayoutId)).check(matches(isDisplayed()));

        onView(ViewMatchers.withId(R.id.reg_usercomu_portal_ed)).check(matches(withText(containsString(mUsuarioComunidad.getPortal()))))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.reg_usercomu_escalera_ed)).check(matches(withText(containsString(mUsuarioComunidad.getEscalera()))))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.reg_usercomu_planta_ed)).check(matches(withText(containsString(mUsuarioComunidad.getPlanta()))))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.reg_usercomu_puerta_ed)).check(matches(withText(containsString(mUsuarioComunidad.getPuerta()))))
                .check(matches(isDisplayed()));

        ViewInteraction viewInteraction_1 =
                onView(ViewMatchers.withId(R.id.reg_usercomu_checbox_pre)).check(matches(isDisplayed()));
        ViewInteraction viewInteraction_2 =
                onView(ViewMatchers.withId(R.id.reg_usercomu_checbox_pro)).check(matches(isDisplayed()));
        ViewInteraction viewInteraction_3 =
                onView(ViewMatchers.withId(R.id.reg_usercomu_checbox_admin)).check(matches(isDisplayed()));
        ViewInteraction viewInteraction_4 =
                onView(ViewMatchers.withId(R.id.reg_usercomu_checbox_inq)).check(matches(isDisplayed()));

        if (mUsuarioComunidad.getRoles().contains(PRESIDENTE.function)) {
            viewInteraction_1.check(matches(isChecked()));
        }
        if (mUsuarioComunidad.getRoles().contains(PROPIETARIO.function)) {
            viewInteraction_2.check(matches(isChecked()));
        }
        if (mUsuarioComunidad.getRoles().contains(ADMINISTRADOR.function)) {
            viewInteraction_3.check(matches(isChecked()));
        }
        if (mUsuarioComunidad.getRoles().contains(INQUILINO.function)) {
            viewInteraction_4.check(matches(isChecked()));
        }

        onView(ViewMatchers.withId(R.id.usercomu_data_ac_modif_button)).check(matches(ViewMatchers.withText(R.string.user_data_modif_button_rot)))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.usercomu_data_ac_delete_button)).check(matches(ViewMatchers.withText(R.string.usercomu_data_ac_delete_button_txt)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testModifyUserComu_1() throws InterruptedException
    {
        onView(ViewMatchers.withId(R.id.reg_usercomu_portal_ed)).perform(replaceText("??=portalNew"));
        // Data wrong: pro rol is not compatible with inq.
        onView(ViewMatchers.withId(R.id.reg_usercomu_checbox_pro)).check(matches(isChecked()));
        onView(ViewMatchers.withId(R.id.reg_usercomu_checbox_inq)).check(matches(isNotChecked()))
                .perform(click()).check(matches(isChecked()));

        onView(ViewMatchers.withId(R.id.usercomu_data_ac_modif_button)).perform(click());
        checkToastInTest(R.string.error_validation_msg, mActivity,
                R.string.reg_usercomu_role_rot, R.string.reg_usercomu_portal_rot);
        Thread.sleep(2000);
    }

    @Test
    public void testModifyUserComu_2()
    {
        onView(ViewMatchers.withId(R.id.reg_usercomu_checbox_pro)).check(matches(isChecked()))
                .perform(click()).check(matches(isNotChecked()));
        onView(ViewMatchers.withId(R.id.reg_usercomu_checbox_inq)).perform(click()).check(matches(isChecked()));

        onView(ViewMatchers.withId(R.id.usercomu_data_ac_modif_button)).perform(click());
        // Verificación.
        onView(ViewMatchers.withId(R.id.see_usercomu_by_user_frg)).check(matches(isDisplayed()));
        checkUp(activityLayoutId);
    }

    @Test
    public void testDeleteUserComu_1() throws UiException
    {
        onView(ViewMatchers.withId(R.id.usercomu_data_ac_delete_button)).perform(click());
        onView(ViewMatchers.withId(R.id.see_usercomu_by_user_frg)).check(matches(isDisplayed()));
        checkUp(R.id.comu_search_ac_coordinatorlayout); // Falla en emulador 4.4.2

        assertThat(TKhandler.getAccessTokenInCache(), notNullValue());
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(true));
        assertThat(TKhandler.isRegisteredUser(), is(true));
    }

//    ======================= MENU =========================

    @Test
    public void testSeeUserComuByComuMn() throws InterruptedException
    {
        SEE_USERCOMU_BY_COMU_AC.checkMenuItem_WTk(mActivity);
        intended(IntentMatchers.hasExtra(ComuBundleKey.COMUNIDAD_ID.key, mUsuarioComunidad.getComunidad().getC_Id()));
        checkUp(activityLayoutId);
    }

    @Test
    public void testComuDataMn() throws InterruptedException
    {
        // Only one user associated to the comunidad: the menu shows the item.
        COMU_DATA_AC.checkMenuItem_WTk(mActivity);
        intended(IntentMatchers.hasExtra(ComuBundleKey.COMUNIDAD_ID.key, mUsuarioComunidad.getComunidad().getC_Id()));
        checkUp(activityLayoutId);
    }

    @Test
    public void testIncidSeeByComuMn() throws InterruptedException
    {
        INCID_SEE_OPEN_BY_COMU_AC.checkMenuItem_WTk(mActivity);
        checkUp(activityLayoutId);
    }

    @Test
    public void testIncidRegMn() throws InterruptedException
    {
        INCID_REG_AC.checkMenuItem_WTk(mActivity);
        checkUp(activityLayoutId);
    }
}