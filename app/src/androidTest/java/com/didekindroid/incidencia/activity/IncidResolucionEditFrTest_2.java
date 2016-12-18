package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.incidencia.dominio.Avance;
import com.didekin.incidencia.dominio.Resolucion;
import com.didekinaar.exception.UiException;
import com.didekinaar.testutil.AarActivityTestUtils;
import com.didekindroid.exception.UiAppException;
import com.didekindroid.R;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekinaar.testutil.AarActivityTestUtils.checkToastInTest;
import static com.didekinaar.testutil.AarActivityTestUtils.checkUp;
import static com.didekinaar.utils.UIutils.formatTimeStampToString;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.insertGetResolucionNoAdvances;
import static com.didekindroid.incidencia.IncidService.IncidenciaServ;
import static com.didekinaar.testutil.AarActivityTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.COMU_PLAZUELA5_JUAN;
import static com.didekinaar.usuario.testutil.UsuarioTestUtils.USER_JUAN;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 14/03/16
 * Time: 17:49
 */
@RunWith(AndroidJUnit4.class)
public class IncidResolucionEditFrTest_2 extends IncidResolucionAbstractTest {

    @Override
    IntentsTestRule<IncidResolucionRegEditSeeAc> doIntentRule()
    {
        return new IntentsTestRule<IncidResolucionRegEditSeeAc>(IncidResolucionRegEditSeeAc.class) {

            /**
             * Preconditions:
             * 1. A user WITH powers to edit a resolucion is received.
             * 2. A resolucion in BD and intent.
             * 3. Resolucion WITH avances.
             * 4. Incidencia is OPEN.
             * */
            @Override
            protected Intent getActivityIntent()
            {
                try {
                    incidImportancia = insertGetIncidImportancia(COMU_PLAZUELA5_JUAN);
                    // Registramos resolución.
                    Thread.sleep(1000);
                    resolucion = insertGetResolucionNoAdvances(incidImportancia);
                    // Modificamos resolución.
                    Avance avance = new Avance.AvanceBuilder().avanceDesc("avance1_desc").build();
                    List<Avance> avances = new ArrayList<>(1);
                    avances.add(avance);
                    resolucion = new Resolucion.ResolucionBuilder(incidImportancia.getIncidencia())
                            .copyResolucion(resolucion)
                            .avances(avances)
                            .build();
                    assertThat(IncidenciaServ.modifyResolucion(resolucion), is(2));
                    resolucion = IncidenciaServ.seeResolucion(resolucion.getIncidencia().getIncidenciaId());
                } catch (UiAppException | InterruptedException | IOException | UiException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia);
                intent.putExtra(INCID_RESOLUCION_OBJECT.key, resolucion);
                return intent;
            }
        };
    }

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(4000);
    }

    @Override
    AarActivityTestUtils.CleanUserEnum whatToClean()
    {
        return CLEAN_JUAN;
    }

    /*    ============================  TESTS  ===================================*/

    @Test
    public void testOnCreate_1() throws Exception
    {
        assertThat(mResolucionIntent.getAvances().size(), is(1));
        checkScreenResolucionEditFr();
    }

    @Test
    public void testOnData_1()
    {
        checkDataResolucionEditFr();
        // Avances.
        Avance avance = resolucion.getAvances().get(0);
        onData(is(avance)).inAdapterView(withId(android.R.id.list)).check(matches(isDisplayed()));
        onView(allOf(
                withText("avance1_desc"),
                withId(R.id.incid_avance_desc_view)
        )).check(matches(isDisplayed()));
        onView(allOf(
                withText(formatTimeStampToString(avance.getFechaAlta())),
                withId(R.id.incid_avance_fecha_view),
                hasSibling(allOf(
                        withId(R.id.incid_avance_aliasUser_view),
                        withText(USER_JUAN.getUserName()) // usuario en sesión que modifica resolución.
                )))).check(matches(isDisplayed()));
    }

    @Test
    public void testOnEdit_1() throws UiAppException, UiException
    {
        // Caso OK: añadimos un avance con descripción Ok .
        onView(withId(R.id.incid_resolucion_avance_ed)).perform(replaceText("avance2_desc_válida"));

        onView(withId(R.id.incid_resolucion_fr_modif_button)).perform(click());
        // Verificamos pantalla de llegada, intent y BD.
        onView(withId(R.id.incid_edit_fragment_container_ac)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_edit_maxpower_fr_layout)).check(matches(isDisplayed()));
        intended(hasExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia));
        Resolucion resolucionDb = IncidenciaServ.seeResolucion(resolucion.getIncidencia().getIncidenciaId());
        assertThat(resolucionDb.getAvances().size(), is(2));
        assertThat(resolucionDb.getAvances().get(1).getAvanceDesc(), is("avance2_desc_válida"));

        checkUp();
        checkScreenResolucionEditFr();
        checkDataResolucionEditFr();
    }

    @Test
    public void testCloseIncidenciaAndBack() throws InterruptedException
    {
        // Caso NOT OK: cerramos la incidencia, damos back y volvemos a intentar cerrarla.
        onView(withId(R.id.incid_resolucion_edit_fr_close_button)).perform(click());
        intended(not(hasExtraWithKey(INCID_IMPORTANCIA_OBJECT.key)));

        onView(withId(R.id.incid_see_closed_by_comu_ac)).check(matches(isDisplayed())).perform(pressBack());

        onView(withId(R.id.incid_resolucion_edit_fr_close_button)).perform(click());
        checkToastInTest(R.string.incidencia_wrong_init, mActivity);
        onView(withId(R.id.incid_see_open_by_comu_ac)).check(matches(isDisplayed()));

        Thread.sleep(2000);
    }

/*    ============================= HELPER METHODS ===========================*/
}
