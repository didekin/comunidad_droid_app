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
        CtrlerIdentityTest.class,
        CtrlerSpinnerTest.class,
        ObserverSingleListSelectedTest.class,
        ObserverMaybeListTest.class,
        ObserverSpinnerTest.class,
        RootViewReplacerTest.class,
        ViewerTest.class,
})
public class ApiSuite {
}
