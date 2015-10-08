package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.didekin.serviceone.domain.Comunidad;
import com.didekin.serviceone.domain.Municipio;
import com.didekin.serviceone.domain.Provincia;
import com.didekindroid.R;
import com.didekindroid.usuario.activity.utils.CleanEnum;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.CursorMatchers.withRowString;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.didekindroid.uiutils.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.activity.utils.CleanEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.COMUNIDAD_ID;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.checkToastInTest;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.cleanOptions;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.signUpAndUpdateTk;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_LA_PLAZUELA_5;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 01/10/15
 * Time: 09:41
 */
@RunWith(AndroidJUnit4.class)
public class ComuDataAcTest {

    private ComuDataAc mActivity;
    CleanEnum whatToClean = CLEAN_JUAN;
    Comunidad mComunidad;

    @Rule
    public IntentsTestRule<ComuDataAc> intentRule = new IntentsTestRule<ComuDataAc>(ComuDataAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {
        }

        @Override
        protected Intent getActivityIntent()
        {
            signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
            mComunidad = ServOne.getComusByUser().get(0);
            Intent intent = new Intent();
            intent.putExtra(COMUNIDAD_ID.extra, mComunidad.getC_Id());
            return intent;
        }
    };

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

//    =============================================================================================

    @Test
    public void testOnCreate() throws Exception
    {
        assertThat(isRegisteredUser(mActivity), is(true));
        assertThat(mActivity.mIdComunidad > 0, is(true));
        assertThat(mActivity.mAcView, notNullValue());
        assertThat(mActivity.mRegComuFrg, notNullValue());

        onView(withId(R.id.comu_data_ac_button)).check(matches(isDisplayed()));
        onView(withId(R.id.comunidad_nombre_via_editT))
                .check(matches(withText(is(mComunidad.getNombreVia()))))
                .check(matches(isDisplayed()));
        onView(withId(R.id.comunidad_numero_editT))
                .check(matches(withText(containsString(String.valueOf(mComunidad.getNumero())))))
                .check(matches(isDisplayed()));
        onView(withId(R.id.comunidad_sufijo_numero_editT))
                .check(matches(withText(is(mComunidad.getSufijoNumero()))))
                .check(matches(isDisplayed()));

        onView(withId(R.id.tipo_via_spinner)).check(matches(withSpinnerText(mComunidad.getTipoVia())))
                .check(matches(isDisplayed()));
        assertThat(mActivity.mRegComuFrg.getComunidadBean().getTipoVia(), is(mComunidad.getTipoVia()));

        onView(allOf(withId(R.id.reg_comunidad_spinner_dropdown_item), withParent(withId(R.id.autonoma_comunidad_spinner))))
                .check(matches(withText(is("Galicia")))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.reg_comunidad_spinner_dropdown_item), withParent(withId(R.id.provincia_spinner))))
                .check(matches(withText(is("Lugo")))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.reg_comunidad_spinner_dropdown_item), withParent(withId(R.id.municipio_spinner))))
                .check(matches(withText(is("Alfoz")))).check(matches(isDisplayed()));

        assertThat(mActivity.mRegComuFrg.getComunidadBean().getMunicipio().getProvincia().getProvinciaId(), is
                (COMU_LA_PLAZUELA_5.getMunicipio().getProvincia().getProvinciaId()));
        assertThat(mActivity.mRegComuFrg.getComunidadBean().getMunicipio().getCodInProvincia(), is
                (COMU_LA_PLAZUELA_5.getMunicipio().getCodInProvincia()));
    }

    @Test
    public void testModifyComuData_1()
    {
        // Comunidad data.
        onView(withId(R.id.comunidad_nombre_via_editT)).perform(scrollTo(), replaceText("nombre via One"));
        onView(withId(R.id.comunidad_numero_editT)).perform(scrollTo(), replaceText("123"));
        onView(withId(R.id.comunidad_sufijo_numero_editT)).perform(scrollTo(), replaceText("Tris"));

        onView(withId(R.id.autonoma_comunidad_spinner)).perform(click());
        onData(withRowString(1, "Valencia")).perform(click());

        onView(withId(R.id.provincia_spinner)).perform(click());
        onData(withRowString(1, "Valencia/València")).perform(click());

        onView(withId(R.id.municipio_spinner)).perform(click());
        onData(withRowString(3, "Ènova, l'")).perform(click());

        onView(withId(R.id.comu_data_ac_button)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.see_usercomu_by_user_ac_frg_container)).check(matches(isDisplayed()));

        Comunidad comunidadDb = ServOne.getComuData(mComunidad.getC_Id());
        assertThat(comunidadDb, Matchers.allOf(
                hasProperty("municipio", equalTo(new Municipio((short) 119, new Provincia((short) 46)))),
                hasProperty("nombreVia", equalTo("nombre via One")),
                hasProperty("numero",equalTo((short)123)),
                hasProperty("sufijoNumero",equalTo("Tris"))
        ));
    }

    @Test
    public void testModifyComuData_2()
    {
        onView(withId(R.id.comu_data_ac_button)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.see_usercomu_by_user_ac_frg_container)).check(matches(isDisplayed()));

        Comunidad comunidadDb = ServOne.getComuData(mComunidad.getC_Id());
        assertThat(comunidadDb, Matchers.allOf(
                hasProperty("municipio", equalTo(COMU_LA_PLAZUELA_5.getMunicipio())),
                hasProperty("nombreVia", equalTo(COMU_LA_PLAZUELA_5.getNombreVia()))
        ));
    }

    @Test
    public void testModifyComuData_3()
    {
        onView(withId(R.id.autonoma_comunidad_spinner)).perform(click());
        onData(withRowString(1, "comunidad autónoma")).perform(click());

        onView(allOf(withId(R.id.reg_comunidad_spinner_dropdown_item), withParent(withId(R.id.autonoma_comunidad_spinner))))
                .check(matches(withText(is("comunidad autónoma")))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.reg_comunidad_spinner_dropdown_item), withParent(withId(R.id.provincia_spinner))))
                .check(matches(withText(is("provincia")))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.reg_comunidad_spinner_dropdown_item), withParent(withId(R.id.municipio_spinner))))
                .check(matches(withText(is("municipio")))).check(matches(isDisplayed()));

        onView(withId(R.id.comu_data_ac_button)).check(matches(isDisplayed())).perform(click());
        checkToastInTest(R.string.error_validation_msg, mActivity,R.string.municipio);
    }
}