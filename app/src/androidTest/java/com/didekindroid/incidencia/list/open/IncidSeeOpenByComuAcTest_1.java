package com.didekindroid.incidencia.list.open;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.core.IncidenciaDataDbHelper;
import com.didekindroid.incidencia.list.close.IncidSeeCloseByComuFr;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static android.database.sqlite.SQLiteDatabase.deleteDatabase;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.comunidad.testutil.ComuDataTestUtil.COMU_LA_PLAZUELA_5;
import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.core.IncidenciaDataDbHelperTest.DB_PATH;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.doIncidencia;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidFragmentTags.incid_see_by_comu_list_fr_tag;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.getAdapterCount;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.regSeveralUserComuSameUser;
import static com.didekindroid.util.UIutils.formatTimeStampToString;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 23/11/15
 * Time: 18:45
   Tests sobre presentación de datos y selección de una incidencia.
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class IncidSeeOpenByComuAcTest_1 {

    UsuarioComunidad juanReal;
    UsuarioComunidad juanPlazuela;
    IncidImportancia incidJuanReal1;
    IncidImportancia incidJuanReal2;
    IncidImportancia incidJuanPlazuela1;
    /**
     * Preconditions:
     * 1. Incidencia without resolucion in BD.
     * 2. User with and without authority 'adm' and with incidImportancia records in DB.
     * Condition:
     * 1. User select an incidencia.
     * Postconditions:
     * 1. The incidencia is shown in edit mode (importancia field).
     * 2. An intent is passed whith:
     * - the incidImportancia instance of the user in session.
     * - a true value in the resolucion flag.
     */
    @Rule
    public IntentsTestRule<IncidSeeOpenByComuAc> activityRule = new IntentsTestRule<IncidSeeOpenByComuAc>(IncidSeeOpenByComuAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            try {
                regSeveralUserComuSameUser(COMU_REAL_JUAN, COMU_PLAZUELA5_JUAN);
                juanReal = userComuDaoRemote.seeUserComusByUser().get(0);
                juanPlazuela = userComuDaoRemote.seeUserComusByUser().get(1);
                incidJuanReal1 = new IncidImportancia.IncidImportanciaBuilder(
                        doIncidencia(juanReal.getUsuario().getUserName(), "Incidencia Real One", juanReal.getComunidad().getC_Id(), (short) 43))
                        .usuarioComunidad(juanReal)
                        .importancia((short) 3).build();
                incidJuanReal2 = new IncidImportancia.IncidImportanciaBuilder(
                        doIncidencia(juanReal.getUsuario().getUserName(), "Incidencia Real Two", juanReal.getComunidad().getC_Id(), (short) 11))
                        .usuarioComunidad(juanReal)
                        .importancia((short) 2).build();
                incidJuanPlazuela1 = new IncidImportancia.IncidImportanciaBuilder(
                        doIncidencia(juanPlazuela.getUsuario().getUserName(), "Incidencia Plazuela One", juanPlazuela.getComunidad().getC_Id(), (short) 26))
                        .usuarioComunidad(juanPlazuela)
                        /*.importancia((short) 4)*/.build();
                incidenciaDao.regIncidImportancia(incidJuanReal1);
                incidenciaDao.regIncidImportancia(incidJuanReal2);
                incidenciaDao.regIncidImportancia(incidJuanPlazuela1);
            } catch (IOException | UiException e) {
                e.printStackTrace();
            }
        }
    };

    IncidenciaDataDbHelper dBHelper;
    CtrlerIncidSeeOpenByComu controller;
    AdapterIncidSeeOpenByComu adapter;
    IncidSeeCloseByComuFr fragment;
    private IncidSeeOpenByComuAc activity;

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(2000);
    }

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        dBHelper = new IncidenciaDataDbHelper(activity);
        fragment = (IncidSeeCloseByComuFr) activity.getSupportFragmentManager()
                .findFragmentByTag(incid_see_by_comu_list_fr_tag);
//        controller = (CtrlerIncidSeeOpenByComu) fragment.controllerSeeIncids;     TODO: quitar Controller.
        adapter = (AdapterIncidSeeOpenByComu) controller.adapter;

    }

    @After
    public void tearDown() throws Exception
    {
        dBHelper.dropAllTables();
        dBHelper.close();
        String dBFileName = DB_PATH.concat(IncidenciaDataDbHelper.DB_NAME);
        deleteDatabase(new File(dBFileName));
        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void testOnCreate_1() throws Exception
    {
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        waitAtMost(3, SECONDS).until(getAdapterCount(adapter), is(2));

        // Default comunidad: Real, in position 0.
        onView(allOf(
                withId(R.id.app_spinner_1_dropdown_item),
                withParent(withId(R.id.incid_reg_comunidad_spinner))))
                .check(matches(withText(is(juanReal.getComunidad().getNombreComunidad()))))
                .check(matches(isDisplayed()));

        // Ordered by fecha_alta of the incidencia.
        assertThat(adapter.getItem(0).getIncidencia().getComunidad(), is(juanReal.getComunidad()));
        assertThat(adapter.getItem(0).getIncidencia().getAmbitoIncidencia().getAmbitoId(), is(incidJuanReal1.getIncidencia().getAmbitoIncidencia().getAmbitoId()));
        assertThat(adapter.getItem(0).getUsuario().getAlias(), is(juanReal.getUsuario().getAlias()));
        assertThat(adapter.getItem(0).getIncidencia().getDescripcion(), is(incidJuanReal1.getIncidencia().getDescripcion()));
        // Solo hay un registro IncidImportancia para la incidencia: importanciaAvg == importancia usuario único.
        assertThat(adapter.getItem(0).getIncidencia().getImportanciaAvg(), is((float) incidJuanReal1.getImportancia()));
        // Fecha alta resolución
        assertThat(adapter.getItem(0).getFechaAltaResolucion(), nullValue());

        assertThat(adapter.getItem(1).getIncidencia().getComunidad(), is(juanReal.getComunidad()));
        assertThat(adapter.getItem(1).getUsuario().getAlias(), is(juanReal.getUsuario().getAlias()));
        assertThat(adapter.getItem(1).getIncidencia().getAmbitoIncidencia().getAmbitoId(), is(incidJuanReal2.getIncidencia().getAmbitoIncidencia().getAmbitoId()));
        assertThat(adapter.getItem(1).getIncidencia().getDescripcion(), is(incidJuanReal2.getIncidencia().getDescripcion()));
        // Solo hay un registro IncidImportancia para la incidencia: importanciaAvg == importancia usuario único.
        assertThat(adapter.getItem(1).getIncidencia().getImportanciaAvg(), is((float) incidJuanReal2.getImportancia()));
        // Fecha alta resolución
        assertThat(adapter.getItem(0).getFechaAltaResolucion(), nullValue());
    }

    @Test
    public void testOnCreate_2() throws Exception
    {
        // Second comunidad (Plazuela) in the comunidad_spinner is selected.
        onView(withId(R.id.incid_reg_comunidad_spinner)).perform(click());
        onView(withText(COMU_LA_PLAZUELA_5.getNombreComunidad())).perform(click()).check(matches(isDisplayed()));

        // Verificamos que la actividad recibe la comunidad seleccionada.
        waitAtMost(3, SECONDS).until(getAdapterCount(adapter), is(1));
//        assertThat(fragment.comunidadSelectedIndex, is(1));     TODO: adaptar y descomentar.

        assertThat(adapter.getItem(0).getIncidencia().getComunidad(), is(juanPlazuela.getComunidad()));
        assertThat(adapter.getItem(0).getUsuario().getAlias(), is(juanPlazuela.getUsuario().getAlias()));
        assertThat(adapter.getItem(0).getIncidencia().getAmbitoIncidencia().getAmbitoId(), is(incidJuanPlazuela1.getIncidencia().getAmbitoIncidencia().getAmbitoId()));
        assertThat(adapter.getItem(0).getIncidencia().getDescripcion(), is(incidJuanPlazuela1.getIncidencia().getDescripcion()));
        // Solo hay un registro IncidImportancia para la incidencia: importanciaAvg == importancia usuario único.
        assertThat(adapter.getItem(0).getIncidencia().getImportanciaAvg(), allOf(
                is((float) incidJuanPlazuela1.getImportancia()),
                is((float) 0)
        ));
    }

    @Test
    public void testOnData_1()
    {
        // Default comunidad (Real) in position 0 in comunidad_spinner.
        waitAtMost(3, SECONDS).until(getAdapterCount(adapter), is(2));

        IncidenciaUser incidUser_0 = adapter.getItem(0);
        IncidenciaUser incidUser_1 = adapter.getItem(1);
        Incidencia incidencia_0 = incidUser_0.getIncidencia();

        onData(is(incidUser_0)).inAdapterView(withId(android.R.id.list)).check(matches(isDisplayed()));
        onData(is(incidUser_1)).inAdapterView(withId(android.R.id.list)).check(matches(isDisplayed()));

        onView(allOf(
                withId(R.id.incid_see_importancia_block),
                hasDescendant(allOf(
                        withId(R.id.incid_importancia_comunidad_view),
                        withText(activity.getResources()
                                .getStringArray(R.array.IncidImportanciaArray)[Math.round(incidJuanReal1.getImportancia())]))
                ),
                hasSibling(allOf(
                        withText(incidJuanReal1.getIncidencia().getDescripcion()),
                        withId(R.id.incid_descripcion_view)
                )),
                hasSibling(allOf(
                        withId(R.id.incid_ambito_view),
                        withText(dBHelper.getAmbitoDescByPk(incidJuanReal1.getIncidencia().getAmbitoIncidencia().getAmbitoId()))
                )),
                hasSibling(allOf(
                        withId(R.id.incid_see_apertura_block),
                        hasSibling(withText(dBHelper.getAmbitoDescByPk(incidJuanReal1.getIncidencia().getAmbitoIncidencia().getAmbitoId()))),
                        hasDescendant(allOf(
                                withId(R.id.incid_fecha_alta_view),
                                withText(formatTimeStampToString(incidencia_0.getFechaAlta()))
                        )),
                        hasDescendant(allOf(
                                withId(R.id.incid_see_iniciador_view),
                                withText(juanReal.getUsuario().getAlias())
                        ))
                ))
        )).check(matches(isDisplayed()));

        // Bloque datos de cierre no visible.
        onView(allOf(
                withId(R.id.incid_see_cierre_block),
                hasSibling(withText(dBHelper.getAmbitoDescByPk(incidJuanReal1.getIncidencia().getAmbitoIncidencia().getAmbitoId())))
        )).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        // Bloque datos de fecha alta resolución no visible.
        onView(allOf(
                withId(R.id.incid_see_resolucion_block),
                hasSibling(withText(dBHelper.getAmbitoDescByPk(incidJuanReal1.getIncidencia().getAmbitoIncidencia().getAmbitoId())))
        )).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    public void testOnData_2() throws InterruptedException
    {
        // Second comunidad (Plazuela) in the comunidad_spinner is selected.
        onView(withId(R.id.incid_reg_comunidad_spinner)).perform(click());
        onView(withText(COMU_LA_PLAZUELA_5.getNombreComunidad())).perform(click()).check(matches(isDisplayed()));
        waitAtMost(3, SECONDS).until(getAdapterCount(adapter), is(1));

        IncidenciaUser incidUser = adapter.getItem(0);
        onData(is(incidUser)).inAdapterView(withId(android.R.id.list)).check(matches(isDisplayed()));

        // Verificamos la importancia que debiera salir en blanco.
        onView(allOf(
                withId(R.id.incid_see_importancia_block),
                hasDescendant(allOf(
                        withId(R.id.incid_importancia_comunidad_view),
                        withText("")
                )),
                hasSibling(allOf(
                        withText(incidJuanPlazuela1.getIncidencia().getDescripcion()),
                        withId(R.id.incid_descripcion_view)
                ))
        )).check(matches(isDisplayed()));
    }

    @Test
    public void testOnSelected_1() throws UiException, InterruptedException
    {

        // Default comunidad (Real), in position 0, is selected.
        waitAtMost(3, SECONDS).until(getAdapterCount(adapter), is(2));
        IncidenciaUser incidUser_0 = adapter.getItem(0);
        Incidencia incidencia_0 = incidUser_0.getIncidencia();

        onData(is(incidUser_0)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()))
                .perform(click());

        // Checks.
        IncidImportancia incidImportancia = incidenciaDao.seeIncidImportancia(incidencia_0.getIncidenciaId()).getIncidImportancia();
        intended(hasExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia));
        onView(withId(R.id.incid_edit_fragment_container_ac)).check(matches(isDisplayed()));
        checkUp(R.id.incid_see_open_by_comu_ac, R.id.incid_see_generic_layout);
    }
}