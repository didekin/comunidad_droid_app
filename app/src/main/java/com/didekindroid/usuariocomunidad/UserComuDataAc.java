package com.didekindroid.usuariocomunidad;

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
import com.didekinaar.exception.UiException;
import com.didekinaar.utils.ConnectionUtils;
import com.didekindroid.R;
import com.didekindroid.comunidad.ComuBundleKey;
import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.comunidad.ComunidadBean;

import java.util.Objects;

import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.didekin.usuario.UsuarioEndPoints.IS_USER_DELETED;
import static com.didekinaar.security.TokenIdentityCacher.TKhandler;
import static com.didekinaar.utils.AarItemMenu.mn_handler;
import static com.didekinaar.utils.UIutils.checkPostExecute;
import static com.didekinaar.utils.UIutils.doToolBar;
import static com.didekinaar.utils.UIutils.getErrorMsgBuilder;
import static com.didekinaar.utils.UIutils.makeToast;
import static com.didekindroid.usuariocomunidad.UserComuService.AppUserComuServ;
import static com.didekindroid.util.AppMenuRouter.doUpMenu;
import static com.didekindroid.util.AppMenuRouter.routerMap;

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
        Objects.equals(TKhandler.isRegisteredUser(), true);
        mOldUserComu = (UsuarioComunidad) getIntent().getSerializableExtra(UserComuBundleKey.USERCOMU_LIST_OBJECT.key);
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
        UsuarioComunidadBean userComuBean = RegUserComuFr.makeUserComuBeanFromView(mAcView,
                new ComunidadBean(mOldUserComu.getComunidad().getC_Id(), null, null, null, null, null),
                null);
        StringBuilder errorBuilder = getErrorMsgBuilder(this);

        if (!userComuBean.validate(getResources(), errorBuilder)) {
            makeToast(this, errorBuilder.toString(), R.color.deep_purple_100);
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
        switch (resourceId){
            case android.R.id.home:
                doUpMenu(this);
                return true;
            case R.id.see_usercomu_by_comu_ac_mn:
            case R.id.comu_data_ac_mn:
                Intent intent = new Intent();
                intent.putExtra(ComuBundleKey.COMUNIDAD_ID.key, mOldUserComu.getComunidad().getC_Id());
                this.setIntent(intent);
                mn_handler.doMenuItem(this, routerMap.get(resourceId));
                return true;
            case R.id.incid_see_open_by_comu_ac_mn:
            case R.id.incid_reg_ac_mn:
                mn_handler.doMenuItem(this, routerMap.get(resourceId));
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
                isOldestUserComu = AppUserComuServ.isOldestOrAdmonUserComu(mOldUserComu.getComunidad().getC_Id());
            } catch (UiException e) {
                uiException = e;
            }
            return isOldestUserComu;
        }

        @Override
        protected void onPostExecute(Boolean isOldestUserComu)
        {
            if (checkPostExecute(UserComuDataAc.this)) return;

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
                modifyUserComu = AppUserComuServ.modifyUserComu(userComus[0]);
            } catch (UiException e) {
                uiException = e;
            }
            return modifyUserComu;
        }

        @Override
        protected void onPostExecute(Integer rowsUpdated)
        {
            if (checkPostExecute(UserComuDataAc.this)) return;

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

        UiException uiException;

        @Override
        protected Integer doInBackground(Comunidad... comunidades)
        {
            Timber.d("doInBackground()");

            int deleteUserComu = 0;
            try {
                deleteUserComu = AppUserComuServ.deleteUserComu(comunidades[0].getC_Id());
            } catch (UiException e) {
                uiException = e;
            }
            return deleteUserComu;
        }

        @Override
        protected void onPostExecute(Integer isDeleted)
        {
            if (checkPostExecute(UserComuDataAc.this)) return;

            Timber.d("onPostExecute() entering.");

            if (uiException == null) {
                Objects.equals(isDeleted != 0, true);
                Intent intent;
                if (isDeleted == IS_USER_DELETED) {
                    TKhandler.cleanIdentityCache();
                    TKhandler.updateIsRegistered(false);
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

