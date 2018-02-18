package com.didekindroid.comunidad;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.ObserverSingleSelectList;
import com.didekindroid.lib_one.api.ParentViewerIf;
import com.didekindroid.lib_one.api.SpinnerEventItemSelectIf;
import com.didekindroid.lib_one.api.SpinnerEventListener;
import com.didekindroid.lib_one.api.Viewer;
import com.didekindroid.lib_one.comunidad.spinner.ComuAutonomaSpinnerEventItemSelect;
import com.didekindroid.lib_one.comunidad.spinner.MunicipioSpinnerEventItemSelect;
import com.didekindroid.lib_one.comunidad.spinner.ProvinciaSpinnerEventItemSelect;
import com.didekindroid.lib_one.comunidad.spinner.TipoViaValueObj;
import com.didekindroid.lib_one.comunidad.spinner.ViewerComuAutonomaSpinner;
import com.didekindroid.lib_one.comunidad.spinner.ViewerMunicipioSpinner;
import com.didekindroid.lib_one.comunidad.spinner.ViewerProvinciaSpinner;
import com.didekindroid.lib_one.comunidad.spinner.ViewerTipoViaSpinner;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.comunidad.ComunidadAutonoma;
import com.didekinlib.model.comunidad.Municipio;
import com.didekinlib.model.comunidad.Provincia;

import java.io.Serializable;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.lib_one.comunidad.spinner.ViewerComuAutonomaSpinner.newViewerComuAutonomaSpinner;
import static com.didekindroid.lib_one.comunidad.spinner.ViewerMunicipioSpinner.newViewerMunicipioSpinner;
import static com.didekindroid.lib_one.comunidad.spinner.ViewerProvinciaSpinner.newViewerProvinciaSpinner;
import static com.didekindroid.lib_one.comunidad.spinner.ViewerTipoViaSpinner.newViewerTipoViaSpinner;

/**
 * User: pedro@didekin
 * Date: 04/05/17
 * Time: 12:28
 */
public class ViewerRegComuFr extends Viewer<View, CtrlerComunidad> implements
        SpinnerEventListener {

    ViewerTipoViaSpinner tipoViaSpinner;
    ViewerComuAutonomaSpinner comuAutonomaSpinner;
    ViewerProvinciaSpinner provinciaSpinner;
    ViewerMunicipioSpinner municipioSpinner;
    private EditText editNombreVia;
    private EditText editNumero;
    private EditText editSufijoNumero;

    ViewerRegComuFr(View view, AppCompatActivity activity, ParentViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
    }

    static ViewerRegComuFr newViewerRegComuFr(@NonNull View view, @NonNull ParentViewerIf parentViewer)
    {
        Timber.d("newViewerRegComuFr()");
        ViewerRegComuFr instance = new ViewerRegComuFr(view, parentViewer.getActivity(), parentViewer);
        return instance.initInstanceViewer(parentViewer.getActivity());
    }

    static ViewerRegComuFr newViewerRegComuFr(@NonNull View frView, @NonNull AppCompatActivity activity)
    {
        Timber.d("newViewerRegComuFr()");
        ViewerRegComuFr instance = new ViewerRegComuFr(frView, activity, null);
        return instance.initInstanceViewer(activity);
    }

    @NonNull
    private ViewerRegComuFr initInstanceViewer(@NonNull AppCompatActivity activity)
    {
        setController(new CtrlerComunidad());

        editSufijoNumero = view.findViewById(R.id.comunidad_sufijo_numero_editT);
        editNumero = view.findViewById(R.id.comunidad_numero_editT);
        editNombreVia = view.findViewById(R.id.comunidad_nombre_via_editT);

        tipoViaSpinner =
                newViewerTipoViaSpinner(view.findViewById(R.id.tipo_via_spinner), activity, this);
        comuAutonomaSpinner =
                newViewerComuAutonomaSpinner(view.findViewById(R.id.autonoma_comunidad_spinner), activity, this);
        provinciaSpinner =
                newViewerProvinciaSpinner(view.findViewById(R.id.provincia_spinner), activity, this);
        municipioSpinner =
                newViewerMunicipioSpinner(view.findViewById(R.id.municipio_spinner), activity, this);

        return this;
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
            } else {
                setTextFields(comunidad);
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
                provinciaSpinner.setSelectedItemId(0L);
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
                municipioSpinner.setSelectedItemId(0L);
            }
            municipioSpinner.getController()
                    .loadItemsByEntitiyId(new ObserverSingleSelectList<>(municipioSpinner), spinnerEventItemSelect.getSpinnerItemIdSelect());
        }
    }

    // ===================================  Helpers  =================================

    void onSuccessLoadComunidad(Comunidad comunidad, Bundle savedState)
    {
        Timber.d("onSuccessLoadComunidad()");

        setTextFields(comunidad);
        initializeSpinnersFromComunidad(comunidad, savedState);
    }

    private void setTextFields(Comunidad comunidad)
    {
        editNombreVia.setText(comunidad.getNombreVia());
        editNumero.setText(String.valueOf(comunidad.getNumero()));
        editSufijoNumero.setText(comunidad.getSufijoNumero());
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
        bean.setNombreVia(editNombreVia.getText().toString());
        bean.setNumeroString(editNumero.getText().toString());
        bean.setSufijoNumero(editSufijoNumero.getText().toString());
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

    public ViewerTipoViaSpinner getTipoViaSpinner()
    {
        return tipoViaSpinner;
    }

    /* ===================================  Getters  =================================*/

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
}
