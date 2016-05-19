package com.didekindroid.incidencia.activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.Incidencia;
import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekin.incidservice.dominio.Resolucion;
import com.didekin.usuario.dominio.Comunidad;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.BundleKey;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.incidencia.repository.IncidenciaDataDbHelper;
import com.didekindroid.usuario.testutils.CleanUserEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static android.database.sqlite.SQLiteDatabase.deleteDatabase;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.common.activity.BundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.common.activity.BundleKey.IS_MENU_IN_FRAGMENT_FLAG;
import static com.didekindroid.common.activity.FragmentTags.incid_resolucion_see_fr_tag;
import static com.didekindroid.common.activity.FragmentTags.incid_see_by_comu_list_fr_tag;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkNavigateUp;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.regSeveralUserComuSameUser;
import static com.didekindroid.common.utils.UIutils.formatTimeStampToString;
import static com.didekindroid.incidencia.repository.IncidenciaDataDbHelperTest.DB_PATH;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.RESOLUCION_DEFAULT_DESC;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doIncidencia;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doResolucionAvances;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_LA_FUENTE_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_PLAZUELA5_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.USER_PEPE;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
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
@SuppressWarnings("unchecked")
@RunWith(AndroidJUnit4.class)
public class IncidSeeClosedByComuAcTest_2 {

    private IncidSeeClosedByComuAc mActivity;
    private CleanUserEnum whatToClean;
    private UsuarioComunidad mPepePlazuelas5;
    private UsuarioComunidad mPepeLaFuente;
    private IncidImportancia incidPepePlazuelas5_1;
    private IncidImportancia incidPepePlazuelas5_2;
    private IncidImportancia incidPepeLaFuente;
    IncidSeeByComuListFr mFragment;
    IncidenciaDataDbHelper dbHelper;
    IncidSeeClosedByComuAdapter mAdapter;
    private Resolucion mResolucionToCheck;

    @Rule
    public IntentsTestRule<IncidSeeClosedByComuAc> activityRule = new IntentsTestRule<IncidSeeClosedByComuAc>(IncidSeeClosedByComuAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            try {
                regSeveralUserComuSameUser(COMU_PLAZUELA5_PEPE, COMU_LA_FUENTE_PEPE);
                mPepeLaFuente = ServOne.seeUserComusByUser().get(0);
                mPepePlazuelas5 = ServOne.seeUserComusByUser().get(1);
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
                Resolucion resolucionToClose = doResolucionAvances(incidPepePlazuelas5_1.getIncidencia(), RESOLUCION_DEFAULT_DESC, 231, doTimeStamp(1));
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
                resolucionToClose = doResolucionAvances(incidPepeLaFuente.getIncidencia(), RESOLUCION_DEFAULT_DESC, 321, doTimeStamp(1));
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
                resolucionToClose = doResolucionAvances(incidPepePlazuelas5_2.getIncidencia(), RESOLUCION_DEFAULT_DESC, 334, doTimeStamp(1));
                mResolucionToCheck = resolucionToClose;
                assertThat(IncidenciaServ.regResolucion(resolucionToClose), is(1));
                resolucionToClose = IncidenciaServ.seeResolucion(resolucionToClose.getIncidencia().getIncidenciaId());
                // Cierre incidPepePlazuelas5_2.
                Thread.sleep(1000);
                IncidenciaServ.closeIncidencia(resolucionToClose);

                Thread.sleep(1000);
                FragmentManager.enableDebugLogging(true);

            } catch (UiException | InterruptedException | IOException e) {
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
        mFragment = (IncidSeeByComuListFr) mActivity.getSupportFragmentManager()
                .findFragmentByTag(incid_see_by_comu_list_fr_tag);
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
    public void testOnSelected_1()
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
        assertThat((Incidencia) args.getSerializable(BundleKey.INCIDENCIA_OBJECT.key), is(incidPepePlazuelas5_2.getIncidencia()));
        assertThat((Resolucion) args.getSerializable(BundleKey.INCID_RESOLUCION_OBJECT.key), is(mResolucionToCheck));
        assertThat(args.getBoolean(IS_MENU_IN_FRAGMENT_FLAG.key), is(true));

        // Probamos el menú del nuevo fragmento.
        onView(withText(R.string.incid_comments_see_ac_mn)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.incid_comments_see_fr_layout)).check(matches(isDisplayed()));
        intended(hasExtra(is(INCIDENCIA_OBJECT.key), is(incidPepePlazuelas5_2.getIncidencia())));
    }

    @Test
    public void testOnSelected_2() throws InterruptedException
    {
        // CASO: verificación de la navegación, Utilizamos PressBack.

        // Comunidad por defecto.
        Thread.sleep(1000);
        IncidenciaUser incidUser = mAdapter.getItem(0);
        // Seleccionamos una incidencia.
        onData(is(incidUser)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()))
                .perform(click());

        // BACK.
        onView(withId(R.id.incid_resolucion_see_fr_layout)).check(matches(isDisplayed())).perform(pressBack());

        /* Datos a la vista de IncidSeeByComuListFr.*/
        showDataIncidList(incidUser);
    }

    @Test
    public void testOnSelected_3() throws InterruptedException
    {
        // CASO: verificación de la navegación,

        // Comunidad por defecto.
        Thread.sleep(1000);
        IncidenciaUser incidUser = mAdapter.getItem(0);

        // Seleccionamos una incidencia.
        onData(is(incidUser)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()))
                .perform(click());

        // Up Navigation:
        checkNavigateUp();

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

    private static Timestamp doTimeStamp(int daysToAdd)
    {
        Calendar fCierre = new GregorianCalendar();
        fCierre.add(Calendar.DAY_OF_MONTH, daysToAdd);
        return new Timestamp(fCierre.getTimeInMillis());
    }
}
/*ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);*/