package com.didekindroid.usuariocomunidad.register;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.ParentViewerIf;
import com.didekindroid.usuariocomunidad.repository.CtrlerUsuarioComunidad;
import com.didekinlib.model.comunidad.Comunidad;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.regComuUserUserComuGetAuthTk;
import static com.didekindroid.testutil.ActivityTestUtil.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtil.isViewDisplayedAndPerform;
import static com.didekindroid.usuariocomunidad.RolUi.INQ;
import static com.didekindroid.usuariocomunidad.RolUi.PRE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.typeUserComuData;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpMockGcmGetComu;
import static io.reactivex.Completable.complete;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 05/06/17
 * Time: 20:17
 */
@RunWith(AndroidJUnit4.class)
public class ViewerRegUserComuAcTest {

    private static Comunidad comunidad;
    private RegUserComuAc activity;

    @Rule
    public IntentsTestRule<RegUserComuAc> intentRule = new IntentsTestRule<RegUserComuAc>(RegUserComuAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            return new Intent().putExtra(COMUNIDAD_LIST_OBJECT.key, comunidad);
        }
    };

    @BeforeClass
    public static void setUpStatic() throws Exception
    {
        comunidad = signUpMockGcmGetComu(COMU_PLAZUELA5_JUAN, "juan_mock_gcm");
        regComuUserUserComuGetAuthTk(COMU_TRAV_PLAZUELA_PEPE);
    }

    @Before
    public void setUp() throws Exception
    {
        activity = intentRule.getActivity();
        waitAtMost(4, SECONDS).until(() -> activity.viewer != null);
    }

    @AfterClass
    public static void tearDown()
    {
        cleanOptions(CLEAN_JUAN_AND_PEPE);
    }

    @Test
    public void test_RegUserComuButtonListener()
    {
        // test_NewViewerRegUserComuAc
        assertThat(activity.viewer.getController(), isA(CtrlerUsuarioComunidad.class));
        // Check for initialization of fragments viewers.
        ParentViewerIf viewerParent = activity.viewer;
        assertThat(viewerParent.getChildViewer(ViewerRegUserComuFr.class), notNullValue());
        // test_DoViewInViewer
        onView(allOf(
                withId(R.id.descripcion_comunidad_text),
                withText(comunidad.getNombreComunidad())
        )).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usercomu_button)).perform(scrollTo()).check(matches(isDisplayed()));

        // Exec OK
        typeUserComuData("port2", "escale_b", "planta-N", "puerta5", PRE, INQ);
        onView(withId(R.id.reg_usercomu_button)).perform(scrollTo(), click());
        waitAtMost(6, SECONDS).until(isResourceIdDisplayed(seeUserComuByUserFrRsId));
    }

    @Test
    public void test_RegUserComuObserver()
    {
        ViewerRegUserComuAc.RegUserComuObserver observer = activity.viewer.new RegUserComuObserver(comunidad);
        complete().subscribeWith(observer);
        waitAtMost(4, SECONDS).until(isViewDisplayedAndPerform(withId(seeUserComuByUserFrRsId)));
        intended(hasExtra(COMUNIDAD_ID.key, comunidad.getC_Id()));
        assertThat(observer.isDisposed(), is(true));
    }

    /*  =========================  TESTS FOR ACTIVITY/FRAGMENT LIFECYCLE  ===========================*/

    @Test
    public void test_OnStop()
    {
        checkSubscriptionsOnStop(activity, activity.viewer.getController());
    }
}