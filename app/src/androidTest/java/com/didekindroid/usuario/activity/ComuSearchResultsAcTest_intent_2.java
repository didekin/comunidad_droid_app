package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.serviceone.domain.Comunidad;
import com.didekindroid.R;
import com.didekindroid.common.UiException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.common.utils.AppKeysForBundle.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.common.utils.AppKeysForBundle.COMUNIDAD_SEARCH;
import static com.didekindroid.usuario.activity.utils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.cleanOptions;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.signUpAndUpdateTk;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_LA_PLAZUELA_5;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_PLAZUELA5_PEPE;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_REAL_JUAN;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.Matchers.is;

/**
 * User: pedro@didekin
 * Date: 04/11/15
 * Time: 18:14
 */
@RunWith(AndroidJUnit4.class)
public class ComuSearchResultsAcTest_intent_2 {

    ComuSearchResultsAc mActivity;
    Comunidad comunidad;

    @Rule
    public IntentsTestRule<ComuSearchResultsAc> intentRule = new IntentsTestRule<ComuSearchResultsAc>(ComuSearchResultsAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            try {
                signUpAndUpdateTk(COMU_PLAZUELA5_PEPE);
                comunidad = ServOne.getComusByUser().get(0);
                signUpAndUpdateTk(COMU_REAL_JUAN);
            } catch (UiException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Intent getActivityIntent()
        {
            Intent intent = new Intent();
            intent.putExtra(COMUNIDAD_SEARCH.extra, COMU_LA_PLAZUELA_5);
            return intent;
        }
    };

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(5000);
    }

    @Before
    public void setUp() throws Exception
    {
        mActivity = intentRule.getActivity();
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_JUAN_AND_PEPE);
    }

    @Test
    public void testOnComunidadSelected_2() throws Exception
    {
        // Usuario registrado. La búsqueda devuelve una comunidad a la que él NO está asociado.

        onData(is(COMU_LA_PLAZUELA_5)).perform(click());
        intended(hasExtra(COMUNIDAD_LIST_OBJECT.extra,comunidad));
        onView(withId(R.id.reg_usercomu_ac_layout)).check(matches(isDisplayed()));
    }
}