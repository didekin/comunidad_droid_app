package com.didekindroid.usuario.comunidad;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import com.didekindroid.DidekindroidApp;
import com.didekindroid.R;
import com.didekindroid.masterdata.dominio.Municipio;
import com.didekindroid.masterdata.dominio.Provincia;
import com.didekindroid.usuario.comunidad.dominio.Comunidad;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.common.DeviceTestUtils.getHightDevice;
import static com.didekindroid.common.DeviceTestUtils.getWidthDevice;
import static com.didekindroid.common.ui.UIutils.isRegisteredUser;
import static com.didekindroid.common.ui.UIutils.updateIsRegistered;
import static com.didekindroid.common.ui.ViewsIDs.COMUNIDADES_FOUND;
import static com.didekindroid.usuario.DataUsuarioTestUtils.*;
import static com.didekindroid.usuario.common.UserIntentExtras.COMUNIDAD_SEARCH;
import static com.didekindroid.usuario.common.UserMenuTest.*;
import static com.didekindroid.usuario.login.TokenHandler.TKhandler;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static com.google.android.apps.common.testing.ui.espresso.sample.LongListMatchers.withAdaptedData;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/05/15
 * Time: 15:00
 */
@RunWith(AndroidJUnit4.class)
public class ComuSearchResultsAcTest {

    private static final String TAG = "ComunidadSeeActivTest";

    private ComuSearchResultsAc activity;
    ComuListFr mComunidadSummaryFrg;
    File refreshTkFile;
    Intent intent;

    @Rule
    public ActivityTestRule<ComuSearchResultsAc> mActivityRule =
            new ActivityTestRule<>(ComuSearchResultsAc.class, true, false);

    @Before
    public void getFixture() throws Exception
    {
        Log.d(TAG, "In getFixture()");
        refreshTkFile = TKhandler.getRefreshTokenFile();
        Comunidad comunidad = new Comunidad("Ronda", "de la Plazuela", (short) 5, null,
                new Municipio(new Provincia((short) 27), (short) 2));
        intent = new Intent();
        intent.putExtra(COMUNIDAD_SEARCH.extra, comunidad);
    }

    @Test
    public void testOnCreate() throws Exception
    {
        activity = mActivityRule.launchActivity(intent);
        mComunidadSummaryFrg = (ComuListFr) activity.getFragmentManager().findFragmentById(R.id.comunidades_summary_frg);

        Log.d(TAG, "In testOnCreate() width= " + getWidthDevice(activity));
        Log.d(TAG, "In testOnCreate() height= " + getHightDevice(activity));

        assertThat(activity, notNullValue());
        assertThat(mComunidadSummaryFrg, notNullValue());


        onView(withId(R.id.comunidades_see_one_pane_frg_container)).check(matches(isDisplayed()));
        onView(withId(R.id.comunidades_summary_frg)).check(matches(isDisplayed()));
    }

    @Test
    public void testComunidadesUsuarioGetter_1()
    {
        // No token in cache.
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));
        assertThat(activity.mUsuarioComunidades, nullValue());
    }

    @Test
    public void testComunidadesUsuarioGetter_2()
    {
        insertOneUserTwoComu();

        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));
        assertThat(activity.mUsuarioComunidades.size(), is(2));
        assertThat(activity.mUsuarioComunidades, hasItems(USUARIO_COMUNIDAD_1, USUARIO_COMUNIDAD_2));

        // User cleanup.
        boolean isDeleted = ServOne.deleteUser();
        assertThat(isDeleted, is(true));
    }

    @Test
    public void testGetDatosUsuarioNoToken() throws InterruptedException
    {
        assertThat(refreshTkFile.exists(), is(false));
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));
        USER_DATA_AC.checkMenuItem_NTk(activity);
    }

    @Test
    public void testGetDatosUsuarioWithToken()
    {
        //With token.
        insertOneUserOneComu();
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));
        USER_DATA_AC.checkMenuItem_WTk(activity);
    }

    @Test
    public void testMenuNuevaComunidad_noToken() throws InterruptedException
    {
        assertThat(refreshTkFile.exists(), is(false));
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));
        REG_COMU_USER_USERCOMU_AC.checkMenuItem_NTk(activity);
    }

    @Test
    public void testMenuNuevaComunidad_withToken()
    {
        insertOneUserOneComu();
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));
        REG_COMU_USER_USERCOMU_AC.checkMenuItem_WTk(activity);
    }

    @Test
    public void testComunidadesByUsuario_withToken() throws InterruptedException
    {
        insertOneUserOneComu();
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));
        Thread.sleep(1000); // Waiting for a previous toast to disappear.
        COMU_BY_USER_LIST_AC.checkMenuItem_WTk(activity);
    }

    @Test
    public void tesComunidadesByUsuario_noToken() throws InterruptedException
    {
        assertThat(refreshTkFile.exists(), is(false));
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));
        COMU_BY_USER_LIST_AC.checkMenuItem_NTk(activity);
    }

    @Test
    public void testSearchComunidades_1()
    {
        // User with 2 comunidades. We search with one of them exactly.
        insertOneUserTwoComu();
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));
        mComunidadSummaryFrg = (ComuListFr) activity.getFragmentManager().findFragmentById(R.id.comunidades_summary_frg);
        ComuListAdapter adapter = (ComuListAdapter) mComunidadSummaryFrg.getListAdapter();

        assertThat(adapter.getCount(), is(1));
        onView(withId(COMUNIDADES_FOUND.idView)).check(
                matches(withAdaptedData(
                        equalTo((Object) intent.getSerializableExtra(COMUNIDAD_SEARCH.extra)))));

        // User cleanup.
        boolean isDeleted = ServOne.deleteUser();
        assertThat(isDeleted, is(true));
    }

    @Test
    public void testSearchComunidades_2()
    {
        Comunidad comunidadNew = new Comunidad("Ronda", "del Norte", (short) 5, null,
                new Municipio(new Provincia((short) 27), (short) 2));
        insertOnePlusComu(comunidadNew);

        // Criterio de búsqueda.
        Comunidad comunidad = new Comunidad("Ronda", "de la Plazuela del Norte", (short) 5, null,
                new Municipio(new Provincia((short) 27), (short) 2));
        Intent intent = new Intent();
        intent.putExtra(COMUNIDAD_SEARCH.extra, comunidad);
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));

        mComunidadSummaryFrg = (ComuListFr) activity.getFragmentManager().findFragmentById(R.id.comunidades_summary_frg);
        ComuListAdapter adapter = (ComuListAdapter) mComunidadSummaryFrg.getListAdapter();
        assertThat(adapter.getCount(), is(2));
        onView(withId(COMUNIDADES_FOUND.idView)).check(
                matches(withAdaptedData(hasProperty("nombreVia", is("del Norte")))));
        onView(withId(COMUNIDADES_FOUND.idView)).check(
                        matches(withAdaptedData(hasProperty("nombreVia", is("de la Plazuela")))));

        // User cleanup.
        boolean isDeleted = ServOne.deleteUser();
        assertThat(isDeleted, is(true));
    }

    @After
    public void cleanData()
    {
        if (refreshTkFile.exists()) {
            refreshTkFile.delete();
        }
        TKhandler.getTokensCache().invalidateAll();
        TKhandler.updateRefreshToken(null);
        updateIsRegistered(false, DidekindroidApp.getContext());
    }
}