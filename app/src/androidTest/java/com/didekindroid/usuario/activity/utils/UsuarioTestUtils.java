package com.didekindroid.usuario.activity.utils;

import com.didekindroid.R;

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

    //    ==================== TYPING DATA =====================

    public static void typeComunidadData()
    {
        onView(withId(R.id.tipo_via_spinner)).perform(click());
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
}
