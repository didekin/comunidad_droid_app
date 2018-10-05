package com.didekindroid.usuariocomunidad.listbyuser;

import android.os.Build;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.comunidad.testutil.ComuTestData;
import com.didekindroid.usuariocomunidad.RolUi;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.app.TaskStackBuilder.create;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.comunidad.testutil.ComuMenuTestUtil.COMU_SEARCH_AC;
import static com.didekindroid.comunidad.testutil.ComuTestData.COMU_LA_FUENTE;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.lib_one.testutil.UiTestUtil.cleanTasks;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.testutil.ActivityTestUtil.isDataDisplayedAndClick;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtil.isViewDisplayed;
import static com.didekindroid.testutil.ActivityTestUtil.isViewDisplayedAndPerform;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.regComu_UserComuAcLayout;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.userComuDataLayout;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_LA_FUENTE_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.regSeveralUserComuSameUser;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 17/09/15
 * Time: 16:15
 */
@RunWith(AndroidJUnit4.class)
public class SeeUserComuByUserAcTest {

    @Rule
    public IntentsTestRule<SeeUserComuByUserAc> intentRule = new IntentsTestRule<SeeUserComuByUserAc>(SeeUserComuByUserAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                create(getTargetContext()).addParentStack(SeeUserComuByUserAc.class).startActivities();
            }
        }
    };
    private SeeUserComuByUserAc activity;

    @BeforeClass
    public static void setUpStatic() throws Exception
    {
        regSeveralUserComuSameUser(COMU_ESCORIAL_PEPE, COMU_LA_FUENTE_PEPE);   // Almería, Alicante.
    }

    @Before
    public void setUp() throws Exception
    {
        activity = intentRule.getActivity();
        waitAtMost(4, SECONDS).until(isViewDisplayed(withId(seeUserComuByUserFrRsId)));
    }

    @After
    public void tearDown() throws Exception
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cleanTasks(activity);
        }

    }

    @AfterClass
    public static void cleanStatic(){
        cleanOptions(CLEAN_PEPE);
    }

//  ================================================================================================================

    @Test
    public void test_Oncreate_1()
    {
        waitAtMost(4, SECONDS).until(isViewDisplayedAndPerform(withId(R.id.new_comunidad_fab), click()));
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(regComu_UserComuAcLayout));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkUp(comuSearchAcLayout);
        }
    }

    @Test
    public void test_Oncreate_2()
    {
        waitAtMost(4, SECONDS).until(
                () -> {
                    try {
                        onData(is(COMU_LA_FUENTE_PEPE)).onChildView(
                                allOf(
                                        withId(R.id.nombreComunidad_view),
                                        withText(COMU_LA_FUENTE.getNombreComunidad()))
                        ).check(matches(isDisplayed()));
                        return true;
                    } catch (Exception ne) {
                        return false;
                    }
                }
        );

        onData(is(COMU_LA_FUENTE_PEPE))
                .onChildView(
                        allOf(
                                withId(R.id.usercomu_item_roles_txt),
                                withText(RolUi.formatRolToString(COMU_LA_FUENTE_PEPE.getRoles(), activity.getResources()))
                        )
                )
                .check(matches(isDisplayed()));

        onData(is(COMU_ESCORIAL_PEPE))
                .onChildView(
                        allOf(
                                withId(R.id.nombreComunidad_view),
                                withText(ComuTestData.COMU_EL_ESCORIAL.getNombreComunidad())
                        )
                )
                .check(matches(isDisplayed()));
        onData(is(COMU_ESCORIAL_PEPE))
                .onChildView(
                        allOf(
                                withId(R.id.usercomu_item_roles_txt),
                                withText(RolUi.formatRolToString(COMU_ESCORIAL_PEPE.getRoles(), activity.getResources()))
                        )
                )
                .check(matches(isDisplayed()));

        onView(allOf(
                withText("Elda"),
                withId(R.id.municipio_view),
                hasSibling(allOf(
                        withId(R.id.provincia_view),
                        withText("Alicante/Alacant")
                ))
        )).check(matches(isDisplayed()));

        onView(allOf(
                withText("Benizalón"),
                withId(R.id.municipio_view),
                hasSibling(allOf(
                        withId(R.id.provincia_view),
                        withText("Almería")
                ))
        )).check(matches(isDisplayed()));

        // Change comunidad.
        waitAtMost(4, SECONDS).until(isDataDisplayedAndClick(is(COMU_ESCORIAL_PEPE)));
        onView(withId(userComuDataLayout)).check(matches(isDisplayed()));
        // Check UP.
        checkUp(seeUserComuByUserFrRsId);
    }

    @Test
    public void testComuSearchMn_withToken()
    {
        COMU_SEARCH_AC.checkItem(activity);
        // NO hay opción de navigate-up.
    }
}