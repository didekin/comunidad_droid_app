package com.didekindroid.incidencia.activity.incidreg;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.didekin.incidencia.dominio.IncidImportancia;
import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.activity.IncidSeeOpenByComuAc;
import com.didekindroid.usuario.firebase.FirebaseTokenReactorIf;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

import static com.didekindroid.incidencia.IncidService.IncidenciaServ;
import static com.didekindroid.incidencia.activity.utils.IncidenciaAssertionMsg.incid_importancia_should_be_registered;
import static com.didekindroid.usuario.firebase.FirebaseTokenReactor.tokenReactor;
import static com.didekindroid.util.ConnectionUtils.checkInternetConnected;
import static com.didekindroid.util.MenuRouter.doUpMenu;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.checkPostExecute;
import static com.didekindroid.util.UIutils.doToolBar;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.util.UIutils.makeToast;

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
public class IncidRegAc extends AppCompatActivity implements IncidRegControllerIf{

    IncidRegAcFragment mRegAcFragment;
    CompositeDisposable subscriptions;
    FirebaseTokenReactorIf reactor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        View mAcView = getLayoutInflater().inflate(R.layout.incid_reg_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);

        reactor = tokenReactor;
        checkGcmToken();

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
        // TODO: sustituir for llamada al observable getGcmToken(this);
        super.onResume();
    }

    @Override
    protected void onDestroy()
    {
        Timber.d("onDestroy()");
        super.onDestroy();
        if (subscriptions != null) {
            subscriptions.clear();
        }
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
            makeToast(this, errorMsg.toString(), R.color.deep_purple_100);
        }
    }

    // ============================================================
    //    ..... CONTROLLER IMPLEMENTATION ....
    /* ============================================================*/

    @Override
    public void checkGcmToken()
    { // TODO: test methods of controller.
        if (subscriptions == null){
            subscriptions = new CompositeDisposable();
        }
        reactor.checkGcmToken(subscriptions);
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

            if (checkPostExecute(IncidRegAc.this)) return;
            Timber.d("onPostExecute()");

            if (uiException != null) {
                uiException.processMe(IncidRegAc.this, new Intent());
            } else {
                assertTrue(rowInserted == 2, incid_importancia_should_be_registered);
                Intent intent = new Intent(IncidRegAc.this, IncidSeeOpenByComuAc.class);
                startActivity(intent);
            }
        }
    }
}
