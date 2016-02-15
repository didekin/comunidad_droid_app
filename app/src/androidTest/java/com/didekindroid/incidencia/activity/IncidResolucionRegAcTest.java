package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.DatePicker;

import com.didekin.incidservice.dominio.Incidencia;
import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.UiException;
import com.didekindroid.common.utils.UIutils;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.PickerActions.setDate;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.common.utils.ActivityTestUtils.checkNavigateUp;
import static com.didekindroid.common.utils.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.common.utils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.utils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.utils.AppKeysForBundle.INCIDENCIA_USER_OBJECT;
import static com.didekindroid.incidencia.IncidenciaTestUtils.doIncidencia;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.activity.utils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_REAL_JUAN;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 13/02/16
 * Time: 15:14
 */
@RunWith(AndroidJUnit4.class)
public class IncidResolucionRegAcTest {

    IncidenciaUser incidJuanReal1;
    IncidResolucionRegAc mActivity;
    IncidResolucionRegAcFragment mFragment;

    @Rule
    public IntentsTestRule<IncidResolucionRegAc> intentRule = new IntentsTestRule<IncidResolucionRegAc>(IncidResolucionRegAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            super.beforeActivityLaunched();
        }

        /**
         * Preconditions:
         * 1. An IncidenciaUser with powers to resolve an incidencia is received.
         * */
        @Override
        protected Intent getActivityIntent()
        {
            try {
                signUpAndUpdateTk(COMU_REAL_JUAN);
                UsuarioComunidad juanReal = ServOne.seeUserComusByUser().get(0);
                incidJuanReal1 = new IncidenciaUser.IncidenciaUserBuilder(doIncidencia("Incidencia Real One", juanReal.getComunidad().getC_Id(), (short) 43))
                        .usuario(juanReal)
                        .importancia((short) 3).build();
                IncidenciaServ.regIncidenciaUser(incidJuanReal1);
                Incidencia incidenciaDb = IncidenciaServ.incidSeeByComu(juanReal.getComunidad().getC_Id()).get(0).getIncidencia();
                incidJuanReal1 = IncidenciaServ.getIncidenciaUserWithPowers(incidenciaDb.getIncidenciaId());
            } catch (UiException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent();
            intent.putExtra(INCIDENCIA_USER_OBJECT.extra, incidJuanReal1);
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
        Thread.sleep(3000);
        mActivity = intentRule.getActivity();
        mFragment = (IncidResolucionRegAcFragment) mActivity.getFragmentManager()
                .findFragmentById(R.id.incid_resolucion_reg_frg);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void testOnCreate_1() throws Exception
    {
        assertThat(mActivity, notNullValue());
        assertThat(mFragment, notNullValue());
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_reg_ac_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_reg_ac_button)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_desc_ed)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_fecha_view)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_fecha_view)).check(matches(isDisplayed()));

        checkNavigateUp();
    }

    @Test
    public void testOnCreate_2() throws Exception
    {
        // DatePicker tests.
        onView(withId(R.id.incid_resolucion_fecha_view)).check(matches(isDisplayed())).perform(click());
        onView(withClassName(is(DatePicker.class.getName()))).inRoot(isDialog()).check(matches(isDisplayed()));
        // We pick a date.
        onView(withClassName(is(DatePicker.class.getName()))).perform(setDate(2016, 3, 21));
        onView(withText(mActivity.getString(android.R.string.ok))).perform(click());
        if (Locale.getDefault().equals(UIutils.SPAIN_LOCALE)) {
            onView(allOf(
                    withId(R.id.incid_resolucion_fecha_view),
                    withText("21/3/2016")
            )).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testOnEdit_1()
    {
        // Descripción errónea y fecha sin fijar: error sólo de fecha.
        onView(withId(R.id.incid_resolucion_desc_ed)).perform(replaceText("Desc * no válida"));
        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());
        checkToastInTest(R.string.error_validation_msg, mActivity,
                R.string.incid_resolucion_fecha_prev_msg);
    }

    @Test
    public void testOnEdit_2()
    {
        // Descripción errónea.
        setFecha(setDate(2016, 3, 22));
        onView(withId(R.id.incid_resolucion_desc_ed)).perform(replaceText("Desc * no válida"));
        setFecha(setDate(2016, 3, 21));

        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());
        checkToastInTest(R.string.error_validation_msg, mActivity,
                R.string.incid_resolucion_descrip_msg);
    }

    @Test
    public void testOnEdit_3()
    {
        // Coste erróneo.
        setFecha(setDate(2016, 3, 22));
        onView(withId(R.id.incid_resolucion_desc_ed)).perform(replaceText("Desc válida"));
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).perform(replaceText("novalid"));

        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());
        checkToastInTest(R.string.error_validation_msg, mActivity,
                R.string.incid_resolucion_coste_prev_msg);
    }

    @Test
    public void testOnEdit_4()
    {
        // Coste y descripción erróneos.
        setFecha(setDate(2016, 3, 22));
        onView(withId(R.id.incid_resolucion_desc_ed)).perform(replaceText("Desc * no válida"));
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).perform(replaceText("novalid"));

        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());
        checkToastInTest(R.string.error_validation_msg, mActivity,
                R.string.incid_resolucion_coste_prev_msg,
                R.string.incid_resolucion_descrip_msg);
    }

//    ============================= HELPER METHODS ===========================

    private void setFecha(ViewAction viewAction)
    {
        onView(withId(R.id.incid_resolucion_fecha_view)).perform(click());
        onView(withClassName(is(DatePicker.class.getName()))).perform(viewAction);
        onView(withText(mActivity.getString(android.R.string.ok))).perform(click());
    }

}