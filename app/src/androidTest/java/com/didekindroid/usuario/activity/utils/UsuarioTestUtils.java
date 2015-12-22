package com.didekindroid.usuario.activity.utils;

import android.support.test.espresso.core.deps.guava.base.Preconditions;

import com.didekin.serviceone.domain.Comunidad;
import com.didekin.serviceone.domain.Usuario;
import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.UiException;

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
import static com.didekindroid.DidekindroidApp.getContext;
import static com.didekindroid.common.TokenHandler.TKhandler;
import static com.didekindroid.common.utils.ActivityTestUtils.updateSecurityData;
import static com.didekindroid.common.utils.UIutils.updateIsRegistered;
import static com.didekindroid.usuario.activity.utils.RolCheckBox.ADM;
import static com.didekindroid.usuario.dominio.DomainDataUtils.USER_JUAN;
import static com.didekindroid.usuario.dominio.DomainDataUtils.USER_JUAN2;
import static com.didekindroid.usuario.dominio.DomainDataUtils.USER_PEPE;
import static com.didekindroid.usuario.dominio.DomainDataUtils.makeUsuarioComunidad;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

/**
 * User: pedro
 * Date: 21/07/15
 * Time: 11:19
 */
@SuppressWarnings("unchecked")
public final class UsuarioTestUtils {

    private UsuarioTestUtils()
    {
    }

//    =========================== REGISTERING USERS ==============================

    public static Usuario signUpAndUpdateTk(UsuarioComunidad usuarioComunidad) throws UiException
    {
        ServOne.regComuAndUserAndUserComu(usuarioComunidad);
        updateSecurityData(usuarioComunidad.getUsuario().getUserName(), usuarioComunidad.getUsuario().getPassword());
        return ServOne.getUserData();
    }

    public static void regTwoUserComuSameUser(List<UsuarioComunidad> usuarioComunidadList) throws UiException
    {
        signUpAndUpdateTk(usuarioComunidadList.get(0));
        ServOne.regComuAndUserComu(usuarioComunidadList.get(1));
    }

    public static void regThreeUserComuSameUser(List<UsuarioComunidad> usuarioComunidadList, Comunidad comunidad) throws UiException
    {
        regTwoUserComuSameUser(usuarioComunidadList);
        UsuarioComunidad usuarioComunidad = makeUsuarioComunidad(comunidad, usuarioComunidadList.get(0).getUsuario(),
                null, null, "plan-5", null, ADM.function);
        ServOne.regComuAndUserComu(usuarioComunidad);
    }

    public static void regThreeUserComuSameUser_2(UsuarioComunidad... userComus) throws UiException
    {
        Preconditions.checkArgument(userComus.length > 0);
        signUpAndUpdateTk(userComus[0]);
        for (int i = 1; i < userComus.length; i++) {
            ServOne.regComuAndUserComu(userComus[i]);
        }
    }

//    ==================== TYPING DATA =====================

    public static void typeComunidadData()
    {
        onView(allOf(withId(R.id.tipo_via_spinner))).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Calle"))).perform(click());
        onView(allOf(withId(R.id.app_spinner_1_dropdown_item), withParent(withId(R.id.tipo_via_spinner))))
                .check(matches(withText(containsString("Calle")))).check(matches(isDisplayed()));

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

    //    ============================ CLEANING ============================

    public static void cleanOneUser(Usuario usuario) throws UiException
    {
        updateSecurityData(usuario.getUserName(), usuario.getPassword());
        ServOne.deleteUser();
        cleanWithTkhandler();
    }

    public static void cleanTwoUsers(Usuario usuarioOne, Usuario usuarioTwo) throws UiException
    {
        cleanOneUser(usuarioOne);
        cleanOneUser(usuarioTwo);
    }

    public static void cleanWithTkhandler()
    {
        TKhandler.cleanCacheAndBckFile();
        updateIsRegistered(false, getContext());
    }

    public static void cleanOptions(CleanUserEnum whatClean) throws UiException
    {
        switch (whatClean) {
            case CLEAN_TK_HANDLER:
                cleanWithTkhandler();
                break;
            case CLEAN_JUAN:
                cleanOneUser(USER_JUAN);
                break;
            case CLEAN_PEPE:
                cleanOneUser(USER_PEPE);
                break;
            case CLEAN_JUAN2:
                cleanOneUser(USER_JUAN2);
                break;
            case CLEAN_JUAN_AND_PEPE:
                cleanTwoUsers(USER_JUAN, USER_PEPE);
                break;
            case CLEAN_JUAN2_AND_PEPE:
                cleanTwoUsers(USER_JUAN2, USER_PEPE);
                break;
            case CLEAN_NOTHING:
                break;
            default:
                throw new IllegalStateException("Wrong cleanUp");
        }
    }
}
