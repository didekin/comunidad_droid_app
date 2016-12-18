package com.didekindroid.usuariocomunidad;

import android.content.Intent;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.comunidad.Comunidad;
import com.didekinaar.R;
import com.didekinaar.exception.UiException;
import com.didekinaar.usuario.testutil.UserEspressoTestUtil;
import com.didekindroid.comunidad.ComuBundleKey;
import com.didekinaar.testutil.AarActivityTestUtils;
import com.didekinaar.usuario.testutil.UserItemMenuTestUtils;
import com.didekinaar.usuario.testutil.UsuarioTestUtils;
import com.didekinaar.utils.UIutils;
import com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil;
import com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil;

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
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_LIST_OBJECT;
import static com.didekinaar.security.TokenHandler.TKhandler;
import static com.didekinaar.testutil.AarActivityTestUtils.checkUp;
import static com.didekinaar.testutil.AarActivityTestUtils.cleanOptions;
import static com.didekinaar.testutil.AarActivityTestUtils.clickNavigateUp;
import static com.didekinaar.testutil.AarActivityTestUtils.CleanUserEnum.CLEAN_JUAN2_AND_PEPE;
import static com.didekinaar.testutil.AarActivityTestUtils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekinaar.testutil.AarActivityTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekinaar.usuariocomunidad.AarUserComuService.AarUserComuServ;
import static com.didekinaar.usuariocomunidad.RolUi.PRE;
import static com.didekinaar.usuariocomunidad.RolUi.PRO;
import static com.didekinaar.utils.UIutils.isRegisteredUser;
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

    private RegUserAndUserComuAc activity;
    Intent intent;
    Comunidad comunidad;

    AarActivityTestUtils.CleanUserEnum whatToClean;
    int activityLayoutId = R.id.reg_user_and_usercomu_ac_layout;

    @Rule
    public IntentsTestRule<RegUserAndUserComuAc> intentRule = new IntentsTestRule<RegUserAndUserComuAc>(RegUserAndUserComuAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            AarActivityTestUtils.cleanWithTkhandler();
        }

        @Override
        protected Intent getActivityIntent()
        {
            // Precondition 2: the comunidad already exists.
            List<Comunidad> comunidadesUserOne = null;
            try {
                UserComuTestUtil.signUpAndUpdateTk(UserComuTestUtil.COMU_TRAV_PLAZUELA_PEPE);
                comunidadesUserOne = AarUserComuServ.getComusByUser();
            } catch (UiException | IOException e) {
                e.printStackTrace();
            }
            comunidad = comunidadesUserOne.get(0);
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

        onView(withId(activityLayoutId)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.reg_user_frg)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.reg_usercomu_frg)).check(matches(isDisplayed()));

        onView(ViewMatchers.withId(R.id.appbar)).perform(scrollTo()).check(matches(isDisplayed()));
        clickNavigateUp();
    }

    @Test
    public void testRegisterUserAndUserComu_1() throws UiException
    {
        whatToClean = CLEAN_JUAN2_AND_PEPE;

        activity = intentRule.getActivity();

        // Usuario data.
        UserEspressoTestUtil.typeUserData(UsuarioTestUtils.USER_JUAN2.getUserName(), UsuarioTestUtils.USER_JUAN2.getAlias(), UsuarioTestUtils.USER_JUAN2.getPassword(), UsuarioTestUtils.USER_JUAN2.getPassword());

        // UsurioComunidad data.
        UserComuEspressoTestUtil.typeUserComuData("portalA", "escC", "plantaB", "puerta_1", PRO, PRE);
        onView(ViewMatchers.withId(R.id.reg_user_usercomu_button)).perform(scrollTo())
                .check(matches(isDisplayed())).perform(click());

        // Actualización correcta de datos de identidad.
        assertThat(TKhandler.getAccessTokenInCache(), notNullValue());
        assertThat(TKhandler.getRefreshTokenValue(), is(TKhandler.getAccessTokenInCache().getRefreshToken().getValue()));
        assertThat(isRegisteredUser(activity), is(true));

        intended(IntentMatchers.hasExtra(ComuBundleKey.COMUNIDAD_ID.key, comunidad.getC_Id()));
        onView(ViewMatchers.withId(R.id.see_usercomu_by_comu_frg)).check(matches(isDisplayed()));
        checkUp(activityLayoutId);

    }

    //    =================================== MENU ===================================

    @Test
    public void testLoginMn_NoToken() throws InterruptedException, UiException
    {
        whatToClean = CLEAN_PEPE;

        activity = intentRule.getActivity();
        assertThat(isRegisteredUser(activity), is(false));
        assertThat(TKhandler.getAccessTokenInCache(), nullValue());

        UserItemMenuTestUtils.LOGIN_AC.checkMenuItem_NTk(activity);
        checkUp(activityLayoutId);
    }

    @Test
    public void testLoginMn_WithToken() throws InterruptedException, UiException, IOException
    {
        whatToClean = CLEAN_JUAN_AND_PEPE;
        //With token.
        UserComuTestUtil.signUpAndUpdateTk(UserComuTestUtil.COMU_REAL_JUAN);

        activity = intentRule.getActivity();
        assertThat(isRegisteredUser(activity), is(true));
        assertThat(TKhandler.getAccessTokenInCache(), not(nullValue()));
        UserItemMenuTestUtils.LOGIN_AC.checkMenuItem_WTk(activity);
        checkUp(activityLayoutId);
    }
}

