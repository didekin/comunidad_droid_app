package com.didekindroid.usuario.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.didekin.usuario.dominio.Comunidad;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.TokenHandler;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.common.utils.ConnectionUtils;
import com.didekindroid.usuario.activity.utils.UserAndComuFiller;
import com.didekindroid.usuario.dominio.ComunidadBean;
import com.didekindroid.usuario.dominio.UsuarioComunidadBean;

import java.util.Objects;

import timber.log.Timber;

import static com.didekin.usuario.controller.UsuarioServiceConstant.IS_USER_DELETED;
import static com.didekindroid.common.activity.BundleKey.COMUNIDAD_ID;
import static com.didekindroid.common.activity.BundleKey.USERCOMU_LIST_OBJECT;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.common.utils.UIutils.getErrorMsgBuilder;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.common.utils.UIutils.makeToast;
import static com.didekindroid.common.utils.UIutils.updateIsRegistered;
import static com.didekindroid.incidencia.activity.utils.IncidenciaMenu.INCID_REG_AC;
import static com.didekindroid.incidencia.activity.utils.IncidenciaMenu.INCID_SEE_BY_COMU_AC;
import static com.didekindroid.usuario.activity.utils.UserMenu.COMU_DATA_AC;
import static com.didekindroid.usuario.activity.utils.UserMenu.SEE_USERCOMU_BY_COMU_AC;
import static com.didekindroid.usuario.activity.utils.UserMenu.doUpMenu;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Preconditions:
 * 1. Registered user.
 * 2. An intent with a UsuarioComunidad key, with:
 * -- userComu: id, alias, userName.
 * -- comunidad: id, tipoVia, nombreVia, numero, sufijoNumero, fechaAlta,
 * ---- municipio: codInProvincia, nombre.
 * ------ provincia: provinciaId, nombre.
 * -- usuarioComunidad: portal, escalera, planta, puerta, roles.
 * Postconditions:
 * 1a. Registered user with modified data in a comunidad: once done, it goes to SeeUserComuByUserAc.
 * 1b. Registered user with data deleted in the comunidad: once done, it goes to SeeUserComuByUserAc.
 * 1c. Unregistered user, once she has deleted the data of the one comunidad associated to her, it goes to
 * ComuSearchAc.
 */
@SuppressWarnings("ConstantConditions")
public class UserComuDataAc extends AppCompatActivity {

    private View mAcView;
    UsuarioComunidad mOldUserComu;
    RegUserComuFr mRegUserComuFr;
    MenuItem mComuDataItem;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        // Preconditions.
        checkState(isRegisteredUser(this));
        mOldUserComu = (UsuarioComunidad) getIntent().getSerializableExtra(USERCOMU_LIST_OBJECT.key);
        checkNotNull(mOldUserComu);

        mAcView = getLayoutInflater().inflate(R.layout.usercomu_data_ac_layout, null);
        setContentView(mAcView);
        doToolBar(this, true);
        mRegUserComuFr = (RegUserComuFr) getFragmentManager().findFragmentById(R.id.reg_usercomu_frg);
        mRegUserComuFr.paintUserComuView(mOldUserComu);

        Button mModifyButton = (Button) findViewById(R.id.usercomu_data_ac_modif_button);
        mModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("mModifyButton.OnClickListener().onClick()");
                modifyUserComuData();
            }
        });

        Button mDeleteButton = (Button) findViewById(R.id.usercomu_data_ac_delete_button);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("mDeleteButton.OnClickListener().onClick()");
                deleteUserComuData();
            }
        });
    }

    void modifyUserComuData()
    {
        Timber.d("modifyUserComuData()");

        // ComunidaBean initialized only with comunidadId. UsuarioBean is not initialized.
        UsuarioComunidadBean userComuBean = UserAndComuFiller.makeUserComuBeanFromView(mAcView,
                new ComunidadBean(mOldUserComu.getComunidad().getC_Id(), null, null, null, null, null),
                null);
        StringBuilder errorBuilder = getErrorMsgBuilder(this);

        if (!userComuBean.validate(getResources(), errorBuilder)) {
            makeToast(this, errorBuilder.toString(), Toast.LENGTH_SHORT);
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            makeToast(this, R.string.no_internet_conn_toast, Toast.LENGTH_LONG);
        } else {
            UsuarioComunidad newUserComu = userComuBean.getUsuarioComunidad();
            new UserComuModifyer().execute(newUserComu);
        }
    }

    void deleteUserComuData()
    {
        Timber.d("deleteUserComuData()");
        new UserComuEraser().execute(mOldUserComu.getComunidad());
    }


    // ============================================================
    //    ..... ACTION BAR ....
    // ============================================================

    /**
     * Option 'comu_data_ac_mn' is only visible if the user is the oldest (oldest fecha_alta) UsuarioComunidad in
     * this comunidad, or has the roles adm or pre.
     * <p/>
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Timber.d("onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.usercomu_data_ac_mn, menu);
        mComuDataItem = menu.findItem(R.id.comu_data_ac_mn);
        // Is the oldest or admon userComu?
        new ComuDataMenuSetter().execute();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");

        int resourceId = checkNotNull(item.getItemId());

        switch (resourceId) {
            case android.R.id.home:
                doUpMenu(this);
                return true;
            case R.id.see_usercomu_by_comu_ac_mn:
                Intent intent = new Intent();
                intent.putExtra(COMUNIDAD_ID.key, mOldUserComu.getComunidad().getC_Id());
                this.setIntent(intent);
                SEE_USERCOMU_BY_COMU_AC.doMenuItem(this);
                return true;
            case R.id.comu_data_ac_mn:
                intent = new Intent(this, ComuDataAc.class);
                intent.putExtra(COMUNIDAD_ID.key, mOldUserComu.getComunidad().getC_Id());
                this.setIntent(intent);
                COMU_DATA_AC.doMenuItem(this);
                return true;
            case R.id.incid_see_open_by_comu_ac_mn:
                INCID_SEE_BY_COMU_AC.doMenuItem(this);
                return true;
            case R.id.incid_reg_ac_mn:
                INCID_REG_AC.doMenuItem(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    // TODO: to persist the task during restarts and properly cancel the task when the activity is destroyed. (Example in Shelves)
    class ComuDataMenuSetter extends AsyncTask<Void, Void, Boolean> {

        UiException uiException;

        @Override
        protected Boolean doInBackground(Void... aVoid)
        {
            Timber.d("doInBackground()");

            boolean isOldestUserComu = false;
            try {
                isOldestUserComu = ServOne.isOldestOrAdmonUserComu(mOldUserComu.getComunidad().getC_Id());
            } catch (UiException e) {
                uiException = e;
            }
            return isOldestUserComu;
        }

        @Override
        protected void onPostExecute(Boolean isOldestUserComu)
        {
            Timber.d("onPostExecute()");

            if (uiException != null) {
                uiException.processMe(UserComuDataAc.this, new Intent());
            } else {
                mComuDataItem.setVisible(isOldestUserComu);
                mComuDataItem.setEnabled(isOldestUserComu);
            }
        }
    }

    class UserComuModifyer extends AsyncTask<UsuarioComunidad, Void, Integer> {

        UiException uiException;

        @Override
        protected Integer doInBackground(UsuarioComunidad... userComus)
        {
            Timber.d("doInBackground()");

            int modifyUserComu = 0;
            try {
                modifyUserComu = ServOne.modifyUserComu(userComus[0]);
            } catch (UiException e) {
                uiException = e;
            }
            return modifyUserComu;
        }

        @Override
        protected void onPostExecute(Integer rowsUpdated)
        {
            Timber.d("onPostExecute()");
            if (uiException != null) {
                uiException.processMe(UserComuDataAc.this, new Intent());
            } else {
                checkState(rowsUpdated == 1);
                Intent intent = new Intent(UserComuDataAc.this, SeeUserComuByUserAc.class);
                startActivity(intent);
            }
        }
    }

    class UserComuEraser extends AsyncTask<Comunidad, Void, Integer> {

        UiException uiException;

        @Override
        protected Integer doInBackground(Comunidad... comunidades)
        {
            Timber.d("doInBackground()");

            int deleteUserComu = 0;
            try {
                deleteUserComu = ServOne.deleteUserComu(comunidades[0].getC_Id());
            } catch (UiException e) {
                uiException = e;
            }
            return deleteUserComu;
        }

        @Override
        protected void onPostExecute(Integer isDeleted)
        {
            Timber.d("onPostExecute() entering.");

            if (uiException == null) {
                checkState(isDeleted != 0);
                Intent intent;
                if (isDeleted == IS_USER_DELETED) {
                    TokenHandler.TKhandler.cleanCacheAndBckFile();
                    updateIsRegistered(false, UserComuDataAc.this);
                    intent = new Intent(UserComuDataAc.this, ComuSearchAc.class);
                } else {
                    Objects.equals(isDeleted == 1, true);
                    intent = new Intent(UserComuDataAc.this, SeeUserComuByUserAc.class);
                }
                startActivity(intent);
            } else {
                uiException.processMe(UserComuDataAc.this, new Intent());
            }
        }
    }
}

