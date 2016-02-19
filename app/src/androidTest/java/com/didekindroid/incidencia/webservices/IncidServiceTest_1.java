package com.didekindroid.incidencia.webservices;

import android.support.test.runner.AndroidJUnit4;

import com.didekin.common.exception.DidekinExceptionMsg;
import com.didekin.incidservice.dominio.IncidComment;
import com.didekin.incidservice.dominio.Incidencia;
import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekin.incidservice.dominio.Resolucion;
import com.didekin.usuario.dominio.Usuario;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.usuario.testutils.CleanUserEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static com.didekin.common.exception.DidekinExceptionMsg.INCIDENCIA_NOT_FOUND;
import static com.didekin.common.exception.DidekinExceptionMsg.INCIDENCIA_WRONG_INIT;
import static com.didekin.common.exception.DidekinExceptionMsg.ROLES_NOT_FOUND;
import static com.didekin.common.exception.DidekinExceptionMsg.USERCOMU_WRONG_INIT;
import static com.didekin.common.oauth2.Rol.PROPIETARIO;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.testutils.ActivityTestUtils.updateSecurityData;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doComment;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doIncidencia;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doIncidenciaWithId;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doResolucion;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.insertGetIncidencia;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.makeUsuarioComunidad;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
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
@RunWith(AndroidJUnit4.class)
public class IncidServiceTest_1 {

    CleanUserEnum whatClean;
    Usuario pepe;
    UsuarioComunidad pepeUserComu;

    @Before
    public void setUp() throws Exception
    {
        whatClean = CLEAN_PEPE;
        pepe = signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
        pepeUserComu = ServOne.seeUserComusByUser().get(0);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(whatClean);
    }

    @Test
    public void testDeleteIncidenciaUser_1() throws UiException
    {
        // Existe la incidencia; la borrarmos.
        Incidencia incidencia = insertGetIncidencia(pepeUserComu, 1).getIncidencia();
        assertThat(IncidenciaServ.deleteIncidencia(incidencia.getIncidenciaId()), is(1));

        // Intentamos borrarla de nuevo: la app. redirige a la consulta de incidencias.
        try {
            IncidenciaServ.deleteIncidencia(incidencia.getIncidenciaId());
            fail();
        } catch (UiException ue) {
            assertThat(ue.getInServiceException().getHttpMessage(), is(INCIDENCIA_NOT_FOUND.getHttpMessage()));
        }
    }

    @Test
    public void testGetIncidenciaUserWithPowers_1() throws UiException
    {
        Incidencia incidencia = insertGetIncidencia(pepeUserComu, 1).getIncidencia();
        IncidenciaUser incidenciaUser = IncidenciaServ.getIncidenciaUserWithPowers(incidencia.getIncidenciaId());
        assertThat(incidenciaUser.isYetIniciador(), is(true));
    }

    @Test
    public void testGetIncidenciaUserWithPowers_2() throws UiException
    {
        // Incidencia no existe en BD.
        Incidencia incidencia = doIncidencia("Incidencia One", pepeUserComu.getComunidad().getC_Id(), (short) 43);
        try {
            IncidenciaServ.getIncidenciaUserWithPowers(incidencia.getIncidenciaId());
            fail();
        } catch (UiException ue) {
            assertThat(ue.getInServiceException().getHttpMessage(), is(INCIDENCIA_NOT_FOUND.getHttpMessage()));
        }
    }

    @Test
    public void testGetIncidenciaUserWithPowers_3() throws UiException
    {
        whatClean = CLEAN_JUAN_AND_PEPE;

        /* Usuario e incidencia en BD, pero no hay relación usuario_incidencia, ni relación usuario_comunidad.*/
        Incidencia incidencia = insertGetIncidencia(pepeUserComu, 1).getIncidencia();
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        try {
            IncidenciaServ.getIncidenciaUserWithPowers(incidencia.getIncidenciaId());
            fail();
        } catch (UiException ue) {
            assertThat(ue.getInServiceException().getHttpMessage(), is(ROLES_NOT_FOUND.getHttpMessage()));
        }
    }

    @Test
    public void testGetIncidenciaUserWithPowers_4() throws UiException
    {
        whatClean = CLEAN_JUAN_AND_PEPE;

        /* Usuario e incidencia en BD, pero no hay relación usuario_incidencia, SÍ hay relación usuario_comunidad.*/
        Incidencia incidencia = insertGetIncidencia(pepeUserComu, 1).getIncidencia();
        UsuarioComunidad userComuJuan = makeUsuarioComunidad(pepeUserComu.getComunidad(), USER_JUAN,
                "portal33", "esc22", "planta33", "door33", PROPIETARIO.function);
        ServOne.regUserAndUserComu(userComuJuan);
        updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());

        // Devuelve instancia de IncidenciaUser con usuario == null.
        assertThat(IncidenciaServ.getIncidenciaUserWithPowers(incidencia.getIncidenciaId()).getUsuarioComunidad(), nullValue());
    }

    @Test
    public void testIncidCommentsSee_1() throws UiException
    {
        // Caso OK.
        Incidencia incid_pepeComu_1 = insertGetIncidencia(pepeUserComu, 1).getIncidencia();
        assertThat(IncidenciaServ.regIncidComment(doComment("Comment_1_pepeComu_1", incid_pepeComu_1)), is(1));

        List<IncidComment> comments = IncidenciaServ.incidCommentsSee(incid_pepeComu_1);
        assertThat(comments.size(), is(1));
        assertThat(IncidenciaServ.regIncidComment(doComment("Comment_2_pepeComu_1", incid_pepeComu_1)), is(1));
        comments = IncidenciaServ.incidCommentsSee(incid_pepeComu_1);
        assertThat(comments.size(), is(2));
    }

    @Test
    public void testIncidCommentsSee_2() throws UiException
    {
        // Incidencia no existe.
        Incidencia incidencia = doIncidenciaWithId(999L, "incid_no_existe", pepeUserComu.getComunidad().getC_Id(), (short) 2);

        try {
            IncidenciaServ.incidCommentsSee(incidencia);
            fail();
        } catch (UiException ue) {
            assertThat(ue.getInServiceException().getHttpMessage(), is(INCIDENCIA_NOT_FOUND.getHttpMessage()));
        }
    }

    @Test
    public void testIncidCommentsSee_3() throws UiException
    {
        // Incidencia existe; no tiene asociada comentarios.
        Incidencia incid_pepeComu_1 = insertGetIncidencia(pepeUserComu, 1).getIncidencia();
        List<IncidComment> comments = IncidenciaServ.incidCommentsSee(incid_pepeComu_1);
        assertThat(comments, notNullValue());
        assertThat(comments.size(), is(0));
    }

    @Test
    public void testIncidCommentsSee_4() throws UiException
    {
        whatClean = CLEAN_JUAN_AND_PEPE;

        // El usuario no está asociado a la comunidad de la incidencia.
        Incidencia incid_pepeComu_1 = insertGetIncidencia(pepeUserComu, 1).getIncidencia();
        assertThat(IncidenciaServ.regIncidComment(doComment("Comment_1_pepeComu_1", incid_pepeComu_1)), is(1));

        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        try {
            IncidenciaServ.incidCommentsSee(incid_pepeComu_1);
            fail();
        } catch (UiException ue) {
            assertThat(ue.getInServiceException().getHttpMessage(), is(USERCOMU_WRONG_INIT.getHttpMessage()));
        }
    }

    @Test
    public void testIncidSeeByComu_1() throws UiException
    {
        IncidenciaUser incidPepeUserComu1 = new IncidenciaUser.IncidenciaUserBuilder(doIncidencia("Incidencia One", pepeUserComu.getComunidad().getC_Id(), (short) 43))
                .usuario(pepeUserComu)
                .importancia((short) 3)
                .build();
        assertThat(IncidenciaServ.regIncidenciaUser(incidPepeUserComu1), is(1));
        IncidenciaUser incidPepeUserComu2 = new IncidenciaUser.IncidenciaUserBuilder(doIncidencia("Incidencia Two", pepeUserComu.getComunidad().getC_Id(), (short) 11))
                .usuario(pepeUserComu)
                .importancia((short) 2)
                .build();
        assertThat(IncidenciaServ.regIncidenciaUser(incidPepeUserComu2), is(1));

        assertThat(IncidenciaServ.incidSeeByComu(pepeUserComu.getComunidad().getC_Id()).size(), is(2));
    }

    @Test
    public void testIncidSeeByComu_2() throws UiException
    {
        // No existe la comunidad en BD: devuelve lista vacía.
        try {
            IncidenciaServ.incidSeeByComu(999L);
            fail();
        } catch (UiException ue) {
            assertThat(ue.getInServiceException().getHttpMessage(), is(USERCOMU_WRONG_INIT.getHttpMessage()));
        }
    }

    @Test
    public void testModifyIncidenciaUser_1() throws UiException
    {
        Incidencia incidenciaDb = insertGetIncidencia(pepeUserComu, 3).getIncidencia();

        // Modificamos la incidencia original.
        IncidenciaUser incidPepeUserComu = new IncidenciaUser.IncidenciaUserBuilder(
                doIncidenciaWithId(incidenciaDb.getIncidenciaId(), "modified_desc", pepeUserComu.getComunidad().getC_Id(), (short) 11))
                .usuario(pepeUserComu)
                .importancia((short) 2)
                .build();
        assertThat(IncidenciaServ.modifyIncidenciaUser(incidPepeUserComu), is(2));

        // Cambiamos incidenciaId a una PK inexistente: excepción INCIDENCIA_NOT_FOUND.
        incidPepeUserComu = new IncidenciaUser.IncidenciaUserBuilder(
                doIncidenciaWithId(999L, "modified_desc", pepeUserComu.getComunidad().getC_Id(), (short) 11))
                .usuario(pepeUserComu)
                .importancia((short) 2)
                .build();
        try {
            IncidenciaServ.modifyIncidenciaUser(incidPepeUserComu);
            fail();
        } catch (UiException u) {
            assertThat(u.getInServiceException().getHttpMessage(), is(INCIDENCIA_NOT_FOUND.getHttpMessage()));
        }
    }

    @Test
    public void testModifyUser() throws UiException
    {
        // Verificamos modificación de importancia.
        Incidencia incidenciaDb = insertGetIncidencia(pepeUserComu, 3).getIncidencia();
        IncidenciaUser incidenciaUser = new IncidenciaUser.IncidenciaUserBuilder(incidenciaDb).usuario(pepeUserComu).importancia((short) 2).build();
        assertThat(IncidenciaServ.modifyUser(incidenciaUser), is(1));
        incidenciaUser = IncidenciaServ.getIncidenciaUserWithPowers(incidenciaDb.getIncidenciaId());
        assertThat(incidenciaUser.getImportancia(), is((short) 2));
    }

    @Test
    public void testRegIncidComment_1() throws UiException
    {
        // Caso OK.
        Incidencia incidencia = insertGetIncidencia(pepeUserComu, 1).getIncidencia();
        assertThat(IncidenciaServ.regIncidComment(doComment("Comment_DESC", incidencia)), is(1));
    }

    @Test
    public void testRegIncidComment_2() throws UiException
    {
        whatClean = CLEAN_JUAN_AND_PEPE;

        // Caso EntityException: USERCOMU_WRONG_INIT.
        Incidencia incidencia = insertGetIncidencia(pepeUserComu, 1).getIncidencia();

        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        try {
            IncidenciaServ.regIncidComment(doComment("Comment_DESC", incidencia));
            fail();
        } catch (UiException ue) {
            assertThat(ue.getInServiceException().getHttpMessage(), is(USERCOMU_WRONG_INIT.getHttpMessage()));
        }
    }

    @Test
    public void testRegIncidComment_3() throws UiException
    {
        // Caso EntityException: INCIDENCIA_WRONG_INIT.
        Incidencia incidencia = insertGetIncidencia(pepeUserComu, 1).getIncidencia();
        IncidComment comment = doComment("Comment_DESC", new Incidencia.IncidenciaBuilder().copyIncidencia(incidencia).incidenciaId(999L).build());

        try {
            IncidenciaServ.regIncidComment(comment);
            fail();
        } catch (UiException ue) {
            assertThat(ue.getInServiceException().getHttpMessage(), is(INCIDENCIA_WRONG_INIT.getHttpMessage()));
        }
    }

    @Test
    public void testRegIncidenciaUserComu() throws Exception
    {
        assertThat(pepeUserComu, notNullValue());
        IncidenciaUser incidPepeUserComu = new IncidenciaUser.IncidenciaUserBuilder(doIncidencia("Incidencia One", pepeUserComu.getComunidad().getC_Id(), (short) 43))
                .usuario(pepeUserComu)
                .importancia((short) 3)
                .build();
        assertThat(IncidenciaServ.regIncidenciaUser(incidPepeUserComu), is(1));
        // TODO: test con android 6 y NotificationManager métodos.
    }

    @Test
    public void testRegResolucion_1() throws UiException, InterruptedException
    {
       // Caso OK.
        Incidencia incidencia = insertGetIncidencia(pepeUserComu, 2).getIncidencia();
        Thread.sleep(1000);
        Resolucion resolucion = doResolucion(incidencia, "resol_desc", 1000, new Timestamp(new Date().getTime()));
        assertThat(IncidenciaServ.regResolucion(resolucion),is(1));
    }

    @Test
    public void testRegResolucion_3() throws UiException, InterruptedException
    {
        whatClean = CLEAN_JUAN_AND_PEPE;

        // Caso: usuarioComunidad no relacionado con comunidad de la incidencia.
        Incidencia incidencia = insertGetIncidencia(pepeUserComu, 2).getIncidencia();
        Thread.sleep(1000);
        Resolucion resolucion = doResolucion(incidencia, "resol_desc", 1000, new Timestamp(new Date().getTime()));

        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        try {
            IncidenciaServ.regResolucion(resolucion);
            fail();
        } catch (UiException ue){
            assertThat(ue.getInServiceException().getHttpMessage(), is(ROLES_NOT_FOUND.getHttpMessage()));
        }
    }

    @Test
    public void testRegResolucion_4()
    {
        // Caso: no existe la incidencia.
        Incidencia incidencia = doIncidencia("incid_noDB", pepeUserComu.getComunidad().getC_Id(), (short) 2);
        Resolucion resolucion = doResolucion(incidencia, "resol_desc", 1000, new Timestamp(new Date().getTime()));
        try {
            IncidenciaServ.regResolucion(resolucion);
            fail();
        } catch (UiException ie){
            assertThat(ie.getInServiceException().getHttpMessage(), is(INCIDENCIA_NOT_FOUND.getHttpMessage()));
        }
    }

    @Test
    public void testRegResolucion_5() throws UiException, InterruptedException
    {
        // Caso: resolución duplicada.
        Incidencia incidencia = insertGetIncidencia(pepeUserComu, 2).getIncidencia();
        Thread.sleep(1000);
        Resolucion resolucion = doResolucion(incidencia, "resol_desc", 1000, new Timestamp(new Date().getTime()));
        assertThat(IncidenciaServ.regResolucion(resolucion),is(1));
        Thread.sleep(1000);

        try{
           IncidenciaServ.regResolucion(resolucion);
            fail();
        } catch (UiException ue){
            assertThat(ue.getInServiceException().getHttpMessage(), is(DidekinExceptionMsg.RESOLUCION_DUPLICATE.getHttpMessage()));
        }
    }

    @Test
    public void testRegUserInIncidencia_2()
    {
        // IncidenciaId == 0 (inicialización por defecto). Existe relación usuario_comunidad.
        Incidencia incidencia = doIncidencia("incidencia sin Id en BD", pepeUserComu.getComunidad().getC_Id(), (short) 11);
        IncidenciaUser incidenciaUser = new IncidenciaUser.IncidenciaUserBuilder(incidencia).usuario(pepeUserComu).importancia((short) 2).build();
        try {
            IncidenciaServ.regUserInIncidencia(incidenciaUser);
            fail();
        } catch (UiException ie) {
            assertThat(ie.getInServiceException().getHttpMessage(), is(INCIDENCIA_WRONG_INIT.getHttpMessage()));
        }
    }

    //    ============================= HELPER METHODS ===============================
}
