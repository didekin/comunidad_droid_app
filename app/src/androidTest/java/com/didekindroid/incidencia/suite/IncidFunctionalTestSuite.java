package com.didekindroid.incidencia.suite;

import com.didekindroid.incidencia.activity.IncidEditAcTest;
import com.didekindroid.incidencia.activity.IncidRegAcTest;
import com.didekindroid.incidencia.activity.IncidSeeByComuAcTest_1;
import com.didekindroid.incidencia.activity.IncidSeeByComuAcTest_2;
import com.didekindroid.incidencia.gcm.GcmBroadCastNotificationAcTest;
import com.didekindroid.incidencia.gcm.IncidRegAcTest_gcm1;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 16/11/15
 * Time: 18:55
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        GcmBroadCastNotificationAcTest.class,
        IncidEditAcTest.class,
        IncidRegAcTest.class,
        IncidRegAcTest_gcm1.class,
        IncidSeeByComuAcTest_1.class,
        IncidSeeByComuAcTest_2.class,
})
public class IncidFunctionalTestSuite {
}
