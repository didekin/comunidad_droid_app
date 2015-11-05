package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.serviceone.domain.Comunidad;
import com.didekin.serviceone.domain.Municipio;
import com.didekin.serviceone.domain.Provincia;
import com.didekin.serviceone.domain.Usuario;
import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.usuario.activity.utils.CleanEnum;

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
import static com.didekindroid.usuario.activity.utils.CleanEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.activity.utils.CleanEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.activity.utils.RolCheckBox.PROPIETARIO;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.COMUNIDAD_SEARCH;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.USERCOMU_LIST_OBJECT;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.cleanOptions;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.cleanTwoUsers;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.cleanWithTkhandler;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.regTwoUserComuSameUser;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.signUpAndUpdateTk;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_LA_PLAZUELA_5;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_PLAZUELA5_PEPE;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_REAL;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_REAL_JUAN;
import static com.didekindroid.usuario.dominio.DomainDataUtils.USER_JUAN;
import static com.didekindroid.usuario.dominio.DomainDataUtils.makeComunidad;
import static com.didekindroid.usuario.dominio.DomainDataUtils.makeListTwoUserComu;
import static com.didekindroid.usuario.dominio.DomainDataUtils.makeUsuario;
import static com.didekindroid.usuario.dominio.DomainDataUtils.makeUsuarioComunidad;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 04/11/15
 * Time: 18:14
 */
@RunWith(AndroidJUnit4.class)
public class ComuSearchResultsAcTest_intent_2 {

    ComuSearchResultsAc mActivity;
    Comunidad comuIntent;

    @Rule
    public IntentsTestRule<ComuSearchResultsAc> intentRule = new IntentsTestRule<ComuSearchResultsAc>(ComuSearchResultsAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            signUpAndUpdateTk(COMU_PLAZUELA5_PEPE);
            comuIntent = ServOne.getComusByUser().get(0);
            signUpAndUpdateTk(COMU_REAL_JUAN);
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

        onData(allOf(is(instanceOf(Comunidad.class)), hasProperty("nombreVia", is("de la Plazuela")))).perform(click());
        intended(hasExtra(COMUNIDAD_LIST_OBJECT.extra,comuIntent));
        onView(withId(R.id.reg_usercomu_ac_layout)).check(matches(isDisplayed()));
    }
}