package com.didekindroid.comunidad.testutil;

import com.didekindroid.R;
import com.didekindroid.comunidad.ComunidadBean;
import com.didekindroid.comunidad.ViewerRegComuFr;
import com.didekindroid.comunidad.spinner.TipoViaValueObj;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.comunidad.ComunidadAutonoma;
import com.didekinlib.model.comunidad.Municipio;
import com.didekinlib.model.comunidad.Provincia;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.M;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
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
        Provincia provincia = new Provincia((short) 3, "Alicante/Alacant");
        final ComunidadAutonoma comunidadAutonoma = new ComunidadAutonoma((short) 10, "Valencia");
        final Municipio municipio = new Municipio((short) 13, "Algueña", provincia);
        final TipoViaValueObj tipoVia = new TipoViaValueObj(52, "Calle");
        typeComunidadData(comunidadAutonoma, provincia, municipio, tipoVia, "Real", "5", "Bis");
    }

    public static void typeComunidadData(ComunidadAutonoma comunidadAuto, Provincia provincia, Municipio municipio, TipoViaValueObj tipoVia, String nombreVia,
                                         String numeroEnVia, String sufijoNumero)
    {
        doTipoViaSpinner(tipoVia);
        typeComuCalleNumero(nombreVia, numeroEnVia, sufijoNumero);
        doComunAutonomaSpinner(comunidadAuto);
        doProvinciaSpinner(provincia);
        doMunicipioSpinner(municipio);
    }

    public static void validaTypedComunidadBean(final ComunidadBean comunidadBean, String tipoVia, short municipioProvId,
                                                short municipioCodProv, String nombreVia, String numeroEnVia, String sufijoNumero)
    {
        assertThat(comunidadBean.getTipoVia().getTipoViaDesc(), is(tipoVia));
        assertThat(comunidadBean.getMunicipio().getProvincia().getProvinciaId(), is(municipioProvId));
        assertThat(comunidadBean.getMunicipio().getCodInProvincia(), is(municipioCodProv));
        assertThat(comunidadBean.getNombreVia(), is(nombreVia));
        assertThat(comunidadBean.getNumeroString(), is(numeroEnVia));
        assertThat(comunidadBean.getSufijoNumero(), is(sufijoNumero));
    }

    public static void validaTypedComunidad(Comunidad comunidad, String tipoVia, short municipioProvId,
                                            short municipioCodProv, String nombreVia, short numeroEnVia, String sufijoNumero)
    {
        assertThat(comunidad, notNullValue());
        assertThat(comunidad.getTipoVia(), is(tipoVia));
        assertThat(comunidad.getMunicipio().getProvincia().getProvinciaId(), is(municipioProvId));
        if (SDK_INT >= M) {
            assertThat(comunidad.getMunicipio().getCodInProvincia(), is(municipioCodProv));
        }
        assertThat(comunidad.getNombreVia(), is(nombreVia));
        assertThat(comunidad.getNumero(), is(numeroEnVia));
        assertThat(comunidad.getSufijoNumero(), is(sufijoNumero));
    }

    public static void validaTypedComunidadShort(Comunidad comunidad, String nombreComunidad, Municipio municipio)
    {
        assertThat(comunidad, notNullValue());
        assertThat(comunidad.getNombreComunidad(), is(nombreComunidad));
        assertThat(comunidad.getMunicipio(), is(municipio));
    }

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

    // ======================================  SPINNER  =========================================

    /**
     * Utility for checking ViewerRegComuFr.initializeSpinnersFromComunidad method when viewBean != null.
     */
    public static void checkSpinnersOff(ViewerRegComuFr viewer, Comunidad comunidad)
    {
        assertThat(viewer.getTipoViaSpinner().getTipoViaValueObj().getTipoViaDesc(), is(comunidad.getTipoVia()));
        assertThat(viewer.getComuAutonomaSpinner().getSpinnerEvent().getComunidadAutonoma(), is(comunidad.getMunicipio().getProvincia().getComunidadAutonoma()));
        assertThat(viewer.getProvinciaSpinner().getProvinciaEventSelect().getProvincia(), is(comunidad.getMunicipio().getProvincia()));
        assertThat(viewer.getMunicipioSpinner().getSpinnerEvent().getMunicipio(), is(comunidad.getMunicipio()));
    }

    /**
     * Utility for checking ViewerRegComuFr.initializeSpinnersFromComunidad method when viewBean == null.
     */
    public static void checkSpinnersOffNull(ViewerRegComuFr viewer)
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

    // ................................................................................................

    public static void doTipoViaSpinner(TipoViaValueObj tipoVia)
    {
        onView(withId(R.id.tipo_via_spinner)).perform(scrollTo(), click());
        waitAtMost(2, SECONDS).until(isDataDisplayedAndClick(
                allOf(
                        isA(TipoViaValueObj.class),
                        is(tipoVia)
                )));
        onView(
                allOf(
                        withId(R.id.app_spinner_1_dropdown_item),
                        withParent(withId(R.id.tipo_via_spinner)),
                        withText(is(tipoVia.getTipoViaDesc()))
                )
        ).check(matches(isDisplayed()));
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
        onView(allOf(withId(R.id.app_spinner_1_dropdown_item), withParent(withId(R.id.autonoma_comunidad_spinner))))
                .check(matches(withText(is(comunidadAutonoma.getNombre())))).check(matches(isDisplayed()));
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
        onView(allOf(withId(R.id.app_spinner_1_dropdown_item), withParent(withId(R.id.provincia_spinner))))
                .check(matches(withText(is(provincia.getNombre())))).check(matches(isDisplayed()));
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
        onView(allOf(withId(R.id.app_spinner_1_dropdown_item), withParent(withId(R.id.municipio_spinner))))
                .check(matches(withText(is(municipio.getNombre())))).check(matches(isDisplayed()));
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
