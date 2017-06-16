package com.didekindroid.comunidad;

import android.content.Intent;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.comunidad.testutil.ComuDataTestUtil;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.testutil.UsuarioDataTestUtils;
import com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.comunidad.ComunidadAutonoma;
import com.didekinlib.model.comunidad.Municipio;
import com.didekinlib.model.comunidad.Provincia;
import com.didekinlib.model.usuario.Usuario;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.comunidad.testutil.ComuDataTestUtil.COMU_LA_PLAZUELA_5;
import static com.didekindroid.comunidad.testutil.ComuDataTestUtil.COMU_REAL;
import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_SEARCH;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.checkBack;
import static com.didekindroid.testutil.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanTwoUsers;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanWithTkhandler;
import static com.didekindroid.usuariocomunidad.RolUi.PRO;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.makeListTwoUserComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.regTwoUserComuSameUser;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekindroid.usuariocomunidad.util.UserComuBundleKey.USERCOMU_LIST_OBJECT;
import static external.LongListMatchers.withAdaptedData;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/05/15
 * Time: 15:00
 */
@RunWith(AndroidJUnit4.class)
public class ComuSearchResultsAc_1_Test {

    @Rule
    public IntentsTestRule<ComuSearchResultsAc> mIntentRule =
            new IntentsTestRule<>(ComuSearchResultsAc.class, true, false);

    ComuSearchResultsListFr mComunidadSummaryFrg;
    ComuSearchResultsListAdapter adapter;
    Intent intent;
    UsuarioDataTestUtils.CleanUserEnum whatClean = UsuarioDataTestUtils.CleanUserEnum.CLEAN_NOTHING;

    int activityLayoutId = R.id.comu_list_fragment;
    private ComuSearchResultsAc activity;

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(2000);
    }

    @Before
    public void getFixture() throws Exception
    {
        intent = new Intent();
        intent.putExtra(COMUNIDAD_SEARCH.key, COMU_LA_PLAZUELA_5);
    }

    @After
    public void cleanData() throws UiException, InterruptedException
    {
        cleanOptions(whatClean);
        Thread.sleep(2000);
        assertThat(TKhandler.isRegisteredUser(), is(false));
    }

    @Test
    public void testOnCreate() throws UiException, IOException
    {
        whatClean = CLEAN_JUAN;

        // Inserto comunidades en DB.
        regTwoUserComuSameUser(makeListTwoUserComu());
        activity = mIntentRule.launchActivity(intent);
        onView(withId(R.id.comu_list_fragment)).check(matches(isDisplayed()));
    }

    @Test
    public void testSearchComunidades_1() throws UiException, IOException, InterruptedException
    {
        whatClean = CLEAN_JUAN;

        // User with 2 comunidades. We search with one of them exactly.
        regTwoUserComuSameUser(makeListTwoUserComu());
        activity = mIntentRule.launchActivity(intent);
        assertThat(TKhandler.isRegisteredUser(), is(true));

        Thread.sleep(2000);
        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getSupportFragmentManager().findFragmentById(R.id.comu_list_fragment);
        adapter = mComunidadSummaryFrg.mAdapter;
        assertThat(adapter.getCount(), is(1));
        Comunidad comunidad = (Comunidad) intent.getSerializableExtra(COMUNIDAD_SEARCH.key);
        onView(withId(android.R.id.list)).check(matches(
                withAdaptedData(Matchers.<Object>equalTo(comunidad))));
    }

    @Test
    public void testSearchComunidades_2() throws UiException, IOException, InterruptedException
    {
        whatClean = CLEAN_JUAN;

        // Caso: existen dos comunidades para el criterio de búsqueda.

        Comunidad comunidadNew = ComuDataTestUtil.makeComunidad("Ronda", "del Norte", (short) 5, "",
                new Municipio((short) 2, new Provincia((short) 27)));
        UserComuDataTestUtil.regThreeUserComuSameUser(makeListTwoUserComu(), comunidadNew);

        // Criterio de búsqueda.
        Comunidad comunidad = ComuDataTestUtil.makeComunidad("Ronda", "de la Plazuela del Norte", (short) 5, "",
                new Municipio((short) 2, new Provincia((short) 27)));
        Intent intent = new Intent();
        intent.putExtra(COMUNIDAD_SEARCH.key, comunidad);
        activity = mIntentRule.launchActivity(intent);

        Thread.sleep(2000);
        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getSupportFragmentManager().findFragmentById(R.id.comu_list_fragment);
        adapter = mComunidadSummaryFrg.mAdapter;
        assertThat(adapter.getCount(), is(2));
        onView(withId(android.R.id.list)).check(
                matches(withAdaptedData(Matchers.<Object>is(comunidadNew))));
        onView(withId(android.R.id.list)).check(
                matches(withAdaptedData(Matchers.<Object>is(COMU_LA_PLAZUELA_5))));
    }

    @Test
    public void testSearchComunidades_3() throws InterruptedException, UiException
    {
        // 1. No existe la comunidad en DB. 2. El usuario no está registrado.

        // Criterio de búsqueda.
        Comunidad comunidad = ComuDataTestUtil.makeComunidad("Rincón", "del No Existente", (short) 123, "",
                new Municipio((short) 2, new Provincia(new ComunidadAutonoma((short) 12), (short) 27, null)));
        Intent intent = new Intent();
        intent.putExtra(COMUNIDAD_SEARCH.key, comunidad);

        // Precondition.
        assertThat(TKhandler.isRegisteredUser(), is(false));
        activity = mIntentRule.launchActivity(intent);
        // Checks.
        waitAtMost(4, SECONDS).until(isToastInView(R.string.no_result_search_comunidad, activity));
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(R.id.reg_comu_usuario_usuariocomu_layout));
        intended(hasExtra(COMUNIDAD_SEARCH.key, comunidad));
    }

    @Test
    public void testSearchComunidades_4() throws InterruptedException, UiException, IOException
    {
        whatClean = CLEAN_JUAN;

        /* 1. No existe la comunidad en DB. 2. El usuario está registrado.*/
        signUpAndUpdateTk(COMU_REAL_JUAN);

        // Criterio de búsqueda.
        Comunidad comunidad = ComuDataTestUtil.makeComunidad("Rincón", "del No Existente", (short) 123, "",
                new Municipio((short) 2, new Provincia(new ComunidadAutonoma((short) 12), (short) 27, null)));
        Intent intent = new Intent();
        intent.putExtra(COMUNIDAD_SEARCH.key, comunidad);

        activity = mIntentRule.launchActivity(intent);
        // Checks.
        waitAtMost(4, SECONDS).until(isToastInView(R.string.no_result_search_comunidad, activity));
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(R.id.reg_comu_and_usercomu_layout));
        intended(hasExtra(COMUNIDAD_SEARCH.key, comunidad));
    }


    @Test
    public void testOnListItemClick_1() throws UiException, IOException, InterruptedException
    {
        whatClean = CLEAN_JUAN;

        //Usuario no registrado. La búsqueda devuelve una comunidad.

        regTwoUserComuSameUser(makeListTwoUserComu());
        // Borro los datos del userComu.
        cleanWithTkhandler();

        activity = mIntentRule.launchActivity(intent);
        assertThat(TKhandler.isRegisteredUser(), is(false));

        Thread.sleep(2000);
        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getSupportFragmentManager().findFragmentById(R.id.comu_list_fragment);
        adapter = mComunidadSummaryFrg.mAdapter;
        assertThat(adapter.getCount(), is(1));
        onView(withAdaptedData(Matchers.<Object>is(COMU_LA_PLAZUELA_5))).check(matches(isDisplayed()));
        onData(is(instanceOf(Comunidad.class))).onChildView(allOf(
                withId(R.id.nombreComunidad_view),
                withText(COMU_LA_PLAZUELA_5.getNombreComunidad())
        )).perform(click());

        onView(withId(R.id.reg_user_and_usercomu_ac_layout)).check(matches(isDisplayed()));

        checkUp(activityLayoutId);
    }

    @Test
    public void testOnListItemClick_2() throws UiException, IOException, InterruptedException
    {
        whatClean = CLEAN_JUAN;

        // Usuario registrado. La búsqueda devuelve una comunidad a la que él ya está asociado.

        regTwoUserComuSameUser(makeListTwoUserComu());
        activity = mIntentRule.launchActivity(intent);
        assertThat(TKhandler.isRegisteredUser(), is(true));

        Thread.sleep(2000);
        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getSupportFragmentManager().findFragmentById(R.id.comu_list_fragment);
        adapter = mComunidadSummaryFrg.mAdapter;
        assertThat(adapter.getCount(), is(1));

        Comunidad comunidad = adapter.getItem(0);
        onView(withAdaptedData(Matchers.<Object>equalTo(comunidad))).check(matches(isDisplayed()));
        onData(is(instanceOf(Comunidad.class))).onChildView(
                allOf(
                        withId(R.id.nombreComunidad_view),
                        withText(COMU_LA_PLAZUELA_5.getNombreComunidad())
                )).perform(click());

        ViewInteraction viewInteraction = onView(withId(R.id.usercomu_data_ac_layout)).check(matches(isDisplayed()));

        checkBack(viewInteraction, activityLayoutId);
    }

    @Test
    public void testOnListItemClick_3() throws Exception
    {
        whatClean = CLEAN_JUAN;

        // Intent específico para este test.
        intent = new Intent();
        intent.putExtra(COMUNIDAD_SEARCH.key, COMU_REAL);

        // Usuario registrado. La búsqueda devuelve una comunidad a la que él ya está asociado.
        // Verificamos intent de salida.
        Usuario userIntent = signUpAndUpdateTk(COMU_REAL_JUAN);
        Comunidad comuIntent = userComuDaoRemote.getComusByUser().get(0);

        activity = mIntentRule.launchActivity(intent);
        assertThat(TKhandler.isRegisteredUser(), is(true));

        onData(Matchers.is(COMU_REAL)).perform(click());

        UsuarioComunidad usuarioComunidad = new UsuarioComunidad.UserComuBuilder(comuIntent, userIntent)
                .portal(COMU_REAL_JUAN.getPortal())
                .escalera(COMU_REAL_JUAN.getEscalera())
                .planta(COMU_REAL_JUAN.getPlanta())
                .puerta(COMU_REAL_JUAN.getPuerta())
                .build();

        intended(hasExtra(USERCOMU_LIST_OBJECT.key, usuarioComunidad));
        onView(withId(R.id.usercomu_data_ac_layout)).check(matches(isDisplayed()));

        checkUp(activityLayoutId);
    }

    @Test
    public void testOnListItemClick_4() throws UiException, IOException, InterruptedException
    {
        // Usuario registrado. La búsqueda devuelve una comunidad a la que él NO está asociado.

        regTwoUserComuSameUser(makeListTwoUserComu());
        cleanWithTkhandler();

        // Insertamos al userComu que hace la búsqueda.
        Comunidad comunidadIn = ComuDataTestUtil.makeComunidad("Calle", "de la Torre", (short) 115, "",
                new Municipio((short) 22, new Provincia((short) 2)));
        Usuario usuarioIn = UsuarioDataTestUtils.makeUsuario("newuser@jnew.us", "newuser", "psw_newuser");
        UsuarioComunidad usuarioComunidad = UserComuDataTestUtil.makeUsuarioComunidad(comunidadIn, usuarioIn, null,
                null, "3pl", "A_puerta", PRO.function);
        signUpAndUpdateTk(usuarioComunidad);

        // Búsqueda con comunidad/intent por defecto.
        activity = mIntentRule.launchActivity(intent);
        assertThat(TKhandler.isRegisteredUser(), is(true));

        Thread.sleep(2000);
        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getSupportFragmentManager().findFragmentById(R.id.comu_list_fragment);
        adapter = mComunidadSummaryFrg.mAdapter;
        assertThat(adapter.getCount(), is(1));
        Comunidad comunidad = adapter.getItem(0);
        assertThat(comunidad.getNombreVia(), is("de la Plazuela"));

        onData(is(comunidad)).perform(click());
        onView(withId(R.id.reg_usercomu_ac_layout)).check(matches(isDisplayed()));
        checkUp(activityLayoutId);

        cleanTwoUsers(UsuarioDataTestUtils.USER_JUAN, usuarioIn);
    }

    @Test
    public void testOnListItemClick_5() throws Exception
    {
        whatClean = CLEAN_JUAN_AND_PEPE;

        // Usuario registrado. La búsqueda devuelve una comunidad a la que él NO está asociado.

        signUpAndUpdateTk(UserComuDataTestUtil.COMU_PLAZUELA5_PEPE);
        Comunidad comunidad = userComuDaoRemote.getComusByUser().get(0);
        signUpAndUpdateTk(COMU_REAL_JUAN);

        activity = mIntentRule.launchActivity(intent);
        assertThat(TKhandler.isRegisteredUser(), is(true));

        onData(Matchers.is(COMU_LA_PLAZUELA_5)).perform(click());
        intended(hasExtra(COMUNIDAD_LIST_OBJECT.key, comunidad));
        ViewInteraction viewInteraction = onView(withId(R.id.reg_usercomu_ac_layout)).check(matches(isDisplayed()));

        checkBack(viewInteraction, activityLayoutId);
    }

    @Test
    public void testOnListItemClick_6() throws UiException, IOException, InterruptedException
    {
        whatClean = CLEAN_JUAN;

        // Usuario registrado. La búsqueda devuelve una comunidad que ya no existe.
        regTwoUserComuSameUser(makeListTwoUserComu());

        // Búsqueda con comunidad/intent por defecto.
        activity = mIntentRule.launchActivity(intent);
        assertThat(TKhandler.isRegisteredUser(), is(true));

        Thread.sleep(2000);
        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getSupportFragmentManager().findFragmentById(R.id.comu_list_fragment);
        adapter = mComunidadSummaryFrg.mAdapter;
        assertThat(adapter.getCount(), is(1));
        Comunidad comunidad = adapter.getItem(0);
        assertThat(comunidad.getNombreVia(), is("de la Plazuela"));

        // Borramos la comunidad en BD.
        assertThat(userComuDaoRemote.deleteUserComu(comunidad.getC_Id()), is(1));
        onData(is(comunidad)).perform(click());
        // On-click devuelve a la pantalla de búsqueda de comunidad.
        onView(withId(R.id.comu_search_ac_linearlayout)).check(matches(isDisplayed()));
        checkToastInTest(R.string.comunidad_not_found_message, activity);

        Thread.sleep(2000);
    }
}