package com.didekindroid.incidencia.suite;

import com.didekindroid.incidencia.activity.IncidRegAcTest;
import com.didekindroid.incidencia.activity.IncidSeeByUserAcTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 16/11/15
 * Time: 18:55
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        IncidRegAcTest.class,
        IncidSeeByUserAcTest.class,
})
public class IncidFunctionalTestSuite {
}
