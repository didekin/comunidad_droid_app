package com.didekindroid.incidencia;

import android.os.Bundle;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum;
import com.didekinlib.model.incidencia.dominio.AmbitoIncidencia;
import com.didekinlib.model.incidencia.dominio.Avance;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidComment;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.incidencia.dominio.Resolucion;
import com.didekinlib.model.usuario.Usuario;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.didekindroid.incidencia.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.incidencia.IncidenciaDao.incidenciaDao;
import static com.didekindroid.incidencia.testutils.IncidTestData.AVANCE_DEFAULT_DES;
import static com.didekindroid.incidencia.testutils.IncidTestData.INCID_DEFAULT_DESC;
import static com.didekindroid.incidencia.testutils.IncidTestData.doComment;
import static com.didekindroid.incidencia.testutils.IncidTestData.doIncidencia;
import static com.didekindroid.incidencia.testutils.IncidTestData.doResolucion;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetDefaultResolucion;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetIncidenciaUser;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.regComuUserUserComuGetUser;
import static com.didekindroid.lib_one.util.UiUtil.getMilliSecondsFromCalendarAdd;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_ESCORIAL_PEPE;
import static com.didekinlib.http.incidencia.IncidenciaExceptionMsg.INCIDENCIA_NOT_FOUND;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.getInstance;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 18/11/15
 * Time: 14:53
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class IncidenciaDaoTest {

    private CleanUserEnum whatClean = CLEAN_PEPE;
    private Usuario pepe;
    private UsuarioComunidad pepeUserComu;

    @Before
    public void setUp() throws Exception
    {
        pepe = regComuUserUserComuGetUser(COMU_ESCORIAL_PEPE);
        pepeUserComu = userComuDao.seeUserComusByUser().blockingGet().get(0);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(whatClean);
    }

    @Test
    public void testCloseIncidencia()
    {
        // CASO OK: cerramos incidencia con resolución sin modificar y avance en blanco.
        // Precondiditions.
        Resolucion resolucion = insertGetDefaultResolucion(pepeUserComu);
        resolucion = new Resolucion.ResolucionBuilder(resolucion.getIncidencia())
                .copyResolucion(resolucion)
                .build();

        assertThat(incidenciaDao.closeIncidencia(resolucion).blockingGet(), is(2)); // Accede a 2 tablas.
        incidenciaDao.closeIncidencia(resolucion).test()
                .assertError(exception -> UiException.class.cast(exception).getErrorHtppMsg().equals(INCIDENCIA_NOT_FOUND.getHttpMessage()));
    }

    @Test
    public void testDeleteIncidencia()
    {
        // Caso OK: existe la incidencia; la borrarmos.
        Incidencia incidencia = insertGetIncidenciaUser(pepeUserComu, 1).getIncidencia();
        assertThat(incidenciaDao.deleteIncidencia(incidencia.getIncidenciaId()).blockingGet(), is(1));
    }

    @Test
    public void testModifyIncidImportancia()
    {
        Incidencia incidenciaDb = insertGetIncidenciaUser(pepeUserComu, 3).getIncidencia();

        // Caso OK: usuario iniciador. Modificamos incidencia e importancia.
        IncidImportancia pepeIncidImportancia = new IncidImportancia.IncidImportanciaBuilder(
                new Incidencia.IncidenciaBuilder()
                        .copyIncidencia(incidenciaDb)
                        .descripcion("modified_desc")
                        .ambitoIncid(new AmbitoIncidencia((short) 11))
                        .build())
                .usuarioComunidad(pepeUserComu)
                .importancia((short) 2)
                .build();
        // Returns 2.
        assertThat(incidenciaDao.modifyIncidImportancia(pepeIncidImportancia).blockingGet(), is(2));
    }

    @Test
    public void testModifyResolucion()
    {
        // Caso OK: modificamos resolución sin avances previos.

        Resolucion resolucion = insertGetDefaultResolucion(pepeUserComu);
        assertThat(resolucion.getAvances().size(), is(0));
        // Nuevos datos.
        Avance avance = new Avance.AvanceBuilder().avanceDesc(AVANCE_DEFAULT_DES)
                .author(new Usuario.UsuarioBuilder().userName(resolucion.getUserName()).build())
                .build();
        List<Avance> avances = new ArrayList<>(1);
        avances.add(avance);
        resolucion = new Resolucion.ResolucionBuilder(resolucion.getIncidencia())
                .copyResolucion(resolucion)
                .descripcion("new_resolucion_1")
                .avances(avances)
                .build();
        assertThat(incidenciaDao.modifyResolucion(resolucion).blockingGet(), is(2));
    }

    @Test
    public void testRegIncidComment()
    {
        // Caso OK.
        Incidencia incidencia = insertGetIncidenciaUser(pepeUserComu, 1).getIncidencia();
        assertThat(incidenciaDao.regIncidComment(doComment("Comment_DESC", incidencia)).blockingGet(), is(1));
    }

    @Test
    public void testRegIncidImportancia()
    {
        /* Caso OK.*/
        assertThat(pepeUserComu, notNullValue());
        IncidImportancia incidPepe =
                new IncidImportancia.IncidImportanciaBuilder(doIncidencia(pepe.getUserName(), "Incidencia One", pepeUserComu.getComunidad().getC_Id(), (short) 43))
                        .usuarioComunidad(pepeUserComu)
                        .importancia((short) 3)
                        .build();
        assertThat(incidenciaDao.regIncidImportancia(incidPepe).blockingGet(), is(2));
    }

    @Test
    public void testRegResolucion()
    {
        // Caso OK.
        Incidencia incidencia = insertGetIncidenciaUser(pepeUserComu, 2).getIncidencia();
        Resolucion resolucion = doResolucion(
                incidencia, "resol_desc", 1000,
                new Timestamp(getMilliSecondsFromCalendarAdd(SECOND, 30))
        );
        assertThat(incidenciaDao.regResolucion(resolucion).blockingGet(), is(1));
    }

    @Test
    public void testSeeCommentsByIncid_1()
    {
        // Caso OK.
        Incidencia incid_pepeComu_1 = insertGetIncidenciaUser(pepeUserComu, 1).getIncidencia();
        assertThat(incidenciaDao.regIncidComment(doComment("Comment_1_pepeComu_1", incid_pepeComu_1)).blockingGet(), is(1));

        List<IncidComment> comments = incidenciaDao.seeCommentsByIncid(incid_pepeComu_1.getIncidenciaId()).blockingGet();
        assertThat(comments.size(), is(1));
    }

    @Test
    public void testSeeCommentsByIncid_2()
    {
        // Incidencia existe; no tiene asociada comentarios.
        Incidencia incid_pepeComu_1 = insertGetIncidenciaUser(pepeUserComu, 1).getIncidencia();
        List<IncidComment> comments = incidenciaDao.seeCommentsByIncid(incid_pepeComu_1.getIncidenciaId()).blockingGet();
        assertThat(comments, notNullValue());
        assertThat(comments.size(), is(0));
    }

    @Test
    public void test_SeeIncidImportancia()
    {
        // Caso OK.
        Incidencia incidencia = insertGetIncidenciaUser(pepeUserComu, 1).getIncidencia();
        IncidAndResolBundle incidResolBundle = incidenciaDao.seeIncidImportanciaRaw(incidencia.getIncidenciaId())
                .blockingGet();
        assertThat(incidResolBundle.getIncidImportancia().isIniciadorIncidencia(), is(true));

        Bundle bundle = incidenciaDao.seeIncidImportancia(incidencia.getIncidenciaId())
                .blockingGet();
        assertThat(bundle.getSerializable(INCID_RESOLUCION_BUNDLE.key), is(incidResolBundle));
    }

    @Test
    public void testSeeIncidsClosedByComu()
    {
        // CASO OK: usuario 'adm'.
        Resolucion resolucion = insertGetDefaultResolucion(pepeUserComu);
        assertThat(incidenciaDao.closeIncidencia(resolucion).blockingGet(), is(2));
        List<IncidenciaUser> incidenciaUsers =
                incidenciaDao.seeIncidsClosedByComu(pepeUserComu.getComunidad().getC_Id()).blockingGet();
        assertThat(incidenciaUsers.size(), is(1));
        assertThat(incidenciaUsers.get(0).getIncidencia(), is(resolucion.getIncidencia()));
    }

    @Test
    public void tesSeeIncidsOpenByComu()
    {
        IncidImportancia incidPepe = new IncidImportancia.IncidImportanciaBuilder(
                doIncidencia(pepe.getUserName(), INCID_DEFAULT_DESC, pepeUserComu.getComunidad().getC_Id(), (short) 43))
                .usuarioComunidad(pepeUserComu)
                .importancia((short) 3)
                .build();
        assertThat(incidenciaDao.regIncidImportancia(incidPepe).blockingGet(), is(2));

        assertThat(incidenciaDao.seeIncidsOpenByComu(pepeUserComu.getComunidad().getC_Id()).blockingGet().size(), is(1));
    }

    @Test
    public void testSeeResolucionRaw()       // TODO: fail.
    {
        // Caso OK.
        Resolucion resolucion = insertGetDefaultResolucion(pepeUserComu); // Implicitly tested calling this utilities method.
        assertThat(resolucion.getFechaPrev().getTime() > getInstance().getTimeInMillis(), is(true));
    }

    @Test
    public void testSeeResolucionRaw_empty()
    {
        // Caso OK.
        Incidencia incidencia = insertGetIncidenciaUser(pepeUserComu, 1).getIncidencia();
        assertThat(incidenciaDao.seeResolucionRaw(incidencia.getIncidenciaId()).blockingGet(), nullValue());
    }

    @Test
    public void testSeeResolucionInBundle()
    {
        // Caso OK.
        Resolucion resolucion = insertGetDefaultResolucion(pepeUserComu);
        assertThat(incidenciaDao.seeResolucionInBundle(resolucion.getIncidencia().getIncidenciaId())
                        .blockingGet().getSerializable(INCID_RESOLUCION_OBJECT.key),
                is(resolucion));
    }

    @Test
    public void testSeeUserComusImportancia()
    {
        // Caso OK.
        Incidencia incidencia = insertGetIncidenciaUser(pepeUserComu, 1).getIncidencia();
        assertThat(incidenciaDao.seeUserComusImportancia(incidencia.getIncidenciaId()).blockingGet().size(), is(1));
    }
}
