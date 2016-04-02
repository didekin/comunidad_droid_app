package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;

import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.usuario.testutils.CleanUserEnum;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekin.common.oauth2.Rol.PROPIETARIO;
import static com.didekindroid.common.activity.BundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.common.activity.BundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.common.activity.FragmentTags.incid_edit_ac_frgs_tag;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.testutils.ActivityTestUtils.updateSecurityData;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.insertGetIncidenciaUser;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_REAL_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.makeUsuarioComunidad;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

/**
 * User: pedro@didekin
 * Date: 19/01/16
 * Time: 16:57
 */

/**
 * Tests para un userComu sin registro IncidImportancia en la incidencia.
 */
@SuppressWarnings("UnnecessaryLocalVariable")
@RunWith(AndroidJUnit4.class)
public class IncidEditAcNoPowerTest_2 extends IncidEditAbstractTest {

    @Override
    IntentsTestRule<IncidEditAc> doIntentRule()
    {
        return new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {

            /**
             * Preconditions:
             * 1. An incidImportancia.userComu WITHOUT powers to erase OR modify is passed.
             * 2. UserComu sin registro IncidImportancia en la incidencia.
             * */
            @Override
            protected Intent getActivityIntent()
            {
                try {
                    signUpAndUpdateTk(COMU_REAL_PEPE);
                    pepeUserComu = ServOne.seeUserComusByUser().get(0);
                    // Insertamos incidencia.
                    IncidenciaUser incidenciaUser_1 = insertGetIncidenciaUser(pepeUserComu, 1);
                    // Registro userComu en misma comunidad.
                    UsuarioComunidad userComuJuan = makeUsuarioComunidad(pepeUserComu.getComunidad(), USER_JUAN,
                            "portal", "esc", "plantaX", "door12", PROPIETARIO.function);
                    ServOne.regUserAndUserComu(userComuJuan);
                    updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
                    incidenciaJuan = IncidenciaServ.seeIncidImportancia(incidenciaUser_1.getIncidencia().getIncidenciaId()).getIncidImportancia();
                } catch (UiException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidenciaJuan);
                intent.putExtra(INCID_RESOLUCION_FLAG.key, false);
                return intent;
            }
        };
    }

    @Override
    IncidImportancia getIncidImportanciaIntent()
    {
        return incidenciaJuan;
    }

    @Override
    boolean isResolucionInIntentTrue()
    {
        assertThat(flagResolucionIntent, is(false));
        return flagResolucionIntent;
    }

    @Override
    boolean isIniciadorUserInSession()
    {
        assertThat(incidenciaJuan.isIniciadorIncidencia(),is(false));
        return incidenciaJuan.isIniciadorIncidencia();
    }

    @Override
    boolean hasAdmAuthority()
    {
        assertThat(incidenciaJuan.getUserComu().hasAdministradorAuthority(),is(false));
        return incidenciaJuan.getUserComu().hasAdministradorAuthority();
    }

    @Override
    Fragment getIncidEditFr()
    {
        IncidEditNoPowerFr fragmentByTag = (IncidEditNoPowerFr) mActivity.getSupportFragmentManager().findFragmentByTag(incid_edit_ac_frgs_tag);
        return fragmentByTag;
    }

    @Override
    CleanUserEnum whatToClean()
    {
        return CLEAN_JUAN_AND_PEPE;
    }

//    ============================  TESTS  ===================================

    @Test
    public void testOnCreate_1() throws Exception
    {
        checkScreenEditNoPowerFr();
    }

    @Test
    public void testOnData_1()
    {
        checkDataEditNoPowerFr();
    }

    @Test
    public void testModifyUser_1()
    {
        // Introducimos importancia.
        onView(withId(R.id.incid_reg_importancia_spinner)).perform(click());
        onData
                (allOf(
                                is(instanceOf(String.class)),
                                is(mActivity.getResources().getStringArray(R.array.IncidImportanciaArray)[4]))
                )
                .perform(click());
        onView(withId(R.id.incid_edit_fr_modif_button)).perform(scrollTo(), click());
        onView(withId(R.id.incid_see_open_by_comu_ac)).check(matches(isDisplayed()));
    }
}