package com.didekindroid.usuariocomunidad;

import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.comunidad.Comunidad;
import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekinaar.R;
import com.didekinaar.usuario.testutil.UserItemMenuTestUtils;
import com.didekindroid.comunidad.ComuBundleKey;
import com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil;
import com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekinaar.testutil.AarActivityTestUtils.checkUp;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekinaar.testutil.AarActivityTestUtils.clickNavigateUp;
import static com.didekinaar.security.TokenIdentityCacher.TKhandler.isRegisteredUser;
import static com.didekindroid.comunidad.testutil.ComuMenuTestUtil.COMU_SEARCH_AC;
import static com.didekindroid.usuariocomunidad.UserComuService.AppUserComuServ;
import static com.didekindroid.usuariocomunidad.testutil.UserComuMenuTestUtil.SEE_USERCOMU_BY_USER_AC;
import static com.external.LongListMatchers.withAdaptedData;
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

    SeeUserComuByComuAc mActivity;
    SeeUserComuByComuFr mFragment;
    long comunidadId;
    Intent intent;
    int fragmentLayoutId = R.id.see_usercomu_by_comu_frg;


    @Rule
    public ActivityTestRule<SeeUserComuByComuAc> mActivityRule =
            new ActivityTestRule<>(SeeUserComuByComuAc.class, true, false);

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(4000);
    }

    @Before
    public void setUp() throws Exception
    {
        // User is registered, with a comunidad in the intent.
        UserComuTestUtil.signUpAndUpdateTk(UserComuTestUtil.COMU_TRAV_PLAZUELA_PEPE);
        // We insert a second user in a different comunidad.
        UserComuTestUtil.signUpAndUpdateTk(UserComuTestUtil.COMU_PLAZUELA5_JUAN);

        // We get the id of the comunidad we will put in the intent.
        List<UsuarioComunidad> usuariosComu = AppUserComuServ.seeUserComusByUser();
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
        assertThat(TKhandler.isRegisteredUser(mActivity), is(true));
        assertThat(mActivity.getIntent().getLongExtra(ComuBundleKey.COMUNIDAD_ID.key, 0L), is(comunidadId));
        assertThat(mFragment, notNullValue());
        assertThat(mFragment.fragmentListView, notNullValue());
        assertThat(mFragment.nombreComuView, notNullValue());

        onView(withId(fragmentLayoutId)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.appbar)).check(matches(isDisplayed()));
        clickNavigateUp();
    }

    @Test
    public void testViewData() throws InterruptedException
    {
        Thread.sleep(2000);
        SeeUserComuByComuListAdapter mAdapter = mFragment.mAdapter;
        assertThat(mAdapter.getCount(), is(1));

        UsuarioComunidad userComu = mAdapter.getItem(0);
        UserComuEspressoTestUtil.validaTypedUsuarioComunidad(userComu, UserComuTestUtil.COMU_PLAZUELA5_JUAN.getPortal(), UserComuTestUtil.COMU_PLAZUELA5_JUAN.getEscalera(), UserComuTestUtil.COMU_PLAZUELA5_JUAN.getPlanta(),
                UserComuTestUtil.COMU_PLAZUELA5_JUAN.getPuerta(), UserComuTestUtil.COMU_PLAZUELA5_JUAN.getRoles());
        assertThat(userComu.getComunidad(),is(new Comunidad.ComunidadBuilder().c_id(comunidadId).build()));

        onView(withAdaptedData(Matchers.<Object>is(userComu))).check(matches(isDisplayed()));
        // Header.
        onView(ViewMatchers.withId(R.id.see_usercomu_by_comu_list_header))
                .check(matches(withText(containsString(UserComuTestUtil.COMU_PLAZUELA5_JUAN.getComunidad().getNombreComunidad()))));
    }

    @Test
    public void testUserComuByUserMn_withToken() throws InterruptedException
    {
        SEE_USERCOMU_BY_USER_AC.checkMenuItem_WTk(mActivity);
        checkUp(fragmentLayoutId);
    }

    @Test
    public void testUserDataMn_withToken() throws InterruptedException
    {
        UserItemMenuTestUtils.USER_DATA_AC.checkMenuItem_WTk(mActivity);
        checkUp(fragmentLayoutId);
    }

    @Test
    public void testComuSearchMn_withToken() throws InterruptedException
    {
        COMU_SEARCH_AC.checkMenuItem_WTk(mActivity);
        // En este caso no hay opción de 'navigate-up'.
    }
}