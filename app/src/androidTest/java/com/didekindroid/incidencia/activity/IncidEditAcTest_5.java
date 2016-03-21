package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;

import com.didekin.incidservice.dominio.IncidAndResolBundle;
import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.Incidencia;
import com.didekin.incidservice.dominio.Resolucion;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.incidencia.repository.IncidenciaDataDbHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;

import static android.database.sqlite.SQLiteDatabase.deleteDatabase;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.common.activity.IntentExtraKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.common.activity.IntentExtraKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkNavigateUp;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.incidencia.repository.IncidenciaDataDbHelperTest.DB_PATH;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.INCID_DEFAULT_DESC;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.RESOLUCION_DEFAULT_DESC;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doIncidencia;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doResolucion;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 17/03/16
 * Time: 15:51
 */
public class IncidEditAcTest_5 {

    IncidEditAc mActivity;
    IncidAndResolBundle incidResolPepeEscorial;

    @Rule
    public IntentsTestRule<IncidEditAc> intentRule = new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            super.beforeActivityLaunched();
        }

        /**
         * Preconditions:
         * 1. An IncidenciaUser with powers to modify and to erase is received.
         * 2. There is resolucion in BD.
         * Postconditions:
         * 1. Erase button is NOT shown.
         * */
        @Override
        protected Intent getActivityIntent()
        {
            try {
                signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
                UsuarioComunidad pepeEscorial = ServOne.seeUserComusByUser().get(0);
                IncidImportancia incidPepeEscorial = new IncidImportancia.IncidImportanciaBuilder(
                        doIncidencia(pepeEscorial.getUsuario().getUserName(), INCID_DEFAULT_DESC, pepeEscorial.getComunidad().getC_Id(), (short) 43))
                        .usuarioComunidad(pepeEscorial)
                        .importancia((short) 3)
                        .build();
                IncidenciaServ.regIncidImportancia(incidPepeEscorial);
                Incidencia incidenciaDb = IncidenciaServ.seeIncidsOpenByComu(pepeEscorial.getComunidad().getC_Id()).get(0).getIncidencia();
                // Registramos resolución.
                Thread.sleep(1000);
                Resolucion resolucion = doResolucion(incidenciaDb, RESOLUCION_DEFAULT_DESC, 1000, new Timestamp(new Date().getTime()));
                assertThat(IncidenciaServ.regResolucion(resolucion), is(1));
                incidResolPepeEscorial = IncidenciaServ.seeIncidImportancia(incidenciaDb.getIncidenciaId());
            } catch (UiException | InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent();
            intent.putExtra(INCID_IMPORTANCIA_OBJECT.extra, incidResolPepeEscorial.getIncidImportancia());
            intent.putExtra(INCID_RESOLUCION_FLAG.extra, incidResolPepeEscorial.hasResolucion());
            return intent;
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
        mActivity = intentRule.getActivity();
        // Premisas.
        IncidImportancia incidImportancia = (IncidImportancia) mActivity.getIntent().getSerializableExtra(INCID_IMPORTANCIA_OBJECT.extra);
        assertThat(incidImportancia.getUserComu().hasAdministradorAuthority(), is(true));
        boolean hasResolucionInBd = mActivity.getIntent().getBooleanExtra(INCID_RESOLUCION_FLAG.extra, false);
        assertThat(hasResolucionInBd, is(true));
    }

    @After
    public void tearDown() throws Exception
    {
        String dBFileName = DB_PATH.concat(IncidenciaDataDbHelper.DB_NAME);
        deleteDatabase(new File(dBFileName));
        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public void testOnCreate() throws Exception
    {
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        ViewMatchers.assertThat(mActivity.findViewById(R.id.incid_edit_maxpower_fr_layout), notNullValue());
        onView(withId(R.id.incid_edit_fragment_container_ac)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_edit_maxpower_fr_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_comunidad_rot)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_comunidad_txt)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_reg_desc_ed)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_reg_ambito_spinner)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_reg_importancia_spinner)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_edit_fr_modif_button)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_comunidad_txt)).check(matches(isDisplayed()));

        // Usuario con autoridad adm, hay resolución en BD: la pantalla NO presenta el botón de borrar.
        onView(withId(R.id.incid_edit_fr_borrar_txt)).check(matches(not(isDisplayed())));
        onView(withId(R.id.incid_edit_fr_borrar_button)).check(matches(not(isDisplayed())));

        checkNavigateUp();
    }
}