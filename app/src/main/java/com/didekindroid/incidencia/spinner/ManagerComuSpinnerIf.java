package com.didekindroid.incidencia.spinner;

import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.didekindroid.ManagerIf;
import com.didekindroid.ViewerWithSelectIf;
import com.didekinlib.model.comunidad.Comunidad;

import java.util.Collection;


/**
 * User: pedro@didekin
 * Date: 12/01/16
 * Time: 14:14
 */
public interface ManagerComuSpinnerIf<B> extends ManagerIf<B>{

    /**
     * Implementations provide fragments witn a comunidadId passed in a Notification PendingIntent.
     */
    long getComunidadIdInIntent();

    Spinner initSpinnerView();

    Spinner getSpinnerViewInManager();

    AdapterView.OnItemSelectedListener getSpinnerListener();

    // ....................... CONTROLLER .......................

    interface ControllerComuSpinnerIf extends ControllerIf {

        void processBackLoadComusInSpinner(Collection<Comunidad> comunidades);
        void loadDataInSpinner();
    }

    // ........................ VIEWER ...........................

    interface ViewerComuSpinnerIf<B> extends ViewerWithSelectIf<Spinner,B> {
        ViewerComuSpinnerIf<B> setDataInView(Bundle savedState);
        long getComunidadSelectedId();
    }

    // ....................... REACTOR ...........................

    interface ReactorComuSpinnerIf {
        boolean loadComunidades();
    }
}
