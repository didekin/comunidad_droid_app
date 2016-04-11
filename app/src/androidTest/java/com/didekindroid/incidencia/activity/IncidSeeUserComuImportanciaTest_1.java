package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.incidservice.dominio.ImportanciaUser;
import com.didekindroid.R;
import com.didekindroid.usuario.testutils.CleanUserEnum;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.common.activity.BundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.common.activity.FragmentTags.incid_see_usercomus_importancia_fr_tag;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkNavigateUp;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * User: pedro@didekin
 * Date: 19/01/16
 * Time: 16:57
 */

/**
 * Tests con el fragmento IncidImportanciaSeeByUserFr.
 * Dos incidImportancias registradas en BD, para la misma incidencia.
 * Usuario inicial en sesión SIN permisos para modificar o borrar una incidencia.
 */
@RunWith(AndroidJUnit4.class)
public class IncidSeeUserComuImportanciaTest_1 extends IncidEditAbstractTest {

    @Override
    IntentsTestRule<IncidEditAc> doIntentRule()
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
    CleanUserEnum whatToClean()
    {
        return CLEAN_JUAN_AND_PEPE;
    }

//    ============================  TESTS  ===================================

    @Test
    public void testOnData_1() throws Exception
    {
        // CASO: presionamos TextView para ver la importancia dada por otros miembros de la comunidad.
        onView(withId(R.id.incid_importancia_otros_view)).check(matches(isDisplayed())).perform(click());

        // Verificamos nuevo fragmento y sus argumentos.
        IncidSeeUserComuImportanciaFr seeByUsersFr = (IncidSeeUserComuImportanciaFr) mActivity.getSupportFragmentManager()
                .findFragmentByTag(incid_see_usercomus_importancia_fr_tag);
        assertThat(seeByUsersFr, notNullValue());
        assertThat(seeByUsersFr.getArguments(), hasEntry(INCIDENCIA_OBJECT.key, is(incidenciaJuan.getIncidencia())));

        // Datos a la vista: lista con 2 incidImportancia.
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_see_usercomu_importancia_layout)).check(matches(isDisplayed()));
        onView(withId(android.R.id.list)).check(matches(isDisplayed()));
        onView(withId(android.R.id.empty)).check(matches(not(isDisplayed())));

        IncidImportanciaSeeAdapter importanciaSeeAdapter = seeByUsersFr.mAdapter;
        assertThat(importanciaSeeAdapter.getCount(), is(2));
        ImportanciaUser importanciaUser_0 = importanciaSeeAdapter.getItem(0);
        ImportanciaUser importanciaUser_1 = importanciaSeeAdapter.getItem(1);
        onData(is(importanciaUser_0)).inAdapterView(withId(android.R.id.list)).check(matches(isDisplayed()));
        onData(is(importanciaUser_1)).inAdapterView(withId(android.R.id.list)).check(matches(isDisplayed()));

        onView(allOf(
                withId(R.id.incid_importancia_alias_view),
                withText(pepeUserComu.getUsuario().getAlias()),
                hasSibling(allOf(
                        withId(R.id.incid_importancia_rating_view),
                        withText("")
                ))
        )).check(matches(isDisplayed()));
    }

    @Test
    public void testPressBack()
    {
        // CASO: presionamos TextView para ver la importancia dada por otros miembros, y luego hacemos BACK.
        onView(withId(R.id.incid_importancia_otros_view)).check(matches(isDisplayed())).perform(click());
        // BACK.
        onView(withId(R.id.incid_see_usercomu_importancia_layout)).check(matches(isDisplayed())).perform(pressBack());
        // Datos a la vista.
        checkDataEditNoPowerFr();
    }

    @Test
    public void testUpNavigate()
    {
        // CASO: presionamos TextView para ver la importancia dada por otros miembros, y luego Up (Volver).

        onView(withId(R.id.incid_importancia_otros_view)).check(matches(isDisplayed())).perform(click());
        // Up Navigate.
        checkNavigateUp();
        // Datos a la vista.
        checkDataEditNoPowerFr();
    }
}