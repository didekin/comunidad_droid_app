package com.didekindroid.comunidad;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.didekinaar.exception.UiException;
import com.didekinaar.usuario.testutil.UsuarioDataTestUtils;
import com.didekindroid.R;
import com.didekindroid.comunidad.testutil.ComuEspresoTestUtil;
import com.didekindroid.usuariocomunidad.UserComuTestUtil;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekinaar.security.TokenIdentityCacher.TKhandler;
import static com.didekinaar.testutil.AarActivityTestUtils.checkToastInTest;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.comunidad.RegComuFr.makeComunidadBeanFromView;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 15/05/15
 * Time: 09:53
 */
@RunWith(AndroidJUnit4.class)
public class ComuSearchAc_1_Test {

    private ComuSearchAc activity;
    Context context;
    private Resources resources;
    private RegComuFr regComuFr;
    File refreshTkFile;
    UsuarioDataTestUtils.CleanUserEnum whatClean = UsuarioDataTestUtils.CleanUserEnum.CLEAN_NOTHING;

    @Rule
    public ActivityTestRule<ComuSearchAc> mActivityRule = new ActivityTestRule<>(ComuSearchAc.class, true, false);

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(4000);
    }

    @Before
    public void getFixture() throws Exception
    {
        context = InstrumentationRegistry.getTargetContext();
        refreshTkFile = TKhandler.getRefreshTokenFile();
        resources = context.getResources();
        UsuarioDataTestUtils.cleanWithTkhandler();
    }

    @After
    public void cleanData() throws UiException
    {
        cleanOptions(whatClean);
    }

    @Test
    public void testPreconditions()
    {
        activity = mActivityRule.launchActivity(new Intent());
        regComuFr = (RegComuFr) activity.getFragmentManager().findFragmentById(R.id.reg_comunidad_frg);

        assertThat(activity, notNullValue());
        assertThat(resources, notNullValue());
        assertThat(regComuFr, notNullValue());

        onView(ViewMatchers.withId(R.id.appbar)).check(matches(isDisplayed()));
        // Es la actividad inicial de la aplicación.
        onView(allOf(
                ViewMatchers.withContentDescription(R.string.navigate_up_txt),
                isClickable())).check(doesNotExist());

        onView(ViewMatchers.withId(R.id.reg_comunidad_frg)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.tipo_via_spinner)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.autonoma_comunidad_spinner)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.provincia_spinner)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.municipio_spinner)).check(matches(isDisplayed()));

        assertThat(regComuFr.getComunidadBean().getTipoVia(), Matchers.is("tipo de vía"));
        onView(allOf(
                ViewMatchers.withId(R.id.app_spinner_1_dropdown_item),
                withParent(ViewMatchers.withId(R.id.tipo_via_spinner))
        )).check(matches(withText(Matchers.is("tipo de vía")))).check(matches(isDisplayed()));

        onView(allOf(ViewMatchers.withId(R.id.app_spinner_1_dropdown_item), withParent(ViewMatchers.withId(R.id.autonoma_comunidad_spinner))))
                .check(matches(withText(Matchers.is("comunidad autónoma")))).check(matches(isDisplayed()));
        onView(allOf(ViewMatchers.withId(R.id.app_spinner_1_dropdown_item), withParent(ViewMatchers.withId(R.id.provincia_spinner))))
                .check(matches(withText(Matchers.is("provincia")))).check(matches(isDisplayed()));
        onView(allOf(ViewMatchers.withId(R.id.app_spinner_1_dropdown_item), withParent(ViewMatchers.withId(R.id.municipio_spinner))))
                .check(matches(withText(Matchers.is("municipio")))).check(matches(isDisplayed()));
    }

    @Test
    public void testUpdateIsRegistered_1() throws UiException
    {
        activity = mActivityRule.launchActivity(new Intent());

        //No token.
        assertThat(refreshTkFile.exists(), is(false));
        assertThat(TKhandler.getAccessTokenInCache(), nullValue());
        assertThat(TKhandler.isRegisteredUser(), is(false));
    }

    @Test
    public void testUpdateIsRegistered_2() throws UiException, IOException
    {
        //With token.
        UserComuTestUtil.signUpAndUpdateTk(UserComuTestUtil.COMU_REAL_JUAN);
        assertThat(refreshTkFile.exists(), is(true));

        activity = mActivityRule.launchActivity(new Intent());
        assertThat(TKhandler.getAccessTokenInCache(), notNullValue());
        assertThat(TKhandler.isRegisteredUser(), is(true));

        whatClean = UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
    }

    @Test
    public void testMakeComunidadBeanFromView() throws InterruptedException
    {
        activity = mActivityRule.launchActivity(new Intent());
        regComuFr = (RegComuFr) activity.getFragmentManager().findFragmentById(R.id.reg_comunidad_frg);

        ComuEspresoTestUtil.typeComunidadData();

        makeComunidadBeanFromView(regComuFr.getFragmentView(), regComuFr.getComunidadBean());
        ComuEspresoTestUtil.validaTypedComunidadBean(regComuFr.getComunidadBean(), "Calle", (short) 3, (short) 13, "Real", "5", "Bis");
    }

    @Test
    public void testComunidadWrong() throws InterruptedException
    {
        activity = mActivityRule.launchActivity(new Intent());

        onView(ViewMatchers.withId(R.id.comunidad_nombre_via_editT)).perform(ViewActions.typeText("select * via"));
        onView(ViewMatchers.withId(R.id.comunidad_numero_editT)).perform(ViewActions.typeText("123"));
        onView(ViewMatchers.withId(R.id.comunidad_sufijo_numero_editT)).perform(ViewActions.typeText("Tris"), ViewActions.closeSoftKeyboard());

        onView(ViewMatchers.withId(R.id.searchComunidad_Bton)).perform(ViewActions.click());
        checkToastInTest(R.string.error_validation_msg, activity, R.string.tipo_via, R.string.nombre_via, R.string.municipio);
    }
}