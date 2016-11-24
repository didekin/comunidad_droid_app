package com.didekinaar.comunidad;

import android.content.Intent;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.comunidad.Comunidad;
import com.didekin.comunidad.Municipio;
import com.didekin.comunidad.Provincia;
import com.didekin.usuario.Usuario;
import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekinaar.R;
import com.didekinaar.exception.UiAarException;
import com.didekinaar.testutil.AarActivityTestUtils;
import com.didekinaar.testutil.CleanUserEnum;
import com.didekinaar.testutil.UsuarioTestUtils;

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
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekinaar.comunidad.ComuBundleKey.COMUNIDAD_LIST_OBJECT;
import static com.didekinaar.comunidad.ComuBundleKey.COMUNIDAD_SEARCH;
import static com.didekinaar.security.TokenHandler.TKhandler;
import static com.didekinaar.testutil.AarActivityTestUtils.checkBack;
import static com.didekinaar.testutil.AarActivityTestUtils.checkToastInTest;
import static com.didekinaar.testutil.AarActivityTestUtils.checkUp;
import static com.didekinaar.testutil.AarActivityTestUtils.cleanOptions;
import static com.didekinaar.testutil.AarActivityTestUtils.cleanTwoUsers;
import static com.didekinaar.testutil.CleanUserEnum.CLEAN_JUAN;
import static com.didekinaar.testutil.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekinaar.usuariocomunidad.AarUserComuService.AarUserComuServ;
import static com.didekinaar.usuariocomunidad.RolUi.PRO;
import static com.didekinaar.usuariocomunidad.UserComuBundleKey.USERCOMU_LIST_OBJECT;
import static com.didekinaar.utils.UIutils.isRegisteredUser;
import static com.external.LongListMatchers.withAdaptedData;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.AllOf.allOf;
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

    int activityLayoutId = R.id.comu_list_fragment;

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
        intent.putExtra(COMUNIDAD_SEARCH.key, UsuarioTestUtils.COMU_LA_PLAZUELA_5);
    }

    @After
    public void cleanData() throws UiAarException, InterruptedException
    {
        cleanOptions(whatClean);
        Thread.sleep(2000);
        assertThat(isRegisteredUser(activity), is(false));
    }

    @Test
    public void testOnCreate() throws UiAarException, IOException
    {
        whatClean = CLEAN_JUAN;

        // Inserto comunidades en DB.
        AarActivityTestUtils.regTwoUserComuSameUser(AarActivityTestUtils.makeListTwoUserComu());
        activity = mIntentRule.launchActivity(intent);
        onView(ViewMatchers.withId(R.id.comu_list_fragment)).check(matches(isDisplayed()));
    }

    @Test
    public void testSearchComunidades_1() throws UiAarException, IOException, InterruptedException
    {
        whatClean = CLEAN_JUAN;

        // User with 2 comunidades. We search with one of them exactly.
        AarActivityTestUtils.regTwoUserComuSameUser(AarActivityTestUtils.makeListTwoUserComu());
        activity = mIntentRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));

        Thread.sleep(2000);
        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getSupportFragmentManager().findFragmentById(R.id.comu_list_fragment);
        adapter = mComunidadSummaryFrg.mAdapter;
        assertThat(adapter.getCount(), is(1));
        Comunidad comunidad = (Comunidad) intent.getSerializableExtra(COMUNIDAD_SEARCH.key);
        onView(withId(android.R.id.list)).check(matches(
                withAdaptedData(Matchers.<Object>equalTo(comunidad))));
    }

    @Test
    public void testSearchComunidades_2() throws UiAarException, IOException, InterruptedException
    {
        whatClean = CLEAN_JUAN;

        // Caso: existen dos comunidades para el criterio de búsqueda.

        Comunidad comunidadNew = UsuarioTestUtils.makeComunidad("Ronda", "del Norte", (short) 5, "",
                new Municipio((short) 2, new Provincia((short) 27)));
        AarActivityTestUtils.regThreeUserComuSameUser(AarActivityTestUtils.makeListTwoUserComu(), comunidadNew);

        // Criterio de búsqueda.
        Comunidad comunidad = UsuarioTestUtils.makeComunidad("Ronda", "de la Plazuela del Norte", (short) 5, "",
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
                matches(withAdaptedData(Matchers.<Object>is(UsuarioTestUtils.COMU_LA_PLAZUELA_5))));
    }

    @Test
    public void testSearchComunidades_3() throws InterruptedException, UiAarException
    {
        // 1. No existe la comunidad en DB. 2. El usuario no está registrado.

        assertThat(TKhandler.getAccessTokenInCache(), nullValue());

        // Criterio de búsqueda.
        Comunidad comunidad = UsuarioTestUtils.makeComunidad("Rincón", "del No Existente", (short) 123, "",
                new Municipio((short) 2, new Provincia((short) 27)));
        Intent intent = new Intent();
        intent.putExtra(COMUNIDAD_SEARCH.key, comunidad);

        activity = mIntentRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));
        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getSupportFragmentManager().findFragmentById(R.id.comu_list_fragment);

        checkToastInTest(R.string.no_result_search_comunidad, activity);
        // Presenta registro de usuario y comunidad.
        onView(ViewMatchers.withId(R.id.reg_comu_usuario_usuariocomu_layout)).check(matches(isDisplayed()));
        Thread.sleep(2000);
    }

    @Test
    public void testSearchComunidades_4() throws InterruptedException, UiAarException, IOException
    {
        whatClean = CLEAN_JUAN;

        /* 1. No existe la comunidad en DB. 2. El usuario está registrado.*/
        AarActivityTestUtils.signUpAndUpdateTk(UsuarioTestUtils.COMU_REAL_JUAN);

        // Criterio de búsqueda.
        Comunidad comunidad = UsuarioTestUtils.makeComunidad("Rincón", "del No Existente", (short) 123, "",
                new Municipio((short) 2, new Provincia((short) 27)));
        Intent intent = new Intent();
        intent.putExtra(COMUNIDAD_SEARCH.key, comunidad);

        activity = mIntentRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));
        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getSupportFragmentManager().findFragmentById(R.id.comu_list_fragment);

        checkToastInTest(R.string.no_result_search_comunidad, activity);
        // Presenta registro de usuarioComunidad y comunidad.
        onView(ViewMatchers.withId(R.id.reg_comu_and_usercomu_layout)).check(matches(isDisplayed()));
        Thread.sleep(2000);
    }


    @Test
    public void testOnListItemClick_1() throws UiAarException, IOException, InterruptedException
    {
        whatClean = CLEAN_JUAN;

        //Usuario no registrado. La búsqueda devuelve una comunidad.

        AarActivityTestUtils.regTwoUserComuSameUser(AarActivityTestUtils.makeListTwoUserComu());
        // Borro los datos del userComu.
        AarActivityTestUtils.cleanWithTkhandler();

        activity = mIntentRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));

        Thread.sleep(2000);
        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getSupportFragmentManager().findFragmentById(R.id.comu_list_fragment);
        adapter = mComunidadSummaryFrg.mAdapter;
        assertThat(adapter.getCount(), is(1));
        onView(withAdaptedData(Matchers.<Object>is(UsuarioTestUtils.COMU_LA_PLAZUELA_5))).check(matches(isDisplayed()));
        onData(is(instanceOf(Comunidad.class))).onChildView(allOf(
                ViewMatchers.withId(R.id.nombreComunidad_view),
                ViewMatchers.withText(UsuarioTestUtils.COMU_LA_PLAZUELA_5.getNombreComunidad())
        )).perform(click());

        onView(ViewMatchers.withId(R.id.reg_user_and_usercomu_ac_layout)).check(matches(isDisplayed()));

        checkUp(activityLayoutId);
    }

    @Test
    public void testOnListItemClick_2() throws UiAarException, IOException, InterruptedException
    {
        whatClean = CLEAN_JUAN;

        // Usuario registrado. La búsqueda devuelve una comunidad a la que él ya está asociado.

        AarActivityTestUtils.regTwoUserComuSameUser(AarActivityTestUtils.makeListTwoUserComu());
        activity = mIntentRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));

        Thread.sleep(2000);
        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getSupportFragmentManager().findFragmentById(R.id.comu_list_fragment);
        adapter = mComunidadSummaryFrg.mAdapter;
        assertThat(adapter.getCount(), is(1));

        Comunidad comunidad = adapter.getItem(0);
        onView(withAdaptedData(Matchers.<Object>equalTo(comunidad))).check(matches(isDisplayed()));
        onData(is(instanceOf(Comunidad.class))).onChildView(
                allOf(
                        ViewMatchers.withId(R.id.nombreComunidad_view),
                        ViewMatchers.withText(UsuarioTestUtils.COMU_LA_PLAZUELA_5.getNombreComunidad())
                )).perform(click());

        ViewInteraction viewInteraction = onView(ViewMatchers.withId(R.id.usercomu_data_ac_layout)).check(matches(isDisplayed()));

        checkBack(viewInteraction, activityLayoutId);
    }

    @Test
    public void testOnListItemClick_3() throws Exception
    {
        whatClean = CLEAN_JUAN;

        // Intent específico para este test.
        intent = new Intent();
        intent.putExtra(COMUNIDAD_SEARCH.key, UsuarioTestUtils.COMU_REAL);

        // Usuario registrado. La búsqueda devuelve una comunidad a la que él ya está asociado.
        // Verificamos intent de salida.
        Usuario userIntent = AarActivityTestUtils.signUpAndUpdateTk(UsuarioTestUtils.COMU_REAL_JUAN);
        Comunidad comuIntent = AarUserComuServ.getComusByUser().get(0);

        activity = mIntentRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));

        onData(Matchers.is(UsuarioTestUtils.COMU_REAL)).perform(click());

        UsuarioComunidad usuarioComunidad = new UsuarioComunidad.UserComuBuilder(comuIntent, userIntent)
                .portal(UsuarioTestUtils.COMU_REAL_JUAN.getPortal())
                .escalera(UsuarioTestUtils.COMU_REAL_JUAN.getEscalera())
                .planta(UsuarioTestUtils.COMU_REAL_JUAN.getPlanta())
                .puerta(UsuarioTestUtils.COMU_REAL_JUAN.getPuerta())
                .build();

        intended(IntentMatchers.hasExtra(USERCOMU_LIST_OBJECT.key, usuarioComunidad));
        onView(ViewMatchers.withId(R.id.usercomu_data_ac_layout)).check(matches(isDisplayed()));

        checkUp(activityLayoutId);
    }

    @Test
    public void testOnListItemClick_4() throws UiAarException, IOException, InterruptedException
    {
        // Usuario registrado. La búsqueda devuelve una comunidad a la que él NO está asociado.

        AarActivityTestUtils.regTwoUserComuSameUser(AarActivityTestUtils.makeListTwoUserComu());
        AarActivityTestUtils.cleanWithTkhandler();

        // Insertamos al userComu que hace la búsqueda.
        Comunidad comunidadIn = UsuarioTestUtils.makeComunidad("Calle", "de la Torre", (short) 115, "",
                new Municipio((short) 22, new Provincia((short) 2)));
        Usuario usuarioIn = UsuarioTestUtils.makeUsuario("newuser@jnew.us", "newuser", "psw_newuser");
        UsuarioComunidad usuarioComunidad = UsuarioTestUtils.makeUsuarioComunidad(comunidadIn, usuarioIn, null,
                null, "3pl", "A_puerta", PRO.function);
        AarActivityTestUtils.signUpAndUpdateTk(usuarioComunidad);

        // Búsqueda con comunidad/intent por defecto.
        activity = mIntentRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));

        Thread.sleep(2000);
        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getSupportFragmentManager().findFragmentById(R.id.comu_list_fragment);
        adapter = mComunidadSummaryFrg.mAdapter;
        assertThat(adapter.getCount(), is(1));
        Comunidad comunidad = adapter.getItem(0);
        assertThat(comunidad.getNombreVia(), is("de la Plazuela"));

        onData(is(comunidad)).perform(click());
        onView(ViewMatchers.withId(R.id.reg_usercomu_ac_layout)).check(matches(isDisplayed()));
        checkUp(activityLayoutId);

        cleanTwoUsers(UsuarioTestUtils.USER_JUAN, usuarioIn);
    }

    @Test
    public void testOnListItemClick_5() throws Exception
    {
        whatClean = CLEAN_JUAN_AND_PEPE;

        // Usuario registrado. La búsqueda devuelve una comunidad a la que él NO está asociado.

        AarActivityTestUtils.signUpAndUpdateTk(UsuarioTestUtils.COMU_PLAZUELA5_PEPE);
        Comunidad comunidad = AarUserComuServ.getComusByUser().get(0);
        AarActivityTestUtils.signUpAndUpdateTk(UsuarioTestUtils.COMU_REAL_JUAN);

        activity = mIntentRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));

        onData(Matchers.is(UsuarioTestUtils.COMU_LA_PLAZUELA_5)).perform(click());
        intended(IntentMatchers.hasExtra(COMUNIDAD_LIST_OBJECT.key, comunidad));
        ViewInteraction viewInteraction = onView(ViewMatchers.withId(R.id.reg_usercomu_ac_layout)).check(matches(isDisplayed()));

        checkBack(viewInteraction, activityLayoutId);
    }

    @Test
    public void testOnListItemClick_6() throws UiAarException, IOException, InterruptedException
    {
        whatClean = CLEAN_JUAN;

        // Usuario registrado. La búsqueda devuelve una comunidad que ya no existe.
        AarActivityTestUtils.regTwoUserComuSameUser(AarActivityTestUtils.makeListTwoUserComu());

        // Búsqueda con comunidad/intent por defecto.
        activity = mIntentRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));

        Thread.sleep(2000);
        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getSupportFragmentManager().findFragmentById(R.id.comu_list_fragment);
        adapter = mComunidadSummaryFrg.mAdapter;
        assertThat(adapter.getCount(), is(1));
        Comunidad comunidad = adapter.getItem(0);
        assertThat(comunidad.getNombreVia(), is("de la Plazuela"));

        // Borramos la comunidad en BD.
        assertThat(AarUserComuServ.deleteUserComu(comunidad.getC_Id()), is(1));
        onData(is(comunidad)).perform(click());
        // On-click devuelve a la pantalla de búsqueda de comunidad.
        onView(ViewMatchers.withId(R.id.comu_search_ac_linearlayout)).check(matches(isDisplayed()));
        checkToastInTest(R.string.comunidad_not_found_message, activity);

        Thread.sleep(2000);
    }
}