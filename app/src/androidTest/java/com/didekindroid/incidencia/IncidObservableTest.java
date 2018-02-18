package com.didekindroid.incidencia;

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.model.incidencia.dominio.ImportanciaUser;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.incidencia.dominio.Resolucion;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import io.reactivex.functions.Consumer;
import io.reactivex.observers.TestObserver;

import static com.didekindroid.incidencia.IncidObservable.incidImportanciaByUsers;
import static com.didekindroid.incidencia.IncidObservable.incidImportanciaModified;
import static com.didekindroid.incidencia.IncidObservable.incidImportanciaRegistered;
import static com.didekindroid.incidencia.IncidObservable.incidenciaDeleted;
import static com.didekindroid.incidencia.IncidObservable.resolucion;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.doIncidencia;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetDefaultResolucion;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidenciaUser;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.makeRegGetIncidImportancia;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpAndUpdateTk;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
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
            pepeUserComu = userComuDao.seeUserComusByUser().get(0);
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
        incidImportanciaRegistered(
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
    public void test_IncidImportanciaByUsers() throws Exception
    {
        final IncidImportancia incidImportancia = makeRegGetIncidImportancia(pepeUserComu, (short) 1);
        incidImportanciaByUsers(incidImportancia.getIncidencia().getIncidenciaId()).test().assertOf(new Consumer<TestObserver<List<ImportanciaUser>>>() {
            @Override
            public void accept(TestObserver<List<ImportanciaUser>> listTestObserver) throws Exception
            {
                List<ImportanciaUser> list = listTestObserver.values().get(0);
                assertThat(list.size(), is(1));
                assertThat(list.get(0).getImportancia(), is(incidImportancia.getImportancia()));
            }
        });
    }

    @Test
    public void testSeeResolucion() throws InterruptedException, UiException
    {
        Resolucion resolucion = insertGetDefaultResolucion(pepeUserComu);
        resolucion(resolucion.getIncidencia().getIncidenciaId()).test().assertResult(resolucion);
    }

    @Test
    public void testSeeResolucion_NULL() throws Exception
    {
        IncidenciaUser incidenciaUser = insertGetIncidenciaUser(pepeUserComu, 3);
        resolucion(incidenciaUser.getIncidencia().getIncidenciaId()).test().assertComplete();
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