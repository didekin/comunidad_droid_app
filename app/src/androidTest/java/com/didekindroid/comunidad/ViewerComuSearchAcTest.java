package com.didekindroid.comunidad;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Button;

import com.didekindroid.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.ViewerRegComuFr.newViewerRegComuFr;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.checkMunicipioSpinner;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.checkRegComuFrViewEmpty;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.typeComunidadData;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_SEARCH;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 16/05/17
 * Time: 16:42
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class ViewerComuSearchAcTest {

    @Rule
    public IntentsTestRule<ComuSearchAc> activityRule = new IntentsTestRule<>(ComuSearchAc.class, true, true);
    private ComuSearchAc activity;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        waitAtMost(4, SECONDS).until(() -> activity.viewerAc != null);
        assertThat(activity.viewerAc, notNullValue());
    }

    @Test
    public void test_DoViewInViewer()
    {
        onView(withId(comuSearchAcLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.searchComunidad_Bton)).check(matches(isDisplayed()));
        checkRegComuFrViewEmpty();
    }

    @Test
    public void test_SetChildViewer()
    {
        activity.viewerAc.setChildViewer(newViewerRegComuFr(activity.regComuFrg.getView(), activity.viewerAc));
        waitAtMost(4, SECONDS).until(() -> activity.viewerAc.getChildViewer(ViewerRegComuFr.class) != null);
    }

    @Test
    public void test_ComuSearchButtonListener()
    {
        checkMunicipioSpinner("municipio"); /* Esperamos por los viejos datos.*/
        typeComunidadData();

        ViewerRegComuFr viewerRegComuFrOld = activity.viewerAc.getChildViewer(ViewerRegComuFr.class);
        activity.setChildInParentViewer(viewerRegComuFrOld);

        Button button = activity.acView.findViewById(R.id.searchComunidad_Bton);
        button.setOnClickListener(activity.viewerAc.new ComuSearchButtonListener());
        button.callOnClick();
        intended(allOf(
                hasExtras(hasEntry(COMUNIDAD_SEARCH.key, is(comu_real))),
                hasComponent(ComuSearchResultsAc.class.getName())
        ));
    }
}