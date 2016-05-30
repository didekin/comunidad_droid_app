package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.didekin.usuario.dominio.Comunidad;
import com.didekin.usuario.dominio.Usuario;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.common.testutils.ActivityTestUtils;
import com.didekindroid.usuario.dominio.ComunidadBean;
import com.didekindroid.usuario.dominio.UsuarioBean;
import com.didekindroid.usuario.dominio.UsuarioComunidadBean;
import com.didekindroid.usuario.testutils.CleanUserEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

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
import static com.didekindroid.common.TokenHandler.TKhandler;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeComunidadBeanFromView;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeUserBeanFromRegUserFrView;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeUserComuBeanFromView;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_NOTHING;
import static com.didekindroid.usuario.testutils.UserMenuTestUtils.LOGIN_AC;
import static com.didekindroid.usuario.testutils.UserMenuTestUtils.REQUIRES_USER_NO_TOKEN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_REAL_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.USER_JUAN2;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro
 * Date: 07/07/15
 * Time: 10:26
 */
@SuppressWarnings({"unchecked", "ConstantConditions"})
@RunWith(AndroidJUnit4.class)
public class RegComuAndUserAndUserComuAcTest {

    @Rule
    public ActivityTestRule<RegComuAndUserAndUserComuAc> mActivityRule = new ActivityTestRule<>(RegComuAndUserAndUserComuAc.class, true, false);

    RegComuAndUserAndUserComuAc mActivity;
    Resources resources;
    CleanUserEnum whatToClean;

    RegComuFr mRegComuFrg;
    RegUserComuFr mRegUserComuFrg;
    RegUserFr mRegUserFr;

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(5000);
    }

    @Before
    public void setUp() throws Exception
    {
        whatToClean = CLEAN_NOTHING;
        resources = InstrumentationRegistry.getTargetContext().getResources();
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(whatToClean);
        Thread.sleep(1000);
    }

    @Test
    public void checkFixture()
    {
        mActivity = mActivityRule.launchActivity(new Intent());
        mRegComuFrg = (RegComuFr) mActivity.getFragmentManager().findFragmentById(R.id.reg_comunidad_frg);
        mRegUserComuFrg = (RegUserComuFr) mActivity.getFragmentManager().findFragmentById(R.id
                .reg_usercomu_frg);
        mRegUserFr = (RegUserFr) mActivity.getFragmentManager().findFragmentById(R.id.reg_user_frg);

        assertThat(mActivity, notNullValue());
        assertThat(resources, notNullValue());
        assertThat(mRegComuFrg, notNullValue());
        assertThat(mRegUserComuFrg, notNullValue());
        assertThat(mRegUserFr, notNullValue());

        assertThat(isRegisteredUser(mActivity), is(false));

        onView(withId(R.id.reg_comu_usuario_usuariocomu_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_comunidad_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.tipo_via_spinner)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usercomu_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_user_frg)).perform(scrollTo()).check(matches(isDisplayed()));

        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        ActivityTestUtils.checkNavigateUp();
    }

    @Test
    public void testMakeUsuarioBeanFromView() throws Exception
    {
        mActivity = mActivityRule.launchActivity(new Intent());
        // Data for Usuario.
        onView(withId(R.id.reg_usuario_email_editT)).perform(scrollTo(), typeText("yo@email.com"));
        onView(withId(R.id.reg_usuario_alias_ediT)).perform(scrollTo(), typeText("alias1"));
        onView(withId(R.id.reg_usuario_password_ediT)).perform(scrollTo(), typeText("password1"));
        onView(withId(R.id.reg_usuario_password_confirm_ediT)).perform(scrollTo(),
                typeText("password1"), closeSoftKeyboard());

        View usuarioRegView = mActivity.findViewById(R.id.reg_user_frg);

        // Test assertions about UsuarioBean.
        UsuarioBean usuarioBean = makeUserBeanFromRegUserFrView(usuarioRegView);
        assertThat(usuarioBean.getUserName(), is("yo@email.com"));
        assertThat(usuarioBean.getAlias(), is("alias1"));
        assertThat(usuarioBean.getPassword(), is("password1"));
        assertThat(usuarioBean.getVerificaPassword(), is("password1"));

        // Test assertions about Usuario.
        usuarioBean.validate(resources, new StringBuilder(resources.getText(R.string.error_validation_msg)));
        Usuario usuario = usuarioBean.getUsuario();
        assertThat(usuario.getUserName(), is("yo@email.com"));
        assertThat(usuario.getAlias(), is("alias1"));
        assertThat(usuario.getPassword(), is("password1"));
    }

    @Test
    public void testMakeUsuarioComunidadBeanFromView_1()
    {
        mActivity = mActivityRule.launchActivity(new Intent());
        // Data for ComunidadBean.
        onView(withId(R.id.tipo_via_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Callejon"))).perform(click());

        onView(withId(R.id.autonoma_comunidad_spinner)).perform(click());
        onData(withRowString(1, "Valencia")).perform(click());

        onView(withId(R.id.provincia_spinner)).perform(click());
        onData(withRowString(1, "Castellón/Castelló")).perform(click());

        onView(withId(R.id.municipio_spinner)).perform(click());
        onData(withRowString(3, "Chilches/Xilxes")).perform(click());


        onView(withId(R.id.comunidad_nombre_via_editT)).perform(typeText("nombre via One"));
        onView(withId(R.id.comunidad_numero_editT)).perform(typeText("123"));
        onView(withId(R.id.comunidad_sufijo_numero_editT)).perform(typeText("Tris"), closeSoftKeyboard());

        mRegComuFrg = (RegComuFr) mActivity.getFragmentManager().findFragmentById(R.id.reg_comunidad_frg);
        mRegUserComuFrg = (RegUserComuFr) mActivity.getFragmentManager().findFragmentById(R.id
                .reg_usercomu_frg);
        mRegUserFr = (RegUserFr) mActivity.getFragmentManager().findFragmentById(R.id.reg_user_frg);

        // Make ComunidadBean.
        ComunidadBean comunidadBean = mRegComuFrg.getComunidadBean();
        makeComunidadBeanFromView(mRegComuFrg.getFragmentView(), comunidadBean);

        // Data for UsuarioComunidadBean.
        onView(withId(R.id.reg_usercomu_portal_ed)).perform(scrollTo(), typeText("port2"));
        onView(withId(R.id.reg_usercomu_escalera_ed)).perform(scrollTo(), typeText("escale_b"));
        onView(withId(R.id.reg_usercomu_planta_ed)).perform(scrollTo(), typeText("planta-N"));
        onView(withId(R.id.reg_usercomu_puerta_ed)).perform(scrollTo(), typeText("puerta5"), closeSoftKeyboard());
        onView(withId(R.id.reg_usercomu_checbox_pre)).perform(scrollTo(), click());
        onView(withId(R.id.reg_usercomu_checbox_inq)).perform(scrollTo(), click());

        // Data for UsuarioBean.
        onView(withId(R.id.reg_usuario_email_editT)).perform(scrollTo(), typeText("yo@email.com"));
        onView(withId(R.id.reg_usuario_alias_ediT)).perform(scrollTo(), typeText("alias1"));
        onView(withId(R.id.reg_usuario_password_ediT)).perform(scrollTo(), typeText("password1"));
        onView(withId(R.id.reg_usuario_password_confirm_ediT)).perform(scrollTo(),
                typeText("password1"), closeSoftKeyboard());

        // Make UsuarioBean.
        UsuarioBean usuarioBean = makeUserBeanFromRegUserFrView(mRegUserFr.getFragmentView());
        // Make UsuarioComunidadBean.
        UsuarioComunidadBean usuarioComunidadBean =
                makeUserComuBeanFromView(mRegUserComuFrg.getFragmentView(), comunidadBean, usuarioBean);

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
    }

    @Test
    public void testRegisterComuAndUserComuAndUser_1() throws InterruptedException
    {
        mActivity = mActivityRule.launchActivity(new Intent());
        mRegComuFrg = (RegComuFr) mActivity.getFragmentManager().findFragmentById(R.id.reg_comunidad_frg);
        mRegUserComuFrg = (RegUserComuFr) mActivity.getFragmentManager().findFragmentById(R.id
                .reg_usercomu_frg);
        mRegUserFr = (RegUserFr) mActivity.getFragmentManager().findFragmentById(R.id.reg_user_frg);

        // Empty ComunidadBean: no input data. ComunidadBean is not null.
        ComunidadBean comunidadBean = mRegComuFrg.getComunidadBean();
        assertThat(comunidadBean, notNullValue());

        // Make ComunidadBean: Comunidad is null.
        makeComunidadBeanFromView(mRegComuFrg.getFragmentView(), comunidadBean);
        StringBuilder errors = new StringBuilder(resources.getString(R.string.error_validation_msg));
        assertThat(comunidadBean.getComunidad(), nullValue());

        // Empty UsuarioBean: no input data. UsuarioBean is not null. Usuario is null.
        UsuarioBean usuarioBean = makeUserBeanFromRegUserFrView(mRegUserFr.getFragmentView());
        assertThat(usuarioBean, notNullValue());
        assertThat(usuarioBean.getUsuario(), nullValue());

        // Empty UsuarioComunidadBean: UsuarioBean not null. Usuario, Comunidad and UsuarioComunidad null.
        UsuarioComunidadBean usuarioComunidadBean =
                makeUserComuBeanFromView(mRegUserComuFrg.getFragmentView(), comunidadBean, usuarioBean);
        assertThat(usuarioComunidadBean.validate(resources, errors), is(false));
        assertThat(usuarioComunidadBean, notNullValue());
        assertThat(usuarioComunidadBean.getUsuarioComunidad(), nullValue());

        onView(withId(R.id.reg_com_usuario_usuariocomu_button)).perform(scrollTo(), click());

        checkToastInTest(R.string.error_validation_msg, mActivity,
                R.string.tipo_via,
                R.string.nombre_via,
                R.string.municipio,
                R.string.reg_usercomu_role_rot,
                R.string.alias,
                R.string.email_hint,
                R.string.password);
    }

    @Test
    public void testRegisterComuAndUserComuAndUser_2() throws UiException
    {
        whatToClean = CleanUserEnum.CLEAN_JUAN2;

        mActivity = mActivityRule.launchActivity(new Intent());

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

        onView(withId(R.id.comunidad_nombre_via_editT)).perform(typeText("nombre via One"));
        onView(withId(R.id.comunidad_numero_editT)).perform(typeText("123"));
        onView(withId(R.id.comunidad_sufijo_numero_editT)).perform(typeText("Tris"), closeSoftKeyboard());

        // UsuarioComunidad.
        onView(withId(R.id.reg_usercomu_portal_ed)).perform(typeText("port2"));
        onView(withId(R.id.reg_usercomu_escalera_ed)).perform(typeText("escale_b"));
        onView(withId(R.id.reg_usercomu_planta_ed)).perform(typeText("planta-N"));
        onView(withId(R.id.reg_usercomu_puerta_ed)).perform(typeText("puerta5"), closeSoftKeyboard());
        onView(withId(R.id.reg_usercomu_checbox_pre)).perform(scrollTo(), click());
        onView(withId(R.id.reg_usercomu_checbox_inq)).perform(scrollTo(), click());

        // Usuario.
        onView(withId(R.id.reg_usuario_email_editT)).perform(scrollTo(), typeText(USER_JUAN2.getUserName()));
        onView(withId(R.id.reg_usuario_alias_ediT)).perform(scrollTo(), typeText(USER_JUAN2.getAlias()));
        onView(withId(R.id.reg_usuario_password_ediT)).perform(scrollTo(), typeText(USER_JUAN2.getPassword()));
        onView(withId(R.id.reg_usuario_password_confirm_ediT)).perform(scrollTo(),
                typeText(USER_JUAN2.getPassword()), closeSoftKeyboard());

        onView(withId(R.id.reg_com_usuario_usuariocomu_button)).perform(scrollTo(), click());
        onView(withId(R.id.see_usercomu_by_user_ac_frg_container)).check(matches(isDisplayed()));
        assertThat(TKhandler.getAccessTokenInCache(), notNullValue());
        assertThat(TKhandler.getRefreshTokenKey(), is(TKhandler.getAccessTokenInCache().getRefreshToken().getValue()));
        assertThat(isRegisteredUser(mActivity), is(true));
    }

    //    =================================== MENU ===================================

    @Test
    public void testLoginMn_1() throws InterruptedException, UiException
    {
        mActivity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(mActivity),is(false));
        assertThat(TKhandler.getAccessTokenInCache(),nullValue());

        LOGIN_AC.checkMenuItem_NTk(mActivity);
    }

    @Test
    public void testLoginMn_2() throws InterruptedException, UiException, IOException
    {
        whatToClean = CleanUserEnum.CLEAN_JUAN;
        //With token.
        signUpAndUpdateTk(COMU_REAL_JUAN);

        mActivity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(mActivity), is(true));
        assertThat(TKhandler.getAccessTokenInCache(),not(nullValue()));

        try {
            LOGIN_AC.checkMenuItem_WTk(mActivity);
            fail();
        } catch (UnsupportedOperationException e) {
            assertThat(e.getMessage(),is(LOGIN_AC.name() + REQUIRES_USER_NO_TOKEN));
        }
    }
}