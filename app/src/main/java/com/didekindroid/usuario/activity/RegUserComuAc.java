package com.didekindroid.usuario.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.didekindroid.R;
import com.didekindroid.usuario.common.UserIntentExtras;
import com.didekindroid.usuario.dominio.Comunidad;
import com.didekindroid.usuario.dominio.ComunidadBean;
import com.didekindroid.usuario.dominio.UsuarioComunidad;
import com.didekindroid.usuario.dominio.UsuarioComunidadBean;

import static com.didekindroid.common.ui.CommonPatterns.LINE_BREAK;
import static com.didekindroid.common.ui.UIutils.makeToast;
import static com.didekindroid.usuario.beanfiller.UserAndComuFiller.makeUsuarioComunidadBeanFromView;
import static com.didekindroid.usuario.common.UserIntentExtras.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;

/**
 * User: pedro@didekin
 * Date: 11/05/15
 * Time: 19:13
 */
public class RegUserComuAc extends Activity {

    public static final String TAG = RegUserComuAc.class.getCanonicalName();

    RegUserComuFr mRegUserComuFr;
    Comunidad mComunidad;
    private Button mRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");

        // Preconditions: a user registered and an existing comunidad passed as intent.
        mComunidad = (Comunidad) getIntent().getExtras().getSerializable(COMUNIDAD_LIST_OBJECT.extra);

        setContentView(R.layout.reg_usercomu_ac);
        mRegUserComuFr = (RegUserComuFr) getFragmentManager().findFragmentById(R.id.reg_usercomu_fr);

        mRegisterButton = (Button) findViewById(R.id.reg_usercomu_button);
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
        UsuarioComunidadBean usuarioComunidadBean = makeUsuarioComunidadBeanFromView(
                mRegUserComuFr.getFragmentView(),
                new ComunidadBean(new Comunidad(mComunidad.getC_Id())),
                null);
        StringBuilder errorMsg = new StringBuilder(getResources().getText(R.string.error_validation_msg))
                .append(LINE_BREAK.literal);

        if (!usuarioComunidadBean.validate(getResources(), errorMsg, false)) {  // error validation.
            makeToast(this, errorMsg.toString());
        } else {
            // Insert usuarioComunidad and go to ComusByUserListAc activity.
            new UserComuRegister().execute(usuarioComunidadBean.getUsuarioComunidad());
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

    private class UserComuRegister extends AsyncTask<UsuarioComunidad, Void, Integer> {

        private final String TAG = UserComuRegister.class.getCanonicalName();

        @Override
        protected Integer doInBackground(UsuarioComunidad... usuarioComunidad)
        {
            Log.d(TAG, "doInBackground()");
            return ServOne.regUserComu(usuarioComunidad[0]);
        }

        @Override
        protected void onPostExecute(Integer rowInserted)
        {
            Log.d(TAG, "onPostExecute()");
            if (rowInserted != 1) {
                Log.e(TAG, getResources().getString(R.string.error_action_in_DB)); // TODO: testar.
            } else {
                Intent intent = new Intent(RegUserComuAc.this, SeeUserComuByComuAc.class);
                intent.putExtra(COMUNIDAD_LIST_OBJECT.extra,mComunidad);
                startActivity(intent);
            }
        }
    }
}