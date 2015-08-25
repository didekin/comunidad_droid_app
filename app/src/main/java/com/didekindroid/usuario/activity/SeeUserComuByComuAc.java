package com.didekindroid.usuario.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.didekindroid.R;
import com.didekindroid.usuario.dominio.Comunidad;
import com.didekindroid.usuario.dominio.ComunidadBean;
import com.didekindroid.usuario.dominio.UsuarioComunidad;
import com.didekindroid.usuario.dominio.UsuarioComunidadBean;

import java.util.List;

import static com.didekindroid.common.ui.CommonPatterns.LINE_BREAK;
import static com.didekindroid.common.ui.UIutils.makeToast;
import static com.didekindroid.usuario.beanfiller.UserAndComuFiller.makeUsuarioComunidadBeanFromView;
import static com.didekindroid.usuario.common.UserIntentExtras.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;

/**
 * User: pedro@didekin
 * Date: 25/08/15
 * Time: 16:30
 */
public class SeeUserComuByComuAc extends Activity {

    public static final String TAG = SeeUserComuByComuAc.class.getCanonicalName();
    Comunidad mComunidad;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");

        // Preconditions: a user registered and an existing comunidad passed as intent.
        mComunidad = (Comunidad) getIntent().getExtras().getSerializable(COMUNIDAD_LIST_OBJECT.extra);

        setContentView(R.layout.see_usercomu_by_comu_ac);

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
}
