package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.didekin.usuario.dominio.Usuario;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.usuario.activity.utils.RolUi;
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

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.common.activity.TokenHandler.TKhandler;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkUp;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeComunidadBeanFromView;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeUserBeanFromRegUserFrView;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeUserComuBeanFromView;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_NOTHING;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_TK_HANDLER;
import static com.didekindroid.usuario.testutils.UserMenuTestUtils.LOGIN_AC;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_REAL_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.USER_JUAN2;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.typeComunidadData;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.typeUserComuData;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.typeUserData;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.validaTypedComunidad;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.validaTypedUsuario;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.validaTypedUsuarioComunidad;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

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
    CleanUserEnum whatToClean = CLEAN_NOTHING;

    RegComuFr mRegComuFrg;
    RegUserComuFr mRegUserComuFrg;
    RegUserFr mRegUserFr;

    int activiyLayoutId = R.id.reg_comu_usuario_usuariocomu_layout;

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(5000);
    }

    @Before
    public void setUp() throws Exception
    {
        cleanOptions(CLEAN_TK_HANDLER);
        resources = InstrumentationRegistry.getTargetContext().getResources();
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(whatToClean);
        Thread.sleep(2000);
    }

    //    ================================================================================

    @Test
    public void checkFixture()
    {
        initActivityAndFragments();

        assertThat(mActivity, notNullValue());
        assertThat(mRegComuFrg, notNullValue());
        assertThat(mRegUserComuFrg, notNullValue());
        assertThat(mRegUserFr, notNullValue());

        assertThat(isRegisteredUser(mActivity), is(false));

        onView(withId(R.id.reg_comu_usuario_usuariocomu_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_comunidad_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.tipo_via_spinner)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usercomu_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_user_frg)).perform(scrollTo()).check(matches(isDisplayed()));

        onView(withId(R.id.appbar)).perform(scrollTo()).check(matches(isDisplayed()));
        clickNavigateUp();
    }

    @Test
    public void testMakeUsuarioBeanFromView_OK() throws Exception
    {
        mActivity = mActivityRule.launchActivity(new Intent());
        // Data for Usuario.
        typeUserData("yo@email.com", "alias1", "password1", "password1");

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
        validaTypedUsuario(usuario, "yo@email.com", "alias1", "password1");
    }

    @Test
    public void testMakeUsuarioComunidadBeanFromView_OK() throws InterruptedException
    {
        initActivityAndFragments();

        // Data for ComunidadBean.
        typeComunidadData("Callejon", "Valencia", "Castellón/Castelló", "Chilches/Xilxes", "nombre via One", "123", "Tris");
        // Data for UsuarioComunidadBean.
        Thread.sleep(1000);
        typeUserComuData("port2", "escale_b", "planta-N", "puerta5", RolUi.PRE, RolUi.INQ);
        // Data for UsuarioBean.
        Thread.sleep(1000);
        typeUserData("yo@email.com", "alias1", "password1", "password1");

        // Make ComunidadBean.
        ComunidadBean comunidadBean = mRegComuFrg.getComunidadBean();
        makeComunidadBeanFromView(mRegComuFrg.getFragmentView(), comunidadBean);
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
        validaTypedComunidad(usuarioComunidad.getComunidad(), "Callejon", (short) 12, (short) 53, "nombre via One", (short) 123, "Tris");
        validaTypedUsuario(usuarioComunidad.getUsuario(), "yo@email.com", "alias1", "password1");
        validaTypedUsuarioComunidad(usuarioComunidad, "port2", "escale_b", "planta-N", "puerta5", "pre,inq");
    }

    @Test
    public void testRegComuAndUserComuAndUser_NotOk() throws InterruptedException
    {
        initActivityAndFragments();

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
    public void testRegComuAndUserComuAndUser_OK() throws UiException, InterruptedException
    {
        mActivity = mActivityRule.launchActivity(new Intent());

        // Comunidad data.
        typeComunidadData("Callejon", "Valencia", "Castellón/Castelló", "Chilches/Xilxes", "nombre via One", "123", "Tris");
        // Data for UsuarioComunidadBean.
        Thread.sleep(1000);
        typeUserComuData("port2", "escale_b", "planta-N", "puerta5", RolUi.PRE, RolUi.INQ);
        // Usuario.
        typeUserData(USER_JUAN2.getUserName(), USER_JUAN2.getAlias(), USER_JUAN2.getPassword(), USER_JUAN2.getPassword());

        onView(withId(R.id.reg_com_usuario_usuariocomu_button)).perform(scrollTo(), click());

        onView(withId(R.id.see_usercomu_by_user_frg)).check(matches(isDisplayed()));
        checkUp(activiyLayoutId);

        assertThat(TKhandler.getAccessTokenInCache(), notNullValue());
        assertThat(TKhandler.getRefreshTokenKey(), is(TKhandler.getAccessTokenInCache().getRefreshToken().getValue()));
        assertThat(isRegisteredUser(mActivity), is(true));

        whatToClean = CleanUserEnum.CLEAN_JUAN2;

    }

    //    =================================== MENU ===================================

    @Test
    public void testLoginMn_NoToken() throws InterruptedException, UiException
    {
        mActivity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(mActivity), is(false));
        assertThat(TKhandler.getAccessTokenInCache(), nullValue());

        LOGIN_AC.checkMenuItem_NTk(mActivity);
        checkUp(activiyLayoutId);
    }

    @Test
    public void testLoginMn_WithToken() throws InterruptedException, UiException, IOException
    {
        whatToClean = CleanUserEnum.CLEAN_JUAN;
        //With token.
        signUpAndUpdateTk(COMU_REAL_JUAN);

        mActivity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(mActivity), is(true));
        assertThat(TKhandler.getAccessTokenInCache(), not(nullValue()));

        LOGIN_AC.checkMenuItem_WTk(mActivity);
        checkUp(activiyLayoutId);
    }

//    ================================================================================

    private void initActivityAndFragments()
    {
        mActivity = mActivityRule.launchActivity(new Intent());
        mRegComuFrg = (RegComuFr) mActivity.getFragmentManager().findFragmentById(R.id.reg_comunidad_frg);
        mRegUserComuFrg = (RegUserComuFr) mActivity.getFragmentManager().findFragmentById(R.id
                .reg_usercomu_frg);
        mRegUserFr = (RegUserFr) mActivity.getFragmentManager().findFragmentById(R.id.reg_user_frg);
    }
}