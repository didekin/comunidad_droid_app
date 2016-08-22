package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.didekin.usuario.dominio.Comunidad;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.common.utils.ConnectionUtils;
import com.didekindroid.common.utils.UIutils;
import com.didekindroid.usuario.dominio.ComunidadBean;
import com.didekindroid.usuario.dominio.UsuarioComunidadBean;

import static com.didekin.common.dominio.DataPatterns.LINE_BREAK;
import static com.didekindroid.common.activity.BundleKey.COMUNIDAD_ID;
import static com.didekindroid.common.activity.BundleKey.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.common.utils.UIutils.makeToast;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeUserComuBeanFromView;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static com.google.common.base.Preconditions.checkState;

/**
 * User: pedro@didekin
 * Date: 11/05/15
 * Time: 19:13
 */

/**
 * Preconditions:
 * 1. The user is already registered.
 * 2. The activity receives a comunidad object, as an intent key, with the following fields:
 * -- comunidadId.
 * -- nombreComunidad (with tipoVia,nombreVia, numero and sufijoNumero).
 * -- municipio, with codInProvincia and nombre.
 * -- provincia, with provinciaId and nombre.
 * The comunidad already exists in BD.
 * <p/>
 * Postconditions:
 * 1. A long comunidadId is passed as an intent key.
 * 2. The activity SeeUserComuByComuAc is started.
 */
@SuppressWarnings("ConstantConditions")
public class RegUserComuAc extends AppCompatActivity {

    public static final String TAG = RegUserComuAc.class.getCanonicalName();

    RegUserComuFr mRegUserComuFr;
    private Comunidad mComunidad;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");

        checkState(isRegisteredUser(this));
        Comunidad coomunidadIntent =  (Comunidad) getIntent().getExtras()
                .getSerializable(COMUNIDAD_LIST_OBJECT.key);
        mComunidad = coomunidadIntent != null ? coomunidadIntent : null;

        setContentView(R.layout.reg_usercomu_ac);
        doToolBar(this, true);
        mRegUserComuFr = (RegUserComuFr) getFragmentManager().findFragmentById(R.id.reg_usercomu_frg);

        Button mRegisterButton = (Button) findViewById(R.id.reg_usercomu_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "View.OnClickListener().onClick()");
                doOnclick();
            }
        });
    }

    private void doOnclick()
    {
        Log.d(TAG, "doOnclick()");

        // We don't need the user: it is already registered. As to comunidad, it is enough with its id in DB.
        UsuarioComunidadBean usuarioComunidadBean = makeUserComuBeanFromView(
                mRegUserComuFr.getFragmentView(),
                new ComunidadBean(mComunidad.getC_Id(),
                        null, null, null, null, null),
                null);

        StringBuilder errorMsg = new StringBuilder(getResources().getText(R.string.error_validation_msg))
                .append(LINE_BREAK.getRegexp());

        if (!usuarioComunidadBean.validate(getResources(), errorMsg)) {  // error validation.
            makeToast(this, errorMsg.toString(), Toast.LENGTH_SHORT);
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            UIutils.makeToast(this, R.string.no_internet_conn_toast, Toast.LENGTH_LONG);
        } else {
            // Insert usuarioComunidad and go to SeeUserComuByComuAc activity.
            new UserComuRegister().execute(usuarioComunidadBean.getUsuarioComunidad());
            Intent intent = new Intent(this, SeeUserComuByComuAc.class);
            intent.putExtra(COMUNIDAD_ID.key, mComunidad.getC_Id());
            startActivity(intent);
        }
    }

    @Override
    protected void onRestart()
    {
        Log.d(TAG, "onRestart()");
        super.onRestart();
    }

    @Override
    protected void onStart()
    {
        Log.d(TAG, "onStart()");
        super.onStart();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Log.d(TAG, "onRestoreInstanceState()");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        Log.d(TAG, "onResume()");
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        Log.d(TAG, "onPause()");
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Log.d(TAG, "onSaveInstanceState()");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop()
    {
        Log.d(TAG, "onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    class UserComuRegister extends AsyncTask<UsuarioComunidad, Void, Integer> {

        private final String TAG = UserComuRegister.class.getCanonicalName();
        UiException uiException;

        @Override
        protected Integer doInBackground(UsuarioComunidad... usuarioComunidad)
        {
            Log.d(TAG, "doInBackground()");

            int i = 0;
            try {
                i = ServOne.regUserComu(usuarioComunidad[0]);
            } catch (UiException e) {
               uiException = e;
            }
            return i;
        }

        @Override
        protected void onPostExecute(Integer rowInserted)
        {
            Log.d(TAG, "onPostExecute()");
            if (uiException != null){
                uiException.processMe(RegUserComuAc.this, new Intent());
            } else{
                checkState(rowInserted == 1);
            }
        }
    }
}