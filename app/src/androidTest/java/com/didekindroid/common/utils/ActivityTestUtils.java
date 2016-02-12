package com.didekindroid.common.utils;

import android.app.Activity;
import android.content.res.Resources;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.core.deps.guava.base.Preconditions;

import com.didekin.common.oauth2.OauthToken;
import com.didekin.usuario.dominio.Comunidad;
import com.didekin.usuario.dominio.Usuario;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.DidekindroidApp;
import com.didekindroid.common.UiException;
import com.didekindroid.usuario.activity.utils.CleanUserEnum;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.DidekindroidApp.getContext;
import static com.didekindroid.common.TokenHandler.TKhandler;
import static com.didekindroid.common.utils.UIutils.updateIsRegistered;
import static com.didekindroid.common.webservices.Oauth2Service.Oauth2;
import static com.didekindroid.usuario.activity.utils.RolCheckBox.ADM;
import static com.didekindroid.usuario.dominio.DomainDataUtils.USER_JUAN;
import static com.didekindroid.usuario.dominio.DomainDataUtils.USER_JUAN2;
import static com.didekindroid.usuario.dominio.DomainDataUtils.USER_PEPE;
import static com.didekindroid.usuario.dominio.DomainDataUtils.makeUsuarioComunidad;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

/**
 * User: pedro@didekin
 * Date: 23/11/15
 * Time: 15:29
 */
public final class ActivityTestUtils {

    private ActivityTestUtils()
    {
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

    public static void regSeveralUserComuSameUser(UsuarioComunidad... userComus) throws UiException
    {
        Preconditions.checkArgument(userComus.length > 0);
        signUpAndUpdateTk(userComus[0]);
        for (int i = 1; i < userComus.length; i++) {
            ServOne.regComuAndUserComu(userComus[i]);
        }
    }

//    ============================ SECURITY ============================

    public static void updateSecurityData(String userName, String password) throws UiException
    {
        OauthToken.AccessToken token = Oauth2.getPasswordUserToken(userName, password);
        TKhandler.initKeyCacheAndBackupFile(token);
        updateIsRegistered(true, getContext());
    }

//    ============================ TOASTS ============================

    public static void checkToastInTest(int resourceId, Activity activity, int... resourceFieldsErrorId)
    {
        Resources resources = DidekindroidApp.getContext().getResources();

        ViewInteraction toast = onView(
                withText(containsString(resources.getText(resourceId).toString())))
                .inRoot(withDecorView(not(activity.getWindow().getDecorView())))
                .check(matches(isDisplayed()));

        if (resourceFieldsErrorId != null) {
            for (int field : resourceFieldsErrorId) {
                toast.check(matches(withText(containsString(resources.getText(field).toString()))));
            }
        }
    }

    public static void checkNoToastInTest(int resourceStringId, Activity activity)
    {
        Resources resources = DidekindroidApp.getContext().getResources();

        onView(
                withText(containsString(resources.getText(resourceStringId).toString())))
                .inRoot(withDecorView(not(activity.getWindow().getDecorView())))
                .check(doesNotExist());
    }
}
