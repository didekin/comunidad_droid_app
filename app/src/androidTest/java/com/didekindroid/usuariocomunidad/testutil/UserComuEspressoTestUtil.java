package com.didekindroid.usuariocomunidad.testutil;

import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.usuariocomunidad.RolUi;
import com.didekindroid.usuariocomunidad.UsuarioComunidadBean;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 11:38
 */

public final class UserComuEspressoTestUtil {

    private UserComuEspressoTestUtil()
    {
    }

    public static void typeUserComuData(String portal, String escalera, String planta, String puerta, RolUi...
            roles)
    {
        onView(withId(R.id.reg_usercomu_portal_ed)).perform(typeText(portal));
        onView(withId(R.id.reg_usercomu_escalera_ed)).perform(typeText(escalera));
        onView(withId(R.id.reg_usercomu_planta_ed)).perform(scrollTo(), typeText(planta));
        onView(withId(R.id.reg_usercomu_puerta_ed)).perform(typeText(puerta), closeSoftKeyboard());

        if (roles != null) {
            for (RolUi rolUi : roles) {
                onView(withId(rolUi.resourceViewId)).perform(scrollTo(), click());
            }
        }
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
}
