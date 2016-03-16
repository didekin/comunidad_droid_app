package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.Incidencia;
import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.common.testutils.ActivityTestUtils;
import com.didekindroid.incidencia.repository.IncidenciaDataDbHelper;
import com.didekindroid.usuario.testutils.CleanUserEnum;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
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
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekin.common.oauth2.Rol.PROPIETARIO;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.testutils.ActivityTestUtils.updateSecurityData;
import static com.didekindroid.common.activity.IntentExtraKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.insertGetIncidenciaUser;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.insertGetIncidenciaWithId;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_REAL_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.makeUsuarioComunidad;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * User: pedro@didekin
 * Date: 19/01/16
 * Time: 16:57
 */

/**
 * Tests genéricos sobre aspecto y tests funcionales para un userComu SIN permisos para modificar o borrar una incidencia.
 * El userComu ya está asociado con la incidencia que edita.
 */
@RunWith(AndroidJUnit4.class)
public class IncidEditAcTest_2 {

    IncidEditAc mActivity;
    CleanUserEnum whatClean;
    UsuarioComunidad pepeUserComu;
    IncidImportancia incidPepeReal;
    IncidImportancia incidJuanReal;

    @Rule
    public IntentsTestRule<IncidEditAc> intentRule = new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            super.beforeActivityLaunched();
        }

        @Override
        protected Intent getActivityIntent()
        {
            try {
                signUpAndUpdateTk(COMU_REAL_PEPE);
                pepeUserComu = ServOne.seeUserComusByUser().get(0);
                // Insertamos incidencia.
                IncidenciaUser incidenciaUser_1 = insertGetIncidenciaUser(pepeUserComu, 1);
                incidPepeReal = IncidenciaServ.seeIncidImportancia(incidenciaUser_1.getIncidencia().getIncidenciaId());

                // Registro userComu en misma comunidad y lo asocio a la incidencia.
                UsuarioComunidad userComuJuan = makeUsuarioComunidad(pepeUserComu.getComunidad(), USER_JUAN,
                        "portal", "esc", "plantaX", "door12", PROPIETARIO.function);
                ServOne.regUserAndUserComu(userComuJuan);
                updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
                Thread.sleep(1000);
                Incidencia incidencia_2 = insertGetIncidenciaWithId(incidPepeReal.getIncidencia().getIncidenciaId(), userComuJuan, 2).getIncidencia();
                // Verificamos poderes de Juan: false, porque no es userComu titular.
                incidJuanReal = IncidenciaServ.seeIncidImportancia(incidencia_2.getIncidenciaId());
                Assert.assertThat(incidJuanReal.isIniciadorIncidencia(), is(false));
            } catch (UiException | InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent();
            intent.putExtra(INCID_IMPORTANCIA_OBJECT.extra, incidJuanReal);
            return intent;
        }
    };

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(4000);
    }

    @Before
    public void setUp() throws Exception
    {
        whatClean = CLEAN_JUAN_AND_PEPE;
        mActivity = intentRule.getActivity();
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(whatClean);
    }

//    ============================  TESTS  ===================================

    @Test
    public void testOnCreate_1() throws Exception
    {
        assertThat(mActivity, notNullValue());
        IncidImportancia incidImportanciaIntent = (IncidImportancia) mActivity.getIntent().getSerializableExtra(INCID_IMPORTANCIA_OBJECT.extra);
        assertThat(incidImportanciaIntent.getUserComu(), notNullValue());
        assertThat(incidImportanciaIntent.isIniciadorIncidencia(), is(false));

        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        assertThat(mActivity.findViewById(R.id.incid_edit_nopower_fr_layout), notNullValue());
        onView(withId(R.id.incid_edit_fragment_container_ac)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_edit_nopower_fr_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_comunidad_rot)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_comunidad_txt)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_reg_desc_txt)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_ambito_view)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_reg_importancia_spinner)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_edit_fr_modif_button)).check(matches(isDisplayed()));

        ActivityTestUtils.checkNavigateUp();
    }

    @Test
    public void testOnData_1()
    {
        onView(allOf(
                withId(R.id.incid_comunidad_txt),
                withText(incidJuanReal.getIncidencia().getComunidad().getNombreComunidad())
        )).check(matches(isDisplayed()));

        onView(allOf(
                withId(R.id.incid_reg_desc_txt),
                withText(incidJuanReal.getIncidencia().getDescripcion())
        )).check(matches(isDisplayed()));

        onView(allOf(
                withId(R.id.incid_ambito_view),
                withText(new IncidenciaDataDbHelper(mActivity).getAmbitoDescByPk(incidJuanReal.getIncidencia().getAmbitoIncidencia().getAmbitoId()))
        )).check(matches(isDisplayed()));

        onView(allOf(
                withId(R.id.app_spinner_1_dropdown_item),
                withParent(withId(R.id.incid_reg_importancia_spinner)),
                withText(mActivity.getResources().getStringArray(R.array.IncidImportanciaArray)[incidJuanReal.getImportancia()])
        )).check(matches(isDisplayed()));
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
        onView(withId(R.id.incid_see_by_comu_ac)).check(matches(isDisplayed()));
    }
}

/*check(matches(withText(is("ámbito de incidencia"))))*/