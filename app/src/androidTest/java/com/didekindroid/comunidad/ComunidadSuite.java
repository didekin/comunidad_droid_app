package com.didekindroid.comunidad;

import com.didekindroid.comunidad.repository.ComunidadDbHelperTest;
import com.didekindroid.comunidad.repository.MockDbHelperTest;

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
        ComunidadServiceTest.class,
        ComuSearchAc_1_Test.class,
        ComuSearchAc_2_Test.class,
        ComuSearchAc_3_SlowTest.class,
        ComuSearchResultsAc_1_Test.class,
        ComuSearchResultsAc_2_SlowTest.class,
        MockDbHelperTest.class,
})
public class ComunidadSuite {

}
