package com.didekindroid.incidencia.suite;

import com.didekindroid.incidencia.activity.IncidRegAcTest;
import com.didekindroid.incidencia.activity.IncidSeeByUserComuAcTest;

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
        IncidSeeByUserComuAcTest.class,
})
public class IncidFunctionalTestSuite {
}
