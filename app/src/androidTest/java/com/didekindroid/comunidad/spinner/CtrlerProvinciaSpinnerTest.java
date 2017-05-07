package com.didekindroid.comunidad.spinner;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.Spinner;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.ViewerMock;
import com.didekinlib.model.comunidad.Provincia;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.functions.Consumer;
import io.reactivex.observers.TestObserver;

import static com.didekindroid.comunidad.spinner.CtrlerProvinciaSpinner.newCtrlerProvinciaSpinner;
import static com.didekindroid.comunidad.spinner.CtrlerProvinciaSpinner.provinciasByComAutonoma;
import static com.didekindroid.comunidad.spinner.ViewerProvinciaSpinner.newViewerProvinciaSpinner;
import static com.didekindroid.testutil.ActivityTestUtils.checkSpinnerCtrlerLoadItems;
import static com.didekindroid.testutil.RxSchedulersUtils.resetAllSchedulers;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 05/05/17
 * Time: 19:13
 */
public class CtrlerProvinciaSpinnerTest {

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    CtrlerProvinciaSpinner controller;

    @Before
    public void setUp() throws Exception
    {
        final Activity activity = activityRule.getActivity();
        final AtomicReference<CtrlerProvinciaSpinner> atomicController = new AtomicReference<>(null);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                atomicController.compareAndSet(
                        null,
                        newCtrlerProvinciaSpinner(
                                newViewerProvinciaSpinner(
                                        new Spinner(activity), activity, new ViewerMock<>(new View(activity), activity, null)
                                )
                        )
                );
            }
        });
        waitAtMost(2, SECONDS).untilAtomic(atomicController, notNullValue());
        controller = atomicController.get();
    }

    @After
    public void tearDown() throws Exception
    {
        controller.clearSubscriptions();
        resetAllSchedulers();
    }

    @Test
    public void test_NewCtrlerProvinciaSpinner() throws Exception
    {
        assertThat(controller.observerSpinner, notNullValue());
    }

    @Test
    public void test_ProvinciasByComAutonoma() throws Exception
    {
        provinciasByComAutonoma(controller.getViewer().getActivity(), (short) 11).test().assertOf(new Consumer<TestObserver<List<Provincia>>>() {
            @Override
            public void accept(TestObserver<List<Provincia>> listTestObserver) throws Exception
            {
                assertThat(listTestObserver.values().size(), is(1)); // Single.
                assertThat(listTestObserver.values().get(0).size(), is(2));
                assertThat(listTestObserver.values().get(0).get(0).getNombre(), is("Badajoz"));
            }
        });
    }

    @Test
    public void test_LoadItemsByEntitiyId() throws Exception
    {
        checkSpinnerCtrlerLoadItems(controller, 11L);
    }
}