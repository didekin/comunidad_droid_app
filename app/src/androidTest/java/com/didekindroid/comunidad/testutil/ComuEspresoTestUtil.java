package com.didekindroid.comunidad.testutil;

import com.didekindroid.R;
import com.didekindroid.comunidad.ComunidadBean;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.comunidad.Municipio;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.M;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.CursorMatchers.withRowString;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.testutil.ActivityTestUtils.isDataDisplayedAndClick;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
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

    public static void typeComunidadData() throws InterruptedException
    {
        typeComunidadData("Calle", "Valencia", "Alicante/Alacant", "Algueña", "Real", "5", "Bis");
    }

    public static void typeComunidadData(String tipoVia, String comunidadAuto, String provincia, String municipio, String nombreVia,
                                         String numeroEnVia, String sufijoNumero)
    {
        doTipoViaSpinner(tipoVia);
        onView(withId(R.id.comunidad_nombre_via_editT)).perform(typeText(nombreVia));
        onView(withId(R.id.comunidad_numero_editT)).perform(typeText(numeroEnVia));
        onView(withId(R.id.comunidad_sufijo_numero_editT)).perform(typeText(sufijoNumero), closeSoftKeyboard());
        doComunAutonomaSpinner(comunidadAuto);
        doProvinciaSpinner(provincia);
        doMunicipioSpinner(municipio);
    }

    public static void validaTypedComunidadBean(final ComunidadBean comunidadBean, String tipoVia, short municipioProvId,
                                                short municipioCodProv, String nombreVia, String numeroEnVia, String sufijoNumero)
    {
        assertThat(comunidadBean.getTipoVia(), is(tipoVia));
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

    // ======================================  SPINNER  =========================================

    public static void doTipoViaSpinner(String tipoVia)
    {
        onView(withId(R.id.tipo_via_spinner)).perform(click());
        waitAtMost(2, SECONDS).until(isDataDisplayedAndClick(withRowString(1, tipoVia)));
    }

    public static void doComunAutonomaSpinner(String comunidadAutonoma)
    {
        onView(withId(R.id.autonoma_comunidad_spinner)).perform(click());
        waitAtMost(2, SECONDS).until(isDataDisplayedAndClick(withRowString(1, comunidadAutonoma)));
    }

    public static void doProvinciaSpinner(String provincia)
    {
        onView(withId(R.id.provincia_spinner)).perform(click());
        waitAtMost(2, SECONDS).until(isDataDisplayedAndClick(withRowString(1, provincia)));
    }

    public static void doMunicipioSpinner(String municipio)
    {
        onView(withId(R.id.municipio_spinner)).perform(click());
        waitAtMost(4, SECONDS).until(isDataDisplayedAndClick(withRowString(3, municipio)));
    }
}
