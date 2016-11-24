package com.didekinaar.comunidad;

import android.content.Intent;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.comunidad.Comunidad;
import com.didekin.comunidad.Municipio;
import com.didekin.comunidad.Provincia;
import com.didekinaar.R;
import com.didekinaar.exception.UiAarException;
import com.didekinaar.testutil.AarActivityTestUtils;
import com.didekinaar.testutil.CleanUserEnum;
import com.didekinaar.testutil.UserMenuTestUtils;
import com.didekinaar.testutil.UsuarioTestUtils;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.matcher.CursorMatchers.withRowString;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekinaar.comunidad.AarComunidadService.AarComuServ;
import static com.didekinaar.testutil.AarActivityTestUtils.checkToastInTest;
import static com.didekinaar.testutil.AarActivityTestUtils.checkUp;
import static com.didekinaar.testutil.AarActivityTestUtils.cleanOptions;
import static com.didekinaar.testutil.CleanUserEnum.CLEAN_JUAN;
import static com.didekinaar.usuariocomunidad.AarUserComuService.AarUserComuServ;
import static com.didekinaar.utils.UIutils.isRegisteredUser;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.AllOf.allOf;
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
    int activityLayoutId = R.id.comu_data_ac_layout;

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
                AarActivityTestUtils.signUpAndUpdateTk(UsuarioTestUtils.COMU_PLAZUELA5_JUAN);
                mComunidad = AarUserComuServ.getComusByUser().get(0);
            } catch (UiAarException | IOException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent();
            intent.putExtra(ComuBundleKey.COMUNIDAD_ID.key, mComunidad.getC_Id());
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

        onView(withId(activityLayoutId)).check(matches(isDisplayed()));

        onView(ViewMatchers.withId(R.id.comunidad_nombre_via_editT))
                .check(matches(withText(is(mComunidad.getNombreVia()))))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.comunidad_numero_editT))
                .check(matches(withText(containsString(String.valueOf(mComunidad.getNumero())))))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.comunidad_sufijo_numero_editT))
                .check(matches(withText(is(mComunidad.getSufijoNumero()))))
                .check(matches(isDisplayed()));

        assertThat(mActivity.mRegComuFrg.getComunidadBean().getTipoVia(), is(mComunidad.getTipoVia()));

        onView(allOf(ViewMatchers.withId(R.id.app_spinner_1_dropdown_item), withParent(ViewMatchers.withId(R.id.tipo_via_spinner))))
                .check(matches(withText(is(mComunidad.getTipoVia())))).check(matches(isDisplayed()));

        onView(allOf(ViewMatchers.withId(R.id.app_spinner_1_dropdown_item), withParent(ViewMatchers.withId(R.id.autonoma_comunidad_spinner))))
                .check(matches(withText(is("Galicia")))).check(matches(isDisplayed()));
        onView(allOf(ViewMatchers.withId(R.id.app_spinner_1_dropdown_item), withParent(ViewMatchers.withId(R.id.provincia_spinner))))
                .check(matches(withText(is("Lugo")))).check(matches(isDisplayed()));
        onView(allOf(ViewMatchers.withId(R.id.app_spinner_1_dropdown_item), withParent(ViewMatchers.withId(R.id.municipio_spinner))))
                .check(matches(withText(is("Alfoz")))).check(matches(isDisplayed())).perform(closeSoftKeyboard());

        onView(ViewMatchers.withId(R.id.comu_data_ac_button)).check(matches(isDisplayed()));

        assertThat(mActivity.mRegComuFrg.getComunidadBean().getMunicipio().getProvincia().getProvinciaId(), Matchers.is
                (UsuarioTestUtils.COMU_LA_PLAZUELA_5.getMunicipio().getProvincia().getProvinciaId()));
        assertThat(mActivity.mRegComuFrg.getComunidadBean().getMunicipio().getCodInProvincia(), Matchers.is
                (UsuarioTestUtils.COMU_LA_PLAZUELA_5.getMunicipio().getCodInProvincia()));
    }

    @Test
    public void testModifyComuData_1() throws UiAarException, InterruptedException
    {
        // Comunidad data.
        onView(ViewMatchers.withId(R.id.comunidad_nombre_via_editT)).perform(scrollTo(), replaceText("nombre via One"));
        onView(ViewMatchers.withId(R.id.comunidad_numero_editT)).perform(scrollTo(), replaceText("123"));
        onView(ViewMatchers.withId(R.id.comunidad_sufijo_numero_editT)).perform(scrollTo(), replaceText("Tris"));

        onView(ViewMatchers.withId(R.id.autonoma_comunidad_spinner)).perform(click());
        Thread.sleep(1000);
        onData(withRowString(1, "Valencia")).perform(click());

        onView(ViewMatchers.withId(R.id.provincia_spinner)).perform(click());
        Thread.sleep(1000);
        onData(withRowString(1, "Valencia/València")).perform(click());

        onView(ViewMatchers.withId(R.id.municipio_spinner)).perform(click());
        Thread.sleep(1000);
        onData(withRowString(3, "Ènova, l'")).perform(click());

        // Modificamos.
        onView(ViewMatchers.withId(R.id.comu_data_ac_button)).check(matches(isDisplayed())).perform(click());
        // Verificamos cambios.
        onView(ViewMatchers.withId(R.id.see_usercomu_by_user_frg)).check(matches(isDisplayed()));
        Comunidad comunidadDb = AarComuServ.getComuData(mComunidad.getC_Id());
        assertThat(comunidadDb != null ? comunidadDb.getMunicipio() : null, is(new Municipio((short) 119, new Provincia((short) 46))));
        assertThat(comunidadDb != null ? comunidadDb.getNombreVia() : null, is("nombre via One"));
        assertThat(comunidadDb != null ? comunidadDb.getNumero() : 0,is((short) 123));
        assertThat(comunidadDb != null ? comunidadDb.getSufijoNumero() : null,is("Tris"));

        checkUp(activityLayoutId);
    }

    @Test
    public void testModifyComuData_2() throws UiAarException
    {
        onView(ViewMatchers.withId(R.id.comu_data_ac_button)).check(matches(isDisplayed())).perform(scrollTo(), click());
        onView(ViewMatchers.withId(R.id.see_usercomu_by_user_frg)).check(matches(isDisplayed())).perform(closeSoftKeyboard());

        Comunidad comunidadDb = AarComuServ.getComuData(mComunidad.getC_Id());
        assertThat(comunidadDb != null ? comunidadDb.getMunicipio() : null, Matchers.is(UsuarioTestUtils.COMU_LA_PLAZUELA_5.getMunicipio()));
        assertThat(comunidadDb != null ? comunidadDb.getNombreVia() : null, Matchers.is(UsuarioTestUtils.COMU_LA_PLAZUELA_5.getNombreVia()));

        checkUp(activityLayoutId);
    }

    @Test
    public void testModifyComuData_3()
    {
        onView(ViewMatchers.withId(R.id.autonoma_comunidad_spinner)).perform(click());
        onData(withRowString(1, "comunidad autónoma")).perform(click());

        onView(allOf(ViewMatchers.withId(R.id.app_spinner_1_dropdown_item), withParent(ViewMatchers.withId(R.id.autonoma_comunidad_spinner))))
                .check(matches(withText(is("comunidad autónoma")))).check(matches(isDisplayed()));
        onView(allOf(ViewMatchers.withId(R.id.app_spinner_1_dropdown_item), withParent(ViewMatchers.withId(R.id.provincia_spinner))))
                .check(matches(withText(is("provincia")))).check(matches(isDisplayed()));
        onView(allOf(ViewMatchers.withId(R.id.app_spinner_1_dropdown_item), withParent(ViewMatchers.withId(R.id.municipio_spinner))))
                .check(matches(withText(is("municipio")))).check(matches(isDisplayed()));

        onView(ViewMatchers.withId(R.id.comu_data_ac_button)).check(matches(isDisplayed())).perform(scrollTo(), click());
        checkToastInTest(R.string.error_validation_msg, mActivity, R.string.municipio);
    }

//     ==================== MENU ====================

    @Test
    public void testSeeUserComuByComuMn() throws InterruptedException
    {
        UserMenuTestUtils.SEE_USERCOMU_BY_COMU_AC.checkMenuItem_WTk(mActivity);
        intended(IntentMatchers.hasExtra(ComuBundleKey.COMUNIDAD_ID.key,mComunidad.getC_Id()));
        checkUp(activityLayoutId);
    }
}