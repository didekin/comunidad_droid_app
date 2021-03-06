package com.didekindroid.usuariocomunidad;

import com.didekindroid.usuariocomunidad.data.UserComuDataAc_Delete_Test;
import com.didekindroid.usuariocomunidad.repository.UserComuDaoTest;
import com.didekindroid.usuariocomunidad.data.UserComuDataAcTest;
import com.didekindroid.usuariocomunidad.data.ViewerUserComuDataAcTest;
import com.didekindroid.usuariocomunidad.listbycomu.CtrlerUserComuByComuTest;
import com.didekindroid.usuariocomunidad.listbycomu.SeeUserComuByComuAcTest;
import com.didekindroid.usuariocomunidad.listbycomu.ViewerSeeUserComuByComuTest;
import com.didekindroid.usuariocomunidad.listbyuser.SeeUserComuByUserAcTest;
import com.didekindroid.usuariocomunidad.repository.CtrlerUsuarioComunidadTest;
import com.didekindroid.usuariocomunidad.register.RegComuAndUserAndUserComuAcTest;
import com.didekindroid.usuariocomunidad.register.RegComuAndUserComuAcTest;
import com.didekindroid.usuariocomunidad.register.RegUserAndUserComuAcTest;
import com.didekindroid.usuariocomunidad.register.RegUserComuAcTest;
import com.didekindroid.usuariocomunidad.register.ViewerRegComuUserComuAcTest;
import com.didekindroid.usuariocomunidad.register.ViewerRegComuUserUserComuAcTest;
import com.didekindroid.usuariocomunidad.register.ViewerRegUserAndUserComuAcTest;
import com.didekindroid.usuariocomunidad.register.ViewerRegUserComuAcTest;
import com.didekindroid.usuariocomunidad.register.ViewerRegUserComuFrTest;
import com.didekindroid.usuariocomunidad.spinner.CtrlerComuSpinnerTest;
import com.didekindroid.usuariocomunidad.spinner.ViewerComuSpinnerTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 22/11/16
 * Time: 10:41
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        // data.
        UserComuDataAcTest.class,
        UserComuDataAc_Delete_Test.class,
        ViewerUserComuDataAcTest.class,
        // listbycomu
        CtrlerUserComuByComuTest.class,
        SeeUserComuByComuAcTest.class,
        ViewerSeeUserComuByComuTest.class,
        // listbyuser
        SeeUserComuByUserAcTest.class,
        // register
        CtrlerUsuarioComunidadTest.class,
        RegComuAndUserAndUserComuAcTest.class,
        RegComuAndUserComuAcTest.class,
        RegUserAndUserComuAcTest.class,
        RegUserComuAcTest.class,
        ViewerRegComuUserComuAcTest.class,
        ViewerRegComuUserUserComuAcTest.class,
        ViewerRegUserAndUserComuAcTest.class,
        ViewerRegUserComuAcTest.class,
        ViewerRegUserComuFrTest.class,
        // repository.
        UserComuDaoTest.class,
        // spinner.
        CtrlerComuSpinnerTest.class,
        ViewerComuSpinnerTest.class,
        // .
        UserComuMockDaoTest.class,
        UsuarioComunidadBeanTests.class,
})
public class UserComuSuite {
}
