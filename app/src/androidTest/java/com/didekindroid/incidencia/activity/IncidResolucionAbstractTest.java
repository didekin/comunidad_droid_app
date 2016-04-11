package com.didekindroid.incidencia.activity;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.v4.app.Fragment;

import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.Incidencia;
import com.didekin.incidservice.dominio.Resolucion;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.usuario.testutils.CleanUserEnum;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;

import java.sql.Timestamp;
import java.util.Locale;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.common.activity.BundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.common.activity.BundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.common.activity.FragmentTags.incid_resolucion_ac_frgs_tag;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.utils.UIutils.SPAIN_LOCALE;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.CALENDAR_DEFAULT;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.CALENDAR_DEFAULT_STRING;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.COSTE_ESTIM_DEFAULT;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.COSTE_ESTIM_DEFAULT_String;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.INCID_DEFAULT_DESC;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.RESOLUCION_DEFAULT_DESC;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doIncidencia;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doResolucion;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * User: pedro@didekin
 * Date: 02/04/16
 * Time: 12:58
 */
public abstract class IncidResolucionAbstractTest {

    IncidResolucionRegEditSeeAc mActivity;
    Fragment incidEditFr;
    IncidImportancia incidImportancia;
    Resolucion resolucion;
    IncidImportancia mIncidImportanciaIntent;
    Resolucion mResolucionIntent;

    @Rule
    public IntentsTestRule<IncidResolucionRegEditSeeAc> intentRule = doIntentRule();

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(4000);
    }

    @Before
    public void setUp() throws Exception
    {
        mActivity = intentRule.getActivity();
        assertThat(mActivity, notNullValue());
        incidEditFr = mActivity.getSupportFragmentManager().findFragmentByTag(incid_resolucion_ac_frgs_tag);
        assertThat(incidEditFr, notNullValue());
        // Intent extras in activity.
        mIncidImportanciaIntent = (IncidImportancia) mActivity.getIntent().getSerializableExtra(INCID_IMPORTANCIA_OBJECT.key);
        mResolucionIntent = (Resolucion) mActivity.getIntent().getSerializableExtra(INCID_RESOLUCION_OBJECT.key);

        // Premisas.
        if (mIncidImportanciaIntent.getUserComu().hasAdministradorAuthority() && mResolucionIntent != null) {
            assertThat(incidEditFr, instanceOf(IncidResolucionEditFr.class));
        } else if (mIncidImportanciaIntent.getUserComu().hasAdministradorAuthority() && mResolucionIntent == null) {
            assertThat(incidEditFr, instanceOf(IncidResolucionRegFr.class));
        } else if (!mIncidImportanciaIntent.getUserComu().hasAdministradorAuthority() && mResolucionIntent != null) {
            assertThat(incidEditFr, instanceOf(IncidResolucionSeeFr.class));
        } else {
            assertThat(incidEditFr, instanceOf(IncidResolucionSeeDefaultFr.class));
        }
        assertThat(incidEditFr.getArguments(), allOf(
                hasEntry(INCID_IMPORTANCIA_OBJECT.key, is(mIncidImportanciaIntent)),
                hasEntry(INCID_RESOLUCION_OBJECT.key, is(mResolucionIntent))
        ));
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(whatToClean());
    }

    abstract IntentsTestRule<IncidResolucionRegEditSeeAc> doIntentRule();

    abstract CleanUserEnum whatToClean();

    //  ===============================  HELPER METHODS ================================

    void doIncidImportancia(UsuarioComunidad userComu) throws UiException
    {
        signUpAndUpdateTk(userComu);
        UsuarioComunidad userComuDb = ServOne.seeUserComusByUser().get(0);
        incidImportancia = new IncidImportancia.IncidImportanciaBuilder(
                doIncidencia(userComuDb.getUsuario().getUserName(), INCID_DEFAULT_DESC, userComuDb.getComunidad().getC_Id(), (short) 43))
                .usuarioComunidad(userComuDb)
                .importancia((short) 3).build();
        IncidenciaServ.regIncidImportancia(incidImportancia);
        Incidencia incidenciaDb = IncidenciaServ.seeIncidsOpenByComu(userComuDb.getComunidad().getC_Id()).get(0).getIncidencia();
        incidImportancia = IncidenciaServ.seeIncidImportancia(incidenciaDb.getIncidenciaId()).getIncidImportancia();
    }

    void doResolucionNoAdvances() throws UiException
    {
        // Registramos resolución.
        resolucion = doResolucion(incidImportancia.getIncidencia(),
                RESOLUCION_DEFAULT_DESC,
                COSTE_ESTIM_DEFAULT,
                new Timestamp(CALENDAR_DEFAULT.getTimeInMillis()));
        Assert.assertThat(IncidenciaServ.regResolucion(resolucion), is(1));
        resolucion = IncidenciaServ.seeResolucion(resolucion.getIncidencia().getIncidenciaId());
    }

    void checkScreenResolucionEditFr()
    {
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));

        onView(withId(R.id.incid_resolucion_fragment_container_ac)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_edit_fr_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_fecha_view)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_avance_ed)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_fr_modif_button)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_edit_fr_close_button)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_txt)).check(matches(isDisplayed()));

        if (mResolucionIntent != null && mResolucionIntent.getAvances().size() > 0) {
            // Lista NO vacía.
            onView(withId(android.R.id.list)).check(matches(isDisplayed()));
            onView(withId(android.R.id.empty)).check(matches(not(isDisplayed())));
        } else {
            // Lista de avances vacía.
            onView(withId(android.R.id.list)).check(matches(not(isDisplayed())));
            onView(withId(android.R.id.empty)).check(matches(isDisplayed()));
        }
    }

    void checkDataResolucionEditFr()
    {
        // Caso: los datos que se muestran por defecto.
        // Fecha.
        if (Locale.getDefault().equals(SPAIN_LOCALE)) {
            onView(allOf(
                    withId(R.id.incid_resolucion_fecha_view),
                    withText(CALENDAR_DEFAULT_STRING)
            )).check(matches(isDisplayed()));
        }
        // Coste.
        onView(allOf(
                withId(R.id.incid_resolucion_coste_prev_ed),
                withText(COSTE_ESTIM_DEFAULT_String)
        )).check(matches(isDisplayed()));
        // Resolución.
        onView(allOf(
                withId(R.id.incid_resolucion_txt),
                withText(RESOLUCION_DEFAULT_DESC)
        )).check(matches(isDisplayed()));
    }

    void checkScreenResolucionRegFr()
    {
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));

        onView(withId(R.id.incid_resolucion_fragment_container_ac)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_reg_frg_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_reg_ac_button)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_desc_ed)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_fecha_view)).check(matches(isDisplayed()));
    }

    void checkScreenResolucionSeeFr()
    {
        onView(withId(R.id.incid_resolucion_fragment_container_ac)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_see_fr_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_fecha_view)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_coste_prev_view)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_txt)).check(matches(isDisplayed()));

        if (mResolucionIntent != null && mResolucionIntent.getAvances().size() > 0) {
            // Lista NO vacía.
            onView(withId(android.R.id.list)).check(matches(isDisplayed()));
            onView(withId(android.R.id.empty)).check(matches(not(isDisplayed())));
        } else {
            // Lista de avances vacía.
            onView(withId(android.R.id.list)).check(matches(not(isDisplayed())));
            onView(withId(android.R.id.empty)).check(matches(isDisplayed()));
        }
    }

    void checkDataResolucionSeeFr()
    {
        // Fecha.
        if (Locale.getDefault().equals(SPAIN_LOCALE)) {
            onView(allOf(
                    withId(R.id.incid_resolucion_fecha_view),
                    withText(CALENDAR_DEFAULT_STRING)
            )).check(matches(isDisplayed()));
        }
        // Coste.
        onView(allOf(
                withId(R.id.incid_resolucion_coste_prev_view),
                withText(COSTE_ESTIM_DEFAULT_String)
        )).check(matches(isDisplayed()));
        // Resolución.
        onView(allOf(
                withId(R.id.incid_resolucion_txt),
                withText(RESOLUCION_DEFAULT_DESC)
        )).check(matches(isDisplayed()));
    }

    void checkScreenResolucionSeeDefaultFr()
    {
        /* CASO OK: se muestra el fragmento/mensaje por defecto.*/
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_fragment_container_ac)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_see_default_fr)).check(matches(isDisplayed()));
    }
}

