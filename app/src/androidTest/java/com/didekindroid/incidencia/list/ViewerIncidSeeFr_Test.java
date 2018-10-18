package com.didekindroid.incidencia.list;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.ActivityNextMock;
import com.didekindroid.lib_one.api.router.FragmentInitiator;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.incidencia.testutils.IncidTestData.doIncidenciaUsers;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.util.UiUtil.getMilliSecondsFromCalendarAdd;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpGetComu;
import static java.util.Calendar.SECOND;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 *  Test of low level details in the manipulation of incidencias list. It uses closed incidencia, but it applicable to both kinds.
 */
@RunWith(AndroidJUnit4.class)
public class ViewerIncidSeeFr_Test {

    @Rule
    public ActivityTestRule<ActivityNextMock> acRule = new ActivityTestRule<>(ActivityNextMock.class, false, true);

    private ActivityNextMock ac;
    private ViewerIncidSeeCloseFr viewer;
    private IncidSeeByComuFr fr;
    private List<IncidenciaUser> list;

    @Before
    public void setUp() throws Exception
    {

        fr = IncidSeeByComuFr.newInstance(signUpGetComu(COMU_ESCORIAL_PEPE).getC_Id(), true);
        ac = acRule.getActivity();
        new FragmentInitiator<IncidSeeByComuFr>(ac, R.id.next_mock_ac_layout).initFragmentTx(fr);
        waitAtMost(4, SECONDS).until(() -> ac.getSupportFragmentManager().findFragmentByTag(fr.getClass().getName()) != null);
        waitAtMost(4, SECONDS).until(() -> (viewer = fr.viewer) != null);
        // Wait until list is made.
        SECONDS.sleep(4);
        onView(allOf(withId(android.R.id.empty), withText(R.string.no_incidencia_to_show))).check(matches(isDisplayed()));
    }

    @After
    public void cleanUp()
    {
        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public void test_OnSuccessLoadItems_1()
    {
        list = doIncidenciaUsers(
                new Timestamp(getMilliSecondsFromCalendarAdd(SECOND, 6)),
                new Timestamp(getMilliSecondsFromCalendarAdd(SECOND, 2)));

        ac.runOnUiThread(() -> {
            viewer.setSelectedItemId(22L);
            viewer.onSuccessLoadItemList(list);
            assertThat(viewer.getViewInViewer().getHeaderViewsCount(), is(1));
            // ListView.getCount() and Adapter.getCount() take into account header views.
            assertThat(viewer.getViewInViewer().getCount(), is(4));
            assertThat(viewer.getViewInViewer().getAdapter().getCount(), is(4));
            assertThat(viewer.getViewInViewer().getCheckedItemPosition(), is(3));
            assertThat(viewer.getViewInViewer().getItemAtPosition(viewer.getViewInViewer().getCheckedItemPosition()),
                    allOf(notNullValue(), instanceOf(IncidenciaUser.class)));
        });
    }

    @Test
    public void test_OnSuccessLoadItems_2()
    {
        list = doIncidenciaUsers(
                new Timestamp(getMilliSecondsFromCalendarAdd(SECOND, 6)),
                new Timestamp(getMilliSecondsFromCalendarAdd(SECOND, 2)));

        ac.runOnUiThread(() -> {
            viewer.setSelectedItemId(0L);
            viewer.onSuccessLoadItemList(list);
            // When itemSelectedId == 0, no checkedItem.
            assertThat(viewer.getViewInViewer().getCheckedItemPosition() < 0, is(true));
        });
    }

    @Test
    public void test_OnSuccessLoadItems_3()
    {
        list = new ArrayList<>(0);

        ac.runOnUiThread(() -> {
            viewer.setSelectedItemId(22L);
            viewer.onSuccessLoadItemList(list);
            // No se cumple la condición view.getCount() > view.getHeaderViewsCount(): no se llama  view.setItemChecked().
            assertThat(viewer.getViewInViewer().getCount() <= viewer.getViewInViewer().getHeaderViewsCount(), is(true));
            // When list is empty, no checkedItem.
            assertThat(viewer.getViewInViewer().getCheckedItemPosition() < 0, is(true));
        });
    }
}
