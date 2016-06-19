package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.usuario.testutils.CleanUserEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.CursorMatchers.withRowString;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.common.activity.TokenHandler.TKhandler;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_NOTHING;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.USER_JUAN2;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;

/**
 * User: pedro
 * Date: 07/07/15
 * Time: 10:26
 */
@SuppressWarnings({"unchecked", "ConstantConditions"})
@RunWith(AndroidJUnit4.class)
public class RegComuAndUserAndUserComuAcTest_4 {

    @Rule
    public ActivityTestRule<RegComuAndUserAndUserComuAc> mActivityRule = new ActivityTestRule<>(RegComuAndUserAndUserComuAc.class, true, false);

    RegComuAndUserAndUserComuAc mActivity;
    Resources resources;
    CleanUserEnum whatToClean;

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
    public void testRegisterComuAndUserComuAndUser_2() throws UiException, InterruptedException
    {
        whatToClean = CleanUserEnum.CLEAN_JUAN2;

        mActivity = mActivityRule.launchActivity(new Intent());

        // Comunidad data.
        onView(withId(R.id.tipo_via_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Callejon")))
                .perform(click());

        Thread.sleep(5000);
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

        // UsuarioComunidad.
        onView(withId(R.id.reg_usercomu_portal_ed)).perform(typeText("port2"));
        onView(withId(R.id.reg_usercomu_escalera_ed)).perform(typeText("escale_b"));
        onView(withId(R.id.reg_usercomu_planta_ed)).perform(typeText("planta-N"));
        onView(withId(R.id.reg_usercomu_puerta_ed)).perform(typeText("puerta5"), closeSoftKeyboard());
        onView(withId(R.id.reg_usercomu_checbox_pre)).perform(scrollTo(), click());
        onView(withId(R.id.reg_usercomu_checbox_inq)).perform(scrollTo(), click());

        // Usuario.
        onView(withId(R.id.reg_usuario_email_editT)).perform(scrollTo(), typeText(USER_JUAN2.getUserName()));
        onView(withId(R.id.reg_usuario_alias_ediT)).perform(scrollTo(), typeText(USER_JUAN2.getAlias()));
        onView(withId(R.id.reg_usuario_password_ediT)).perform(scrollTo(), typeText(USER_JUAN2.getPassword()));
        onView(withId(R.id.reg_usuario_password_confirm_ediT)).perform(scrollTo(),
                typeText(USER_JUAN2.getPassword()), closeSoftKeyboard());

        onView(withId(R.id.reg_com_usuario_usuariocomu_button)).perform(scrollTo(), click());
        onView(withId(R.id.see_usercomu_by_user_ac_frg_container)).check(matches(isDisplayed()));
        assertThat(TKhandler.getAccessTokenInCache(), notNullValue());
        assertThat(TKhandler.getRefreshTokenKey(), is(TKhandler.getAccessTokenInCache().getRefreshToken().getValue()));
        assertThat(isRegisteredUser(mActivity), is(true));
    }
}