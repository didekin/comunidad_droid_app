package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.usuario.dominio.DomainDataUtils;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.didekindroid.uiutils.UIutils.isRegisteredUser;
import static com.didekindroid.uiutils.ViewsIDs.SEE_USERCOMU_BY_COMU;
import static com.didekindroid.usuario.activity.utils.CleanEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.cleanOptions;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.signUpAndUpdateTk;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.COMUNIDAD_ID;
import static com.didekindroid.usuario.activity.utils.UserMenuTestUtils.COMU_SEARCH_AC;
import static com.didekindroid.usuario.activity.utils.UserMenuTestUtils.SEE_USERCOMU_BY_USER_AC;
import static com.didekindroid.usuario.activity.utils.UserMenuTestUtils.USER_DATA_AC;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static com.google.android.apps.common.testing.ui.espresso.sample.LongListMatchers.withAdaptedData;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 27/08/15
 * Time: 11:38
 */
@RunWith(AndroidJUnit4.class)
public class SeeUserComuByComuAcTest {

    SeeUserComuByComuAc mActivity;
    SeeUserComuByComuFr mFragment;
    long comunidadId;
    Intent intent;

    @Rule
    public ActivityTestRule<SeeUserComuByComuAc> mActivityRule =
            new ActivityTestRule<>(SeeUserComuByComuAc.class, true, false);

    @Before
    public void setUp() throws Exception
    {
        // User is registered, with a comunidad in the intent.
        signUpAndUpdateTk(DomainDataUtils.COMU_TRAV_PLAZUELA_PEPE);
        // We insert a secondo user with the same comunidad.
        signUpAndUpdateTk(DomainDataUtils.COMU_PLAZUELA5_JUAN);

        // We get the id of the comunidad we will put in the intent.
        List<UsuarioComunidad> usuariosComu = ServOne.getUsuariosComunidad();
        comunidadId = usuariosComu.get(0).getComunidad().getC_Id(); // COMU_PLAZUELA5_JUAN.
        // We put the comunidad in the intent.
        intent = new Intent();
        intent.putExtra(COMUNIDAD_ID.extra, comunidadId);

        mActivity = mActivityRule.launchActivity(intent);
        mFragment = (SeeUserComuByComuFr) mActivity.getFragmentManager().findFragmentById(R.id.see_usercomu_by_comu_frg);
    }

    @Test
    public void testOnCreate() throws Exception
    {
        assertThat(mActivity, notNullValue());
        assertThat(isRegisteredUser(mActivity), is(true));
        assertThat(mActivity.getIntent().getLongExtra(COMUNIDAD_ID.extra,0L),is(comunidadId));
        assertThat(mFragment, notNullValue());

        onView(withId(R.id.see_usercomu_by_comu_ac_frg_container)).check(matches(isDisplayed()));
        onView(withId(R.id.see_usercomu_by_comu_frg)).check(matches(isDisplayed()));
    }

    @Test
    public void testViewData_1()
    {
        SeeUserComutByComuListAdapter mAdapter = (SeeUserComutByComuListAdapter) mFragment.getListAdapter();
        assertThat(mAdapter.getCount(), is(1));

        onView(withId(SEE_USERCOMU_BY_COMU.idView)).check(
                matches(withAdaptedData(hasProperty("escalera", is(COMU_PLAZUELA5_JUAN.getEscalera())))));
        onView(withId(SEE_USERCOMU_BY_COMU.idView)).check(
                matches(withAdaptedData(hasProperty("portal", is(COMU_PLAZUELA5_JUAN.getPortal())))));
        onView(withId(SEE_USERCOMU_BY_COMU.idView)).check(
                matches(withAdaptedData(hasProperty("planta", is(COMU_PLAZUELA5_JUAN.getPlanta())))));
        onView(withId(SEE_USERCOMU_BY_COMU.idView)).check(
                matches(withAdaptedData(hasProperty("puerta", is(COMU_PLAZUELA5_JUAN.getPuerta())))));
        onView(withId(SEE_USERCOMU_BY_COMU.idView)).check(
                matches(withAdaptedData(hasProperty("roles", is(COMU_PLAZUELA5_JUAN.getRoles())))));
        onView(withId(SEE_USERCOMU_BY_COMU.idView)).check(matches(
                withAdaptedData(Matchers.<Object>equalTo(COMU_PLAZUELA5_JUAN))));

        onView(withId(R.id.usercomu_item_nombreComunidad_txt))
                .check(matches(withText(containsString(COMU_PLAZUELA5_JUAN.getComunidad().getNombreComunidad()))));
        onData(allOf(
                is(instanceOf(UsuarioComunidad.class)),
                hasProperty("comunidad", is(COMU_PLAZUELA5_JUAN.getComunidad()))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testUserComuByUserMn_withToken() throws InterruptedException
    {
        SEE_USERCOMU_BY_USER_AC.checkMenuItem_WTk(mActivity);
    }

    @Test
    public void testUserDataMn_withToken() throws InterruptedException
    {
        USER_DATA_AC.checkMenuItem_WTk(mActivity);
    }

    @Test
    public void testComuSearchMn_withToken() throws InterruptedException
    {
        COMU_SEARCH_AC.checkMenuItem_WTk(mActivity);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_JUAN_AND_PEPE);
    }
}