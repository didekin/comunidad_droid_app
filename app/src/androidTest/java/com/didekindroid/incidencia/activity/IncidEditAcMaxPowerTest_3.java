package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.v4.app.Fragment;

import com.didekin.incidservice.dominio.IncidAndResolBundle;
import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.Incidencia;
import com.didekin.incidservice.dominio.Resolucion;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.usuario.testutils.CleanUserEnum;

import org.junit.Test;

import java.sql.Timestamp;
import java.util.Date;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.common.activity.BundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.common.activity.BundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.common.activity.FragmentTags.incid_edit_ac_frgs_tag;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.INCID_DEFAULT_DESC;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.RESOLUCION_DEFAULT_DESC;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doIncidencia;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doResolucion;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 17/03/16
 * Time: 15:51
 */
@SuppressWarnings("UnnecessaryLocalVariable")
public class IncidEditAcMaxPowerTest_3 extends IncidEditAbstractTest {

    IncidAndResolBundle incidResolBundlePepe;

    @Override
    IntentsTestRule<IncidEditAc> doIntentRule()
    {
        return new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {

            /**
             * Preconditions:
             * 1. An IncidenciaUser with powers to modify and to erase is received.
             * 2. There is resolucion in BD.
             * Postconditions:
             * 1. Erase button is NOT shown.
             * */
            @Override
            protected Intent getActivityIntent()
            {
                try {
                    signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
                    UsuarioComunidad pepeEscorial = ServOne.seeUserComusByUser().get(0);
                    IncidImportancia incidPepeEscorial = new IncidImportancia.IncidImportanciaBuilder(
                            doIncidencia(pepeEscorial.getUsuario().getUserName(), INCID_DEFAULT_DESC, pepeEscorial.getComunidad().getC_Id(), (short) 43))
                            .usuarioComunidad(pepeEscorial)
                            .importancia((short) 3)
                            .build();
                    IncidenciaServ.regIncidImportancia(incidPepeEscorial);
                    Incidencia incidenciaDb = IncidenciaServ.seeIncidsOpenByComu(pepeEscorial.getComunidad().getC_Id()).get(0).getIncidencia();
                    // Registramos resolución.
                    Thread.sleep(1000);
                    Resolucion resolucion = doResolucion(incidenciaDb, RESOLUCION_DEFAULT_DESC, 1000, new Timestamp(new Date().getTime()));
                    assertThat(IncidenciaServ.regResolucion(resolucion), is(1));
                    incidResolBundlePepe = IncidenciaServ.seeIncidImportancia(incidenciaDb.getIncidenciaId());
                    incidenciaPepe = incidResolBundlePepe.getIncidImportancia();
                } catch (UiException | InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidenciaPepe);
                intent.putExtra(INCID_RESOLUCION_FLAG.key, incidResolBundlePepe.hasResolucion());
                return intent;
            }
        };
    }

    @Override
    IncidImportancia getIncidImportanciaIntent()
    {
        return incidenciaPepe;
    }

    @Override
    boolean isResolucionInIntentTrue()
    {
        assertThat(flagResolucionIntent, is(true));
        return flagResolucionIntent;
    }

    @Override
    boolean isIniciadorUserInSession()
    {
        assertThat(incidenciaPepe.isIniciadorIncidencia(), is(true));
        return incidenciaPepe.isIniciadorIncidencia();
    }

    @Override
    boolean hasAdmAuthority()
    {
        assertThat(incidenciaPepe.getUserComu().hasAdministradorAuthority(), is(true));
        return incidenciaPepe.getUserComu().hasAdministradorAuthority();
    }

    @Override
    Fragment getIncidEditFr()
    {
        IncidEditMaxPowerFr fragmentByTag = (IncidEditMaxPowerFr) mActivity.getSupportFragmentManager().findFragmentByTag(incid_edit_ac_frgs_tag);
        return fragmentByTag;
    }

    @Override
    CleanUserEnum whatToClean()
    {
        return CLEAN_PEPE;
    }

    @Test
    public void testOnCreate() throws Exception
    {
        checkScreenEditMaxPowerFr();
        // Incidencia con resolución: la pantalla no presenta el botón de borrar.
        onView(withId(R.id.incid_edit_fr_borrar_txt)).check(matches(not(isDisplayed())));
        onView(withId(R.id.incid_edit_fr_borrar_button)).check(matches(not(isDisplayed())));
    }
}