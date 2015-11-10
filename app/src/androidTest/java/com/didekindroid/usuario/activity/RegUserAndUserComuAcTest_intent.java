package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.serviceone.domain.Comunidad;
import com.didekindroid.R;
import com.didekindroid.common.utils.UIutils;
import com.didekindroid.usuario.activity.utils.CleanEnum;
import com.didekindroid.usuario.dominio.FullComunidadIntent;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.common.TokenHandler.TKhandler;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.activity.utils.CleanEnum.CLEAN_JUAN2_AND_PEPE;
import static com.didekindroid.usuario.activity.utils.CleanEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.activity.utils.CleanEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.activity.utils.RolCheckBox.PRESIDENTE;
import static com.didekindroid.usuario.activity.utils.RolCheckBox.PROPIETARIO;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.COMUNIDAD_ID;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.usuario.activity.utils.UserMenuTestUtils.LOGIN_AC;
import static com.didekindroid.usuario.activity.utils.UserMenuTestUtils.REQUIRES_USER_NO_TOKEN;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.cleanOptions;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.cleanWithTkhandler;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.signUpAndUpdateTk;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.typeRegUserComuData;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_REAL_JUAN;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuario.dominio.DomainDataUtils.USER_JUAN2;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 13/09/15
 * Time: 11:30
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class RegUserAndUserComuAcTest_intent {

    private RegUserAndUserComuAc activity;
    private Intent intent;
    Comunidad comunidad;

    CleanEnum whatToClean;

    @Rule
    public IntentsTestRule<RegUserAndUserComuAc> intentRule = new IntentsTestRule<RegUserAndUserComuAc>
            (RegUserAndUserComuAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            // Erase identification data.
            cleanWithTkhandler();
        }

        @Override
        protected Intent getActivityIntent()
        {
            // Precondition 2: the comunidad already exists.
            signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
            List<Comunidad> comunidadesUserOne = ServOne.getComusByUser();
            comunidad = comunidadesUserOne.get(0);
            // We pass the comunidad as an intent.
            intent = new Intent();
            intent.putExtra(COMUNIDAD_LIST_OBJECT.extra, new FullComunidadIntent(comunidad));
            return intent;
        }
    };

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(5000);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(whatToClean);
    }

    @Test
    public void testOnCreate() throws Exception
    {
        whatToClean = CLEAN_PEPE;

        activity = intentRule.getActivity();

        assertThat(UIutils.isRegisteredUser(activity), is(false));
        FullComunidadIntent comunidadIntent = (FullComunidadIntent) intent.getSerializableExtra(COMUNIDAD_LIST_OBJECT.extra);
        assertThat(comunidadIntent.getComunidad(), is(comunidad));
        assertThat(comunidadIntent.getComunidad().getC_Id(), is(comunidad.getC_Id()));

        assertThat(activity, notNullValue());
        assertThat(activity.getFragmentManager().findFragmentById(R.id.reg_usercomu_frg), notNullValue());

        onView(withId(R.id.reg_user_and_usercomu_ac_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_user_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usercomu_frg)).check(matches(isDisplayed()));

        onView(withId(R.id.appbar)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withContentDescription("Navigate up")).check(matches(isDisplayed()));
        onView(CoreMatchers.allOf(
                        withContentDescription("Navigate up"),
                        isClickable())
        ).check(matches(isDisplayed())).perform(click());
    }

    @Test
    public void testRegisterUserAndUserComu_1()
    {
        whatToClean = CLEAN_JUAN2_AND_PEPE;

        activity = intentRule.getActivity();

        // Usuario data.
        onView(withId(R.id.reg_usuario_email_editT)).perform(scrollTo(), typeText(USER_JUAN2.getUserName()));
        onView(withId(R.id.reg_usuario_alias_ediT)).perform(scrollTo(), typeText(USER_JUAN2.getAlias()));
        onView(withId(R.id.reg_usuario_password_ediT)).perform(scrollTo(), typeText(USER_JUAN2.getPassword()));
        onView(withId(R.id.reg_usuario_password_confirm_ediT)).perform(scrollTo(),
                typeText(USER_JUAN2.getPassword()), closeSoftKeyboard());

        // UsurioComunidad data.
        typeRegUserComuData("portalA", "escC", "plantaB", "puerta_1", PROPIETARIO, PRESIDENTE);
        onView(withId(R.id.reg_user_usercomu_button)).perform(scrollTo())
                .check(matches(isDisplayed())).perform(click());

        intended(hasExtra(COMUNIDAD_ID.extra, comunidad.getC_Id()));
        onView(withId(R.id.see_usercomu_by_comu_ac_frg_container)).check(matches(isDisplayed()));

        assertThat(TKhandler.getAccessTokenInCache(), notNullValue());
        assertThat(TKhandler.getRefreshTokenKey(), is(TKhandler.getAccessTokenInCache().getRefreshToken().getValue()));
        assertThat(isRegisteredUser(activity), is(true));
    }

    //    =================================== MENU ===================================

    @Test
    public void testLoginMn_1() throws InterruptedException
    {
        whatToClean = CLEAN_PEPE;

        activity = intentRule.getActivity();
        assertThat(isRegisteredUser(activity), CoreMatchers.is(false));
        assertThat(TKhandler.getAccessTokenInCache(), nullValue());

        LOGIN_AC.checkMenuItem_NTk(activity);
    }

    @Test
    public void testLoginMn_2() throws InterruptedException
    {
        whatToClean = CLEAN_JUAN_AND_PEPE;
        //With token.
        signUpAndUpdateTk(COMU_REAL_JUAN);

        activity = intentRule.getActivity();
        assertThat(isRegisteredUser(activity), CoreMatchers.is(true));
        assertThat(TKhandler.getAccessTokenInCache(), not(nullValue()));

        try {
            LOGIN_AC.checkMenuItem_WTk(activity);
            fail();
        } catch (UnsupportedOperationException e) {
            assertThat(e.getMessage(), CoreMatchers.is(LOGIN_AC.name() + REQUIRES_USER_NO_TOKEN));
        }
    }
}

