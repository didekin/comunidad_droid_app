package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.usuario.dominio.Comunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.common.testutils.ActivityTestUtils;
import com.didekindroid.common.utils.UIutils;
import com.didekindroid.usuario.testutils.CleanUserEnum;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.common.activity.BundleKey.COMUNIDAD_ID;
import static com.didekindroid.common.activity.BundleKey.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.common.activity.TokenHandler.TKhandler;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanWithTkhandler;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.activity.utils.RolUi.PRE;
import static com.didekindroid.usuario.activity.utils.RolUi.PRO;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_JUAN2_AND_PEPE;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutils.UserMenuTestUtils.LOGIN_AC;
import static com.didekindroid.usuario.testutils.UserMenuTestUtils.REQUIRES_USER_NO_TOKEN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_REAL_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.USER_JUAN2;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.typeUserComuData;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.typeUserData;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
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
public class RegUserAndUserComuAcTest {

    private RegUserAndUserComuAc activity;
    private Intent intent;
    Comunidad comunidad;

    CleanUserEnum whatToClean;

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
            List<Comunidad> comunidadesUserOne = null;
            try {
                signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
                comunidadesUserOne = ServOne.getComusByUser();
            } catch (UiException | IOException e) {
                e.printStackTrace();
            }
            comunidad = comunidadesUserOne.get(0);
            // We pass the comunidad as an intent.
            intent = new Intent();
            intent.putExtra(COMUNIDAD_LIST_OBJECT.key, comunidad);
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
        Comunidad comunidad = (Comunidad) intent.getSerializableExtra(COMUNIDAD_LIST_OBJECT.key);
        assertThat(comunidad, is(this.comunidad));
        assertThat(comunidad.getC_Id(), is(this.comunidad.getC_Id()));

        assertThat(activity, notNullValue());
        assertThat(activity.getFragmentManager().findFragmentById(R.id.reg_usercomu_frg), notNullValue());

        onView(withId(R.id.reg_user_and_usercomu_ac_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_user_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usercomu_frg)).check(matches(isDisplayed()));

        onView(withId(R.id.appbar)).perform(scrollTo()).check(matches(isDisplayed()));
        ActivityTestUtils.checkNavigateUp();
    }

    @Test
    public void testRegisterUserAndUserComu_1() throws UiException
    {
        whatToClean = CLEAN_JUAN2_AND_PEPE;

        activity = intentRule.getActivity();

        // Usuario data.
        typeUserData(USER_JUAN2.getUserName(),USER_JUAN2.getAlias(),USER_JUAN2.getPassword(),USER_JUAN2.getPassword());

        // UsurioComunidad data.
        typeUserComuData("portalA", "escC", "plantaB", "puerta_1", PRO, PRE);
        onView(withId(R.id.reg_user_usercomu_button)).perform(scrollTo())
                .check(matches(isDisplayed())).perform(click());

        intended(hasExtra(COMUNIDAD_ID.key, comunidad.getC_Id()));
        onView(withId(R.id.see_usercomu_by_comu_frg)).check(matches(isDisplayed()));

        assertThat(TKhandler.getAccessTokenInCache(), notNullValue());
        assertThat(TKhandler.getRefreshTokenKey(), is(TKhandler.getAccessTokenInCache().getRefreshToken().getValue()));
        assertThat(isRegisteredUser(activity), is(true));
    }

    //    =================================== MENU ===================================

    @Test
    public void testLoginMn_1() throws InterruptedException, UiException
    {
        whatToClean = CLEAN_PEPE;

        activity = intentRule.getActivity();
        assertThat(isRegisteredUser(activity), CoreMatchers.is(false));
        assertThat(TKhandler.getAccessTokenInCache(), nullValue());

        LOGIN_AC.checkMenuItem_NTk(activity);
    }

    @Test
    public void testLoginMn_2() throws InterruptedException, UiException, IOException
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
