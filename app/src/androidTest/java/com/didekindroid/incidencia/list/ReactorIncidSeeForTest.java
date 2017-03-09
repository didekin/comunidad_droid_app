package com.didekindroid.incidencia.list;

import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 08/03/17
 * Time: 13:39
 *
 * Helper class for ControllerIncidCloseSeeTest and ControllerIncidOpenSeeTest.
 */
class ReactorIncidSeeForTest implements ManagerIncidSeeIf.ReactorIncidSeeIf {

    final static AtomicReference<String> flagReactorMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);
    final static String AFTER_seeIncidClosedList_EXEC = "after execution of seeIncidClosedList";
    final static String AFTER_seeResolucioin_EXEC = "after execution of seeResolucion";
    final static String AFTER_seeIncidOpenList_EXEC = "after execution of seeIncidOpenList";
    final static String AFTER_seeIncidImportancia_EXEC = "after execution of seeIncidImportancia";

    @Override  // User in ControllerIncidCloseSeeTest.testDealWithIncidSelected().
    public boolean seeResolucion(ManagerIncidSeeIf.ControllerIncidSeeIf<Resolucion> controller, Incidencia incidencia)
    {
        assertThat(flagReactorMethodExec.getAndSet(AFTER_seeResolucioin_EXEC), is(BEFORE_METHOD_EXEC));
        return flagReactorMethodExec.get().equals(AFTER_seeResolucioin_EXEC);
    }

    @Override  // Used in ControllerIncidCloseSeeTest.testLoadIncidsByComu().
    public boolean seeIncidClosedList(ManagerIncidSeeIf.ControllerIncidSeeIf controller, long comunidadId)
    {
        assertThat(flagReactorMethodExec.getAndSet(AFTER_seeIncidClosedList_EXEC), is(BEFORE_METHOD_EXEC));
        return flagReactorMethodExec.get().equals(AFTER_seeIncidClosedList_EXEC);
    }

    @Override // Used in ControllerIncidOpenSeeTest.loadIncidsByComu().
    public boolean seeIncidOpenList(ManagerIncidSeeIf.ControllerIncidSeeIf controller, long comunidadId)
    {
        assertThat(flagReactorMethodExec.getAndSet(AFTER_seeIncidOpenList_EXEC), is(BEFORE_METHOD_EXEC));
        return flagReactorMethodExec.get().equals(AFTER_seeIncidOpenList_EXEC);
    }

    @Override  // Used in ControllerIncidOpenSeeTest.dealWithIncidSelected().
    public boolean seeIncidImportancia(ManagerIncidSeeIf.ControllerIncidSeeIf<IncidAndResolBundle> controller, Incidencia incidencia)
    {
        assertThat(flagReactorMethodExec.getAndSet(AFTER_seeIncidImportancia_EXEC), is(BEFORE_METHOD_EXEC));
        return flagReactorMethodExec.get().equals(AFTER_seeIncidImportancia_EXEC);
    }
}
