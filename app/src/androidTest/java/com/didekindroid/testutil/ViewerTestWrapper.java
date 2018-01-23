package com.didekindroid.testutil;

import android.os.Bundle;

import com.didekindroid.api.Viewer;

import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;

/**
 * User: pedro@didekin
 * Date: 16/09/17
 * Time: 15:00
 */
public class ViewerTestWrapper {

    public final String AFTER_ClearSubscriptions = "afterClearSubscriptionsMethod";
    public final String AFTER_SaveState = "afterSaveStateMethod";
    final AtomicReference<String> flagMethod = new AtomicReference<>(BEFORE_METHOD_EXEC);

    public int clearSubscriptions()
    {
        flagMethod.set(AFTER_ClearSubscriptions);
        return 0;
    }

    public void saveState()
    {
        flagMethod.set(AFTER_SaveState);
    }

    public void checkOnSaveInstanceState(final Viewer viewer)
    {
        viewer.getActivity().runOnUiThread(() -> getInstrumentation().callActivityOnSaveInstanceState(viewer.getActivity(), new Bundle(0)));
        waitAtMost(6, SECONDS).untilAtomic(flagMethod, is(AFTER_SaveState));
    }
}
