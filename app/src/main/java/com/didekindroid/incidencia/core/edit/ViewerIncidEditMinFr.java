package com.didekindroid.incidencia.core.edit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.incidencia.core.CtrlerIncidRegEditFr;
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

    private ViewerIncidEditMinFr(View view, AppCompatActivity activity, ViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
    }

    static ViewerIncidEditMinFr newViewerIncidEditMinFr(@NonNull View frView, @NonNull ViewerIf parentViewer)
    {
        Timber.d("newViewerIncidEditMaxFr()");

        AppCompatActivity activity = parentViewer.getActivity();
        ViewerIncidEditMinFr instance = new ViewerIncidEditMinFr(frView, activity, parentViewer);
        instance.viewerImportanciaSpinner =
                newViewerImportanciaSpinner((Spinner) frView.findViewById(R.id.incid_reg_importancia_spinner), activity, instance);
        instance.setController(new CtrlerIncidRegEditFr());
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
