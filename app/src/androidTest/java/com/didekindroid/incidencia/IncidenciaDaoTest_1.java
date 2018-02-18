package com.didekindroid.incidencia;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.usuario.UserTestData;
import com.didekinlib.model.incidencia.dominio.AmbitoIncidencia;
import com.didekinlib.model.incidencia.dominio.Avance;
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

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.didekindroid.incidencia.IncidenciaDao.incidenciaDao;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.AVANCE_DEFAULT_DES;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.INCID_DEFAULT_DESC;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.RESOLUCION_DEFAULT_DESC;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.doComment;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.doIncidencia;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.doIncidenciaWithId;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.doResolucion;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetDefaultResolucion;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidenciaUser;
import static com.didekindroid.lib_one.security.SecurityTestUtils.updateSecurityData;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.usuariocomunidad.UserComuMockDao.userComuMockDao;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_ESCORIAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.makeUserComuWithComunidadId;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpAndUpdateTk;
import static com.didekinlib.http.incidencia.IncidenciaExceptionMsg.INCIDENCIA_NOT_FOUND;
import static com.didekinlib.http.incidencia.IncidenciaExceptionMsg.RESOLUCION_DUPLICATE;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.USERCOMU_WRONG_INIT;
import static com.didekinlib.model.common.dominio.BeanBuilder.error_message_bean_building;
import static com.didekinlib.model.usuariocomunidad.Rol.PROPIETARIO;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 18/11/15
 * Time: 14:53
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class IncidenciaDaoTest_1 {

    UserTestData.CleanUserEnum whatClean;
    Usuario pepe;
    UsuarioComunidad pepeUserComu;

    @Before
    public void setUp() throws Exception
    {
        whatClean = CLEAN_PEPE;
        pepe = signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
        pepeUserComu = userComuDao.seeUserComusByUser().get(0);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(whatClean);
    }

    @Test
    public void testCloseIncidencia_1() throws InterruptedException, UiException
    {
        // CASO OK: cerramos incidencia con resolución sin modificar y avance en blanco.
        Resolucion resolucion = insertGetDefaultResolucion(pepeUserComu);
        Incidencia incidencia = incidenciaDao.seeIncidImportancia(resolucion.getIncidencia().getIncidenciaId()).getIncidImportancia().getIncidencia();
        assertThat(incidencia.getFechaCierre(), nullValue());
        // Nuevos datos.
        Avance avance = new Avance.AvanceBuilder().avanceDesc("")
                .author(new Usuario.UsuarioBuilder().userName(resolucion.getUserName()).build())
                .build();
        List<Avance> avances = new ArrayList<>(1);
        avances.add(avance);
        resolucion = new Resolucion.ResolucionBuilder(resolucion.getIncidencia())
                .copyResolucion(resolucion)
                .avances(avances)
                .build();

        assertThat(incidenciaDao.closeIncidencia(resolucion), is(2)); // Accede a 2 tablas.
        try {
            incidenciaDao.seeIncidImportancia(resolucion.getIncidencia().getIncidenciaId()).getIncidImportancia().getIncidencia();
            fail();
        } catch (UiException ue) {
            // La incidencia no se encuentra por la consulta de incidencias abiertas.
            assertThat(ue.getErrorBean().getMessage(), is(INCIDENCIA_NOT_FOUND.getHttpMessage()));
            // Sí está en las incidencias cerradas.
            assertThat(incidenciaDao.seeIncidsClosedByComu(incidencia.getComunidad().getC_Id()).size(), is(1));
        }
    }

    @Test
    public void testDeleteIncidencia_1() throws UiException
    {
        // Caso OK: existe la incidencia; la borrarmos.
        Incidencia incidencia = insertGetIncidenciaUser(pepeUserComu, 1).getIncidencia();
        assertThat(incidenciaDao.deleteIncidencia(incidencia.getIncidenciaId()), is(1));

        // Caso NOT OK: intentamos borrarla de nuevo: la app. redirige a la consulta de incidencias.
        try {
            incidenciaDao.deleteIncidencia(incidencia.getIncidenciaId());
            fail();
        } catch (UiException ue) {
            assertThat(ue.getErrorBean().getMessage(), is(INCIDENCIA_NOT_FOUND.getHttpMessage()));
        }
    }

    @Test
    public void testModifyIncidImportancia_1() throws UiException
    {
        Incidencia incidenciaDb = insertGetIncidenciaUser(pepeUserComu, 3).getIncidencia();

        // Caso OK: usuario iniciador. Modificamos incidencia e importancia.
        IncidImportancia pepeIncidImportancia = new IncidImportancia.IncidImportanciaBuilder(
                new Incidencia.IncidenciaBuilder().copyIncidencia(incidenciaDb).descripcion("modified_desc").ambitoIncid(new AmbitoIncidencia((short) 11)).build())
                .usuarioComunidad(pepeUserComu)
                .importancia((short) 2)
                .build();
        // Returns 2.
        assertThat(incidenciaDao.modifyIncidImportancia(pepeIncidImportancia), is(2));
        pepeIncidImportancia = incidenciaDao.seeIncidImportancia(incidenciaDb.getIncidenciaId()).getIncidImportancia();
        assertThat(pepeIncidImportancia.getImportancia(), is((short) 2));
        assertThat(pepeIncidImportancia.getIncidencia().getDescripcion(), is("modified_desc"));

        // Caso NOT OK: incidencia no existe en BD.
        // Cambiamos incidenciaId a una PK inexistente: excepción INCIDENCIA_NOT_FOUND.
        pepeIncidImportancia = new IncidImportancia.IncidImportanciaBuilder(
                new Incidencia.IncidenciaBuilder().copyIncidencia(incidenciaDb).incidenciaId(999L).descripcion("modified_desc").build())
                .usuarioComunidad(pepeUserComu)
                .importancia((short) 2)
                .build();
        try {
            incidenciaDao.modifyIncidImportancia(pepeIncidImportancia);
            fail();
        } catch (UiException u) {
            assertThat(u.getErrorBean().getMessage(), is(INCIDENCIA_NOT_FOUND.getHttpMessage()));
        }
    }

    @Test
    public void testModifyResolucion_1() throws InterruptedException, UiException
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
        assertThat(incidenciaDao.modifyResolucion(resolucion), is(2));
        assertThat(resolucion.getAvances().size(), is(1));
        assertThat(resolucion.getDescripcion(), is("new_resolucion_1"));
    }

    @Test
    public void testRegIncidComment_1() throws UiException
    {
        // Caso OK.
        Incidencia incidencia = insertGetIncidenciaUser(pepeUserComu, 1).getIncidencia();
        assertThat(incidenciaDao.regIncidComment(doComment("Comment_DESC", incidencia)), is(1));
    }

    @Test
    public void testRegIncidComment_2() throws IOException, UiException
    {
        whatClean = CLEAN_JUAN_AND_PEPE;

        // Caso EntityException: USERCOMU_WRONG_INIT.
        Incidencia incidencia = insertGetIncidenciaUser(pepeUserComu, 1).getIncidencia();

        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        try {
            incidenciaDao.regIncidComment(doComment("Comment_DESC", incidencia));
            fail();
        } catch (UiException ue) {
            assertThat(ue.getErrorBean().getMessage(), is(USERCOMU_WRONG_INIT.getHttpMessage()));
        }
    }

    @Test
    public void testRegIncidComment_3() throws UiException
    {
        // Caso: no existe incidencia.
        Incidencia incidencia = insertGetIncidenciaUser(pepeUserComu, 1).getIncidencia();
        IncidComment comment = doComment("Comment_DESC", new Incidencia.IncidenciaBuilder().copyIncidencia(incidencia).incidenciaId(999L).build());

        try {
            incidenciaDao.regIncidComment(comment);
            fail();
        } catch (UiException ue) {
            assertThat(ue.getErrorBean().getMessage(), is(INCIDENCIA_NOT_FOUND.getHttpMessage()));
        }
    }

    @Test
    public void testRegIncidImportancia_1() throws Exception
    {
        /* Caso OK.*/
        assertThat(pepeUserComu, notNullValue());
        IncidImportancia incidPepe = new IncidImportancia.IncidImportanciaBuilder(doIncidencia(pepe.getUserName(), "Incidencia One", pepeUserComu.getComunidad().getC_Id(), (short) 43))
                .usuarioComunidad(pepeUserComu)
                .importancia((short) 3)
                .build();
        assertThat(incidenciaDao.regIncidImportancia(incidPepe), is(2));
    }

    @Test
    public void testRegIncidImportancia_2()
    {
        // Caso: no existe la comunidad en la incidencia.
        Incidencia incidencia = doIncidencia(pepe.getUserName(), "incidencia sin Id en BD", 999L, (short) 11);

        try {
            new IncidImportancia.IncidImportanciaBuilder(incidencia)
                    .usuarioComunidad(pepeUserComu)
                    .importancia((short) 2)
                    .build();

            fail();
        } catch (IllegalStateException ie) {
            assertThat(ie.getMessage(), is(error_message_bean_building + IncidImportancia.IncidImportanciaBuilder.class.getName()));
        }
    }

    @Test
    public void testRegResolucion_1() throws UiException, InterruptedException
    {
        // Caso OK.
        Incidencia incidencia = insertGetIncidenciaUser(pepeUserComu, 2).getIncidencia();
        Thread.sleep(1000);
        Resolucion resolucion = doResolucion(incidencia, "resol_desc", 1000, new Timestamp(new Date().getTime()));
        assertThat(incidenciaDao.regResolucion(resolucion), is(1));
    }

    @Test
    public void testRegResolucion_2() throws InterruptedException, IOException, UiException
    {
        whatClean = CLEAN_JUAN_AND_PEPE;

        // Caso: usuarioComunidad no relacionado con comunidad de la incidencia.
        Incidencia incidencia = insertGetIncidenciaUser(pepeUserComu, 2).getIncidencia();
        Thread.sleep(1000);
        Resolucion resolucion = doResolucion(incidencia, "resol_desc", 1000, new Timestamp(new Date().getTime()));

        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        try {
            incidenciaDao.regResolucion(resolucion);
            fail();
        } catch (UiException ue) {
            assertThat(ue.getErrorBean().getMessage(), is(USERCOMU_WRONG_INIT.getHttpMessage()));
        }
    }

    @Test
    public void testRegResolucion_3()
    {
        // Caso: no existe la incidencia.
        Incidencia incidencia = doIncidenciaWithId(999L, "incid_noDB", pepeUserComu.getComunidad().getC_Id(), (short) 2);
        Resolucion resolucion = doResolucion(incidencia, "resol_desc", 1000, new Timestamp(new Date().getTime()));
        try {
            incidenciaDao.regResolucion(resolucion);
            fail();
        } catch (UiException ie) {
            assertThat(ie.getErrorBean().getMessage(), is(INCIDENCIA_NOT_FOUND.getHttpMessage()));
        }
    }

    @Test
    public void testRegResolucion_4() throws UiException, InterruptedException
    {
        // Caso: resolución duplicada.
        Incidencia incidencia = insertGetIncidenciaUser(pepeUserComu, 2).getIncidencia();
        Thread.sleep(1000);
        Resolucion resolucion = doResolucion(incidencia, "resol_desc", 1000, new Timestamp(new Date().getTime()));
        assertThat(incidenciaDao.regResolucion(resolucion), is(1));
        Thread.sleep(1000);

        try {
            incidenciaDao.regResolucion(resolucion);
            fail();
        } catch (UiException ue) {
            assertThat(ue.getErrorBean().getMessage(), is(RESOLUCION_DUPLICATE.getHttpMessage()));
        }
    }

    @Test
    public void testSeeCommentsByIncid_1() throws UiException
    {
        // Caso OK.
        Incidencia incid_pepeComu_1 = insertGetIncidenciaUser(pepeUserComu, 1).getIncidencia();
        assertThat(incidenciaDao.regIncidComment(doComment("Comment_1_pepeComu_1", incid_pepeComu_1)), is(1));

        List<IncidComment> comments = incidenciaDao.seeCommentsByIncid(incid_pepeComu_1.getIncidenciaId());
        assertThat(comments.size(), is(1));
        assertThat(incidenciaDao.regIncidComment(doComment("Comment_2_pepeComu_1", incid_pepeComu_1)), is(1));
        comments = incidenciaDao.seeCommentsByIncid(incid_pepeComu_1.getIncidenciaId());
        assertThat(comments.size(), is(2));
    }

    @Test
    public void testSeeCommentsByIncid_2() throws UiException
    {
        // Incidencia no existe.
        Incidencia incidencia = doIncidenciaWithId(999L, "incid_no_existe", pepeUserComu.getComunidad().getC_Id(), (short) 2);

        try {
            incidenciaDao.seeCommentsByIncid(incidencia.getIncidenciaId());
            fail();
        } catch (UiException ue) {
            assertThat(ue.getErrorBean().getMessage(), is(INCIDENCIA_NOT_FOUND.getHttpMessage()));
        }
    }

    @Test
    public void testSeeCommentsByIncid_3() throws UiException
    {
        // Incidencia existe; no tiene asociada comentarios.
        Incidencia incid_pepeComu_1 = insertGetIncidenciaUser(pepeUserComu, 1).getIncidencia();
        List<IncidComment> comments = incidenciaDao.seeCommentsByIncid(incid_pepeComu_1.getIncidenciaId());
        assertThat(comments, notNullValue());
        assertThat(comments.size(), is(0));
    }

    @Test
    public void testSeeCommentsByIncid_4() throws IOException, UiException
    {
        whatClean = CLEAN_JUAN_AND_PEPE;

        // El userComu no está asociado a la comunidad de la incidencia.
        Incidencia incid_pepeComu_1 = insertGetIncidenciaUser(pepeUserComu, 1).getIncidencia();
        assertThat(incidenciaDao.regIncidComment(doComment("Comment_1_pepeComu_1", incid_pepeComu_1)), is(1));

        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        try {
            incidenciaDao.seeCommentsByIncid(incid_pepeComu_1.getIncidenciaId());
            fail();
        } catch (UiException ue) {
            assertThat(ue.getErrorBean().getMessage(), is(USERCOMU_WRONG_INIT.getHttpMessage()));
        }
    }

    @Test
    public void testSeeIncidImportancia_1() throws UiException
    {
        // Caso OK.
        Incidencia incidencia = insertGetIncidenciaUser(pepeUserComu, 1).getIncidencia();
        IncidImportancia incidImportancia = incidenciaDao.seeIncidImportancia(incidencia.getIncidenciaId()).getIncidImportancia();
        assertThat(incidImportancia.isIniciadorIncidencia(), is(true));
    }

    @Test
    public void testSeeIncidImportancia_2() throws UiException
    {
        // Caso : incidencia no existe en BD.
        Incidencia incidencia = doIncidencia(pepe.getUserName(), "Incidencia One", pepeUserComu.getComunidad().getC_Id(), (short) 43);
        try {
            incidenciaDao.seeIncidImportancia(incidencia.getIncidenciaId());
            fail();
        } catch (UiException ue) {
            assertThat(ue.getErrorBean().getMessage(), is(INCIDENCIA_NOT_FOUND.getHttpMessage()));
        }
    }

    @Test
    public void testSeeIncidImportancia_3() throws IOException, UiException
    {
        whatClean = CLEAN_JUAN_AND_PEPE;

        /* Caso: usuario e incidencia en BD, pero incidencia.comunidad != usuario.comunidad.*/

        Incidencia incidencia = insertGetIncidenciaUser(pepeUserComu, 1).getIncidencia();
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        try {
            incidenciaDao.seeIncidImportancia(incidencia.getIncidenciaId());
            fail();
        } catch (UiException ue) {
            assertThat(ue.getErrorBean().getMessage(), is(USERCOMU_WRONG_INIT.getHttpMessage()));
        }
    }

    @Test
    public void testSeeIncidImportancia_4() throws IOException, UiException
    {
        whatClean = CLEAN_JUAN_AND_PEPE;

        /* Caso: no hay registro incidImportancia.userComu.usuario == usuario, SÍ usuario.comunidad == incidencia.comunidad.*/

        Incidencia incidencia = insertGetIncidenciaUser(pepeUserComu, 1).getIncidencia();
        userComuMockDao.regUserAndUserComu(makeUserComuWithComunidadId(COMU_ESCORIAL_JUAN, incidencia.getComunidad().getC_Id())).execute();
        updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());

        IncidImportancia incidImportancia = incidenciaDao.seeIncidImportancia(incidencia.getIncidenciaId()).getIncidImportancia();
        assertThat(incidImportancia.getIncidencia(), is(incidencia));
        assertThat(incidImportancia.getImportancia(), is((short) 0));
        assertThat(incidImportancia.getUserComu().getUsuario().getAlias(), is(USER_JUAN.getAlias()));
        assertThat(incidImportancia.getUserComu().getUsuario().getUserName(), is(USER_JUAN.getUserName()));
        assertThat(incidImportancia.getUserComu().getRoles(), is(PROPIETARIO.function));
        assertThat(incidImportancia.getUserComu().getUsuario().getuId() > 0L, is(true));
        assertThat(incidImportancia.getIncidencia().getComunidad(), is(incidencia.getComunidad()));
        assertThat(incidImportancia.getUserComu().getComunidad(), is(incidencia.getComunidad()));
    }

    @Test
    public void testSeeIncidsClosedByComu_1() throws InterruptedException, UiException
    {
        // CASO OK: usuario 'adm'.
        Resolucion resolucion = insertGetDefaultResolucion(pepeUserComu);
        assertThat(incidenciaDao.closeIncidencia(resolucion), is(2));
        List<IncidenciaUser> incidenciaUsers = incidenciaDao.seeIncidsClosedByComu(pepeUserComu.getComunidad().getC_Id());
        assertThat(incidenciaUsers.size(), is(1));
        assertThat(incidenciaUsers.get(0).getIncidencia(), is(resolucion.getIncidencia()));
    }

    @Test
    public void testSeeIncidsClosedByComu_2() throws InterruptedException, IOException, UiException
    {
        // CASO NOT OK: usuario no pertenece a la comunidad.

        whatClean = CLEAN_JUAN_AND_PEPE;

        Resolucion resolucion = insertGetDefaultResolucion(pepeUserComu);
        assertThat(incidenciaDao.closeIncidencia(resolucion), is(2));
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        try {
            incidenciaDao.seeIncidsClosedByComu(pepeUserComu.getComunidad().getC_Id());
            fail();
        } catch (UiException ue) {
            assertThat(ue.getErrorBean().getMessage(), is(USERCOMU_WRONG_INIT.getHttpMessage()));
        }
    }

    @Test
    public void tesSeeIncidsOpenByComu_1() throws UiException
    {
        IncidImportancia incidPepe = new IncidImportancia.IncidImportanciaBuilder(doIncidencia(pepe.getUserName(), INCID_DEFAULT_DESC, pepeUserComu.getComunidad().getC_Id(), (short) 43))
                .usuarioComunidad(pepeUserComu)
                .importancia((short) 3)
                .build();
        assertThat(incidenciaDao.regIncidImportancia(incidPepe), is(2));
        IncidImportancia incidPepe2 = new IncidImportancia.IncidImportanciaBuilder(doIncidencia(pepe.getUserName(), "Incidencia Two", pepeUserComu.getComunidad().getC_Id(), (short) 11))
                .usuarioComunidad(pepeUserComu)
                .importancia((short) 2)
                .build();
        assertThat(incidenciaDao.regIncidImportancia(incidPepe2), is(2));

        assertThat(incidenciaDao.seeIncidsOpenByComu(pepeUserComu.getComunidad().getC_Id()).size(), is(2));
    }

    @Test
    public void testSeeIncidsOpenByComu_2() throws UiException
    {
        // No existe la comunidad en BD: devuelve lista vacía.
        try {
            incidenciaDao.seeIncidsOpenByComu(999L);
            fail();
        } catch (UiException ue) {
            assertThat(ue.getErrorBean().getMessage(), is(USERCOMU_WRONG_INIT.getHttpMessage()));
        }
    }

    @Test
    public void testSeeResolucion_1() throws UiException, InterruptedException
    {
        // Caso OK.
        Resolucion resolucion = insertGetDefaultResolucion(pepeUserComu);
        assertThat(resolucion.getDescripcion(), is(RESOLUCION_DEFAULT_DESC));
        assertThat(resolucion.getFechaPrev().getTime() > 0L, is(true));
    }

    @Test
    public void testSeeResolucion_2() throws UiException, InterruptedException
    {
        // Caso NOT OK: incidencia no existe.
        Incidencia incidencia = insertGetIncidenciaUser(pepeUserComu, 1).getIncidencia();
        incidencia = new Incidencia.IncidenciaBuilder().copyIncidencia(incidencia).incidenciaId(999L).build();
        Thread.sleep(1000);
        Resolucion resolucion = doResolucion(incidencia, RESOLUCION_DEFAULT_DESC, 1122, new Timestamp(new Date().getTime()));
        try {
            incidenciaDao.seeResolucion(resolucion.getIncidencia().getIncidenciaId());
            fail();
        } catch (UiException ue) {
            assertThat(ue.getErrorBean().getMessage(), is(INCIDENCIA_NOT_FOUND.getHttpMessage()));
        }
    }

    @Test
    public void testSeeUserComusImportancia() throws UiException
    {
        // Caso OK.
        Incidencia incidencia = insertGetIncidenciaUser(pepeUserComu, 1).getIncidencia();
        assertThat(incidenciaDao.seeUserComusImportancia(incidencia.getIncidenciaId()).size(), is(1));
    }
}
