package com.didekindroid.incidencia.activity;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.FragmentManager;

import com.didekin.incidencia.dominio.IncidAndResolBundle;
import com.didekin.incidencia.dominio.IncidImportancia;
import com.didekin.incidencia.dominio.Incidencia;
import com.didekin.incidencia.dominio.IncidenciaUser;
import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekinaar.exception.UiException;
import com.didekinaar.usuario.testutil.UsuarioDataTestUtils;
import com.didekindroid.R;
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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekin.usuariocomunidad.Rol.PROPIETARIO;
import static com.didekinaar.testutil.AarActivityTestUtils.checkUp;
import static com.didekinaar.testutil.AarActivityTestUtils.clickNavigateUp;
import static com.didekinaar.testutil.AarTestUtil.updateSecurityData;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.incidencia.IncidService.IncidenciaServ;
import static com.didekindroid.incidencia.IncidenciaDataDbHelperTest.DB_PATH;
import static com.didekindroid.incidencia.IncidenciaTestUtils.insertGetIncidenciaUser;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.incidencia.activity.utils.IncidFragmentTags.incid_see_by_comu_list_fr_tag;
import static com.didekindroid.usuariocomunidad.UserComuService.AppUserComuServ;
import static com.didekindroid.usuariocomunidad.UserComuTestUtil.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.UserComuTestUtil.makeUsuarioComunidad;
import static com.didekindroid.usuariocomunidad.UserComuTestUtil.signUpAndUpdateTk;
import static org.hamcrest.CoreMatchers.is;

/**
 * User: pedro@didekin
 * Date: 23/11/15
 * Time: 18:45
 */
/* Tests con usuario sin incidImportancia asociado a la incidencia. */
@RunWith(AndroidJUnit4.class)
public class IncidSeeOpenByComuAcTest_3 {

    private UsuarioDataTestUtils.CleanUserEnum whatToClean = CLEAN_JUAN_AND_PEPE;
    private IncidSeeOpenByComuAdapter adapter;
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
                UsuarioComunidad pepeUserComu = AppUserComuServ.seeUserComusByUser().get(0);
                // Insertamos incidencia.
                insertGetIncidenciaUser(pepeUserComu, 1);
                // Registro userComu en misma comunidad.
                userComuJuan = makeUsuarioComunidad(pepeUserComu.getComunidad(), USER_JUAN,
                        "portal", "esc", "plantaX", "door12", PROPIETARIO.function);
                AppUserComuServ.regUserAndUserComu(userComuJuan).execute();
                updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
            } catch (IOException | UiException e) {
                e.printStackTrace();
            }
            FragmentManager.enableDebugLogging(true);
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
        IncidSeeOpenByComuAc mActivity = activityRule.getActivity();
        IncidSeeByComuListFr mFragment = (IncidSeeByComuListFr) mActivity.getSupportFragmentManager()
                .findFragmentByTag(incid_see_by_comu_list_fr_tag);
        Thread.sleep(2000);
        adapter = (IncidSeeOpenByComuAdapter) mFragment.mAdapter;
    }

    @After
    public void tearDown() throws Exception
    {
        String dBFileName = DB_PATH.concat(IncidenciaDataDbHelper.DB_NAME);
        deleteDatabase(new File(dBFileName));
        cleanOptions(whatToClean);
    }

    @Test
    public void testOnSelected_1() throws UiException, InterruptedException
    {

        // CASO OK
        // Default comunidad (Real), in position 0, is selected.
        Thread.sleep(1000);
        IncidenciaUser incidUser_0 = adapter.getItem(0);
        Incidencia incidencia_0 = incidUser_0.getIncidencia();
        // Usuario Juan ve la incidencia por usuario Pepe.
        onData(is(incidUser_0)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()))
                .perform(click());

        // Verificamos intents.
        IncidAndResolBundle incidAndResolBundle = IncidenciaServ.seeIncidImportancia(incidencia_0.getIncidenciaId());
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
        IncidenciaUser incidUser_0 = adapter.getItem(0);
        // Usuario Juan ve la incidencia por usuario Pepe.
        onData(is(incidUser_0)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()))
                .perform(click());

        // Juan entra en la pantalla de edición de la incidencia, y pulsa Up.
        onView(withId(R.id.incid_edit_fragment_container_ac)).check(matches(isDisplayed()));
        clickNavigateUp();
        onView(withId(R.id.incid_see_generic_layout)).check(matches(isDisplayed()));
        onData(is(incidUser_0)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testPressBack() throws InterruptedException
    {
        // CASO OK: probamos BACK navigation.
        Thread.sleep(1000);
        IncidenciaUser incidUser_0 = adapter.getItem(0);
        // Usuario Juan ve la incidencia puesta por usuario Pepe.
        onData(is(incidUser_0)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()))
                .perform(click());

        // Juan entra en la pantalla de edición de la incidencia, y pulsa BACK.
        onView(withId(R.id.incid_edit_nopower_fr_layout)).check(matches(isDisplayed())).perform(pressBack());
        onView(withId(R.id.incid_see_generic_layout)).check(matches(isDisplayed()));
        onData(is(incidUser_0)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()));
    }
}