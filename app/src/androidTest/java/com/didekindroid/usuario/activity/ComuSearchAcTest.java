package com.didekindroid.usuario.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.usuario.activity.utils.CleanEnum;
import com.didekindroid.usuario.dominio.DomainDataUtils;

import junit.framework.AssertionFailedError;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_REAL_JUAN;
import static com.didekindroid.utils.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.*;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeComunidadBeanFromView;
import static com.didekindroid.usuario.activity.utils.UserMenuTestUtils.*;
import static com.didekindroid.security.TokenHandler.TKhandler;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 15/05/15
 * Time: 09:53
 */
@RunWith(AndroidJUnit4.class)
public class ComuSearchAcTest {

    private ComuSearchAc activity;
    Context context;
    private Resources resources;
    private RegComuFr regComuFr;
    File refreshTkFile;
    CleanEnum whatClean;

    @Rule
    public ActivityTestRule<ComuSearchAc> mActivityRule = new ActivityTestRule<>(ComuSearchAc.class, true, false);

    @Before
    public void getFixture() throws Exception
    {
        context = InstrumentationRegistry.getTargetContext();
        refreshTkFile = TKhandler.getRefreshTokenFile();
        resources = context.getResources();
        whatClean = CleanEnum.CLEAN_NOTHING;
    }

    @Test
    public void testPreconditions()
    {
        activity = mActivityRule.launchActivity(new Intent());
        regComuFr = (RegComuFr) activity.getFragmentManager().findFragmentById(R.id.reg_comunidad_frg);

        assertThat(activity, notNullValue());
        assertThat(resources, notNullValue());
        assertThat(regComuFr, notNullValue());
        onView(withId(R.id.reg_comunidad_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.tipo_via_spinner)).check(matches(isDisplayed()));
    }

    @Test
    public void testUpdateIsRegistered_1()
    {
        activity = mActivityRule.launchActivity(new Intent());

        //No token.
        assertThat(refreshTkFile.exists(), is(false));
        assertThat(TKhandler.getAccessTokenInCache(), nullValue());
        assertThat(isRegisteredUser(activity), is(false));
    }

    @Test
    public void testUpdateIsRegistered_2()
    {
        //With token.
        signUpAndUpdateTk(COMU_REAL_JUAN);
        assertThat(refreshTkFile.exists(), is(true));

        activity = mActivityRule.launchActivity(new Intent());
        assertThat(TKhandler.getAccessTokenInCache(), notNullValue());
        assertThat(isRegisteredUser(activity), is(true));

        whatClean = CleanEnum.CLEAN_JUAN;
    }

    @Test
    public void testMakeComunidadBeanFromView()
    {
        activity = mActivityRule.launchActivity(new Intent());
        regComuFr = (RegComuFr) activity.getFragmentManager().findFragmentById(R.id.reg_comunidad_frg);

        // Valor por defecto del spinner.
        assertThat(regComuFr.getComunidadBean().getTipoVia(), is(activity.getResources().getString(R.string
                .tipo_via_spinner)));

        typeComunidadData();

        makeComunidadBeanFromView(regComuFr.getFragmentView(), regComuFr.getComunidadBean());

        assertThat(regComuFr.getComunidadBean().getTipoVia(), is("Calle"));
        assertThat(regComuFr.getComunidadBean().getMunicipio().getProvincia().getProvinciaId(), is((short) 3));
        assertThat(regComuFr.getComunidadBean().getMunicipio().getCodInProvincia(), is((short) 13));
        assertThat(regComuFr.getComunidadBean().getProvincia().getProvinciaId(), is((short) 3));
        assertThat(regComuFr.getComunidadBean().getNombreVia(), is("Real"));
        assertThat(regComuFr.getComunidadBean().getNumeroString(), is("5"));
        assertThat(regComuFr.getComunidadBean().getSufijoNumero(), is("Bis"));
    }

    @Test
    public void searchComunidadWrong() throws InterruptedException
    {
        activity = mActivityRule.launchActivity(new Intent());

        onView(withId(R.id.comunidad_nombre_via_editT)).perform(ViewActions.typeText("select * via"));
        onView(withId(R.id.comunidad_numero_editT)).perform(ViewActions.typeText("123"));
        onView(withId(R.id.comunidad_sufijo_numero_editT)).perform(ViewActions.typeText("Tris"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.searchComunidad_Bton)).perform(ViewActions.click());
        checkToastInTest(R.string.error_validation_msg, activity, R.string.tipo_via, R.string.nombre_via, R.string.municipio);
        Thread.sleep(1000);
    }

    @Test
    public void searchComunidadOK_1() throws InterruptedException
    {
        // Without token.
        activity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(activity), is(false));

        typeComunidadData();

        onView(withId(R.id.searchComunidad_Bton)).perform(click());
        // No results in DB. The user is invited to register.
        checkToastInTest(R.string.no_result_search_comunidad, activity);
        onView(withId(R.id.reg_comu_usuario_usuariocomu_layout)).check(matches(isDisplayed()));

        Thread.sleep(1000);
    }

    @Test
    public void searchComunidadOK_2() throws InterruptedException
    {
        //With token.
        signUpAndUpdateTk(COMU_REAL_JUAN);

        activity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(activity), is(true));

        // Data corresponds to a comunidad in DB.
        typeComunidadData();
        onView(withId(R.id.searchComunidad_Bton)).perform(ViewActions.click());
        // Check the view for comunidades list fragment.
        onView(withId(R.id.comu_search_results_ac_one_pane_frg_container)).check(matches(isDisplayed()));
        onView(withId(R.id.comu_list_frg)).check(matches(isDisplayed()));

        whatClean = CleanEnum.CLEAN_JUAN;
    }

    @Test
    public void testGetDatosUsuarioNoToken() throws InterruptedException
    {
        assertThat(refreshTkFile.exists(), is(false));
        activity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(activity), is(false));
        USER_DATA_AC.checkMenuItem_NTk(activity);
    }

    @Test
    public void testGetDatosUsuarioWithToken() throws InterruptedException
    {
        whatClean = CleanEnum.CLEAN_JUAN;

        //With token.
        signUpAndUpdateTk(COMU_REAL_JUAN);
        activity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(activity), is(true));
        USER_DATA_AC.checkMenuItem_WTk(activity);
    }

    @Test
    public void testMenuNuevaComunidad_noToken() throws InterruptedException
    {
        assertThat(refreshTkFile.exists(), is(false));
        activity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(activity), is(false));
        REG_COMU_USER_USERCOMU_AC.checkMenuItem_NTk(activity);
    }

    @Test
    public void testMenuNuevaComunidad_withToken() throws InterruptedException
    {
        whatClean = CleanEnum.CLEAN_JUAN;

        signUpAndUpdateTk(COMU_REAL_JUAN);
        activity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(activity), is(true));
        REG_COMU_USER_USERCOMU_AC.checkMenuItem_WTk(activity);
    }

    @Test
    public void testComunidadesByUsuario_withToken() throws InterruptedException
    {
        whatClean = CleanEnum.CLEAN_JUAN;

        signUpAndUpdateTk(COMU_REAL_JUAN);
        activity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(activity), is(true));
        SEE_USERCOMU_BY_USER_AC.checkMenuItem_WTk(activity);
    }

    @Test
    public void tesComunidadesByUsuario_noToken() throws InterruptedException
    {
        assertThat(refreshTkFile.exists(), is(false));
        activity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(activity), is(false));
        SEE_USERCOMU_BY_USER_AC.checkMenuItem_NTk(activity);
    }

    @Test
    public void testLogin_withToken() throws InterruptedException
    {

        whatClean = CleanEnum.CLEAN_JUAN;
        signUpAndUpdateTk(COMU_REAL_JUAN);
        activity = mActivityRule.launchActivity(new Intent());

        onView(withId(R.id.login_ac_mn)).check(doesNotExist());
        openActionBarOverflowOrOptionsMenu(activity);
        onView(withId(R.id.login_ac_mn)).check(doesNotExist());
    }

    @Test
    public void testLogin_withoutToken() throws InterruptedException
    {
        activity = mActivityRule.launchActivity(new Intent());

        try {
            onView(withId(R.id.login_ac_mn)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            openActionBarOverflowOrOptionsMenu(activity);
            onView(withId(R.id.login_ac_mn)).check(matches(isDisplayed()));
        }

        LOGIN_AC.checkMenuItem_NTk(activity);
    }


    @After
    public void cleanData()
    {
        cleanOptions(whatClean);

    }

//    ................ UTILIDADES .....................

}