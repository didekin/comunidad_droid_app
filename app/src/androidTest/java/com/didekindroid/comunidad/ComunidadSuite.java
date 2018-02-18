package com.didekindroid.comunidad;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 22/11/16
 * Time: 10:41
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        // .
        ComuDataAcTest.class,
        ComunidadBeanTest.class,
        ComunidadDaoTest.class,
        ComunidadObservableTest.class,
        ComuSearchAcTest.class,
        ComuSearchResultsAcTest.class,
        CtrlerComunidadTest.class,
        ViewerComuDataAcTest.class,
        ViewerComuSearchAcTest.class,
        ViewerComuSearchResultAcTest.class,
        ViewerComuSearchResultsFrTest.class,
        ViewerRegComuFr_Mock_Test.class,
        ViewerRegComuFrTest.class,
})
public class ComunidadSuite {
}
