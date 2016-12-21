package com.didekindroid.usuariocomunidad;

import android.content.Intent;
import android.content.res.Resources;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.didekin.comunidad.Municipio;
import com.didekin.comunidad.Provincia;
import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekinaar.R;
import com.didekinaar.exception.UiException;
import com.didekindroid.comunidad.ComunidadBean;
import com.didekindroid.comunidad.RegComuFr;
import com.didekinaar.testutil.AarActivityTestUtils;
import com.didekinaar.usuario.testutil.UsuarioDataTestUtils;
import com.didekindroid.comunidad.testutil.ComuEspresoTestUtil;
import com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil;
import com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil;

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
import static com.didekinaar.security.TokenHandler.TKhandler;
import static com.didekinaar.testutil.AarActivityTestUtils.checkToastInTest;
import static com.didekinaar.testutil.AarActivityTestUtils.checkUp;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekinaar.usuariocomunidad.RolUi.ADM;
import static com.didekinaar.usuariocomunidad.RolUi.INQ;
import static com.didekinaar.usuariocomunidad.RolUi.PRE;
import static com.didekinaar.usuariocomunidad.UserAndComuFiller.makeUserComuBeanFromView;
import static com.didekinaar.utils.UIutils.isRegisteredUser;
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
        UserComuTestUtil.signUpAndUpdateTk(UserComuTestUtil.COMU_TRAV_PLAZUELA_PEPE);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOneUser(UsuarioDataTestUtils.USER_PEPE);
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
        onView(ViewMatchers.withId(R.id.reg_comunidad_frg)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.reg_usercomu_frg)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.reg_comu_usuariocomunidad_button)).perform(scrollTo()).check(matches(isDisplayed()));

        onView(ViewMatchers.withId(R.id.appbar)).perform(scrollTo()).check(matches(isDisplayed()));
        AarActivityTestUtils.clickNavigateUp();
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
        UserComuEspressoTestUtil.typeUserComuData("port2","escale_b","planta-N","puerta5", PRE, ADM, INQ);
        // ComunidadBean data: we do not introduce the data in the screen.
        ComunidadBean comunidadBean = new ComunidadBean("ataxo", "24 de Otoño", "001", "bis",
                new Municipio((short) 162, new Provincia((short) 10)));
        UsuarioComunidadBean usuarioComunidadBean =
                makeUserComuBeanFromView(usuarioComunidadRegView, comunidadBean, null);
        // Verificamos usuarioComunidadBean.
        UserComuEspressoTestUtil.validaTypedUserComuBean(usuarioComunidadBean,"port2","escale_b","planta-N","puerta5", true, true, false, true);

        // Verificamos usuarioComunidad.
        usuarioComunidadBean.validate(resources, new StringBuilder(resources.getText(R.string.error_validation_msg)));
        UsuarioComunidad usuarioComunidad = usuarioComunidadBean.getUsuarioComunidad();
        UserComuEspressoTestUtil.validaTypedUsuarioComunidad(usuarioComunidad,"port2","escale_b","planta-N","puerta5","adm,pre,inq");
    }

    @Test
    public void testRegisterComuAndUserComu_1() throws InterruptedException
    {
        activity = mActivityRule.launchActivity(new Intent());

        // NO data.
        onView(ViewMatchers.withId(R.id.reg_comu_usuariocomunidad_button)).perform(scrollTo(), click());

        AarActivityTestUtils.checkToastInTest(R.string.error_validation_msg, activity,
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
        onView(ViewMatchers.withId(R.id.reg_usercomu_checbox_pre)).perform(click());
        onView(ViewMatchers.withId(R.id.reg_usercomu_checbox_admin)).perform(click());
        onView(ViewMatchers.withId(R.id.reg_usercomu_checbox_inq)).perform(click());
        onView(ViewMatchers.withId(R.id.reg_usercomu_checbox_pro)).perform(click());

        onView(ViewMatchers.withId(R.id.reg_usercomu_escalera_ed)).perform(typeText("escal ?? b"), closeSoftKeyboard());

        onView(ViewMatchers.withId(R.id.reg_comu_usuariocomunidad_button)).perform(scrollTo(), click());
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

        ComuEspresoTestUtil.typeComunidadData();
        UserComuEspressoTestUtil.typeUserComuData("port2","escale_b","planta-N","puerta5", PRE, ADM, INQ);

        onView(ViewMatchers.withId(R.id.reg_comu_usuariocomunidad_button)).perform(scrollTo(), click());
        onView(ViewMatchers.withId(R.id.see_usercomu_by_user_frg)).check(matches(isDisplayed()));

        checkUp(activityLayoutId);
    }
}