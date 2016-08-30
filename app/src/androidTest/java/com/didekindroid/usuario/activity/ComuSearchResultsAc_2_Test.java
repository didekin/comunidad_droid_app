package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.usuario.dominio.Comunidad;
import com.didekin.usuario.dominio.Usuario;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.common.activity.BundleKey.COMUNIDAD_SEARCH;
import static com.didekindroid.common.activity.BundleKey.USERCOMU_LIST_OBJECT;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_REAL;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_REAL_JUAN;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.Matchers.is;

/**
 * User: pedro@didekin
 * Date: 04/11/15
 * Time: 18:14
 */
@RunWith(AndroidJUnit4.class)
public class ComuSearchResultsAc_2_Test {

    ComuSearchResultsAc mActivity;
    Comunidad comuIntent;
    Usuario userIntent;

    @Rule
    public IntentsTestRule<ComuSearchResultsAc> intentRule = new IntentsTestRule<ComuSearchResultsAc>(ComuSearchResultsAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            try {
                userIntent = signUpAndUpdateTk(COMU_REAL_JUAN);
                comuIntent = ServOne.getComusByUser().get(0);
            } catch (UiException | IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Intent getActivityIntent()
        {
            Intent intent = new Intent();
            intent.putExtra(COMUNIDAD_SEARCH.key, COMU_REAL);
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
        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void testOnComunidadSelected_1() throws Exception
    {
        // Usuario registrado. La búsqueda devuelve una comunidad a la que él ya está asociado.

        onData(is(COMU_REAL)).perform(click());

        UsuarioComunidad usuarioComunidad = new UsuarioComunidad.UserComuBuilder(comuIntent, userIntent)
                .portal(COMU_REAL_JUAN.getPortal())
                .escalera(COMU_REAL_JUAN.getEscalera())
                .planta(COMU_REAL_JUAN.getPlanta())
                .puerta(COMU_REAL_JUAN.getPuerta())
                .build();

        intended(hasExtra(USERCOMU_LIST_OBJECT.key, usuarioComunidad));
        onView(withId(R.id.usercomu_data_ac_layout)).check(matches(isDisplayed()));
    }
}