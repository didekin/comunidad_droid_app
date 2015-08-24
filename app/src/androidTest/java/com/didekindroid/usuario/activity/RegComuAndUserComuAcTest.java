package com.didekindroid.usuario.activity;

import android.content.res.Resources;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import com.didekindroid.R;
import com.didekindroid.masterdata.dominio.Municipio;
import com.didekindroid.masterdata.dominio.Provincia;
import com.didekindroid.usuario.dominio.ComunidadBean;
import com.didekindroid.usuario.dominio.UsuarioComunidad;
import com.didekindroid.usuario.dominio.UsuarioComunidadBean;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.CursorMatchers.withRowString;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.didekindroid.usuario.beanfiller.UserAndComuFiller.makeUsuarioComunidadBeanFromView;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;

/**
 * User: pedro
 * Date: 09/07/15
 * Time: 09:56
 */
@RunWith(AndroidJUnit4.class)
public class RegComuAndUserComuAcTest {

    private RegComuAndUserComuAc activity;
    private Resources resources;
    private RegComuFr regComuFr;
    private RegUserComuFr regUserComuFr;

    @Rule
    public ActivityTestRule<RegComuAndUserComuAc> mActivityRule = new ActivityTestRule<>(RegComuAndUserComuAc.class);

    @Before
    public void setUp() throws Exception
    {
        activity = mActivityRule.getActivity();
        resources = activity.getResources();
        regComuFr = (RegComuFr) activity.getFragmentManager().findFragmentById(R.id.reg_comunidad_frg);
        regUserComuFr = (RegUserComuFr) activity.getFragmentManager().findFragmentById(R.id
                .reg_usuariocomunidad_frg);
    }

    @Test
    public void testPreconditions()
    {
        assertThat(activity, notNullValue());
        assertThat(resources, notNullValue());
        assertThat(regComuFr, notNullValue());
        assertThat(regUserComuFr, notNullValue());
        onView(withId(R.id.reg_comu_usuariocomu_layout));
        onView(withId(R.id.reg_comunidad_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usuariocomunidad_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_comu_usuariocomunidad_button)).perform(scrollTo()).check(matches(isDisplayed()));
    }

    @Test
    public void testMakeUsuarioComunidadBeanFromView() throws Exception
    {
        View usuarioComunidadRegView = activity.findViewById(R.id.reg_usuariocomunidad_frg);

        onView(withId(R.id.reg_usuariocomunidad_portal_editT)).perform(typeText("port2"));
        onView(withId(R.id.reg_usuariocomunidad_escalera_editT)).perform(typeText("escale_b"));
        onView(withId(R.id.reg_usuariocomunidad_planta_editT)).perform(typeText("planta-N"));
        onView(withId(R.id.reg_usuariocomunidad_puerta_editT)).perform(typeText("puerta5"), closeSoftKeyboard());
        onView(withId(R.id.reg_usuariocomunidad_roles_checbox_presi)).perform(scrollTo(), click());
        onView(withId(R.id.reg_usuariocomunidad_roles_checbox_admin)).perform(scrollTo(), click());
        onView(withId(R.id.reg_usuariocomunidad_roles_checbox_inquilino)).perform(scrollTo(), click());

        ComunidadBean comunidadBean = new ComunidadBean("ataxo", "24 de Otoño", "001", "bis",
                new Municipio(new Provincia((short) 10), (short) 162));

        UsuarioComunidadBean usuarioComunidadBean =
                makeUsuarioComunidadBeanFromView(usuarioComunidadRegView, comunidadBean, null);
        assertThat(usuarioComunidadBean.getPortal(), is("port2"));
        assertThat(usuarioComunidadBean.getEscalera(), is("escale_b"));
        assertThat(usuarioComunidadBean.getPlanta(), is("planta-N"));
        assertThat(usuarioComunidadBean.getPuerta(), is("puerta5"));
        assertThat(usuarioComunidadBean.isPresidente, is(true));
        assertThat(usuarioComunidadBean.isAdministrador, is(true));
        assertThat(usuarioComunidadBean.isPropietario, is(false));
        assertThat(usuarioComunidadBean.isInquilino, is(true));

        usuarioComunidadBean.validate(resources, new StringBuilder(resources.getString(R.string.error_validation_msg)), true);
        UsuarioComunidad usuarioComunidad = usuarioComunidadBean.getUsuarioComunidad();
        assertThat(usuarioComunidad.getPortal(), is("port2"));
        assertThat(usuarioComunidad.getEscalera(), is("escale_b"));
        assertThat(usuarioComunidad.getPlanta(), is("planta-N"));
        assertThat(usuarioComunidad.getPuerta(), is("puerta5"));
        assertThat(usuarioComunidad.getRoles(), is("adm,pre,inq"));

    }

    @Test
    public void testWrongUsuarioComunidad_1()
    {
        onView(withId(R.id.reg_comu_usuariocomunidad_button)).perform(scrollTo(), click());

        ViewInteraction toastViewInteraction = onView(withText(
                containsString(resources.getText(R.string.error_validation_msg).toString())
        ));

        toastViewInteraction.inRoot(withDecorView(not(activity.getWindow().getDecorView())))
                .check(matches(isDisplayed()))
                .check(matches(withText(containsString(resources.getText(R.string.tipo_via).toString()))))
                .check(matches(withText(containsString(resources.getText(R.string.nombre_via).toString()))))
                .check(matches(withText(containsString(resources.getText(R.string.municipio).toString()))))
                .check(matches(withText(containsString(resources.getText(R.string.comunidad_role).toString()))));
    }

    @Test
    public void tesWrongUsuarioComunidad_2()
    {
        onView(withId(R.id.reg_usuariocomunidad_roles_checbox_presi)).perform(scrollTo(), click());
        onView(withId(R.id.reg_usuariocomunidad_roles_checbox_admin)).perform(scrollTo(), click());
        onView(withId(R.id.reg_usuariocomunidad_roles_checbox_inquilino)).perform(scrollTo(), click());
        onView(withId(R.id.reg_usuariocomunidad_roles_checbox_propietario)).perform(scrollTo(), click());

        onView(withId(R.id.reg_usuariocomunidad_escalera_editT)).perform(typeText("esca??_b"));

        onView(withId(R.id.reg_comu_usuariocomunidad_button)).perform(scrollTo(), click());

        ViewInteraction toastViewInteraction = onView(withText(
                containsString(resources.getText(R.string.error_validation_msg).toString())
        ));

        toastViewInteraction.inRoot(withDecorView(not(activity.getWindow().getDecorView())))
                .check(matches(isDisplayed()))
                .check(matches(withText(containsString(resources.getText(R.string.tipo_via).toString()))))
                .check(matches(withText(containsString(resources.getText(R.string.vivienda_escalera_hint).toString()))))
                .check(matches(withText(containsString(resources.getText(R.string.nombre_via).toString()))))
                .check(matches(withText(containsString(resources.getText(R.string.municipio).toString()))))
                .check(matches(withText(containsString(resources.getText(R.string.comunidad_role).toString()))));
    }

    @Test
    public void testOkUsuarioComunidad()
    {

        onData(allOf(is(instanceOf(String.class)), is("Callejon")))
                .perform(click());

        onView(withId(R.id.autonoma_comunidad_spinner)).perform(click());
        onData(withRowString(1, "Valencia"))
                .perform(click());

        onView(withId(R.id.provincia_spinner)).perform(click());
        onData(withRowString(1, "Castellón/Castelló"))
                .perform(click());

        onView(withId(R.id.municipio_spinner)).perform(click());
        onData(withRowString(3, "Chilches/Xilxes"))
                .perform(click());

        onView(withId(R.id.comunidad_nombre_via_editT)).perform(typeText("nombre via One"));
        onView(withId(R.id.comunidad_numero_editT)).perform(typeText("123"));
        onView(withId(R.id.comunidad_sufijo_numero_editT)).perform(typeText("Tris"), closeSoftKeyboard());

        onView(withId(R.id.reg_usuariocomunidad_portal_editT)).perform(typeText("port2"));
        onView(withId(R.id.reg_usuariocomunidad_escalera_editT)).perform(typeText("escale_b"));
        onView(withId(R.id.reg_usuariocomunidad_planta_editT)).perform(typeText("planta-N"));
        onView(withId(R.id.reg_usuariocomunidad_puerta_editT)).perform(typeText("puerta5"), closeSoftKeyboard());
        onView(withId(R.id.reg_usuariocomunidad_roles_checbox_presi)).perform(scrollTo(), click());
        onView(withId(R.id.reg_usuariocomunidad_roles_checbox_admin)).perform(scrollTo(), click());
        onView(withId(R.id.reg_usuariocomunidad_roles_checbox_inquilino)).perform(scrollTo(), click());

        onView(withId(R.id.reg_comu_usuariocomunidad_button)).perform(scrollTo(), click());

        //TODO: assertions sobre la nueva actividad.
    }
}