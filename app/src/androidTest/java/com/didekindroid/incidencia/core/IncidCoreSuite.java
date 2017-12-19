package com.didekindroid.incidencia.core;

import com.didekindroid.incidencia.core.edit.IncidCloseAc_GCM_Test;
import com.didekindroid.incidencia.core.edit.IncidEditAcMaxTest;
import com.didekindroid.incidencia.core.edit.IncidEditAcMinTest;
import com.didekindroid.incidencia.core.edit.IncidEditAc_Mn1_Test;
import com.didekindroid.incidencia.core.edit.ViewerIncidEditAcTest;
import com.didekindroid.incidencia.core.edit.ViewerIncidEditMaxFrTest;
import com.didekindroid.incidencia.core.edit.ViewerIncidEditMinFrTest;
import com.didekindroid.incidencia.core.edit.importancia.ViewerIncidSeeUserComuImportanciaTest;
import com.didekindroid.incidencia.core.reg.IncidRegAcTest;
import com.didekindroid.incidencia.core.reg.IncidRegAc_GCM_Test;
import com.didekindroid.incidencia.core.reg.ViewerIncidRegAcTest;
import com.didekindroid.incidencia.core.reg.ViewerIncidRegFrTest;
import com.didekindroid.incidencia.core.resolucion.IncidRegResolucion_GCM_Test;
import com.didekindroid.incidencia.core.resolucion.IncidResolucionEditAcTest;
import com.didekindroid.incidencia.core.resolucion.IncidResolucionEditFrTest;
import com.didekindroid.incidencia.core.resolucion.IncidResolucionRegAcTest;
import com.didekindroid.incidencia.core.resolucion.IncidResolucionSeeFrTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 07/04/17
 * Time: 17:56
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        // core.edit
        IncidCloseAc_GCM_Test.class,
        IncidEditAc_Mn1_Test.class,
        IncidEditAcMaxTest.class,
        IncidEditAcMinTest.class,
        ViewerIncidEditAcTest.class,
        ViewerIncidEditMaxFrTest.class,
        ViewerIncidEditMinFrTest.class,
        // core.edit.importancia
        ViewerIncidSeeUserComuImportanciaTest.class,
        // core.reg
        IncidRegAc_GCM_Test.class,
        IncidRegAcTest.class,
        ViewerIncidRegAcTest.class,
        ViewerIncidRegFrTest.class,
        // core.resolucion
        IncidRegResolucion_GCM_Test.class,
        IncidResolucionEditAcTest.class,
        IncidResolucionEditFrTest.class,
        IncidResolucionRegAcTest.class,
        IncidResolucionSeeFrTest.class,
        // core
        CtrlerAmbitoIncidSpinnerTest.class,
        CtrlerIncidenciaCoreTest.class,
        IncidenciaBeanTest.class,
        IncidenciaDataDbHelperTest.class,
        IncidImportanciaBeanTest.class,
        ViewerAmbitoIncidSpinnerTest.class,
        ViewerImportanciaSpinnerTest.class,
})
public class IncidCoreSuite {
}
