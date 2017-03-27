package com.didekindroid.comunidad;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum;

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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.comunidad.RegComuFr.makeComunidadBeanFromView;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.typeComunidadData;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.validaTypedComunidadBean;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_NOTHING;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanWithTkhandler;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
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
    private RegComuFr regComuFr;
    File refreshTkFile;
    CleanUserEnum whatClean = CLEAN_NOTHING;

    @Rule
    public ActivityTestRule<ComuSearchAc> mActivityRule = new ActivityTestRule<>(ComuSearchAc.class, true, false);

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(2000);
    }

    @Before
    public void getFixture() throws Exception
    {
        context = InstrumentationRegistry.getTargetContext();
        refreshTkFile = TKhandler.getRefreshTokenFile();
        cleanWithTkhandler();
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

        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        // Es la actividad inicial de la aplicación.
        onView(allOf(
                withContentDescription(R.string.navigate_up_txt),
                isClickable())).check(doesNotExist());

        onView(withId(R.id.reg_comunidad_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.tipo_via_spinner)).check(matches(isDisplayed()));
        onView(withId(R.id.autonoma_comunidad_spinner)).check(matches(isDisplayed()));
        onView(withId(R.id.provincia_spinner)).check(matches(isDisplayed()));
        onView(withId(R.id.municipio_spinner)).check(matches(isDisplayed()));

        assertThat(regComuFr.getComunidadBean().getTipoVia(), Matchers.is("tipo de vía"));
        onView(allOf(
                withId(R.id.app_spinner_1_dropdown_item),
                withParent(withId(R.id.tipo_via_spinner))
        )).check(matches(withText(Matchers.is("tipo de vía")))).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.app_spinner_1_dropdown_item), withParent(withId(R.id.autonoma_comunidad_spinner))))
                .check(matches(withText(Matchers.is("comunidad autónoma")))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.app_spinner_1_dropdown_item), withParent(withId(R.id.provincia_spinner))))
                .check(matches(withText(Matchers.is("provincia")))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.app_spinner_1_dropdown_item), withParent(withId(R.id.municipio_spinner))))
                .check(matches(withText(Matchers.is("municipio")))).check(matches(isDisplayed()));
    }

    @Test
    public void testUpdateIsRegistered_1() throws UiException
    {
        activity = mActivityRule.launchActivity(new Intent());

        //No token.
        assertThat(refreshTkFile.exists(), is(false));
        assertThat(TKhandler.getTokenCache().get(), nullValue());
        assertThat(TKhandler.isRegisteredUser(), is(false));
    }

    @Test
    public void testUpdateIsRegistered_2() throws UiException, IOException
    {
        //With token.
        signUpAndUpdateTk(COMU_REAL_JUAN);
        assertThat(refreshTkFile.exists(), is(true));

        activity = mActivityRule.launchActivity(new Intent());
        assertThat(TKhandler.getTokenCache().get(), notNullValue());
        assertThat(TKhandler.isRegisteredUser(), is(true));

        whatClean = CLEAN_JUAN;
    }

    @Test
    public void testMakeComunidadBeanFromView() throws InterruptedException
    {
        activity = mActivityRule.launchActivity(new Intent());
        regComuFr = (RegComuFr) activity.getFragmentManager().findFragmentById(R.id.reg_comunidad_frg);

        typeComunidadData();

        makeComunidadBeanFromView(regComuFr.getFragmentView(), regComuFr.getComunidadBean());
        validaTypedComunidadBean(regComuFr.getComunidadBean(), "Calle", (short) 3, (short) 13, "Real", "5", "Bis");
    }

    @Test
    public void testComunidadWrong() throws InterruptedException
    {
        activity = mActivityRule.launchActivity(new Intent());

        onView(withId(R.id.comunidad_nombre_via_editT)).perform(typeText("select * via"));
        onView(withId(R.id.comunidad_numero_editT)).perform(typeText("123"));
        onView(withId(R.id.comunidad_sufijo_numero_editT)).perform(typeText("Tris"), closeSoftKeyboard());

        onView(withId(R.id.searchComunidad_Bton)).perform(click());
        checkToastInTest(R.string.error_validation_msg, activity, R.string.tipo_via, R.string.nombre_via, R.string.municipio);
    }
}