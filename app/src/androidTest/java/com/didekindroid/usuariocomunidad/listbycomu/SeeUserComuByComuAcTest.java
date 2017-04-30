package com.didekindroid.usuariocomunidad.listbycomu;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.comunidad.ComuBundleKey;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.comunidad.testutil.ComuMenuTestUtil.COMU_SEARCH_AC;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.testutil.ActivityTestUtils.isViewDisplayed;
import static com.didekindroid.usuario.testutil.UserItemMenuTestUtils.USER_DATA_AC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.checkUserComuByComuCommon;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.checkUserComuPlantaPuerta;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.checkUserComuPortalEscalera;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.runFinalCheckUserComuByComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.SEE_USERCOMU_BY_USER_AC;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByComuFrRsId;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 27/08/15
 * Time: 11:38
 */
@RunWith(AndroidJUnit4.class)
public class SeeUserComuByComuAcTest {

    SeeUserComuByComuAc activity;
    SeeUserComuByComuFr fragment;
    UsuarioComunidad usuarioComunidad;
    long comunidadId;

    @Rule
    public IntentsTestRule<SeeUserComuByComuAc> mActivityRule = new IntentsTestRule<SeeUserComuByComuAc>(SeeUserComuByComuAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            try {
                signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
                usuarioComunidad = userComuDaoRemote.seeUserComusByUser().get(0);
                comunidadId = usuarioComunidad.getComunidad().getC_Id();
            } catch (IOException | UiException e) {
                fail();
            }
            Intent intent = new Intent();
            intent.putExtra(ComuBundleKey.COMUNIDAD_ID.key, comunidadId);
            return intent;
        }
    };

    Intent intent;

    @Before
    public void setUp() throws Exception
    {
        activity = mActivityRule.getActivity();
        fragment = (SeeUserComuByComuFr) activity.getSupportFragmentManager().findFragmentById(seeUserComuByComuFrRsId);
        assertThat(fragment, notNullValue());
        // Wait until the screen data are there.
        waitAtMost(2, SECONDS).until(isViewDisplayed(allOf(withId(R.id.see_usercomu_by_comu_list_header),
                withText(containsString(usuarioComunidad.getComunidad().getNombreComunidad())))));
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_PEPE);
    }

//    ==========================================  TESTS  ===========================================

    @Test
    public void testOnCreate() throws Exception
    {
        onView(withId(seeUserComuByComuFrRsId)).check(matches(isDisplayed()));
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));

        runFinalCheckUserComuByComu(
                checkUserComuPortalEscalera(
                        usuarioComunidad,
                        checkUserComuPlantaPuerta(
                                usuarioComunidad,
                                checkUserComuByComuCommon(usuarioComunidad)
                        )
                )
        );

        clickNavigateUp();
    }

    @Test
    public void testOnStop() throws Exception
    {
       getInstrumentation().callActivityOnStop(activity);
        assertThat(fragment.viewer.getController().getSubscriptions().size(), is(0));
    }

    //    =====================================  MENU TESTS  =======================================

    @Test
    public void testUserComuByUserMn() throws InterruptedException
    {
        SEE_USERCOMU_BY_USER_AC.checkMenuItem_WTk(activity);
        checkUp(seeUserComuByComuFrRsId);
    }

    @Test
    public void testUserDataMn() throws InterruptedException
    {
        USER_DATA_AC.checkMenuItem_WTk(activity);
        checkUp(seeUserComuByComuFrRsId);
    }

    @Test
    public void testComuSearchMn() throws InterruptedException
    {
        COMU_SEARCH_AC.checkMenuItem_WTk(activity);
        // En este caso no hay opción de 'navigate-up'.
    }
}