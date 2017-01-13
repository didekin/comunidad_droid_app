package com.didekindroid;

import com.didekindroid.comunidad.ComunidadTest;
import com.didekindroid.comunidad.MunicipioTest;
import com.didekindroid.incidencia.activity.IncidBundleKeyTest;
import com.didekindroid.usuariocomunidad.UsuarioComunidadTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 31/12/16
 * Time: 10:08
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        ComunidadTest.class,
        MunicipioTest.class,
        IncidBundleKeyTest.class,
        UsuarioComunidadTest.class,
})
public class AppUnitSuite {
}