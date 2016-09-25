package com.didekindroid.incidencia.activity;

import android.database.Cursor;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;

import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekin.usuario.dominio.Comunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.incidencia.dominio.IncidenciaBean;
import com.didekindroid.usuario.testutils.CleanUserEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.CursorMatchers.withRowString;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkUp;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.common.testutils.ActivityTestUtils.regSeveralUserComuSameUser;
import static com.didekindroid.common.utils.UIutils.getErrorMsgBuilder;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.common.utils.UIutils.updateIsGcmTokenSentServer;
import static com.didekindroid.incidencia.repository.IncidenciaDataDb.AmbitoIncidencia.AMBITO_INCID_COUNT;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_LA_FUENTE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_LA_FUENTE_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_REAL_PEPE;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 17/11/15
 * Time: 10:07
 */
@SuppressWarnings({"ConstantConditions", "unchecked"})
@RunWith(AndroidJUnit4.class)
public class IncidRegAcTest {

//    TODO: internacionalizar textos.

    private IncidRegAc mActivity;
    private CleanUserEnum whatToClean = CleanUserEnum.CLEAN_PEPE;
    private Comunidad comunidadByDefault;
    ArrayAdapter<Comunidad> comunidadesAdapter;

    int activityLayoutId = R.id.incid_reg_ac_layout;
    int fragmentLayoutId = R.id.incid_reg_frg;

    @Rule
    public IntentsTestRule<IncidRegAc> intentRule = new IntentsTestRule<IncidRegAc>(IncidRegAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            try {
                regSeveralUserComuSameUser(COMU_ESCORIAL_PEPE, COMU_REAL_PEPE, COMU_LA_FUENTE_PEPE);
            } catch (UiException | IOException e) {
                e.printStackTrace();
            }
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
        mActivity = intentRule.getActivity();
        Thread.sleep(2000);
        comunidadesAdapter = (ArrayAdapter<Comunidad>) mActivity.mRegAcFragment.mComunidadSpinner.getAdapter();
        comunidadByDefault = comunidadesAdapter.getItem(0);
        updateIsGcmTokenSentServer(false, mActivity);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(whatToClean);
        updateIsGcmTokenSentServer(false, mActivity);
    }

    //  ===========================================================================

    @Test
    public void testOnCreate_1() throws Exception
    {
        assertThat(isRegisteredUser(mActivity), is(true));
        assertThat(mActivity, notNullValue());

        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        onView(withId(activityLayoutId)).check(matches(isDisplayed()));
        onView(withId(fragmentLayoutId)).check(matches(isDisplayed()));

        // Comunidad spinner.
        assertThat(comunidadesAdapter.getCount(), is(3));
        onView(allOf(
                withId(R.id.app_spinner_1_dropdown_item),
                withParent(withId(R.id.incid_reg_comunidad_spinner))))
                .check(matches(withText(is(comunidadByDefault.getNombreComunidad()))))
                .check(matches(isDisplayed()));

        /* Ámbito incidencia spinner.*/
        onView(allOf(withId(R.id.app_spinner_1_dropdown_item), withParent(withId(R.id.incid_reg_ambito_spinner))))
                .check(matches(withText(is("ámbito de incidencia")))).check(matches(isDisplayed()));
        int count = mActivity.mRegAcFragment.mAmbitoIncidenciaSpinner.getCount();
        assertThat(count, is(AMBITO_INCID_COUNT));
        Cursor cursor = ((CursorAdapter) mActivity.mRegAcFragment.mAmbitoIncidenciaSpinner.getAdapter()).getCursor();
        cursor.moveToPosition(1);
        assertThat(cursor.getString(1), is("Alarmas comunitarias"));
        cursor.moveToPosition(51);
        assertThat(cursor.getString(1), is("Zonas de juegos"));

        // Importancia spinner.
        String[] importancias = mActivity.getResources().getStringArray(R.array.IncidImportanciaArray);
        assertThat(importancias.length, is(5));
        onView(withId(R.id.incid_reg_importancia_spinner))
                .check(matches(withSpinnerText(importancias[0])))
                .check(matches(isDisplayed()));
        String item = (String) mActivity.mRegAcFragment.mImportanciaSpinner.getItemAtPosition(1);
        assertThat(item, is("baja"));
        item = (String) mActivity.mRegAcFragment.mImportanciaSpinner.getItemAtPosition(4);
        assertThat(item, is("urgente"));

        clickNavigateUp();
    }

    @Test
    public void testRegisterIncidencia_1()
    {
        // Caso NOT OK: descripción de incidencia no válida.

        doImportanciaSpinner(4);
        doAmbitoAndDescripcion("Calefacción comunitaria", "descripcion = not valid");

        assertThat(new IncidenciaBean().makeIncidenciaFromView(mActivity.mRegAcFragment.mFragmentView,
                getErrorMsgBuilder(mActivity), mActivity.getResources()), nullValue());

        onView(withId(R.id.incid_reg_ac_button)).perform(scrollTo(), click());
        checkToastInTest(R.string.error_validation_msg, mActivity, R.string.incid_reg_descripcion);
    }

    @Test
    public void testRegisterIncidencia_2() throws UiException
    {
        // Precondition.
        assertThat(IncidenciaServ.seeIncidsOpenByComu(comunidadByDefault.getC_Id()).size(), is(0));

        // Caso OK: incidencia con datos de importancia.
        doImportanciaSpinner(4);
        doAmbitoAndDescripcion("Calefacción comunitaria", "descripcion is valid");

        onView(withId(R.id.incid_reg_ac_button)).perform(scrollTo(), click());

        onView(withId(R.id.incid_see_open_by_comu_ac)).check(matches(isDisplayed()));
        assertThat(IncidenciaServ.seeIncidsOpenByComu(comunidadByDefault.getC_Id()).size(), is(1));
        checkUp(activityLayoutId,fragmentLayoutId);
    }

    @Test
    public void testRegisterIncidencia_3() throws UiException
    {
        // Precondition.
        assertThat(IncidenciaServ.seeIncidsOpenByComu(comunidadByDefault.getC_Id()).size(), is(0));

        // Caso OK: no cubro importancia.
        doAmbitoAndDescripcion("Calefacción comunitaria", "descripcion is valid");

        onView(withId(R.id.incid_reg_ac_button)).perform(scrollTo(), click());

        onView(withId(R.id.incid_see_open_by_comu_ac)).check(matches(isDisplayed()));
        assertThat(IncidenciaServ.seeIncidsOpenByComu(comunidadByDefault.getC_Id()).size(), is(1));
        checkUp(activityLayoutId,fragmentLayoutId);
    }

    @Test
    public void testRegisterIncidencia_4() throws UiException
    {
        // Probamos cambio de comunidad en spinner: Calle La Fuente.
        Comunidad comunidadFuente = comunidadesAdapter.getItem(1);
        assertThat(comunidadFuente.getNombreComunidad(), is(COMU_LA_FUENTE.getNombreComunidad()));
        onView(withId(R.id.incid_reg_comunidad_spinner)).perform(click());
        onData(allOf(is(instanceOf(Comunidad.class)), is(COMU_LA_FUENTE))).perform(click()).check(matches(isDisplayed()));

        // Registro de incidencia con importancia.
        doImportanciaSpinner(4);
        doAmbitoAndDescripcion("Calefacción comunitaria", "Incidencia La Fuente");

        onView(withId(R.id.incid_reg_ac_button)).perform(scrollTo(), click());

        onView(withId(R.id.incid_see_open_by_comu_ac)).check(matches(isDisplayed()));
        List<IncidenciaUser> incidencias = IncidenciaServ.seeIncidsOpenByComu(comunidadFuente.getC_Id());
        assertThat(incidencias.size(), is(1));
        assertThat(incidencias.get(0).getIncidencia().getDescripcion(), is("Incidencia La Fuente"));
        checkUp(activityLayoutId,fragmentLayoutId);
    }

//    =======================   HELPER METHODS ========================

    private void doImportanciaSpinner(int i)
    {
        onView(withId(R.id.incid_reg_importancia_spinner)).perform(click());
        onData
                (allOf(
                        is(instanceOf(String.class)),
                        is(mActivity.getResources().getStringArray(R.array.IncidImportanciaArray)[i]))
                )
                .perform(click());
    }

    private void doAmbitoAndDescripcion(String ambito, String descripcion)
    {
        onView(withId(R.id.incid_reg_ambito_spinner)).perform(click());
        onData(withRowString(1, ambito)).perform(click());
        onView(withId(R.id.incid_reg_desc_ed)).perform(typeText(descripcion));
    }
}