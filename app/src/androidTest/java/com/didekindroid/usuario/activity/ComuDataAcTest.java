package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.serviceone.domain.Comunidad;
import com.didekin.serviceone.domain.Municipio;
import com.didekin.serviceone.domain.Provincia;
import com.didekindroid.R;
import com.didekindroid.common.UiException;
import com.didekindroid.usuario.activity.utils.CleanUserEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.CursorMatchers.withRowString;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.common.utils.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.common.utils.AppKeysForBundle.COMUNIDAD_ID;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.activity.utils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.common.utils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.utils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_LA_PLAZUELA_5;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 01/10/15
 * Time: 09:41
 */
@RunWith(AndroidJUnit4.class)
public class ComuDataAcTest {

    private ComuDataAc mActivity;
    CleanUserEnum whatToClean = CLEAN_JUAN;
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
            try {
                signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
                mComunidad = ServOne.getComusByUser().get(0);
            } catch (UiException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent();
            intent.putExtra(COMUNIDAD_ID.extra, mComunidad.getC_Id());
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

        onView(allOf(withId(R.id.app_spinner_1_dropdown_item), withParent(withId(R.id.autonoma_comunidad_spinner))))
                .check(matches(withText(is("Galicia")))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.app_spinner_1_dropdown_item), withParent(withId(R.id.provincia_spinner))))
                .check(matches(withText(is("Lugo")))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.app_spinner_1_dropdown_item), withParent(withId(R.id.municipio_spinner))))
                .check(matches(withText(is("Alfoz")))).check(matches(isDisplayed())).perform(closeSoftKeyboard());

        assertThat(mActivity.mRegComuFrg.getComunidadBean().getMunicipio().getProvincia().getProvinciaId(), is
                (COMU_LA_PLAZUELA_5.getMunicipio().getProvincia().getProvinciaId()));
        assertThat(mActivity.mRegComuFrg.getComunidadBean().getMunicipio().getCodInProvincia(), is
                (COMU_LA_PLAZUELA_5.getMunicipio().getCodInProvincia()));
    }

    @Test
    public void testModifyComuData_1() throws UiException
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
        assertThat(comunidadDb.getMunicipio(), is(new Municipio((short) 119, new Provincia((short) 46))));
        assertThat(comunidadDb.getNombreVia(), is("nombre via One"));
        assertThat(comunidadDb.getNumero(),is((short) 123));
        assertThat(comunidadDb.getSufijoNumero(),is("Tris"));
    }

    @Test
    public void testModifyComuData_2() throws UiException
    {
        onView(withId(R.id.comu_data_ac_button)).check(matches(isDisplayed())).perform(scrollTo(), click());
        onView(withId(R.id.see_usercomu_by_user_ac_frg_container)).check(matches(isDisplayed())).perform(closeSoftKeyboard());

        Comunidad comunidadDb = ServOne.getComuData(mComunidad.getC_Id());
        assertThat(comunidadDb.getMunicipio(), is(COMU_LA_PLAZUELA_5.getMunicipio()));
        assertThat(comunidadDb.getNombreVia(), is(COMU_LA_PLAZUELA_5.getNombreVia()));
    }

    @Test
    public void testModifyComuData_3()
    {
        onView(withId(R.id.autonoma_comunidad_spinner)).perform(click());
        onData(withRowString(1, "comunidad autónoma")).perform(click());

        onView(allOf(withId(R.id.app_spinner_1_dropdown_item), withParent(withId(R.id.autonoma_comunidad_spinner))))
                .check(matches(withText(is("comunidad autónoma")))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.app_spinner_1_dropdown_item), withParent(withId(R.id.provincia_spinner))))
                .check(matches(withText(is("provincia")))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.app_spinner_1_dropdown_item), withParent(withId(R.id.municipio_spinner))))
                .check(matches(withText(is("municipio")))).check(matches(isDisplayed()));

        onView(withId(R.id.comu_data_ac_button)).check(matches(isDisplayed())).perform(scrollTo(), click());
        checkToastInTest(R.string.error_validation_msg, mActivity, R.string.municipio);
    }
}