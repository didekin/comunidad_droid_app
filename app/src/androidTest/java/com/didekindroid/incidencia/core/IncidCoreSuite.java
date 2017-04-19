package com.didekindroid.incidencia.core;

import com.didekindroid.incidencia.core.edit.CtrlerIncidEditAcTest;
import com.didekindroid.incidencia.core.edit.CtrlerIncidRegEditFr_Edit_Test;
import com.didekindroid.incidencia.core.edit.IncidCloseAc_GCM_Test;
import com.didekindroid.incidencia.core.edit.IncidEditAcMaxTest;
import com.didekindroid.incidencia.core.edit.IncidEditAcMinTest;
import com.didekindroid.incidencia.core.edit.IncidEditAc_Mn_Test;
import com.didekindroid.incidencia.core.edit.ViewerIncidEditAcTest;
import com.didekindroid.incidencia.core.edit.ViewerIncidEditMaxFrTest;
import com.didekindroid.incidencia.core.edit.ViewerIncidEditMinFrTest;
import com.didekindroid.incidencia.core.reg.CtrlerIncidRegEditFr_Reg_Test;
import com.didekindroid.incidencia.core.reg.IncidRegAcTest;
import com.didekindroid.incidencia.core.reg.IncidRegAc_GCM_Test;
import com.didekindroid.incidencia.core.reg.ViewerIncidRegAcTest;
import com.didekindroid.incidencia.core.reg.ViewerIncidRegFrTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 07/04/17
 * Time: 17:56
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        // GCM
        IncidCloseAc_GCM_Test.class,
        IncidRegAc_GCM_Test.class,
        // Activities.
        IncidEditAc_Mn_Test.class,
        IncidEditAcMaxTest.class,
        IncidEditAcMinTest.class,
        IncidRegAcTest.class,
        // Viewers
        ViewerAmbitoIncidSpinnerTest.class,
        ViewerImportanciaSpinnerTest.class,
        ViewerIncidEditAcTest.class,
        ViewerIncidEditMaxFrTest.class,
        ViewerIncidEditMinFrTest.class,
        ViewerIncidRegAcTest.class,
        ViewerIncidRegFrTest.class,
        // Controllers
        CtrlerAmbitoIncidSpinnerTest.class,
        CtrlerImportanciaSpinnerTest.class,
        CtrlerIncidEditAcTest.class,
        CtrlerIncidRegEditFr_Edit_Test.class,
        CtrlerIncidRegEditFr_Reg_Test.class,
        // Helpers
        IncidenciaBeanTest.class,
        IncidenciaDataDbHelperTest.class,
        IncidImportanciaBeanTest.class,
})
public class IncidCoreSuite {
}
