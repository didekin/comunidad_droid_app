package com.didekindroid.usuariocomunidad.listbycomu;

import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.comunidad.ComuBundleKey;
import com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.comunidad.testutil.ComuMenuTestUtil.COMU_SEARCH_AC;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.usuario.testutil.UserItemMenuTestUtils.USER_DATA_AC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.validaTypedUsuarioComunidad;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.SEE_USERCOMU_BY_USER_AC;
import static external.LongListMatchers.withAdaptedData;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 27/08/15
 * Time: 11:38
 */
@RunWith(AndroidJUnit4.class)
public class SeeUserComuByComuAc_1_Test {

    @Rule
    public ActivityTestRule<SeeUserComuByComuAc> mActivityRule = new ActivityTestRule<>(SeeUserComuByComuAc.class, true, false);
    SeeUserComuByComuAc mActivity;
    SeeUserComuByComuFr mFragment;
    long comunidadId;
    Intent intent;
    int fragmentLayoutId = R.id.see_usercomu_by_comu_frg;

    @Before
    public void setUp() throws Exception
    {
        // User is registered, with a comunidad in the intent.
        signUpAndUpdateTk(UserComuDataTestUtil.COMU_TRAV_PLAZUELA_PEPE);
        // We insert a second user in a different comunidad.
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);

        // We get the id of the comunidad we will put in the intent.
        List<UsuarioComunidad> usuariosComu = userComuDaoRemote.seeUserComusByUser();
        comunidadId = usuariosComu.get(0).getComunidad().getC_Id(); // COMU_PLAZUELA5_JUAN.
        // We put the comunidad in the intent.
        intent = new Intent();
        intent.putExtra(ComuBundleKey.COMUNIDAD_ID.key, comunidadId);

        mActivity = mActivityRule.launchActivity(intent);
        mFragment = (SeeUserComuByComuFr) mActivity.getSupportFragmentManager().findFragmentById(R.id.see_usercomu_by_comu_frg);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_JUAN_AND_PEPE);
    }

//    =================================================================================================================

    @Test
    public void testOnCreate() throws Exception
    {
        assertThat(mActivity, notNullValue());
        assertThat(TKhandler.isRegisteredUser(), is(true));
        assertThat(mActivity.getIntent().getLongExtra(ComuBundleKey.COMUNIDAD_ID.key, 0L), is(comunidadId));
        assertThat(mFragment, notNullValue());

        onView(withId(fragmentLayoutId)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.appbar)).check(matches(isDisplayed()));
        clickNavigateUp();
    }

    @Test
    public void testViewData() throws InterruptedException
    {
        MILLISECONDS.sleep(3000);
        SeeUserComuByComuListAdapter adapter = null;/* = mFragment.mAdapter*/;    // TODO: rehacer entero.
        UsuarioComunidad userComu = adapter.getItem(0);
        validaTypedUsuarioComunidad(
                userComu,
                COMU_PLAZUELA5_JUAN.getPortal(),
                COMU_PLAZUELA5_JUAN.getEscalera(),
                COMU_PLAZUELA5_JUAN.getPlanta(),
                COMU_PLAZUELA5_JUAN.getPuerta(),
                COMU_PLAZUELA5_JUAN.getRoles());

        assertThat(userComu.getComunidad(), is(new Comunidad.ComunidadBuilder().c_id(comunidadId).build()));
        onView(withAdaptedData(Matchers.<Object>is(userComu))).check(matches(isDisplayed()));
        // Header.
        onView(ViewMatchers.withId(R.id.see_usercomu_by_comu_list_header))
                .check(matches(withText(containsString(COMU_PLAZUELA5_JUAN.getComunidad().getNombreComunidad()))));
    }

    @Test
    public void testUserComuByUserMn() throws InterruptedException
    {
        SEE_USERCOMU_BY_USER_AC.checkMenuItem_WTk(mActivity);
        checkUp(fragmentLayoutId);
    }

    @Test
    public void testUserDataMn() throws InterruptedException
    {
        USER_DATA_AC.checkMenuItem_WTk(mActivity);
        checkUp(fragmentLayoutId);
    }

    @Test
    public void testComuSearchMn() throws InterruptedException
    {
        COMU_SEARCH_AC.checkMenuItem_WTk(mActivity);
        // En este caso no hay opción de 'navigate-up'.
    }
}