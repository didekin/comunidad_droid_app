package com.didekindroid.incidencia.resolucion;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.v4.app.Fragment;

import com.didekindroid.R;
import com.didekindroid.usuario.testutil.UsuarioDataTestUtils;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * User: pedro@didekin
 * Date: 02/04/16
 * Time: 12:58
 */
public abstract class IncidResolucionAbstractTest {

    @Rule
    public IntentsTestRule<IncidResolucionEditAc> intentRule = doIntentRule();
    IncidResolucionEditAc activity;
    Fragment incidEditFr;
    IncidImportancia incidImportancia;
    Resolucion resolucion;
    IncidImportancia incidImportanciaIntent;
    Resolucion resolucionIntent;

    @Before
    public void setUp() throws Exception
    {
        activity = intentRule.getActivity();
        assertThat(activity, notNullValue());
        onView(withId(R.id.incid_resolucion_fragment_container_ac)).check(matches(isDisplayed()));
//        incidEditFr = activity.getSupportFragmentManager().findFragmentByTag(incid_resolucion_reg_fr_tag);  // TODO: descomentar.
        assertThat(incidEditFr, notNullValue());
        // Intent extras in activity.
        incidImportanciaIntent = (IncidImportancia) activity.getIntent().getSerializableExtra(INCID_IMPORTANCIA_OBJECT.key);
        resolucionIntent = (Resolucion) activity.getIntent().getSerializableExtra(INCID_RESOLUCION_OBJECT.key);

        // Premisas.
        if (incidImportanciaIntent.getUserComu().hasAdministradorAuthority() && resolucionIntent != null) {
            assertThat(incidEditFr, instanceOf(IncidResolucionEditFr.class));
        } else if (incidImportanciaIntent.getUserComu().hasAdministradorAuthority() && resolucionIntent == null) {
            assertThat(incidEditFr, instanceOf(IncidResolucionRegFr.class));
        } else if (!incidImportanciaIntent.getUserComu().hasAdministradorAuthority() && resolucionIntent != null) {
            assertThat(incidEditFr, instanceOf(IncidResolucionSeeFr.class));
        } else {
           // TODO: simplificar.
        }
        assertThat(incidEditFr.getArguments(), hasEntry(INCID_IMPORTANCIA_OBJECT.key, is(incidImportanciaIntent)));
        if (resolucion != null) {
            assertThat(incidEditFr.getArguments(), hasEntry(INCID_RESOLUCION_OBJECT.key, is(resolucionIntent)));
        }
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(whatToClean());
    }

    abstract IntentsTestRule<IncidResolucionEditAc> doIntentRule();

    abstract UsuarioDataTestUtils.CleanUserEnum whatToClean();
}

