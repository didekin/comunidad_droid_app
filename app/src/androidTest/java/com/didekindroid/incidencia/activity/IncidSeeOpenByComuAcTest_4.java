package com.didekindroid.incidencia.activity;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.incidservice.dominio.IncidAndResolBundle;
import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekin.incidservice.dominio.Resolucion;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
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
import java.util.Date;

import static android.database.sqlite.SQLiteDatabase.deleteDatabase;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekin.usuario.dominio.Rol.PROPIETARIO;
import static com.didekindroid.common.activity.BundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.common.activity.BundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.common.activity.FragmentTags.incid_see_by_comu_list_fr_tag;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.testutils.ActivityTestUtils.updateSecurityData;
import static com.didekindroid.incidencia.repository.IncidenciaDataDbHelperTest.DB_PATH;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.RESOLUCION_DEFAULT_DESC;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doResolucion;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.insertGetIncidenciaUser;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.makeUsuarioComunidad;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 17/03/16
 * Time: 15:41
 */
@RunWith(AndroidJUnit4.class)
public class IncidSeeOpenByComuAcTest_4 {

    private CleanUserEnum whatToClean = CLEAN_JUAN_AND_PEPE;
    UsuarioComunidad pepeUserComu;
    UsuarioComunidad userComuJuan;
    private IncidSeeOpenByComuAdapter adapter;

    private Resolucion resolucion;
    @Rule
    public IntentsTestRule<IncidSeeOpenByComuAc> activityRule = new IntentsTestRule<IncidSeeOpenByComuAc>(IncidSeeOpenByComuAc.class) {

        /**
         * Preconditions:
         * 1. Incidencia with resolucion in BD.
         * 2. User without authority 'adm'.
         * Condition:
         * 1. User select an incidencia.
         * Postconditions:
         * 1. The incidencia is shown in edit mode.
         * 2. An intent is passed whith:
         *    - the incidImportancia instance of the user in session.
         *    - a true value in the resolucion flag.
         */
        @Override
        protected void beforeActivityLaunched()
        {
            try {
                signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
                pepeUserComu = ServOne.seeUserComusByUser().get(0);
                // Insertamos incidencia.
                IncidenciaUser incidenciaUser = insertGetIncidenciaUser(pepeUserComu, 1);
                // Insertamos resolución.
                resolucion = doResolucion(incidenciaUser.getIncidencia(), RESOLUCION_DEFAULT_DESC, 1000, new Timestamp(new Date().getTime()));
                assertThat(IncidenciaServ.regResolucion(resolucion), is(1));
                // Registro userComu en misma comunidad.
                userComuJuan = makeUsuarioComunidad(pepeUserComu.getComunidad(), USER_JUAN,
                        "portal", "esc", "plantaX", "door12", PROPIETARIO.function);
                ServOne.regUserAndUserComu(userComuJuan).execute();
                updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
            } catch (UiException | IOException e) {
                e.printStackTrace();
            }
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
        // Premisas.
        assertThat(userComuJuan.hasAdministradorAuthority(), is(false));
        assertThat(IncidenciaServ.seeResolucion(resolucion.getIncidencia().getIncidenciaId()),notNullValue());
    }

    @After
    public void tearDown() throws Exception
    {
        String dBFileName = DB_PATH.concat(IncidenciaDataDbHelper.DB_NAME);
        deleteDatabase(new File(dBFileName));
        cleanOptions(whatToClean);
    }

    @Test
    public void testOnSelected_1() throws Exception
    {
        // Caso OK: comunidad Escorial es mostrada. El usuario la selecciona.
        IncidenciaUser incidenciaUser = adapter.getItem(0);
        onData(is(incidenciaUser)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()))
                .perform(click());

        IncidAndResolBundle incidAndResolBundle = IncidenciaServ.seeIncidImportancia(incidenciaUser.getIncidencia().getIncidenciaId());
        IncidImportancia incidImportancia = incidAndResolBundle.getIncidImportancia();
        assertThat(incidImportancia.getUserComu(), is(userComuJuan));
        assertThat(incidAndResolBundle.hasResolucion(),is(true));
        intended(hasExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia));
        intended(hasExtra(INCID_RESOLUCION_FLAG.key, incidAndResolBundle.hasResolucion()));
        // Juan entra en la pantalla de edición de la incidencia, tras seleccionarla.
        onView(withId(R.id.incid_edit_fragment_container_ac)).check(matches(isDisplayed()));
    }
}