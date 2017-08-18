package com.didekindroid.incidencia.resolucion;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.v4.app.Fragment;

import com.didekindroid.R;
import com.didekindroid.usuario.testutil.UsuarioDataTestUtils;
import com.didekindroid.util.UIutils;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import java.util.Locale;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.incidencia.utils.IncidFragmentTags.incid_resolucion_ac_frgs_tag;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.COSTE_ESTIM_DEFAULT_String;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.RESOLUCION_DEFAULT_DESC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.util.UIutils.SPAIN_LOCALE;
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

    @Rule
    public IntentsTestRule<IncidResolucionRegEditSeeAc> intentRule = doIntentRule();
    IncidResolucionRegEditSeeAc activity;
    Fragment incidEditFr;
    IncidImportancia incidImportancia;
    Resolucion resolucion;
    IncidImportancia mIncidImportanciaIntent;
    Resolucion mResolucionIntent;

    @Before
    public void setUp() throws Exception
    {
        activity = intentRule.getActivity();
        assertThat(activity, notNullValue());
        onView(withId(R.id.incid_resolucion_fragment_container_ac)).check(matches(isDisplayed()));
        incidEditFr = activity.getSupportFragmentManager().findFragmentByTag(incid_resolucion_ac_frgs_tag);
        assertThat(incidEditFr, notNullValue());
        // Intent extras in activity.
        mIncidImportanciaIntent = (IncidImportancia) activity.getIntent().getSerializableExtra(INCID_IMPORTANCIA_OBJECT.key);
        mResolucionIntent = (Resolucion) activity.getIntent().getSerializableExtra(INCID_RESOLUCION_OBJECT.key);

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

    abstract UsuarioDataTestUtils.CleanUserEnum whatToClean();

    //  ===============================  HELPER METHODS ================================

    void checkScreenResolucionEditFr()
    {
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));

        onView(withId(R.id.incid_resolucion_fragment_container_ac)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_edit_fr_layout)).check(matches(isDisplayed())).perform(closeSoftKeyboard());
        onView(withId(R.id.incid_resolucion_fecha_view)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_avance_ed)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_fr_modif_button)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_edit_fr_close_button)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_txt)).check(matches(isDisplayed()));

        if (mResolucionIntent != null && !mResolucionIntent.getAvances().isEmpty()) {
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
        if (Locale.getDefault().equals(SPAIN_LOCALE)) {  // TODO: internacionalizar.
            onView(allOf(
                    withId(R.id.incid_resolucion_fecha_view),
                    withText(UIutils.formatTimeStampToString(resolucion.getFechaPrev()))
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

        if (mResolucionIntent != null && !mResolucionIntent.getAvances().isEmpty()) {
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
                    withText(UIutils.formatTimeStampToString(resolucion.getFechaPrev()))
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

