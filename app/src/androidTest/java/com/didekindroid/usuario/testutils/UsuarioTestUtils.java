package com.didekindroid.usuario.testutils;

import com.didekin.usuario.dominio.Comunidad;
import com.didekin.usuario.dominio.Municipio;
import com.didekin.usuario.dominio.Provincia;
import com.didekin.usuario.dominio.Usuario;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.usuario.activity.utils.RolCheckBox;

import java.util.ArrayList;
import java.util.List;

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
import static com.didekin.usuario.dominio.Rol.PRESIDENTE;
import static com.didekin.usuario.dominio.Rol.PROPIETARIO;
import static com.didekindroid.usuario.activity.utils.RolCheckBox.ADM;
import static com.didekindroid.usuario.activity.utils.RolCheckBox.INQ;
import static com.didekindroid.usuario.activity.utils.RolCheckBox.PRE;
import static com.didekindroid.usuario.activity.utils.RolCheckBox.PRO;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

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

    public static List<UsuarioComunidad> makeListTwoUserComu()
    {
        // Dos comunidades diferentes con un mismo userComu.
        List<UsuarioComunidad> userComuList = new ArrayList<>(2);
        userComuList.add(COMU_REAL_JUAN);
        userComuList.add(COMU_PLAZUELA5_JUAN);
        return userComuList;
    }

//  ======================================= UI TYPING DATA ==========================================

    public static void typeComunidadData() throws InterruptedException
    {
        onView(withId(R.id.tipo_via_spinner)).perform(click());
        onData(allOf(
                is(instanceOf(String.class)),
                is("Calle")
        )).perform(click());
        onView(allOf(withId(R.id.app_spinner_1_dropdown_item), withParent(withId(R.id.tipo_via_spinner))))
                .check(matches(withText(containsString("Calle")))).check(matches(isDisplayed()));

        Thread.sleep(5000);
        onView(withId(R.id.autonoma_comunidad_spinner)).perform(click());
        onData(withRowString(1, "Valencia")).perform(click());
        onView(allOf(withId(R.id.app_spinner_1_dropdown_item), withParent(withId(R.id.autonoma_comunidad_spinner))))
                .check(matches(withText(containsString("Valencia"))));

        onView(withId(R.id.provincia_spinner)).perform(click());
        onData(withRowString(1, "Alicante/Alacant")).perform(click());
        onView(allOf(withId(R.id.app_spinner_1_dropdown_item), withParent(withId(R.id.provincia_spinner))))
                .check(matches(withText(containsString("Alicante/Alacant"))));

        onView(withId(R.id.municipio_spinner)).perform(click());
        onData(withRowString(3, "Algueña")).perform(click());
        onView(allOf(withId(R.id.app_spinner_1_dropdown_item), withParent(withId(R.id.municipio_spinner))))
                .check(matches(withText(containsString("Algueña"))));


        onView(withId(R.id.comunidad_nombre_via_editT)).perform(typeText("Real"));
        onView(withId(R.id.comunidad_numero_editT)).perform(typeText("5"));
        onView(withId(R.id.comunidad_sufijo_numero_editT)).perform(typeText("Bis"), closeSoftKeyboard());
    }

    public static void typeRegUserComuData(String portal, String escalera, String planta, String puerta, RolCheckBox...
            roles)
    {
        onView(withId(R.id.reg_usercomu_portal_ed)).perform(typeText(portal));
        onView(withId(R.id.reg_usercomu_escalera_ed)).perform(typeText(escalera));
        onView(withId(R.id.reg_usercomu_planta_ed)).perform(typeText(planta));
        onView(withId(R.id.reg_usercomu_puerta_ed)).perform(typeText(puerta), closeSoftKeyboard());

        if (roles != null) {
            for (RolCheckBox rolCheckBox : roles) {
                onView(withId(rolCheckBox.resourceViewId)).perform(scrollTo(), click());
            }
        }
    }
}
