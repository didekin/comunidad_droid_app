package com.didekindroid.incidencia.list.importancia;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 15/04/17
 * Time: 15:41
 */
public class IncidSeeUserComuImportanciaAcTest {

    IncidImportancia incidImportancia;
    @Rule
    public IntentsTestRule<IncidSeeUserComuImportanciaAc> activityRule = new IntentsTestRule<IncidSeeUserComuImportanciaAc>(IncidSeeUserComuImportanciaAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            try {
                // Perfil adm, inicidador de la incidencia.
                incidImportancia = insertGetIncidImportancia(COMU_PLAZUELA5_JUAN);
            } catch (IOException | UiException e) {
                fail();
            }

            Intent intent = new Intent();
            intent.putExtra(INCIDENCIA_OBJECT.key, incidImportancia.getIncidencia());
            return intent;
        }
    };
    IncidSeeUserComuImportanciaAc activity;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void testOnCreate() throws Exception
    {
        // Verificamos layout.
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_see_usercomu_importancia_frg)).check(matches(isDisplayed()));

        /* Datos a la vista: lista con 2 oldIncidImportancia.*/
        onView(withId(android.R.id.list)).check(matches(isDisplayed()));
        onView(withId(android.R.id.empty)).check(matches(not(isDisplayed())));

        onView(allOf(
                withId(R.id.incid_importancia_alias_view),
                withText(incidImportancia.getUserComu().getUsuario().getAlias()),
                hasSibling(allOf(
                        withId(R.id.incid_importancia_rating_view),
                        withText(activity.getResources().getStringArray(R.array.IncidImportanciaArray)[incidImportancia.getImportancia()])
                ))
        )).check(matches(isDisplayed()));
    }
}