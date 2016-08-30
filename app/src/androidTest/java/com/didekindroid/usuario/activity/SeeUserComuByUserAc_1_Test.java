package com.didekindroid.usuario.activity;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.common.testutils.ActivityTestUtils;
import com.didekindroid.usuario.activity.utils.RolUi;

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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.regSeveralUserComuSameUser;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.external.LongListMatchers.withAdaptedData;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_EL_ESCORIAL;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_LA_FUENTE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_LA_FUENTE_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_LA_PLAZUELA_5;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_PLAZUELA5_PEPE;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 17/09/15
 * Time: 16:15
 */
@RunWith(AndroidJUnit4.class)
public class SeeUserComuByUserAc_1_Test {

    SeeUserComuByUserAc mActivity;
    SeeUserComuByUserFr mFragment;

    @Rule
    public IntentsTestRule<SeeUserComuByUserAc> intentRule = new IntentsTestRule<SeeUserComuByUserAc>(SeeUserComuByUserAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            try {
                regSeveralUserComuSameUser(COMU_ESCORIAL_PEPE, COMU_PLAZUELA5_PEPE, COMU_LA_FUENTE_PEPE);
            } catch (UiException | IOException e) {
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
        mFragment = (SeeUserComuByUserFr) mActivity.getSupportFragmentManager().findFragmentById(R.id.see_usercomu_by_user_frg);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_PEPE);
    }

//  ================================================================================================================

    @Test
    public void testOnCreate() throws Exception
    {
        assertThat(mActivity, notNullValue());
        assertThat(mFragment, notNullValue());
        assertThat(isRegisteredUser(mActivity), is(true));
        assertThat(mFragment.getFragmentView(), notNullValue());

        onView(withId(R.id.see_usercomu_by_user_frg)).check(matches(isDisplayed()));
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        ActivityTestUtils.checkNavigateUp();
    }

    @Test
    public void testViewData_1() throws InterruptedException
    {
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
                                withText(COMU_LA_FUENTE.getNombreComunidad())
                        )
                )
                .check(matches(isDisplayed()));
        onData(is(COMU_LA_FUENTE_PEPE))
                .onChildView(
                        allOf(
                                withId(R.id.usercomu_item_roles_txt),
                                withText(RolUi.formatRol(COMU_LA_FUENTE_PEPE.getRoles(), mActivity.getResources()))
                        )
                )
                .check(matches(isDisplayed()));

        onData(is(COMU_ESCORIAL_PEPE))
                .onChildView(
                        allOf(
                                withId(R.id.nombreComunidad_view),
                                withText(COMU_EL_ESCORIAL.getNombreComunidad())
                        )
                )
                .check(matches(isDisplayed()));
        onData(is(COMU_ESCORIAL_PEPE))
                .onChildView(
                        allOf(
                                withId(R.id.usercomu_item_roles_txt),
                                withText(RolUi.formatRol(COMU_ESCORIAL_PEPE.getRoles(), mActivity.getResources()))
                        )
                )
                .check(matches(isDisplayed()));

        onData(is(COMU_PLAZUELA5_PEPE))
                .onChildView(
                        allOf(
                                withId(R.id.nombreComunidad_view),
                                withText(COMU_LA_PLAZUELA_5.getNombreComunidad())
                        )
                )
                .check(matches(isDisplayed()));
        onData(is(COMU_PLAZUELA5_PEPE))
                .onChildView(
                        allOf(
                                withId(R.id.usercomu_item_roles_txt),
                                withText(RolUi.formatRol(COMU_PLAZUELA5_PEPE.getRoles(), mActivity.getResources()))
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
}