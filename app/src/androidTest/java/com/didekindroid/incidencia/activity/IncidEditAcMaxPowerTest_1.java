package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.Incidencia;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.incidencia.repository.IncidenciaDataDbHelper;
import com.didekindroid.usuario.testutils.CleanUserEnum;

import org.hamcrest.core.AllOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.CursorMatchers.withRowString;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.common.activity.BundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.common.activity.BundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doIncidencia;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_REAL_JUAN;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

/**
 * User: pedro@didekin
 * Date: 19/01/16
 * Time: 16:57
 */

/**
 * Tests genéricos sobre aspecto y tests funcionales para un userComu CON permisos para modificar, pero NO borrar una incidencia.
 */
@SuppressWarnings("UnnecessaryLocalVariable")
@RunWith(AndroidJUnit4.class)
public class IncidEditAcMaxPowerTest_1 extends IncidEditAbstractTest {

    private IncidenciaDataDbHelper dBHelper;

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        dBHelper = new IncidenciaDataDbHelper(mActivity);
    }

    @After
    public void tearDown() throws Exception
    {
        dBHelper.dropAllTables();
        dBHelper.close();
        super.tearDown();
    }

    @Override
    IntentsTestRule<IncidEditAc> doIntentRule()
    {
        return new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {
            /**
             * Preconditions:
             * 1. An fIncidenciaUser with powers to modify, but not to erase, is received.
             */
            @Override
            protected Intent getActivityIntent()
            {
                try {
                    signUpAndUpdateTk(COMU_REAL_JUAN);
                    juanUserComu = ServOne.seeUserComusByUser().get(0);
                    incidenciaJuan = new IncidImportancia.IncidImportanciaBuilder(
                            doIncidencia(juanUserComu.getUsuario().getUserName(), "Incidencia Real One", juanUserComu.getComunidad().getC_Id(), (short) 43))
                            .usuarioComunidad(juanUserComu)
                            .importancia((short) 3).build();
                    IncidenciaServ.regIncidImportancia(incidenciaJuan);
                    Incidencia incidenciaDb = IncidenciaServ.seeIncidsOpenByComu(juanUserComu.getComunidad().getC_Id()).get(0).getIncidencia();
                    incidenciaJuan = IncidenciaServ.seeIncidImportancia(incidenciaDb.getIncidenciaId()).getIncidImportancia();
                } catch (UiException | IOException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidenciaJuan);
                intent.putExtra(INCID_RESOLUCION_FLAG.key, false);
                return intent;
            }
        };
    }

    @Override
    CleanUserEnum whatToClean()
    {
        return CLEAN_JUAN;
    }

//    ============================  TESTS  ===================================

    @Test
    public void testOnCreate_1() throws Exception
    {
        checkScreenEditMaxPowerFr();
    }

    @Test
    public void testOnData_1()
    {
        checkDataEditMaxPowerFr(dBHelper);
    }

    @Test
    public void testModifyIncidencia_1()
    {
        // Cason NOT OK: descripción de incidencia no válida.
        onView(withId(R.id.incid_reg_desc_ed)).perform(replaceText("descripcion = not valid"));
        onView(withId(R.id.incid_edit_fr_modif_button)).perform(scrollTo(), click());
        checkToastInTest(R.string.error_validation_msg, mActivity, R.string.incid_reg_descripcion);
    }

    @Test
    public void testModifyIncidencia_2()
    {
        // Caso OK.
        onView(withId(R.id.incid_reg_importancia_spinner)).perform(click());
        onData
                (AllOf.allOf(
                                is(instanceOf(String.class)),
                                is(mActivity.getResources().getStringArray(R.array.IncidImportanciaArray)[4]))
                )
                .perform(click());
        onView(withId(R.id.incid_reg_ambito_spinner)).perform(click());
        onData(withRowString(1, "Calefacción comunitaria")).perform(click());
        onView(withId(R.id.incid_reg_desc_ed)).perform(replaceText("valid description"));

        onView(withId(R.id.incid_edit_fr_modif_button)).perform(scrollTo(), click());
        onView(withId(R.id.incid_see_open_by_comu_ac)).check(matches(isDisplayed()));
    }
}