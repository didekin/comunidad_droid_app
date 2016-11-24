package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekinaar.exception.UiAarException;
import com.didekinaar.testutil.CleanUserEnum;
import com.didekindroid.R;
import com.didekindroid.incidencia.exception.UiAppException;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekin.usuariocomunidad.Rol.PRESIDENTE;
import static com.didekinaar.testutil.AarActivityTestUtils.updateSecurityData;
import static com.didekinaar.testutil.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekinaar.testutil.UsuarioTestUtils.COMU_ESCORIAL_JUAN;
import static com.didekinaar.testutil.UsuarioTestUtils.USER_JUAN;
import static com.didekinaar.testutil.UsuarioTestUtils.USER_PEPE;
import static com.didekinaar.usuariocomunidad.AarUserComuService.AarUserComuServ;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.insertGetResolucionNoAdvances;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 14/03/16
 * Time: 17:49
 */
@RunWith(AndroidJUnit4.class)
public class IncidResolucionSeeFrTest_1 extends IncidResolucionAbstractTest {

    @Override
    IntentsTestRule<IncidResolucionRegEditSeeAc> doIntentRule()
    {
        return new IntentsTestRule<IncidResolucionRegEditSeeAc>(IncidResolucionRegEditSeeAc.class) {

            /**
             * Preconditions:
             * 1. A user WITHOUT 'adm' powers.
             * 2. A resolucion in BD and intent.
             * 3. Resolucion without avances.
             * */
            @Override
            protected Intent getActivityIntent()
            {
                try {
                    incidImportancia = insertGetIncidImportancia(COMU_ESCORIAL_JUAN);
                    Thread.sleep(1000);
                    // Necesitamos usuario con 'adm' para registrar resolución.
                    assertThat(AarUserComuServ.regUserAndUserComu(
                            new UsuarioComunidad.UserComuBuilder(
                                    incidImportancia.getUserComu().getComunidad(), USER_PEPE).roles(PRESIDENTE.function)
                                    .build())
                            .execute().body(),
                            is(true));
                    updateSecurityData(USER_PEPE.getUserName(), USER_PEPE.getPassword());
                    // Registramos resolución.
                    resolucion = insertGetResolucionNoAdvances(incidImportancia);
                    // Volvemos a usuario del test.
                    updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
                } catch (UiAppException | InterruptedException | IOException | UiAarException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia);
                intent.putExtra(INCID_RESOLUCION_OBJECT.key, resolucion);
                return intent;
            }
        };
    }

    @Override
    CleanUserEnum whatToClean()
    {
        return CLEAN_JUAN_AND_PEPE;
    }

    /*    ============================  TESTS  ===================================*/

    @Test
    public void testOnCreate_1() throws Exception
    {
        checkScreenResolucionSeeFr();
        checkDataResolucionSeeFr();
        // Avances.
        onView(allOf(
                withId(android.R.id.empty),
                withText(R.string.incid_resolucion_no_avances_message)
        )).check(matches(isDisplayed()));
    }
}
