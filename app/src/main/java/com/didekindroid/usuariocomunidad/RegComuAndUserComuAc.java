package com.didekindroid.usuariocomunidad;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.didekindroid.R;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerParentInjectorIf;
import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.comunidad.RegComuFr;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import timber.log.Timber;

import static com.didekindroid.usuariocomunidad.UserComuAssertionMsg.user_and_comunidad_should_be_registered;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.checkPostExecute;
import static com.didekindroid.util.UIutils.doToolBar;

/**
 * Preconditions:
 * 1. The user is registered with a different comunidad.
 */
@SuppressWarnings("ConstantConditions")
public class RegComuAndUserComuAc extends AppCompatActivity implements ViewerParentInjectorIf {

    Button mRegistroButton;
    private RegComuFr mRegComuFrg;
    private RegUserComuFr mRegUserComuFrg;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_comu_and_usercomu_ac);
        doToolBar(this, true);

        mRegComuFrg = (RegComuFr) getSupportFragmentManager().findFragmentById(R.id.reg_comunidad_frg);
        mRegUserComuFrg = (RegUserComuFr) getFragmentManager().findFragmentById(R.id
                .reg_usercomu_frg);

        mRegistroButton = (Button) findViewById(R.id.reg_comu_usuariocomunidad_button);
        mRegistroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("View.OnClickListener().onClickLinkToImportanciaUsers()");
                registerComuAndUserComu();
            }
        });
    }

    void registerComuAndUserComu()
    {
        Timber.d("registerComuAndUserComu()");

        /*ComunidadBean comunidadBean = mRegComuFrg.getComunidadBean();     TODO: modificar y descomentar.
        ViewerRegComuFr.makeComunidadBeanFromView(mRegComuFrg.getFragmentView(), comunidadBean);
        UsuarioComunidadBean usuarioComunidadBean = RegUserComuFr.makeUserComuBeanFromView(mRegUserComuFrg
                .getFragmentView(), comunidadBean, null);

        // Validation of data.
        StringBuilder errorMsg = getErrorMsgBuilder(this);

        if (!usuarioComunidadBean.validate(getResources(), errorMsg)) {
            makeToast(this, errorMsg.toString());
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            makeToast(this, R.string.no_internet_conn_toast);
        } else {
            new ComuAndUserComuRegister().execute(usuarioComunidadBean.getUsuarioComunidad());
            Intent intent = new Intent(this, SeeUserComuByUserAc.class);
            startActivity(intent);
        }*/
    }

    // ==================================  ViewerParentInjectorIf  =================================

    @Override
    public ViewerIf getViewerAsParent()
    {
        Timber.d("getViewerAsParent()");
        return null; // TODO.
    }

    @Override
    public void setChildInViewer(ViewerIf childInViewer)
    {
        Timber.d("setChildInViewer()"); // TODO.
    }

//    ============================================================
//    ..... ACTION BAR ....
//    ============================================================

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");

        int resourceId = item.getItemId();

        switch (resourceId) {
            case android.R.id.home:
                Intent intent = new Intent(this, ComuSearchAc.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                NavUtils.navigateUpTo(this, intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    @SuppressWarnings("WeakerAccess")
    class ComuAndUserComuRegister extends AsyncTask<UsuarioComunidad, Void, Boolean> {

        UiException uiException;

        @Override
        protected Boolean doInBackground(UsuarioComunidad... usuarioComunidad)
        {
            Timber.d("doInBackground()");
            boolean isRegistered = false;
            try {
                isRegistered = userComuDaoRemote.regComuAndUserComu(usuarioComunidad[0]);
            } catch (UiException e) {
                uiException = e;
            }
            return isRegistered;
        }

        @Override
        protected void onPostExecute(Boolean rowInserted)
        {
            if (checkPostExecute(RegComuAndUserComuAc.this)) return;

            Timber.d("onPostExecute()");
            if (uiException != null) {
                uiException.processMe(RegComuAndUserComuAc.this, new Intent());
            } else {
                assertTrue(rowInserted, user_and_comunidad_should_be_registered);
            }
        }
    }
}

