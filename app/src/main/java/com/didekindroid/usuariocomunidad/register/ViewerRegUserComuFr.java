package com.didekindroid.usuariocomunidad.register;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.Controller;
import com.didekindroid.lib_one.api.ParentViewerIf;
import com.didekindroid.lib_one.api.Viewer;
import com.didekindroid.lib_one.security.CtrlerAuthToken;
import com.didekindroid.usuariocomunidad.RolUi;
import com.didekindroid.usuariocomunidad.UsuarioComunidadBean;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuario.Usuario;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.lib_one.util.CommonAssertionMsg.user_should_be_registered;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;

/**
 * User: pedro@didekin
 * Date: 23/05/17
 * Time: 09:56
 */
public final class ViewerRegUserComuFr extends Viewer<View, Controller> {

    private ViewerRegUserComuFr(View view, AppCompatActivity activity, @NonNull ParentViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
    }

    static ViewerRegUserComuFr newViewerRegUserComuFr(@NonNull View view, @NonNull ParentViewerIf parentViewer)
    {
        Timber.d("newViewerRegUserComuFr()");
        ViewerRegUserComuFr instance = new ViewerRegUserComuFr(view, parentViewer.getActivity(), parentViewer);
        instance.setController(new CtrlerAuthToken());
        return instance;
    }

    // ==================================== ViewerIf ===================================

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        if (viewBean != null) {
            assertTrue(controller.isRegisteredUser(), user_should_be_registered);
            UsuarioComunidad initUserComu = UsuarioComunidad.class.cast(viewBean);
            paintUserComuView(initUserComu);
        }
    }

    // ==================================== Helpers ====================================

    public UsuarioComunidad getUserComuFromViewer(StringBuilder errorMessages
            , Comunidad comunidad, Usuario usuario)
    {
        UsuarioComunidadBean userComuBean = new UsuarioComunidadBean(
                comunidad,
                usuario,
                ((TextView) view.findViewById(R.id.reg_usercomu_portal_ed)).getText()
                        .toString(),
                ((TextView) view.findViewById(R.id.reg_usercomu_escalera_ed)).getText()
                        .toString(),
                ((TextView) view.findViewById(R.id.reg_usercomu_planta_ed)).getText()
                        .toString(),
                ((TextView) view.findViewById(R.id.reg_usercomu_puerta_ed)).getText()
                        .toString(),
                ((CheckBox) view.findViewById(R.id.reg_usercomu_checbox_pre))
                        .isChecked(),
                ((CheckBox) view.findViewById(R.id.reg_usercomu_checbox_admin))
                        .isChecked(),
                ((CheckBox) view.findViewById(R.id.reg_usercomu_checbox_pro))
                        .isChecked(),
                ((CheckBox) view.findViewById(R.id.reg_usercomu_checbox_inq))
                        .isChecked()
        );

        if (userComuBean.validate(activity.getResources(), errorMessages)) {
            return userComuBean.getUsuarioComunidad();
        }
        return null;
    }

    void paintUserComuView(UsuarioComunidad initUserComu)
    {
        Timber.d("paintUserComuView()");

        ((EditText) view.findViewById(R.id.reg_usercomu_portal_ed)).setText(initUserComu.getPortal());
        ((EditText) view.findViewById(R.id.reg_usercomu_escalera_ed)).setText(initUserComu.getEscalera());
        ((EditText) view.findViewById(R.id.reg_usercomu_planta_ed)).setText(initUserComu.getPlanta());
        ((EditText) view.findViewById(R.id.reg_usercomu_puerta_ed)).setText(initUserComu.getPuerta());

        ((CheckBox) view.findViewById(R.id.reg_usercomu_checbox_pre))
                .setChecked(initUserComu.getRoles().contains(RolUi.PRE.function));
        ((CheckBox) view.findViewById(R.id.reg_usercomu_checbox_admin))
                .setChecked(initUserComu.getRoles().contains(RolUi.ADM.function));
        ((CheckBox) view.findViewById(R.id.reg_usercomu_checbox_pro))
                .setChecked(initUserComu.getRoles().contains(RolUi.PRO.function));
        ((CheckBox) view.findViewById(R.id.reg_usercomu_checbox_inq))
                .setChecked(initUserComu.getRoles().contains(RolUi.INQ.function));
    }
}
