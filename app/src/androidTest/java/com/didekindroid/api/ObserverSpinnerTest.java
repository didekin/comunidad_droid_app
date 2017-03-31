package com.didekindroid.api;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Spinner;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Single;

import static com.didekindroid.usuariocomunidad.spinner.ViewerComuSpinner.newViewerComuSpinner;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 16/03/17
 * Time: 17:50
 */
@RunWith(AndroidJUnit4.class)
public class ObserverSpinnerTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    ObserverSpinner<String> observerSpinner;

    @Before
    public void setUp()
    {
        final Activity activity = activityRule.getActivity();
        final AtomicReference<ObserverSpinner<String>> atomicObserver = new AtomicReference<>(null);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                atomicObserver.compareAndSet(null, new ObserverSpinner<>(new CtrlerSpinnerForTest(newViewerComuSpinner(new Spinner(activity), activity, null))));
            }
        });
        waitAtMost(2, SECONDS).untilAtomic(atomicObserver, notNullValue());
        observerSpinner = atomicObserver.get();
    }

    @Test
    public void testOnSuccess() throws Exception
    {
        List<String> stringList = Arrays.asList("uno", "dos", "tres");
        Single.just(stringList).subscribeWith(observerSpinner);
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }

    //    =========================== HELPERS =============================

    static class CtrlerSpinnerForTest extends Controller<Spinner> implements
            CtrlerSpinnerIf<String> {

        CtrlerSpinnerForTest(ViewerSelectableIf<Spinner, CtrlerSpinnerIf> viewer)
        {
            super(viewer);
        }

        @Override   // IN TEST.
        public void onSuccessLoadDataInSpinner(Collection<String> comunidades)
        {
            assertThat(comunidades.size(), is(3));
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
        }

        @Override
        public boolean loadDataInSpinner()
        {
            return false;
        }

        @Override
        public int getSelectedFromItemId(long itemId)
        {
            return 0;
        }
    }
}