package com.didekindroid.comunidad;

import com.didekindroid.comunidad.repository.ComunidadDbHelperTest;
import com.didekindroid.comunidad.spinner.CtrlerComAutonomaSpinnerTest;
import com.didekindroid.comunidad.spinner.CtrlerMunicipioSpinnerTest;
import com.didekindroid.comunidad.spinner.CtrlerProvinciaSpinnerTest;
import com.didekindroid.comunidad.spinner.CtrlerTipoViaSpinnerTest;
import com.didekindroid.comunidad.spinner.ViewerComuAutonomaSpinnerTest;
import com.didekindroid.comunidad.spinner.ViewerMunicipioSpinnerTest;
import com.didekindroid.comunidad.spinner.ViewerProvinciaSpinnerTest;
import com.didekindroid.comunidad.spinner.ViewerTipoViaSpinnerTest;

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
        ComunidadBeanTest.class,
        ComunidadDaoTest.class,
        ComunidadDbHelperTest.class,
        ComunidadObservableTest.class,
        ComuSearchAcTest.class,
        ComuSearchResultsAc_1_Test.class,
        ComuSearchResultsAc_2_Test.class,
        CtrlerComAutonomaSpinnerTest.class,
        CtrlerComuDataAcTest.class,
        CtrlerMunicipioSpinnerTest.class,
        CtrlerProvinciaSpinnerTest.class,
        CtrlerRegComuFrTest.class,
        CtrlerTipoViaSpinnerTest.class,
        ViewerComuAutonomaSpinnerTest.class,
        ViewerComuDataAcTest.class,
        ViewerComuSearchTest.class,
        ViewerMunicipioSpinnerTest.class,
        ViewerProvinciaSpinnerTest.class,
        ViewerRegComuFrTest.class,
        ViewerTipoViaSpinnerTest.class,
})
public class ComunidadSuite {
}
