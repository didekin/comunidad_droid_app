package com.didekindroid.incidencia.core.reg;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.core.AmbitoIncidValueObj;
import com.didekinlib.model.comunidad.Comunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.testutil.ComuDataTestUtil.COMU_LA_FUENTE;
import static com.didekindroid.testutil.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.isViewDisplayed;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_LA_FUENTE_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.regSeveralUserComuSameUser;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 17/11/15
 * Time: 10:07
 */
@RunWith(AndroidJUnit4.class)
public class IncidRegAcTest_1 {

    @Rule
    public IntentsTestRule<IncidRegAc> intentRule = new IntentsTestRule<IncidRegAc>(IncidRegAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            try {
                regSeveralUserComuSameUser(COMU_ESCORIAL_PEPE, COMU_REAL_PEPE, COMU_LA_FUENTE_PEPE);
            } catch (IOException | UiException e) {
                fail();
            }
        }
    };


    int activityLayoutId = R.id.incid_reg_ac_layout;
    int fragmentLayoutId = R.id.incid_reg_frg;
    AmbitoIncidValueObj ambitoObj = new AmbitoIncidValueObj((short) 10, "Calefacción comunitaria");
    private IncidRegAc activity;

    @Before
    public void setUp() throws Exception
    {
        activity = intentRule.getActivity();
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_PEPE);
    }

    //  ================================ INTEGRATION ===================================

    @Test
    public void testRegisterIncidencia_1()
    {
        /* Caso NOT OK: descripción de incidencia no válida.*/
        doImportanciaSpinner(4);
        doAmbitoAndDescripcion(ambitoObj, "descripcion = not valid");
        onView(withId(R.id.incid_reg_ac_button)).perform(scrollTo(), click());

        checkToastInTest(R.string.error_validation_msg, activity, R.string.incid_reg_descripcion);
    }

    @Test
    public void testRegisterIncidencia_2() throws UiException
    {
        // Caso OK: incidencia con datos de importancia.
        doImportanciaSpinner(4);
        doAmbitoAndDescripcion(ambitoObj, "descripcion is valid");

        onView(withId(R.id.incid_reg_ac_button)).perform(scrollTo(), click());
        waitAtMost(3, SECONDS).until(isViewDisplayed(withId(R.id.incid_see_open_by_comu_ac)));

        checkUp(activityLayoutId, fragmentLayoutId);
    }

    @Test
    public void testRegisterIncidencia_3() throws UiException
    {
        // Caso OK: no cubro importancia.
        doAmbitoAndDescripcion(ambitoObj, "descripcion is valid");
        onView(withId(R.id.incid_reg_ac_button)).perform(scrollTo(), click());

        waitAtMost(3, SECONDS).until(isViewDisplayed(withId(R.id.incid_see_open_by_comu_ac)));

        checkUp(activityLayoutId, fragmentLayoutId);
    }

    @Test
    public void testRegisterIncidencia_4() throws UiException
    {
        // Probamos cambio de comunidad en spinner: Calle La Fuente.
        onView(withId(R.id.incid_reg_comunidad_spinner)).perform(click());
        onData(allOf(is(instanceOf(Comunidad.class)), is(COMU_LA_FUENTE))).perform(click()).check(matches(isDisplayed()));

        // Registro de incidencia con importancia.
        doImportanciaSpinner(4);
        doAmbitoAndDescripcion(ambitoObj, "Incidencia La Fuente");
        onView(withId(R.id.incid_reg_ac_button)).perform(scrollTo(), click());

        waitAtMost(3, SECONDS).until(isViewDisplayed(withId(R.id.incid_see_open_by_comu_ac)));

        checkUp(activityLayoutId, fragmentLayoutId);
    }

//    =======================   HELPER METHODS ========================

    private void doImportanciaSpinner(int i)
    {
        onView(withId(R.id.incid_reg_importancia_spinner)).perform(click());
        onData
                (allOf(
                        is(instanceOf(String.class)),
                        is(activity.getResources().getStringArray(R.array.IncidImportanciaArray)[i]))
                )
                .perform(click());
    }

    private void doAmbitoAndDescripcion(AmbitoIncidValueObj ambito, String descripcion)
    {
        onView(withId(R.id.incid_reg_ambito_spinner)).perform(click());
        onData(allOf(
                is(instanceOf(AmbitoIncidValueObj.class)),
                is(ambito)
        )).perform(click());
        onView(withId(R.id.incid_reg_desc_ed)).perform(typeText(descripcion));
    }
}