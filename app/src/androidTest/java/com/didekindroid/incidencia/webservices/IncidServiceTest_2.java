package com.didekindroid.incidencia.webservices;

import android.support.test.runner.AndroidJUnit4;

import com.didekin.common.exception.DidekinExceptionMsg;
import com.didekin.incidservice.domain.Incidencia;
import com.didekin.incidservice.domain.IncidenciaUser;
import com.didekin.serviceone.domain.Usuario;
import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.UiException;
import com.didekindroid.usuario.activity.utils.CleanUserEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekin.common.oauth2.Rol.PROPIETARIO;
import static com.didekindroid.common.utils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.utils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.utils.ActivityTestUtils.updateSecurityData;
import static com.didekindroid.incidencia.IncidenciaTestUtils.doIncidenciaWithId;
import static com.didekindroid.incidencia.IncidenciaTestUtils.insertGetIncidencia;
import static com.didekindroid.incidencia.IncidenciaTestUtils.insertGetIncidenciaWithId;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.activity.utils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_REAL_PEPE;
import static com.didekindroid.usuario.dominio.DomainDataUtils.USER_JUAN;
import static com.didekindroid.usuario.dominio.DomainDataUtils.USER_PEPE;
import static com.didekindroid.usuario.dominio.DomainDataUtils.makeUsuarioComunidad;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 18/11/15
 * Time: 14:53
 */

/**
 * Tests con usario_comunidad sin perfil administrador o presidente.
 */
@RunWith(AndroidJUnit4.class)
public class IncidServiceTest_2 {

    CleanUserEnum whatClean;
    Usuario pepe;
    UsuarioComunidad pepeUserComu;

    @Before
    public void setUp() throws Exception
    {
        whatClean = CLEAN_JUAN_AND_PEPE;
        pepe = signUpAndUpdateTk(COMU_REAL_PEPE);
        pepeUserComu = ServOne.seeUserComusByUser().get(0);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(whatClean);
    }

    @Test
    public void testDeleteIncidenciaUser_2() throws UiException, InterruptedException
    {
        // Verificamos la excepción UNAUTHORIZED_TX_TO_USER;

        // Insertamos incidencia.
        Incidencia incidencia_1 = insertGetIncidencia(pepeUserComu, 1);
        // Verificamos poderes: true; solo hay una incidenciaUser.
        IncidenciaUser incidUser = IncidenciaServ.getIncidenciaUserWithPowers(incidencia_1.getIncidenciaId());
        assertThat(incidUser.isModifyDescOrEraseIncid(), is(true));

        // Registro usuario en misma comunidad y lo asocio a la incidencia.
        UsuarioComunidad userComuJuan = makeUsuarioComunidad(pepeUserComu.getComunidad(), USER_JUAN,
                "portal", "esc", "plantaX", "door12", PROPIETARIO.function);
        assertThat(ServOne.regUserAndUserComu(userComuJuan), is(true));
        updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
        Thread.sleep(1000);
        Incidencia incidencia_2 = insertGetIncidenciaWithId(incidencia_1.getIncidenciaId(), userComuJuan, 2);
        // Verificamos poderes de Juan: false, porque no es usuario titular.
        assertThat(incidencia_2, is(incidencia_1));
        incidUser = IncidenciaServ.getIncidenciaUserWithPowers(incidencia_2.getIncidenciaId());
        assertThat(incidUser.isModifyDescOrEraseIncid(), is(false));
        // Cambiamos de usario y verificamos poderes de Pepe: false, porque existe otra incidencia con importancia > 1.
        updateSecurityData(USER_PEPE.getUserName(), USER_PEPE.getPassword());
        incidUser = IncidenciaServ.getIncidenciaUserWithPowers(incidencia_2.getIncidenciaId());
        assertThat(incidUser.isModifyDescOrEraseIncid(), is(false));
        // Intentamos borrar la incidencia.
        try {
            IncidenciaServ.deleteIncidencia(incidUser.getIncidencia().getIncidenciaId());
            fail();
        } catch (UiException ue) {
            assertThat(ue.getAction(), is(UiException.UiAction.LOGIN));
            assertThat(ue.getInServiceException().getHttpMessage(), is(DidekinExceptionMsg.UNAUTHORIZED_TX_TO_USER.getHttpMessage()));
            assertThat(ue.getResourceId(), is(R.string.user_without_powers));
        }
    }

    @Test
    public void testModifyIncidenciaUser_2() throws UiException, InterruptedException
    {
        // Verificamos la excepción UNAUTHORIZED_TX_TO_USER;

        // Insertamos incidencia.
        Incidencia incidencia_1 = insertGetIncidencia(pepeUserComu, 1);
        // Registro usuario en misma comunidad y lo asocio a la incidencia.
        UsuarioComunidad userComuJuan = makeUsuarioComunidad(pepeUserComu.getComunidad(), USER_JUAN,
                "portal", "esc", "plantaX", "door12", PROPIETARIO.function);
        assertThat(ServOne.regUserAndUserComu(userComuJuan), is(true));
        updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
        Thread.sleep(1000);
        insertGetIncidenciaWithId(incidencia_1.getIncidenciaId(), userComuJuan, 2);
        // Cambiamos de usario e intentamos modificar la incidencia.
        updateSecurityData(USER_PEPE.getUserName(), USER_PEPE.getPassword());
        try {
            IncidenciaServ.modifyIncidenciaUser(new IncidenciaUser.IncidenciaUserBuilder(
                    doIncidenciaWithId(incidencia_1.getIncidenciaId(), "modified_desc", incidencia_1.getComunidad().getC_Id(), (short) 16))
                    .usuario(pepe)
                    .importancia((short) 2)
                    .build());
            fail();
        } catch (UiException ue) {
            assertThat(ue.getAction(), is(UiException.UiAction.LOGIN));
            assertThat(ue.getInServiceException().getHttpMessage(), is(DidekinExceptionMsg.UNAUTHORIZED_TX_TO_USER.getHttpMessage()));
            assertThat(ue.getResourceId(), is(R.string.user_without_powers));
        }
    }

    @Test
    public void testRegUserInIncidencia_1() throws UiException, InterruptedException
    {
        // Funcionamiento OK.

        // Insertamos incidencia.
        Incidencia incidencia_1 = insertGetIncidencia(pepeUserComu, 1);
        // Registro usuario en misma comunidad y lo asocio a la incidencia.
        UsuarioComunidad userComuJuan = makeUsuarioComunidad(pepeUserComu.getComunidad(), USER_JUAN,
                "portal", "esc", "plantaX", "door12", PROPIETARIO.function);
        assertThat(ServOne.regUserAndUserComu(userComuJuan), is(true));
        updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
        Usuario juanDb = ServOne.getUserData();
        IncidenciaUser newIncidUser = new IncidenciaUser.IncidenciaUserBuilder(incidencia_1)
                .usuario(juanDb)
                .importancia((short)2)
                .build();
        assertThat(IncidenciaServ.regUserInIncidencia(newIncidUser), is(1));
    }

    @Test
    public void testRegUserInIncidencia_3() throws UiException
    {
        // UsuarioComunidad incongruente con incidencia_comunidad.
        // Insertamos incidencia.
        Incidencia incidencia_1 = insertGetIncidencia(pepeUserComu, 1);
        // Registramos usuario en otra comunidad.
        Usuario userJuan = signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        // Intentamos asociar el nuevo usuario a la incidencia.
        IncidenciaUser newIncidUser = new IncidenciaUser.IncidenciaUserBuilder(incidencia_1)
                .usuario(userJuan)
                .importancia((short)2)
                .build();
        try{
            IncidenciaServ.regUserInIncidencia(newIncidUser);
            fail();
        }catch (UiException ue){
            assertThat(ue.getInServiceException().getHttpMessage(),is(DidekinExceptionMsg.USERCOMU_WRONG_INIT.getHttpMessage()));
            assertThat(ue.getAction(),is(UiException.UiAction.LOGIN));
            assertThat(ue.getResourceId(),is(R.string.user_without_powers));
        }
    }

//    ============================= HELPER METHODS ===============================

}
