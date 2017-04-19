package com.didekindroid.incidencia.testutils;

import android.app.Activity;

import com.didekindroid.R;
import com.didekindroid.incidencia.core.AmbitoIncidValueObj;
import com.didekindroid.incidencia.core.IncidenciaDataDbHelper;
import com.didekindroid.incidencia.core.edit.IncidEditAc;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.testutil.ActivityTestUtils.isDataDisplayedAndClick;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 11/04/17
 * Time: 18:29
 */

public final class IncidUiTestUtils {

    private IncidUiTestUtils()
    {
    }

    public static void checkScreenEditMaxPowerFr(IncidImportancia incidImportanciaIntent, boolean flagResolucionIntent)
    {
        // Precondiditions:
        assertThat(incidImportanciaIntent.isIniciadorIncidencia() || incidImportanciaIntent.getUserComu().hasAdministradorAuthority(), is(true));

        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_edit_fragment_container_ac)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_edit_maxpower_fr_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_comunidad_rot)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_comunidad_txt)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_reg_desc_ed)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_reg_ambito_spinner)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_reg_importancia_spinner)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_edit_fr_modif_button)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_comunidad_txt)).check(matches(isDisplayed()));

        if (incidImportanciaIntent.getUserComu().hasAdministradorAuthority() && !flagResolucionIntent) {
            onView(withId(R.id.incid_edit_fr_borrar_txt)).check(matches(isDisplayed()));
            onView(withId(R.id.incid_edit_fr_borrar_button)).check(matches(isDisplayed()));
        } else {
            onView(withId(R.id.incid_edit_fr_borrar_txt)).check(matches(not(isDisplayed())));
            onView(withId(R.id.incid_edit_fr_borrar_button)).check(matches(not(isDisplayed())));
        }
    }

    public static void checkDataEditMaxPowerFr(IncidenciaDataDbHelper dbHelper, IncidEditAc activity, IncidImportancia incidImportancia)
    {
        // Precondiditions:
        assertThat(incidImportancia.isIniciadorIncidencia() || incidImportancia.getUserComu().hasAdministradorAuthority(), is(true));

        onView(allOf(
                withId(R.id.incid_comunidad_txt),
                withText(incidImportancia.getIncidencia().getComunidad().getNombreComunidad())
        )).check(matches(isDisplayed()));

        onView(allOf(
                withId(R.id.incid_reg_desc_ed),
                withText(incidImportancia.getIncidencia().getDescripcion())
        )).check(matches(isDisplayed()));

        onView(allOf(
                withId(R.id.app_spinner_1_dropdown_item),
                withParent(withId(R.id.incid_reg_ambito_spinner)),
                withText(dbHelper.getAmbitoDescByPk(incidImportancia.getIncidencia().getAmbitoIncidencia().getAmbitoId()))
        )).check(matches(isDisplayed()));

        onView(allOf(
                withId(R.id.app_spinner_1_dropdown_item),
                withParent(withId(R.id.incid_reg_importancia_spinner)),
                withText(activity.getResources().getStringArray(R.array.IncidImportanciaArray)[incidImportancia.getImportancia()])
        )).check(matches(isDisplayed()));
    }

    public static void checkScreenEditMinFr()
    {
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_edit_nopower_fr_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_comunidad_rot)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_comunidad_txt)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_reg_desc_txt)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_ambito_view)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_importancia_otros_view)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_reg_importancia_spinner)).check(matches(isDisplayed()));
        onView(allOf(
                withId(R.id.incid_edit_fr_modif_button),
                withText(R.string.incid_importancia_reg_edit_button_rot)
        )).check(matches(isDisplayed()));
    }

    public static void checkDataEditMinFr(IncidenciaDataDbHelper dbHelper, IncidEditAc activity, IncidImportancia incidImportancia)
    {
        // Precondiditions:
        assertThat(!incidImportancia.isIniciadorIncidencia() && !incidImportancia.getUserComu().hasAdministradorAuthority(), is(true));

        checkScreenEditMinFr();

        onView(allOf(
                withId(R.id.incid_comunidad_txt),
                withText(incidImportancia.getIncidencia().getComunidad().getNombreComunidad())
        )).check(matches(isDisplayed()));

        onView(allOf(
                withId(R.id.incid_reg_desc_txt),
                withText(incidImportancia.getIncidencia().getDescripcion())
        )).check(matches(isDisplayed()));

        onView(allOf(
                withId(R.id.incid_ambito_view),
                withText(dbHelper.getAmbitoDescByPk(incidImportancia.getIncidencia().getAmbitoIncidencia().getAmbitoId()))
        )).check(matches(isDisplayed()));

        onView(allOf(
                withId(R.id.app_spinner_1_dropdown_item),
                withParent(withId(R.id.incid_reg_importancia_spinner)),
                // We adjust the array counter: android seems to add 1 to the counter passed to the method getStringArray().
                withText(activity.getResources().getStringArray(R.array.IncidImportanciaArray)[incidImportancia.getImportancia()])
        )).check(matches(isDisplayed()));
    }

    public static void doComunidadSpinner(Comunidad comunidad)
    {
        doComunidadSpinner(comunidad, R.id.incid_reg_comunidad_spinner);
    }

    public static void doComunidadSpinner(Comunidad comunidad, int resourceId)
    {

        onView(withId(resourceId)).perform(click());
        waitAtMost(2, SECONDS).until(isDataDisplayedAndClick(
                allOf(
                        is(instanceOf(Comunidad.class)),
                        is(comunidad)
                )
        ));
    }

    public static void doImportanciaSpinner(Activity activity, int i)
    {
        onView(withId(R.id.incid_reg_importancia_spinner)).perform(click());
        waitAtMost(2, SECONDS).until(isDataDisplayedAndClick(
                allOf(
                        is(instanceOf(String.class)),
                        is(activity.getResources().getStringArray(R.array.IncidImportanciaArray)[i])
                )
        ));
    }

    public static void doAmbitoAndDescripcion(AmbitoIncidValueObj ambito, String descripcion)
    {
        onView(withId(R.id.incid_reg_ambito_spinner)).perform(click());
        waitAtMost(2, SECONDS).until(isDataDisplayedAndClick(
                allOf(
                        is(instanceOf(AmbitoIncidValueObj.class)),
                        is(ambito)
                )
        ));

        onView(withId(R.id.incid_reg_desc_ed)).perform(replaceText(descripcion));
    }
}