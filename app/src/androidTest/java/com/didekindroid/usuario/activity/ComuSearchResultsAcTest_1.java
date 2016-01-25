package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.didekin.serviceone.domain.Comunidad;
import com.didekin.serviceone.domain.Municipio;
import com.didekin.serviceone.domain.Provincia;
import com.didekin.serviceone.domain.Usuario;
import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.UiException;
import com.didekindroid.usuario.activity.utils.CleanUserEnum;
import com.didekindroid.usuario.dominio.DomainDataUtils;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.common.TokenHandler.TKhandler;
import static com.didekindroid.common.utils.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.common.utils.AppKeysForBundle.COMUNIDAD_SEARCH;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.common.utils.ViewsIDs.COMU_SEARCH_RESULTS;
import static com.didekindroid.usuario.activity.utils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.activity.utils.RolCheckBox.PRO;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.cleanOptions;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.cleanTwoUsers;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.cleanWithTkhandler;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.regThreeUserComuSameUser;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.regTwoUserComuSameUser;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.signUpAndUpdateTk;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_LA_PLAZUELA_5;
import static com.didekindroid.usuario.dominio.DomainDataUtils.USER_JUAN;
import static com.didekindroid.usuario.dominio.DomainDataUtils.makeComunidad;
import static com.didekindroid.usuario.dominio.DomainDataUtils.makeListTwoUserComu;
import static com.didekindroid.usuario.dominio.DomainDataUtils.makeUsuario;
import static com.didekindroid.usuario.dominio.DomainDataUtils.makeUsuarioComunidad;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static com.google.android.apps.common.testing.ui.espresso.sample.LongListMatchers.withAdaptedData;
import static com.google.common.base.Preconditions.checkState;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
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
public class ComuSearchResultsAcTest_1 {

    private static final String TAG = "ComunidadSeeActivTest";

    private ComuSearchResultsAc activity;
    ComuSearchResultsListFr mComunidadSummaryFrg;
    Intent intent;
    CleanUserEnum whatClean;

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
        Log.d(TAG, "In getFixture()");
        whatClean = CleanUserEnum.CLEAN_NOTHING;
        intent = new Intent();
        intent.putExtra(COMUNIDAD_SEARCH.extra, COMU_LA_PLAZUELA_5);
    }

    @Test
    public void testOnCreate_0() throws UiException
    {
        whatClean = CLEAN_JUAN;

        // Inserto comunidades en DB.
        regTwoUserComuSameUser(makeListTwoUserComu());
        activity = mActivityRule.launchActivity(intent);

        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        onView(withContentDescription("Navigate up")).check(matches(isDisplayed()));
        onView(CoreMatchers.allOf(withContentDescription(containsString("Navigate up")),
                isClickable())).check(matches(isDisplayed())).perform(click());
    }

    @Test
    public void testOnCreate_2() throws UiException
    {
        whatClean = CLEAN_JUAN;

        // Inserto comunidades en DB.
        regTwoUserComuSameUser(makeListTwoUserComu());
        activity = mActivityRule.launchActivity(intent);
        onView(withId(R.id.comu_search_results_ac_one_pane_frg_container)).check(matches(isDisplayed()));
        onView(withId(R.id.comu_list_frg)).check(matches(isDisplayed()));
    }

    @Test
    public void testSearchComunidades_1() throws UiException
    {
        whatClean = CLEAN_JUAN;

        // User with 2 comunidades. We search with one of them exactly.
        regTwoUserComuSameUser(makeListTwoUserComu());
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));

        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getFragmentManager().findFragmentById(R.id.comu_list_frg);
        ComuSearchResultsListAdapter adapter = (ComuSearchResultsListAdapter) mComunidadSummaryFrg.getListAdapter();
        assertThat(adapter.getCount(), is(1));
        Comunidad comunidad = (Comunidad) intent.getSerializableExtra(COMUNIDAD_SEARCH.extra);
        onView(withId(COMU_SEARCH_RESULTS.idView)).check(matches(
                withAdaptedData(Matchers.<Object>equalTo(comunidad))));
    }

    @Test
    public void testSearchComunidades_2() throws UiException
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
        intent.putExtra(COMUNIDAD_SEARCH.extra, comunidad);
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));

        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getFragmentManager().findFragmentById(R.id.comu_list_frg);
        ComuSearchResultsListAdapter adapter = (ComuSearchResultsListAdapter) mComunidadSummaryFrg.getListAdapter();
        assertThat(adapter.getCount(), is(2));
        onView(withId(COMU_SEARCH_RESULTS.idView)).check(
                matches(withAdaptedData(Matchers.<Object>is(comunidadNew))));
        onView(withId(COMU_SEARCH_RESULTS.idView)).check(
                matches(withAdaptedData(Matchers.<Object>is(COMU_LA_PLAZUELA_5))));
    }

    @Test
    public void testSearchComunidades_3() throws InterruptedException, UiException
    {
        // No existe la comunidad en DB. El usuario no está registrado.
        assertThat(TKhandler.getAccessTokenInCache(), nullValue());

        // Criterio de búsqueda.
        Comunidad comunidad = makeComunidad("Rincón", "del No Existente", (short) 123, "",
                new Municipio((short) 2, new Provincia((short) 27)));
        Intent intent = new Intent();
        intent.putExtra(COMUNIDAD_SEARCH.extra, comunidad);
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));

        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getFragmentManager().findFragmentById(R.id.comu_list_frg);
        ComuSearchResultsListAdapter adapter = (ComuSearchResultsListAdapter) mComunidadSummaryFrg.getListAdapter();
        assertThat(adapter.getCount(), is(0));

        Thread.sleep(2000);
        checkToastInTest(R.string.no_result_search_comunidad, activity);
        onView(withId(R.id.reg_comu_usuario_usuariocomu_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void testSearchComunidades_4() throws InterruptedException, UiException
    {
        whatClean = CLEAN_JUAN;

        // No existe la comunidad en DB. El usuario está registrado.

        signUpAndUpdateTk(DomainDataUtils.COMU_REAL_JUAN);
        // Criterio de búsqueda.
        Comunidad comunidad = makeComunidad("Rincón", "del No Existente", (short) 123, "",
                new Municipio((short) 2, new Provincia((short) 27)));
        Intent intent = new Intent();
        intent.putExtra(COMUNIDAD_SEARCH.extra, comunidad);
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));

        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getFragmentManager().findFragmentById(R.id.comu_list_frg);
        ComuSearchResultsListAdapter adapter = (ComuSearchResultsListAdapter) mComunidadSummaryFrg.getListAdapter();
        assertThat(adapter.getCount(), is(0));

        Thread.sleep(2000);
        checkToastInTest(R.string.no_result_search_comunidad, activity);
        // This is the difference with the not registered user case.
        onView(withId(R.id.reg_comu_and_usercomu_layout)).check(matches(isDisplayed()));
    }


    @Test
    public void testOnListItemClick_1() throws UiException
    {
        whatClean = CLEAN_JUAN;

        //Usuario no registrado. La búsqueda devuelve una comunidad.

        regTwoUserComuSameUser(makeListTwoUserComu());
        // Borro los datos del usuario.
        cleanWithTkhandler();

        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));

        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getFragmentManager().findFragmentById(R.id.comu_list_frg);
        ComuSearchResultsListAdapter adapter = (ComuSearchResultsListAdapter) mComunidadSummaryFrg.getListAdapter();
        assertThat(adapter.getCount(), is(1));
        onView(withAdaptedData(Matchers.<Object>is(COMU_LA_PLAZUELA_5))).check(matches(isDisplayed()));
        onData(is(instanceOf(Comunidad.class))).onChildView(allOf(
                withId(R.id.nombreComunidad_view),
                withText(COMU_LA_PLAZUELA_5.getNombreComunidad())
        )).perform(click());

        onView(withId(R.id.reg_user_and_usercomu_ac_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void testOnListItemClick_2() throws UiException
    {
        whatClean = CLEAN_JUAN;

        // Usuario registrado. La búsqueda devuelve una comunidad a la que él ya está asociado.

        regTwoUserComuSameUser(makeListTwoUserComu());
        activity = mActivityRule.launchActivity(intent);
        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getFragmentManager().findFragmentById(R.id.comu_list_frg);
        ComuSearchResultsListAdapter adapter = (ComuSearchResultsListAdapter) mComunidadSummaryFrg.getListAdapter();
        assertThat(adapter.getCount(), is(1));

        Comunidad comunidad = adapter.getItem(0);
        onView(withAdaptedData(Matchers.<Object>equalTo(comunidad))).check(matches(isDisplayed()));
        onData(is(instanceOf(Comunidad.class))).onChildView(allOf(
                withId(R.id.nombreComunidad_view),
                withText(COMU_LA_PLAZUELA_5.getNombreComunidad())
        )).perform(click());
        onView(withId(R.id.usercomu_data_ac_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void testOnListItemClick_3() throws UiException
    {
        // Usuario registrado. La búsqueda devuelve una comunidad a la que él NO está asociado.

        regTwoUserComuSameUser(makeListTwoUserComu());
        cleanWithTkhandler();

        // Insertamos al usuario que hace la búsqueda.
        Comunidad comunidadIn = makeComunidad("Calle", "de la Torre", (short) 115, "",
                new Municipio((short) 22, new Provincia((short) 2)));
        Usuario usuarioIn = makeUsuario("newuser@jnew.us", "newuser", "psw_newuser");
        UsuarioComunidad usuarioComunidad = makeUsuarioComunidad(comunidadIn, usuarioIn, null,
                null, "3pl", "A_puerta", PRO.function);
        signUpAndUpdateTk(usuarioComunidad);

        // Búsqueda con comunidad/intent por defecto.
        activity = mActivityRule.launchActivity(intent);
        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getFragmentManager().findFragmentById(R.id.comu_list_frg);
        ComuSearchResultsListAdapter adapter = (ComuSearchResultsListAdapter) mComunidadSummaryFrg.getListAdapter();
        assertThat(adapter.getCount(), is(1));
        Comunidad comunidad = adapter.getItem(0);
        assertThat(comunidad.getNombreVia(),is("de la Plazuela"));

        onData(is(comunidad)).perform(click());
        onView(withId(R.id.reg_usercomu_ac_layout)).check(matches(isDisplayed()));

        cleanTwoUsers(USER_JUAN, usuarioIn);
    }

    @Test
    public void testOnListItemClick_4() throws UiException
    {
        whatClean = CLEAN_JUAN;

        // Usuario registrado. La búsqueda devuelve una comunidad que ya no existe.
        regTwoUserComuSameUser(makeListTwoUserComu());

        // Búsqueda con comunidad/intent por defecto.
        activity = mActivityRule.launchActivity(intent);
        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getFragmentManager().findFragmentById(R.id.comu_list_frg);
        ComuSearchResultsListAdapter adapter = (ComuSearchResultsListAdapter) mComunidadSummaryFrg.getListAdapter();
        assertThat(adapter.getCount(), is(1));
        Comunidad comunidad = adapter.getItem(0);
        assertThat(comunidad.getNombreVia(), is("de la Plazuela"));

        // Borramos la comunidad en BD.
        assertThat(ServOne.deleteUserComu(comunidad.getC_Id()), is(1));
        onData(is(comunidad)).perform(click());
        onView(withId(R.id.comu_search_ac_layout)).check(matches(isDisplayed()));
        checkToastInTest(R.string.comunidad_not_found_message, activity);
    }

    @After
    public void cleanData() throws UiException
    {
        cleanOptions(whatClean);
        checkState(!isRegisteredUser(activity));
    }
}