package com.didekindroid.usuario.activity;

import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.didekindroid.R;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.didekindroid.common.ui.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.common.DataUsuarioTestUtils.typeComunidadData;
import static com.didekindroid.usuario.common.UserIntentExtras.COMUNIDAD_SEARCH;
import static com.didekindroid.usuario.common.TokenHandler.TKhandler;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 12/08/15
 * Time: 17:06
 */
@RunWith(AndroidJUnit4.class)
public class ComuSearchAcTest_B {

    @Rule
    public IntentsTestRule<ComuSearchAc> intentRule = new IntentsTestRule<>(ComuSearchAc.class);

    @Test
    public void testSearchComusExtra()
    {
        // Without token.
        assertThat(isRegisteredUser(intentRule.getActivity()), is(false));
        typeComunidadData();
        onView(withId(R.id.searchComunidad_Bton)).perform(click());

        intended(hasExtraWithKey(COMUNIDAD_SEARCH.extra));

        // No results in DB. The user is invited to register.
        ViewInteraction toastViewInteraction = onView(withText(
                containsString(intentRule.getActivity().getResources().getText(R.string.no_result_search_comunidad).toString())
        ));
        toastViewInteraction.inRoot(withDecorView(not(intentRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
        onView(withId(R.id.reg_comu_usuario_usuariocomu_layout)).check(matches(isDisplayed()));
    }


    @After
    public void cleanData()
    {
        if (TKhandler.getRefreshTokenFile().exists()) {
            TKhandler.getRefreshTokenFile().delete();
        }
        TKhandler.getTokensCache().invalidateAll();
        TKhandler.updateRefreshToken(null);
    }
}
