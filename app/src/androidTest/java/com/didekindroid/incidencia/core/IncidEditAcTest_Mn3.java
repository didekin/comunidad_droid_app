package com.didekindroid.incidencia.core;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.incidencia.dominio.Resolucion;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.doIncidencia;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.doResolucion;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_RESOLUCION_REG_EDIT_AC;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.security.SecurityTestUtils.updateSecurityData;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.model.usuariocomunidad.Rol.PRESIDENTE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 19/01/16
 * Time: 16:57
 *
 * Tests sobre una incidencia CON resolución en BD.
 * El usuario NO tiene autoridad 'adm'; es iniciador de la incidencia.
 */
@RunWith(AndroidJUnit4.class)
public class IncidEditAcTest_Mn3 extends IncidEditAbstractTest {

    Resolucion resolucion;
    boolean hasResolucion;

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(3000);
    }

    @Override
    protected IntentsTestRule<IncidEditAc> doIntentRule()
    {
        return new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {
            /**
             * The users hasn't got authority 'adm'; she is iniciadora.
             * */
            @Override
            protected Intent getActivityIntent()
            {
                try {
                    signUpAndUpdateTk(COMU_ESCORIAL_JUAN);
                    UsuarioComunidad juanEscorial = userComuDaoRemote.seeUserComusByUser().get(0);
                    incidenciaJuan = new IncidImportancia.IncidImportanciaBuilder(
                            doIncidencia(juanEscorial.getUsuario().getUserName(), "Incidencia Escorial", juanEscorial.getComunidad().getC_Id(), (short) 43))
                            .usuarioComunidad(juanEscorial)
                            .importancia((short) 3).build();
                    incidenciaDao.regIncidImportancia(incidenciaJuan);
                    IncidenciaUser incidenciaUserDb = incidenciaDao.seeIncidsOpenByComu(juanEscorial.getComunidad().getC_Id()).get(0);
                    incidenciaJuan = incidenciaDao.seeIncidImportancia(incidenciaUserDb.getIncidencia().getIncidenciaId()).getIncidImportancia();
                    Thread.sleep(1000);

                    // Necesitamos usuario con 'adm' para registrar resolución.
                    assertThat(userComuDaoRemote.regUserAndUserComu(new UsuarioComunidad.UserComuBuilder(juanEscorial.getComunidad(), USER_PEPE)
                            .roles(PRESIDENTE.function)
                            .build()).execute().body(), is(true));
                    updateSecurityData(USER_PEPE.getUserName(), USER_PEPE.getPassword());
                    resolucion = doResolucion(incidenciaJuan.getIncidencia(), "resol_desc1", 1000, new Timestamp(new Date().getTime()));
                    assertThat(incidenciaDao.regResolucion(resolucion), is(1));
                    hasResolucion = incidenciaDao.seeIncidImportancia(resolucion.getIncidencia().getIncidenciaId()).hasResolucion();

                    // Volvemos a usuario del test.
                    updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
                } catch (InterruptedException | IOException | UiException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidenciaJuan);
                intent.putExtra(INCID_RESOLUCION_FLAG.key, hasResolucion);
                return intent;
            }
        };
    }

    @Override
    protected CleanUserEnum whatToClean()
    {
        return CLEAN_JUAN_AND_PEPE;
    }

//    ============================  TESTS  ===================================

    @Test
    public void testIncidResolucionReg_Mn() throws Exception
    {
        INCID_RESOLUCION_REG_EDIT_AC.checkMenuItem_WTk(mActivity);

        onView(ViewMatchers.withId(R.id.incid_resolucion_see_fr_layout)).check(matches(isDisplayed()));
        // Extra con incidImportancia.
        intended(hasExtra(INCID_IMPORTANCIA_OBJECT.key, incidenciaJuan));
        // Hay resolución en BD --> extra con resolución.
        intended(hasExtra(INCID_RESOLUCION_OBJECT.key, resolucion));

        checkUp();
        checkScreenEditMaxPowerFr();
    }
}