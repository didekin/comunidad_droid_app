package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.didekin.incidencia.dominio.IncidImportancia;
import com.didekindroid.R;
import com.didekindroid.exception.UiAppException;

import java.util.Objects;

import timber.log.Timber;

import static com.didekinaar.utils.UIutils.checkPostExecute;
import static com.didekinaar.utils.UIutils.doUpMenu;
import static com.didekinaar.gcm.GcmUtils.getGcmToken;
import static com.didekinaar.utils.ConnectionUtils.checkInternetConnected;
import static com.didekinaar.utils.UIutils.doToolBar;
import static com.didekinaar.utils.UIutils.getErrorMsgBuilder;
import static com.didekinaar.utils.UIutils.makeToast;
import static com.didekindroid.incidencia.IncidService.IncidenciaServ;

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
            makeToast(this, errorMsg.toString(), com.didekinaar.R.color.deep_purple_100);
        }
    }

    // ============================================================
    //    ..... ACTION BAR ....
    // ============================================================

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");

        int resourceId = item.getItemId();

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

        UiAppException uiException;

        @Override
        protected Integer doInBackground(IncidImportancia... incidImportancias)
        {
            Timber.d("doInBackground()");
            int rowInserted = 0;

            try {
                rowInserted = IncidenciaServ.regIncidImportancia(incidImportancias[0]);
            } catch (UiAppException e) {
                uiException = e;
            }
            return rowInserted;
        }

        @Override
        protected void onPostExecute(Integer rowInserted)
        {

            if (checkPostExecute(IncidRegAc.this)) return;
            Timber.d("onPostExecute()");

            if (uiException != null) {
                uiException.processMe(IncidRegAc.this, new Intent());
            } else {
                Objects.equals(rowInserted == 2, true);
                Intent intent = new Intent(IncidRegAc.this, IncidSeeOpenByComuAc.class);
                startActivity(intent);
            }
        }
    }
}
