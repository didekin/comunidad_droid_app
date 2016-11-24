package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.incidencia.dominio.IncidenciaUser;
import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekinaar.exception.UiAarException;
import com.didekinaar.testutil.CleanUserEnum;
import com.didekindroid.R;
import com.didekindroid.incidencia.exception.UiAppException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekin.usuariocomunidad.Rol.PROPIETARIO;
import static com.didekinaar.testutil.AarActivityTestUtils.checkNoToastInTest;
import static com.didekinaar.testutil.AarActivityTestUtils.checkUp;
import static com.didekinaar.testutil.AarActivityTestUtils.signUpAndUpdateTk;
import static com.didekinaar.testutil.AarActivityTestUtils.updateSecurityData;
import static com.didekinaar.testutil.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekinaar.testutil.UsuarioTestUtils.COMU_REAL_PEPE;
import static com.didekinaar.testutil.UsuarioTestUtils.USER_JUAN;
import static com.didekinaar.testutil.UsuarioTestUtils.makeUsuarioComunidad;
import static com.didekinaar.usuariocomunidad.AarUserComuService.AarUserComuServ;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.insertGetIncidenciaUser;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

/**
 * User: pedro@didekin
 * Date: 19/01/16
 * Time: 16:57
 */

/**
 * Tests para un userComu sin registro IncidImportancia en la incidencia.
 */
@RunWith(AndroidJUnit4.class)
public class IncidEditAcNoPowerTest_2 extends IncidEditAbstractTest {

    @Override
    IntentsTestRule<IncidEditAc> doIntentRule()
    {
        return new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {

            /**
             * Preconditions:
             * 1. An incidImportancia.userComu WITHOUT powers to erase OR modify is passed.
             * 2. UserComu sin registro IncidImportancia en la incidencia.
             * */
            @Override
            protected Intent getActivityIntent()
            {
                try {
                    signUpAndUpdateTk(COMU_REAL_PEPE);
                    pepeUserComu = AarUserComuServ.seeUserComusByUser().get(0);
                    // Insertamos incidencia.
                    IncidenciaUser incidenciaUser_1 = insertGetIncidenciaUser(pepeUserComu, 1);
                    // Registro userComu en misma comunidad.
                    UsuarioComunidad userComuJuan = makeUsuarioComunidad(pepeUserComu.getComunidad(), USER_JUAN,
                            "portal", "esc", "plantaX", "door12", PROPIETARIO.function);
                    AarUserComuServ.regUserAndUserComu(userComuJuan).execute();
                    updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
                    incidenciaJuan = IncidenciaServ.seeIncidImportancia(incidenciaUser_1.getIncidencia().getIncidenciaId()).getIncidImportancia();
                } catch (UiAppException | IOException | UiAarException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidenciaJuan);
                intent.putExtra(INCID_RESOLUCION_FLAG.key, false);
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
        return CLEAN_JUAN_AND_PEPE;
    }

//    ============================  TESTS  ===================================

    @Test
    public void testOnCreate_1() throws Exception
    {
        checkScreenEditNoPowerFr();
    }

    @Test
    public void testOnData_1()
    {
        checkDataEditNoPowerFr();
    }

    @Test
    public void testModifyUser_1()
    {
        // Introducimos importancia.
        onView(withId(R.id.incid_reg_importancia_spinner)).perform(click());
        onData
                (allOf(
                                is(instanceOf(String.class)),
                                is(mActivity.getResources().getStringArray(R.array.IncidImportanciaArray)[4]))
                )
                .perform(click());

        onView(withId(R.id.incid_edit_fr_modif_button)).perform(scrollTo(), click());
        // Verificamos que no ha habido error.
        checkNoToastInTest(R.string.incidencia_wrong_init,mActivity);
        onView(withId(R.id.incid_see_open_by_comu_ac)).check(matches(isDisplayed()));

        checkUp();
        checkScreenEditNoPowerFr();
    }
}