package com.didekindroid.usuario.activity;

import android.content.res.Resources;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import com.didekindroid.R;
import com.didekindroid.usuario.activity.RegComuAndUserAndUserComuAc;
import com.didekindroid.usuario.activity.RegComuFr;
import com.didekindroid.usuario.activity.RegUserComuFr;
import com.didekindroid.usuario.activity.RegUserFr;
import com.didekindroid.usuario.comunidad.dominio.*;
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
import static com.didekindroid.usuario.beanfiller.UserAndComuFiller.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;

/**
 * User: pedro
 * Date: 07/07/15
 * Time: 10:26
 */
@RunWith(AndroidJUnit4.class)
public class RegComuAndUserAndUserComuAcTest {

    @Rule
    public ActivityTestRule<RegComuAndUserAndUserComuAc> mActivityRule = new ActivityTestRule<>(RegComuAndUserAndUserComuAc.class);

    RegComuAndUserAndUserComuAc mActivity;
    Resources resources;

    RegComuFr mRegComuFrg;
    RegUserComuFr mRegUserComuFrg;
    RegUserFr mRegUserFr;

    @Before
    public void setUp() throws Exception
    {
        mActivity = mActivityRule.getActivity();
        resources = mActivity.getResources();
        mRegComuFrg = (RegComuFr) mActivity.getFragmentManager().findFragmentById(R.id.reg_comunidad_frg);
        mRegUserComuFrg = (RegUserComuFr) mActivity.getFragmentManager().findFragmentById(R.id
                .reg_usuariocomunidad_frg);
        mRegUserFr = (RegUserFr) mActivity.getFragmentManager().findFragmentById(R.id.reg_usuario_frg);
    }

    @Test
    public void checkFixture()
    {
        assertThat(mActivity, notNullValue());
        assertThat(resources, notNullValue());
        assertThat(mRegComuFrg, notNullValue());
        assertThat(mRegUserComuFrg, notNullValue());
        assertThat(mRegUserFr, notNullValue());
        onView(withId(R.id.reg_comu_usuario_usuariocomu_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_comunidad_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usuariocomunidad_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usuario_frg)).perform(scrollTo()).check(matches(isDisplayed()));
    }

    @Test
    public void testMakeUsuarioBeanFromView() throws Exception
    {
        onView(withId(R.id.reg_usuario_email_editT)).perform(scrollTo(), typeText("yo@email.com"));
        onView(withId(R.id.reg_usuario_alias_ediT)).perform(scrollTo(), typeText("alias1"));
        onView(withId(R.id.reg_usuario_password_ediT)).perform(scrollTo(), typeText("password1"));
        onView(withId(R.id.reg_usuario_password_confirm_ediT)).perform(scrollTo(), typeText("password1"));
        onView(withId(R.id.reg_usuario_phone_prefix_ediT)).perform(scrollTo(), typeText("001"));
        onView(withId(R.id.reg_usuario_phone_editT)).perform(scrollTo(), typeText("123456789"), closeSoftKeyboard());

        View usuarioRegView = mActivity.findViewById(R.id.reg_usuario_frg);

        UsuarioBean usuarioBean = makeUsuarioBeanFromView(usuarioRegView);
        assertThat(usuarioBean.getUserName(), is("yo@email.com"));
        assertThat(usuarioBean.getAlias(), is("alias1"));
        assertThat(usuarioBean.getPassword(), is("password1"));
        assertThat(usuarioBean.getVerificaPassword(), is("password1"));
        assertThat(usuarioBean.getPrefixTf(), is("001"));
        assertThat(usuarioBean.getNumeroTf(), is("123456789"));

        usuarioBean.validate(resources, new StringBuilder(resources.getText(R.string.error_validation_msg)));
        Usuario usuario = usuarioBean.getUsuario();
        assertThat(usuario.getUserName(), is("yo@email.com"));
        assertThat(usuario.getAlias(), is("alias1"));
        assertThat(usuario.getPassword(), is("password1"));
        assertThat(usuario.getPrefixTf(), is(Short.parseShort("001")));
        assertThat(usuario.getNumeroTf(), is(Integer.parseInt("123456789")));
    }

    @Test
    public void testValidateUserFullDataOk()
    {

        onView(withId(R.id.tipo_via_spinner)).perform(click());
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

        onView(withId(R.id.comunidad_nombre_via_editT)).perform(scrollTo(), typeText("nombre via One"));
        onView(withId(R.id.comunidad_numero_editT)).perform(scrollTo(), typeText("123"));
        onView(withId(R.id.comunidad_sufijo_numero_editT)).perform(scrollTo(), typeText("Tris"));

        ComunidadBean comunidadBean = mRegComuFrg.getComunidadBean();

        makeComunidadBeanFromView(mRegComuFrg.getFragmentView(), comunidadBean);

        onView(withId(R.id.reg_usuariocomunidad_portal_editT)).perform(scrollTo(), typeText("port2"));
        onView(withId(R.id.reg_usuariocomunidad_escalera_editT)).perform(scrollTo(), typeText("escale_b"));
        onView(withId(R.id.reg_usuariocomunidad_planta_editT)).perform(scrollTo(), typeText("planta-N"));
        onView(withId(R.id.reg_usuariocomunidad_puerta_editT)).perform(scrollTo(), typeText("puerta5"));
        onView(withId(R.id.reg_usuariocomunidad_roles_checbox_presi)).perform(scrollTo(), click());
        onView(withId(R.id.reg_usuariocomunidad_roles_checbox_inquilino)).perform(scrollTo(), click());

        onView(withId(R.id.reg_usuario_email_editT)).perform(scrollTo(), typeText("yo@email.com"));
        onView(withId(R.id.reg_usuario_alias_ediT)).perform(scrollTo(), typeText("alias1"));
        onView(withId(R.id.reg_usuario_password_ediT)).perform(scrollTo(), typeText("password1"));
        onView(withId(R.id.reg_usuario_password_confirm_ediT)).perform(scrollTo(), typeText("password1"));
        onView(withId(R.id.reg_usuario_phone_prefix_ediT)).perform(scrollTo(), typeText("001"));
        onView(withId(R.id.reg_usuario_phone_editT)).perform(scrollTo(), typeText("123456789"), closeSoftKeyboard());

        UsuarioBean usuarioBean = makeUsuarioBeanFromView(mRegUserFr.getFragmentView());
        UsuarioComunidadBean usuarioComunidadBean = makeUsuarioComunidadBeanFromView
                (mRegUserComuFrg.getFragmentView(), comunidadBean, usuarioBean);

        StringBuilder errors = new StringBuilder("");
        assertThat(usuarioComunidadBean.validate(resources, errors), is(true));
        assertThat(errors.toString().trim().length(), is(0));

        UsuarioComunidad usuarioComunidad = usuarioComunidadBean.getUsuarioComunidad();
        assertThat(usuarioComunidad, notNullValue());
        assertThat(usuarioComunidad.getPortal(), is("port2"));
        assertThat(usuarioComunidad.getEscalera(), is("escale_b"));
        assertThat(usuarioComunidad.getPlanta(), is("planta-N"));
        assertThat(usuarioComunidad.getPuerta(), is("puerta5"));
        assertThat(usuarioComunidad.getRoles(), is("pre,inq"));

        Comunidad comunidad = usuarioComunidad.getComunidad();
        assertThat(comunidad, notNullValue());
        assertThat(comunidad.getTipoVia(), is("Callejon"));
        assertThat(comunidad.getMunicipio().getProvincia().getProvinciaId(), is((short) 12));
        assertThat(comunidad.getMunicipio().getCodInProvincia(), is((short) 53));
        assertThat(comunidad.getNombreVia(), is("nombre via One"));
        assertThat(comunidad.getNumero(), is((short) 123));
        assertThat(comunidad.getSufijoNumero(), is("Tris"));

        Usuario usuario = usuarioComunidad.getUsuario();
        assertThat(usuario, notNullValue());
        assertThat(usuario.getUserName(), is("yo@email.com"));
        assertThat(usuario.getAlias(), is("alias1"));
        assertThat(usuario.getPassword(), is("password1"));
        assertThat(usuario.getPrefixTf(), is((short) 1));
        assertThat(usuario.getNumeroTf(), is(123456789));
    }

    @Test
    public void testFormEmpty()
    {
        ComunidadBean comunidadBean = mRegComuFrg.getComunidadBean();
        assertThat(comunidadBean, notNullValue());
        makeComunidadBeanFromView(mRegComuFrg.getFragmentView(), comunidadBean);

        StringBuilder errors = new StringBuilder(resources.getString(R.string.error_validation_msg));
        assertThat(comunidadBean.getComunidad(), notNullValue());

        UsuarioBean usuarioBean = makeUsuarioBeanFromView(mRegUserFr.getFragmentView());
        assertThat(usuarioBean.getUsuario(), notNullValue());

        UsuarioComunidadBean usuarioComunidadBean =
                makeUsuarioComunidadBeanFromView(mRegUserComuFrg.getFragmentView(),
                        comunidadBean, usuarioBean);
        assertThat(usuarioComunidadBean.validate(resources, errors), is(false));
        assertThat(usuarioComunidadBean.getUsuarioComunidad(), notNullValue());
        assertThat(usuarioComunidadBean.getUsuarioComunidad().getComunidad(), notNullValue());
        assertThat(usuarioComunidadBean.getUsuarioComunidad().getUsuario(), notNullValue());

        onView(withId(R.id.reg_com_usuario_usuariocomu_button)).perform(scrollTo(), click());

        ViewInteraction toastViewInteraction = onView(withText(
                containsString(resources.getText(R.string.error_validation_msg).toString())
        ));

        toastViewInteraction.inRoot(withDecorView(not(mActivity.getWindow().getDecorView())))
                .check(matches(isDisplayed()))
                .check(matches(withText(containsString(resources.getText(R.string.tipo_via).toString()))))
                .check(matches(withText(containsString(resources.getText(R.string.nombre_via).toString()))))
                .check(matches(withText(containsString(resources.getText(R.string.municipio).toString()))))
                .check(matches(withText(containsString(resources.getText(R.string.comunidad_role).toString()))))
                .check(matches(withText(containsString(resources.getText(R.string.alias).toString()))))
                .check(matches(withText(containsString(resources.getText(R.string.email_hint).toString()))))
                .check(matches(withText(containsString(resources.getText(R.string.telefono_prefix_rotulo).toString()))))
                .check(matches(withText(containsString(resources.getText(R.string.telefono_numero).toString()))))
                .check(matches(withText(containsString(resources.getText(R.string.password).toString()))));
    }

    @Test
    public void testFormOk()
    {

        onView(withId(R.id.tipo_via_spinner)).perform(click());
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

        onView(withId(R.id.comunidad_nombre_via_editT)).perform(scrollTo(), typeText("nombre via One"));
        onView(withId(R.id.comunidad_numero_editT)).perform(scrollTo(), typeText("123"));
        onView(withId(R.id.comunidad_sufijo_numero_editT)).perform(scrollTo(), typeText("Tris"));

        onView(withId(R.id.reg_usuariocomunidad_portal_editT)).perform(scrollTo(), typeText("port2"));
        onView(withId(R.id.reg_usuariocomunidad_escalera_editT)).perform(scrollTo(), typeText("escale_b"));
        onView(withId(R.id.reg_usuariocomunidad_planta_editT)).perform(scrollTo(), typeText("planta-N"));
        onView(withId(R.id.reg_usuariocomunidad_puerta_editT)).perform(scrollTo(), typeText("puerta5"));
        onView(withId(R.id.reg_usuariocomunidad_roles_checbox_presi)).perform(scrollTo(), click());
        onView(withId(R.id.reg_usuariocomunidad_roles_checbox_inquilino)).perform(scrollTo(), click());

        onView(withId(R.id.reg_usuario_email_editT)).perform(scrollTo(), typeText("yo@email.com"));
        onView(withId(R.id.reg_usuario_alias_ediT)).perform(scrollTo(), typeText("alias1"));
        onView(withId(R.id.reg_usuario_password_ediT)).perform(scrollTo(), typeText("password1"));
        onView(withId(R.id.reg_usuario_password_confirm_ediT)).perform(scrollTo(), typeText("password1"));
        onView(withId(R.id.reg_usuario_phone_prefix_ediT)).perform(scrollTo(), typeText("001"));
        onView(withId(R.id.reg_usuario_phone_editT)).perform(scrollTo(), typeText("123456789"), closeSoftKeyboard());

        onView(withId(R.id.reg_com_usuario_usuariocomu_button)).perform(scrollTo(), click());
        onView(withId(R.id.usuario_datos_layout)).check(matches(isDisplayed()));
    }
}