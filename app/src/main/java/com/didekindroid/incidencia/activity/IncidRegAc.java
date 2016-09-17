package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;

import timber.log.Timber;

import static com.didekindroid.common.utils.ConnectionUtils.checkInternetConnected;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.common.utils.UIutils.getErrorMsgBuilder;
import static com.didekindroid.common.utils.UIutils.getGcmToken;
import static com.didekindroid.common.utils.UIutils.makeToast;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.activity.utils.UserMenu.doUpMenu;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Preconditions:
 * 1. The user is registered.
 * 2. No intent received.
 * Postconditions:
 * 1. No intent passed.
 */

/**
 * This activity is a point of registration for receiving notifications of new incidencias.
 * TODO: añadir varios tags a la incidencia para facilitar búsquedas.
 */
@SuppressWarnings("ConstantConditions")
public class IncidRegAc extends AppCompatActivity {

    IncidRegAcFragment mRegAcFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        getGcmToken(this);

        View mAcView = getLayoutInflater().inflate(R.layout.incid_reg_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);

        mRegAcFragment = (IncidRegAcFragment) getSupportFragmentManager().findFragmentById(R.id.incid_reg_frg);
        Button mRegisterButton = (Button) findViewById(R.id.incid_reg_ac_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("View.OnClickListener().onClick()");
                registerIncidencia();
            }
        });
    }

    @Override
    protected void onResume()
    {
        Timber.d("onResume()");
        getGcmToken(this);
        super.onResume();
    }

    void registerIncidencia()
    {
        Timber.d("registerIncidencia()");

        StringBuilder errorMsg = getErrorMsgBuilder(this);
        try {
            IncidImportancia incidImportancia = mRegAcFragment.getIncidImportanciaBean().makeIncidImportancia(
                    errorMsg, getResources(), mRegAcFragment.getmFragmentView(), mRegAcFragment.getIncidenciaBean());
            if (checkInternetConnected(this)) {
                new IncidenciaRegister().execute(incidImportancia);
            }
        } catch (IllegalStateException e) {
            Timber.e(e.getMessage());
            makeToast(this, errorMsg.toString(), Toast.LENGTH_SHORT);
        }
    }

    // ============================================================
    //    ..... ACTION BAR ....
    // ============================================================

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");

        int resourceId = checkNotNull(item.getItemId());

        switch (resourceId) {
            case android.R.id.home:
                doUpMenu(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    class IncidenciaRegister extends AsyncTask<IncidImportancia, Void, Integer> {

        UiException uiException;

        @Override
        protected Integer doInBackground(IncidImportancia... incidImportancias)
        {
            Timber.d("doInBackground()");
            int rowInserted = 0;

            try {
                rowInserted = IncidenciaServ.regIncidImportancia(incidImportancias[0]);
            } catch (UiException e) {
                uiException = e;
            }
            return rowInserted;
        }

        @Override
        protected void onPostExecute(Integer rowInserted)
        {
            Timber.d("onPostExecute()");

            if (uiException != null) {
                uiException.processMe(IncidRegAc.this, new Intent());
            } else {
                checkState(rowInserted == 2);
                Intent intent = new Intent(IncidRegAc.this, IncidSeeOpenByComuAc.class);
                startActivity(intent);
            }
        }
    }
}
