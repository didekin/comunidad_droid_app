package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.usuario.dominio.Comunidad;
import com.didekin.usuario.dominio.Municipio;
import com.didekin.usuario.dominio.Provincia;
import com.didekin.usuario.dominio.Usuario;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.usuario.testutils.CleanUserEnum;
import com.didekindroid.usuario.testutils.UsuarioTestUtils;

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
import static com.didekindroid.common.activity.BundleKey.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.common.activity.BundleKey.COMUNIDAD_SEARCH;
import static com.didekindroid.common.activity.BundleKey.USERCOMU_LIST_OBJECT;
import static com.didekindroid.common.activity.TokenHandler.TKhandler;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkUp;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanTwoUsers;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanWithTkhandler;
import static com.didekindroid.common.testutils.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.common.testutils.ActivityTestUtils.makeListTwoUserComu;
import static com.didekindroid.common.testutils.ActivityTestUtils.regThreeUserComuSameUser;
import static com.didekindroid.common.testutils.ActivityTestUtils.regTwoUserComuSameUser;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.external.LongListMatchers.withAdaptedData;
import static com.didekindroid.usuario.activity.utils.RolUi.PRO;
import static com.didekindroid.usuario.activity.utils.UsuarioFragmentTags.comu_search_results_list_fr_tag;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_LA_PLAZUELA_5;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_PLAZUELA5_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_REAL;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_REAL_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.makeComunidad;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.makeUsuario;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.makeUsuarioComunidad;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/05/15
 * Time: 15:00
 */
@RunWith(AndroidJUnit4.class)
public class ComuSearchResultsAc_1_Test {

    private ComuSearchResultsAc activity;
    ComuSearchResultsListFr mComunidadSummaryFrg;
    ComuSearchResultsListAdapter adapter;
    Intent intent;
    CleanUserEnum whatClean = CleanUserEnum.CLEAN_NOTHING;

    int activityLayoutId = R.id.comu_list_frg;

    @Rule
    public IntentsTestRule<ComuSearchResultsAc> mIntentRule =
            new IntentsTestRule<>(ComuSearchResultsAc.class, true, false);

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(4000);
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
        assertThat(isRegisteredUser(activity), is(false));
    }

    @Test
    public void testOnCreate_0() throws UiException, IOException
    {
        whatClean = CLEAN_JUAN;

        // Inserto comunidades en DB.
        regTwoUserComuSameUser(makeListTwoUserComu());
        activity = mIntentRule.launchActivity(intent);
        onView(withId(R.id.comu_list_frg)).check(matches(isDisplayed()));

        clickNavigateUp();
    }

    @Test
    public void testSearchComunidades_1() throws UiException, IOException, InterruptedException
    {
        whatClean = CLEAN_JUAN;

        // User with 2 comunidades. We search with one of them exactly.
        regTwoUserComuSameUser(makeListTwoUserComu());
        activity = mIntentRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));

        Thread.sleep(2000);
        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getSupportFragmentManager().findFragmentByTag(comu_search_results_list_fr_tag);
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

        Comunidad comunidadNew = makeComunidad("Ronda", "del Norte", (short) 5, "",
                new Municipio((short) 2, new Provincia((short) 27)));
        regThreeUserComuSameUser(makeListTwoUserComu(), comunidadNew);

        // Criterio de búsqueda.
        Comunidad comunidad = makeComunidad("Ronda", "de la Plazuela del Norte", (short) 5, "",
                new Municipio((short) 2, new Provincia((short) 27)));
        Intent intent = new Intent();
        intent.putExtra(COMUNIDAD_SEARCH.key, comunidad);
        activity = mIntentRule.launchActivity(intent);

        Thread.sleep(2000);
        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getSupportFragmentManager().findFragmentByTag(comu_search_results_list_fr_tag);
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

        assertThat(TKhandler.getAccessTokenInCache(), nullValue());

        // Criterio de búsqueda.
        Comunidad comunidad = makeComunidad("Rincón", "del No Existente", (short) 123, "",
                new Municipio((short) 2, new Provincia((short) 27)));
        Intent intent = new Intent();
        intent.putExtra(COMUNIDAD_SEARCH.key, comunidad);

        activity = mIntentRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));
        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getSupportFragmentManager().findFragmentByTag(comu_search_results_list_fr_tag);
        assertThat(mComunidadSummaryFrg, nullValue());

        checkToastInTest(R.string.no_result_search_comunidad, activity);
        // Presenta registro de usuario y comunidad.
        onView(withId(R.id.reg_comu_usuario_usuariocomu_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void testSearchComunidades_4() throws InterruptedException, UiException, IOException
    {
        whatClean = CLEAN_JUAN;

        /* 1. No existe la comunidad en DB. 2. El usuario está registrado.*/
        signUpAndUpdateTk(UsuarioTestUtils.COMU_REAL_JUAN);

        // Criterio de búsqueda.
        Comunidad comunidad = makeComunidad("Rincón", "del No Existente", (short) 123, "",
                new Municipio((short) 2, new Provincia((short) 27)));
        Intent intent = new Intent();
        intent.putExtra(COMUNIDAD_SEARCH.key, comunidad);

        activity = mIntentRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));
        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getSupportFragmentManager().findFragmentByTag(comu_search_results_list_fr_tag);
        assertThat(mComunidadSummaryFrg, nullValue());

        checkToastInTest(R.string.no_result_search_comunidad, activity);
        // Presenta registro de usuarioComunidad y comunidad.
        onView(withId(R.id.reg_comu_and_usercomu_layout)).check(matches(isDisplayed()));
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
        assertThat(isRegisteredUser(activity), is(false));

        Thread.sleep(2000);
        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getSupportFragmentManager().findFragmentByTag(comu_search_results_list_fr_tag);
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
        assertThat(isRegisteredUser(activity), is(true));

        Thread.sleep(2000);
        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getSupportFragmentManager().findFragmentByTag(comu_search_results_list_fr_tag);
        adapter = mComunidadSummaryFrg.mAdapter;
        assertThat(adapter.getCount(), is(1));

        Comunidad comunidad = adapter.getItem(0);
        onView(withAdaptedData(Matchers.<Object>equalTo(comunidad))).check(matches(isDisplayed()));
        onData(is(instanceOf(Comunidad.class))).onChildView(
                allOf(
                withId(R.id.nombreComunidad_view),
                withText(COMU_LA_PLAZUELA_5.getNombreComunidad())
        )).perform(click());

        onView(withId(R.id.usercomu_data_ac_layout)).check(matches(isDisplayed()));

        checkUp(activityLayoutId);
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
        Comunidad comuIntent = ServOne.getComusByUser().get(0);

        activity = mIntentRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));

        onData(is(COMU_REAL)).perform(click());

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
        Comunidad comunidadIn = makeComunidad("Calle", "de la Torre", (short) 115, "",
                new Municipio((short) 22, new Provincia((short) 2)));
        Usuario usuarioIn = makeUsuario("newuser@jnew.us", "newuser", "psw_newuser");
        UsuarioComunidad usuarioComunidad = makeUsuarioComunidad(comunidadIn, usuarioIn, null,
                null, "3pl", "A_puerta", PRO.function);
        signUpAndUpdateTk(usuarioComunidad);

        // Búsqueda con comunidad/intent por defecto.
        activity = mIntentRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));

        Thread.sleep(2000);
        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getSupportFragmentManager().findFragmentByTag(comu_search_results_list_fr_tag);
        adapter = mComunidadSummaryFrg.mAdapter;
        assertThat(adapter.getCount(), is(1));
        Comunidad comunidad = adapter.getItem(0);
        assertThat(comunidad.getNombreVia(),is("de la Plazuela"));

        onData(is(comunidad)).perform(click());
        onView(withId(R.id.reg_usercomu_ac_layout)).check(matches(isDisplayed()));

        checkUp(activityLayoutId);

        cleanTwoUsers(USER_JUAN, usuarioIn);
    }

    @Test
    public void testOnListItemClick_5() throws Exception
    {
        whatClean = CLEAN_JUAN_AND_PEPE;

        // Usuario registrado. La búsqueda devuelve una comunidad a la que él NO está asociado.

        signUpAndUpdateTk(COMU_PLAZUELA5_PEPE);
        Comunidad comunidad = ServOne.getComusByUser().get(0);
        signUpAndUpdateTk(COMU_REAL_JUAN);

        activity = mIntentRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));

        onData(is(COMU_LA_PLAZUELA_5)).perform(click());
        intended(hasExtra(COMUNIDAD_LIST_OBJECT.key,comunidad));
        onView(withId(R.id.reg_usercomu_ac_layout)).check(matches(isDisplayed()));

        checkUp(activityLayoutId);
    }

    @Test
    public void testOnListItemClick_6() throws UiException, IOException, InterruptedException
    {
        whatClean = CLEAN_JUAN;

        // Usuario registrado. La búsqueda devuelve una comunidad que ya no existe.
        regTwoUserComuSameUser(makeListTwoUserComu());

        // Búsqueda con comunidad/intent por defecto.
        activity = mIntentRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));

        Thread.sleep(2000);
        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getSupportFragmentManager().findFragmentByTag(comu_search_results_list_fr_tag);
        adapter = mComunidadSummaryFrg.mAdapter;
        assertThat(adapter.getCount(), is(1));
        Comunidad comunidad = adapter.getItem(0);
        assertThat(comunidad.getNombreVia(), is("de la Plazuela"));

        // Borramos la comunidad en BD.
        assertThat(ServOne.deleteUserComu(comunidad.getC_Id()), is(1));
        onData(is(comunidad)).perform(click());
        // On-click devuelve a la pantalla de búsqueda de comunidad.
        onView(withId(R.id.comu_search_ac_linearlayout)).check(matches(isDisplayed()));
        checkToastInTest(R.string.comunidad_not_found_message, activity);
    }
}