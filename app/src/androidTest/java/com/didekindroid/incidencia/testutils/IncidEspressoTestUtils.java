package com.didekindroid.incidencia.testutils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.matcher.ViewMatchers;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.didekindroid.R;
import com.didekindroid.lib_one.incidencia.spinner.AmbitoIncidValueObj;
import com.didekindroid.lib_one.incidencia.IncidenciaDataDbHelper;
import com.didekindroid.incidencia.core.edit.IncidEditAc;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.incidencia.dominio.ImportanciaUser;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import org.hamcrest.Matcher;

import java.sql.Timestamp;
import java.util.Locale;
import java.util.concurrent.Callable;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.Visibility.GONE;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.lib_one.incidencia.IncidenciaDataDbHelper.DB_NAME;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.COSTE_ESTIM_DEFAULT_String;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.RESOLUCION_DEFAULT_DESC;
import static com.didekindroid.testutil.ActivityTestUtil.isDataDisplayedAndClick;
import static com.didekindroid.testutil.ActivityTestUtil.isViewDisplayed;
import static com.didekindroid.lib_one.util.UiUtil.SPAIN_LOCALE;
import static com.didekindroid.lib_one.util.UiUtil.formatTimeStampToString;
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

public final class IncidEspressoTestUtils {

    private IncidEspressoTestUtils()
    {
    }

    public static View doFragmentTextView(int resourdeIdLayout, String description)
    {
        LayoutInflater inflater = (LayoutInflater) getTargetContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View frView = inflater.inflate(resourdeIdLayout, null);
        EditText editText = frView.findViewById(R.id.incid_reg_desc_ed);
        editText.setText(description);
        return frView;
    }

    // ====================================== INCIDENCIA EDIT =========================================

    public static void checkScreenEditMaxPowerFrErase(IncidAndResolBundle resolBundle)
    {
        // Precondiditions:
        assertThat(resolBundle.getIncidImportancia().isIniciadorIncidencia()
                || resolBundle.getIncidImportancia().getUserComu().hasAdministradorAuthority(), is(true));
        assertThat(resolBundle.hasResolucion(), is(false));

        checkScreenEditMaxPowerFr();
        onView(withId(R.id.incid_edit_fr_borrar_button)).check(matches(isDisplayed()));
    }

    public static void checkScreenEditMaxPowerFrNotErase(IncidAndResolBundle resolBundle)
    {
        // Precondiditions:
        assertThat(resolBundle.getIncidImportancia().isIniciadorIncidencia()
                || resolBundle.getIncidImportancia().getUserComu().hasAdministradorAuthority(), is(true));
        assertThat(resolBundle.hasResolucion(), is(true));

        checkScreenEditMaxPowerFr();
        onView(withId(R.id.incid_edit_fr_borrar_button)).check(matches(not(isDisplayed())));
    }

    private static void checkScreenEditMaxPowerFr()
    {
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
        onView(withId(R.id.incid_reg_importancia_spinner)).check(matches(isDisplayed()));
        onView(allOf(
                withId(R.id.incid_edit_fr_modif_button),
                withText(R.string.modif_button_rot)
        )).check(matches(isDisplayed()));

        onView(withId(R.id.incid_edit_fr_borrar_button)).check(doesNotExist());

    }

    public static boolean checkDataEditMinFr(IncidenciaDataDbHelper dbHelper, IncidEditAc activity, IncidImportancia incidImportancia)
    {
        boolean isDone;

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
                withText(activity.getResources().getStringArray(R.array.IncidImportanciaArray)[incidImportancia.getImportancia()])
        )).check(matches(isDisplayed()));

        isDone = true;
        return isDone;
    }

    // ====================================== INCIDENCIA LIST =========================================

    @SuppressWarnings("unchecked")
    public static Matcher<View> checkIncidClosedListView(IncidImportancia incidImportancia, Activity activity)
    {
        ViewMatchers.assertThat(activity.getTitle(), is(activity.getText(R.string.incid_closed_by_user_ac_label)));
        return allOf(
                withId(R.id.incid_see_cierre_block),
                hasDescendant(allOf(
                        withId(R.id.incid_fecha_cierre_view),
                        withText(formatTimeStampToString(incidImportancia.getIncidencia().getFechaCierre()))
                )),
                hasSibling(allOf(
                        withId(R.id.incid_see_resolucion_block),
                        withEffectiveVisibility(GONE)
                )),
                addIncidCommonMatcher(incidImportancia, activity)
        );
    }

    public static Matcher<View> checkIncidOpenListView(IncidImportancia incidImportancia, Activity activity, Timestamp fechaAltaResolucion)
    {
        ViewMatchers.assertThat(activity.getTitle(), is(activity.getText(R.string.incid_see_by_user_ac_label)));
        return allOf(
                withId(R.id.incid_see_resolucion_block),
                hasDescendant(allOf(
                        withId(R.id.incid_see_fecha_alta_resolucion_view),
                        withText(formatTimeStampToString(fechaAltaResolucion))
                )),
                hasSibling(allOf(
                        withId(R.id.incid_see_cierre_block),
                        withEffectiveVisibility(GONE)
                )),
                addIncidCommonMatcher(incidImportancia, activity)
        );
    }

    @SuppressWarnings("unchecked")
    public static Matcher<View> checkIncidOpenListViewNoResol()
    {
        return allOf(
                withId(R.id.incid_see_apertura_block),
                hasDescendant(withId(R.id.incid_see_iniciador_view)),
                hasSibling(allOf(
                        withId(R.id.incid_see_importancia_block),
                        hasDescendant(withId(R.id.incid_importancia_comunidad_view)))),
                hasSibling(withId(R.id.incid_ambito_view)),
                hasSibling(withId(R.id.incid_descripcion_view)),
                hasSibling(allOf(
                        withId(R.id.incid_see_resolucion_block),
                        withEffectiveVisibility(GONE)
                )),
                hasSibling(allOf(
                        withId(R.id.incid_see_cierre_block),
                        withEffectiveVisibility(GONE)
                ))
        );
    }

    // ====================================== INCIDENCIA =========================================

    @NonNull
    private static Matcher<View> addIncidCommonMatcher(IncidImportancia incidImportancia, Activity activity)
    {
        IncidenciaDataDbHelper dbHelper = new IncidenciaDataDbHelper(activity);

        Matcher<View> matcher = allOf(
                hasSibling(allOf(
                        withId(R.id.incid_see_apertura_block),
                        hasDescendant(allOf(
                                withId(R.id.incid_fecha_alta_view),
                                withText(formatTimeStampToString(incidImportancia.getIncidencia().getFechaAlta()))
                        )),
                        hasDescendant(allOf(
                                withId(R.id.incid_see_iniciador_view),
                                withText(incidImportancia.getUserComu().getUsuario().getAlias())
                        ))
                )),
                hasSibling(allOf(
                        withId(R.id.incid_see_importancia_block),
                        hasDescendant(allOf(
                                withId(R.id.incid_importancia_comunidad_view),
                                withText(activity.getResources()
                                        .getStringArray(R.array.IncidImportanciaArray)[Math.round(incidImportancia.getImportancia())]))
                        ))),
                hasSibling(allOf(
                        withId(R.id.incid_ambito_view),
                        withText(dbHelper.getAmbitoDescByPk(incidImportancia.getIncidencia().getAmbitoIncidencia().getAmbitoId()))
                )),
                hasSibling(allOf(
                        withText(incidImportancia.getIncidencia().getDescripcion()),
                        withId(R.id.incid_descripcion_view)
                ))
        );

        dbHelper.close();
        activity.deleteDatabase(DB_NAME);
        return matcher;
    }

    // =================================== IMPORTANCIA USER ======================================

    public static boolean checkImportanciaUser(ImportanciaUser importanciaUser, Activity activity)
    {
        waitAtMost(4, SECONDS).until(isViewDisplayed(
                allOf(
                        withId(R.id.incid_importancia_alias_view),
                        withText(importanciaUser.getUserAlias()),
                        hasSibling(allOf(
                                withId(R.id.incid_importancia_rating_view),
                                withText(activity.getResources().getStringArray(R.array.IncidImportanciaArray)[importanciaUser.getImportancia()])
                        ))
                )
        ));
        return true;
    }

    // ====================================== RESOLUCION =========================================

    public static void checkScreenResolucionEditFr(Resolucion resolucionIntent)
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

        checkAvancesList(resolucionIntent);
    }

    public static void checkDataResolucionEditFr(Resolucion resolucion)
    {
        // Caso: los datos que se muestran por defecto.
        checkDataResolucion(resolucion, R.id.incid_resolucion_coste_prev_ed);
    }

    public static void checkScreenResolucionRegFr()
    {
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));

        onView(withId(R.id.incid_resolucion_fragment_container_ac)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_reg_frg_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_reg_ac_button)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_desc_ed)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_fecha_view)).check(matches(isDisplayed()));
    }

    public static void checkScreenResolucionSeeFr(Resolucion resolucionIntent)
    {
        onView(withId(R.id.incid_resolucion_fragment_container_ac)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_see_fr_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_fecha_view)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_coste_prev_view)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_txt)).check(matches(isDisplayed()));

        checkAvancesList(resolucionIntent);
    }

    public static void checkDataResolucionSeeFr(Resolucion resolucion)
    {
        checkDataResolucion(resolucion, R.id.incid_resolucion_coste_prev_view);
    }

    private static void checkAvancesList(Resolucion resolucionIntent)
    {
        if (resolucionIntent != null && !resolucionIntent.getAvances().isEmpty()) {
            // Lista NO vacía.
            onView(withId(android.R.id.list)).check(matches(isDisplayed()));
            onView(withId(android.R.id.empty)).check(matches(not(isDisplayed())));
        } else {
            // Lista de avances vacía.
            onView(withId(android.R.id.list)).check(matches(not(isDisplayed())));
            onView(withId(android.R.id.empty)).check(matches(isDisplayed()));
        }
    }

    private static void checkDataResolucion(Resolucion resolucion, int costePrevRsId)
    {
        // Fecha.
        if (Locale.getDefault().equals(SPAIN_LOCALE)) {  // TODO: internacionalizar.
            onView(allOf(
                    withId(R.id.incid_resolucion_fecha_view),
                    withText(formatTimeStampToString(resolucion.getFechaPrev()))
            )).check(matches(isDisplayed()));
        }
        // Coste.
        onView(allOf(
                withId(costePrevRsId),
                withText(COSTE_ESTIM_DEFAULT_String)
        )).check(matches(isDisplayed()));
        // Resolución.
        onView(allOf(
                withId(R.id.incid_resolucion_txt),
                withText(RESOLUCION_DEFAULT_DESC)
        )).check(matches(isDisplayed()));
    }

    // ====================================== SPINNERS =========================================

    public static void doComunidadSpinner(Comunidad comunidad)
    {
        doComunidadSpinner(comunidad, R.id.incid_comunidad_spinner);
    }

    public static void doComunidadSpinner(Comunidad comunidad, int resourceId)
    {
        onView(withId(resourceId)).perform(click());
        waitAtMost(4, SECONDS).until(isDataDisplayedAndClick(
                allOf(
                        is(instanceOf(Comunidad.class)),
                        is(comunidad),
                        not(instanceOf(String.class)),
                        not(instanceOf(AmbitoIncidValueObj.class))
                )
        ));
    }

    public static Callable<Boolean> isComuSpinnerWithText(final String textToCheck)
    {
        return () -> {
            try {
                onView(
                        allOf(
                                withId(R.id.app_spinner_1_dropdown_item),
                                withParent(withId(R.id.incid_comunidad_spinner))
                        )
                ).check(matches(withText(is(textToCheck)))).check(matches(isDisplayed()));
                return true;
            } catch (NoMatchingViewException ne) {
                return false;
            }
        };
    }

    public static void doImportanciaSpinner(Activity activity, int i)
    {
        onView(withId(R.id.incid_reg_importancia_spinner)).perform(click());
        waitAtMost(4, SECONDS).until(isDataDisplayedAndClick(
                allOf(
                        is(instanceOf(String.class)),
                        is(activity.getResources().getStringArray(R.array.IncidImportanciaArray)[i]),
                        not(instanceOf(Comunidad.class)),
                        not(instanceOf(AmbitoIncidValueObj.class))
                )
        ));
    }

    public static void doAmbitoAndDescripcion(AmbitoIncidValueObj ambito, String descripcion)
    {
        onView(withId(R.id.incid_reg_ambito_spinner)).perform(click());
        waitAtMost(4, SECONDS).until(isDataDisplayedAndClick(
                allOf(
                        is(instanceOf(AmbitoIncidValueObj.class)),
                        is(ambito),
                        not(instanceOf(String.class)),
                        not(instanceOf(Comunidad.class))
                )
        ));

        // Replace text in description.
        onView(withId(R.id.incid_reg_desc_ed)).perform(replaceText(descripcion));
    }
}