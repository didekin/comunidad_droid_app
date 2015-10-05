package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.didekin.security.Rol;
import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.usuario.activity.utils.CleanEnum;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.didekindroid.uiutils.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.activity.utils.CleanEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.activity.utils.CleanEnum.CLEAN_NOTHING;
import static com.didekindroid.usuario.activity.utils.RolCheckBox.PRESIDENTE;
import static com.didekindroid.usuario.activity.utils.RolCheckBox.PROPIETARIO;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.COMUNIDAD_ID;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.USERCOMU_LIST_OBJECT;
import static com.didekindroid.usuario.activity.utils.UserMenuTestUtils.COMU_DATA_AC;
import static com.didekindroid.usuario.activity.utils.UserMenuTestUtils.SEE_USERCOMU_BY_COMU_AC;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.*;
import static com.didekindroid.usuario.dominio.DomainDataUtils.*;
import static com.didekindroid.usuario.security.TokenHandler.TKhandler;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static org.hamcrest.Matchers.*;
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
    CleanEnum whatToClean = CLEAN_JUAN;

    @Rule
    public IntentsTestRule<UserComuDataAc> intentRule = new IntentsTestRule<UserComuDataAc>(UserComuDataAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {
        }

        @Override
        protected Intent getActivityIntent()
        {
            signUpAndUpdateTk(COMU_REAL_JUAN);
            List<UsuarioComunidad> comunidadesUserOne = ServOne.getUserComusByUser();
            mUsuarioComunidad = comunidadesUserOne.get(0);

            // We use that comunidad as the one to associate to the present user.
            Intent intent = new Intent();
            intent.putExtra(USERCOMU_LIST_OBJECT.extra, mUsuarioComunidad);
            return intent;
        }
    };

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
    public void testOnCreate() throws Exception
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

        if (mUsuarioComunidad.getRoles().contains(Rol.PRESIDENTE.function)) {
            viewInteraction_1.check(matches(isChecked()));
        }
        if (mUsuarioComunidad.getRoles().contains(Rol.PROPIETARIO.function)) {
            viewInteraction_2.check(matches(isChecked()));
        }
        if (mUsuarioComunidad.getRoles().contains(Rol.ADMINISTRADOR.function)) {
            viewInteraction_3.check(matches(isChecked()));
        }
        if (mUsuarioComunidad.getRoles().contains(Rol.INQUILINO.function)) {
            viewInteraction_4.check(matches(isChecked()));
        }

        onView(withId(R.id.usercomu_data_ac_modif_button)).check(matches(withText(R.string.user_data_modif_button_txt)))
                .check(matches(isDisplayed()));
        onView(withId(R.id.usercomu_data_ac_delete_button)).check(matches(withText(R.string.usercomu_data_ac_delete_button_txt)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testSeeUserComuByComuMn_withToken() throws InterruptedException
    {
        SEE_USERCOMU_BY_COMU_AC.checkMenuItem_WTk(mActivity);
        intended(hasExtra(COMUNIDAD_ID.extra, mUsuarioComunidad.getComunidad().getC_Id()));
    }

    @Test
    public void testComuDataMn_withToken_1() throws InterruptedException
    {
        // Only one user associated to the comunidad: the menu shows the item.
        COMU_DATA_AC.checkMenuItem_WTk(mActivity);
        intended(hasExtra(COMUNIDAD_ID.extra, mUsuarioComunidad.getComunidad().getC_Id()));
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
        onView(withId(R.id.see_usercomu_by_user_ac_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void testDeleteUserComu_1()
    {
        whatToClean = CLEAN_NOTHING;

        onView(withId(R.id.usercomu_data_ac_delete_button)).perform(click());
        onView(withId(R.id.comu_search_ac_layout)).check(matches(isDisplayed()));
        assertThat(TKhandler.getAccessTokenInCache(), nullValue());
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(false));
        assertThat(isRegisteredUser(mActivity), is(false));
    }
}