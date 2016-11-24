package com.didekinaar.testutil;

import android.support.test.espresso.matcher.ViewMatchers;

import com.didekin.comunidad.Comunidad;
import com.didekin.comunidad.Municipio;
import com.didekin.comunidad.Provincia;
import com.didekin.usuario.Usuario;
import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekinaar.R;
import com.didekinaar.comunidad.ComunidadBean;
import com.didekinaar.usuariocomunidad.RolUi;
import com.didekinaar.usuariocomunidad.UsuarioComunidadBean;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.M;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.CursorMatchers.withRowString;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekin.usuariocomunidad.Rol.PRESIDENTE;
import static com.didekin.usuariocomunidad.Rol.PROPIETARIO;
import static com.didekinaar.usuariocomunidad.RolUi.ADM;
import static com.didekinaar.usuariocomunidad.RolUi.INQ;
import static com.didekinaar.usuariocomunidad.RolUi.PRE;
import static com.didekinaar.usuariocomunidad.RolUi.PRO;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro
 * Date: 21/07/15
 * Time: 11:19
 */

public final class UsuarioTestUtils {

    private UsuarioTestUtils()
    {
    }

    public static final Usuario USER_DROID = new Usuario.UsuarioBuilder()
            .userName("didekindroid@didekin.es")
            .alias("didekindroid")
            .password("psw_droid")
            .build();

    public static final Usuario USER_JUAN = new Usuario.UsuarioBuilder()
            .userName("juan@juan.us")
            .alias("alias_juan")
            .password("psw_juan")
            .build();

    public static final Usuario USER_JUAN2 = new Usuario.UsuarioBuilder()
            .userName("juan@juan.com")
            .alias("alias_juan")
            .password("pswd01")
            .build();

    public static final Usuario USER_PEPE = new Usuario.UsuarioBuilder()
            .userName("pepe@pepe.org")
            .alias("pepe")
            .password("psw_pepe")
            .build();

    // Municipio: Benizalón  Provincia: Almería
    public static final Comunidad COMU_EL_ESCORIAL = new Comunidad.ComunidadBuilder()
            .tipoVia("Calle")
            .nombreVia("de El Escorial")
            .numero((short) 2)
            .municipio(new Municipio((short) 27, new Provincia((short) 4)))
            .build();

    // Municipio: Elda   Provincia: Alicante/Alacant
    public static final Comunidad COMU_LA_FUENTE = new Comunidad.ComunidadBuilder()
            .tipoVia("Calle")
            .nombreVia("de la Fuente")
            .numero((short) 11)
            .municipio(new Municipio((short) 66, new Provincia((short) 3)))
            .build();

    // Municipio: Alfoz   Provincia: Lugo
    public static final Comunidad COMU_LA_PLAZUELA_5 = new Comunidad.ComunidadBuilder()
            .tipoVia("Ronda")
            .nombreVia("de la Plazuela")
            .numero((short) 5)
            .municipio(new Municipio((short) 2, new Provincia((short) 27)))
            .build();

    // Municipio:  Algueña   Provincia: Alicante/Alacant
    public static final Comunidad COMU_TRAV_PLAZUELA_11 = new Comunidad.ComunidadBuilder()
            .tipoVia("Travesía")
            .nombreVia("de la Plazuela")
            .numero((short) 11)
            .municipio(new Municipio((short) 13, new Provincia((short) 3)))
            .build();

    // Municipio: Algueña  Provincia: Alicante/Alacant
    public static final Comunidad COMU_REAL = new Comunidad.ComunidadBuilder()
            .tipoVia("Calle")
            .nombreVia("Real")
            .numero((short) 5)
            .sufijoNumero("Bis")
            .municipio(new Municipio((short) 13, new Provincia((short) 3)))
            .build();

    public static final UsuarioComunidad COMU_ESCORIAL_PEPE = makeUsuarioComunidad(COMU_EL_ESCORIAL, USER_PEPE,
            "portal22", "esc22", "planta22", "door22", PRESIDENTE.function.concat(",").concat(INQ.function));

    public static final UsuarioComunidad COMU_ESCORIAL_JUAN = makeUsuarioComunidad(COMU_EL_ESCORIAL, USER_JUAN,
            "portal21", "esc21", "planta21", "door21", PRO.function);

    public static final UsuarioComunidad COMU_LA_FUENTE_PEPE = makeUsuarioComunidad(COMU_LA_FUENTE, USER_PEPE,
            "portal33", "esc33", "planta33", "door33", ADM.function.concat(",").concat(PRE.function));

    public static final UsuarioComunidad COMU_PLAZUELA5_JUAN = makeUsuarioComunidad(COMU_LA_PLAZUELA_5, USER_JUAN, null,
            null, "planta3", "doorA", ADM.function);

    public static final UsuarioComunidad COMU_PLAZUELA5_PEPE = makeUsuarioComunidad(COMU_LA_PLAZUELA_5, USER_PEPE,
            "portal11", "esc11", "planta11", "door11", PRE.function.concat(",").concat(PRO.function));

    public static final UsuarioComunidad COMU_TRAV_PLAZUELA_PEPE = makeUsuarioComunidad(COMU_TRAV_PLAZUELA_11, USER_PEPE,
            "portalA", null, "planta2", null, INQ.function);

    public static final UsuarioComunidad COMU_REAL_JUAN = makeUsuarioComunidad(COMU_REAL, USER_JUAN, "portal", "esc",
            "plantaX", "door12", PROPIETARIO.function);

    public static final UsuarioComunidad COMU_REAL_PEPE = makeUsuarioComunidad(COMU_REAL, USER_PEPE, "portal",
            "esc", "plantaY", "door21", PRO.function);

    public static final UsuarioComunidad COMU_REAL_DROID = makeUsuarioComunidad(COMU_REAL, USER_DROID, "portal",
            "esc", "plantaH", "door11", PRO.function);

//  ======================================= ENTITIES METHODS  ==========================================

    public static Comunidad makeComunidad(String tipoVia, String nombreVia, short numero, String sufijoNumero,
                                          Municipio municipio)
    {
        return new Comunidad.ComunidadBuilder().tipoVia(tipoVia)
                .nombreVia(nombreVia)
                .numero(numero)
                .sufijoNumero(sufijoNumero)
                .municipio(municipio)
                .build();
    }

    public static Usuario makeUsuario(String userName, String alias, String password)
    {
        return new Usuario.UsuarioBuilder()
                .userName(userName)
                .alias(alias)
                .password(password)
                .build();
    }

    public static UsuarioComunidad makeUsuarioComunidad(Comunidad comunidad, Usuario usuario, String portal, String escalera,
                                                        String planta,
                                                        String puerta,
                                                        String roles)
    {
        return new UsuarioComunidad.UserComuBuilder(comunidad, usuario)
                .portal(portal)
                .escalera(escalera)
                .planta(planta)
                .puerta(puerta)
                .roles(roles).build();
    }

    public static UsuarioComunidad makeUserComuWithComunidadId(UsuarioComunidad usuarioComunidad, long comunidadId)
    {

        Comunidad comunidad = new Comunidad.ComunidadBuilder().c_id(comunidadId).build();
        return new UsuarioComunidad.UserComuBuilder(comunidad, usuarioComunidad.getUsuario()).userComuRest(usuarioComunidad).build();
    }

    //  ======================================= UI TYPING DATA ==========================================

    public static void typeComunidadData() throws InterruptedException
    {
        typeComunidadData("Calle", "Valencia", "Alicante/Alacant", "Algueña", "Real", "5", "Bis");
    }

    public static void typeComunidadData(String tipoVia, String comunidadAuto, String provincia, String municipio, String nombreVia,
                                         String numeroEnVia, String sufijoNumero) throws InterruptedException
    {
        onView(ViewMatchers.withId(R.id.tipo_via_spinner)).perform(click());
        Thread.sleep(1000);
        onData(withRowString(1, tipoVia)).perform(click());
        onView(allOf(ViewMatchers.withId(R.id.app_spinner_1_dropdown_item), withParent(ViewMatchers.withId(R.id.tipo_via_spinner))))
                .check(matches(withText(containsString(tipoVia)))).check(matches(isDisplayed()));

        onView(ViewMatchers.withId(R.id.comunidad_nombre_via_editT)).perform(typeText(nombreVia));
        onView(ViewMatchers.withId(R.id.comunidad_numero_editT)).perform(typeText(numeroEnVia));
        onView(ViewMatchers.withId(R.id.comunidad_sufijo_numero_editT)).perform(typeText(sufijoNumero), closeSoftKeyboard());

        onView(ViewMatchers.withId(R.id.autonoma_comunidad_spinner)).perform(click());
        Thread.sleep(1000);
        onData(withRowString(1, comunidadAuto)).perform(click());
        onView(allOf(ViewMatchers.withId(R.id.app_spinner_1_dropdown_item), withParent(ViewMatchers.withId(R.id.autonoma_comunidad_spinner))))
                .check(matches(withText(containsString(comunidadAuto))));

        onView(ViewMatchers.withId(R.id.provincia_spinner)).perform(click());
        Thread.sleep(1000);
        onData(withRowString(1, provincia)).perform(click());
        onView(allOf(ViewMatchers.withId(R.id.app_spinner_1_dropdown_item), withParent(ViewMatchers.withId(R.id.provincia_spinner))))
                .check(matches(withText(containsString(provincia))));

        onView(ViewMatchers.withId(R.id.municipio_spinner)).perform(click());
        Thread.sleep(1000);
        onData(withRowString(3, municipio)).perform(click());

        if (SDK_INT >= M) {
            onView(allOf(ViewMatchers.withId(R.id.app_spinner_1_dropdown_item), withParent(ViewMatchers.withId(R.id.municipio_spinner))))
                    .check(matches(withText(containsString(municipio))));
        }
    }

    public static void typeUserComuData(String portal, String escalera, String planta, String puerta, RolUi...
            roles)
    {
        onView(ViewMatchers.withId(R.id.reg_usercomu_portal_ed)).perform(typeText(portal));
        onView(ViewMatchers.withId(R.id.reg_usercomu_escalera_ed)).perform(typeText(escalera));
        onView(ViewMatchers.withId(R.id.reg_usercomu_planta_ed)).perform(scrollTo(), typeText(planta));
        onView(ViewMatchers.withId(R.id.reg_usercomu_puerta_ed)).perform(typeText(puerta), closeSoftKeyboard());

        if (roles != null) {
            for (RolUi rolUi : roles) {
                onView(withId(rolUi.resourceViewId)).perform(scrollTo(), click());
            }
        }
    }

    public static void typeUserData(String email, String alias, String password, String passwordConfirm)
    {
        onView(ViewMatchers.withId(R.id.reg_usuario_password_ediT)).perform(scrollTo(), typeText(password));
        onView(ViewMatchers.withId(R.id.reg_usuario_password_confirm_ediT)).perform(scrollTo(), typeText(passwordConfirm));
        onView(ViewMatchers.withId(R.id.reg_usuario_email_editT)).perform(scrollTo(), typeText(email));
        onView(ViewMatchers.withId(R.id.reg_usuario_alias_ediT)).perform(scrollTo(), typeText(alias), closeSoftKeyboard());
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

    public static void validaTypedUserComuBean(UsuarioComunidadBean usuarioComunidadBean, String portal, String escalera, String planta, String puerta,
                                               boolean isPre, boolean isAdm, boolean isPro, boolean isInq)
    {
        assertThat(usuarioComunidadBean, notNullValue());
        assertThat(usuarioComunidadBean.getPortal(), is(portal));
        assertThat(usuarioComunidadBean.getEscalera(), is(escalera));
        assertThat(usuarioComunidadBean.getPlanta(), is(planta));
        assertThat(usuarioComunidadBean.getPuerta(), is(puerta));
        assertThat(usuarioComunidadBean.isPresidente(), is(isPre));
        assertThat(usuarioComunidadBean.isAdministrador(), is(isAdm));
        assertThat(usuarioComunidadBean.isPropietario(), is(isPro));
        assertThat(usuarioComunidadBean.isInquilino(), is(isInq));
    }

    public static void validaTypedUsuarioComunidad(UsuarioComunidad usuarioComunidad, String portal, String escalera, String planta, String puerta, String roles)
    {
        assertThat(usuarioComunidad, notNullValue());
        assertThat(usuarioComunidad.getPortal(), is(portal));
        assertThat(usuarioComunidad.getEscalera(), is(escalera));
        assertThat(usuarioComunidad.getPlanta(), is(planta));
        assertThat(usuarioComunidad.getPuerta(), is(puerta));
        assertThat(usuarioComunidad.getRoles(), is(roles));
    }

    public static void validaTypedUsuario(Usuario usuario, String email, String alias1, String password)
    {
        assertThat(usuario, notNullValue());
        assertThat(usuario.getUserName(), is(email));
        assertThat(usuario.getAlias(), is(alias1));
        assertThat(usuario.getPassword(), is(password));
    }
}


