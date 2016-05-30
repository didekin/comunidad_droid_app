package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.usuario.testutils.CleanUserEnum;

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
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekin.common.oauth2.Rol.ADMINISTRADOR;
import static com.didekin.common.oauth2.Rol.INQUILINO;
import static com.didekin.common.oauth2.Rol.PRESIDENTE;
import static com.didekin.common.oauth2.Rol.PROPIETARIO;
import static com.didekindroid.common.TokenHandler.TKhandler;
import static com.didekindroid.common.activity.BundleKey.COMUNIDAD_ID;
import static com.didekindroid.common.activity.BundleKey.USERCOMU_LIST_OBJECT;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_REG_AC;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_SEE_OPEN_BY_COMU_AC;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_NOTHING;
import static com.didekindroid.usuario.testutils.UserMenuTestUtils.COMU_DATA_AC;
import static com.didekindroid.usuario.testutils.UserMenuTestUtils.SEE_USERCOMU_BY_COMU_AC;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_REAL_JUAN;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 30/09/15
 * Time: 11:32
 */
@RunWith(AndroidJUnit4.class)
public class UserComuDataAcTest_1 {

    private UserComuDataAc mActivity;
    private UsuarioComunidad mUsuarioComunidad;
    CleanUserEnum whatToClean = CLEAN_JUAN;

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
                signUpAndUpdateTk(COMU_REAL_JUAN);
            } catch (UiException | IOException e) {
                e.printStackTrace();
            }
            List<UsuarioComunidad> comunidadesUserOne = null;
            try {
                comunidadesUserOne = ServOne.seeUserComusByUser();
            } catch (UiException e) {
            }
            mUsuarioComunidad = comunidadesUserOne != null ? comunidadesUserOne.get(0) : null;

            // We use that comunidad as the one to associate to the present user.
            Intent intent = new Intent();
            intent.putExtra(USERCOMU_LIST_OBJECT.key, mUsuarioComunidad);
            return intent;
        }
    };

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
        onView(withId(R.id.reg_usercomu_portal_ed)).check(matches(withText(containsString(mUsuarioComunidad.getPortal()))))
                .check(matches(isDisplayed()));
        onView(withId(R.id.reg_usercomu_escalera_ed)).check(matches(withText(containsString(mUsuarioComunidad.getEscalera()))))
                .check(matches(isDisplayed()));
        onView(withId(R.id.reg_usercomu_planta_ed)).check(matches(withText(containsString(mUsuarioComunidad.getPlanta()))))
                .check(matches(isDisplayed()));
        onView(withId(R.id.reg_usercomu_puerta_ed)).check(matches(withText(containsString(mUsuarioComunidad.getPuerta()))))
                .check(matches(isDisplayed()));

        ViewInteraction viewInteraction_1 =
                onView(withId(R.id.reg_usercomu_checbox_pre)).check(matches(isDisplayed()));
        ViewInteraction viewInteraction_2 =
                onView(withId(R.id.reg_usercomu_checbox_pro)).check(matches(isDisplayed()));
        ViewInteraction viewInteraction_3 =
                onView(withId(R.id.reg_usercomu_checbox_admin)).check(matches(isDisplayed()));
        ViewInteraction viewInteraction_4 =
                onView(withId(R.id.reg_usercomu_checbox_inq)).check(matches(isDisplayed()));

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

        onView(withId(R.id.usercomu_data_ac_modif_button)).check(matches(withText(R.string.user_data_modif_button_txt)))
                .check(matches(isDisplayed()));
        onView(withId(R.id.usercomu_data_ac_delete_button)).check(matches(withText(R.string.usercomu_data_ac_delete_button_txt)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testOnCreate_2()
    {
        UsuarioComunidad usuarioComunidad = (UsuarioComunidad) mActivity.getIntent().getSerializableExtra(USERCOMU_LIST_OBJECT.key);
        assertThat(usuarioComunidad, notNullValue());
        assertThat(usuarioComunidad.getComunidad(),is(mUsuarioComunidad.getComunidad()));
    }

    @Test
    public void testSeeUserComuByComuMn_withToken() throws InterruptedException
    {
        SEE_USERCOMU_BY_COMU_AC.checkMenuItem_WTk(mActivity);
        intended(hasExtra(COMUNIDAD_ID.key, mUsuarioComunidad.getComunidad().getC_Id()));
    }

    @Test
    public void testComuDataMn_withToken_1() throws InterruptedException
    {
        // Only one user associated to the comunidad: the menu shows the item.
        COMU_DATA_AC.checkMenuItem_WTk(mActivity);
        intended(hasExtra(COMUNIDAD_ID.key, mUsuarioComunidad.getComunidad().getC_Id()));
    }

    @Test
    public void testIncidSeeByComu_withToken_1() throws InterruptedException
    {
        INCID_SEE_OPEN_BY_COMU_AC.checkMenuItem_WTk(mActivity);
    }

    @Test
    public void testIncidRegMn_withToken_1() throws InterruptedException
    {
        INCID_REG_AC.checkMenuItem_WTk(mActivity);
    }

    @Test
    public void testModifyUserComu_1() throws InterruptedException
    {
        onView(withId(R.id.reg_usercomu_portal_ed)).perform(replaceText("??=portalNew"));
        // Data wrong: pro rol is not compatible with inq.
        onView(withId(R.id.reg_usercomu_checbox_pro)).check(matches(isChecked()));
        onView(withId(R.id.reg_usercomu_checbox_inq)).check(matches(isNotChecked()))
                .perform(click()).check(matches(isChecked()));

        onView(withId(R.id.usercomu_data_ac_modif_button)).perform(click());
        checkToastInTest(R.string.error_validation_msg, mActivity,
                R.string.reg_usercomu_role_rot, R.string.reg_usercomu_portal_hint);

        Thread.sleep(2000);
    }

    @Test
    public void testModifyUserComu_2()
    {
        onView(withId(R.id.reg_usercomu_checbox_pro)).check(matches(isChecked()))
                .perform(click()).check(matches(isNotChecked()));
        onView(withId(R.id.reg_usercomu_checbox_inq)).perform(click()).check(matches(isChecked()));

        onView(withId(R.id.usercomu_data_ac_modif_button)).perform(click());
        onView(withId(R.id.see_usercomu_by_user_ac_frg_container)).check(matches(isDisplayed()));
    }

    @Test
    public void testDeleteUserComu_1() throws UiException
    {
        whatToClean = CLEAN_NOTHING;

        onView(withId(R.id.usercomu_data_ac_delete_button)).perform(click());
        onView(withId(R.id.comu_search_ac_layout)).check(matches(isDisplayed()));
        assertThat(TKhandler.getAccessTokenInCache(), nullValue());
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(false));
        assertThat(isRegisteredUser(mActivity), is(false));
    }
}