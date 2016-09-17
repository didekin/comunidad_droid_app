package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.common.activity.BundleKey.COMUNIDAD_SEARCH;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkUp;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOneUser;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanWithTkhandler;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_ESCORIAL_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_REAL_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.typeComunidadData;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 12/08/15
 * Time: 17:06
 *
 * Tests sin resultados de búsqueda.
 */
@RunWith(AndroidJUnit4.class)
public class ComuSearchAc_2_Test {

    // Navigate-up activity layout: vuelve a búsqueda de comunidad tanto si hay resultados como si no.
    int ativityLayoutId = R.id.comu_search_ac_linearlayout;

    @Rule
    public IntentsTestRule<ComuSearchAc> intentRule = new IntentsTestRule<>(ComuSearchAc.class, true, false);

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(5000);
    }

    @Before @After
    public void cleanData()
    {
        cleanWithTkhandler();
    }

    @Test
    public void testWithResults() throws InterruptedException, UiException, IOException
    {
        //With token.
        signUpAndUpdateTk(COMU_REAL_JUAN);

        ComuSearchAc activity = intentRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(activity), is(true));

        // Data corresponds to a comunidad in DB.
        typeComunidadData();
        onView(withId(R.id.searchComunidad_Bton)).perform(ViewActions.click());
        // Check the view for comunidades list fragment.
        onView(withId(R.id.comu_list_frg)).check(matches(isDisplayed()));

        checkUp(ativityLayoutId);

        cleanOneUser(USER_JUAN);
    }

    @Test
    public void testNoResults_1() throws InterruptedException
    {
        // Without token.
        intentRule.launchActivity(new Intent());

        typeComunidadData();
        onView(withId(R.id.searchComunidad_Bton)).perform(click());

        intended(hasExtraWithKey(COMUNIDAD_SEARCH.key));
        // No results in DB. The user is invited to register himself and the comunidad.
        checkToastInTest(R.string.no_result_search_comunidad, intentRule.getActivity());
        onView(withId(R.id.reg_comu_usuario_usuariocomu_layout)).check(matches(isDisplayed()));

        // Up: volvemos a búsqueda de comunidades.
        checkUp(ativityLayoutId);
    }

    @Test
    public void testNoResults_2() throws InterruptedException, UiException, IOException
    {
        // With token.
        signUpAndUpdateTk(COMU_ESCORIAL_JUAN);
        intentRule.launchActivity(new Intent());

        typeComunidadData();
        onView(withId(R.id.searchComunidad_Bton)).perform(click());

        intended(hasExtraWithKey(COMUNIDAD_SEARCH.key));
        // No results in DB. The user is invited to register the comunidad.
        checkToastInTest(R.string.no_result_search_comunidad, intentRule.getActivity());
        onView(withId(R.id.reg_comu_and_usercomu_layout)).check(matches(isDisplayed()));

        // Up: volvemos a búsqueda de comunidades.
        checkUp(ativityLayoutId);

        cleanOneUser(USER_JUAN);
    }
}
