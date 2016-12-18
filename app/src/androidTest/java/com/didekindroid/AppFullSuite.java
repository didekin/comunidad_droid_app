package com.didekindroid;

import com.didekindroid.incidencia.IncidSuiteFunctional;
import com.didekindroid.incidencia.IncidSuiteSupport;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 17/11/15
 * Time: 12:47
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        IncidSuiteFunctional.class,
})
public class AppFullSuite {
}
