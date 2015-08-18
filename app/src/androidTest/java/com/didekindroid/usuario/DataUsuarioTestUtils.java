package com.didekindroid.usuario;

import com.didekindroid.R;
import com.didekindroid.masterdata.dominio.Municipio;
import com.didekindroid.masterdata.dominio.Provincia;
import com.didekindroid.usuario.comunidad.dominio.Comunidad;
import com.didekindroid.usuario.comunidad.dominio.Usuario;
import com.didekindroid.usuario.comunidad.dominio.UsuarioComunidad;
import com.didekindroid.usuario.login.dominio.AccessToken;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.CursorMatchers.withRowString;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.didekindroid.DidekindroidApp.getContext;
import static com.didekindroid.common.ui.UIutils.updateIsRegistered;
import static com.didekindroid.usuario.comunidad.dominio.Roles.ADMINISTRADOR;
import static com.didekindroid.usuario.comunidad.dominio.Roles.PROPIETARIO;
import static com.didekindroid.usuario.login.TokenHandler.TKhandler;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static org.hamcrest.CoreMatchers.*;

/**
 * User: pedro
 * Date: 21/07/15
 * Time: 11:19
 */
public final class DataUsuarioTestUtils {

    public static final Comunidad COMUNIDAD_1 = new Comunidad("Calle", "Real", (short) 5, "Bis",
            new Municipio(new Provincia((short) 3), (short) 13));
    public static final Comunidad COMUNIDAD_2 = new Comunidad("Ronda", "de la Plazuela", (short) 5, null,
            new Municipio(new Provincia((short) 27), (short) 2));
    public static final Usuario USUARIO = new Usuario("juan@juan.us", "juan", "psw_juan", (short) 0, 0);
    public static final UsuarioComunidad USUARIO_COMUNIDAD_1 = new UsuarioComunidad(COMUNIDAD_1, USUARIO, "portal", "esc",
            "plantaX", "door12", PROPIETARIO.getFunction());
    public static final UsuarioComunidad USUARIO_COMUNIDAD_2 = new UsuarioComunidad(COMUNIDAD_2, USUARIO, null,
            null, "planta3", "doorA", ADMINISTRADOR.getFunction());

    private DataUsuarioTestUtils()
    {
    }

    public static void insertOneUserOneComu()
    {
        Usuario usuario = ServOne.signUp(USUARIO_COMUNIDAD_1);
        AccessToken token = ServOne.getPasswordUserToken(usuario.getUserName(), "psw_juan");
        TKhandler.initKeyCacheAndBackupFile(token);
        updateIsRegistered(true, getContext());
    }

    public static void insertOneUserTwoComu()
    {
        insertOneUserOneComu();
        ServOne.insertUserOldComunidadNew(USUARIO_COMUNIDAD_2);
    }

    public static void insertOnePlusComu(Comunidad comunidad)
    {
        UsuarioComunidad usuarioComunidad = new UsuarioComunidad(comunidad,USUARIO,null,null,"plan-5",null,
                ADMINISTRADOR.getFunction());
        insertOneUserTwoComu();
        ServOne.insertUserOldComunidadNew(usuarioComunidad);
    }

    public static void typeComunidadData()
    {
        onView(allOf(withId(R.id.tipo_via_spinner))).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Callejon"))).perform(click());
        onView(allOf(withId(R.id.reg_comunidad_spinner_dropdown_item), withParent(withId(R.id.tipo_via_spinner))))
                .check(matches(withText(containsString("Callejon"))));

        onView(withId(R.id.autonoma_comunidad_spinner)).perform(click());
        onData(withRowString(1, "Valencia")).perform(click());
        onView(allOf(withId(R.id.reg_comunidad_spinner_dropdown_item), withParent(withId(R.id.autonoma_comunidad_spinner))))
                .check(matches(withText(containsString("Valencia"))));

        onView(withId(R.id.provincia_spinner)).perform(click());
        onData(withRowString(1, "Castellón/Castelló")).perform(click());
        onView(allOf(withId(R.id.reg_comunidad_spinner_dropdown_item), withParent(withId(R.id.provincia_spinner))))
                .check(matches(withText(containsString("Castellón/Castelló"))));

        onView(withId(R.id.municipio_spinner)).perform(click());
        onData(withRowString(3, "Chilches/Xilxes")).perform(click());
        onView(allOf(withId(R.id.reg_comunidad_spinner_dropdown_item), withParent(withId(R.id.municipio_spinner))))
                .check(matches(withText(containsString("Chilches/Xilxes"))));


        onView(withId(R.id.comunidad_nombre_via_editT)).perform(typeText("nombre via One"));
        onView(withId(R.id.comunidad_numero_editT)).perform(typeText("123"));
        onView(withId(R.id.comunidad_sufijo_numero_editT)).perform(typeText("Tris"), closeSoftKeyboard());
    }
}
