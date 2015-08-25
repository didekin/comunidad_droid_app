package com.didekindroid.usuario.common;

import android.app.Activity;
import android.content.res.Resources;
import android.support.test.espresso.ViewInteraction;
import com.didekindroid.DidekindroidApp;
import com.didekindroid.R;
import com.didekindroid.common.dominio.Rol;
import com.didekindroid.masterdata.dominio.Municipio;
import com.didekindroid.masterdata.dominio.Provincia;
import com.didekindroid.usuario.dominio.AccessToken;
import com.didekindroid.usuario.dominio.Comunidad;
import com.didekindroid.usuario.dominio.Usuario;
import com.didekindroid.usuario.dominio.UsuarioComunidad;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.CursorMatchers.withRowString;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.didekindroid.DidekindroidApp.getContext;
import static com.didekindroid.common.dominio.Rol.*;
import static com.didekindroid.common.ui.UIutils.updateIsRegistered;
import static com.didekindroid.usuario.common.TokenHandler.TKhandler;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

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

    public static final Usuario USUARIO_1 = new Usuario("juan@juan.us", "juan", "psw_juan", (short) 0, 0);
    public static final Usuario USUARIO_2 = new Usuario("pepe@pepe.org", "pepe", "psw_pepe", (short) 34, 234432123);

    public static final UsuarioComunidad USUARIO_COMUNIDAD_1 = new UsuarioComunidad(COMUNIDAD_1, USUARIO_1, "portal", "esc",
            "plantaX", "door12", PROPIETARIO.function);
    public static final UsuarioComunidad USUARIO_COMUNIDAD_2 = new UsuarioComunidad(COMUNIDAD_2, USUARIO_1, null,
            null, "planta3", "doorA", ADMINISTRADOR.function);
    public static final UsuarioComunidad USUARIO_COMUNIDAD_3 = new UsuarioComunidad(COMUNIDAD_2, USUARIO_2, "portalA",
            null, "planta2", null, INQUILINO.function);

    private DataUsuarioTestUtils()
    {
    }

    public static List<UsuarioComunidad> makeListTwoUserComu()
    {
        List<UsuarioComunidad> userComuList = new ArrayList<UsuarioComunidad>(2);
        userComuList.add(USUARIO_COMUNIDAD_1);
        userComuList.add(USUARIO_COMUNIDAD_2);
        return userComuList;
    }

    public static void updateSecurityData(String userName, String password)
    {
        AccessToken token = ServOne.getPasswordUserToken(userName, password);
        TKhandler.initKeyCacheAndBackupFile(token);
        updateIsRegistered(true, getContext());
    }

    public static Usuario signUpAndUpdateTk(UsuarioComunidad usuarioComunidad)
    {
        Usuario usuario = ServOne.signUp(usuarioComunidad);
        updateSecurityData(usuarioComunidad.getUsuario().getUserName(), usuarioComunidad.getUsuario().getPassword());
        return usuario;
    }

    public static void regComuAndUserComuWith2Comu(List<UsuarioComunidad> usuarioComunidadList)
    {
        signUpAndUpdateTk(usuarioComunidadList.get(0));
        ServOne.regComuAndUserComu(usuarioComunidadList.get(1));
    }

    public static void regComuAndUserComuWith3Comu(List<UsuarioComunidad> usuarioComunidadList, Comunidad comunidad)
    {
        UsuarioComunidad usuarioComunidad = new UsuarioComunidad(comunidad, usuarioComunidadList.get(0).getUsuario(),
                null, null, "plan-5", null, ADMINISTRADOR.function);
        regComuAndUserComuWith2Comu(usuarioComunidadList);
        ServOne.regComuAndUserComu(usuarioComunidad);
    }

    public static void typeComunidadData()
    {
        onView(allOf(withId(R.id.tipo_via_spinner))).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Calle"))).perform(click());
        onView(allOf(withId(R.id.reg_comunidad_spinner_dropdown_item), withParent(withId(R.id.tipo_via_spinner))))
                .check(matches(withText(containsString("Calle"))));

        onView(withId(R.id.autonoma_comunidad_spinner)).perform(click());
        onData(withRowString(1, "Valencia")).perform(click());
        onView(allOf(withId(R.id.reg_comunidad_spinner_dropdown_item), withParent(withId(R.id.autonoma_comunidad_spinner))))
                .check(matches(withText(containsString("Valencia"))));

        onView(withId(R.id.provincia_spinner)).perform(click());
        onData(withRowString(1, "Alicante/Alacant")).perform(click());
        onView(allOf(withId(R.id.reg_comunidad_spinner_dropdown_item), withParent(withId(R.id.provincia_spinner))))
                .check(matches(withText(containsString("Alicante/Alacant"))));

        onView(withId(R.id.municipio_spinner)).perform(click());
        onData(withRowString(3, "Algueña")).perform(click());
        onView(allOf(withId(R.id.reg_comunidad_spinner_dropdown_item), withParent(withId(R.id.municipio_spinner))))
                .check(matches(withText(containsString("Algueña"))));


        onView(withId(R.id.comunidad_nombre_via_editT)).perform(typeText("Real"));
        onView(withId(R.id.comunidad_numero_editT)).perform(typeText("5"));
        onView(withId(R.id.comunidad_sufijo_numero_editT)).perform(typeText("Bis"), closeSoftKeyboard());
    }

    public static void typeRegUserComuData(String portal, String escalera, String planta, String puerta, Rol...
            roles)
    {
        onView(withId(R.id.reg_usercomu_portal_ed)).perform(typeText(portal));
        onView(withId(R.id.reg_usercomu_escalera_ed)).perform(typeText(escalera));
        onView(withId(R.id.reg_usercomu_planta_ed)).perform(typeText(planta));
        onView(withId(R.id.reg_usercomu_puerta_ed)).perform(typeText(puerta), closeSoftKeyboard());

        if (roles != null) {
            for (Rol rol : roles) {
                onView(withId(rol.resourceViewId)).perform(scrollTo(), click());
            }
        }
    }

    public static void makeErrorValidationToast(Activity activity, int... fieldsErrors)
    {
        Resources resources = DidekindroidApp.getContext().getResources();

        ViewInteraction toast = onView(
                withText(containsString(resources.getText(R.string.error_validation_msg).toString())))
                .inRoot(withDecorView(not(activity.getWindow().getDecorView())))
                .check(matches(isDisplayed()));

        if (fieldsErrors != null){
            for (int field : fieldsErrors) {
                toast.check(matches(withText(containsString(resources.getText(field).toString()))));
            }
        }
    }
}
