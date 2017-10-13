package com.didekindroid.incidencia.core.edit;

import android.os.Bundle;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static com.didekindroid.incidencia.utils.IncidFragmentTags.incid_edit_ac_frgs_tag;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 08/09/17
 * Time: 11:34
 */
@SuppressWarnings("AbstractClassWithoutAbstractMethods")
abstract class IncidEditAcTest {

    final AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);
    IncidEditAc activity;

    @Test
    public void test_OnCreate() throws Exception
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                getInstrumentation().callActivityOnSaveInstanceState(activity, new Bundle(0));
                assertThat(activity.acView, notNullValue());
                assertThat(activity.resolBundle, notNullValue());
                assertThat(activity.viewer, notNullValue());
                IncidEditFr fragmentToAdd = (IncidEditFr) activity.getSupportFragmentManager().findFragmentByTag(incid_edit_ac_frgs_tag);
                assertThat(fragmentToAdd, notNullValue());
                assertThat(fragmentToAdd.resolBundle, notNullValue());
                assertThat(fragmentToAdd.frView, notNullValue());
            }
        });
    }

    @Test
    public void testOnSaveInstanceState()
    {
        activity.viewer = new ViewerIncidEditAc(activity) {
            @Override
            public void saveState(Bundle savedState)
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
            }

            @Override
            public int clearSubscriptions()  // It is called from onStop() and gives problems.
            {
                return 0;
            }
        };
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                getInstrumentation().callActivityOnSaveInstanceState(activity, new Bundle(0));
            }
        });
        waitAtMost(4, SECONDS).untilAtomic(flagMethodExec, is(AFTER_METHOD_EXEC_A));
    }
}