package com.didekindroid.usuario.activity;

import android.content.res.Resources;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import com.didekin.serviceone.domain.Comunidad;
import com.didekin.serviceone.domain.Usuario;
import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.usuario.activity.utils.CleanEnum;
import com.didekindroid.usuario.activity.utils.UsuarioTestUtils;
import com.didekindroid.usuario.dominio.ComunidadBean;
import com.didekindroid.usuario.dominio.UsuarioBean;
import com.didekindroid.usuario.dominio.UsuarioComunidadBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.CursorMatchers.withRowString;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.uiutils.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.*;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.cleanOptions;
import static com.didekindroid.usuario.dominio.DomainDataUtils.*;
import static com.didekindroid.usuario.security.TokenHandler.TKhandler;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
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
    CleanEnum whatToClean;

    RegComuFr mRegComuFrg;
    RegUserComuFr mRegUserComuFrg;
    RegUserFr mRegUserFr;

    @Before
    public void setUp() throws Exception
    {
        whatToClean = CleanEnum.CLEAN_NOTHING;

        mActivity = mActivityRule.getActivity();
        resources = mActivity.getResources();
        mRegComuFrg = (RegComuFr) mActivity.getFragmentManager().findFragmentById(R.id.reg_comunidad_frg);
        mRegUserComuFrg = (RegUserComuFr) mActivity.getFragmentManager().findFragmentById(R.id
                .reg_usuariocomunidad_frg);
        mRegUserFr = (RegUserFr) mActivity.getFragmentManager().findFragmentById(R.id.reg_user_frg);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(whatToClean);
    }

    @Test
    public void checkFixture()
    {
        assertThat(mActivity, notNullValue());
        assertThat(resources, notNullValue());
        assertThat(mRegComuFrg, notNullValue());
        assertThat(mRegUserComuFrg, notNullValue());
        assertThat(mRegUserFr, notNullValue());

        assertThat(isRegisteredUser(mActivity), is(false));

        onView(withId(R.id.reg_comu_usuario_usuariocomu_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_comunidad_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usuariocomunidad_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_user_frg)).perform(scrollTo()).check(matches(isDisplayed()));
    }

    @Test
    public void testMakeUsuarioBeanFromView() throws Exception
    {
        // Data for Usuario.
        onView(withId(R.id.reg_usuario_email_editT)).perform(scrollTo(), typeText("yo@email.com"));
        onView(withId(R.id.reg_usuario_alias_ediT)).perform(scrollTo(), typeText("alias1"));
        onView(withId(R.id.reg_usuario_password_ediT)).perform(scrollTo(), typeText("password1"));
        onView(withId(R.id.reg_usuario_password_confirm_ediT)).perform(scrollTo(), typeText("password1"));
        onView(withId(R.id.reg_usuario_phone_prefix_ediT)).perform(scrollTo(), typeText("001"));
        onView(withId(R.id.reg_usuario_phone_editT)).perform(scrollTo(), typeText("123456789"), closeSoftKeyboard());

        View usuarioRegView = mActivity.findViewById(R.id.reg_user_frg);

        // Test assertions about UsuarioBean.
        UsuarioBean usuarioBean = makeUsuarioBeanFromView(usuarioRegView);
        assertThat(usuarioBean.getUserName(), is("yo@email.com"));
        assertThat(usuarioBean.getAlias(), is("alias1"));
        assertThat(usuarioBean.getPassword(), is("password1"));
        assertThat(usuarioBean.getVerificaPassword(), is("password1"));
        assertThat(usuarioBean.getPrefixTf(), is("001"));
        assertThat(usuarioBean.getNumeroTf(), is("123456789"));

        // Test assertions about Usuario.
        usuarioBean.validate(resources, new StringBuilder(resources.getText(R.string.error_validation_msg)));
        Usuario usuario = usuarioBean.getUsuario();
        assertThat(usuario.getUserName(), is("yo@email.com"));
        assertThat(usuario.getAlias(), is("alias1"));
        assertThat(usuario.getPassword(), is("password1"));
        assertThat(usuario.getPrefixTf(), is(Short.parseShort("001")));
        assertThat(usuario.getNumeroTf(), is(Integer.parseInt("123456789")));
    }

    @Test
    public void testMakeUsuarioComunidadBeanFromView_1()
    {

        // Data for ComunidadBean.
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

        // Make ComunidadBean.
        ComunidadBean comunidadBean = mRegComuFrg.getComunidadBean();
        makeComunidadBeanFromView(mRegComuFrg.getFragmentView(), comunidadBean);

        // Data for UsuarioComunidadBean.
        onView(withId(R.id.reg_usercomu_portal_ed)).perform(scrollTo(), typeText("port2"));
        onView(withId(R.id.reg_usercomu_escalera_ed)).perform(scrollTo(), typeText("escale_b"));
        onView(withId(R.id.reg_usercomu_planta_ed)).perform(scrollTo(), typeText("planta-N"));
        onView(withId(R.id.reg_usercomu_puerta_ed)).perform(scrollTo(), typeText("puerta5"));
        onView(withId(R.id.reg_usercomu_checbox_pre)).perform(scrollTo(), click());
        onView(withId(R.id.reg_usercomu_checbox_inq)).perform(scrollTo(), click());

        // Data for UsuarioBean.
        onView(withId(R.id.reg_usuario_email_editT)).perform(scrollTo(), typeText("yo@email.com"));
        onView(withId(R.id.reg_usuario_alias_ediT)).perform(scrollTo(), typeText("alias1"));
        onView(withId(R.id.reg_usuario_password_ediT)).perform(scrollTo(), typeText("password1"));
        onView(withId(R.id.reg_usuario_password_confirm_ediT)).perform(scrollTo(), typeText("password1"));
        onView(withId(R.id.reg_usuario_phone_prefix_ediT)).perform(scrollTo(), typeText("001"));
        onView(withId(R.id.reg_usuario_phone_editT)).perform(scrollTo(), typeText("123456789"), closeSoftKeyboard());

        // Make UsuarioBean.
        UsuarioBean usuarioBean = makeUsuarioBeanFromView(mRegUserFr.getFragmentView());
        // Make UsuarioComunidadBean.
        UsuarioComunidadBean usuarioComunidadBean =
                makeUsuarioComunidadBeanFromView(mRegUserComuFrg.getFragmentView(), comunidadBean, usuarioBean);

        // Validate UsuarioComunidadBean.
        StringBuilder errors = new StringBuilder("");
        assertThat(usuarioComunidadBean.validate(resources, errors), is(true));
        assertThat(errors.toString().trim().length(), is(0));

        // Test assertions.
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
    public void testRegisterComuAndUserComuAndUser_1()
    {
        // Empty ComunidadBean: no input data. ComunidadBean is not null.
        ComunidadBean comunidadBean = mRegComuFrg.getComunidadBean();
        assertThat(comunidadBean, notNullValue());

        // Make ComunidadBean: Comunidad is null.
        makeComunidadBeanFromView(mRegComuFrg.getFragmentView(), comunidadBean);
        StringBuilder errors = new StringBuilder(resources.getString(R.string.error_validation_msg));
        assertThat(comunidadBean.getComunidad(), nullValue());

        // Empty UsuarioBean: no input data. UsuarioBean is not null. Usuario is null.
        UsuarioBean usuarioBean = makeUsuarioBeanFromView(mRegUserFr.getFragmentView());
        assertThat(usuarioBean, notNullValue());
        assertThat(usuarioBean.getUsuario(), nullValue());

        // Empty UsuarioComunidadBean: UsuarioBean not null. Usuario, Comunidad and UsuarioComunidad null.
        UsuarioComunidadBean usuarioComunidadBean =
                makeUsuarioComunidadBeanFromView(mRegUserComuFrg.getFragmentView(), comunidadBean, usuarioBean);
        assertThat(usuarioComunidadBean.validate(resources, errors), is(false));
        assertThat(usuarioComunidadBean, notNullValue());
        assertThat(usuarioComunidadBean.getUsuarioComunidad(), nullValue());

        onView(withId(R.id.reg_com_usuario_usuariocomu_button)).perform(scrollTo(), click());

        UsuarioTestUtils.checkToastInTest(R.string.error_validation_msg, mActivity,
                R.string.tipo_via,
                R.string.nombre_via,
                R.string.municipio,
                R.string.reg_usercomu_role_rot,
                R.string.alias,
                R.string.email_hint,
                R.string.telefono_prefix_rotulo,
                R.string.telefono_numero,
                R.string.password);
    }

    @Test
    public void testRegisterComuAndUserComuAndUser_2()
    {
        whatToClean = CleanEnum.CLEAN_JUAN_with_TF;

        // Comunidad data.
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

        // UsuarioComunidad.
        onView(withId(R.id.reg_usercomu_portal_ed)).perform(scrollTo(), typeText("port2"));
        onView(withId(R.id.reg_usercomu_escalera_ed)).perform(scrollTo(), typeText("escale_b"));
        onView(withId(R.id.reg_usercomu_planta_ed)).perform(scrollTo(), typeText("planta-N"));
        onView(withId(R.id.reg_usercomu_puerta_ed)).perform(scrollTo(), typeText("puerta5"));
        onView(withId(R.id.reg_usercomu_checbox_pre)).perform(scrollTo(), click());
        onView(withId(R.id.reg_usercomu_checbox_inq)).perform(scrollTo(), click());

        // Usuario.
        onView(withId(R.id.reg_usuario_email_editT)).perform(scrollTo(), typeText(USER_JUAN_with_TF.getUserName()));
        onView(withId(R.id.reg_usuario_alias_ediT)).perform(scrollTo(), typeText(USER_JUAN_with_TF.getAlias()));
        onView(withId(R.id.reg_usuario_password_ediT)).perform(scrollTo(), typeText(USER_JUAN_with_TF.getPassword()));
        onView(withId(R.id.reg_usuario_password_confirm_ediT)).perform(scrollTo(), typeText(USER_JUAN_with_TF.getPassword()));
        onView(withId(R.id.reg_usuario_phone_prefix_ediT)).perform(scrollTo(),
                typeText(String.valueOf(USER_JUAN_with_TF.getPrefixTf())));
        onView(withId(R.id.reg_usuario_phone_editT)).perform(scrollTo(),
                typeText(String.valueOf(USER_JUAN_with_TF.getNumeroTf())),
                closeSoftKeyboard());

        onView(withId(R.id.reg_com_usuario_usuariocomu_button)).perform(scrollTo(), click());
        onView(withId(R.id.see_usercomu_by_user_ac_layout)).check(matches(isDisplayed()));
        assertThat(TKhandler.getAccessTokenInCache(), notNullValue());
        assertThat(TKhandler.getRefreshTokenKey(), is(TKhandler.getAccessTokenInCache().getRefreshToken().getValue()));
        assertThat(isRegisteredUser(mActivity), is(true));
    }
}