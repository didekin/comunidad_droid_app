package com.didekindroid;

import com.didekindroid.gcm.GcmBroadCastNotificationActivityTest;
import com.didekindroid.gcm.GcmRequestTest;
import com.didekindroid.incidencia.IncidFunctionalSuite;
import com.didekindroid.incidencia.IncidSupportSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 17/11/15
 * Time: 12:47
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        IncidFunctionalSuite.class,
        IncidSupportSuite.class,
})
public class AppFullSuite {
}
