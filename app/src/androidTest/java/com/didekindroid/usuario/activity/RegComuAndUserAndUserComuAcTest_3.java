package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
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

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeComunidadBeanFromView;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeUserBeanFromRegUserFrView;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeUserComuBeanFromView;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_NOTHING;
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
public class RegComuAndUserAndUserComuAcTest_3 {

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
        Thread.sleep(4000);
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
}