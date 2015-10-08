package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import com.didekin.serviceone.domain.*;
import com.didekindroid.R;
import com.didekindroid.usuario.activity.utils.CleanEnum;
import com.didekindroid.usuario.dominio.DomainDataUtils;
import com.didekindroid.usuario.security.TokenHandler;
import com.google.common.base.Preconditions;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.usuario.activity.utils.CleanEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.activity.utils.RolCheckBox.PROPIETARIO;
import static com.didekindroid.uiutils.UIutils.isRegisteredUser;
import static com.didekindroid.uiutils.ViewsIDs.COMU_SEARCH_RESULTS;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.*;
import static com.didekindroid.usuario.dominio.DomainDataUtils.*;
import static com.didekindroid.usuario.security.TokenHandler.TKhandler;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.COMUNIDAD_SEARCH;
import static com.didekindroid.usuario.activity.utils.UserMenuTestUtils.*;
import static com.google.android.apps.common.testing.ui.espresso.sample.LongListMatchers.withAdaptedData;
import static com.google.common.base.Preconditions.checkState;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/05/15
 * Time: 15:00
 */
@RunWith(AndroidJUnit4.class)
public class ComuSearchResultsAcTest {

    private static final String TAG = "ComunidadSeeActivTest";

    private ComuSearchResultsAc activity;
    ComuSearchResultsListFr mComunidadSummaryFrg;
    Intent intent;
    CleanEnum whatClean;

    @Rule
    public ActivityTestRule<ComuSearchResultsAc> mActivityRule =
            new ActivityTestRule<>(ComuSearchResultsAc.class, true, false);

    @Before
    public void getFixture() throws Exception
    {
        Log.d(TAG, "In getFixture()");
        whatClean = CleanEnum.CLEAN_NOTHING;
        intent = new Intent();
        intent.putExtra(COMUNIDAD_SEARCH.extra, COMU_LA_PLAZUELA_5);
    }

    @Test
    public void testOnCreate_1() throws Exception
    {
        activity = mActivityRule.launchActivity(intent);
        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getFragmentManager().findFragmentById(R.id.comu_list_frg);

        assertThat(activity, notNullValue());
        assertThat(mComunidadSummaryFrg, notNullValue());

        // No results in DB. The user is invited to register.
        checkToastInTest(R.string.no_result_search_comunidad, activity);

        onView(withId(R.id.reg_comu_usuario_usuariocomu_layout)).check(matches(isDisplayed()));

        Thread.sleep(4000);
    }

    @Test
    public void testOnCreate_2()
    {
        whatClean = CLEAN_JUAN;

        // Inserto comunidades en DB.
        regTwoUserComuSameUser(makeListTwoUserComu());
        activity = mActivityRule.launchActivity(intent);
        onView(withId(R.id.comu_search_results_ac_one_pane_frg_container)).check(matches(isDisplayed()));
        onView(withId(R.id.comu_list_frg)).check(matches(isDisplayed()));
    }

    @Test
    public void testComunidadesUsuarioGetter_1()
    {
        // No token in cache.
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));
        assertThat(activity.mUsuarioComunidades, nullValue());
    }

    @Test
    public void testComunidadesUsuarioGetter_2()
    {
        whatClean = CLEAN_JUAN;

        // Usuario registrado.
        regTwoUserComuSameUser(makeListTwoUserComu());

        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));
        assertThat(activity.mUsuarioComunidades.size(), is(2));
        assertThat(activity.mUsuarioComunidades, hasItems(DomainDataUtils.COMU_REAL, COMU_LA_PLAZUELA_5));
    }

    @Test
    public void testGetDatosUsuarioNoToken_1() throws InterruptedException
    {
        assertThat(TKhandler.getAccessTokenInCache(),nullValue());
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));

        checkToastInTest(R.string.no_result_search_comunidad, activity);

        onView(withId(R.id.reg_comu_usuario_usuariocomu_layout)).check(matches(isDisplayed()));

        Thread.sleep(4000);
    }

    @Test
    public void testGetDatosUsuarioNoToken_2() throws InterruptedException
    {
        whatClean = CLEAN_JUAN;

        //Usuario no registrado. La búsqueda devuelve una comunidad.
        regTwoUserComuSameUser(makeListTwoUserComu());
        // Borro los datos del token.
        cleanWithTkhandler();

        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));

        USER_DATA_AC.checkMenuItem_NTk(activity);
    }

    @Test
    public void testGetDatosUsuarioWithToken() throws InterruptedException
    {
        whatClean = CLEAN_JUAN;

        //With token.
        regTwoUserComuSameUser(makeListTwoUserComu());
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));
        USER_DATA_AC.checkMenuItem_WTk(activity);
    }

    @Test
    public void testMenuNuevaComunidad_noToken_1() throws InterruptedException
    {
        assertThat(TKhandler.getAccessTokenInCache(),nullValue());
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));

        // No results in DB. The user is invited to register.
        checkToastInTest(R.string.no_result_search_comunidad, activity);

        onView(withId(R.id.reg_comu_usuario_usuariocomu_layout)).check(matches(isDisplayed()));

        Thread.sleep(4000);
    }

    @Test
    public void testMenuNuevaComunidad_noToken_2() throws InterruptedException
    {
        whatClean = CLEAN_JUAN;

        //Usuario no registrado. La búsqueda devuelve una comunidad.
        regTwoUserComuSameUser(makeListTwoUserComu());
        // Borro los datos del usuario.
        cleanWithTkhandler();

        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));

        REG_COMU_USER_USERCOMU_AC.checkMenuItem_NTk(activity);
    }

    @Test
    public void testMenuNuevaComunidad_withToken() throws InterruptedException
    {
        whatClean = CLEAN_JUAN;

        regTwoUserComuSameUser(makeListTwoUserComu());
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));

        REG_COMU_USER_USERCOMU_AC.checkMenuItem_WTk(activity);
    }

    @Test
    public void testComunidadesByUsuario_withToken() throws InterruptedException
    {
        whatClean = CLEAN_JUAN;

        regTwoUserComuSameUser(makeListTwoUserComu());
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));
        SEE_USERCOMU_BY_USER_AC.checkMenuItem_WTk(activity);
    }

    @Test
    public void tesComunidadesByUsuario_noToken_1() throws InterruptedException
    {
        assertThat(TKhandler.getAccessTokenInCache(),nullValue());
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));

        // No results in DB. The user is invited to register.
        checkToastInTest(R.string.no_result_search_comunidad, activity);

        onView(withId(R.id.reg_comu_usuario_usuariocomu_layout)).check(matches(isDisplayed()));

        Thread.sleep(4000);
    }

    @Test
    public void tesComunidadesByUsuario_noToken_2() throws InterruptedException
    {
        whatClean = CLEAN_JUAN;

        //Usuario no registrado. La búsqueda devuelve una comunidad.
        regTwoUserComuSameUser(makeListTwoUserComu());
        // Borro los datos del usuario.
        cleanWithTkhandler();
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));

        SEE_USERCOMU_BY_USER_AC.checkMenuItem_NTk(activity);
    }

    @Test
    public void testSearchComunidades_1()
    {
        whatClean = CLEAN_JUAN;

        // User with 2 comunidades. We search with one of them exactly.
        regTwoUserComuSameUser(makeListTwoUserComu());
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));

        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getFragmentManager().findFragmentById(R.id.comu_list_frg);
        ComuSearchResultsListAdapter adapter = (ComuSearchResultsListAdapter) mComunidadSummaryFrg.getListAdapter();
        assertThat(adapter.getCount(), is(1));
        onView(withId(COMU_SEARCH_RESULTS.idView)).check(matches(
                withAdaptedData(equalTo((Object) intent.getSerializableExtra(COMUNIDAD_SEARCH.extra)))));
    }

    @Test
    public void testSearchComunidades_2()
    {
        whatClean = CLEAN_JUAN;

        // Caso: existen dos comunidades para el criterio de búsqueda.

        Comunidad comunidadNew = makeComunidad("Ronda", "del Norte", (short) 5, "",
                new Municipio((short) 2,new Provincia((short) 27)));
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
                matches(withAdaptedData(hasProperty("nombreVia", is("del Norte")))));
        onView(withId(COMU_SEARCH_RESULTS.idView)).check(
                matches(withAdaptedData(hasProperty("nombreVia", is("de la Plazuela")))));
    }

    @Test
    public void testSearchComunidades_3() throws InterruptedException
    {
        // No existe la comunidad en DB. El usuario no está registrado.
        assertThat(TKhandler.getAccessTokenInCache(),nullValue());

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

        checkToastInTest(R.string.no_result_search_comunidad, activity);

        onView(withId(R.id.reg_comu_usuario_usuariocomu_layout)).check(matches(isDisplayed()));

        Thread.sleep(4000);
    }

    @Test
    public void testSearchComunidades_4() throws InterruptedException
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

        checkToastInTest(R.string.no_result_search_comunidad, activity);

        // This is the difference with the not registered user case.
        onView(withId(R.id.reg_comu_and_usercomu_layout)).check(matches(isDisplayed()));

        Thread.sleep(4000);
    }

    @Test
    public void testOnListItemClick_1()
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
        onView(withAdaptedData(hasProperty("nombreVia", is("de la Plazuela")))).check(matches(isDisplayed()));

        onData(allOf(is(instanceOf(Comunidad.class)), hasProperty("nombreVia", is("de la Plazuela")))).perform(click());
        onView(withId(R.id.reg_user_and_usercomu_ac_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void testOnListItemClick_2()
    {
        whatClean = CLEAN_JUAN;

        // Usuario registrado. La búsqueda devuelve una comunidad a la que él ya está asociado.

        regTwoUserComuSameUser(makeListTwoUserComu());
        activity = mActivityRule.launchActivity(intent);
        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getFragmentManager().findFragmentById(R.id.comu_list_frg);
        ComuSearchResultsListAdapter adapter = (ComuSearchResultsListAdapter) mComunidadSummaryFrg.getListAdapter();
        assertThat(adapter.getCount(), is(1));

        Comunidad comunidadInAdapter = adapter.getItem(0);
        onView(withAdaptedData(Matchers.<Object>equalTo(comunidadInAdapter))).check(matches(isDisplayed()));
        onData(allOf(is(instanceOf(Comunidad.class)), hasProperty("nombreVia", is("de la Plazuela")))).perform(click());

        onView(withId(R.id.see_usercomu_by_user_ac_frg_container)).check(matches(isDisplayed()));
    }

    @Test
    public void testOnListItemClick_3()
    {
        // Usuario registrado. La búsqueda devuelve una comunidad a la que él NO está asociado.

        regTwoUserComuSameUser(makeListTwoUserComu());
        cleanWithTkhandler();

        // Insertamos al usuario que hace la búsqueda.
        Comunidad comunidadIn = makeComunidad("Calle", "de la Torre", (short) 115, "",
                new Municipio((short) 22, new Provincia((short) 2)));
        Usuario usuarioIn = makeUsuario("newuser@jnew.us", "newuser", "psw_newuser", (short) 34, 600151515);
        UsuarioComunidad usuarioComunidad = makeUsuarioComunidad(comunidadIn, usuarioIn, null,
                null, "3pl", "A_puerta", PROPIETARIO.function);
        signUpAndUpdateTk(usuarioComunidad);

        // Búsqueda con comunidad/intent por defecto.
        activity = mActivityRule.launchActivity(intent);
        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getFragmentManager().findFragmentById(R.id.comu_list_frg);
        ComuSearchResultsListAdapter adapter = (ComuSearchResultsListAdapter) mComunidadSummaryFrg.getListAdapter();
        assertThat(adapter.getCount(), is(1));

        onData(allOf(is(instanceOf(Comunidad.class)), hasProperty("nombreVia", is("de la Plazuela")))).perform(click());
        onView(withId(R.id.reg_usercomu_ac_layout)).check(matches(isDisplayed()));

        cleanTwoUsers(USER_JUAN, usuarioIn);
    }

    @After
    public void cleanData()
    {
        cleanOptions(whatClean);
        checkState(!isRegisteredUser(activity.getBaseContext()));
    }
}