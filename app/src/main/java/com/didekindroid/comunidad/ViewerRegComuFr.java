package com.didekindroid.comunidad;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.didekindroid.R;
import com.didekindroid.api.ObserverSingleSelectList;
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

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.comunidad.spinner.ViewerComuAutonomaSpinner.newViewerComuAutonomaSpinner;
import static com.didekindroid.comunidad.spinner.ViewerMunicipioSpinner.newViewerMunicipioSpinner;
import static com.didekindroid.comunidad.spinner.ViewerProvinciaSpinner.newViewerProvinciaSpinner;
import static com.didekindroid.comunidad.spinner.ViewerTipoViaSpinner.newViewerTipoViaSpinner;

/**
 * User: pedro@didekin
 * Date: 04/05/17
 * Time: 12:28
 */
public class ViewerRegComuFr extends Viewer<View, CtrlerRegComuFr> implements
        SpinnerEventListener {

    ViewerTipoViaSpinner tipoViaSpinner;
    ViewerComuAutonomaSpinner comuAutonomaSpinner;
    ViewerProvinciaSpinner provinciaSpinner;
    ViewerMunicipioSpinner municipioSpinner;

    ViewerRegComuFr(View view, AppCompatActivity activity, ViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
    }

    static ViewerRegComuFr newViewerRegComuFr(@NonNull View view, @NonNull ViewerIf parentViewer)
    {
        Timber.d("newViewerRegComuFr()");
        ViewerRegComuFr instance = new ViewerRegComuFr(view, parentViewer.getActivity(), parentViewer);
        return initInstanceViewer(parentViewer.getActivity(), instance);
    }

    static ViewerRegComuFr newViewerRegComuFr(@NonNull View frView, @NonNull AppCompatActivity activity)
    {
        Timber.d("newViewerRegComuFr()");
        ViewerRegComuFr instance = new ViewerRegComuFr(frView, activity, null);
        return initInstanceViewer(activity, instance);
    }

    @NonNull
    private static ViewerRegComuFr initInstanceViewer(@NonNull AppCompatActivity activity, ViewerRegComuFr instance)
    {
        instance.setController(new CtrlerRegComuFr());
        instance.tipoViaSpinner =
                newViewerTipoViaSpinner((Spinner) instance.getViewInViewer().findViewById(R.id.tipo_via_spinner), activity, instance);
        instance.comuAutonomaSpinner =
                newViewerComuAutonomaSpinner((Spinner) instance.getViewInViewer().findViewById(R.id.autonoma_comunidad_spinner), activity, instance);
        instance.provinciaSpinner =
                newViewerProvinciaSpinner((Spinner) instance.getViewInViewer().findViewById(R.id.provincia_spinner), activity, instance);
        instance.municipioSpinner =
                newViewerMunicipioSpinner((Spinner) instance.getViewInViewer().findViewById(R.id.municipio_spinner), activity, instance);
        return instance;
    }

    // ==================================== ViewerIf ====================================

    /**
     * @param viewBean may have three states:
     *                 - null, if no intent is passed to the fragment's parent activity.
     *                 - a comunidad initialized only with an ID, if the user is editing a previous comunidad.
     *                 - a comunidad initialized, without ID, to register a new comunidad previously searched.
     */
    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        Comunidad comunidad = null;

        if (viewBean != null) {
            comunidad = Comunidad.class.cast(viewBean);
            if (comunidad.getC_Id() > 0L) {
                controller.loadComunidadData(new RegComuFrObserver(savedState), comunidad.getC_Id());
                return;
            }
        }
        initializeSpinnersFromComunidad(comunidad, savedState);
    }

    @Override
    public int clearSubscriptions()
    {
        Timber.d("clearSubscriptions()");
        return tipoViaSpinner.clearSubscriptions()
                + comuAutonomaSpinner.clearSubscriptions()
                + provinciaSpinner.clearSubscriptions()
                + municipioSpinner.clearSubscriptions()
                + controller.clearSubscriptions();
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
            provinciaSpinner.getController()
                    .loadItemsByEntitiyId(new ObserverSingleSelectList<>(provinciaSpinner), comunidadInEvent.getSpinnerItemIdSelect());
        }
        if (ProvinciaSpinnerEventItemSelect.class.isInstance(spinnerEventItemSelect)) {
            Timber.d("ProvinciaSpinnerEventItemSelect");
            ProvinciaSpinnerEventItemSelect provinciaInEvent = ProvinciaSpinnerEventItemSelect.class.cast(spinnerEventItemSelect);
            Municipio municipioOld = municipioSpinner.getSpinnerEvent().getMunicipio();
            // If provincia has changed, municipio is initialized to 0.
            if (!municipioOld.getProvincia().equals(provinciaInEvent.getProvincia())) {
                municipioSpinner.setItemSelectedId(0L);
            }
            municipioSpinner.getController()
                    .loadItemsByEntitiyId(new ObserverSingleSelectList<>(municipioSpinner), spinnerEventItemSelect.getSpinnerItemIdSelect());
        }
    }

    // ===================================  Helpers  =================================

    void onSuccessLoadComunidad(Comunidad comunidad, Bundle savedState)
    {
        Timber.d("onSuccessLoadComunidad()");

        ((EditText) view.findViewById(R.id.comunidad_nombre_via_editT)).setText(comunidad.getNombreVia());
        ((EditText) view.findViewById(R.id.comunidad_numero_editT)).setText(String.valueOf(comunidad.getNumero()));
        ((EditText) view.findViewById(R.id.comunidad_sufijo_numero_editT)).setText(comunidad.getSufijoNumero());

        initializeSpinnersFromComunidad(comunidad, savedState);
    }

    void initializeSpinnersFromComunidad(Comunidad comunidad, Bundle savedState)
    {
        tipoViaSpinner.doViewInViewer(savedState,
                comunidad != null ? new TipoViaValueObj(comunidad.getTipoVia()) : null);
        provinciaSpinner.doViewInViewer(savedState,
                comunidad != null ? new ProvinciaSpinnerEventItemSelect(comunidad.getMunicipio().getProvincia()) : null);
        municipioSpinner.doViewInViewer(savedState,
                comunidad != null ? new MunicipioSpinnerEventItemSelect(comunidad.getMunicipio()) : null);
        comuAutonomaSpinner.doViewInViewer(savedState, comunidad != null ?
                new ComuAutonomaSpinnerEventItemSelect(comunidad.getMunicipio().getProvincia().getComunidadAutonoma()) : null);
    }

    public Comunidad getComunidadFromViewer(StringBuilder errorMessages)
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
                        new Provincia(
                                new ComunidadAutonoma((short) comuAutonomaSpinner.getSelectedItemId()),
                                (short) provinciaSpinner.getSelectedItemId(),
                                null
                        )
                )
        );
        if (bean.validate(activity.getResources(), errorMessages)) {
            return bean.getComunidad();
        }
        return null;
    }

    /* ===================================  Observers  =================================*/

    @SuppressWarnings("WeakerAccess")
    class RegComuFrObserver extends DisposableSingleObserver<Comunidad> {

        private final Bundle savedState;

        RegComuFrObserver(Bundle savedState)
        {
            this.savedState = savedState;
        }

        @Override
        public void onSuccess(Comunidad comunidad)
        {
            Timber.d("onSuccess()");
            onSuccessLoadComunidad(comunidad, savedState);
        }

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onError()");
            onErrorInObserver(e);
        }
    }

    /* ===================================  Getters  =================================*/

    public ViewerTipoViaSpinner getTipoViaSpinner()
    {
        return tipoViaSpinner;
    }

    public ViewerComuAutonomaSpinner getComuAutonomaSpinner()
    {
        return comuAutonomaSpinner;
    }

    public ViewerProvinciaSpinner getProvinciaSpinner()
    {
        return provinciaSpinner;
    }

    public ViewerMunicipioSpinner getMunicipioSpinner()
    {
        return municipioSpinner;
    }
}
