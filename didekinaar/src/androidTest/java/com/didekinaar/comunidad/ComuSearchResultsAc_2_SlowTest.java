package com.didekinaar.comunidad;

import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekinaar.R;
import com.didekinaar.exception.UiAarException;
import com.didekinaar.testutil.AarActivityTestUtils;
import com.didekinaar.testutil.CleanUserEnum;
import com.didekinaar.testutil.UserMenuTestUtils;
import com.didekinaar.testutil.UsuarioTestUtils;

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
import static com.didekinaar.comunidad.ComuBundleKey.COMUNIDAD_SEARCH;
import static com.didekinaar.testutil.AarActivityTestUtils.checkToastInTest;
import static com.didekinaar.testutil.AarActivityTestUtils.checkUp;
import static com.didekinaar.testutil.AarActivityTestUtils.cleanOptions;
import static com.didekinaar.testutil.CleanUserEnum.CLEAN_JUAN;
import static com.didekinaar.utils.UIutils.isRegisteredUser;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 09/10/15
 * Time: 13:04
 */


@RunWith(AndroidJUnit4.class)
public class ComuSearchResultsAc_2_SlowTest {

    private ComuSearchResultsAc activity;
    ComuSearchResultsListFr mComunidadSummaryFrg;
    Intent intent;
    CleanUserEnum whatClean;

    // Navigate-up layout cuando hay resultados.
    protected int fragmentLayoutId = R.id.comu_list_fragment;
    // Navigate-up layout cuando NO hay resultados.
    int activityLayoutId = R.id.comu_search_results_frg_container_ac;

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
        Thread.sleep(5000);

        whatClean = CleanUserEnum.CLEAN_NOTHING;

        intent = new Intent();
        intent.putExtra(COMUNIDAD_SEARCH.key, UsuarioTestUtils.COMU_LA_PLAZUELA_5);
    }

    @After
    public void cleanData() throws UiAarException
    {
        cleanOptions(whatClean);
    }

    @Test
    public void testNoUserNoResults() throws Exception
    {
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));

        mComunidadSummaryFrg = (ComuSearchResultsListFr) activity.getSupportFragmentManager().findFragmentById(R.id.comu_list_fragment);
        assertThat(activity, notNullValue());
        assertThat(mComunidadSummaryFrg, notNullValue());

        // No results in DB. The user is invited to register.
        checkToastInTest(R.string.no_result_search_comunidad, activity);
        onView(ViewMatchers.withId(R.id.reg_comu_usuario_usuariocomu_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void testMenuNuevaComunidad_noToken() throws InterruptedException, UiAarException, IOException
    {
        whatClean = CLEAN_JUAN;

        AarActivityTestUtils.regTwoUserComuSameUser(AarActivityTestUtils.makeListTwoUserComu());
        // Borro los datos del userComu.
        AarActivityTestUtils.cleanWithTkhandler();

        //Usuario no registrado. La búsqueda devuelve una comunidad.
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));
        // La opción de nueva comunidad implica también registro de usuario.
        UserMenuTestUtils.REG_COMU_USER_USERCOMU_AC.checkMenuItem_NTk(activity);
        checkUp(fragmentLayoutId);
    }

    @Test
    public void testMenuNuevaComunidad_withToken() throws InterruptedException, UiAarException, IOException
    {
        whatClean = CLEAN_JUAN;

        AarActivityTestUtils.regTwoUserComuSameUser(AarActivityTestUtils.makeListTwoUserComu());
        //Usuario no registrado. La búsqueda devuelve una comunidad.
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));

        Thread.sleep(2000);
        // La opción de nueva comunidad NO implica registro de usuario.
        UserMenuTestUtils.REG_COMU_USERCOMU_AC.checkMenuItem_WTk(activity);
        checkUp(fragmentLayoutId);
    }

    @Test
    public void tesComunidadesByUsuario_noToken() throws InterruptedException, UiAarException, IOException
    {
        whatClean = CLEAN_JUAN;

        //Usuario no registrado. La búsqueda devuelve una comunidad.
        AarActivityTestUtils.regTwoUserComuSameUser(AarActivityTestUtils.makeListTwoUserComu());
        // Borro los datos del userComu.
        AarActivityTestUtils.cleanWithTkhandler();
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(false));

        UserMenuTestUtils.SEE_USERCOMU_BY_USER_AC.checkMenuItem_NTk(activity);
        // No se mueve de la actividad. El home llevaría a la actividad de búsqueda de comunidad.
        onView(withId(fragmentLayoutId)).check(matches(isDisplayed()));
    }

    @Test
    public void testComunidadesByUsuario_withToken() throws InterruptedException, UiAarException, IOException
    {
        whatClean = CLEAN_JUAN;

        AarActivityTestUtils.regTwoUserComuSameUser(AarActivityTestUtils.makeListTwoUserComu());
        //Usuario registrado. La búsqueda devuelve una comunidad.
        activity = mActivityRule.launchActivity(intent);
        assertThat(isRegisteredUser(activity), is(true));
        Thread.sleep(2000);
        // La consulta muestra las comunidades del usuario.
        UserMenuTestUtils.SEE_USERCOMU_BY_USER_AC.checkMenuItem_WTk(activity);
        checkUp(fragmentLayoutId);
    }
}