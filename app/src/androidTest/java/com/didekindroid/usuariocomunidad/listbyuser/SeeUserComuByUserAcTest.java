package com.didekindroid.usuariocomunidad.listbyuser;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.comunidad.testutil.ComuDataTestUtil;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.testutil.UserItemMenuTestUtils;
import com.didekindroid.usuariocomunidad.RolUi;
import com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil;

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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.comunidad.testutil.ComuMenuTestUtil.COMU_SEARCH_AC;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_LA_FUENTE_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.userComuDataLayout;
import static external.LongListMatchers.withAdaptedData;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 17/09/15
 * Time: 16:15
 */
@RunWith(AndroidJUnit4.class)
public class SeeUserComuByUserAcTest {

    @Rule
    public IntentsTestRule<SeeUserComuByUserAc> intentRule = new IntentsTestRule<SeeUserComuByUserAc>(SeeUserComuByUserAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            try {
                UserComuDataTestUtil.regSeveralUserComuSameUser(COMU_ESCORIAL_PEPE, COMU_PLAZUELA5_PEPE, COMU_LA_FUENTE_PEPE);
            } catch (UiException | IOException e) {
                e.printStackTrace();
            }
        }
    };
    SeeUserComuByUserAc mActivity;
    SeeUserComuByUserFr mFragment;

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(3000);
    }

    @Before
    public void setUp() throws Exception
    {
        mActivity = intentRule.getActivity();
        mFragment = (SeeUserComuByUserFr) mActivity.getSupportFragmentManager().findFragmentById(seeUserComuByUserFrRsId);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_PEPE);
    }

//  ================================================================================================================

    @Test
    public void testOnCreateAndNavigateUp() throws Exception
    {
        assertThat(mActivity, notNullValue());
        assertThat(mFragment, notNullValue());
        assertThat(TKhandler.isRegisteredUser(), is(true));
        assertThat(mFragment.getFragmentView(), notNullValue());

        onView(withId(seeUserComuByUserFrRsId)).check(matches(isDisplayed()));
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));

        // Verificamos navegación en ambas direcciones.
        onData(is(COMU_LA_FUENTE_PEPE)).check(matches(isDisplayed())).perform(click());
        onView(withId(userComuDataLayout)).check(matches(isDisplayed()));
        checkUp(seeUserComuByUserFrRsId);
    }

    @Test
    public void testViewData_1() throws InterruptedException
    {
        Thread.sleep(3000);
        SeeUserComuByUserAdapter adapter = mFragment.mAdapter;
        assertThat(adapter.getCount(), is(3));
        // Orden es provinciaId, municipioCd.
        assertThat(adapter.getItem(0), is(COMU_LA_FUENTE_PEPE));
        assertThat(adapter.getItem(1), is(COMU_ESCORIAL_PEPE));
        assertThat(adapter.getItem(2), is(COMU_PLAZUELA5_PEPE));

        for (int i = 0; i < adapter.getCount(); ++i) {
            onView(withAdaptedData(Matchers.<Object>is(adapter.getItem(i)))).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testViewData_2()
    {
        onData(is(COMU_LA_FUENTE_PEPE))
                .onChildView(
                        allOf(
                                withId(R.id.nombreComunidad_view),
                                ViewMatchers.withText(ComuDataTestUtil.COMU_LA_FUENTE.getNombreComunidad())
                        )
                )
                .check(matches(isDisplayed()));
        onData(is(COMU_LA_FUENTE_PEPE))
                .onChildView(
                        allOf(
                                withId(R.id.usercomu_item_roles_txt),
                                ViewMatchers.withText(RolUi.formatRolToString(COMU_LA_FUENTE_PEPE.getRoles(), mActivity.getResources()))
                        )
                )
                .check(matches(isDisplayed()));

        onData(is(COMU_ESCORIAL_PEPE))
                .onChildView(
                        allOf(
                                withId(R.id.nombreComunidad_view),
                                ViewMatchers.withText(ComuDataTestUtil.COMU_EL_ESCORIAL.getNombreComunidad())
                        )
                )
                .check(matches(isDisplayed()));
        onData(is(COMU_ESCORIAL_PEPE))
                .onChildView(
                        allOf(
                                withId(R.id.usercomu_item_roles_txt),
                                withText(RolUi.formatRolToString(COMU_ESCORIAL_PEPE.getRoles(), mActivity.getResources()))
                        )
                )
                .check(matches(isDisplayed()));

        onData(is(COMU_PLAZUELA5_PEPE))
                .onChildView(
                        allOf(
                                withId(R.id.nombreComunidad_view),
                                ViewMatchers.withText(ComuDataTestUtil.COMU_LA_PLAZUELA_5.getNombreComunidad())
                        )
                )
                .check(matches(isDisplayed()));
        onData(is(COMU_PLAZUELA5_PEPE))
                .onChildView(
                        allOf(
                                withId(R.id.usercomu_item_roles_txt),
                                withText(RolUi.formatRolToString(COMU_PLAZUELA5_PEPE.getRoles(), mActivity.getResources()))
                        )
                )
                .check(matches(isDisplayed()));

        onView(allOf(
                withText("Elda"),
                withId(R.id.municipio_view),
                hasSibling(allOf(
                        withId(R.id.provincia_view),
                        withText("Alicante/Alacant")
                ))
        )).check(matches(isDisplayed()));

        onView(allOf(
                withText("Alfoz"),
                withId(R.id.municipio_view),
                hasSibling(allOf(
                        withId(R.id.provincia_view),
                        withText("Lugo")
                ))
        )).check(matches(isDisplayed()));

        onView(allOf(
                withText("Benizalón"),
                withId(R.id.municipio_view),
                hasSibling(allOf(
                        withId(R.id.provincia_view),
                        withText("Almería")
                ))
        )).check(matches(isDisplayed()));
    }

    @Test
    public void testUserDataMn_withToken() throws InterruptedException
    {
        UserItemMenuTestUtils.USER_DATA_AC.checkMenuItem_WTk(mActivity);
        checkUp(seeUserComuByUserFrRsId);
    }

    @Test
    public void testComuSearchMn_withToken() throws InterruptedException
    {
        COMU_SEARCH_AC.checkMenuItem_WTk(mActivity);
        // NO hay opción de navigate-up.
    }
}