package com.didekindroid.usuario.activity;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.UiException;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.usuario.activity.utils.CleanEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.activity.utils.UserMenuTestUtils.COMU_SEARCH_AC;
import static com.didekindroid.usuario.activity.utils.UserMenuTestUtils.USER_DATA_AC;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.cleanOptions;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.regThreeUserComuSameUser_2;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_EL_ESCORIAL;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_LA_FUENTE;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_LA_FUENTE_PEPE;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_LA_PLAZUELA_5;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_PLAZUELA5_PEPE;
import static com.didekindroid.common.utils.ViewsIDs.SEE_USER_COMU_BY_USER;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 17/09/15
 * Time: 16:15
 */
@RunWith(AndroidJUnit4.class)
public class SeeUserComuByUserAcTest {

    SeeUserComuByUserAc mActivity;
    SeeUserComuByUserFr mFragment;

    @Rule
    public IntentsTestRule<SeeUserComuByUserAc> intentRule = new IntentsTestRule<SeeUserComuByUserAc>(SeeUserComuByUserAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            try {
                regThreeUserComuSameUser_2(COMU_ESCORIAL_PEPE, COMU_PLAZUELA5_PEPE, COMU_LA_FUENTE_PEPE);
            } catch (UiException e) {
                e.printStackTrace();
            }
        }
    };

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(5000);
    }

    @Before
    public void setUp() throws Exception
    {
        mActivity = intentRule.getActivity();
        mFragment = (SeeUserComuByUserFr) mActivity.getFragmentManager().findFragmentById(R.id.see_usercomu_by_user_frg);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_PEPE);
        Thread.sleep(2000);
    }

//  ================================================================================================================

    @Test
    public void testOnCreate() throws Exception
    {
        assertThat(mActivity, notNullValue());
        assertThat(mFragment, notNullValue());

        onView(withId(R.id.see_usercomu_by_user_ac_frg_container)).check(matches(isDisplayed()));
        onView(withId(R.id.see_usercomu_by_user_frg)).check(matches(isDisplayed()));
        onView(withId(SEE_USER_COMU_BY_USER.idView)).check(matches(isDisplayed()));

        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        onView(withContentDescription("Navigate up")).check(matches(isDisplayed()));
        onView(CoreMatchers.allOf(
                        withContentDescription("Navigate up"),
                        isClickable())
        ).check(matches(isDisplayed())).perform(click());
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

    @Test
    public void testViewData_1() throws InterruptedException
    {
        SeeUserComuByUserAdapter adapter = (SeeUserComuByUserAdapter) mFragment.getListAdapter();
        assertThat(adapter.getCount(), is(3));

        onData(allOf(
                is(instanceOf(UsuarioComunidad.class)),
                hasProperty("portal", is(COMU_PLAZUELA5_PEPE.getPortal())),
                hasProperty("escalera", is(COMU_PLAZUELA5_PEPE.getEscalera())),
                hasProperty("planta", is(COMU_PLAZUELA5_PEPE.getPlanta())),
                hasProperty("puerta", is(COMU_PLAZUELA5_PEPE.getPuerta())),
                hasProperty("roles", is(COMU_PLAZUELA5_PEPE.getRoles()))
        )).onChildView(allOf(
                withId(R.id.nombreComunidad_view),
                withText(COMU_LA_PLAZUELA_5.getNombreComunidad()))).check(matches(isDisplayed()));

        onData(allOf(
                is(instanceOf(UsuarioComunidad.class)),
                hasProperty("portal", is(COMU_LA_FUENTE_PEPE.getPortal())),
                hasProperty("escalera", is(COMU_LA_FUENTE_PEPE.getEscalera())),
                hasProperty("planta", is(COMU_LA_FUENTE_PEPE.getPlanta())),
                hasProperty("puerta", is(COMU_LA_FUENTE_PEPE.getPuerta())),
                hasProperty("roles", is(COMU_LA_FUENTE_PEPE.getRoles()))
        )).onChildView(allOf(
                withId(R.id.nombreComunidad_view),
                withText(COMU_LA_FUENTE.getNombreComunidad()))).check(matches(isDisplayed()));

        onData(allOf(
                is(instanceOf(UsuarioComunidad.class)),
                hasProperty("portal", is(COMU_ESCORIAL_PEPE.getPortal())),
                hasProperty("escalera", is(COMU_ESCORIAL_PEPE.getEscalera())),
                hasProperty("planta", is(COMU_ESCORIAL_PEPE.getPlanta())),
                hasProperty("puerta", is(COMU_ESCORIAL_PEPE.getPuerta())),
                hasProperty("roles", is(COMU_ESCORIAL_PEPE.getRoles()))
        )).onChildView(allOf(
                withId(R.id.nombreComunidad_view),
                withText(COMU_EL_ESCORIAL.getNombreComunidad()))).check(matches(isDisplayed()));
    }

    @Test
    public void testViewData_2()
    {
        onView(allOf(
                withText("Elda"),
                withId(R.id.municipio_view),
                hasSibling(allOf(
                        withId(R.id.nombreComunidad_view),
                        withText(COMU_LA_FUENTE.getNombreComunidad())
                )),
                hasSibling(allOf(
                        withId(R.id.provincia_view),
                        withText("Alicante/Alacant")
                ))
        )).check(matches(isDisplayed()));

        onView(allOf(
                withText("Alfoz"),
                withId(R.id.municipio_view),
                hasSibling(allOf(
                        withId(R.id.nombreComunidad_view),
                        withText(COMU_LA_PLAZUELA_5.getNombreComunidad())
                )),
                hasSibling(allOf(
                        withId(R.id.provincia_view),
                        withText("Lugo")
                ))
        )).check(matches(isDisplayed()));

        onView(allOf(
                withText("Benizalón"),
                withId(R.id.municipio_view),
                hasSibling(allOf(
                        withId(R.id.nombreComunidad_view),
                        withText(COMU_EL_ESCORIAL.getNombreComunidad())
                )),
                hasSibling(allOf(
                        withId(R.id.provincia_view),
                        withText("Almería")
                ))
        )).check(matches(isDisplayed()));
    }
}