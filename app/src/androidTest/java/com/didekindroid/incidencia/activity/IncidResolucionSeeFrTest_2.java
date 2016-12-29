package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.incidencia.dominio.Avance;
import com.didekin.incidencia.dominio.Resolucion;
import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekinaar.exception.UiException;
import com.didekinaar.usuario.testutil.UsuarioDataTestUtils;
import com.didekindroid.R;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekin.usuariocomunidad.Rol.PRESIDENTE;
import static com.didekinaar.testutil.AarTestUtil.updateSecurityData;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekinaar.utils.UIutils.formatTimeStampToString;
import static com.didekindroid.incidencia.IncidService.IncidenciaServ;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.insertGetResolucionNoAdvances;
import static com.didekindroid.usuariocomunidad.UserComuService.AppUserComuServ;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.COMU_ESCORIAL_JUAN;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 14/03/16
 * Time: 17:49
 */
@RunWith(AndroidJUnit4.class)
public class IncidResolucionSeeFrTest_2 extends IncidResolucionAbstractTest {

    @Override
    IntentsTestRule<IncidResolucionRegEditSeeAc> doIntentRule()
    {
        return new IntentsTestRule<IncidResolucionRegEditSeeAc>(IncidResolucionRegEditSeeAc.class) {
            /**
             * Preconditions:
             * 1. A user WITHOUT powers to edit a resolucion is received.
             * 2. A resolucion in BD and intent.
             * 3. Resolucion with avances.
             * */
            @Override
            protected Intent getActivityIntent()
            {
                try {
                    incidImportancia = insertGetIncidImportancia(COMU_ESCORIAL_JUAN);
                    Thread.sleep(1000);
                    // Necesitamos usuario con 'adm' para registrar resolución.
                    assertThat(AppUserComuServ.regUserAndUserComu(
                            new UsuarioComunidad.UserComuBuilder(
                                    incidImportancia.getUserComu().getComunidad(), USER_PEPE)
                                    .roles(PRESIDENTE.function)
                                    .build()).execute().body(),
                            is(true));
                    updateSecurityData(USER_PEPE.getUserName(), USER_PEPE.getPassword());
                    // Registramos resolución.
                    resolucion = insertGetResolucionNoAdvances(incidImportancia);
                    // Modificamos con avances.
                    Avance avance = new Avance.AvanceBuilder().avanceDesc("avance1_desc").build();
                    List<Avance> avances = new ArrayList<>(1);
                    avances.add(avance);
                    resolucion = new Resolucion.ResolucionBuilder(incidImportancia.getIncidencia())
                            .copyResolucion(resolucion)
                            .avances(avances)
                            .build();
                    assertThat(IncidenciaServ.modifyResolucion(resolucion), is(2));
                    resolucion = IncidenciaServ.seeResolucion(resolucion.getIncidencia().getIncidenciaId());
                    // Volvemos a usuario del test.
                    updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
                } catch ( InterruptedException | IOException | UiException e) {
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
    UsuarioDataTestUtils.CleanUserEnum whatToClean()
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
                        withText(USER_PEPE.getUserName()) // usuario en sesión que modifica resolución.
                )))).check(matches(isDisplayed()));
    }
}
