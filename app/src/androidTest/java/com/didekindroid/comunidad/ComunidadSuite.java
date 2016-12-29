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
        ComuDataAcTest.class,
        ComunidadBeanValidaTests.class,
        ComunidadDbHelperTest.class,
        ComunidadTest.class,
        ComuSearchAc_1_Test.class,
        ComuSearchAc_2_Test.class,
        ComuSearchAc_3_SlowTest.class,
        ComuSearchResultsAc_1_Test.class,
        ComuSearchResultsAc_2_SlowTest.class,
        MunicipioTest.class,
})
public class ComunidadSuite {

}