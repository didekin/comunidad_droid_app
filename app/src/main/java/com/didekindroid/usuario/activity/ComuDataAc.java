package com.didekindroid.usuario.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.didekin.serviceone.domain.Comunidad;
import com.didekindroid.R;

import static com.didekindroid.uiutils.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.COMUNIDAD_ID;
import static com.didekindroid.usuario.activity.utils.UserMenu.SEE_USERCOMU_BY_COMU_AC;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Preconditions:
 * 1. Registered user.
 * 2. Oldest user in the comunidad (to be changed in the future).
 * 3. An intent with a comunidad id extra.
 * Postconditions:
 * 1.
 */
public class ComuDataAc extends Activity {

    private static final String TAG = ComuDataAc.class.getCanonicalName();

    private long mIdComunidad;
    private View mAcView;
    private Button mModifyButton;
    RegComuFr mRegComuFrg;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        // Preconditions.
        checkState(isRegisteredUser(this));
        mIdComunidad = getIntent().getLongExtra(COMUNIDAD_ID.extra, 0L);
        checkNotNull(mIdComunidad);

        mAcView = getLayoutInflater().inflate(R.layout.comu_data_ac, null);
        setContentView(mAcView);
        mRegComuFrg = (RegComuFr) getFragmentManager().findFragmentById(R.id.reg_comunidad_frg);
        mModifyButton = (Button) findViewById(R.id.comu_data_ac_button);

        mModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "mModifyButton.OnClickListener().onClick()");
                modifyComuData();
            }
        });
    }

    private void modifyComuData()
    {
        Log.d(TAG, "modifyComuData()");
    }

//    =============================== Painting the default comunidad data ================================

    private void paintComuDataView(Comunidad comunidad)
    {
        Log.d(TAG, "paintComuDataView()");

        View comuFrView = mRegComuFrg.getFragmentView();

        ((EditText) comuFrView.findViewById(R.id.comunidad_nombre_via_editT)).setText(comunidad.getNombreVia());
        ((EditText) comuFrView.findViewById(R.id.comunidad_numero_editT)).setText(comunidad.getNumero());
        ((EditText) comuFrView.findViewById(R.id.comunidad_sufijo_numero_editT)).setText(comunidad.getSufijoNumero());

        // TipoVia spinner.
        setTipoViaSpinner(comunidad);
        // ComunidadAutonoma spinner.
        setCASpinner(comunidad);
        // Provincia spinner.
        setProvinciaSpinner(comunidad);
        //Municipio spinner.
        setMunicipioSpinner(comunidad);

        mRegComuFrg.new SpinnerProvinciasLoader().execute(comunidad.getMunicipio().getProvincia().getProvinciaId());
    }

    /**
     * 1. It fixes the current tipoVia as the default value of the spinner.
     * 2. It sets the current tipoVia in the comunidadBean associated to the comunidad fragment.
     * 3. It assign the position of the current tipoVia to the mRegComuFrg.mTipoViaPointer.
     *
     * @param comunidad
     */
    private void setTipoViaSpinner(Comunidad comunidad)
    {
        Log.d(TAG, "setTipoViaSpinner()");

        int position = 0;
        for (int i = 0; i < mRegComuFrg.mTipoViaSpinner.getCount(); i++) {
            if (mRegComuFrg.mTipoViaSpinner.getItemAtPosition(i).equals(comunidad.getTipoVia())) {
                position = i;
            }
        }
        mRegComuFrg.mTipoViaPointer = position;
        mRegComuFrg.mTipoViaSpinner.setSelection(mRegComuFrg.mTipoViaPointer);
        mRegComuFrg.comunidadBean.setTipoVia(
                (String) mRegComuFrg.mTipoViaSpinner.getItemAtPosition(mRegComuFrg.mTipoViaPointer));
    }

    /**
     * */
    private void setCASpinner(Comunidad comunidad)
    {
        Log.d(TAG,"setCASpinner()");
            /*R.id.autonoma_comunidad_spinner)*/

    }

    private void setProvinciaSpinner(Comunidad comunidad)
    {
        Log.d(TAG,"setProvinciaSpinner()");
            /*R.id.provincia_spinner*/
    }

    private void setMunicipioSpinner(Comunidad comunidad)
    {
        Log.d(TAG,"setMunicipioSpinner()");
           /*R.id.municipio_spinner*/

    }

//    ============================================================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.d(TAG, "onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.comu_data_ac_mn, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.d(TAG, "onOptionsItemSelected()");

        int resourceId = checkNotNull(item.getItemId());

        switch (resourceId) {
            case R.id.see_usercomu_by_comu_ac_mn:
                Intent intent = new Intent(this, SeeUserComuByComuAc.class);
                intent.putExtra(COMUNIDAD_ID.extra, mIdComunidad);
                this.setIntent(intent);
                SEE_USERCOMU_BY_COMU_AC.doMenuItem(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    private class ComuDataSetter extends AsyncTask<Void, Void, Comunidad> {

        final String TAG = ComuDataSetter.class.getCanonicalName();

        @Override
        protected Comunidad doInBackground(Void... aVoid)
        {
            Log.d(TAG, "doInBackground()");
            return ServOne.getComuData(mIdComunidad);
        }

        @Override
        protected void onPostExecute(Comunidad comunidad)
        {
            Log.d(TAG, "onPostExecute()");
            paintComuDataView(comunidad);
        }
    }
}
