package com.didekindroid.usuario.activity;

import android.content.res.Resources;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import com.didekin.serviceone.domain.Municipio;
import com.didekin.serviceone.domain.Provincia;
import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.usuario.activity.utils.UsuarioTestUtils;
import com.didekindroid.usuario.dominio.ComunidadBean;
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
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeUserComuBeanFromView;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.*;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuario.dominio.DomainDataUtils.USER_PEPE;
import static com.didekindroid.usuario.security.TokenHandler.TKhandler;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;

/**
 * User: pedro
 * Date: 09/07/15
 * Time: 09:56
 */
@RunWith(AndroidJUnit4.class)
public class RegComuAndUserComuAcTest {

    private RegComuAndUserComuAc activity;
    private Resources resources;
    private RegComuFr regComuFr;
    private RegUserComuFr regUserComuFr;

    @Rule
    public ActivityTestRule<RegComuAndUserComuAc> mActivityRule = new ActivityTestRule<>(RegComuAndUserComuAc.class);

    @Before
    public void setUp() throws Exception
    {
        // Preconditions: the user is already registered.
        signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);

        activity = mActivityRule.getActivity();
        resources = activity.getResources();
        regComuFr = (RegComuFr) activity.getFragmentManager().findFragmentById(R.id.reg_comunidad_frg);
        regUserComuFr = (RegUserComuFr) activity.getFragmentManager().findFragmentById(R.id
                .reg_usercomu_frg);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOneUser(USER_PEPE);
    }

    @Test
    public void testPreconditions()
    {
        assertThat(activity, notNullValue());

        assertThat(isRegisteredUser(activity), is(true));
        assertThat(TKhandler.getAccessTokenInCache(), notNullValue());

        assertThat(resources, notNullValue());
        assertThat(regComuFr, notNullValue());
        assertThat(regUserComuFr, notNullValue());

        onView(withId(R.id.reg_comu_and_usercomu_layout));
        onView(withId(R.id.reg_comunidad_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usercomu_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_comu_usuariocomunidad_button)).perform(scrollTo()).check(matches(isDisplayed()));
    }

    @Test
    public void testMakeUsuarioComunidadBeanFromView() throws Exception
    {
        View usuarioComunidadRegView = activity.findViewById(R.id.reg_usercomu_frg);

        //UsuarioComunidadBean data.
        onView(withId(R.id.reg_usercomu_portal_ed)).perform(typeText("port2"));
        onView(withId(R.id.reg_usercomu_escalera_ed)).perform(typeText("escale_b"));
        onView(withId(R.id.reg_usercomu_planta_ed)).perform(typeText("planta-N"));
        onView(withId(R.id.reg_usercomu_puerta_ed)).perform(typeText("puerta5"), closeSoftKeyboard());
        onView(withId(R.id.reg_usercomu_checbox_pre)).perform(scrollTo(), click());
        onView(withId(R.id.reg_usercomu_checbox_admin)).perform(scrollTo(), click());
        onView(withId(R.id.reg_usercomu_checbox_inq)).perform(scrollTo(), click());

        // ComunidadBean data: we do not introduce the data in the screen.
        ComunidadBean comunidadBean = new ComunidadBean("ataxo", "24 de Otoño", "001", "bis",
                new Municipio((short) 162, new Provincia((short) 10)));

        UsuarioComunidadBean usuarioComunidadBean =
                makeUserComuBeanFromView(usuarioComunidadRegView, comunidadBean, null);
        assertThat(usuarioComunidadBean.getPortal(), is("port2"));
        assertThat(usuarioComunidadBean.getEscalera(), is("escale_b"));
        assertThat(usuarioComunidadBean.getPlanta(), is("planta-N"));
        assertThat(usuarioComunidadBean.getPuerta(), is("puerta5"));
        assertThat(usuarioComunidadBean.isPresidente(), is(true));
        assertThat(usuarioComunidadBean.isAdministrador(), is(true));
        assertThat(usuarioComunidadBean.isPropietario(), is(false));
        assertThat(usuarioComunidadBean.isInquilino(), is(true));

        usuarioComunidadBean.validate(resources, new StringBuilder(resources.getString(R.string.error_validation_msg)));
        UsuarioComunidad usuarioComunidad = usuarioComunidadBean.getUsuarioComunidad();
        assertThat(usuarioComunidad.getPortal(), is("port2"));
        assertThat(usuarioComunidad.getEscalera(), is("escale_b"));
        assertThat(usuarioComunidad.getPlanta(), is("planta-N"));
        assertThat(usuarioComunidad.getPuerta(), is("puerta5"));
        assertThat(usuarioComunidad.getRoles(), is("adm,pre,inq"));
    }

    @Test
    public void testRegisterComuAndUserComu_1() throws InterruptedException
    {
        // NO data.

        onView(withId(R.id.reg_comu_usuariocomunidad_button)).perform(scrollTo(), click());

        UsuarioTestUtils.checkToastInTest(R.string.error_validation_msg, activity,
                R.string.tipo_via,
                R.string.nombre_via,
                R.string.municipio,
                R.string.usercomu_role_rot);

        Thread.sleep(2000);
    }

    @Test
    public void testRegisterComuAndUserComu_2()
    {
        // Wrong data.

        onView(withId(R.id.reg_usercomu_checbox_pre)).perform(scrollTo(), click());
        onView(withId(R.id.reg_usercomu_checbox_admin)).perform(scrollTo(), click());
        onView(withId(R.id.reg_usercomu_checbox_inq)).perform(scrollTo(), click());
        onView(withId(R.id.reg_usercomu_checbox_pro)).perform(scrollTo(), click());

        onView(withId(R.id.reg_usercomu_escalera_ed)).perform(typeText("esca??_b"));

        onView(withId(R.id.reg_comu_usuariocomunidad_button)).perform(scrollTo(), click());
        checkToastInTest(R.string.error_validation_msg, activity,
                R.string.tipo_via,
                R.string.nombre_via,
                R.string.municipio,
                R.string.usercomu_role_rot,
                R.string.reg_usercomu_escalera_hint);
    }

    @Test
    public void testRegisterComuAndUserComu_3()
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

        onView(withId(R.id.comunidad_nombre_via_editT)).perform(typeText("nombre via One"));
        onView(withId(R.id.comunidad_numero_editT)).perform(typeText("123"));
        onView(withId(R.id.comunidad_sufijo_numero_editT)).perform(typeText("Tris"), closeSoftKeyboard());

        onView(withId(R.id.reg_usercomu_portal_ed)).perform(typeText("port2"));
        onView(withId(R.id.reg_usercomu_escalera_ed)).perform(typeText("escale_b"));
        onView(withId(R.id.reg_usercomu_planta_ed)).perform(typeText("planta-N"));
        onView(withId(R.id.reg_usercomu_puerta_ed)).perform(typeText("puerta5"), closeSoftKeyboard());
        onView(withId(R.id.reg_usercomu_checbox_pre)).perform(scrollTo(), click());
        onView(withId(R.id.reg_usercomu_checbox_admin)).perform(scrollTo(), click());
        onView(withId(R.id.reg_usercomu_checbox_inq)).perform(scrollTo(), click());

        onView(withId(R.id.reg_comu_usuariocomunidad_button)).perform(scrollTo(), click());
        onView(withId(R.id.see_usercomu_by_user_ac_layout)).check(matches(isDisplayed()));
    }
}