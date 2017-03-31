package com.didekindroid.incidencia.list.close;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.core.IncidenciaDataDbHelper;
import com.didekindroid.incidencia.resolucion.IncidResolucionSeeFr;
import com.didekindroid.incidencia.utils.IncidBundleKey;
import com.didekindroid.testutil.ActivityTestUtils;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.incidencia.dominio.Resolucion;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.core.IncidenciaDataDbHelper.DB_NAME;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.RESOLUCION_DEFAULT_DESC;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.doIncidencia;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.doResolucionAvances;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidFragmentTags.incid_resolucion_see_fr_tag;
import static com.didekindroid.incidencia.utils.IncidFragmentTags.incid_see_by_comu_list_fr_tag;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.testutil.ActivityTestUtils.getAdapterCount;
import static com.didekindroid.testutil.ActivityTestUtils.isViewDisplayed;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_LA_FUENTE_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.regSeveralUserComuSameUser;
import static com.didekindroid.util.AppBundleKey.IS_MENU_IN_FRAGMENT_FLAG;
import static com.didekindroid.util.UIutils.formatTimeStampToString;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 02/02/16
 * Time: 18:00
 * Tests sobre la lista de incidencias cerradas.
 */
@SuppressWarnings({"unchecked", "ConstantConditions"})
@RunWith(AndroidJUnit4.class)
public class IncidSeeClosedByComuAcTest {

    UsuarioComunidad pepePlazuelas5;
    UsuarioComunidad pepeLaFuente;
    IncidImportancia incidPepePlazuelas5_1;
    IncidImportancia incidPepePlazuelas5_2;
    IncidImportancia incidPepeLaFuente;
    IncidSeeCloseByComuFr fragment;
    IncidenciaDataDbHelper dbHelper;
    CtrlerIncidSeeCloseByComu controllerList;
    AdapterIncidSeeClosedByComu listAdapter;
    Resolucion resolucionToCheck;

    @Rule
    public IntentsTestRule<IncidSeeClosedByComuAc> activityRule = new IntentsTestRule<IncidSeeClosedByComuAc>(IncidSeeClosedByComuAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            try {
                regSeveralUserComuSameUser(COMU_PLAZUELA5_PEPE, COMU_LA_FUENTE_PEPE);
                pepeLaFuente = userComuDaoRemote.seeUserComusByUser().get(0);
                pepePlazuelas5 = userComuDaoRemote.seeUserComusByUser().get(1);
                incidPepePlazuelas5_1 = new IncidImportancia.IncidImportanciaBuilder(
                        doIncidencia(USER_PEPE.getUserName(), "Incid_pepePlazuelas_1", pepePlazuelas5.getComunidad().getC_Id(), (short) 12))
                        .usuarioComunidad(pepePlazuelas5)
                        .importancia((short) 2)
                        .build();
                incidPepePlazuelas5_2 = new IncidImportancia.IncidImportanciaBuilder(
                        doIncidencia(USER_PEPE.getUserName(), "Incid_pepePlazuelas_2", pepePlazuelas5.getComunidad().getC_Id(), (short) 23))
                        .usuarioComunidad(pepePlazuelas5)
                        .build();
                incidPepeLaFuente = new IncidImportancia.IncidImportanciaBuilder(
                        doIncidencia(USER_PEPE.getUserName(), "Incid_pepeLaFuente_1", pepeLaFuente.getComunidad().getC_Id(), (short) 33))
                        .usuarioComunidad(pepeLaFuente)
                        .importancia((short) 3)
                        .build();

                // Registro incidPepePlazuelas5_1.
                assertThat(incidenciaDao.regIncidImportancia(incidPepePlazuelas5_1), is(2));
                Thread.sleep(1000);
                Incidencia incidenciaDB = incidenciaDao.seeIncidsOpenByComu(pepePlazuelas5.getComunidad().getC_Id()).get(0).getIncidencia();
                incidPepePlazuelas5_1 = incidenciaDao.seeIncidImportancia(incidenciaDB.getIncidenciaId()).getIncidImportancia();
                Resolucion resolucionToClose = doResolucionAvances(incidPepePlazuelas5_1.getIncidencia(), RESOLUCION_DEFAULT_DESC, 231, ActivityTestUtils.doTimeStampFromCalendar(1));
                assertThat(incidenciaDao.regResolucion(resolucionToClose), is(1));
                resolucionToClose = incidenciaDao.seeResolucion(resolucionToClose.getIncidencia().getIncidenciaId());
                // Cierre incidPepePlazuelas5_1.
                Thread.sleep(1000);
                incidenciaDao.closeIncidencia(resolucionToClose);

                // Registro incidPepeLaFuente.
                assertThat(incidenciaDao.regIncidImportancia(incidPepeLaFuente), is(2));
                Thread.sleep(1000);
                incidenciaDB = incidenciaDao.seeIncidsOpenByComu(pepeLaFuente.getComunidad().getC_Id()).get(0).getIncidencia();
                incidPepeLaFuente = incidenciaDao.seeIncidImportancia(incidenciaDB.getIncidenciaId()).getIncidImportancia();
                resolucionToClose = doResolucionAvances(incidPepeLaFuente.getIncidencia(), RESOLUCION_DEFAULT_DESC, 321, ActivityTestUtils.doTimeStampFromCalendar(1));
                assertThat(incidenciaDao.regResolucion(resolucionToClose), is(1));
                resolucionToClose = incidenciaDao.seeResolucion(resolucionToClose.getIncidencia().getIncidenciaId());
                // Cierre incidPepeLaFuente.
                Thread.sleep(1000);
                incidenciaDao.closeIncidencia(resolucionToClose);

                // Registro incidPepePlazuelas5_2.
                assertThat(incidenciaDao.regIncidImportancia(incidPepePlazuelas5_2), is(2));
                Thread.sleep(1000);
                incidenciaDB = incidenciaDao.seeIncidsOpenByComu(pepePlazuelas5.getComunidad().getC_Id()).get(0).getIncidencia();
                incidPepePlazuelas5_2 = incidenciaDao.seeIncidImportancia(incidenciaDB.getIncidenciaId()).getIncidImportancia();
                resolucionToClose = doResolucionAvances(incidPepePlazuelas5_2.getIncidencia(), RESOLUCION_DEFAULT_DESC, 334, ActivityTestUtils.doTimeStampFromCalendar(1));
                resolucionToCheck = resolucionToClose;
                assertThat(incidenciaDao.regResolucion(resolucionToClose), is(1));
                resolucionToClose = incidenciaDao.seeResolucion(resolucionToClose.getIncidencia().getIncidenciaId());
                // Cierre incidPepePlazuelas5_2.
                Thread.sleep(1000);
                incidenciaDao.closeIncidencia(resolucionToClose);

                Thread.sleep(1000);
                FragmentManager.enableDebugLogging(true);

            } catch (InterruptedException | IOException | UiException e) {
                e.printStackTrace();
            }
        }
    };

    IncidSeeClosedByComuAc activity;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        dbHelper = new IncidenciaDataDbHelper(activity);
        fragment = (IncidSeeCloseByComuFr) activity.getSupportFragmentManager().findFragmentByTag(incid_see_by_comu_list_fr_tag);
        // controllerList = (CtrlerIncidSeeCloseByComu) fragment.controllerSeeIncids;  TODO: no necesario el controller.
        listAdapter = (AdapterIncidSeeClosedByComu) controllerList.adapter;
    }

    @After
    public void tearDown() throws Exception
    {
        dbHelper.dropAllTables();
        dbHelper.close();
        activity.deleteDatabase(DB_NAME);
        cleanOptions(CLEAN_PEPE);
    }

    // ======================================  ViewerSelectableListIf  ==================================

    @Test
    public void testOnData_1() throws Exception
    {
        // CASO OK: muestra las incidencias de la comunidad por defecto (Calle La Fuente).
        waitAtMost(3, SECONDS).until(getAdapterCount(listAdapter), is(1));

        IncidenciaUser incidUser_1 = listAdapter.getItem(0);
        ViewMatchers.assertThat(incidUser_1.getIncidencia(), is(incidPepeLaFuente.getIncidencia()));
        onData(is(incidUser_1)).inAdapterView(withId(android.R.id.list)).check(matches(isDisplayed()));

         /* Datos a la vista de IncidSeeCloseByComuFr.*/
        waitAtMost(2, SECONDS).until(isViewDisplayed(getIncidListViewMatcher(incidUser_1)));
    }

    @Test
    public void testOnCloseSubscriptions(){
        // Preconditions.
        waitAtMost(3, SECONDS).until(getAdapterCount(listAdapter), is(1));

        assertThat(controllerList.getSubscriptions().size(), is(1));
        InstrumentationRegistry.getInstrumentation().callActivityOnStop(activity);
        // Check.
        assertThat(controllerList.getSubscriptions().size(), is(0));
    }

    @Test
    public void testOnData_2() throws Exception
    {
        // CASO OK: cambiamos la comunidad en el spinner y revisamos los datos.
        onView(withId(R.id.incid_reg_comunidad_spinner)).perform(click());
        onData(allOf(
                is(instanceOf(Comunidad.class)),
                is(pepePlazuelas5.getComunidad()))
        ).check(matches(isDisplayed())).perform(click());

        waitAtMost(2, SECONDS).until(getAdapterCount(listAdapter), is(2));

        IncidenciaUser incidUser_1 = listAdapter.getItem(0);
        IncidenciaUser incidUser_2 = listAdapter.getItem(1);

        ViewMatchers.assertThat(incidUser_1.getIncidencia(), is(incidPepePlazuelas5_1.getIncidencia()));
        onData(is(incidUser_1)).inAdapterView(withId(android.R.id.list)).check(matches(isDisplayed()));

        ViewMatchers.assertThat(incidUser_2.getIncidencia(), is(incidPepePlazuelas5_2.getIncidencia()));
        onData(is(incidUser_2)).inAdapterView(withId(android.R.id.list)).check(matches(isDisplayed()));
    }

    @Test
    public void testOnSelectedWithDoubleUp() throws UiException
    {
        // CASO OK: seleccionamos 2ª comunidad en spinner y la 2ª incidencia.

        onView(withId(R.id.incid_reg_comunidad_spinner)).perform(click());
        onData(allOf(
                is(instanceOf(Comunidad.class)),
                is(pepePlazuelas5.getComunidad()))
        ).perform(click());

        waitAtMost(2, SECONDS).until(getAdapterCount(listAdapter), is(2));
        IncidenciaUser incidUser = listAdapter.getItem(1);
        onData(is(incidUser)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()))
                .perform(click());

        waitAtMost(2, SECONDS).until(isViewDisplayed(withId(R.id.incid_resolucion_see_fr_layout)));

        // Verificamos argumentos del nuevo fragmento.
        IncidResolucionSeeFr resolucionSeeFr = (IncidResolucionSeeFr) activity.getSupportFragmentManager()
                .findFragmentByTag(incid_resolucion_see_fr_tag);
        assertThat(resolucionSeeFr, notNullValue());
        Bundle args = resolucionSeeFr.getArguments();
        assertThat(args.size(), is(3));
        assertThat((Incidencia) args.getSerializable(IncidBundleKey.INCIDENCIA_OBJECT.key), is(incidPepePlazuelas5_2.getIncidencia()));
        assertThat((Resolucion) args.getSerializable(IncidBundleKey.INCID_RESOLUCION_OBJECT.key), is(resolucionToCheck));
        assertThat(args.getBoolean(IS_MENU_IN_FRAGMENT_FLAG.key), is(true));

        // Probamos el menú del nuevo fragmento.
        onView(withText(R.string.incid_comments_see_ac_mn)).check(matches(isDisplayed())).perform(click());
        // Verificamos cambio a 'Comentarios'.
        onView(withId(R.id.incid_comments_see_fr_layout)).check(matches(isDisplayed()));
        intended(hasExtra(is(INCIDENCIA_OBJECT.key), is(incidPepePlazuelas5_2.getIncidencia())));
        // Comentarios no muestra menú porque la incidencia está cerrada.
        assertThat(incidenciaDao.seeResolucion(incidPepePlazuelas5_2.getIncidencia().getIncidenciaId()).getIncidencia().getFechaCierre(),
                notNullValue());
        onView(withId(R.id.incid_comment_reg_ac_mn)).check(doesNotExist());

        // Navigate-up desde IncidCommentSeeAc
        checkUp(R.id.incid_resolucion_see_fr_layout);  // Up to 'Resolución'
        checkUp(R.id.incid_see_closed_by_comu_ac, R.id.incid_see_generic_layout);  // Up to consulta incidencias.
    }

    @Test
    public void testOnSelectedWithBack() throws InterruptedException
    {
        // CASO: verificación de la navegación, Utilizamos PressBack.

        // Comunidad por defecto.
        waitAtMost(2, SECONDS).until(getAdapterCount(listAdapter), is(1));
        IncidenciaUser incidUser = listAdapter.getItem(0);
        // Seleccionamos una incidencia.
        onData(is(incidUser)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()))
                .perform(click());

        // BACK.
        waitAtMost(2, SECONDS).until(isViewDisplayed(withId(R.id.incid_resolucion_see_fr_layout)));
        onView(withId(R.id.incid_resolucion_see_fr_layout)).perform(pressBack());
        /* Datos a la vista de IncidSeeCloseByComuFr.*/
        waitAtMost(2, SECONDS).until(isViewDisplayed(getIncidListViewMatcher(incidUser)));
    }

    @Test
    public void testOnSelectedWithUp() throws InterruptedException
    {
        // CASO: verificación de la navegación,

        // Comunidad por defecto.
        waitAtMost(2, SECONDS).until(getAdapterCount(listAdapter), is(1));
        IncidenciaUser incidUser = listAdapter.getItem(0);
        // Seleccionamos una incidencia.
        onData(is(incidUser)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()))
                .perform(click());

        waitAtMost(2, SECONDS).until(isViewDisplayed(withId(R.id.incid_resolucion_see_fr_layout)));

        // Up Navigation:
        clickNavigateUp();
        /* Datos a la vista de IncidSeeCloseByComuFr.*/
        waitAtMost(2, SECONDS).until(isViewDisplayed(getIncidListViewMatcher(incidUser)));
    }

//    ===================================== HELPERS =====================================

    private Matcher<View> getIncidListViewMatcher(IncidenciaUser incidUser)
    {
        return allOf(
                withId(R.id.incid_see_apertura_block),
                hasDescendant(allOf(
                        withId(R.id.incid_fecha_alta_view),
                        withText(formatTimeStampToString(incidUser.getIncidencia().getFechaAlta()))
                )),
                hasDescendant(allOf(
                        withId(R.id.incid_see_iniciador_view),
                        withText(incidUser.getUsuario().getAlias())
                )),
                hasSibling(allOf(
                        withId(R.id.incid_see_cierre_block),
                        hasDescendant(allOf(
                                withId(R.id.incid_fecha_cierre_view),
                                withText(formatTimeStampToString(incidUser.getIncidencia().getFechaCierre()))
                        ))
                )),
                hasSibling(allOf(
                        withId(R.id.incid_see_importancia_block),
                        hasDescendant(allOf(
                                withId(R.id.incid_importancia_comunidad_view),
                                withText(activity.getResources()
                                        .getStringArray(R.array.IncidImportanciaArray)[Math.round(incidPepeLaFuente.getImportancia())]))
                        ))),
                hasSibling(allOf(
                        withId(R.id.incid_ambito_view),
                        withText(dbHelper.getAmbitoDescByPk(incidUser.getIncidencia().getAmbitoIncidencia().getAmbitoId()))
                )),
                hasSibling(allOf(
                        withText(incidUser.getIncidencia().getDescripcion()),
                        withId(R.id.incid_descripcion_view)
                ))
        );
    }
}