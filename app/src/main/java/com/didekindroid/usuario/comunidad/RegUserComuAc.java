package com.didekindroid.usuario.comunidad;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import com.didekindroid.R;
import com.didekindroid.usuario.common.UserIntentExtras;
import com.didekindroid.usuario.comunidad.dominio.ComunidadBean;

/**
 * User: pedro@didekin
 * Date: 11/05/15
 * Time: 19:13
 */
public class RegUserComuAc extends Activity {

    public static final String TAG = RegUserComuAc.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate()");

        // If we are in two-pane layout mode, this activity is no longer necessary.
        if (getResources().getBoolean((R.bool.has_two_panes))) {
            finish();
            return;
        }

        ComunidadBean comunidadBean = (ComunidadBean) getIntent().getExtras()
                        .getSerializable(UserIntentExtras.COMUNIDAD_INDEX_LIST.name());

        RegUserAndUserComuFr regUserAndUserComuFr = new RegUserAndUserComuFr();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(android.R.id.content, regUserAndUserComuFr);
        transaction.addToBackStack(null);
        transaction.commit();

        regUserAndUserComuFr.updateComunidadItem(comunidadBean);
    }
}

/*private void componeNombreComunidad()
    {
        StringBuilder builder = new StringBuilder(mComunidadBean.getNombreVia());
        if (mComunidadBean.getNumeroEnVia() != null){
            builder.append(" ").append(String.valueOf(mComunidadBean.getNumeroEnVia()));
        }
        if (mComunidadBean.getSufijoNumero() != null){
            builder.append(" ").append(mComunidadBean.getSufijoNumero());
        }
    }
*/