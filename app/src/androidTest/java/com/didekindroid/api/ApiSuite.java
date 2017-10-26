package com.didekindroid.api;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 18/02/17
 * Time: 12:33
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        ControllerTest.class,
        ObserverCacheCleanerTest.class,
        ObserverSingleSelectItemTest.class,
        ObserverSingleSelectListTest.class,
        ParentViewerInjectedTest.class,
        ViewerSelectListTest.class,
        ViewerTest.class,
})
public class ApiSuite {
}
