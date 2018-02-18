package com.didekindroid.comunidad;

import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Button;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.Viewer;
import com.didekindroid.lib_one.security.CtrlerAuthToken;
import com.didekindroid.lib_one.security.CtrlerAuthTokenIf;
import com.didekindroid.testutil.ViewerTestWrapper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.ViewerRegComuFr.newViewerRegComuFr;
import static com.didekindroid.comunidad.testutil.ComuTestData.COMU_REAL;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.checkMunicipioSpinner;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.checkRegComuFrViewEmpty;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.typeComunidadData;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_SEARCH;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.AFTER_METHOD_EXEC_A;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.BEFORE_METHOD_EXEC;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 16/05/17
 * Time: 16:42
 */
@RunWith(AndroidJUnit4.class)
public class ViewerComuSearchAcTest {

    final AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public IntentsTestRule<ComuSearchAc> activityRule = new IntentsTestRule<>(ComuSearchAc.class, true, true);
    ComuSearchAc activity;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        AtomicReference<ViewerComuSearchAc> viewerAtomic = new AtomicReference<>(null);
        viewerAtomic.compareAndSet(null, activity.viewerAc);
        waitAtMost(4, SECONDS).untilAtomic(viewerAtomic, notNullValue());
    }

    @Test
    public void test_NewViewerComuSearch() throws Exception
    {
        assertThat(activity.viewerAc.getController(), isA(CtrlerAuthTokenIf.class));
    }

    @Test
    public void test_DoViewInViewer_1() throws Exception
    {
        onView(withId(comuSearchAcLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.searchComunidad_Bton)).check(matches(isDisplayed()));
        checkRegComuFrViewEmpty();
    }

    @Test
    public void test_DoViewInViewer_2() throws Exception
    {
        activity.viewerAc.setController(new CtrlerAuthToken() {
            @Override
            public void refreshAccessToken(Viewer viewer)
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
            }
        });
        activity.viewerAc.doViewInViewer(null, null);
        waitAtMost(4, SECONDS).untilAtomic(flagMethodExec, is(AFTER_METHOD_EXEC_A));
    }

    @Test
    public void test_SetChildViewer() throws Exception
    {
        activity.viewerAc.setChildViewer(newViewerRegComuFr(activity.regComuFrg.getView(), activity.viewerAc));
        assertThat(activity.viewerAc.getChildViewer(ViewerRegComuFr.class), notNullValue());
    }

    @Test
    public void test_ComuSearchButtonListener()
    {
        checkMunicipioSpinner("municipio"); /* Esperamos por los viejos datos.*/
        typeComunidadData();

        ViewerRegComuFr viewerRegComuFrOld = activity.viewerAc.getChildViewer(ViewerRegComuFr.class);
        activity.setChildInParentViewer(viewerRegComuFrOld);
        activity.viewerAc.setController(new CtrlerAuthToken());

        Button button = activity.acView.findViewById(R.id.searchComunidad_Bton);
        button.setOnClickListener(activity.viewerAc.new ComuSearchButtonListener());
        button.callOnClick();
        intended(allOf(
                hasExtras(hasEntry(COMUNIDAD_SEARCH.key, is(COMU_REAL))),
                hasComponent(ComuSearchResultsAc.class.getName())
        ));
    }

    //  =========================  TESTS FOR ACTIVITY/FRAGMENT LIFECYCLE  ===========================

    @Test
    public void test_OnSaveInstanceState()
    {
        final ViewerTestWrapper wrapper = new ViewerTestWrapper();
        activity.viewerAc = new ViewerComuSearchAc(null, activity) {
            @Override
            public void saveState(Bundle savedState)
            {
                wrapper.saveState();
            }

            @Override
            public int clearSubscriptions()
            {
                return wrapper.clearSubscriptions();
            }
        };
        wrapper.checkOnSaveInstanceState(activity.viewerAc);
    }
}