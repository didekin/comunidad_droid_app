package com.didekinaar.comunidad;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;

import com.didekin.comunidad.Comunidad;
import com.didekinaar.R;
import com.didekinaar.exception.UiAarException;
import com.didekinaar.usuario.UserMenu;
import com.didekinaar.usuariocomunidad.SeeUserComuByUserAc;
import com.didekinaar.usuariocomunidad.UserAndComuFiller;
import com.didekinaar.utils.ConnectionUtils;

import java.util.Objects;

import timber.log.Timber;

import static com.didekinaar.comunidad.AarComunidadService.AarComuServ;
import static com.didekinaar.usuariocomunidad.AarUserComuService.AarUserComuServ;
import static com.didekinaar.utils.UIutils.doToolBar;
import static com.didekinaar.utils.UIutils.getErrorMsgBuilder;
import static com.didekinaar.utils.UIutils.isRegisteredUser;
import static com.didekinaar.utils.UIutils.makeToast;

/**
 * Preconditions:
 * 1. Registered user.
 * 2. Oldest user in the comunidad (to be changed in the future).
 * 3. An intent with a comunidad id key.
 * Postconditions:
 * 1.
 */
public class ComuDataAc extends AppCompatActivity implements RegComuFr.ComuDataControllerIf {

    long mIdComunidad;
    View mAcView;
    Button mModifyButton;
    RegComuFr mRegComuFrg;
    Comunidad mComunidad;

    private boolean isTipoViaSpinnerSet;
    private boolean isCASpinnerSet;
    private boolean isProvinciaSpinnerSet;
    private boolean isMunicipioSpinnerSet;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        // Preconditions.
        Objects.equals(isRegisteredUser(this), true);
        mIdComunidad = getIntent().getLongExtra(ComuBundleKey.COMUNIDAD_ID.key, 0L);
        Objects.equals(mIdComunidad > 0L, true);

        // Asunción: esta tarea termina antes que la carga de los spinners en RegComuFr.
        // Si hay problemas: meter C_Autonoma en la query que obtiene los objetos Comunidad que se usan para el intent inicial de esta actividad.
        new ComuDataSetter().execute();

        mAcView = getLayoutInflater().inflate(R.layout.comu_data_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);
        mRegComuFrg = (RegComuFr) getFragmentManager().findFragmentById(R.id.reg_comunidad_frg);
        mRegComuFrg.setmComuDataController(this);

        mModifyButton = (Button) findViewById(R.id.comu_data_ac_button);
        mModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("mModifyButton.OnClickListener().onClick()");
                modifyComuData();
            }
        });
    }

    void modifyComuData()
    {
        Timber.d("modifyComuData()");

        ComunidadBean comuBean = mRegComuFrg.getComunidadBean();
        UserAndComuFiller.makeComunidadBeanFromView(mRegComuFrg.getFragmentView(), comuBean);
        StringBuilder errorBuilder = getErrorMsgBuilder(this);

        if (!comuBean.validate(getResources(), errorBuilder)) {
            makeToast(this, errorBuilder.toString(), com.didekinaar.R.color.deep_purple_100);
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            makeToast(this, R.string.no_internet_conn_toast);
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

    void setEditTextComuData()
    {
        Timber.d("setEditTextComuData()");

        View comuFrView = mRegComuFrg.getFragmentView();

        ((EditText) comuFrView.findViewById(R.id.comunidad_nombre_via_editT)).setText(mComunidad.getNombreVia());
        ((EditText) comuFrView.findViewById(R.id.comunidad_numero_editT)).setText(String.valueOf(mComunidad.getNumero()));
        ((EditText) comuFrView.findViewById(R.id.comunidad_sufijo_numero_editT)).setText(mComunidad.getSufijoNumero());
    }

    @Override
    public void onTipoViaSpinnerLoaded()
    {
        Timber.d("onTipoViaSpinnerLoaded()");

        if (!isTipoViaSpinnerSet) {
            Timber.d("onTipoViaSpinnerLoaded(): spinner not set");
            int position = 0;
            Cursor cursor;
            for (int i = 0; i < mRegComuFrg.mTipoViaSpinner.getCount(); i++) {
                cursor = (Cursor) mRegComuFrg.mTipoViaSpinner.getItemAtPosition(i);
                if (cursor.getString(1).equals(mComunidad.getTipoVia())) {
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
        Timber.d("onCAutonomaSpinnerLoaded()");

        if (!isCASpinnerSet) {
            Timber.d("onCAutonomaSpinnerLoaded(): spinner not set");
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
        Timber.d("onProvinciaSpinnerLoaded()");

        if (!isProvinciaSpinnerSet) {
            Timber.d("onProvinciaSpinnerLoaded(): spinner not set");
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
        Timber.d("onMunicipioSpinnerLoaded()");

        if (!isMunicipioSpinnerSet) {
            Timber.d("onMunicipioSpinnerLoaded(): spinner not set");
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
        Timber.d("onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.comu_data_ac_mn, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");

        int resourceId = item.getItemId();

        if (resourceId == android.R.id.home) {
            UserMenu.doUpMenu(this);
            return true;
        } else if (resourceId == R.id.see_usercomu_by_comu_ac_mn) {
            Intent intent = new Intent();
            intent.putExtra(ComuBundleKey.COMUNIDAD_ID.key, mIdComunidad);
            this.setIntent(intent);
            UserMenu.SEE_USERCOMU_BY_COMU_AC.doMenuItem(this);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    // TODO: to persist the task during restarts and properly cancel the task when the activity is destroyed. (Example in Shelves)
    class ComuDataSetter extends AsyncTask<Void, Void, Comunidad> {

        UiAarException uiException;

        @Override
        protected Comunidad doInBackground(Void... aVoid)
        {
            Timber.d("doInBackground()");

            Comunidad comuData = null;
            try {
                comuData = AarComuServ.getComuData(mIdComunidad);
            } catch (UiAarException e) {
                uiException = e;
            }
            return comuData;
        }

        @Override
        protected void onPostExecute(Comunidad comunidad)
        {
            Timber.d("onPostExecute()");

            if (uiException != null) {
                uiException.processMe(ComuDataAc.this, new Intent());
            } else {
                mComunidad = comunidad;
                setEditTextComuData();
            }
        }
    }

    class ComuDataModifier extends AsyncTask<Comunidad, Void, Integer> {

        UiAarException uiException;

        @Override
        protected Integer doInBackground(Comunidad... comunidades)
        {
            Timber.d("doInBackground()");

            int modifyComuData = 0;
            try {
                modifyComuData = AarUserComuServ.modifyComuData(comunidades[0]);
            } catch (UiAarException e) {
                uiException = e;
            }
            return modifyComuData;
        }

        @Override
        protected void onPostExecute(Integer rowsUpdated)
        {
            Timber.d("onPostExecute()");
            if (uiException != null) {
                uiException.processMe(ComuDataAc.this, new Intent());
            } else {
                Objects.equals(rowsUpdated == 1, true);
            }
        }
    }
}
