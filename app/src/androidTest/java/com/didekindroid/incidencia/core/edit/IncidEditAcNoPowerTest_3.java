package com.didekindroid.incidencia.core.edit;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.incidencia.testutils.IncidUiUtils;
import com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.testutil.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;

/**
 * User: pedro@didekin
 * Date: 19/01/16
 * Time: 16:57
 *
 * Tests con inicio de IncidSeeUserComuImportancia desde IncidEditAc.IncidEditMinFr.
 * Dos incidImportancias registradas en BD, para la misma incidencia.
 * Usuario inicial en sesión SIN permisos para modificar o borrar una incidencia.
 */
@RunWith(AndroidJUnit4.class)
public class IncidEditAcNoPowerTest_3 extends IncidEditAbstractTest {

    @Override
    protected IntentsTestRule<IncidEditAc> doIntentRule()
    {
        return new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {
            @Override
            protected Intent getActivityIntent()
            {
                return getIntentPepeJuanRealNoPower();
            }
        };
    }

    @Override
    protected CleanUserEnum whatToClean()
    {
        return CLEAN_JUAN_AND_PEPE;
    }

//    ============================  TESTS  ===================================

    @Test
    public void testOnData_1() throws Exception
    {
        // CASO: presionamos TextView para ver la importancia dada por otros miembros de la comunidad.
        onView(withId(R.id.incid_importancia_otros_view)).check(matches(isDisplayed())).perform(click());

        // Verificamos layout.
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_see_usercomu_importancia_frg)).check(matches(isDisplayed()));

        /* Datos a la vista: lista con 2 oldIncidImportancia.*/
        onView(withId(android.R.id.list)).check(matches(isDisplayed()));
        onView(withId(android.R.id.empty)).check(matches(not(isDisplayed())));

        onView(allOf(
                withId(R.id.incid_importancia_alias_view),
                withText(incidenciaJuan.getUserComu().getUsuario().getAlias()),
                hasSibling(allOf(
                        withId(R.id.incid_importancia_rating_view),
                        withText(mActivity.getResources().getStringArray(R.array.IncidImportanciaArray)[incidenciaJuan.getImportancia()])
                ))
        )).check(matches(isDisplayed()));
        onView(allOf(
                withId(R.id.incid_importancia_alias_view),
                withText(pepeUserComu.getUsuario().getAlias()),
                hasSibling(allOf(
                        withId(R.id.incid_importancia_rating_view),
                        withText(R.string.no_sabe)
                ))
        )).check(matches(isDisplayed()));
    }

    @Test
    public void testPressBack()
    {
        // CASO: presionamos TextView para ver la importancia dada por otros miembros, y luego hacemos BACK.
        onView(withId(R.id.incid_importancia_otros_view)).check(matches(isDisplayed())).perform(click());
        // BACK.
        onView(withId(R.id.incid_see_usercomu_importancia_frg)).check(matches(isDisplayed())).perform(pressBack());
        // Datos a la vista.
        IncidUiUtils.checkDataEditMinFr(dbHelper, mActivity, incidenciaJuan);
    }

    @Test
    public void testUpNavigate()
    {
        // CASO: presionamos TextView para ver la importancia dada por otros miembros, y luego Up (Volver).

        onView(withId(R.id.incid_importancia_otros_view)).check(matches(isDisplayed())).perform(click());
        // Up Navigate.
        clickNavigateUp();
        // Datos a la vista.
        IncidUiUtils.checkDataEditMinFr(dbHelper, mActivity, incidenciaJuan);
    }
}