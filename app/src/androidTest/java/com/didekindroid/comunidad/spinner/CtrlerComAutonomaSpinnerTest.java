package com.didekindroid.comunidad.spinner;

import android.app.Activity;
import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.Spinner;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.ViewerMock;
import com.didekindroid.comunidad.repository.ComunidadDataDb;
import com.didekinlib.model.comunidad.ComunidadAutonoma;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.functions.Consumer;
import io.reactivex.observers.TestObserver;

import static com.didekindroid.comunidad.spinner.CtrlerComAutonomaSpinner.comunidadesAutonomasList;
import static com.didekindroid.comunidad.spinner.CtrlerComAutonomaSpinner.newCtrlerComAutonomaSpinner;
import static com.didekindroid.comunidad.spinner.ViewerComuAutonomaSpinner.newViewerComuAutonomaSpinner;
import static com.didekindroid.testutil.ActivityTestUtils.checkSpinnerCtrlerLoadItems;
import static com.didekindroid.testutil.RxSchedulersUtils.resetAllSchedulers;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 04/05/17
 * Time: 10:09
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerComAutonomaSpinnerTest {

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    CtrlerComAutonomaSpinner controller;

    @Before
    public void setUp() throws Exception
    {
        final Activity activity = activityRule.getActivity();
        final AtomicReference<CtrlerComAutonomaSpinner> atomicController = new AtomicReference<>(null);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                atomicController.compareAndSet(
                        null,
                        newCtrlerComAutonomaSpinner(
                                newViewerComuAutonomaSpinner(
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
    public void test_NewCtrlerComAutonomaSpinner() throws Exception
    {
        assertThat(controller, notNullValue());
    }

    @Test
    public void test_ComunidadesAutonomasList() throws Exception
    {
        controller.comunidadesAutonomasList().test().assertOf(new Consumer<TestObserver<List<ComunidadAutonoma>>>() {
            @Override
            public void accept(TestObserver<List<ComunidadAutonoma>> listTestObserver) throws Exception
            {
                assertThat(listTestObserver.values().size(), is(1));
                assertThat(listTestObserver.values().get(0).size(), is(ComunidadDataDb.ComunidadAutonoma.NUMBER_RECORDS));
            }
        });
    }

    @Test
    public void test_LoadItemsByEntitiyId() throws Exception
    {
        checkSpinnerCtrlerLoadItems(controller);
    }
}