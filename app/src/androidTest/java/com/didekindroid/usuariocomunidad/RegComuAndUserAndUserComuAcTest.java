package com.didekindroid.usuariocomunidad;

import android.content.Intent;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.comunidad.RegComuFr;
import com.didekindroid.comunidad.spinner.TipoViaValueObj;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.RegUserFr;
import com.didekindroid.usuario.testutil.UsuarioDataTestUtils;
import com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil;
import com.didekinlib.model.comunidad.ComunidadAutonoma;
import com.didekinlib.model.comunidad.Municipio;
import com.didekinlib.model.comunidad.Provincia;

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
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.typeComunidadData;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.usuario.testutil.UserEspressoTestUtil.typeUserDataFull;
import static com.didekindroid.usuario.testutil.UserItemMenuTestUtils.LOGIN_AC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_NOTHING;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_TK_HANDLER;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.RolUi.INQ;
import static com.didekindroid.usuariocomunidad.RolUi.PRE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.typeUserComuData;
import static org.hamcrest.CoreMatchers.is;
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
        Thread.sleep(2000);
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
        onView(ViewMatchers.withId(R.id.reg_usercomu_frg)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.reg_user_frg)).perform(scrollTo()).check(matches(isDisplayed()));

        onView(ViewMatchers.withId(R.id.appbar)).perform(scrollTo()).check(matches(isDisplayed()));
        clickNavigateUp();
    }

    @Test
    public void testMakeUsuarioComunidadBeanFromView_OK() throws InterruptedException
    {
        initActivityAndFragments();

        // Data for ComunidadBean.
        helpTypeComunidadData();
        // Data for UsuarioComunidadBean.
        Thread.sleep(1000);
        typeUserComuData("port2", "escale_b", "planta-N", "puerta5", PRE, INQ);
        // Data for UsuarioBean.
        Thread.sleep(1000);
        typeUserDataFull("yo@email.com", "alias1", "password1", "password1");

        // Make ComunidadBean.
        /*ComunidadBean comunidadBean = regComuFr.getComunidadBean();
        getComunidadFromViewer(regComuFr.getFragmentView(), comunidadBean);
        // Make UsuarioBean.
        UsuarioBean usuarioBean = getUserFromViewer(regUserFr.getFragmentView());
        // Make UsuarioComunidadBean.
        UsuarioComunidadBean usuarioComunidadBean =
                getUserComuFromViewer(regUserComuFr.getFragmentView(), comunidadBean, usuarioBean);

        // Validate UsuarioComunidadBean.
        StringBuilder errors = new StringBuilder("");
        assertThat(usuarioComunidadBean.validate(resources, errors), is(true));
        assertThat(errors.toString().trim().length(), is(0));

        // Test assertions.
        UsuarioComunidad usuarioComunidad = usuarioComunidadBean.getUsuarioComunidad();
        ComuEspresoTestUtil.validaTypedComunidad(usuarioComunidad.getComunidad(), "Callejon", (short) 12, (short) 53, "nombre via One", (short) 123, "Tris");
        validaTypedUserData(usuarioComunidad.getUsuario(), "yo@email.com", "alias1", "password1");
        validaTypedUsuarioComunidad(usuarioComunidad, "port2", "escale_b", "planta-N", "puerta5", "pre,inq");*/
    }

    @Test
    public void testRegComuAndUserComuAndUser_NotOk() throws InterruptedException
    {
        initActivityAndFragments();

        // Empty ComunidadBean: no input data. ComunidadBean is not null.
        /*ComunidadBean comunidadBean = regComuFr.getComunidadBean();
        assertThat(comunidadBean, notNullValue());

        // Make ComunidadBean: Comunidad is null.
        getComunidadFromViewer(regComuFr.getFragmentView(), comunidadBean);
        StringBuilder errors = new StringBuilder(resources.getString(R.string.error_validation_msg));
        assertThat(comunidadBean.getComunidad(), nullValue());

        // Empty UsuarioBean: no input data. UsuarioBean is not null. Usuario is null.
        UsuarioBean usuarioBean = getUserFromViewer(regUserFr.getFragmentView());
        assertThat(usuarioBean, notNullValue());
        assertThat(usuarioBean.getUsuario(), nullValue());

        // Empty UsuarioComunidadBean: UsuarioBean not null. Usuario, Comunidad and UsuarioComunidad null.
        UsuarioComunidadBean usuarioComunidadBean =
                getUserComuFromViewer(regUserComuFr.getFragmentView(), comunidadBean, usuarioBean);
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
                R.string.password);*/    // TODO: modificar y descomentar.
    }

    @Test
    public void testRegComuAndUserComuAndUser_OK() throws UiException, InterruptedException
    {
        mActivity = mActivityRule.launchActivity(new Intent());

        // Comunidad data.
        helpTypeComunidadData();
        // Data for UsuarioComunidadBean.
        Thread.sleep(1000);
        typeUserComuData("port2", "escale_b", "planta-N", "puerta5", PRE, INQ);
        // Usuario.
        typeUserDataFull(
                UsuarioDataTestUtils.USER_JUAN2.getUserName(),
                UsuarioDataTestUtils.USER_JUAN2.getAlias(),
                UsuarioDataTestUtils.USER_JUAN2.getPassword(),
                UsuarioDataTestUtils.USER_JUAN2.getPassword());

        onView(ViewMatchers.withId(R.id.reg_com_usuario_usuariocomu_button)).perform(scrollTo(), click());

        onView(ViewMatchers.withId(R.id.see_usercomu_by_user_frg)).check(matches(isDisplayed()));
        checkUp(activiyLayoutId);

        assertThat(TKhandler.getTokenCache().get(), notNullValue());
        assertThat(TKhandler.getRefreshTokenValue(), is(TKhandler.getTokenCache().get().getRefreshToken().getValue()));
        assertThat(TKhandler.isRegisteredUser(), is(true));

        whatToClean = UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN2;

    }

    //    =================================== MENU ===================================

    @Test
    public void testLoginMn_NoToken() throws InterruptedException, UiException
    {
        mActivity = mActivityRule.launchActivity(new Intent());
        assertThat(TKhandler.isRegisteredUser(), is(false));
        assertThat(TKhandler.getTokenCache().get(), nullValue());

        LOGIN_AC.checkMenuItem_NTk(mActivity);
        checkUp(activiyLayoutId);
    }

    @Test
    public void testLoginMn_WithToken() throws InterruptedException, UiException, IOException
    {
        whatToClean = CLEAN_JUAN;
        //With token.
        signUpAndUpdateTk(UserComuDataTestUtil.COMU_REAL_JUAN);

        mActivity = mActivityRule.launchActivity(new Intent());
        assertThat(TKhandler.isRegisteredUser(), is(true));
        assertThat(TKhandler.getTokenCache().get(), notNullValue());

        LOGIN_AC.checkMenuItem_WTk(mActivity);
        checkUp(activiyLayoutId);
    }

//    ================================================================================

    private void initActivityAndFragments()
    {
        mActivity = mActivityRule.launchActivity(new Intent());
        mRegComuFrg = (RegComuFr) mActivity.getSupportFragmentManager().findFragmentById(R.id.reg_comunidad_frg);
        mRegUserComuFrg = (RegUserComuFr) mActivity.getSupportFragmentManager().findFragmentById(R.id.reg_usercomu_frg);
        mRegUserFr = (RegUserFr) mActivity.getSupportFragmentManager().findFragmentById(R.id.reg_user_frg);
    }

    private void helpTypeComunidadData()
    {
        typeComunidadData(new ComunidadAutonoma((short) 10, "Valencia"),
                new Provincia((short) 12, "Castellón/Castelló"),
                new Municipio((short) 53, "Chilches/Xilxes", new Provincia((short) 12, "Castellón/Castelló")),
                new TipoViaValueObj(54, "Callejon"),
                "nombre via One", "123", "Tris");
    }
}