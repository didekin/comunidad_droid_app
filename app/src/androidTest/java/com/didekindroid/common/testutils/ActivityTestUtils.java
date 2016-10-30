package com.didekindroid.common.testutils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.test.espresso.ViewInteraction;
import android.widget.DatePicker;

import com.didekin.oauth2.SpringOauthToken;
import com.didekin.usuario.dominio.Comunidad;
import com.didekin.usuario.dominio.Usuario;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.DidekindroidApp;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.usuario.testutils.CleanUserEnum;
import com.didekindroid.usuario.testutils.UsuarioTestUtils;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.KITKAT;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.PickerActions.setDate;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.DidekindroidApp.getContext;
import static com.didekindroid.common.activity.TokenHandler.TKhandler;
import static com.didekindroid.common.utils.UIutils.updateIsRegistered;
import static com.didekindroid.common.webservices.Oauth2Service.Oauth2;
import static com.didekindroid.usuario.activity.utils.RolUi.ADM;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.USER_JUAN2;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.makeUsuarioComunidad;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
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
        TKhandler.cleanTokenAndBackFile();
        updateIsRegistered(false, getContext());
    }

    public static void cleanWithTkhandler(Context context)
    {
        TKhandler.cleanTokenAndBackFile();
        updateIsRegistered(false, context);
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

    //    ============================= DATE PICKERS ===================================

    public static Calendar reSetDatePicker(long fechaInicial, int monthsToAdd)
    {
        Calendar newCalendar = new GregorianCalendar();
        if (fechaInicial > 0L) {
            newCalendar.setTimeInMillis(fechaInicial);
        }
        // Aumentamos la fecha estimada en un número de meses.
        newCalendar.add(MONTH, monthsToAdd);
        // Android PickerActions substract 1 from the month passed to setDate(), so we increased the month parameter value in 1 before passing it.
        onView(withClassName(is(DatePicker.class.getName())))
                .perform(setDate(newCalendar.get(Calendar.YEAR), newCalendar.get(MONTH) + 1, newCalendar.get(DAY_OF_MONTH)));
        return newCalendar;
    }

    public static void closeDatePicker(Context context)
    {
        if (SDK_INT == KITKAT) {
            onView(withId(android.R.id.button1)).perform(click());
        }
        if (SDK_INT > KITKAT) {
            onView(withText(context.getString(android.R.string.ok))).perform(click());
        }
    }

    //    ============================= DATES ===================================

    public static Timestamp doTimeStampFromCalendar(int daysToAdd)
    {
        Calendar fCierre = new GregorianCalendar();
        fCierre.add(DAY_OF_MONTH, daysToAdd);
        return new Timestamp(fCierre.getTimeInMillis());
    }

    //    ============================= NAVIGATION ===================================

    public static void clickNavigateUp()
    {
        onView(allOf(
                withContentDescription(R.string.navigate_up_txt),
                isClickable())
        ).check(matches(isDisplayed())).perform(click());
    }

    public static void checkUp(Integer... activityLayoutIds)
    {
        clickNavigateUp();
        for (Integer layout : activityLayoutIds) {
            onView(withId(layout)).check(matches(isDisplayed()));
        }
    }

    public static void checkBack(ViewInteraction viewInteraction, Integer... activityLayoutIds){
        viewInteraction.perform(closeSoftKeyboard()).perform(pressBack());
        for (Integer layout : activityLayoutIds) {
            onView(withId(layout)).check(matches(isDisplayed()));
        }
    }

//    =========================== REGISTERING USERS ==============================

    public static Usuario signUpAndUpdateTk(UsuarioComunidad usuarioComunidad) throws UiException, IOException
    {
        ServOne.regComuAndUserAndUserComu(usuarioComunidad).execute().body();
        updateSecurityData(usuarioComunidad.getUsuario().getUserName(), usuarioComunidad.getUsuario().getPassword());
        return ServOne.getUserData();
    }

    public static void regTwoUserComuSameUser(List<UsuarioComunidad> usuarioComunidadList) throws UiException, IOException
    {
        signUpAndUpdateTk(usuarioComunidadList.get(0));
        ServOne.regComuAndUserComu(usuarioComunidadList.get(1));
    }

    public static void regThreeUserComuSameUser(List<UsuarioComunidad> usuarioComunidadList, Comunidad comunidad) throws UiException, IOException
    {
        regTwoUserComuSameUser(usuarioComunidadList);
        UsuarioComunidad usuarioComunidad = makeUsuarioComunidad(comunidad, usuarioComunidadList.get(0).getUsuario(),
                null, null, "plan-5", null, ADM.function);
        ServOne.regComuAndUserComu(usuarioComunidad);
    }

    public static void regSeveralUserComuSameUser(UsuarioComunidad... userComus) throws UiException, IOException
    {
        Objects.equals(userComus.length > 0, true);
        signUpAndUpdateTk(userComus[0]);
        for (int i = 1; i < userComus.length; i++) {
            ServOne.regComuAndUserComu(userComus[i]);
        }
    }

    public static List<UsuarioComunidad> makeListTwoUserComu()
    {
        // Dos comunidades diferentes con un mismo userComu.
        List<UsuarioComunidad> userComuList = new ArrayList<>(2);
        userComuList.add(UsuarioTestUtils.COMU_REAL_JUAN);
        userComuList.add(UsuarioTestUtils.COMU_PLAZUELA5_JUAN);
        return userComuList;
    }

//    ============================ SECURITY ============================

    public static void updateSecurityData(String userName, String password) throws UiException
    {
        SpringOauthToken token = Oauth2.getPasswordUserToken(userName, password);
        TKhandler.initTokenAndBackupFile(token);
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
