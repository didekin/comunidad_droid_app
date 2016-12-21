package com.didekindroid.incidencia.activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.comunidad.Comunidad;
import com.didekin.incidencia.dominio.IncidImportancia;
import com.didekin.incidencia.dominio.Incidencia;
import com.didekin.incidencia.dominio.IncidenciaUser;
import com.didekin.incidencia.dominio.Resolucion;
import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekinaar.exception.UiException;
import com.didekinaar.testutil.AarTestUtil;
import com.didekinaar.usuario.testutil.UsuarioDataTestUtils;
import com.didekindroid.R;
import com.didekindroid.incidencia.activity.utils.IncidBundleKey;
import com.didekindroid.exception.UiAppException;
import com.didekindroid.incidencia.IncidenciaDataDbHelper;

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
import static com.didekinaar.testutil.AarActivityTestUtils.checkUp;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekinaar.testutil.AarActivityTestUtils.clickNavigateUp;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.regSeveralUserComuSameUser;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.COMU_LA_FUENTE_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.COMU_PLAZUELA5_PEPE;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuariocomunidad.UserComuService.AppUserComuServ;
import static com.didekinaar.utils.UIutils.formatTimeStampToString;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.activity.utils.IncidFragmentTags.incid_resolucion_see_fr_tag;
import static com.didekindroid.incidencia.activity.utils.IncidFragmentTags.incid_see_by_comu_list_fr_tag;
import static com.didekindroid.incidencia.IncidenciaDataDbHelperTest.DB_PATH;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.RESOLUCION_DEFAULT_DESC;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doIncidencia;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doResolucionAvances;
import static com.didekindroid.incidencia.IncidService.IncidenciaServ;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 02/02/16
 * Time: 18:00
 */

/**
 * Tests sobre la lista de incidencias cerradas.
 */
@SuppressWarnings({"unchecked", "ConstantConditions"})
@RunWith(AndroidJUnit4.class)
public class IncidSeeClosedByComuAcTest_2 {

    private IncidSeeClosedByComuAc mActivity;
    private UsuarioDataTestUtils.CleanUserEnum whatToClean;
    UsuarioComunidad mPepePlazuelas5;
    UsuarioComunidad mPepeLaFuente;
    IncidImportancia incidPepePlazuelas5_1;
    IncidImportancia incidPepePlazuelas5_2;
    IncidImportancia incidPepeLaFuente;
    IncidSeeByComuListFr mFragment;
    IncidenciaDataDbHelper dbHelper;
    IncidSeeClosedByComuAdapter mAdapter;
    Resolucion mResolucionToCheck;

    @Rule
    public IntentsTestRule<IncidSeeClosedByComuAc> activityRule = new IntentsTestRule<IncidSeeClosedByComuAc>(IncidSeeClosedByComuAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            try {
                regSeveralUserComuSameUser(COMU_PLAZUELA5_PEPE, COMU_LA_FUENTE_PEPE);
                mPepeLaFuente = AppUserComuServ.seeUserComusByUser().get(0);
                mPepePlazuelas5 = AppUserComuServ.seeUserComusByUser().get(1);
                incidPepePlazuelas5_1 = new IncidImportancia.IncidImportanciaBuilder(
                        doIncidencia(USER_PEPE.getUserName(), "Incid_pepePlazuelas_1", mPepePlazuelas5.getComunidad().getC_Id(), (short) 12))
                        .usuarioComunidad(mPepePlazuelas5)
                        .importancia((short) 2)
                        .build();
                incidPepePlazuelas5_2 = new IncidImportancia.IncidImportanciaBuilder(
                        doIncidencia(USER_PEPE.getUserName(), "Incid_pepePlazuelas_2", mPepePlazuelas5.getComunidad().getC_Id(), (short) 23))
                        .usuarioComunidad(mPepePlazuelas5)
                        .build();
                incidPepeLaFuente = new IncidImportancia.IncidImportanciaBuilder(
                        doIncidencia(USER_PEPE.getUserName(), "Incid_pepeLaFuente_1", mPepeLaFuente.getComunidad().getC_Id(), (short) 33))
                        .usuarioComunidad(mPepeLaFuente)
                        .importancia((short) 3)
                        .build();

                // Registro incidPepePlazuelas5_1.
                assertThat(IncidenciaServ.regIncidImportancia(incidPepePlazuelas5_1), is(2));
                Thread.sleep(1000);
                Incidencia incidenciaDB = IncidenciaServ.seeIncidsOpenByComu(mPepePlazuelas5.getComunidad().getC_Id()).get(0).getIncidencia();
                incidPepePlazuelas5_1 = IncidenciaServ.seeIncidImportancia(incidenciaDB.getIncidenciaId()).getIncidImportancia();
                Resolucion resolucionToClose = doResolucionAvances(incidPepePlazuelas5_1.getIncidencia(), RESOLUCION_DEFAULT_DESC, 231, AarTestUtil.doTimeStampFromCalendar(1));
                assertThat(IncidenciaServ.regResolucion(resolucionToClose), is(1));
                resolucionToClose = IncidenciaServ.seeResolucion(resolucionToClose.getIncidencia().getIncidenciaId());
                // Cierre incidPepePlazuelas5_1.
                Thread.sleep(1000);
                IncidenciaServ.closeIncidencia(resolucionToClose);

                // Registro incidPepeLaFuente.
                assertThat(IncidenciaServ.regIncidImportancia(incidPepeLaFuente), is(2));
                Thread.sleep(1000);
                incidenciaDB = IncidenciaServ.seeIncidsOpenByComu(mPepeLaFuente.getComunidad().getC_Id()).get(0).getIncidencia();
                incidPepeLaFuente = IncidenciaServ.seeIncidImportancia(incidenciaDB.getIncidenciaId()).getIncidImportancia();
                resolucionToClose = doResolucionAvances(incidPepeLaFuente.getIncidencia(), RESOLUCION_DEFAULT_DESC, 321, AarTestUtil.doTimeStampFromCalendar(1));
                assertThat(IncidenciaServ.regResolucion(resolucionToClose), is(1));
                resolucionToClose = IncidenciaServ.seeResolucion(resolucionToClose.getIncidencia().getIncidenciaId());
                // Cierre incidPepeLaFuente.
                Thread.sleep(1000);
                IncidenciaServ.closeIncidencia(resolucionToClose);

                // Registro incidPepePlazuelas5_2.
                assertThat(IncidenciaServ.regIncidImportancia(incidPepePlazuelas5_2), is(2));
                Thread.sleep(1000);
                incidenciaDB = IncidenciaServ.seeIncidsOpenByComu(mPepePlazuelas5.getComunidad().getC_Id()).get(0).getIncidencia();
                incidPepePlazuelas5_2 = IncidenciaServ.seeIncidImportancia(incidenciaDB.getIncidenciaId()).getIncidImportancia();
                resolucionToClose = doResolucionAvances(incidPepePlazuelas5_2.getIncidencia(), RESOLUCION_DEFAULT_DESC, 334, AarTestUtil.doTimeStampFromCalendar(1));
                mResolucionToCheck = resolucionToClose;
                assertThat(IncidenciaServ.regResolucion(resolucionToClose), is(1));
                resolucionToClose = IncidenciaServ.seeResolucion(resolucionToClose.getIncidencia().getIncidenciaId());
                // Cierre incidPepePlazuelas5_2.
                Thread.sleep(1000);
                IncidenciaServ.closeIncidencia(resolucionToClose);

                Thread.sleep(1000);
                FragmentManager.enableDebugLogging(true);

            } catch (UiAppException | InterruptedException | IOException | UiException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Intent getActivityIntent()
        {
            return super.getActivityIntent();
        }
    };

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(4000);
    }

    @Before
    public void setUp() throws Exception
    {
        whatToClean = CLEAN_PEPE;
        mActivity = activityRule.getActivity();
        mFragment = (IncidSeeByComuListFr) mActivity.getSupportFragmentManager().findFragmentByTag(incid_see_by_comu_list_fr_tag);
        Thread.sleep(2000);
        mAdapter = (IncidSeeClosedByComuAdapter) mFragment.mAdapter;
        dbHelper = new IncidenciaDataDbHelper(mActivity);
        assertThat(mActivity.mComunidadSelected, is(mPepeLaFuente.getComunidad()));
    }

    @After
    public void tearDown() throws Exception
    {
        dbHelper.dropAllTables();
        dbHelper.close();
        String dBFileName = DB_PATH.concat(IncidenciaDataDbHelper.DB_NAME);
        deleteDatabase(new File(dBFileName));
        cleanOptions(whatToClean);
    }

    @Test
    public void testOnData_1() throws Exception
    {
        // CASO OK: muestra las incidencias de la comunidad por defecto (Calle La Fuente).
        Thread.sleep(1000);
        assertThat(mAdapter.getCount(), is(1));

        IncidenciaUser incidUser_1 = mAdapter.getItem(0);
        ViewMatchers.assertThat(incidUser_1.getIncidencia(), is(incidPepeLaFuente.getIncidencia()));
        onData(is(incidUser_1)).inAdapterView(withId(android.R.id.list)).check(matches(isDisplayed()));

         /* Datos a la vista de IncidSeeByComuListFr.*/
        showDataIncidList(incidUser_1);
    }

    @Test
    public void testOnData_2() throws Exception
    {
        // CASO OK: cambiamos la comunidad en el spinner y revisamos los datos.
        onView(withId(R.id.incid_reg_comunidad_spinner)).perform(click());
        onData(allOf(
                is(instanceOf(Comunidad.class)),
                is(mPepePlazuelas5.getComunidad()))
        ).check(matches(isDisplayed())).perform(click());

        assertThat(mAdapter.getCount(), is(2));
        IncidenciaUser incidUser_1 = mAdapter.getItem(0);
        IncidenciaUser incidUser_2 = mAdapter.getItem(1);

        ViewMatchers.assertThat(incidUser_1.getIncidencia(), is(incidPepePlazuelas5_1.getIncidencia()));
        onData(is(incidUser_1)).inAdapterView(withId(android.R.id.list)).check(matches(isDisplayed()));

        ViewMatchers.assertThat(incidUser_2.getIncidencia(), is(incidPepePlazuelas5_2.getIncidencia()));
        onData(is(incidUser_2)).inAdapterView(withId(android.R.id.list)).check(matches(isDisplayed()));
    }

    @Test
    public void testOnSelectedWithDoubleUp() throws UiAppException
    {
        // CASO OK: seleccionamos 2ª comunidad en spinner y la 2ª incidencia.

        onView(withId(R.id.incid_reg_comunidad_spinner)).perform(click());
        onData(allOf(
                is(instanceOf(Comunidad.class)),
                is(mPepePlazuelas5.getComunidad()))
        ).perform(click());

        IncidenciaUser incidUser = mAdapter.getItem(1);
        onData(is(incidUser)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withId(R.id.incid_resolucion_see_fr_layout)).check(matches(isDisplayed()));

        // Verificamos argumentos del nuevo fragmento.
        IncidResolucionSeeFr resolucionSeeFr = (IncidResolucionSeeFr) mActivity.getSupportFragmentManager()
                .findFragmentByTag(incid_resolucion_see_fr_tag);
        assertThat(resolucionSeeFr, notNullValue());
        Bundle args = resolucionSeeFr.getArguments();
        assertThat(args.size(), is(3));
        assertThat((Incidencia) args.getSerializable(IncidBundleKey.INCIDENCIA_OBJECT.key), is(incidPepePlazuelas5_2.getIncidencia()));
        assertThat((Resolucion) args.getSerializable(IncidBundleKey.INCID_RESOLUCION_OBJECT.key), is(mResolucionToCheck));
        assertThat(args.getBoolean(IS_MENU_IN_FRAGMENT_FLAG.key), is(true));

        // Probamos el menú del nuevo fragmento.
        onView(withText(R.string.incid_comments_see_ac_mn)).check(matches(isDisplayed())).perform(click());
        // Verificamos cambio a 'Comentarios'.
        onView(withId(R.id.incid_comments_see_fr_layout)).check(matches(isDisplayed()));
        intended(hasExtra(is(INCIDENCIA_OBJECT.key), is(incidPepePlazuelas5_2.getIncidencia())));
        // Comentarios no muestra menú porque la incidencia está cerrada.
        assertThat(IncidenciaServ.seeResolucion(incidPepePlazuelas5_2.getIncidencia().getIncidenciaId()).getIncidencia().getFechaCierre(),
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
        Thread.sleep(1000);
        IncidenciaUser incidUser = mAdapter.getItem(0);

        // Seleccionamos una incidencia.
        onData(is(incidUser)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.incid_resolucion_see_fr_layout)).check(matches(isDisplayed()));

        // BACK.
        onView(withId(R.id.incid_resolucion_see_fr_layout)).check(matches(isDisplayed())).perform(pressBack());
        /* Datos a la vista de IncidSeeByComuListFr.*/
        showDataIncidList(incidUser);
    }

    @Test
    public void testOnSelectedWithUp() throws InterruptedException
    {
        // CASO: verificación de la navegación,

        // Comunidad por defecto.
        Thread.sleep(3000);
        IncidenciaUser incidUser = mAdapter.getItem(0);

        // Seleccionamos una incidencia.
        onData(is(incidUser)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.incid_resolucion_see_fr_layout)).check(matches(isDisplayed()));

        // Up Navigation:
        clickNavigateUp();
        /* Datos a la vista de IncidSeeByComuListFr.*/
        showDataIncidList(incidUser);
    }

//    ===================================== HELPERS =====================================

    private void showDataIncidList(IncidenciaUser incidUser)
    {
        onView(allOf(
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
                                withText(mActivity.getResources()
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
        )).check(matches(isDisplayed()));
    }
}