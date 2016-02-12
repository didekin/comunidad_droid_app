package com.didekindroid.usuario.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.didekin.usuario.dominio.Comunidad;
import com.didekindroid.R;
import com.didekindroid.common.UiException;
import com.didekindroid.usuario.dominio.ComunidadBean;
import com.didekindroid.common.utils.ConnectionUtils;

import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeComunidadBeanFromView;
import static com.didekindroid.common.utils.AppKeysForBundle.COMUNIDAD_ID;
import static com.didekindroid.usuario.activity.utils.UserMenu.SEE_USERCOMU_BY_COMU_AC;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static com.didekindroid.common.utils.UIutils.getErrorMsgBuilder;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.common.utils.UIutils.makeToast;
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
public class ComuDataAc extends Activity implements RegComuFr.RegComuFrListener {

    private static final String TAG = ComuDataAc.class.getCanonicalName();

    long mIdComunidad;
    View mAcView;
    Button mModifyButton;
    RegComuFr mRegComuFrg;
    private Comunidad mComunidad;

    private boolean isTipoViaSpinnerSet;
    private boolean isCASpinnerSet;
    private boolean isProvinciaSpinnerSet;
    private boolean isMunicipioSpinnerSet;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        // Preconditions.
        checkState(isRegisteredUser(this));
        mIdComunidad = getIntent().getLongExtra(COMUNIDAD_ID.extra, 0L);
        checkState(mIdComunidad > 0L);

        mAcView = getLayoutInflater().inflate(R.layout.comu_data_ac, null);
        setContentView(mAcView);
        mRegComuFrg = (RegComuFr) getFragmentManager().findFragmentById(R.id.reg_comunidad_frg);
        mRegComuFrg.setmActivityListener(this);

        // Asunción: esta tarea termina antes que la carga de los spinners en RegComuFr.
        // TODO: cambiar el intent; meter el objeto comunidad e inicializar mComunidad con él.
       new ComuDataSetter().execute();

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

        ComunidadBean comuBean = mRegComuFrg.getComunidadBean();
        makeComunidadBeanFromView(mRegComuFrg.getFragmentView(), comuBean);
        StringBuilder errorBuilder = getErrorMsgBuilder(this);

        if (!comuBean.validate(getResources(), errorBuilder)) {
            makeToast(this, errorBuilder.toString(), Toast.LENGTH_SHORT);
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            makeToast(this, R.string.no_internet_conn_toast, Toast.LENGTH_LONG);
        } else {
            Comunidad comunidadIn = new Comunidad.ComunidadBuilder()
                    .c_id(mComunidad.getC_Id())
                    .tipoVia(comuBean.getComunidad().getTipoVia())
                    .nombreVia(comuBean.getComunidad().getNombreVia())
                    .numero(comuBean.getComunidad().getNumero())
                    .sufijoNumero(comuBean.getComunidad().getSufijoNumero())
                    .municipio(comuBean.getComunidad().getMunicipio())
                    .build();
            new ComuDataModifier().execute(comunidadIn);
            Intent intent = new Intent(this, SeeUserComuByUserAc.class);
            startActivity(intent);
        }
    }

//  ================================= Painting the default comunidad data ================================

    private void setEditTextComuData()
    {
        Log.d(TAG, "setEditTextComuData()");

        View comuFrView = mRegComuFrg.getFragmentView();

        ((EditText) comuFrView.findViewById(R.id.comunidad_nombre_via_editT)).setText(mComunidad.getNombreVia());
        ((EditText) comuFrView.findViewById(R.id.comunidad_numero_editT)).setText(String.valueOf(mComunidad.getNumero()));
        ((EditText) comuFrView.findViewById(R.id.comunidad_sufijo_numero_editT)).setText(mComunidad.getSufijoNumero());
    }

    @Override
    public void onTipoViaSpinnerLoaded()
    {
        Log.d(TAG, "onTipoViaSpinnerLoaded()");

        if (!isTipoViaSpinnerSet) {
            Log.d(TAG, "onTipoViaSpinnerLoaded(): spinner not set");
            int position = 0;
            for (int i = 0; i < mRegComuFrg.mTipoViaSpinner.getCount(); i++) {
                if (mRegComuFrg.mTipoViaSpinner.getItemAtPosition(i).equals(mComunidad.getTipoVia())) {
                    position = i;
                }
            }
            mRegComuFrg.mTipoViaSpinner.setSelection(position);
            isTipoViaSpinnerSet = true;
        }
    }

    @Override
    public void onCAutonomaSpinnerLoaded()
    {
        Log.d(TAG, "onCAutonomaSpinnerLoaded()");

        if (!isCASpinnerSet) {
            Log.d(TAG, "onCAutonomaSpinnerLoaded(): spinner not set");
            short cAutonomaId = mComunidad.getMunicipio().getProvincia().getComunidadAutonoma().getCuId();

            int position = 0;
            int itemsLength = mRegComuFrg.mAutonomaComuSpinner.getCount();
            for (int i = 0; i < itemsLength; i++) {
                if ((short) mRegComuFrg.mAutonomaComuSpinner.getItemIdAtPosition(i) == cAutonomaId) {
                    position = i;
                }
            }
            mRegComuFrg.mAutonomaComuSpinner.setSelection(position);
            isCASpinnerSet = true;
        }
    }

    @Override
    public void onProvinciaSpinnerLoaded()
    {
        Log.d(TAG, "onProvinciaSpinnerLoaded()");

        if (!isProvinciaSpinnerSet) {
            Log.d(TAG, "onProvinciaSpinnerLoaded(): spinner not set");
            short provinciaId = mComunidad.getMunicipio().getProvincia().getProvinciaId();
            int position = 0;
            int itemsLength = mRegComuFrg.provinciaSpinner.getCount();
            for (int i = 0; i < itemsLength; i++) {
                if ((short) mRegComuFrg.provinciaSpinner.getItemIdAtPosition(i) == provinciaId) {
                    position = i;
                }
            }
            mRegComuFrg.provinciaSpinner.setSelection(position);
            isProvinciaSpinnerSet = true;
        }
    }

    @Override
    public void onMunicipioSpinnerLoaded()
    {
        Log.d(TAG, "onMunicipioSpinnerLoaded()");

        if (!isMunicipioSpinnerSet) {
            Log.d(TAG, "onMunicipioSpinnerLoaded(): spinner not set");
            int municipioCP = mComunidad.getMunicipio().getCodInProvincia();
            short provinciaId = mComunidad.getMunicipio().getProvincia().getProvinciaId();
            int position = 0;
            Cursor cursor = ((CursorAdapter) mRegComuFrg.municipioSpinner.getAdapter()).getCursor();
            do {
                if (cursor.getShort(2) == municipioCP && cursor.getShort(1) == provinciaId) {
                    position = cursor.getPosition();
                    break;
                }
            } while (cursor.moveToNext());
            mRegComuFrg.municipioSpinner.setSelection(position);
            isMunicipioSpinnerSet = true;
        }
    }

    @Override
    public void onDestroyFragment()
    {
        isCASpinnerSet = false;
        isMunicipioSpinnerSet = false;
        isProvinciaSpinnerSet = false;
        isTipoViaSpinnerSet = false;
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
                Intent intent = new Intent();
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

    class ComuDataSetter extends AsyncTask<Void, Void, Comunidad> {

        final String TAG = ComuDataSetter.class.getCanonicalName();

        UiException uiException;

        @Override
        protected Comunidad doInBackground(Void... aVoid)
        {
            Log.d(TAG, "doInBackground()");

            Comunidad comuData = null;
            try {
                comuData = ServOne.getComuData(mIdComunidad);
            } catch (UiException e) {
                uiException = e;
            }
            return comuData;
        }

        @Override
        protected void onPostExecute(Comunidad comunidad)
        {
            Log.d(TAG, "onPostExecute()");

            if (uiException != null) {
                Log.d(TAG, "UiException" + (uiException.getInServiceException() != null ? uiException.getInServiceException().getHttpMessage() : UiException.TOKEN_NULL));
                uiException.getAction().doAction(ComuDataAc.this, uiException.getResourceId());
            } else {
                mComunidad = comunidad;
                setEditTextComuData();
            }
        }
    }

    class ComuDataModifier extends AsyncTask<Comunidad, Void, Integer> {

        final String TAG = ComuDataModifier.class.getCanonicalName();
        UiException uiException;

        @Override
        protected Integer doInBackground(Comunidad... comunidades)
        {
            Log.d(TAG, "doInBackground()");

            int modifyComuData = 0;
            try {
                modifyComuData = ServOne.modifyComuData(comunidades[0]);
            } catch (UiException e) {
                uiException = e;
            }
            return modifyComuData;
        }

        @Override
        protected void onPostExecute(Integer rowsUpdated)
        {
            Log.d(TAG, "onPostExecute()");
            if (uiException != null) {
                Log.d(TAG, "onPostExecute(): uiException " + (uiException.getInServiceException() != null ?
                        uiException.getInServiceException().getHttpMessage() : UiException.TOKEN_NULL));
                uiException.getAction().doAction(ComuDataAc.this, uiException.getResourceId());
            } else {
                checkState(rowsUpdated == 1);
            }
        }
    }
}
