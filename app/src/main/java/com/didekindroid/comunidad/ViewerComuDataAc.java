package com.didekindroid.comunidad;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.ParentViewer;
import com.didekinlib.model.comunidad.Comunidad;

import java.io.Serializable;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.comunidad.util.ComuContextualName.comu_data_just_modified;
import static com.didekindroid.comunidad.util.ComunidadAssertionMsg.comuData_should_be_modified;
import static com.didekindroid.comunidad.util.ComunidadAssertionMsg.comunidadId_should_be_initialized;
import static com.didekindroid.lib_one.util.ConnectionUtils.checkInternetConnected;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;
import static com.didekindroid.lib_one.util.UiUtil.getErrorMsgBuilder;
import static com.didekindroid.lib_one.util.UiUtil.makeToast;

/**
 * User: pedro@didekin
 * Date: 08/05/17
 * Time: 14:09
 */
class ViewerComuDataAc extends ParentViewer<View, CtrlerComunidad> {

    private ViewerComuDataAc(View view, AppCompatActivity activity)
    {
        super(view, activity, null);
    }

    static ViewerComuDataAc newViewerComuDataAc(@NonNull ComuDataAc activity)
    {
        Timber.d("newViewerComuDataAc()");
        ViewerComuDataAc instance = new ViewerComuDataAc(activity.acView, activity);
        instance.setController(new CtrlerComunidad());
        // We initialize viewerRegComuFr in its associated fragment.
        return instance;
    }

    // ==================================== ViewerIf ====================================

    @Override
    public void doViewInViewer(Bundle savedState, final Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        assertTrue(controller.isRegisteredUser() && viewBean != null, comunidadId_should_be_initialized);
        Button modifyButton = view.findViewById(R.id.comu_data_ac_button);
        modifyButton.setOnClickListener(new ComuDataAcButtonListener(Comunidad.class.cast(viewBean)));
    }

    // ==================================  HELPERS =================================

    @SuppressWarnings("WeakerAccess")
    class ComuDataAcButtonListener implements View.OnClickListener {

        private final Comunidad comunidadIn;

        public ComuDataAcButtonListener(Comunidad viewBean)
        {
            comunidadIn = viewBean;
        }

        @Override
        public void onClick(View v)
        {
            Timber.d("onClick()");
            StringBuilder errorBuilder = getErrorMsgBuilder(activity);
            Comunidad comunidadFromViewer = getChildViewer(ViewerRegComuFr.class).getComunidadFromViewer(errorBuilder);
            if (comunidadFromViewer == null) {
                makeToast(activity, errorBuilder.toString());
                return;
            }
            if (checkInternetConnected(activity)){
                controller.modifyComunidadData(new ComuDataAcObserver(),
                        new Comunidad.ComunidadBuilder()
                                .c_id(comunidadIn.getC_Id())
                                .copyComunidadNonNullValues(comunidadFromViewer)
                                .build()
                );
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    class ComuDataAcObserver extends DisposableSingleObserver<Integer> {

        @Override
        public void onSuccess(Integer rowsUpdated)
        {
            Timber.d("onSuccess()");
            assertTrue(rowsUpdated == 1, comuData_should_be_modified);
            getContextualRouter().getActionFromContextNm(comu_data_just_modified).initActivity(activity);
        }

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onError()");
            onErrorInObserver(e);
        }
    }
}
