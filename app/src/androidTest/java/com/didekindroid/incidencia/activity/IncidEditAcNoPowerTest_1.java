package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;

import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekindroid.R;
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
import static com.didekindroid.common.activity.FragmentTags.incid_edit_ac_frgs_tag;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

/**
 * User: pedro@didekin
 * Date: 19/01/16
 * Time: 16:57
 */

/**
 * Tests genéricos sobre aspecto y tests funcionales para un userComu SIN permisos para modificar o borrar una incidencia.
 * El userComu ya tiene registrada incidImportancia en BD.
 */
@SuppressWarnings("UnnecessaryLocalVariable")
@RunWith(AndroidJUnit4.class)
public class IncidEditAcNoPowerTest_1 extends IncidEditAbstractTest {

    @Override
    IntentsTestRule<IncidEditAc> doIntentRule()
    {
        return new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {

            @Override
            protected Intent getActivityIntent()
            {
                return getIntentPepeJuanRealNoPower();
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
        // Modificamos importancia.
        onView(withId(R.id.incid_reg_importancia_spinner)).perform(click());
        onData
                (allOf(
                                is(instanceOf(String.class)),
                                is(mActivity.getResources().getStringArray(R.array.IncidImportanciaArray)[3]))
                )
                .perform(click());
        onView(withId(R.id.incid_edit_fr_modif_button)).perform(scrollTo(), click());
        onView(withId(R.id.incid_see_open_by_comu_ac)).check(matches(isDisplayed()));
    }
}