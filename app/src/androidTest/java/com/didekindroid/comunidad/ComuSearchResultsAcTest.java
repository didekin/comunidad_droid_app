package com.didekindroid.comunidad;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.comunidad.Municipio;
import com.didekinlib.model.comunidad.Provincia;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.comunidad.testutil.ComuDataTestUtil.COMU_LA_PLAZUELA_5;
import static com.didekindroid.comunidad.testutil.ComuDataTestUtil.makeComunidad;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.checkComuData;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchResultsAcLayout;
import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_SEARCH;
import static com.didekindroid.testutil.ActivityTestUtils.checkBack;
import static com.didekindroid.testutil.ActivityTestUtils.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtils.isViewDisplayed;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.makeUsuarioComunidad;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.regSeveralUserComuSameUser;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.REG_COMU_USERCOMU_AC;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.REG_COMU_USER_USERCOMU_AC;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.SEE_USERCOMU_BY_USER_AC;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.regComu_User_UserComuAcLayout;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.userComuDataLayout;
import static com.didekinlib.model.usuariocomunidad.Rol.INQUILINO;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 24/05/15
 * Time: 15:00
 */
@RunWith(AndroidJUnit4.class)
public class ComuSearchResultsAcTest {

    ComuSearchResultsAc activity;
    Comunidad comuRondaDelNorte;

    @Rule
    public IntentsTestRule<ComuSearchResultsAc> intentRule =
            new IntentsTestRule<ComuSearchResultsAc>(ComuSearchResultsAc.class) {
                @Override
                protected void beforeActivityLaunched()
                {
                    comuRondaDelNorte =
                            makeComunidad("Ronda", "del Norte", (short) 5, "", new Municipio((short) 2, new Provincia((short) 27)));
                    try {
                        regSeveralUserComuSameUser(
                                COMU_PLAZUELA5_JUAN,
                                makeUsuarioComunidad(comuRondaDelNorte, USER_JUAN, "portal_3", "esc_A", "planta_1", "puerta_2", INQUILINO.function)
                        );
                    } catch (IOException | UiException e) {
                        fail();
                    }
                }

                @Override
                protected Intent getActivityIntent()
                {
                    Comunidad comunidadToSearch = makeComunidad("Ronda", "de la Plazuela del Norte", (short) 5, "",
                            new Municipio((short) 2, new Provincia((short) 27)));
                    Intent intent = new Intent();
                    intent.putExtra(COMUNIDAD_SEARCH.key, comunidadToSearch);
                    return intent;
                }
            };

    @Before
    public void setUp()
    {
        activity = intentRule.getActivity();
    }

    @After
    public void cleanData() throws UiException, InterruptedException
    {
        cleanOptions(CLEAN_JUAN);
    }

    // ======================================= TESTS ===============================================

    @Test
    public void testOnCreate() throws UiException, IOException
    {
        onView(withId(R.id.comu_list_fragment)).check(matches(isDisplayed()));
        assertThat(activity.viewer, notNullValue());
    }

    @Test
    public void testOnStop() throws Exception
    {
        checkSubscriptionsOnStop(activity, activity.viewer.getController());
    }

    @Test
    public void testSearchComunidades_1() throws UiException, IOException, InterruptedException
    {
        // Caso: existen dos comunidades para el criterio de búsqueda.
        checkComuData(COMU_LA_PLAZUELA_5);
        checkComuData(comuRondaDelNorte);
    }

    @Test
    public void testSelectComunidad_1() throws UiException, IOException, InterruptedException
    {
        waitAtMost(4, SECONDS).until(isViewDisplayed(allOf(
                withId(R.id.nombreComunidad_view),
                withText(comuRondaDelNorte.getNombreComunidad())), click()));
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(userComuDataLayout));
        checkUp(comuSearchAcLayout);

    }

    @Test
    public void testSelectComunidad_2() throws UiException, IOException, InterruptedException
    {
        waitAtMost(4, SECONDS).until(isViewDisplayed(allOf(
                withId(R.id.nombreComunidad_view),
                withText(COMU_LA_PLAZUELA_5.getNombreComunidad())), click()));
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(userComuDataLayout));
        checkBack(onView(withId(userComuDataLayout)), comuSearchResultsAcLayout);
    }

    //    ======================= MENU =========================

    @Test
    public void test_OnPrepareOptionsMenu() throws Exception
    {
        // Preconditions:
        assertThat(activity.viewer.getController().isRegisteredUser(), is(true));

        // Check in the overflow menu.
        openActionBarOverflowOrOptionsMenu(activity);
        onView(withText(activity.getString(R.string.see_usercomu_by_user_ac_mn))).check(matches(isDisplayed()));
    }

    @Test
    public void testMenuNuevaComunidad_RegUser() throws InterruptedException, UiException, IOException
    {
        // Usuario registrado.
        REG_COMU_USERCOMU_AC.checkMenuItem_WTk(activity);
        checkUp(comuSearchAcLayout);
    }

    @Test
    public void testMenuNuevaComunidad_UnRegUser() throws InterruptedException, UiException, IOException
    {
        // Usuario no registrado.
        activity.viewer.getController().updateIsRegistered(false);
        REG_COMU_USER_USERCOMU_AC.checkMenuItem_NTk(activity);
        checkBack(onView(withId(regComu_User_UserComuAcLayout)), comuSearchAcLayout);
    }

    @Test
    public void testComunidadesByUsuario() throws InterruptedException, UiException, IOException
    {
        // La consulta muestra las comunidades del usuario.
        SEE_USERCOMU_BY_USER_AC.checkMenuItem_WTk(activity);
        checkUp(comuSearchAcLayout);
    }
}