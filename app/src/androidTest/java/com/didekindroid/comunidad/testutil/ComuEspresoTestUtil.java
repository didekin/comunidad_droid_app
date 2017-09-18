package com.didekindroid.comunidad.testutil;

import android.view.View;
import android.widget.EditText;

import com.didekindroid.R;
import com.didekindroid.comunidad.ViewerRegComuFr;
import com.didekindroid.comunidad.spinner.TipoViaValueObj;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.comunidad.ComunidadAutonoma;
import com.didekinlib.model.comunidad.Municipio;
import com.didekinlib.model.comunidad.Provincia;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.testutil.ActivityTestUtils.isDataDisplayedAndClick;
import static com.didekindroid.testutil.ActivityTestUtils.isViewDisplayed;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 11:49
 */

public final class ComuEspresoTestUtil {

    private ComuEspresoTestUtil()
    {
    }

    // ======================================  TYPING  =========================================

    public static void typeComuCalleNumero(String nombreVia, String numero, String sufijo)
    {
        onView(withId(R.id.comunidad_nombre_via_editT)).perform(scrollTo(), replaceText(nombreVia));
        onView(withId(R.id.comunidad_numero_editT)).perform(scrollTo(), replaceText(numero));
        onView(withId(R.id.comunidad_sufijo_numero_editT)).perform(scrollTo(), replaceText(sufijo), closeSoftKeyboard());
    }

    public static void typeComunidadData()
    {
        final ComunidadAutonoma comunidadAutonoma = new ComunidadAutonoma((short) 10, "Valencia");
        final Provincia provincia = new Provincia(comunidadAutonoma, (short) 3, "Alicante/Alacant");
        final Municipio municipio = new Municipio((short) 13, "Algueña", provincia);
        final TipoViaValueObj tipoVia = new TipoViaValueObj(52, "Calle");
        try {
            typeComunidadData(municipio, tipoVia, "Real", "5", "Bis");
        } catch (InterruptedException e) {
            fail("InterruptedException");
        }
    }

    public static void typeComunidadData(Municipio municipio, TipoViaValueObj tipoVia, String nombreVia,
                                         String numeroEnVia, String sufijoNumero) throws InterruptedException
    {
        doTipoViaSpinner(tipoVia);
        SECONDS.sleep(1);
        typeComuCalleNumero(nombreVia, numeroEnVia, sufijoNumero);
        doComunAutonomaSpinner(municipio.getProvincia().getComunidadAutonoma());
        SECONDS.sleep(1);
        doProvinciaSpinner(municipio.getProvincia());
        SECONDS.sleep(1);
        doMunicipioSpinner(municipio);
    }

    // ======================================  CHECKING IN VIEW =========================================

    public static void checkRegComuFrViewEmpty()
    {
        waitAtMost(4, SECONDS).until(isViewDisplayed(
                allOf(
                        withId(R.id.app_spinner_1_dropdown_item),
                        withParent(withId(R.id.tipo_via_spinner)),
                        withText(R.string.tipoVia_spinner_default)
                )
        ));

        waitAtMost(4, SECONDS).until(isViewDisplayed(
                allOf(
                        withId(R.id.app_spinner_1_dropdown_item),
                        withParent(withId(R.id.autonoma_comunidad_spinner)),
                        withText(R.string.comAutonoma_spinner_default)
                )
        ));

        waitAtMost(4, SECONDS).until(isViewDisplayed(
                allOf(
                        withId(R.id.app_spinner_1_dropdown_item),
                        withParent(withId(R.id.provincia_spinner)),
                        withText(R.string.provincia_spinner_default)
                )
        ));

        waitAtMost(4, SECONDS).until(isViewDisplayed(
                allOf(
                        withId(R.id.app_spinner_1_dropdown_item),
                        withParent(withId(R.id.municipio_spinner)),
                        withText(R.string.municipio_spinner_default)
                )
        ));
    }

    public static void checkRegComuFrView(Comunidad comunidad, String nombreComAutonoma)
    {
        checkNumeroEnVia(comunidad);
        checkNumeroSufijo(comunidad);
        checkTipoViaSpinner(comunidad);
        checkComuAutonomaSpinner(nombreComAutonoma);
        checkProvinciaSpinner(comunidad);
        checkMunicipioSpinner(comunidad);
    }

    public static void checkNumeroEnVia(Comunidad comunidad)
    {
        waitAtMost(4, SECONDS).until(isViewDisplayed(
                allOf(
                        withId(R.id.comunidad_numero_editT),
                        withText(containsString(String.valueOf(comunidad.getNumero())))
                )
        ));
    }

    public static void checkNumeroSufijo(Comunidad comunidad)
    {
        waitAtMost(4, SECONDS).until(isViewDisplayed(
                allOf(
                        withId(R.id.comunidad_sufijo_numero_editT),
                        withText(containsString(String.valueOf(comunidad.getSufijoNumero())))
                )
        ));
    }

    public static void checkComuData(Comunidad comunidad)
    {
        onView(allOf(
                withId(R.id.nombreComunidad_view),
                withText(comunidad.getNombreComunidad()),
                hasSibling(allOf(
                        withChild(allOf(
                                withId(R.id.municipio_view),
                                withText(comunidad.getMunicipio().getNombre())
                        )),
                        withChild(allOf(
                                withId(R.id.provincia_view),
                                withText(comunidad.getMunicipio().getProvincia().getNombre())
                        ))
                ))
        ));
    }

    // ======================================  CHECKING TEXTS OFF ViEW  =========================================

    public static void checkComunidadTextsOffView(ViewerRegComuFr viewer, Comunidad comunidad)
    {
        View viewFr = viewer.getViewInViewer();
        assertThat(((EditText) viewFr.findViewById(R.id.comunidad_nombre_via_editT)).getText().toString(), is(comunidad.getNombreVia()));
        assertThat(((EditText) viewFr.findViewById(R.id.comunidad_numero_editT)).getText().toString(), is(String.valueOf(comunidad.getNumero())));
        assertThat(((EditText) viewFr.findViewById(R.id.comunidad_sufijo_numero_editT)).getText().toString(), is(comunidad.getSufijoNumero()));
    }

    // ======================================  SPINNERS OFF ViEW  =========================================

    /**
     * Utility for checking ViewerRegComuFr.initializeSpinnersFromComunidad method when viewBean != null.
     */
    public static void checkSpinnersOffView(ViewerRegComuFr viewer, Comunidad comunidad)
    {
        assertThat(viewer.getTipoViaSpinner().getTipoViaValueObj().getTipoViaDesc(), is(comunidad.getTipoVia()));
        assertThat(viewer.getComuAutonomaSpinner().getSpinnerEvent().getComunidadAutonoma(), is(comunidad.getMunicipio().getProvincia().getComunidadAutonoma()));
        assertThat(viewer.getProvinciaSpinner().getProvinciaEventSelect().getProvincia(), is(comunidad.getMunicipio().getProvincia()));
        assertThat(viewer.getMunicipioSpinner().getSpinnerEvent().getMunicipio(), is(comunidad.getMunicipio()));
    }

    /**
     * Utility for checking ViewerRegComuFr.initializeSpinnersFromComunidad method when viewBean == null.
     */
    public static void checkSpinnersOffViewNull(ViewerRegComuFr viewer)
    {
        assertThat(viewer.getTipoViaSpinner().getTipoViaValueObj(), nullValue());
        checkSubsetSpinnersOff(viewer);
    }

    /**
     * Utility for checking ViewerRegComuFr.doInViewer method when viewBean == null.
     */
    public static void checkSpinnersDoInViewerOffNull(ViewerRegComuFr viewer)
    {
        assertThat(viewer.getTipoViaSpinner().getTipoViaValueObj(), notNullValue());
        checkSubsetSpinnersOff(viewer);
    }

    public static void checkSubsetSpinnersOff(ViewerRegComuFr viewer)
    {
        assertThat(viewer.getTipoViaSpinner().getSelectedItemId(), is(0L));
        assertThat(viewer.getComuAutonomaSpinner().getSpinnerEvent().getComunidadAutonoma().getCuId(), is((short) 0));
        assertThat(viewer.getProvinciaSpinner().getProvinciaEventSelect().getProvincia().getProvinciaId(), is((short) 0));
        assertThat(viewer.getMunicipioSpinner().getSpinnerEvent().getMunicipio().getCodInProvincia(), is((short) 0));
    }

    // ======================================  SPINNERS ON ViEW  =========================================

    public static void doTipoViaSpinner(TipoViaValueObj tipoVia)
    {
        onView(withId(R.id.tipo_via_spinner)).perform(scrollTo(), click());
        waitAtMost(2, SECONDS).until(isDataDisplayedAndClick(
                allOf(
                        isA(TipoViaValueObj.class),
                        is(tipoVia)
                )));

        waitAtMost(2, SECONDS).until(isViewDisplayed(
                allOf(
                        withId(R.id.app_spinner_1_dropdown_item),
                        withParent(withId(R.id.tipo_via_spinner)),
                        withText(is(tipoVia.getTipoViaDesc()))
                )));
    }

    public static void checkTipoViaSpinner(Comunidad comunidad)
    {
        waitAtMost(3, SECONDS).until(isViewDisplayed(
                allOf(
                        withId(R.id.app_spinner_1_dropdown_item),
                        withParent(withId(R.id.tipo_via_spinner)),
                        withText(comunidad.getTipoVia()))
        ));
    }

    public static void doComunAutonomaSpinner(ComunidadAutonoma comunidadAutonoma)
    {
        onView(withId(R.id.autonoma_comunidad_spinner)).perform(click());
        waitAtMost(2, SECONDS).until(isDataDisplayedAndClick(
                allOf(
                        isA(ComunidadAutonoma.class),
                        is(comunidadAutonoma)
                )));
        waitAtMost(2, SECONDS).until(isViewDisplayed(allOf(
                withId(R.id.app_spinner_1_dropdown_item),
                withParent(withId(R.id.autonoma_comunidad_spinner)),
                withText(is(comunidadAutonoma.getNombre()))
        )));
    }

    public static void checkComuAutonomaSpinner(String nombreComAutonoma)
    {
        waitAtMost(3, SECONDS).until(isViewDisplayed(
                allOf(
                        withId(R.id.app_spinner_1_dropdown_item),
                        withParent(withId(R.id.autonoma_comunidad_spinner)),
                        withText(nombreComAutonoma))
        ));
    }

    public static void doProvinciaSpinner(Provincia provincia)
    {
        onView(withId(R.id.provincia_spinner)).perform(click());
        waitAtMost(3, SECONDS).until(isDataDisplayedAndClick(
                allOf(
                        isA(Provincia.class),
                        is(provincia)
                )));
        waitAtMost(2, SECONDS).until(isViewDisplayed(
                allOf(withId(R.id.app_spinner_1_dropdown_item),
                        withParent(withId(R.id.provincia_spinner)),
                        withText(is(provincia.getNombre()))
                )
        ));
    }

    public static void checkProvinciaSpinner(Comunidad comunidad)
    {
        checkProvinciaSpinner(comunidad.getMunicipio().getProvincia().getNombre());
    }

    public static void checkProvinciaSpinner(String provinciaNombre)
    {
        waitAtMost(3, SECONDS).until(isViewDisplayed(
                allOf(
                        withId(R.id.app_spinner_1_dropdown_item),
                        withParent(withId(R.id.provincia_spinner)),
                        withText(provinciaNombre))
        ));
    }

    public static void doMunicipioSpinner(Municipio municipio)
    {
        onView(withId(R.id.municipio_spinner)).perform(click());
        waitAtMost(3, SECONDS).until(isDataDisplayedAndClick(
                allOf(
                        isA(Municipio.class),
                        is(municipio)
                )));
        waitAtMost(2, SECONDS).until(isViewDisplayed(
                allOf(withId(R.id.app_spinner_1_dropdown_item),
                        withParent(withId(R.id.municipio_spinner)),
                        withText(is(municipio.getNombre()))
                )
        ));
    }

    public static void checkMunicipioSpinner(Comunidad comunidad)
    {
        checkMunicipioSpinner(comunidad.getMunicipio().getNombre());
    }

    public static void checkMunicipioSpinner(String municipioNombre)
    {
        waitAtMost(4, SECONDS).until(isViewDisplayed(
                allOf(
                        withId(R.id.app_spinner_1_dropdown_item),
                        withParent(withId(R.id.municipio_spinner)),
                        withText(municipioNombre))
        ));
    }
}
