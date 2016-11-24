package com.didekinaar.usuariocomunidad;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.didekin.comunidad.Comunidad;
import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekinaar.R;
import com.didekinaar.comunidad.ComuBundleKey;
import com.didekinaar.comunidad.ComuDataAc;
import com.didekinaar.comunidad.ComuSearchAc;
import com.didekinaar.comunidad.ComunidadBean;
import com.didekinaar.exception.UiAarException;
import com.didekinaar.security.TokenHandler;
import com.didekinaar.usuario.UserMenu;
import com.didekinaar.utils.ConnectionUtils;

import java.util.Objects;

import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.didekin.usuario.UsuarioEndPoints.IS_USER_DELETED;
import static com.didekinaar.usuariocomunidad.AarUserComuService.AarUserComuServ;
import static com.didekinaar.usuariocomunidad.UserComuBundleKey.USERCOMU_LIST_OBJECT;
import static com.didekinaar.utils.UIutils.doToolBar;
import static com.didekinaar.utils.UIutils.getErrorMsgBuilder;
import static com.didekinaar.utils.UIutils.isRegisteredUser;
import static com.didekinaar.utils.UIutils.makeToast;
import static com.didekinaar.utils.UIutils.updateIsRegistered;

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
    public RegUserComuFr mRegUserComuFr;
    MenuItem mComuDataItem;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        // Preconditions.
        Objects.equals(isRegisteredUser(this), true);
        mOldUserComu = (UsuarioComunidad) getIntent().getSerializableExtra(USERCOMU_LIST_OBJECT.key);
        Objects.equals(mOldUserComu != null, true);

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
            makeToast(this, errorBuilder.toString(), com.didekinaar.R.color.deep_purple_100);
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            makeToast(this, R.string.no_internet_conn_toast);
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

        int resourceId = item.getItemId();

        if (resourceId == android.R.id.home) {
            UserMenu.doUpMenu(this);
            return true;
        } else if (resourceId == R.id.see_usercomu_by_comu_ac_mn) {
            Intent intent = new Intent();
            intent.putExtra(ComuBundleKey.COMUNIDAD_ID.key, mOldUserComu.getComunidad().getC_Id());
            this.setIntent(intent);
            UserMenu.SEE_USERCOMU_BY_COMU_AC.doMenuItem(this);
            return true;
        } else if (resourceId == R.id.comu_data_ac_mn) {
            Intent intent;
            intent = new Intent(this, ComuDataAc.class);
            intent.putExtra(ComuBundleKey.COMUNIDAD_ID.key, mOldUserComu.getComunidad().getC_Id());
            this.setIntent(intent);
            UserMenu.COMU_DATA_AC.doMenuItem(this);
            return true;
        /*} else if (resourceId == R.id.incid_see_open_by_comu_ac_mn) {
            IncidenciaMenu.INCID_SEE_BY_COMU_AC.doMenuItem(this);
            return true;
        } else if (resourceId == R.id.incid_reg_ac_mn) {
            IncidenciaMenu.INCID_REG_AC.doMenuItem(this);
            return true;*/   // TODO: conectar con DidekinApp para estas opciones de menú.
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    // TODO: to persist the task during restarts and properly cancel the task when the activity is destroyed. (Example in Shelves)
    class ComuDataMenuSetter extends AsyncTask<Void, Void, Boolean> {

        UiAarException uiException;

        @Override
        protected Boolean doInBackground(Void... aVoid)
        {
            Timber.d("doInBackground()");

            boolean isOldestUserComu = false;
            try {
                isOldestUserComu = AarUserComuServ.isOldestOrAdmonUserComu(mOldUserComu.getComunidad().getC_Id());
            } catch (UiAarException e) {
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

        UiAarException uiException;

        @Override
        protected Integer doInBackground(UsuarioComunidad... userComus)
        {
            Timber.d("doInBackground()");

            int modifyUserComu = 0;
            try {
                modifyUserComu = AarUserComuServ.modifyUserComu(userComus[0]);
            } catch (UiAarException e) {
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
                Objects.equals(rowsUpdated == 1, true);
                Intent intent = new Intent(UserComuDataAc.this, SeeUserComuByUserAc.class);
                startActivity(intent);
            }
        }
    }

    class UserComuEraser extends AsyncTask<Comunidad, Void, Integer> {

        UiAarException uiException;

        @Override
        protected Integer doInBackground(Comunidad... comunidades)
        {
            Timber.d("doInBackground()");

            int deleteUserComu = 0;
            try {
                deleteUserComu = AarUserComuServ.deleteUserComu(comunidades[0].getC_Id());
            } catch (UiAarException e) {
                uiException = e;
            }
            return deleteUserComu;
        }

        @Override
        protected void onPostExecute(Integer isDeleted)
        {
            Timber.d("onPostExecute() entering.");

            if (uiException == null) {
                Objects.equals(isDeleted != 0, true);
                Intent intent;
                if (isDeleted == IS_USER_DELETED) {
                    TokenHandler.TKhandler.cleanTokenAndBackFile();
                    updateIsRegistered(false, UserComuDataAc.this);
                    intent = new Intent(UserComuDataAc.this, ComuSearchAc.class);
                    intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_NEW_TASK);
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

