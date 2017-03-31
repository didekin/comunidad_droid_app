package com.didekindroid.incidencia.list.open;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.FragmentManager;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.list.close.IncidSeeCloseByComuFr;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
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

import java.io.IOException;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.core.IncidenciaDataDbHelper.DB_NAME;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidenciaUser;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.incidencia.utils.IncidFragmentTags.incid_see_by_comu_list_fr_tag;
import static com.didekindroid.security.SecurityTestUtils.updateSecurityData;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.testutil.ActivityTestUtils.getAdapterCount;
import static com.didekindroid.testutil.ActivityTestUtils.isViewDisplayed;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.makeUsuarioComunidad;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.model.usuariocomunidad.Rol.PROPIETARIO;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;

/**
 * User: pedro@didekin
 * Date: 23/11/15
 * Time: 18:45
 */
/* Tests con usuario sin incidImportancia asociado a la incidencia. */
@RunWith(AndroidJUnit4.class)
public class IncidSeeOpenByComuAcTest_2 {

    UsuarioComunidad userComuJuan;
    /**
     * Preconditions:
     * 1. Incidencia without resolucion in BD.
     * 2. User without authority 'adm' and without incidImportancia record in DB.
     * Condition:
     * 1. User select an incidencia.
     * Postconditions:
     * 1. The incidencia is shown in edit mode (importancia field).
     * 2. An intent is passed whith:
     * - the incidImportancia instance of the user in session.
     * - a false value in the resolucion flag.
     */
    @Rule
    public IntentsTestRule<IncidSeeOpenByComuAc> activityRule = new IntentsTestRule<IncidSeeOpenByComuAc>(IncidSeeOpenByComuAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            try {
                signUpAndUpdateTk(COMU_REAL_PEPE);
                UsuarioComunidad pepeUserComu = userComuDaoRemote.seeUserComusByUser().get(0);
                // Insertamos incidencia.
                insertGetIncidenciaUser(pepeUserComu, 1);
                // Registro userComu en misma comunidad.
                userComuJuan = makeUsuarioComunidad(pepeUserComu.getComunidad(), USER_JUAN,
                        "portal", "esc", "plantaX", "door12", PROPIETARIO.function);
                userComuDaoRemote.regUserAndUserComu(userComuJuan).execute();
                updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
            } catch (IOException | UiException e) {
                e.printStackTrace();
            }
            FragmentManager.enableDebugLogging(true);
        }
    };

    CtrlerIncidSeeOpenByComu controller;
    private AdapterIncidSeeOpenByComu adapter;
    IncidSeeOpenByComuAc activity;

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(2000);
    }

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        IncidSeeCloseByComuFr fragmentByTag = (IncidSeeCloseByComuFr) activity.getSupportFragmentManager()
                .findFragmentByTag(incid_see_by_comu_list_fr_tag);
//        controller = (CtrlerIncidSeeOpenByComu) fragmentByTag.controllerSeeIncids;    TODO: quitar controller.
        adapter = (AdapterIncidSeeOpenByComu) controller.adapter;
    }

    @After
    public void tearDown() throws Exception
    {

        activity.deleteDatabase(DB_NAME);
        cleanOptions(CLEAN_JUAN_AND_PEPE);
    }

    @Test
    public void testOnSelected_1() throws UiException, InterruptedException
    {

        // CASO OK
        // Default comunidad (Real), in position 0, is selected.
        waitAtMost(3, SECONDS).until(getAdapterCount(adapter), is(1));

        IncidenciaUser incidUser_0 = adapter.getItem(0);
        Incidencia incidencia_0 = incidUser_0.getIncidencia();
        // Usuario Juan ve la incidencia por usuario Pepe.
        onData(is(incidUser_0)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()))
                .perform(click());

        // Verificamos intents.
        IncidAndResolBundle incidAndResolBundle = incidenciaDao.seeIncidImportancia(incidencia_0.getIncidenciaId());
        IncidImportancia incidImportancia = incidAndResolBundle.getIncidImportancia();
        assertThat(incidImportancia.getUserComu(), is(userComuJuan));
        assertThat(incidAndResolBundle.hasResolucion(), is(false));
        intended(hasExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia));
        intended(hasExtra(INCID_RESOLUCION_FLAG.key, incidAndResolBundle.hasResolucion()));

        // Juan entra en la pantalla de edición de la incidencia, tras seleccionarla.
        onView(withId(R.id.incid_edit_fragment_container_ac)).check(matches(isDisplayed()));
        // Navigate-up.
        checkUp(R.id.incid_see_open_by_comu_ac, R.id.incid_see_generic_layout);
    }

    @Test
    public void testUpNavigate()
    {
        // CASO OK: probamos UP navigation.
        waitAtMost(3, SECONDS).until(getAdapterCount(adapter), is(1));
        IncidenciaUser incidUser_0 = adapter.getItem(0);
        // Usuario Juan ve la incidencia por usuario Pepe.
        onData(is(incidUser_0)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()))
                .perform(click());

        // Juan entra en la pantalla de edición de la incidencia, y pulsa Up.
        waitAtMost(2, SECONDS).until(isViewDisplayed(withId(R.id.incid_edit_fragment_container_ac)));

        clickNavigateUp();
        waitAtMost(2, SECONDS).until(isViewDisplayed(withId(R.id.incid_see_generic_layout)));
        waitAtMost(3, SECONDS).until(getAdapterCount(adapter), is(1));
    }

    @Test
    public void testPressBack() throws InterruptedException     // TODO.
    {
        // CASO OK: probamos BACK navigation.
        waitAtMost(3, SECONDS).until(getAdapterCount(adapter), is(1));
        IncidenciaUser incidUser_0 = adapter.getItem(0);
        // Usuario Juan ve la incidencia puesta por usuario Pepe.
        onData(is(incidUser_0)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()))
                .perform(click());

        // Juan entra en la pantalla de edición de la incidencia, y pulsa BACK.
        waitAtMost(2, SECONDS).until(isViewDisplayed(withId(R.id.incid_edit_nopower_fr_layout)));
        onView(withId(R.id.incid_edit_nopower_fr_layout)).perform(pressBack());

        waitAtMost(2, SECONDS).until(isViewDisplayed(withId(R.id.incid_see_generic_layout)));
        waitAtMost(3, SECONDS).until(getAdapterCount(adapter), is(1));
    }
}