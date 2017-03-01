package com.didekindroid.comunidad.testutil;

import com.didekindroid.R;
import com.didekindroid.comunidad.ComunidadBean;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.comunidad.Municipio;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.M;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.CursorMatchers.withRowString;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.containsString;
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
                                         String numeroEnVia, String sufijoNumero) throws InterruptedException
    {
        onView(withId(R.id.tipo_via_spinner)).perform(click());
        Thread.sleep(1000);
        onData(withRowString(1, tipoVia)).perform(click());
        onView(allOf(withId(R.id.app_spinner_1_dropdown_item), withParent(withId(R.id.tipo_via_spinner))))
                .check(matches(withText(containsString(tipoVia)))).check(matches(isDisplayed()));

        onView(withId(R.id.comunidad_nombre_via_editT)).perform(typeText(nombreVia));
        onView(withId(R.id.comunidad_numero_editT)).perform(typeText(numeroEnVia));
        onView(withId(R.id.comunidad_sufijo_numero_editT)).perform(typeText(sufijoNumero), closeSoftKeyboard());

        onView(withId(R.id.autonoma_comunidad_spinner)).perform(click());
        Thread.sleep(1000);
        onData(withRowString(1, comunidadAuto)).perform(click());
        onView(allOf(withId(R.id.app_spinner_1_dropdown_item), withParent(withId(R.id.autonoma_comunidad_spinner))))
                .check(matches(withText(containsString(comunidadAuto))));

        onView(withId(R.id.provincia_spinner)).perform(click());
        Thread.sleep(1000);
        onData(withRowString(1, provincia)).perform(click());
        onView(allOf(withId(R.id.app_spinner_1_dropdown_item), withParent(withId(R.id.provincia_spinner))))
                .check(matches(withText(containsString(provincia))));

        onView(withId(R.id.municipio_spinner)).perform(click());
        Thread.sleep(1000);
        onData(withRowString(3, municipio)).perform(click());
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
}
