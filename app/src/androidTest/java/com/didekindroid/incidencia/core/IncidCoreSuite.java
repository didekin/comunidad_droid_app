package com.didekindroid.incidencia.core;

import com.didekindroid.incidencia.core.edit.IncidEditAcMaxTest;
import com.didekindroid.incidencia.core.edit.IncidEditAcMax_delete_Test;
import com.didekindroid.incidencia.core.edit.IncidEditAc_Mn1_Test;
import com.didekindroid.incidencia.core.edit.IncidEditAc_Mn2_Test;
import com.didekindroid.incidencia.core.edit.ViewerIncidEditAcTest;
import com.didekindroid.incidencia.core.edit.ViewerIncidEditMaxFrTest;
import com.didekindroid.incidencia.core.edit.ViewerIncidEditMaxFr_erase_Test;
import com.didekindroid.incidencia.core.edit.ViewerIncidEditMinFrTest;
import com.didekindroid.incidencia.core.edit.importancia.ViewerIncidSeeUserComuImportanciaTest;
import com.didekindroid.incidencia.core.reg.IncidRegAcTest;
import com.didekindroid.incidencia.core.reg.ViewerIncidRegAcTest;
import com.didekindroid.incidencia.core.reg.ViewerIncidRegFrTest;
import com.didekindroid.incidencia.core.resolucion.IncidResolucionEditAcTest;
import com.didekindroid.incidencia.core.resolucion.IncidResolucionEditFrTest;
import com.didekindroid.incidencia.core.resolucion.IncidResolucionRegAcTest;
import com.didekindroid.incidencia.core.resolucion.IncidResolucionSeeFr_Test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 07/04/17
 * Time: 17:56
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        /* core.edit*/
        IncidEditAc_Mn1_Test.class,
        IncidEditAc_Mn2_Test.class,
        IncidEditAcMaxTest.class,
        IncidEditAcMax_delete_Test.class,
        ViewerIncidEditAcTest.class,
        ViewerIncidEditMaxFrTest.class,
        ViewerIncidEditMaxFr_erase_Test.class,
        ViewerIncidEditMinFrTest.class,
        // core.edit.importancia
        ViewerIncidSeeUserComuImportanciaTest.class,
        // core.reg
        IncidRegAcTest.class,
        ViewerIncidRegAcTest.class,
        ViewerIncidRegFrTest.class,
        // core.resolucion
        IncidResolucionEditAcTest.class,
        IncidResolucionEditFrTest.class,
        IncidResolucionRegAcTest.class,
        IncidResolucionSeeFr_Test.class,
        /* core*/
        CtrlerIncidenciaCoreTest.class,
        Incid_Firebase_Notif_Test.class,
        IncidImportanciaBeanTest.class,
        ViewerImportanciaSpinnerTest.class,
})
public class IncidCoreSuite {
}
