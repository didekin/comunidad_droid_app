package com.didekindroid.incidencia.list;

import android.os.Bundle;
import android.widget.ListView;

import com.didekindroid.api.ManagerIf;
import com.didekindroid.api.ViewerWithSelectIf;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import java.io.Serializable;
import java.util.List;

/**
 * User: pedro@didekin
 * Date: 12/01/16
 * Time: 12:16
 */
public interface ManagerIncidSeeIf<B> extends ManagerIf<B> {

    /**
     * To allow for controller injection in the viewer from the manager.
     */
    ControllerIncidSeeIf getController();

    // .................... VIEWER .....................

    interface ViewerIncidSeeIf<B> extends ViewerWithSelectIf<ListView, B> {

        void doIncidListView(Bundle savedState);
    }

    // ................. CONTROLLER ....................

    interface ControllerIncidSeeIf<S extends Serializable> extends ControllerIf {

        void loadIncidsByComu(long comunidadId);

        void processBackLoadIncidsByComu(List<IncidenciaUser> incidList);

        void dealWithIncidSelected(Incidencia incidencia);

        void processBackDealWithIncidencia(S itemBack);
    }

    // .............................. REACTORS ..................................

    interface ReactorIncidSeeIf {

        boolean seeResolucion(ControllerIncidSeeIf<Resolucion> controller, Incidencia incidencia);

        boolean seeIncidClosedList(ControllerIncidSeeIf controller, long comunidadId);

        boolean seeIncidOpenList(ControllerIncidSeeIf controller, long comunidadId);

        boolean seeIncidImportancia(ControllerIncidSeeIf<IncidAndResolBundle> controller, Incidencia incidencia);
    }
}
