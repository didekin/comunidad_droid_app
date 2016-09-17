package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.didekin.incidservice.dominio.IncidAndResolBundle;
import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.Incidencia;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.usuario.testutils.CleanUserEnum;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.common.activity.BundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.common.activity.BundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkNoToastInTest;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkUp;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.INCID_DEFAULT_DESC;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doIncidencia;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;

/**
 * User: pedro@didekin
 * Date: 17/03/16
 * Time: 15:51
 */
public class IncidEditAcMaxPowerTest_2 extends IncidEditAbstractTest {

    IncidAndResolBundle incidResolBundlePepe;

    @Override
    IntentsTestRule<IncidEditAc> doIntentRule()
    {
        return new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {
            /**
             * Preconditions:
             * 1. An IncidenciaUser with powers to modify and to erase is received.
             * 2. There isn't resolucion in BD.
             * Postconditions:
             * 1. Erase button is shown.
             * */
            @Override
            protected Intent getActivityIntent()
            {
                try {
                    signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
                    UsuarioComunidad pepeEscorial = ServOne.seeUserComusByUser().get(0);
                    IncidImportancia incidPepeEscorial = new IncidImportancia.IncidImportanciaBuilder(
                            doIncidencia(pepeEscorial.getUsuario().getUserName(), INCID_DEFAULT_DESC, pepeEscorial.getComunidad().getC_Id(), (short) 43))
                            .usuarioComunidad(pepeEscorial)
                            .importancia((short) 3)
                            .build();
                    IncidenciaServ.regIncidImportancia(incidPepeEscorial);
                    Incidencia incidenciaDb = IncidenciaServ.seeIncidsOpenByComu(pepeEscorial.getComunidad().getC_Id()).get(0).getIncidencia();
                    incidResolBundlePepe = IncidenciaServ.seeIncidImportancia(incidenciaDb.getIncidenciaId());
                    incidenciaPepe = incidResolBundlePepe.getIncidImportancia();
                } catch (UiException | IOException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidenciaPepe);
                intent.putExtra(INCID_RESOLUCION_FLAG.key, incidResolBundlePepe.hasResolucion());
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
    CleanUserEnum whatToClean()
    {
        return CLEAN_PEPE;
    }

//  ========================================  TESTS  =======================================

    @Test
    public void testDeleteIncidencia_1() throws InterruptedException
    {
        checkScreenEditMaxPowerFr();

        /* CASO OK: borramos la incidencia.*/
        onView(withId(R.id.incid_edit_fr_borrar_button)).perform(scrollTo(), click());
        // Verificamos que no ha habido error.
        checkNoToastInTest(R.string.incidencia_wrong_init,mActivity);
        onView(withId(R.id.incid_see_open_by_comu_ac)).check(matches(isDisplayed()));
        checkUp();
        checkScreenEditMaxPowerFr();
    }

    @Test
    public void testDeleteAndPressBack() throws InterruptedException
    {
        checkScreenEditMaxPowerFr();

        //CASO NOT OK: intentamos borrar una incidencia ya borrada, volviendo con back.
        onView(withId(R.id.incid_edit_fr_borrar_button)).perform(scrollTo(), click());
        onView(withId(R.id.incid_see_open_by_comu_ac)).check(matches(isDisplayed())).perform(pressBack());

        onView(withId(R.id.incid_edit_fr_borrar_button)).check(matches(isDisplayed())).perform(click());
        checkToastInTest(R.string.incidencia_wrong_init, mActivity);
        onView(withId(R.id.incid_see_open_by_comu_ac)).check(matches(isDisplayed()));
        Thread.sleep(3000);
    }
}