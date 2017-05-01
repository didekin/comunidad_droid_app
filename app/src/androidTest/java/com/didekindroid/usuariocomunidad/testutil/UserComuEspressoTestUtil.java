package com.didekindroid.usuariocomunidad.testutil;


import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.matcher.ViewMatchers;

import com.didekindroid.R;
import com.didekindroid.usuariocomunidad.RolUi;
import com.didekindroid.usuariocomunidad.UsuarioComunidadBean;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.AppInitializer.creator;
import static com.didekindroid.usuariocomunidad.RolUi.formatRolToString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;
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

    public static DataInteraction checkUserComuByComuCommon(UsuarioComunidad userComuRow)
    {
        return onData(is(userComuRow))
                .inAdapterView(withId(android.R.id.list))
                .onChildView(allOf(
                        withText(formatRolToString(userComuRow.getRoles(), creator.get().getContext().getResources())),
                        withId(R.id.usercomu_item_roles_txt)
                ));
    }

    public static DataInteraction checkUserComuPortalEscalera(UsuarioComunidad userComuRow, DataInteraction parent)
    {
        return parent.onChildView(allOf(
                withId(R.id.usercomu_portal_escalera_block),
                withChild(allOf(
                        withId(R.id.usercomu_item_portal_txt),
                        withText(userComuRow.getPortal())
                )),
                withChild(allOf(
                        withId(R.id.usercomu_item_escalera_txt),
                        withText(userComuRow.getEscalera())
                ))
        ));
    }

    public static DataInteraction checkUserComuPortalNoEscalera(UsuarioComunidad userComuRow, DataInteraction parent)
    {
        return parent.onChildView(allOf(
                withId(R.id.usercomu_portal_escalera_block),
                withChild(allOf(
                        withId(R.id.usercomu_item_portal_txt),
                        withText(userComuRow.getPortal())
                )),
                withChild(allOf(
                        withId(R.id.usercomu_item_escalera_txt),
                        withEffectiveVisibility(ViewMatchers.Visibility.GONE))
                ))
        );
    }

    public static DataInteraction checkUserComuNoPortalNoEscalera(UsuarioComunidad userComuRow, DataInteraction parent)
    {
        return parent.onChildView(allOf(
                withId(R.id.usercomu_portal_escalera_block),
                withEffectiveVisibility(ViewMatchers.Visibility.GONE)
        ));
    }

    public static DataInteraction checkUserComuPlantaPuerta(UsuarioComunidad userComuRow, DataInteraction parent)
    {
        return parent.onChildView(allOf(
                withId(R.id.usercomu_planta_puerta_block),
                withChild(allOf(
                        withId(R.id.usercomu_item_planta_txt),
                        withText(userComuRow.getPlanta())
                )),
                withChild(allOf(
                        withId(R.id.usercomu_item_puerta_txt),
                        withText(userComuRow.getPuerta())
                ))
        ));
    }

    public static DataInteraction checkUserComuPlantaNoPuerta(UsuarioComunidad userComuRow, DataInteraction parent)
    {
        return parent.onChildView(allOf(
                withId(R.id.usercomu_planta_puerta_block),
                withChild(allOf(
                        withId(R.id.usercomu_item_planta_txt),
                        withText(userComuRow.getPlanta())
                )),
                withChild(allOf(
                        withId(R.id.usercomu_item_puerta_txt),
                        withEffectiveVisibility(ViewMatchers.Visibility.GONE))
                ))
        );
    }

    public static DataInteraction checkUserComuNoPlantaNoPuerta(UsuarioComunidad userComuRow, DataInteraction parent)
    {
        return parent.onChildView(allOf(
                withId(R.id.usercomu_planta_puerta_block),
                withEffectiveVisibility(ViewMatchers.Visibility.GONE)
        ));
    }

    public static void runFinalCheckUserComuByComu(DataInteraction parent)
    {
        parent.check(matches(isDisplayed()));
    }
}
