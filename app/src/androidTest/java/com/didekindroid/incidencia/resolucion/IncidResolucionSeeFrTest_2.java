package com.didekindroid.incidencia.resolucion;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.testutils.IncidEspressoTestUtils;
import com.didekindroid.usuario.testutil.UsuarioDataTestUtils;
import com.didekinlib.model.incidencia.dominio.Avance;
import com.didekinlib.model.incidencia.dominio.Resolucion;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

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
import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkDataResolucionSeeFr;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkScreenResolucionSeeFr;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetResolucionNoAdvances;
import static com.didekindroid.security.SecurityTestUtils.updateSecurityData;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuariocomunidad.repository.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_JUAN;
import static com.didekindroid.util.UIutils.formatTimeStampToString;
import static com.didekinlib.model.usuariocomunidad.Rol.PRESIDENTE;
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
                    assertThat(userComuDaoRemote.regUserAndUserComu(
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
                    assertThat(incidenciaDao.modifyResolucion(resolucion), is(2));
                    resolucion = incidenciaDao.seeResolucion(resolucion.getIncidencia().getIncidenciaId());
                    // Volvemos a usuario del test.
                    updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
                } catch (InterruptedException | IOException | UiException e) {
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
        checkScreenResolucionSeeFr(resolucionIntent);
        checkDataResolucionSeeFr(resolucion);

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
                        withText(USER_PEPE.getAlias()) // usuario en sesión que modifica resolución.
                )))).check(matches(isDisplayed()));
    }
}
