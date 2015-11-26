package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.database.Cursor;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.CursorAdapter;

import com.didekin.serviceone.domain.Comunidad;
import com.didekindroid.R;
import com.didekindroid.common.UiException;
import com.didekindroid.incidencia.dominio.IncidenciaBean;
import com.didekindroid.usuario.activity.utils.CleanUserEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.CursorMatchers.withRowString;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.common.utils.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.common.utils.AppIntentExtras.COMUNIDAD_ID;
import static com.didekindroid.common.utils.UIutils.getErrorMsgBuilder;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.incidencia.repository.IncidenciaDataDb.AmbitoIncidencia.AMBITO_INCID_COUNT;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.cleanOptions;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.signUpAndUpdateTk;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
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
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class IncidRegAcTest {

    private IncidRegAc mActivity;
    private CleanUserEnum whatToClean = CleanUserEnum.CLEAN_PEPE;
    private long comunidadIdIntent;

    @Rule
    public IntentsTestRule<IncidRegAc> intentRule = new IntentsTestRule<IncidRegAc>(IncidRegAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
        }

        @Override
        protected Intent getActivityIntent()
        {
            try {
                signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
            } catch (UiException e) {
                e.printStackTrace();
            }
            List<Comunidad> comunidadesUserOne = null;
            try {
                comunidadesUserOne = ServOne.getComusByUser();
            } catch (UiException e) {
            }
            Intent intent = new Intent();
            comunidadIdIntent =  comunidadesUserOne.get(0).getC_Id();
            intent.putExtra(COMUNIDAD_ID.extra, comunidadIdIntent);
            return intent;
        }
    };

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(5000);
    }

    @Before
    public void setUp() throws Exception
    {
        mActivity = intentRule.getActivity();
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(whatToClean);
    }

    //  ===========================================================================

    @Test
    public void testOnCreate_1() throws Exception
    {
        assertThat(isRegisteredUser(mActivity), is(true));
        assertThat(mActivity, notNullValue());

        long comunidadId = mActivity.getIntent().getLongExtra(COMUNIDAD_ID.extra, 0L);
        assertThat(comunidadId > 0, is(true));

        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_reg_ac_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_reg_frg)).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.app_spinner_1_dropdown_item), withParent(withId(R.id.incid_reg_ambito_spinner))))
                .check(matches(withText(is("ámbito de incidencia")))).check(matches(isDisplayed()));

        String[] importancias = mActivity.getResources().getStringArray(R.array.IncidImportanciaArray);
        assertThat(importancias.length, is(5));
        onView(withId(R.id.incid_reg_importancia_spinner))
                .check(matches(withSpinnerText(importancias[0])))
                .check(matches(isDisplayed()));

        onView(withContentDescription("Navigate up")).check(matches(isDisplayed()));
        onView(allOf(
                        withContentDescription("Navigate up"),
                        isClickable())
        ).check(matches(isDisplayed())).perform(click());
    }

    @Test
    public void testOnCreate_2() throws Exception
    {
        int count = mActivity.mRegAcFragment.mImportanciaSpinner.getCount();
        assertThat(count, is(5));
        String item = (String) mActivity.mRegAcFragment.mImportanciaSpinner.getItemAtPosition(1);
        assertThat(item, is("Baja"));
        item = (String) mActivity.mRegAcFragment.mImportanciaSpinner.getItemAtPosition(4);
        assertThat(item, is("Urgente"));

        count = mActivity.mRegAcFragment.mTipoIncidenciaSpinner.getCount();
        assertThat(count, is(AMBITO_INCID_COUNT));
        Cursor cursor = ((CursorAdapter) mActivity.mRegAcFragment.mTipoIncidenciaSpinner.getAdapter()).getCursor();
        cursor.moveToPosition(1);
        assertThat(cursor.getString(1), is("Alarmas comunitarias"));
        cursor.moveToPosition(51);
        assertThat(cursor.getString(1), is("Zonas de juegos"));
    }

    @Test
    public void testRegisterIncidencia_1()
    {
        // Descripción de incidencia no válida.

        onView(withId(R.id.incid_reg_importancia_spinner)).perform(click());
        onData
                (allOf(
                                is(instanceOf(String.class)),
                                is(mActivity.getResources().getStringArray(R.array.IncidImportanciaArray)[4]))
                )
                .perform(click());
        onView(withId(R.id.incid_reg_ambito_spinner)).perform(click());
        onData(withRowString(1, "Calefacción comunitaria")).perform(click());
        onView(withId(R.id.incid_reg_desc_ed)).perform(typeText("descripcion = not valid"));
        IncidenciaBean incidenciaBean = new IncidenciaBean();

        assertThat(incidenciaBean.makeIncidUserComu(mActivity.mRegAcFragment.mFragmentView,
                getErrorMsgBuilder(mActivity), comunidadIdIntent), nullValue());

        onView(withId(R.id.incid_reg_ac_button)).perform(scrollTo(), click());
        checkToastInTest(R.string.error_validation_msg, mActivity, R.string.incid_reg_descripcion);
    }

    @Test
    public void testRegisterIncidencia_2()
    {
        onView(withId(R.id.incid_reg_importancia_spinner)).perform(click());
        onData
                (allOf(
                                is(instanceOf(String.class)),
                                is(mActivity.getResources().getStringArray(R.array.IncidImportanciaArray)[4]))
                )
                .perform(click());
        onView(withId(R.id.incid_reg_ambito_spinner)).perform(click());
        onData(withRowString(1, "Calefacción comunitaria")).perform(click());
        onView(withId(R.id.incid_reg_desc_ed)).perform(typeText("descripcion is valid"));
        onView(withId(R.id.incid_reg_ac_button)).perform(scrollTo(), click());

        onView(withId(R.id.incid_see_by_user_comu_ac)).check(matches(isDisplayed()));
    }
}