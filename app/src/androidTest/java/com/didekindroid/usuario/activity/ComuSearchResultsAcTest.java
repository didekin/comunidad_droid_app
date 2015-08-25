package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import com.didekindroid.DidekindroidApp;
import com.didekindroid.R;
import com.didekindroid.masterdata.dominio.Municipio;
import com.didekindroid.masterdata.dominio.Provincia;
import com.didekindroid.usuario.dominio.Comunidad;
import com.didekindroid.usuario.dominio.Usuario;
import com.didekindroid.usuario.dominio.UsuarioComunidad;
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
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.didekindroid.common.ui.UIutils.isRegisteredUser;
import static com.didekindroid.common.ui.UIutils.updateIsRegistered;
import static com.didekindroid.common.ui.ViewsIDs.COMUNIDADES_FOUND;
import static com.didekindroid.usuario.common.DataUsuarioTestUtils.*;
import static com.didekindroid.usuario.common.TokenHandler.TKhandler;
import static com.didekindroid.usuario.common.UserIntentExtras.COMUNIDAD_SEARCH;
import static com.didekindroid.usuario.common.UserMenuTest.*;
import static com.didekindroid.common.dominio.Rol.PROPIETARIO;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static com.google.android.apps.common.testing.ui.espresso.sample.LongListMatchers.withAdaptedData;
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
    ComuListFr mComunidadSummaryFrg;
    File refreshTkFile;
    Intent intent;

    @Rule
    public ActivityTestRule<ComuSearchResultsAc> mActivityRule =
            new ActivityTestRule<>(ComuSearchResultsAc.class, true, false);

    @Before
    public void getFixture() throws Exception
    {
        Log.d(TAG, "In getFixture()");
        refreshTkFile = TKhandler.getRefreshTokenFile();
        Comunidad comunidad = new Comunidad("Ronda", "de la Plazuela", (short) 5, null,
                new Municipio(new Provincia((short) 27), (short) 2));
        intent = new Intent();
        intent.putExtra(COMUNIDAD_SEARCH.extra, comunidad);
    }

    @Test
    public void testOnCreate_1() throws Exception
    {
        activity = mActivityRule.launchActivity(intent);
        mComunidadSummaryFrg = (ComuListFr) activity.getFragmentManager().findFragmentById(R.id.comu_list_frg);

        assertThat(activity, notNullValue());
        assertThat(mComunidadSummaryFrg, notNullValue());

        // No results in DB. The user is invited to register.
        ViewInteraction toastViewInteraction = onView(withText(
                containsString(activity.getResources().getText(R.string.no_result_search_comunidad).toString())
        ));
        toastViewInteraction.inRoot(withDecorView(not(activity.getWindow().getDecorView())))
                .check(matches(isDisplayed()));
        onView(withId(R.id.reg_comu_usuario_usuariocomu_layout)).check(matches(isDisplayed()));

        Thread.sleep(4000);
    }

    @Test
    public void testOnCreate_2()
    {
        // Inserto comunidades en DB.
        regComuAndUserComuWith2Comu(makeListTwoUserComu());
        activity = mActivityRule.launchActivity(intent);
        onView(withId(R.id.comu_search_results_ac_one_pane_frg_container)).check(matches(isDisplayed()));
        onView(withId(R.id.comu_list_frg)).check(matches(isDisplayed()));

        // User cleanup.
        boolean isDeleted = ServOne.deleteUser();
        assertThat(isDeleted, is(true));
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
        // Usuario registrado.
        regComuAndUserComuWith2Comu(makeListTwoUserComu());

        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));
        assertThat(activity.mUsuarioComunidades.size(), is(2));
        assertThat(activity.mUsuarioComunidades, hasItems(COMUNIDAD_1, COMUNIDAD_2));

        // User cleanup.
        boolean isDeleted = ServOne.deleteUser();
        assertThat(isDeleted, is(true));
    }

    @Test
    public void testGetDatosUsuarioNoToken_1() throws InterruptedException
    {
        assertThat(refreshTkFile.exists(), is(false));
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));

        // No results in DB. The user is invited to register.
        ViewInteraction toastViewInteraction = onView(withText(
                containsString(activity.getResources().getText(R.string.no_result_search_comunidad).toString())
        ));
        toastViewInteraction.inRoot(withDecorView(not(activity.getWindow().getDecorView())))
                .check(matches(isDisplayed()));
        onView(withId(R.id.reg_comu_usuario_usuariocomu_layout)).check(matches(isDisplayed()));

        Thread.sleep(4000);
    }

    @Test
    public void testGetDatosUsuarioNoToken_2() throws InterruptedException
    {
        //Usuario no registrado. La búsqueda devuelve una comunidad.
        regComuAndUserComuWith2Comu(makeListTwoUserComu());
        // Borro los datos del usuario.
        cleanData();
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));

        USER_DATA_AC.checkMenuItem_NTk(activity);

        // User cleanup. We update user credentiasl first.
        updateSecurityData(USUARIO_1.getUserName(), "psw_juan");
        boolean isDeleted = ServOne.deleteUser();
        assertThat(isDeleted, is(true));
    }

    @Test
    public void testGetDatosUsuarioWithToken() throws InterruptedException
    {
        //With token.
        regComuAndUserComuWith2Comu(makeListTwoUserComu());
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));
        USER_DATA_AC.checkMenuItem_WTk(activity);
    }

    @Test
    public void testMenuNuevaComunidad_noToken_1() throws InterruptedException
    {
        assertThat(refreshTkFile.exists(), is(false));
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));

        // No results in DB. The user is invited to register.
        ViewInteraction toastViewInteraction = onView(withText(
                containsString(activity.getResources().getText(R.string.no_result_search_comunidad).toString())
        ));
        toastViewInteraction.inRoot(withDecorView(not(activity.getWindow().getDecorView())))
                .check(matches(isDisplayed()));
        onView(withId(R.id.reg_comu_usuario_usuariocomu_layout)).check(matches(isDisplayed()));
        Thread.sleep(4000);
    }

    @Test
    public void testMenuNuevaComunidad_noToken_2() throws InterruptedException
    {
        //Usuario no registrado. La búsqueda devuelve una comunidad.
        regComuAndUserComuWith2Comu(makeListTwoUserComu());
        // Borro los datos del usuario.
        cleanData();
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));

        REG_COMU_USER_USERCOMU_AC.checkMenuItem_NTk(activity);

        // User cleanup. We update user credentiasl first.
        updateSecurityData(USUARIO_1.getUserName(), "psw_juan");
        boolean isDeleted = ServOne.deleteUser();
        assertThat(isDeleted, is(true));
    }

    @Test
    public void testMenuNuevaComunidad_withToken() throws InterruptedException
    {
        regComuAndUserComuWith2Comu(makeListTwoUserComu());
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));
        REG_COMU_USER_USERCOMU_AC.checkMenuItem_WTk(activity);
    }

    @Test
    public void testComunidadesByUsuario_withToken() throws InterruptedException
    {
        regComuAndUserComuWith2Comu(makeListTwoUserComu());
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));
        COMU_BY_USER_LIST_AC.checkMenuItem_WTk(activity);
    }

    @Test
    public void tesComunidadesByUsuario_noToken_1() throws InterruptedException
    {
        assertThat(refreshTkFile.exists(), is(false));
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));

        // No results in DB. The user is invited to register.
        ViewInteraction toastViewInteraction = onView(withText(
                containsString(activity.getResources().getText(R.string.no_result_search_comunidad).toString())
        ));
        toastViewInteraction.inRoot(withDecorView(not(activity.getWindow().getDecorView())))
                .check(matches(isDisplayed()));
        onView(withId(R.id.reg_comu_usuario_usuariocomu_layout)).check(matches(isDisplayed()));

        Thread.sleep(4000);
    }

    @Test
    public void tesComunidadesByUsuario_noToken_2() throws InterruptedException
    {
        //Usuario no registrado. La búsqueda devuelve una comunidad.
        regComuAndUserComuWith2Comu(makeListTwoUserComu());
        // Borro los datos del usuario.
        cleanData();
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));

        COMU_BY_USER_LIST_AC.checkMenuItem_NTk(activity);

        // User cleanup. We update user credentiasl first.
        updateSecurityData(USUARIO_1.getUserName(), "psw_juan");
        boolean isDeleted = ServOne.deleteUser();
        assertThat(isDeleted, is(true));
    }

    @Test
    public void testSearchComunidades_1()
    {
        // User with 2 comunidades. We search with one of them exactly.
        regComuAndUserComuWith2Comu(makeListTwoUserComu());
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));
        mComunidadSummaryFrg = (ComuListFr) activity.getFragmentManager().findFragmentById(R.id.comu_list_frg);
        ComuListAdapter adapter = (ComuListAdapter) mComunidadSummaryFrg.getListAdapter();

        assertThat(adapter.getCount(), is(1));
        onView(withId(COMUNIDADES_FOUND.idView)).check(
                matches(withAdaptedData(
                        equalTo((Object) intent.getSerializableExtra(COMUNIDAD_SEARCH.extra)))));

        // User cleanup.
        boolean isDeleted = ServOne.deleteUser();
        assertThat(isDeleted, is(true));
    }

    @Test
    public void testSearchComunidades_2()
    {
        // Caso: existen dos comunidades para el criterio de búsqueda.

        Comunidad comunidadNew = new Comunidad("Ronda", "del Norte", (short) 5, null,
                new Municipio(new Provincia((short) 27), (short) 2));
        regComuAndUserComuWith3Comu(makeListTwoUserComu(), comunidadNew);

        // Criterio de búsqueda.
        Comunidad comunidad = new Comunidad("Ronda", "de la Plazuela del Norte", (short) 5, null,
                new Municipio(new Provincia((short) 27), (short) 2));
        Intent intent = new Intent();
        intent.putExtra(COMUNIDAD_SEARCH.extra, comunidad);
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));

        mComunidadSummaryFrg = (ComuListFr) activity.getFragmentManager().findFragmentById(R.id.comu_list_frg);
        ComuListAdapter adapter = (ComuListAdapter) mComunidadSummaryFrg.getListAdapter();
        assertThat(adapter.getCount(), is(2));
        onView(withId(COMUNIDADES_FOUND.idView)).check(
                matches(withAdaptedData(hasProperty("nombreVia", is("del Norte")))));
        onView(withId(COMUNIDADES_FOUND.idView)).check(
                matches(withAdaptedData(hasProperty("nombreVia", is("de la Plazuela")))));

        // User cleanup.
        boolean isDeleted = ServOne.deleteUser();
        assertThat(isDeleted, is(true));
    }

    @Test
    public void testSearchComunidades_3() throws InterruptedException
    {
        // No existe la comunidad en DB. El usuario no está registrado.
        assertThat(refreshTkFile.exists(), is(false));
        // Criterio de búsqueda.
        Comunidad comunidad = new Comunidad("Rincón", "del No Existente", (short) 123, null,
                new Municipio(new Provincia((short) 27), (short) 2));
        Intent intent = new Intent();
        intent.putExtra(COMUNIDAD_SEARCH.extra, comunidad);
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));
        mComunidadSummaryFrg = (ComuListFr) activity.getFragmentManager().findFragmentById(R.id.comu_list_frg);
        ComuListAdapter adapter = (ComuListAdapter) mComunidadSummaryFrg.getListAdapter();
        assertThat(adapter.getCount(), is(0));

        ViewInteraction toastViewInteraction = onView(withText(
                containsString(activity.getResources().getText(R.string.no_result_search_comunidad).toString())
        ));
        toastViewInteraction.inRoot(withDecorView(not(activity.getWindow().getDecorView())))
                .check(matches(isDisplayed()));

        onView(withId(R.id.reg_comu_usuario_usuariocomu_layout)).check(matches(isDisplayed()));

        Thread.sleep(4000);
    }

    @Test
    public void testSearchComunidades_4() throws InterruptedException
    {
        // No existe la comunidad en DB. El usuario está registrado.
        signUpAndUpdateTk(USUARIO_COMUNIDAD_1);
        // Criterio de búsqueda.
        Comunidad comunidad = new Comunidad("Rincón", "del No Existente", (short) 123, null,
                new Municipio(new Provincia((short) 27), (short) 2));
        Intent intent = new Intent();
        intent.putExtra(COMUNIDAD_SEARCH.extra, comunidad);
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));

        mComunidadSummaryFrg = (ComuListFr) activity.getFragmentManager().findFragmentById(R.id.comu_list_frg);
        ComuListAdapter adapter = (ComuListAdapter) mComunidadSummaryFrg.getListAdapter();
        assertThat(adapter.getCount(), is(0));

        ViewInteraction toastViewInteraction = onView(withText(
                containsString(activity.getResources().getText(R.string.no_result_search_comunidad).toString())
        ));
        toastViewInteraction.inRoot(withDecorView(not(activity.getWindow().getDecorView())))
                .check(matches((isDisplayed())));
        // This is the difference with the not registered user case.
        onView(withId(R.id.reg_comu_usuariocomu_layout)).check(matches(isDisplayed()));

        // User cleanup.
        boolean isDeleted = ServOne.deleteUser();
        assertThat(isDeleted, is(true));

        Thread.sleep(4000);
    }

    @Test
    public void testOnListItemClick_1()
    {
        //Usuario no registrado. La búsqueda devuelve una comunidad.
        regComuAndUserComuWith2Comu(makeListTwoUserComu());
        // Borro los datos del usuario.
        cleanData();
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));

        mComunidadSummaryFrg = (ComuListFr) activity.getFragmentManager().findFragmentById(R.id.comu_list_frg);
        ComuListAdapter adapter = (ComuListAdapter) mComunidadSummaryFrg.getListAdapter();
        assertThat(adapter.getCount(), is(1));
        onView(withAdaptedData(hasProperty("nombreVia", is("de la Plazuela")))).check(matches(isDisplayed()));
        onData(allOf(is(instanceOf(Comunidad.class)), hasProperty("nombreVia", is("de la Plazuela")))).perform(click());
        onView(withId(R.id.reg_user_and_usercomu_ac_layout)).check(matches(isDisplayed()));

        // User cleanup. We update user credentiasl first.
        updateSecurityData(USUARIO_1.getUserName(), "psw_juan");
        boolean isDeleted = ServOne.deleteUser();
        assertThat(isDeleted, is(true));
    }

    @Test
    public void testOnListItemClick_2()
    {
        // Usuario registrado. La búsqueda devuelve una comunidad a la que él ya está asociado.
        regComuAndUserComuWith2Comu(makeListTwoUserComu());
        activity = mActivityRule.launchActivity(intent);
        mComunidadSummaryFrg = (ComuListFr) activity.getFragmentManager().findFragmentById(R.id.comu_list_frg);
        ComuListAdapter adapter = (ComuListAdapter) mComunidadSummaryFrg.getListAdapter();
        assertThat(adapter.getCount(), is(1));

        Comunidad comunidadAdapter = adapter.getItem(0);
        onView(withAdaptedData(Matchers.<Object>equalTo(comunidadAdapter))).check(matches(isDisplayed()));
        onData(allOf(is(instanceOf(Comunidad.class)), hasProperty("nombreVia", is("de la Plazuela")))).perform(click());
        onView(withId(R.id.see_comu_and_usercomu_ac_layout)).check(matches(isDisplayed()));

        // User cleanup.
        boolean isDeleted = ServOne.deleteUser();
        assertThat(isDeleted, is(true));
    }

    @Test
    public void testOnListItemClick_3()
    {
        // Usuario registrado. La búsqueda devuelve una comunidad a la que él NO está asociado.
        // Insertamos la comunidad a devolver y borramos credenciales de seguridad.
        regComuAndUserComuWith2Comu(makeListTwoUserComu());
        cleanData();
        // Insertamos al usuario que hace la búsqueda.
        Comunidad comunidadIn = new Comunidad("Calle", "de la Torre", (short) 115, null,
                new Municipio(new Provincia((short) 2), (short) 22));
        Usuario usuarioIn = new Usuario("newuser@jnew.us", "newuser", "psw_newuser", (short) 34, 600151515);
        UsuarioComunidad usuarioComunidad = new UsuarioComunidad(comunidadIn, usuarioIn, null,
                null, "3pl", "A_puerta", PROPIETARIO.function);
        Usuario usuario = ServOne.signUp(usuarioComunidad);
        updateSecurityData(usuario.getUserName(), "psw_newuser");

        // Búsqueda con comunidad/intent por defecto.
        activity = mActivityRule.launchActivity(intent);
        mComunidadSummaryFrg = (ComuListFr) activity.getFragmentManager().findFragmentById(R.id.comu_list_frg);
        ComuListAdapter adapter = (ComuListAdapter) mComunidadSummaryFrg.getListAdapter();
        assertThat(adapter.getCount(), is(1));
        onData(allOf(is(instanceOf(Comunidad.class)), hasProperty("nombreVia", is("de la Plazuela")))).perform(click());
        onView(withId(R.id.reg_usercomu_ac_layout)).check(matches(isDisplayed()));

        // User2 cleanup.
        boolean isDeleted = ServOne.deleteUser();
        assertThat(isDeleted, is(true));

        // User1 cleanup. We update user credentiasl first.
        updateSecurityData(USUARIO_1.getUserName(), "psw_juan");
        isDeleted = ServOne.deleteUser();
        assertThat(isDeleted, is(true));
    }

    @After
    public void cleanData()
    {
        if (refreshTkFile.exists()) {
            refreshTkFile.delete();
        }
        TKhandler.getTokensCache().invalidateAll();
        TKhandler.updateRefreshToken(null);
        updateIsRegistered(false, DidekindroidApp.getContext());
    }
}