package com.didekindroid.comunidad;

import android.content.Intent;
import android.os.Build;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.usuariocomunidad.data.UserComuDataAc;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.comunidad.Municipio;
import com.didekinlib.model.comunidad.Provincia;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.app.TaskStackBuilder.create;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.checkComuData;
import static com.didekindroid.comunidad.testutil.ComuTestData.COMU_LA_PLAZUELA_5;
import static com.didekindroid.comunidad.testutil.ComuTestData.makeComunidad;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchResultsListLayout;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_SEARCH;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.testutil.ActivityTestUtil.checkBack;
import static com.didekindroid.testutil.ActivityTestUtil.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtil.isViewDisplayedAndPerform;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.REG_COMU_USERCOMU_AC;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.REG_COMU_USER_USERCOMU_AC;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.SEE_USERCOMU_BY_USER_AC;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.regComu_UserComuAcLayout;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.regComu_User_UserComuAcLayout;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.regUser_UserComuAcLayout;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.userComuDataLayout;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.makeUsuarioComunidad;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.regSeveralUserComuSameUser;
import static com.didekinlib.model.usuariocomunidad.Rol.INQUILINO;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/05/15
 * Time: 15:00
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class ComuSearchResultsAcTest {

    private ComuSearchResultsAc activity;
    private static Comunidad comuRondaDelNorte;
    private static Comunidad comunidadToSearch;

    @Rule
    public IntentsTestRule<ComuSearchResultsAc> intentRule = new IntentsTestRule<ComuSearchResultsAc>(ComuSearchResultsAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                create(getTargetContext()).addParentStack(ComuSearchResultsAc.class).startActivities();
            }
            return new Intent().putExtra(COMUNIDAD_SEARCH.key, comunidadToSearch);
        }
    };

    @BeforeClass
    public static void setStatic() throws Exception
    {
        comuRondaDelNorte =
                makeComunidad("Ronda", "del Norte", (short) 5, "", new Municipio((short) 2, new Provincia((short) 27)));
        regSeveralUserComuSameUser(
                COMU_PLAZUELA5_JUAN,
                makeUsuarioComunidad(comuRondaDelNorte, USER_JUAN, "portal_3", "esc_A", "planta_1", "puerta_2", INQUILINO.function)
        );
        comunidadToSearch = makeComunidad("Ronda", "de la Plazuela del Norte", (short) 5, "",
                new Municipio((short) 2, new Provincia((short) 27)));
    }

    @Before
    public void setUp()
    {
        activity = intentRule.getActivity();
    }

    @AfterClass
    public static void cleanData()
    {
        cleanOptions(CLEAN_JUAN);
    }

    // ======================================= TESTS ===============================================

    @Test
    public void testOnCreate()
    {
        onView(withId(R.id.comu_list_fragment)).check(matches(isDisplayed()));
        assertThat(activity.viewer, notNullValue());

        // test_OnPrepareOptionsMenu
        // Preconditions:
        assertThat(activity.viewer.getController().isRegisteredUser(), is(true));
        // Check in the overflow menu.
        openActionBarOverflowOrOptionsMenu(activity);
        onView(withText(activity.getString(R.string.see_usercomu_by_user_ac_mn))).check(matches(isDisplayed()));

        // testOnStop
        checkSubscriptionsOnStop(activity, activity.viewer.getController());
    }

    @Test
    public void testSearchComunidades_Up()
    {
        // Caso: existen dos comunidades para el criterio de búsqueda.
        checkComuData(COMU_LA_PLAZUELA_5);
        checkComuData(comuRondaDelNorte);
        checkUp(comuSearchAcLayout);
    }

    @Test
    public void testSelectComunidad_RegUser_Back()
    {
        doSelectComunidadRegUser();
        checkBack(onView(withId(userComuDataLayout)), comuSearchResultsListLayout);
    }

    @Test
    public void testSelectComunidad_RegUser_Up()
    {
        create(getTargetContext()).addParentStack(UserComuDataAc.class).startActivities();
        doSelectComunidadRegUser();
        checkUp(seeUserComuByUserFrRsId);
    }

    @Test
    public void testSelectComunidad_UnRegUser_Up() throws UiException
    {
        activity.viewer.getController().getTkCacher().updateAuthToken(null);
        doSelectComunidadNotRegUser();
        checkUp(comuSearchResultsListLayout);
    }

    //    ======================= MENU =========================

    @Test
    public void testRegComuAndUserComu_RegUser_Up()
    {
        // Usuario registrado.
        REG_COMU_USERCOMU_AC.checkItem(activity);
        checkUp(comuSearchAcLayout);
    }

    @Test
    public void testRegComuAndUserComu_RegUser_Back()
    {
        // Usuario registrado.
        REG_COMU_USERCOMU_AC.checkItem(activity);
        checkBack(onView(withId(regComu_UserComuAcLayout)), comuSearchResultsListLayout);
    }

    @Test
    public void testRegComuAndUserComu_UnRegUser_Up() throws UiException
    {
        // Usuario no registrado.
        activity.viewer.getController().getTkCacher().updateAuthToken(null);
        REG_COMU_USER_USERCOMU_AC.checkItem(activity);
        checkUp(comuSearchAcLayout);
    }

    @Test
    public void testRegComuAndUserComu_UnRegUser_Back() throws UiException
    {
        // Usuario no registrado.
        activity.viewer.getController().getTkCacher().updateAuthToken(null);
        REG_COMU_USER_USERCOMU_AC.checkItem(activity);
        checkBack(onView(withId(regComu_User_UserComuAcLayout)), comuSearchResultsListLayout);
    }

    @Test
    public void testSeeUserComuByUser_Up()
    {
        // La consulta muestra las comunidades del usuario.
        SEE_USERCOMU_BY_USER_AC.checkItem(activity);
        checkUp(comuSearchAcLayout);
    }

    @Test
    public void testSeeUserComuByUser_Back()
    {
        // La consulta muestra las comunidades del usuario.
        SEE_USERCOMU_BY_USER_AC.checkItem(activity);
        checkBack(onView(withId(seeUserComuByUserFrRsId)), comuSearchResultsListLayout);
    }

    //    ======================= HELPERS =========================

    private void doSelectComunidadRegUser()
    {
        waitAtMost(6, SECONDS).until(isViewDisplayedAndPerform(withText(COMU_LA_PLAZUELA_5.getNombreComunidad()), click()));
        waitAtMost(6, SECONDS).until(isResourceIdDisplayed(userComuDataLayout));
    }

    private void doSelectComunidadNotRegUser()
    {
        waitAtMost(4, SECONDS).until(isViewDisplayedAndPerform(withText(COMU_LA_PLAZUELA_5.getNombreComunidad()), click()));
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(regUser_UserComuAcLayout));
    }
}