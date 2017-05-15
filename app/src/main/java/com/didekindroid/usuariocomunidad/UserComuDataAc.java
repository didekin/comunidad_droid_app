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

import com.didekindroid.R;
import com.didekindroid.comunidad.utils.ComuBundleKey;
import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.comunidad.ComunidadBean;
import com.didekindroid.exception.UiException;
import com.didekindroid.router.ActivityInitiator;
import com.didekindroid.security.IdentityCacher;
import com.didekindroid.util.ConnectionUtils;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_be_registered;
import static com.didekindroid.usuariocomunidad.UserComuAssertionMsg.userComu_should_be_deleted;
import static com.didekindroid.usuariocomunidad.UserComuAssertionMsg.userComu_should_be_modified;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.router.ActivityRouter.doUpMenu;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.checkPostExecute;
import static com.didekindroid.util.UIutils.doToolBar;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.util.CommonAssertionMsg.intent_extra_should_be_initialized;
import static com.didekindroid.util.UIutils.makeToast;
import static com.didekinlib.http.UsuarioServConstant.IS_USER_DELETED;

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

    public RegUserComuFr mRegUserComuFr;
    UsuarioComunidad mOldUserComu;
    MenuItem mComuDataItem;
    IdentityCacher identityCacher;
    private View mAcView;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);
        identityCacher = TKhandler;

        // Preconditions.
        assertTrue(identityCacher.isRegisteredUser(), user_should_be_registered);
        mOldUserComu = (UsuarioComunidad) getIntent().getSerializableExtra(UserComuBundleKey.USERCOMU_LIST_OBJECT.key);
        assertTrue(mOldUserComu != null, intent_extra_should_be_initialized);

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
                Timber.d("mModifyButton.OnClickListener().onClickLinkToImportanciaUsers()");
                modifyUserComuData();
            }
        });

        Button mDeleteButton = (Button) findViewById(R.id.usercomu_data_ac_delete_button);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("mDeleteButton.OnClickListener().onClickLinkToImportanciaUsers()");
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
            makeToast(this, errorBuilder.toString());
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
        switch (resourceId) {
            case android.R.id.home:
                doUpMenu(this);
                return true;
            case R.id.see_usercomu_by_comu_ac_mn:
            case R.id.comu_data_ac_mn:
                Intent intent = new Intent();
                intent.putExtra(ComuBundleKey.COMUNIDAD_ID.key, mOldUserComu.getComunidad().getC_Id());
                setIntent(intent);
                new ActivityInitiator(this).initActivityFromMn(resourceId);
                return true;
            case R.id.incid_see_open_by_comu_ac_mn:
            case R.id.incid_reg_ac_mn:
                new ActivityInitiator(this).initActivityFromMn(resourceId);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    @SuppressWarnings("WeakerAccess")
    class ComuDataMenuSetter extends AsyncTask<Void, Void, Boolean> {

        UiException uiException;

        @Override
        protected Boolean doInBackground(Void... aVoid)
        {
            Timber.d("doInBackground()");

            boolean isOldestUserComu = false;
            try {
                isOldestUserComu = userComuDaoRemote.isOldestOrAdmonUserComu(mOldUserComu.getComunidad().getC_Id());
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

    @SuppressWarnings("WeakerAccess")
    class UserComuModifyer extends AsyncTask<UsuarioComunidad, Void, Integer> {

        UiException uiException;

        @Override
        protected Integer doInBackground(UsuarioComunidad... userComus)
        {
            Timber.d("doInBackground()");

            int modifyUserComu = 0;
            try {
                modifyUserComu = userComuDaoRemote.modifyUserComu(userComus[0]);
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
                assertTrue(rowsUpdated == 1, userComu_should_be_modified);
                Intent intent = new Intent(UserComuDataAc.this, SeeUserComuByUserAc.class);
                startActivity(intent);
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    class UserComuEraser extends AsyncTask<Comunidad, Void, Integer> {

        UiException uiException;

        @Override
        protected Integer doInBackground(Comunidad... comunidades)
        {
            Timber.d("doInBackground()");

            int deleteUserComu = 0;
            try {
                deleteUserComu = userComuDaoRemote.deleteUserComu(comunidades[0].getC_Id());
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
                assertTrue(isDeleted != 0, userComu_should_be_deleted);
                Intent intent;
                if (isDeleted == IS_USER_DELETED) {
                    identityCacher.cleanIdentityCache();
                    identityCacher.updateIsRegistered(false);
                    intent = new Intent(UserComuDataAc.this, ComuSearchAc.class);
                    intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_NEW_TASK);
                } else {
                    assertTrue(isDeleted == 1, userComu_should_be_deleted);
                    intent = new Intent(UserComuDataAc.this, SeeUserComuByUserAc.class);
                }
                startActivity(intent);
            } else {
                uiException.processMe(UserComuDataAc.this, new Intent());
            }
        }
    }
}

