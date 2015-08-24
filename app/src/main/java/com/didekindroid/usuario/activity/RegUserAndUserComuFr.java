package com.didekindroid.usuario.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.didekindroid.R;
import com.didekindroid.common.ui.CommonPatterns;
import com.didekindroid.usuario.beanfiller.UserAndComuFiller;
import com.didekindroid.usuario.comunidad.dominio.ComunidadBean;
import com.didekindroid.usuario.comunidad.dominio.UsuarioComunidadBean;

/**
 * User: pedro@didekin
 * Date: 11/05/15
 * Time: 16:00
 */
public class RegUserAndUserComuFr extends Fragment {

    public static final String TAG = RegUserAndUserComuFr.class.getCanonicalName();
    private View mRegUsuarioComunidadRegView;
    private Button mRegistroButton;
    private ComunidadBean mComunidadBean;
    private TextView mComunidadNombreText;

    private int IS_VISIBLE = View.INVISIBLE;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView().");

        mRegUsuarioComunidadRegView = inflater.inflate(R.layout.reg_usuario_and_usuariocomunidad_fr, container, false);
        mRegUsuarioComunidadRegView.findViewById(R.id.reg_usuariocomunidad_usuario_form_root).setVisibility(IS_VISIBLE);
        mComunidadNombreText = (TextView) mRegUsuarioComunidadRegView.findViewById(R.id.descripcion_comunidad_text);

        // En construcción de la vista en modo two-panes no hay bean, porque no pasa por
        // ComuSearchResultsAc.updateComunidadItem(..).
        if (mComunidadBean != null) {
            mComunidadNombreText.setText(mComunidadBean.getComunidad().getNombreComunidad());
        }

        mRegistroButton = (Button) mRegUsuarioComunidadRegView.findViewById(R.id.reg_usuario_usuariocomunidad_fr_button);
        mRegistroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "onClick().");
                registroNuevoUsuarioComunidad(mComunidadBean);
            }
        });
        return mRegUsuarioComunidadRegView;
    }

    public void updateComunidadItem(final ComunidadBean comunidadBean)
    {
        Log.d(TAG, "updateComunidadItem()");
        mComunidadBean = comunidadBean;
        // En two-panes modo, la vista ya está construida cuando se ejecuta este método.
        if (mComunidadNombreText != null) {
           mComunidadNombreText.setText(mComunidadBean.getComunidad().getNombreComunidad());
        }
        IS_VISIBLE = View.VISIBLE;
    }

    private void registroNuevoUsuarioComunidad(final ComunidadBean comunidadBean)
    {
        Log.d(TAG, "Enters registroNuevoUsuarioComunidad()");

        UsuarioComunidadBean usuarioComunidadBean = UserAndComuFiller.makeUsuarioComunidadBeanFromView
                (mRegUsuarioComunidadRegView, comunidadBean, null);

        // Validation of data.
        StringBuilder errorMsg = new StringBuilder(getResources().getText(R.string.error_validation_msg))
                .append(CommonPatterns.LINE_BREAK.literal);

        if (!usuarioComunidadBean.validate(getResources(), errorMsg)) {
            Toast clickToast = new Toast(getActivity()).makeText(getActivity(), errorMsg, Toast.LENGTH_LONG);
            clickToast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
            clickToast.show();

        } else {
            /* 1. registro asíncronamente en servidor
               2. si ok registro, inserto comunidad en base datos local (sigo asincrono). Registro también los datos
               de identificación (token). Servicio de actualización de tokens.
               3. si ok, llamo nueva actividad (consulta de mis comunidades).
            */
            // TODO: hacerlo asíncrono.
            /*ServiceOneEndPoints didekinSpringServiceOne = ServiceOne.getService
                    (ServiceOneEndPoints.class, ConnectionUtils.getHostAndPort(getActivity()));
            UsuarioComunidadBean usuarioComunidadBeanDb = didekinSpringServiceOne.signUp(usuarioComunidadBean.getUsuarioComunidad());

            Intent intent = new Intent(getActivity(), ComusByUserListAc.class);
            startActivity(intent);*/
        }
    }
}