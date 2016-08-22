package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.didekin.usuario.dominio.Usuario;
import com.didekindroid.R;
import com.didekindroid.common.testutils.ActivityTestUtils;
import com.didekindroid.usuario.dominio.UsuarioBean;
import com.didekindroid.usuario.testutils.CleanUserEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeUserBeanFromRegUserFrView;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_NOTHING;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_TK_HANDLER;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.typeUserData;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.validaTypedUsuario;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro
 * Date: 07/07/15
 * Time: 10:26
 */
@SuppressWarnings({"unchecked", "ConstantConditions"})
@RunWith(AndroidJUnit4.class)
public class RegComuAndUserAndUserComuAcTest_1 {

    @Rule
    public ActivityTestRule<RegComuAndUserAndUserComuAc> mActivityRule = new ActivityTestRule<>(RegComuAndUserAndUserComuAc.class, true, false);

    RegComuAndUserAndUserComuAc mActivity;
    Resources resources;
    CleanUserEnum whatToClean = CLEAN_NOTHING;

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
        cleanOptions(CLEAN_TK_HANDLER);
        resources = InstrumentationRegistry.getTargetContext().getResources();
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(whatToClean);
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
}