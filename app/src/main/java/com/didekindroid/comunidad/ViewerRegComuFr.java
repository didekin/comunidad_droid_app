package com.didekindroid.comunidad;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.didekindroid.R;
import com.didekindroid.api.SpinnerEventItemSelectIf;
import com.didekindroid.api.SpinnerEventListener;
import com.didekindroid.api.Viewer;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.comunidad.spinner.ComuAutonomaSpinnerEventItemSelect;
import com.didekindroid.comunidad.spinner.MunicipioSpinnerEventItemSelect;
import com.didekindroid.comunidad.spinner.ProvinciaSpinnerEventItemSelect;
import com.didekindroid.comunidad.spinner.TipoViaValueObj;
import com.didekindroid.comunidad.spinner.ViewerComuAutonomaSpinner;
import com.didekindroid.comunidad.spinner.ViewerMunicipioSpinner;
import com.didekindroid.comunidad.spinner.ViewerProvinciaSpinner;
import com.didekindroid.comunidad.spinner.ViewerTipoViaSpinner;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.comunidad.ComunidadAutonoma;
import com.didekinlib.model.comunidad.Municipio;
import com.didekinlib.model.comunidad.Provincia;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.comunidad.spinner.ViewerComuAutonomaSpinner.newViewerComuAutonomaSpinner;
import static com.didekindroid.comunidad.spinner.ViewerMunicipioSpinner.newViewerMunicipioSpinner;
import static com.didekindroid.comunidad.spinner.ViewerProvinciaSpinner.newViewerProvinciaSpinner;
import static com.didekindroid.comunidad.spinner.ViewerTipoViaSpinner.newViewerTipoViaSpinner;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_be_registered;
import static com.didekindroid.util.UIutils.assertTrue;

/**
 * User: pedro@didekin
 * Date: 04/05/17
 * Time: 12:28
 */
class ViewerRegComuFr extends Viewer<View, CtrlerRegComuFr> implements
        SpinnerEventListener {

    ViewerTipoViaSpinner tipoViaSpinner;
    ViewerComuAutonomaSpinner comuAutonomaSpinner;
    ViewerProvinciaSpinner provinciaSpinner;
    ViewerMunicipioSpinner municipioSpinner;

    ViewerRegComuFr(View view, Activity activity, ViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
    }

    static ViewerRegComuFr newViewerRegComuFr(@NonNull View view, @NonNull ViewerIf parentViewer)
    {
        Timber.d("newViewerRegComuFr()");
        ViewerRegComuFr instance = new ViewerRegComuFr(view, parentViewer.getActivity(), parentViewer);
        instance.setController(new CtrlerRegComuFr(instance));
        instance.tipoViaSpinner =
                newViewerTipoViaSpinner((Spinner) instance.getViewInViewer().findViewById(R.id.tipo_via_spinner), parentViewer.getActivity(), instance);
        instance.comuAutonomaSpinner =
                newViewerComuAutonomaSpinner((Spinner) instance.getViewInViewer().findViewById(R.id.autonoma_comunidad_spinner), parentViewer.getActivity(), instance);
        instance.provinciaSpinner =
                newViewerProvinciaSpinner((Spinner) instance.getViewInViewer().findViewById(R.id.provincia_spinner), parentViewer.getActivity(), instance);
        instance.municipioSpinner =
                newViewerMunicipioSpinner((Spinner) instance.getViewInViewer().findViewById(R.id.municipio_spinner), parentViewer.getActivity(), instance);
        return instance;
    }

    // ==================================== ViewerIf ====================================

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");

        if (viewBean != null) {
            // Precondition.
            assertTrue(controller.isRegisteredUser(), user_should_be_registered);
            Comunidad comunidad = Comunidad.class.cast(viewBean);
            CtrlerRegComuFr.class.cast(controller).loadComunidadData(comunidad.getC_Id(), savedState);
        } else {
            tipoViaSpinner.doViewInViewer(savedState, null);
            comuAutonomaSpinner.doViewInViewer(savedState, null);
            provinciaSpinner.doViewInViewer(savedState, null);
            municipioSpinner.doViewInViewer(savedState, null);
        }
    }

    @Override
    public int clearSubscriptions()
    {
        Timber.d("clearSubscriptions()");
        return tipoViaSpinner.clearSubscriptions()
                + comuAutonomaSpinner.clearSubscriptions()
                + provinciaSpinner.clearSubscriptions()
                + municipioSpinner.clearSubscriptions();
    }

    @Override
    public void saveState(Bundle savedState)
    {
        Timber.d("saveState()");
        tipoViaSpinner.saveState(savedState);
        comuAutonomaSpinner.saveState(savedState);
        provinciaSpinner.saveState(savedState);
        municipioSpinner.saveState(savedState);
    }

    // ===================================  SpinnerEventListener  =================================

    @SuppressWarnings("ConstantConditions")
    @Override
    public void doOnClickItemId(@NonNull SpinnerEventItemSelectIf spinnerEventItemSelect)
    {
        Timber.d("doOnClickItemId()");
        if (ComuAutonomaSpinnerEventItemSelect.class.isInstance(spinnerEventItemSelect)) {
            Timber.d("ComuAutonomaSpinnerEventItemSelect");
            ComuAutonomaSpinnerEventItemSelect comunidadInEvent = ComuAutonomaSpinnerEventItemSelect.class.cast(spinnerEventItemSelect);
            ComunidadAutonoma comunidadAutonomaOld = provinciaSpinner.getProvinciaEventSelect().getProvincia().getComunidadAutonoma();
            // If comunidadAutonoma has changed, provincia is initialized to 0.
            if (!comunidadAutonomaOld.equals(comunidadInEvent.getComunidadAutonoma())) {
                provinciaSpinner.setItemSelectedId(0L);
            }
            provinciaSpinner.getController().loadItemsByEntitiyId(comunidadInEvent.getSpinnerItemIdSelect());
        }
        if (ProvinciaSpinnerEventItemSelect.class.isInstance(spinnerEventItemSelect)) {
            Timber.d("ProvinciaSpinnerEventItemSelect");
            ProvinciaSpinnerEventItemSelect provinciaInEvent = ProvinciaSpinnerEventItemSelect.class.cast(spinnerEventItemSelect);
            Municipio municipioOld = municipioSpinner.getSpinnerEvent().getMunicipio();
            // If provincia has changed, municipio is initialized to 0.
            if (!municipioOld.getProvincia().equals(provinciaInEvent.getProvincia())) {
                municipioSpinner.setItemSelectedId(0L);
            }
            municipioSpinner.getController().loadItemsByEntitiyId(spinnerEventItemSelect.getSpinnerItemIdSelect());
        }
    }

    // ===================================  Helpers  =================================

    void onSuccessLoadComunidad(Comunidad comunidad, Bundle savedState)
    {
        Timber.d("onSuccessLoadComunidad()");

        ((EditText) view.findViewById(R.id.comunidad_nombre_via_editT)).setText(comunidad.getNombreVia());
        ((EditText) view.findViewById(R.id.comunidad_numero_editT)).setText(String.valueOf(comunidad.getNumero()));
        ((EditText) view.findViewById(R.id.comunidad_sufijo_numero_editT)).setText(comunidad.getSufijoNumero());

        tipoViaSpinner.doViewInViewer(savedState, new TipoViaValueObj(comunidad.getTipoVia()));
        provinciaSpinner.doViewInViewer(savedState, new ProvinciaSpinnerEventItemSelect(comunidad.getMunicipio().getProvincia()));
        municipioSpinner.doViewInViewer(savedState, new MunicipioSpinnerEventItemSelect(comunidad.getMunicipio()));
        comuAutonomaSpinner.doViewInViewer(savedState, new ComuAutonomaSpinnerEventItemSelect(comunidad.getMunicipio().getProvincia().getComunidadAutonoma()));
    }

    Comunidad getComunidadFromViewer(StringBuilder errorMessages)
    {
        Timber.d("getComunidadFromViewer()");

        ComunidadBean bean = new ComunidadBean();
        bean.setTipoVia(tipoViaSpinner.getTipoViaValueObj());
        bean.setNombreVia(((EditText) view.findViewById(R.id.comunidad_nombre_via_editT)).getText().toString());
        bean.setNumeroString(((EditText) view.findViewById(R.id.comunidad_numero_editT)).getText().toString());
        bean.setSufijoNumero(((EditText) view.findViewById(R.id.comunidad_sufijo_numero_editT)).getText().toString());
        bean.setMunicipio(
                new Municipio(
                        (short) municipioSpinner.getSelectedItemId(),
                        new Provincia((short) provinciaSpinner.getSelectedItemId())
                )
        );
        if (bean.validate(activity.getResources(), errorMessages)) {
            return bean.getComunidad();
        }
        return null;
    }
}
