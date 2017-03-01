package com.didekindroid.usuariocomunidad;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.security.TokenIdentityCacher;
import com.didekindroid.usuario.testutil.UserEspressoTestUtil;
import com.didekindroid.usuario.testutil.UserItemMenuTestUtils;
import com.didekindroid.usuario.testutil.UsuarioDataTestUtils;
import com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil;
import com.didekinlib.model.comunidad.Comunidad;

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
import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN2_AND_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN2;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.RolUi.PRE;
import static com.didekindroid.usuariocomunidad.RolUi.PRO;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.typeUserComuData;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 13/09/15
 * Time: 11:30
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class RegUserAndUserComuAcTest {

    Intent intent;
    Comunidad comunidad;
    @Rule
    public IntentsTestRule<RegUserAndUserComuAc> intentRule = new IntentsTestRule<RegUserAndUserComuAc>(RegUserAndUserComuAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            UsuarioDataTestUtils.cleanWithTkhandler();
        }

        @Override
        protected Intent getActivityIntent()
        {
            // Precondition 2: the comunidad already exists.
            List<Comunidad> comunidadesUserOne = null;
            try {
                UserComuDataTestUtil.signUpAndUpdateTk(UserComuDataTestUtil.COMU_TRAV_PLAZUELA_PEPE);
                comunidadesUserOne = userComuDaoRemote.getComusByUser();
            } catch (UiException | IOException e) {
                e.printStackTrace();
            }
            comunidad = comunidadesUserOne.get(0);
            intent = new Intent();
            intent.putExtra(COMUNIDAD_LIST_OBJECT.key, comunidad);
            return intent;
        }
    };
    UsuarioDataTestUtils.CleanUserEnum whatToClean;
    int activityLayoutId = R.id.reg_user_and_usercomu_ac_layout;
    private RegUserAndUserComuAc activity;

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(3000);
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

        assertThat(TokenIdentityCacher.TKhandler.isRegisteredUser(), is(false));
        Comunidad comunidad = (Comunidad) intent.getSerializableExtra(COMUNIDAD_LIST_OBJECT.key);
        assertThat(comunidad, is(this.comunidad));
        assertThat(comunidad.getC_Id(), is(this.comunidad.getC_Id()));

        assertThat(activity, notNullValue());
        assertThat(activity.getFragmentManager().findFragmentById(R.id.reg_usercomu_frg), notNullValue());

        onView(withId(activityLayoutId)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_user_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usercomu_frg)).check(matches(isDisplayed()));

        onView(withId(R.id.appbar)).perform(scrollTo()).check(matches(isDisplayed()));
        clickNavigateUp();
    }

    @Test
    public void testRegisterUserAndUserComu_1() throws UiException
    {
        whatToClean = CLEAN_JUAN2_AND_PEPE;

        activity = intentRule.getActivity();

        // Usuario data.
        UserEspressoTestUtil.typeUserData(
                USER_JUAN2.getUserName(),
                USER_JUAN2.getAlias(),
                USER_JUAN2.getPassword(),
                USER_JUAN2.getPassword());

        // UsurioComunidad data.
        typeUserComuData("portalA", "escC", "plantaB", "puerta_1", PRO, PRE);
        onView(withId(R.id.reg_user_usercomu_button)).perform(scrollTo())
                .check(matches(isDisplayed())).perform(click());

        // Actualización correcta de datos de identidad.
        assertThat(TKhandler.getAccessTokenInCache(), notNullValue());
        assertThat(TKhandler.getRefreshTokenValue(), is(TKhandler.getAccessTokenInCache().getRefreshToken().getValue()));
        assertThat(TKhandler.isRegisteredUser(), is(true));

        intended(hasExtra(COMUNIDAD_ID.key, comunidad.getC_Id()));
        onView(withId(R.id.see_usercomu_by_comu_frg)).check(matches(isDisplayed()));
        checkUp(activityLayoutId);

    }

    //    =================================== MENU ===================================

    @Test
    public void testLoginMn_NoToken() throws InterruptedException, UiException
    {
        whatToClean = CLEAN_PEPE;

        activity = intentRule.getActivity();
        assertThat(TKhandler.isRegisteredUser(), is(false));
        assertThat(TKhandler.getAccessTokenInCache(), nullValue());

        UserItemMenuTestUtils.LOGIN_AC.checkMenuItem_NTk(activity);
        checkUp(activityLayoutId);
    }

    @Test
    public void testLoginMn_WithToken() throws InterruptedException, UiException, IOException
    {
        whatToClean = CLEAN_JUAN_AND_PEPE;
        //With token.
        UserComuDataTestUtil.signUpAndUpdateTk(UserComuDataTestUtil.COMU_REAL_JUAN);

        activity = intentRule.getActivity();
        assertThat(TKhandler.isRegisteredUser(), is(true));
        assertThat(TKhandler.getAccessTokenInCache(), not(nullValue()));
        UserItemMenuTestUtils.LOGIN_AC.checkMenuItem_WTk(activity);
        checkUp(activityLayoutId);
    }
}

