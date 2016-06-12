package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.usuario.testutils.CleanUserEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.common.activity.TokenHandler.TKhandler;
import static com.didekindroid.common.activity.BundleKey.COMUNIDAD_SEARCH;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanWithTkhandler;
import static com.didekindroid.common.testutils.ActivityTestUtils.regTwoUserComuSameUser;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutils.UserMenuTestUtils.REG_COMU_USER_USERCOMU_AC;
import static com.didekindroid.usuario.testutils.UserMenuTestUtils.SEE_USERCOMU_BY_USER_AC;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_LA_PLAZUELA_5;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.makeListTwoUserComu;
import static com.google.common.base.Preconditions.checkState;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 09/10/15
 * Time: 13:04
 */


@RunWith(AndroidJUnit4.class)
public class ComuSearchResultsAc_SlowTest {

    private static final String TAG = "ComunidadSeeActivTest";

    private ComuSearchResultsAc activity;
    ComuSearchResultsListFr mComunidadSummaryFrg;
    Intent intent;
    CleanUserEnum whatClean;

    @Rule
    public ActivityTestRule<ComuSearchResultsAc> mActivityRule =
            new ActivityTestRule<>(ComuSearchResultsAc.class, true, false);

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(5000);
    }

    @Before
    public void getFixture() throws Exception
    {
        Thread.sleep(6000);

        Log.d(TAG, "In getFixture()");
        whatClean = CleanUserEnum.CLEAN_NOTHING;

        intent = new Intent();
        intent.putExtra(COMUNIDAD_SEARCH.key, COMU_LA_PLAZUELA_5);
    }

    @Test
    public void testOnCreate_1() throws Exception
    {
        activity = mActivityRule.launchActivity(intent);
        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getFragmentManager().findFragmentById(R.id.comu_list_frg);

        assertThat(activity, notNullValue());
        assertThat(mComunidadSummaryFrg, notNullValue());
        // No results in DB. The user is invited to register.
        checkToastInTest(R.string.no_result_search_comunidad, activity);
        onView(withId(R.id.reg_comu_usuario_usuariocomu_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void testMenuNuevaComunidad_noToken_1() throws InterruptedException, UiException
    {
        assertThat(TKhandler.getAccessTokenInCache(), nullValue());
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));

        // No results in DB. The user is invited to register.
        checkToastInTest(R.string.no_result_search_comunidad, activity);
        onView(withId(R.id.reg_comu_usuario_usuariocomu_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void testMenuNuevaComunidad_noToken_2() throws InterruptedException, UiException, IOException
    {
        whatClean = CLEAN_JUAN;

        //Usuario no registrado. La búsqueda devuelve una comunidad.
        regTwoUserComuSameUser(makeListTwoUserComu());
        // Borro los datos del userComu.
        cleanWithTkhandler();

        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));
        REG_COMU_USER_USERCOMU_AC.checkMenuItem_NTk(activity);
    }

    @Test
    public void testMenuNuevaComunidad_withToken() throws InterruptedException, UiException, IOException
    {
        whatClean = CLEAN_JUAN;

        regTwoUserComuSameUser(makeListTwoUserComu());
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));

        Thread.sleep(2000);
        REG_COMU_USER_USERCOMU_AC.checkMenuItem_WTk(activity);
    }

    @Test
    public void tesComunidadesByUsuario_noToken_1() throws InterruptedException, UiException
    {
        assertThat(TKhandler.getAccessTokenInCache(), nullValue());
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));

        // No results in DB. The user is invited to register.
        checkToastInTest(R.string.no_result_search_comunidad, activity);
        onView(withId(R.id.reg_comu_usuario_usuariocomu_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void tesComunidadesByUsuario_noToken_2() throws InterruptedException, UiException, IOException
    {
        whatClean = CLEAN_JUAN;

        //Usuario no registrado. La búsqueda devuelve una comunidad.
        regTwoUserComuSameUser(makeListTwoUserComu());
        // Borro los datos del userComu.
        cleanWithTkhandler();
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));

        SEE_USERCOMU_BY_USER_AC.checkMenuItem_NTk(activity);
    }

    @Test
    public void testComunidadesByUsuario_withToken() throws InterruptedException, UiException, IOException
    {
        whatClean = CLEAN_JUAN;

        regTwoUserComuSameUser(makeListTwoUserComu());
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));
        Thread.sleep(2000);
        SEE_USERCOMU_BY_USER_AC.checkMenuItem_WTk(activity);
    }
    @After
    public void cleanData() throws UiException
    {
        cleanOptions(whatClean);
        checkState(!isRegisteredUser(activity));
    }
}