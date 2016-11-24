package com.didekinaar.comunidad;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekinaar.R;
import com.didekinaar.exception.UiAarException;
import com.didekinaar.testutil.AarActivityTestUtils;
import com.didekinaar.testutil.CleanUserEnum;
import com.didekinaar.testutil.UsuarioTestUtils;

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
import static com.didekinaar.security.TokenHandler.TKhandler;
import static com.didekinaar.testutil.AarActivityTestUtils.checkToastInTest;
import static com.didekinaar.testutil.AarActivityTestUtils.cleanOptions;
import static com.didekinaar.usuariocomunidad.UserAndComuFiller.makeComunidadBeanFromView;
import static com.didekinaar.utils.UIutils.isRegisteredUser;
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
    CleanUserEnum whatClean = CleanUserEnum.CLEAN_NOTHING;

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
        AarActivityTestUtils.cleanWithTkhandler();
    }

    @After
    public void cleanData() throws UiAarException
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
    public void testUpdateIsRegistered_1() throws UiAarException
    {
        activity = mActivityRule.launchActivity(new Intent());

        //No token.
        assertThat(refreshTkFile.exists(), is(false));
        assertThat(TKhandler.getAccessTokenInCache(), nullValue());
        assertThat(isRegisteredUser(activity), is(false));
    }

    @Test
    public void testUpdateIsRegistered_2() throws UiAarException, IOException
    {
        //With token.
        AarActivityTestUtils.signUpAndUpdateTk(UsuarioTestUtils.COMU_REAL_JUAN);
        assertThat(refreshTkFile.exists(), is(true));

        activity = mActivityRule.launchActivity(new Intent());
        assertThat(TKhandler.getAccessTokenInCache(), notNullValue());
        assertThat(isRegisteredUser(activity), is(true));

        whatClean = CleanUserEnum.CLEAN_JUAN;
    }

    @Test
    public void testMakeComunidadBeanFromView() throws InterruptedException
    {
        activity = mActivityRule.launchActivity(new Intent());
        regComuFr = (RegComuFr) activity.getFragmentManager().findFragmentById(R.id.reg_comunidad_frg);

        UsuarioTestUtils.typeComunidadData();

        makeComunidadBeanFromView(regComuFr.getFragmentView(), regComuFr.getComunidadBean());
        UsuarioTestUtils.validaTypedComunidadBean(regComuFr.getComunidadBean(), "Calle", (short) 3, (short) 13, "Real", "5", "Bis");
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