package com.didekindroid.incidencia;

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.exception.UiException;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Resolucion;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.didekindroid.incidencia.IncidObservable.incidImportanciaModified;
import static com.didekindroid.incidencia.IncidObservable.incidenciaDeleted;
import static com.didekindroid.incidencia.IncidObservable.incidenciaRegistered;
import static com.didekindroid.incidencia.IncidObservable.resolucion;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.doIncidencia;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetDefaultResolucion;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidenciaUser;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 05/04/17
 * Time: 18:09
 */
@RunWith(AndroidJUnit4.class)
public class IncidObservableTest {

    UsuarioComunidad pepeUserComu;

    @Before
    public void setUp() throws Exception
    {
        try {
            signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
            pepeUserComu = userComuDaoRemote.seeUserComusByUser().get(0);
        } catch (IOException | UiException e) {
            fail();
        }
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public void tesIncidenciaRegistered() throws Exception
    {
        incidenciaRegistered(
                doIncidImportancia()
        ).test().assertResult(2);
    }

    @Test
    public void testIncidImportanciaModified() throws Exception
    {
        incidImportanciaModified(
                new IncidImportancia.IncidImportanciaBuilder(insertGetIncidenciaUser(pepeUserComu, 1).getIncidencia())
                        .importancia((short) 3) // Modificamos importancia de 1 a 3.
                        .usuarioComunidad(pepeUserComu)
                        .build()
        ).test().assertResult(2);
    }

    @Test
    public void testIncidImportanciaDeleted() throws Exception
    {
        incidenciaDeleted(insertGetIncidenciaUser(pepeUserComu, 1).getIncidencia()).test().assertResult(1);
    }

    @Test
    public void testSeeResolucion() throws InterruptedException, UiException
    {
        Resolucion resolucion = insertGetDefaultResolucion(pepeUserComu);
        resolucion(resolucion.getIncidencia().getIncidenciaId()).test().assertResult(resolucion);
    }

    // =================================== HELPERS ====================================

    @NonNull
    private IncidImportancia doIncidImportancia()
    {
        return new IncidImportancia.IncidImportanciaBuilder(
                doIncidencia(pepeUserComu.getUsuario().getUserName(), "Incidencia One", pepeUserComu.getComunidad().getC_Id(), (short) 43)
        ).usuarioComunidad(pepeUserComu)
                .importancia((short) 3)
                .build();
    }
}