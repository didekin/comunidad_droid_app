package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.didekin.incidencia.dominio.IncidAndResolBundle;
import com.didekin.incidencia.dominio.IncidImportancia;
import com.didekin.incidencia.dominio.Incidencia;
import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.testutil.UsuarioDataTestUtils;
import com.didekindroid.R;

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
import static com.didekindroid.testutil.ActivityTestUtils.checkNoToastInTest;
import static com.didekindroid.testutil.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.incidencia.IncidService.IncidenciaServ;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.INCID_DEFAULT_DESC;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.doIncidencia;
import static com.didekindroid.usuariocomunidad.UserComuService.AppUserComuServ;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;

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
                    UsuarioComunidad pepeEscorial = AppUserComuServ.seeUserComusByUser().get(0);
                    IncidImportancia incidPepeEscorial = new IncidImportancia.IncidImportanciaBuilder(
                            doIncidencia(pepeEscorial.getUsuario().getUserName(), INCID_DEFAULT_DESC, pepeEscorial.getComunidad().getC_Id(), (short) 43))
                            .usuarioComunidad(pepeEscorial)
                            .importancia((short) 3)
                            .build();
                    IncidenciaServ.regIncidImportancia(incidPepeEscorial);
                    Incidencia incidenciaDb = IncidenciaServ.seeIncidsOpenByComu(pepeEscorial.getComunidad().getC_Id()).get(0).getIncidencia();
                    incidResolBundlePepe = IncidenciaServ.seeIncidImportancia(incidenciaDb.getIncidenciaId());
                    incidenciaPepe = incidResolBundlePepe.getIncidImportancia();
                } catch ( IOException | UiException e) {
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
    UsuarioDataTestUtils.CleanUserEnum whatToClean()
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
        Thread.sleep(2000);
    }
}