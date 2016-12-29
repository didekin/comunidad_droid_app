package com.didekindroid.usuariocomunidad;

import android.content.Intent;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.didekin.usuario.Usuario;
import com.didekin.usuariocomunidad.UsuarioComunidad;

import com.didekinaar.exception.UiException;
import com.didekinaar.usuario.RegUserFr;
import com.didekinaar.usuario.UsuarioBean;
import com.didekinaar.usuario.testutil.UserEspressoTestUtil;
import com.didekinaar.usuario.testutil.UserItemMenuTestUtils;
import com.didekinaar.usuario.testutil.UsuarioDataTestUtils;
import com.didekindroid.R;
import com.didekindroid.comunidad.ComunidadBean;
import com.didekindroid.comunidad.RegComuFr;
import com.didekindroid.comunidad.testutil.ComuEspresoTestUtil;
import com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil;
import com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil;

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
import static com.didekinaar.security.TokenIdentityCacher.TKhandler;
import static com.didekinaar.testutil.AarActivityTestUtils.checkToastInTest;
import static com.didekinaar.testutil.AarActivityTestUtils.checkUp;
import static com.didekinaar.testutil.AarActivityTestUtils.clickNavigateUp;
import static com.didekinaar.usuario.RegUserFr.makeUserBeanFromRegUserFrView;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_NOTHING;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_TK_HANDLER;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.comunidad.RegComuFr.makeComunidadBeanFromView;
import static com.didekindroid.usuariocomunidad.RegUserComuFr.makeUserComuBeanFromView;
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
    UsuarioDataTestUtils.CleanUserEnum whatToClean = CLEAN_NOTHING;

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

        assertThat(TKhandler.isRegisteredUser(), is(false));

        onView(ViewMatchers.withId(R.id.reg_comu_usuario_usuariocomu_layout)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.reg_comunidad_frg)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.tipo_via_spinner)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.reg_usercomu_frg)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.reg_user_frg)).perform(scrollTo()).check(matches(isDisplayed()));

        onView(ViewMatchers.withId(R.id.appbar)).perform(scrollTo()).check(matches(isDisplayed()));
        clickNavigateUp();
    }

    @Test
    public void testMakeUsuarioBeanFromView_OK() throws Exception
    {
        mActivity = mActivityRule.launchActivity(new Intent());
        // Data for Usuario.
        UserEspressoTestUtil.typeUserData("yo@email.com", "alias1", "password1", "password1");

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
        UserEspressoTestUtil.validaTypedUsuario(usuario, "yo@email.com", "alias1", "password1");
    }

    @Test
    public void testMakeUsuarioComunidadBeanFromView_OK() throws InterruptedException
    {
        initActivityAndFragments();

        // Data for ComunidadBean.
        ComuEspresoTestUtil.typeComunidadData("Callejon", "Valencia", "Castellón/Castelló", "Chilches/Xilxes", "nombre via One", "123", "Tris");
        // Data for UsuarioComunidadBean.
        Thread.sleep(1000);
        UserComuEspressoTestUtil.typeUserComuData("port2", "escale_b", "planta-N", "puerta5", RolUi.PRE, RolUi.INQ);
        // Data for UsuarioBean.
        Thread.sleep(1000);
        UserEspressoTestUtil.typeUserData("yo@email.com", "alias1", "password1", "password1");

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
        ComuEspresoTestUtil.validaTypedComunidad(usuarioComunidad.getComunidad(), "Callejon", (short) 12, (short) 53, "nombre via One", (short) 123, "Tris");
        UserEspressoTestUtil.validaTypedUsuario(usuarioComunidad.getUsuario(), "yo@email.com", "alias1", "password1");
        UserComuEspressoTestUtil.validaTypedUsuarioComunidad(usuarioComunidad, "port2", "escale_b", "planta-N", "puerta5", "pre,inq");
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

        onView(ViewMatchers.withId(R.id.reg_com_usuario_usuariocomu_button)).perform(scrollTo(), click());

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
        ComuEspresoTestUtil.typeComunidadData("Callejon", "Valencia", "Castellón/Castelló", "Chilches/Xilxes", "nombre via One", "123", "Tris");
        // Data for UsuarioComunidadBean.
        Thread.sleep(1000);
        UserComuEspressoTestUtil.typeUserComuData("port2", "escale_b", "planta-N", "puerta5", RolUi.PRE, RolUi.INQ);
        // Usuario.
        UserEspressoTestUtil.typeUserData(UsuarioDataTestUtils.USER_JUAN2.getUserName(), UsuarioDataTestUtils.USER_JUAN2.getAlias(), UsuarioDataTestUtils.USER_JUAN2.getPassword(), UsuarioDataTestUtils.USER_JUAN2.getPassword());

        onView(ViewMatchers.withId(R.id.reg_com_usuario_usuariocomu_button)).perform(scrollTo(), click());

        onView(ViewMatchers.withId(R.id.see_usercomu_by_user_frg)).check(matches(isDisplayed()));
        checkUp(activiyLayoutId);

        assertThat(TKhandler.getAccessTokenInCache(), notNullValue());
        assertThat(TKhandler.getRefreshTokenValue(), is(TKhandler.getAccessTokenInCache().getRefreshToken().getValue()));
        assertThat(TKhandler.isRegisteredUser(), is(true));

        whatToClean = UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN2;

    }

    //    =================================== MENU ===================================

    @Test
    public void testLoginMn_NoToken() throws InterruptedException, UiException
    {
        mActivity = mActivityRule.launchActivity(new Intent());
        assertThat(TKhandler.isRegisteredUser(), is(false));
        assertThat(TKhandler.getAccessTokenInCache(), nullValue());

        UserItemMenuTestUtils.LOGIN_AC.checkMenuItem_NTk(mActivity);
        checkUp(activiyLayoutId);
    }

    @Test
    public void testLoginMn_WithToken() throws InterruptedException, UiException, IOException
    {
        whatToClean = UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
        //With token.
        UserComuTestUtil.signUpAndUpdateTk(UserComuTestUtil.COMU_REAL_JUAN);

        mActivity = mActivityRule.launchActivity(new Intent());
        assertThat(TKhandler.isRegisteredUser(), is(true));
        assertThat(TKhandler.getAccessTokenInCache(), not(nullValue()));

        UserItemMenuTestUtils.LOGIN_AC.checkMenuItem_WTk(mActivity);
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