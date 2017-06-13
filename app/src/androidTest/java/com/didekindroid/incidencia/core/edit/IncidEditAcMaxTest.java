package com.didekindroid.incidencia.core.edit;

import android.content.Intent;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.core.AmbitoIncidValueObj;
import com.didekindroid.incidencia.core.IncidenciaDataDbHelper;
import com.didekinlib.model.incidencia.dominio.AmbitoIncidencia;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkDataEditMaxPowerFr;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkScreenEditMaxPowerFr;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.doAmbitoAndDescripcion;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.doImportanciaSpinner;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.incidencia.utils.IncidFragmentTags.incid_edit_ac_frgs_tag;
import static com.didekindroid.testutil.ActivityTestUtils.checkBack;
import static com.didekindroid.testutil.ActivityTestUtils.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 17/03/16
 * Time: 15:51
 */
@RunWith(AndroidJUnit4.class)
public class IncidEditAcMaxTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    IncidEditAc activity;
    IncidImportancia incidImportancia;
    boolean flagResolucion;

    @Rule
    public IntentsTestRule<IncidEditAc> activityRule = new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            try {
                // Perfil adm, inicidador de la incidencia.
                incidImportancia = insertGetIncidImportancia(COMU_PLAZUELA5_JUAN);
                flagResolucion = false;
            } catch (IOException | UiException e) {
                fail();
            }

            Intent intent = new Intent();
            intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia);
            intent.putExtra(INCID_RESOLUCION_FLAG.key, flagResolucion);
            return intent;
        }
    };

    IncidenciaDataDbHelper dbHelper;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        dbHelper = new IncidenciaDataDbHelper(activity);
    }

    @After
    public void tearDown() throws Exception
    {
        dbHelper.close();
        cleanOptions(CLEAN_JUAN);
    }

//  ======================================= INTEGRATION TESTS  =====================================

    @Test
    public void testOnCreate_1() throws Exception
    {
        checkScreenEditMaxPowerFr(incidImportancia, flagResolucion);
        checkDataEditMaxPowerFr(dbHelper, activity, incidImportancia);
    }

    @Test
    public void testModifyIncidencia() throws InterruptedException
    {
        // Cason NOT OK: descripción de incidencia no válida.
        onView(withId(R.id.incid_reg_desc_ed)).perform(replaceText("descripcion = not valid"));
        onView(withId(R.id.incid_edit_fr_modif_button)).perform(scrollTo(), click());
        waitAtMost(2, SECONDS).until(isToastInView(R.string.error_validation_msg, activity, R.string.incid_reg_descripcion));
    }

    @Test
    public void testModifyIncidenciaPressUp() throws InterruptedException
    {
        // Caso OK. Modificamos: importancia, ámbito y descripción. Hacemos UP.
        short newImportancia = (short) 2;
        String newDesc = "descripcion es valida";
        doImportanciaSpinner(activity, newImportancia);
        AmbitoIncidValueObj ambitoObj = new AmbitoIncidValueObj((short) 10, "Calefacción comunitaria");
        doAmbitoAndDescripcion(ambitoObj, newDesc);

        onView(withId(R.id.incid_edit_fr_modif_button)).perform(scrollTo(), click());

        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(R.id.incid_see_open_by_comu_ac));
        checkUp();
        IncidImportancia newIncidImportancia = new IncidImportancia.IncidImportanciaBuilder
                (
                        new Incidencia.IncidenciaBuilder()
                                .copyIncidencia(incidImportancia.getIncidencia())
                                .descripcion(newDesc)
                                .ambitoIncid(new AmbitoIncidencia(ambitoObj.get_ID()))
                                .build()
                )
                .copyIncidImportancia(incidImportancia)
                .importancia(newImportancia)
                .build();
        checkScreenEditMaxPowerFr(newIncidImportancia, flagResolucion);
        checkDataEditMaxPowerFr(dbHelper, activity, newIncidImportancia);
    }

    @Test
    public void testModifyIncidenciaPressBack() throws InterruptedException
    {
        // Caso OK. No cambiamos nada. Hacemos BACK.
        onView(withId(R.id.incid_edit_fr_modif_button)).perform(scrollTo(), click());

        waitAtMost(2, SECONDS).until(isResourceIdDisplayed(R.id.incid_see_open_by_comu_ac));
        checkBack(onView(withId(R.id.incid_see_open_by_comu_ac)));
        checkScreenEditMaxPowerFr(incidImportancia, flagResolucion);
    }

    @Test
    public void testDeleteIncidenciaPressUp() throws InterruptedException
    {
        // CASO OK: borramos la incidencia.
        onView(withId(R.id.incid_edit_fr_borrar_button)).perform(scrollTo(), click());

        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(R.id.incid_see_open_by_comu_ac));
        checkUp();
        checkScreenEditMaxPowerFr(incidImportancia, flagResolucion);
        checkDataEditMaxPowerFr(dbHelper, activity, incidImportancia);
    }

    @Test
    public void testDeleteAndPressBack() throws InterruptedException
    {
        //CASO NOT OK: intentamos borrar una incidencia ya borrada, volviendo con back.
        onView(withId(R.id.incid_edit_fr_borrar_button)).perform(scrollTo(), click());

        // BACK y verificamos que hemos vuelto.
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(R.id.incid_see_open_by_comu_ac));
        checkBack(onView(withId(R.id.incid_see_open_by_comu_ac)));

        checkScreenEditMaxPowerFr(incidImportancia, flagResolucion);
        checkDataEditMaxPowerFr(dbHelper, activity, incidImportancia);

        // Intentamos borrar y verificamos error.
        onView(withId(R.id.incid_edit_fr_borrar_button)).check(matches(isDisplayed())).perform(click());
        waitAtMost(4, SECONDS).until(isToastInView(R.string.incidencia_wrong_init, activity));
    }

    //  ======================================== UNIT TESTS  =======================================

    @Test
    public void testOnCreate_2() throws Exception
    {
        IncidEditMaxFr fragment = (IncidEditMaxFr) activity.getSupportFragmentManager().findFragmentByTag(incid_edit_ac_frgs_tag);
        assertThat(fragment.viewerInjector, instanceOf(IncidEditAc.class));
        assertThat(fragment.viewer.getParentViewer(), CoreMatchers.<ViewerIf>is(activity.viewer));
    }

    @Test
    public void testOnSaveInstanceState()
    {
        activity.viewer = new ViewerIncidEditAc(activity) {
            @Override
            public void saveState(Bundle savedState)
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
            }

            @Override
            public int clearSubscriptions()  // It is called from onStop() and gives problems.
            {
                return 0;
            }
        };
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                InstrumentationRegistry.getInstrumentation().callActivityOnSaveInstanceState(activity, new Bundle(0));
            }
        });
        waitAtMost(1, SECONDS).untilAtomic(flagMethodExec, is(AFTER_METHOD_EXEC_A));
    }

    @Test
    public void testOnStop()
    {
        checkSubscriptionsOnStop(activity, activity.viewer.getController());
    }
}