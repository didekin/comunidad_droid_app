package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.usuario.dominio.Comunidad;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.testutils.ActivityTestUtils;

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
import static com.didekindroid.common.activity.BundleKey.COMUNIDAD_ID;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.external.LongListMatchers.withAdaptedData;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.validaTypedUsuarioComunidad;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
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
        signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        // We insert a second user in a different comunidad.
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);

        // We get the id of the comunidad we will put in the intent.
        List<UsuarioComunidad> usuariosComu = ServOne.seeUserComusByUser();
        comunidadId = usuariosComu.get(0).getComunidad().getC_Id(); // COMU_PLAZUELA5_JUAN.
        // We put the comunidad in the intent.
        intent = new Intent();
        intent.putExtra(COMUNIDAD_ID.key, comunidadId);

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
        assertThat(isRegisteredUser(mActivity), is(true));
        assertThat(mActivity.getIntent().getLongExtra(COMUNIDAD_ID.key, 0L), is(comunidadId));
        assertThat(mFragment, notNullValue());
        assertThat(mFragment.fragmentListView, notNullValue());
        assertThat(mFragment.nombreComuView, notNullValue());

        onView(withId(R.id.see_usercomu_by_comu_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        ActivityTestUtils.checkNavigateUp();
    }

    @Test
    public void testViewData_1() throws InterruptedException
    {
        Thread.sleep(2000);
        SeeUserComutByComuListAdapter mAdapter = mFragment.mAdapter;
        assertThat(mAdapter.getCount(), is(1));

        UsuarioComunidad userComu = mAdapter.getItem(0);
        validaTypedUsuarioComunidad(userComu,COMU_PLAZUELA5_JUAN.getPortal(),COMU_PLAZUELA5_JUAN.getEscalera(), COMU_PLAZUELA5_JUAN.getPlanta(),
                COMU_PLAZUELA5_JUAN.getPuerta(), COMU_PLAZUELA5_JUAN.getRoles());
        assertThat(userComu.getComunidad(),is(new Comunidad.ComunidadBuilder().c_id(comunidadId).build()));

        onView(withAdaptedData(Matchers.<Object>is(userComu))).check(matches(isDisplayed()));
        // Header.
        onView(withId(R.id.see_usercomu_by_comu_list_header))
                .check(matches(withText(containsString(COMU_PLAZUELA5_JUAN.getComunidad().getNombreComunidad()))));
    }
}