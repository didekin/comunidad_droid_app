package com.didekindroid.usuariocomunidad.listbycomu;

import android.content.Intent;
import android.os.Build;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.comunidad.util.ComuBundleKey;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.app.TaskStackBuilder.create;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.comunidad.testutil.ComuMenuTestUtil.COMU_SEARCH_AC;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.lib_one.testutil.UiTestUtil.cleanTasks;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.regComuUserUserComuGetAuthTk;
import static com.didekindroid.testutil.ActivityTestUtil.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.testutil.ActivityTestUtil.isViewDisplayed;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.checkUserComuByComuRol;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.checkUserComuPlantaPuerta;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.checkUserComuPortalEscalera;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.runFinalCheckUserComuByComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.SEE_USERCOMU_BY_USER_AC;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByComuFrRsId;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_ESCORIAL_PEPE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.AllOf.allOf;

/**
 * User: pedro@didekin
 * Date: 27/08/15
 * Time: 11:38
 */
@RunWith(AndroidJUnit4.class)
public class SeeUserComuByComuAcTest {

    private SeeUserComuByComuAc activity;
    private SeeUserComuByComuFr fragment;
    private static UsuarioComunidad usuarioComunidad;

    @Rule
    public IntentsTestRule<SeeUserComuByComuAc> mActivityRule = new IntentsTestRule<SeeUserComuByComuAc>(SeeUserComuByComuAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                create(getTargetContext()).addParentStack(SeeUserComuByComuAc.class).startActivities();
            }
        }

        @Override
        protected Intent getActivityIntent()
        {
            return new Intent().putExtra(ComuBundleKey.COMUNIDAD_ID.key, usuarioComunidad.getComunidad().getC_Id());
        }
    };

    @BeforeClass
    public static void setUpStatic() throws Exception
    {
        regComuUserUserComuGetAuthTk(COMU_ESCORIAL_PEPE);
        usuarioComunidad = userComuDao.seeUserComusByUser().blockingGet().get(0);
    }

    @Before
    public void setUp() throws Exception
    {
        activity = mActivityRule.getActivity();
        fragment = (SeeUserComuByComuFr) activity.getSupportFragmentManager().findFragmentById(seeUserComuByComuFrRsId);
        // Wait until the screen data are there.
        waitAtMost(8, SECONDS).until(() -> fragment.viewer.getViewInViewer().getAdapter() != null);
        waitAtMost(6, SECONDS)
                .until(
                        isViewDisplayed(
                                allOf(
                                        withId(R.id.see_usercomu_by_comu_list_header),
                                        withText(containsString(usuarioComunidad.getComunidad().getNombreComunidad()))
                                )
                        )
                );
    }

    @After
    public void tearDown() throws Exception
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cleanTasks(activity);
        }
    }

    @AfterClass
    public static void cleanStatic()
    {
        cleanOptions(CLEAN_PEPE);
    }

//    ==========================================  TESTS  ===========================================

    @Test
    public void testOnCreate()
    {
        waitAtMost(4, SECONDS).until(isViewDisplayed(withId(seeUserComuByComuFrRsId)));

        runFinalCheckUserComuByComu(
                checkUserComuPortalEscalera(
                        usuarioComunidad,
                        checkUserComuPlantaPuerta(
                                usuarioComunidad,
                                checkUserComuByComuRol(usuarioComunidad)
                        )
                )
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkUp(seeUserComuByUserFrRsId);
        }
    }

    @Test
    public void testOnStop()
    {
        checkSubscriptionsOnStop(activity, fragment.viewer.getController());
    }

    //    =====================================  MENU TESTS  =======================================

    @SuppressWarnings("RedundantThrows")
    @Test
    public void testUserComuByUserMn() throws InterruptedException
    {
        SEE_USERCOMU_BY_USER_AC.checkItem(activity);
        checkUp(comuSearchAcLayout);
    }

    @Test
    public void testComuSearchMn()
    {
        COMU_SEARCH_AC.checkItem(activity);
        // En este caso no hay opción de 'navigate-up'.
    }
}