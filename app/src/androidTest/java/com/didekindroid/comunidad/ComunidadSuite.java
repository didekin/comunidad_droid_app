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
        // repository
        ComunidadDbHelperTest.class,
        // spinner
        CtrlerComAutonomaSpinnerTest.class,
        CtrlerMunicipioSpinnerTest.class,
        CtrlerProvinciaSpinnerTest.class,
        CtrlerTipoViaSpinnerTest.class,
        ViewerComuAutonomaSpinnerTest.class,
        ViewerMunicipioSpinnerTest.class,
        ViewerProvinciaSpinnerTest.class,
        ViewerTipoViaSpinnerTest.class,
})
public class ComunidadSuite {
}
