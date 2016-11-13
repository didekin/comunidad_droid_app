package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.didekin.usuario.dominio.Municipio;
import com.didekin.usuario.dominio.Provincia;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.common.testutils.ActivityTestUtils;
import com.didekindroid.usuario.dominio.ComunidadBean;
import com.didekindroid.usuario.dominio.UsuarioComunidadBean;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.common.activity.TokenHandler.TKhandler;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkUp;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOneUser;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.activity.utils.RolUi.ADM;
import static com.didekindroid.usuario.activity.utils.RolUi.INQ;
import static com.didekindroid.usuario.activity.utils.RolUi.PRE;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeUserComuBeanFromView;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.typeComunidadData;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.typeUserComuData;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.validaTypedUserComuBean;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.validaTypedUsuarioComunidad;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro
 * Date: 09/07/15
 * Time: 09:56
 */
@RunWith(AndroidJUnit4.class)
public class RegComuAndUserComuAcTest {

    private RegComuAndUserComuAc activity;
    private RegUserComuFr regUserComuFr;

    @Rule
    public ActivityTestRule<RegComuAndUserComuAc> mActivityRule = new ActivityTestRule<>(RegComuAndUserComuAc.class, true, false);
    private int activityLayoutId = R.id.reg_comu_and_usercomu_layout;

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(5000);
    }

    @Before
    public void setUp() throws Exception
    {
        // Preconditions: the user is already registered.
        signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOneUser(USER_PEPE);
    }

    @Test
    public void testPreconditions() throws UiException
    {
        activity = mActivityRule.launchActivity(new Intent());
        RegComuFr regComuFr = (RegComuFr) activity.getFragmentManager().findFragmentById(R.id.reg_comunidad_frg);
        regUserComuFr = (RegUserComuFr) activity.getFragmentManager().findFragmentById(R.id
                .reg_usercomu_frg);

        assertThat(activity, notNullValue());

        assertThat(isRegisteredUser(activity), is(true));
        assertThat(TKhandler.getAccessTokenInCache(), notNullValue());

        assertThat(regComuFr, notNullValue());
        assertThat(regUserComuFr, notNullValue());

        onView(withId(activityLayoutId));
        onView(withId(R.id.reg_comunidad_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usercomu_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_comu_usuariocomunidad_button)).perform(scrollTo()).check(matches(isDisplayed()));

        onView(withId(R.id.appbar)).perform(scrollTo()).check(matches(isDisplayed()));
        ActivityTestUtils.clickNavigateUp();
    }

    @Test
    public void testMakeUsuarioComunidadBeanFromView() throws Exception
    {
        activity = mActivityRule.launchActivity(new Intent());
        Resources resources = activity.getResources();
        regUserComuFr = (RegUserComuFr) activity.getFragmentManager().findFragmentById(R.id
                .reg_usercomu_frg);

        View usuarioComunidadRegView = activity.findViewById(R.id.reg_usercomu_frg);

        //UsuarioComunidadBean data.
        typeUserComuData("port2","escale_b","planta-N","puerta5", PRE, ADM, INQ);
        // ComunidadBean data: we do not introduce the data in the screen.
        ComunidadBean comunidadBean = new ComunidadBean("ataxo", "24 de Otoño", "001", "bis",
                new Municipio((short) 162, new Provincia((short) 10)));
        UsuarioComunidadBean usuarioComunidadBean =
                makeUserComuBeanFromView(usuarioComunidadRegView, comunidadBean, null);
        // Verificamos usuarioComunidadBean.
        validaTypedUserComuBean(usuarioComunidadBean,"port2","escale_b","planta-N","puerta5", true, true, false, true);

        // Verificamos usuarioComunidad.
        usuarioComunidadBean.validate(resources, new StringBuilder(resources.getText(R.string.error_validation_msg)));
        UsuarioComunidad usuarioComunidad = usuarioComunidadBean.getUsuarioComunidad();
        validaTypedUsuarioComunidad(usuarioComunidad,"port2","escale_b","planta-N","puerta5","adm,pre,inq");
    }

    @Test
    public void testRegisterComuAndUserComu_1() throws InterruptedException
    {
        activity = mActivityRule.launchActivity(new Intent());

        // NO data.
        onView(withId(R.id.reg_comu_usuariocomunidad_button)).perform(scrollTo(), click());

        ActivityTestUtils.checkToastInTest(R.string.error_validation_msg, activity,
                R.string.tipo_via,
                R.string.nombre_via,
                R.string.municipio,
                R.string.reg_usercomu_role_rot);
    }

    @Test
    public void testRegisterComuAndUserComu_2()
    {
        activity = mActivityRule.launchActivity(new Intent());

        // Wrong data.
        onView(withId(R.id.reg_usercomu_checbox_pre)).perform(click());
        onView(withId(R.id.reg_usercomu_checbox_admin)).perform(click());
        onView(withId(R.id.reg_usercomu_checbox_inq)).perform(click());
        onView(withId(R.id.reg_usercomu_checbox_pro)).perform(click());

        onView(withId(R.id.reg_usercomu_escalera_ed)).perform(typeText("escal ?? b"), closeSoftKeyboard());

        onView(withId(R.id.reg_comu_usuariocomunidad_button)).perform(scrollTo(), click());
        checkToastInTest(R.string.error_validation_msg, activity,
                R.string.tipo_via,
                R.string.nombre_via,
                R.string.municipio,
                R.string.reg_usercomu_role_rot,
                R.string.reg_usercomu_escalera_rot);
    }

    @Test
    public void testRegisterComuAndUserComu_3() throws InterruptedException
    {
        activity = mActivityRule.launchActivity(new Intent());

        typeComunidadData();
        typeUserComuData("port2","escale_b","planta-N","puerta5", PRE, ADM, INQ);

        onView(withId(R.id.reg_comu_usuariocomunidad_button)).perform(scrollTo(), click());
        onView(withId(R.id.see_usercomu_by_user_frg)).check(matches(isDisplayed()));

        checkUp(activityLayoutId);
    }
}