package com.didekindroid.usuario.comunidad;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.didekindroid.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.didekindroid.common.ui.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.DataUsuarioTestUtils.insertOneUserOneComu;
import static com.didekindroid.usuario.DataUsuarioTestUtils.typeComunidadData;
import static com.didekindroid.usuario.common.UserMenuTest.*;
import static com.didekindroid.usuario.common.UsuarioComunidadFiller.makeComunidadBeanFromView;
import static com.didekindroid.usuario.login.TokenHandler.TKhandler;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
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

    @Rule
    public ActivityTestRule<ComuSearchAc> mActivityRule = new ActivityTestRule<>(ComuSearchAc.class, true, false);

    @Before
    public void getFixture() throws Exception
    {
        context = InstrumentationRegistry.getTargetContext();
        refreshTkFile = TKhandler.getRefreshTokenFile();
        resources = context.getResources();
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
        assertThat(isRegisteredUser(activity), is(false));
        assertThat(refreshTkFile.exists(), is(false));
    }

    @Test
    public void testUpdateIsRegistered_2()
    {
        //With token.
        insertOneUserOneComu();
        assertThat(refreshTkFile.exists(), is(true));

        activity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(activity), is(true));

        // User clean up.
        assertThat(ServOne.deleteUser(), is(true));
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

        assertThat(regComuFr.getComunidadBean().getTipoVia(), is("Callejon"));
        assertThat(regComuFr.getComunidadBean().getMunicipio().getProvincia().getProvinciaId(), is((short) 12));
        assertThat(regComuFr.getComunidadBean().getMunicipio().getCodInProvincia(), is((short) 53));
        assertThat(regComuFr.getComunidadBean().getProvincia().getProvinciaId(), is((short) 12));
        assertThat(regComuFr.getComunidadBean().getNombreVia(), is("nombre via One"));
        assertThat(regComuFr.getComunidadBean().getNumeroString(), is("123"));
        assertThat(regComuFr.getComunidadBean().getSufijoNumero(), is("Tris"));
    }


    @Test
    public void searchComunidadWrong()
    {
        activity = mActivityRule.launchActivity(new Intent());

        onView(withId(R.id.comunidad_nombre_via_editT)).perform(ViewActions.typeText("select * via"));
        onView(withId(R.id.comunidad_numero_editT)).perform(ViewActions.typeText("123"));
        onView(withId(R.id.comunidad_sufijo_numero_editT)).perform(ViewActions.typeText("Tris"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.searchComunidad_Bton)).perform(ViewActions.click());

        ViewInteraction toastViewInteraction = onView(withText(
                containsString(resources.getText(R.string.error_validation_msg).toString())
        ));

        toastViewInteraction.inRoot(withDecorView(not(activity.getWindow().getDecorView())))
                .check(matches(isDisplayed()))
                .check(matches(withText(containsString(resources.getText(R.string.tipo_via).toString()))))
                .check(matches(withText(containsString(resources.getText(R.string.nombre_via).toString()))))
                .check(matches(withText(containsString(resources.getText(R.string.municipio).toString()))));
    }

    @Test
    public void searchComunidadOK_1()
    {
        // Without token.
        activity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(activity), is(false));

        typeComunidadData();

        onView(withId(R.id.searchComunidad_Bton)).perform(click());
        // Check the view for comunidades list fragment.
        onView(withId(R.id.comunidades_summary_frg)).check(matches(isDisplayed()));
    }

    @Test
    public void searchComunidadOK_2() throws InterruptedException
    {
        //With token.
        insertOneUserOneComu();

        activity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(activity), is(true));

        typeComunidadData();
        onView(withId(R.id.searchComunidad_Bton)).perform(ViewActions.click());
        // Check the view for comunidades list fragment.
        onView(withId(R.id.comunidades_summary_frg)).check(matches(isDisplayed()));

        // User clean up.
        assertThat(ServOne.deleteUser(), is(true));
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
    public void testGetDatosUsuarioWithToken()
    {
        //With token.
        insertOneUserOneComu();
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
    public void testMenuNuevaComunidad_withToken()
    {
        insertOneUserOneComu();
        activity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(activity), is(true));
        REG_COMU_USER_USERCOMU_AC.checkMenuItem_WTk(activity);
    }

    @Test
    public void testComunidadesByUsuario_withToken() throws InterruptedException
    {
        insertOneUserOneComu();
        activity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(activity), is(true));
        COMU_BY_USER_LIST_AC.checkMenuItem_WTk(activity);
    }

    @Test
    public void tesComunidadesByUsuario_noToken() throws InterruptedException
    {
        assertThat(refreshTkFile.exists(), is(false));
        activity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(activity), is(false));
        COMU_BY_USER_LIST_AC.checkMenuItem_NTk(activity);
    }

    @After
    public void cleanData()
    {
        if (refreshTkFile.exists()) {
            refreshTkFile.delete();
        }
        TKhandler.getTokensCache().invalidateAll();
        TKhandler.updateRefreshToken(null);
    }

//    ................ UTILIDADES .....................

}