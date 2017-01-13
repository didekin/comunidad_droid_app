package com.didekindroid.comunidad;

import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;


import com.didekindroid.exception.UiException;
import com.didekindroid.comunidad.testutil.ComuDataTestUtil;
import com.didekindroid.usuario.testutil.UsuarioDataTestUtils;
import com.didekindroid.R;
import com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_SEARCH;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.REG_COMU_USERCOMU_AC;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.REG_COMU_USER_USERCOMU_AC;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.SEE_USERCOMU_BY_USER_AC;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 09/10/15
 * Time: 13:04
 */


@RunWith(AndroidJUnit4.class)
public class ComuSearchResultsAc_2_SlowTest {

    private ComuSearchResultsAc activity;
    ComuSearchResultsListFr mComunidadSummaryFrg;
    Intent intent;
    UsuarioDataTestUtils.CleanUserEnum whatClean;

    // Navigate-up layout cuando hay resultados.
    protected int fragmentLayoutId = R.id.comu_list_fragment;
    // Navigate-up layout cuando NO hay resultados.
    int activityLayoutId = R.id.comu_search_results_frg_container_ac;

    @Rule
    public ActivityTestRule<ComuSearchResultsAc> mActivityRule =
            new ActivityTestRule<>(ComuSearchResultsAc.class, true, false);

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(5000);
    }

    @Before
    public void getFixture() throws Exception
    {
        Thread.sleep(5000);

        whatClean = UsuarioDataTestUtils.CleanUserEnum.CLEAN_NOTHING;

        intent = new Intent();
        intent.putExtra(COMUNIDAD_SEARCH.key, ComuDataTestUtil.COMU_LA_PLAZUELA_5);
    }

    @After
    public void cleanData() throws UiException
    {
        cleanOptions(whatClean);
    }

    @Test
    public void testNoUserNoResults() throws Exception
    {
        activity = mActivityRule.launchActivity(intent);
        assertThat(TKhandler.isRegisteredUser(), is(false));

        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getSupportFragmentManager().findFragmentById(R.id.comu_list_fragment);
        assertThat(activity, notNullValue());
        assertThat(mComunidadSummaryFrg, notNullValue());

        // No results in DB. The user is invited to register.
        checkToastInTest(R.string.no_result_search_comunidad, activity);
        onView(ViewMatchers.withId(R.id.reg_comu_usuario_usuariocomu_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void testMenuNuevaComunidad_noToken() throws InterruptedException, UiException, IOException
    {
        whatClean = CLEAN_JUAN;

        UserComuDataTestUtil.regTwoUserComuSameUser(UserComuDataTestUtil.makeListTwoUserComu());
        // Borro los datos del userComu.
        UsuarioDataTestUtils.cleanWithTkhandler();

        //Usuario no registrado. La búsqueda devuelve una comunidad.
        activity = mActivityRule.launchActivity(intent);
        assertThat(TKhandler.isRegisteredUser(), is(false));
        // La opción de nueva comunidad implica también registro de usuario.
        REG_COMU_USER_USERCOMU_AC.checkMenuItem_NTk(activity);
        checkUp(fragmentLayoutId);
    }

    @Test
    public void testMenuNuevaComunidad_withToken() throws InterruptedException, UiException, IOException
    {
        whatClean = CLEAN_JUAN;

        UserComuDataTestUtil.regTwoUserComuSameUser(UserComuDataTestUtil.makeListTwoUserComu());
        //Usuario no registrado. La búsqueda devuelve una comunidad.
        activity = mActivityRule.launchActivity(intent);
        assertThat(TKhandler.isRegisteredUser(), is(true));

        Thread.sleep(2000);
        // La opción de nueva comunidad NO implica registro de usuario.
        REG_COMU_USERCOMU_AC.checkMenuItem_WTk(activity);
        checkUp(fragmentLayoutId);
    }

    @Test
    public void tesComunidadesByUsuario_noToken() throws InterruptedException, UiException, IOException
    {
        whatClean = CLEAN_JUAN;

        //Usuario no registrado. La búsqueda devuelve una comunidad.
        UserComuDataTestUtil.regTwoUserComuSameUser(UserComuDataTestUtil.makeListTwoUserComu());
        // Borro los datos del userComu.
        UsuarioDataTestUtils.cleanWithTkhandler();
        activity = mActivityRule.launchActivity(intent);
        assertThat(TKhandler.isRegisteredUser(), is(false));

        SEE_USERCOMU_BY_USER_AC.checkMenuItem_NTk(activity);
        // No se mueve de la actividad. El home llevaría a la actividad de búsqueda de comunidad.
        onView(withId(fragmentLayoutId)).check(matches(isDisplayed()));
    }

    @Test
    public void testComunidadesByUsuario_withToken() throws InterruptedException, UiException, IOException
    {
        whatClean = CLEAN_JUAN;

        UserComuDataTestUtil.regTwoUserComuSameUser(UserComuDataTestUtil.makeListTwoUserComu());
        //Usuario registrado. La búsqueda devuelve una comunidad.
        activity = mActivityRule.launchActivity(intent);
        assertThat(TKhandler.isRegisteredUser(), is(true));
        Thread.sleep(2000);
        // La consulta muestra las comunidades del usuario.
        SEE_USERCOMU_BY_USER_AC.checkMenuItem_WTk(activity);
        checkUp(fragmentLayoutId);
    }
}