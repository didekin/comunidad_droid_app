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
import android.widget.CheckBox;
import android.widget.EditText;
import com.didekin.serviceone.domain.Comunidad;
import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.ioutils.ConnectionUtils;
import com.didekindroid.usuario.activity.utils.UserAndComuFiller;
import com.didekindroid.usuario.activity.utils.UserIntentExtras;
import com.didekindroid.usuario.activity.utils.UserMenu;
import com.didekindroid.usuario.dominio.ComunidadBean;
import com.didekindroid.usuario.dominio.UsuarioComunidadBean;
import com.didekindroid.usuario.security.TokenHandler;

import static com.didekin.serviceone.controllers.ControllerConstant.IS_USER_DELETED;
import static com.didekindroid.uiutils.UIutils.*;
import static com.didekindroid.usuario.activity.utils.RolCheckBox.*;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.*;
import static com.didekindroid.usuario.activity.utils.UserMenu.COMU_DATA_AC;
import static com.didekindroid.usuario.activity.utils.UserMenu.SEE_USERCOMU_BY_COMU_AC;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Preconditions:
 * 1. Registered user.
 * 2. An intent with a UsuarioComunidad extra.
 * Postconditions:
 * 1a. Registered user with modified data in a comunidad. Once done, it goes to SeeUserComuByUserAc.
 * 1b. Registered user with data deleted in the comunidad chosen. Once done, it goes to SeeUserComuByUserAc.
 * 1c. Unregistered user, once she has deleted the data of the one comunidad associated to ther. It goes to
 * ComuSearchAc.
 */
public class UserComuDataAc extends Activity {

    private static final String TAG = UserComuDataAc.class.getCanonicalName();

    private View mAcView;
    private Button mModifyButton;
    private Button mDeleteButton;
    private UsuarioComunidad mOldUserComu;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        // Preconditions.
        checkState(isRegisteredUser(this));
        mOldUserComu = (UsuarioComunidad) getIntent().getSerializableExtra(USERCOMU_LIST_OBJECT.extra);
        checkNotNull(mOldUserComu);

        mAcView = getLayoutInflater().inflate(R.layout.usercomu_data_ac_layout, null);
        setContentView(mAcView);
        paintUserComuView();

        mModifyButton = (Button) findViewById(R.id.usercomu_data_ac_modif_button);
        mModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "mModifyButton.OnClickListener().onClick()");
                modifyUserComuData();
            }
        });

        mDeleteButton = (Button) findViewById(R.id.usercomu_data_ac_delete_button);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "mDeleteButton.OnClickListener().onClick()");
                deleteUserComuData();
            }
        });
    }

    private void paintUserComuView()
    {
        Log.d(TAG, "paintUserComuView()");

        ((EditText) mAcView.findViewById(R.id.reg_usercomu_portal_ed)).setText(mOldUserComu.getPortal());
        ((EditText) mAcView.findViewById(R.id.reg_usercomu_escalera_ed)).setText(mOldUserComu.getEscalera());
        ((EditText) mAcView.findViewById(R.id.reg_usercomu_planta_ed)).setText(mOldUserComu.getPlanta());
        ((EditText) mAcView.findViewById(R.id.reg_usercomu_puerta_ed)).setText(mOldUserComu.getPuerta());

        ((CheckBox) mAcView.findViewById(R.id.reg_usercomu_checbox_pre))
                .setChecked(mOldUserComu.getRoles().contains(PRESIDENTE.function));
        ((CheckBox) mAcView.findViewById(R.id.reg_usercomu_checbox_admin))
                .setChecked(mOldUserComu.getRoles().contains(ADMINISTRADOR.function));
        ((CheckBox) mAcView.findViewById(R.id.reg_usercomu_checbox_pro))
                .setChecked(mOldUserComu.getRoles().contains(PROPIETARIO.function));
        ((CheckBox) mAcView.findViewById(R.id.reg_usercomu_checbox_inq))
                .setChecked(mOldUserComu.getRoles().contains(INQUILINO.function));
    }

    private void modifyUserComuData()
    {
        Log.d(TAG, "modifyUserComuData()");

        // ComunidaBean initialized only with comunidadId. UsuarioBean is not initialized.
        UsuarioComunidadBean userComuBean = UserAndComuFiller.makeUserComuBeanFromView(mAcView,
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
            Intent intent = new Intent(this, SeeUserComuByUserAc.class);
            startActivity(intent);
        }
    }

    private void deleteUserComuData()
    {
        Log.d(TAG, "deleteUserComuData()");
        new UserComuEraser().execute(mOldUserComu.getComunidad());
        Intent intent = new Intent(this, ComuSearchAc.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.d(TAG, "onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.usercomu_data_ac_mn, menu);
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
                intent.putExtra(COMUNIDAD_ID.extra, mOldUserComu.getComunidad().getC_Id());
                this.setIntent(intent);
                SEE_USERCOMU_BY_COMU_AC.doMenuItem(this);
                return true;
            case R.id.comu_data_ac_mn:
                COMU_DATA_AC.doMenuItem(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    private class UserComuModifyer extends AsyncTask<UsuarioComunidad, Void, Integer> {

        final String TAG = UserComuModifyer.class.getCanonicalName();

        protected Integer doInBackground(UsuarioComunidad... userComus)
        {
            Log.d(TAG, "doInBackground()");
            return ServOne.modifyUserComu(userComus[0]);
        }

        @Override
        protected void onPostExecute(Integer rowsUpdated)
        {
            Log.d(TAG, "onPostExecute()");
            checkState(rowsUpdated == 1);
        }
    }

    private class UserComuEraser extends AsyncTask<Comunidad, Void, Integer> {

        final String TAG = UserComuEraser.class.getCanonicalName();

        @Override
        protected Integer doInBackground(Comunidad... comunidades)
        {
            Log.d(TAG, "doInBackground()");
            return ServOne.deleteUserComu(comunidades[0].getC_Id());
        }

        @Override
        protected void onPostExecute(Integer isDeleted)
        {
            Log.d(TAG, "onPostExecute()");
            checkState(isDeleted != 0);
            if (isDeleted == IS_USER_DELETED) {
                TokenHandler.TKhandler.cleanCacheAndBckFile();
                updateIsRegistered(false, UserComuDataAc.this);
            }
        }
    }
}

