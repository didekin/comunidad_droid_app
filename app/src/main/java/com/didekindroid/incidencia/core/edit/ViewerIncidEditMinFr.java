package com.didekindroid.incidencia.core.edit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.api.ParentViewerInjectedIf;
import com.didekindroid.incidencia.core.CtrlerIncidenciaCore;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.incidencia.core.ViewerImportanciaSpinner.newViewerImportanciaSpinner;

/**
 * User: pedro@didekin
 * Date: 04/04/17
 * Time: 15:06
 */
final class ViewerIncidEditMinFr extends ViewerIncidEditFr {

    private ViewerIncidEditMinFr(View view, ParentViewerInjectedIf parentViewer)
    {
        super(view, parentViewer.getActivity(), parentViewer);
    }

    static ViewerIncidEditMinFr newViewerIncidEditMinFr(@NonNull View frView, @NonNull ParentViewerInjectedIf parentViewer)
    {
        Timber.d("newViewerIncidEditMaxFr()");
        ViewerIncidEditMinFr instance = new ViewerIncidEditMinFr(frView, parentViewer);
        instance.viewerImportanciaSpinner =
                newViewerImportanciaSpinner(frView.findViewById(R.id.incid_reg_importancia_spinner), instance);
        instance.setController(new CtrlerIncidenciaCore());
        return instance;
    }

    @Override
    public void doViewInViewer(Bundle savedState, @NonNull Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        super.doViewInViewer(savedState, viewBean);

        ((TextView) view.findViewById(R.id.incid_reg_desc_txt)).setText(resolBundle.getIncidImportancia().getIncidencia().getDescripcion());
        ((TextView) view.findViewById(R.id.incid_ambito_view)).setText(controller.getAmbitoIncidDesc(incidenciaBean.getCodAmbitoIncid()));
    }

    protected IncidImportancia doNewIncidImportancia(StringBuilder errorMsg) throws IllegalStateException
    {
        Timber.d("doNewIncidImportancia()");
        return incidImportanciaBean.makeIncidImportancia(
                errorMsg,
                activity.getResources(),
                resolBundle.getIncidImportancia()
        );
    }
}
