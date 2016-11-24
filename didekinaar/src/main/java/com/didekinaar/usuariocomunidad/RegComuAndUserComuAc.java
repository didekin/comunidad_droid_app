package com.didekinaar.usuariocomunidad;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekinaar.R;
import com.didekinaar.comunidad.ComuSearchAc;
import com.didekinaar.comunidad.ComunidadBean;
import com.didekinaar.comunidad.RegComuFr;
import com.didekinaar.exception.UiAarException;
import com.didekinaar.utils.ConnectionUtils;

import java.util.Objects;

import timber.log.Timber;

import static com.didekinaar.usuariocomunidad.AarUserComuService.AarUserComuServ;
import static com.didekinaar.utils.UIutils.doToolBar;
import static com.didekinaar.utils.UIutils.getErrorMsgBuilder;
import static com.didekinaar.utils.UIutils.makeToast;

/**
 * Preconditions:
 * 1. The user is registered with a different comunidad.
 */
@SuppressWarnings("ConstantConditions")
public class RegComuAndUserComuAc extends AppCompatActivity {

    private RegComuFr mRegComuFrg;
    private RegUserComuFr mRegUserComuFrg;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_comu_and_usercomu_ac);
        doToolBar(this, true);

        mRegComuFrg = (RegComuFr) getFragmentManager().findFragmentById(R.id.reg_comunidad_frg);
        mRegUserComuFrg = (RegUserComuFr) getFragmentManager().findFragmentById(R.id
                .reg_usercomu_frg);

        Button mRegistroButton = (Button) findViewById(R.id.reg_comu_usuariocomunidad_button);
        mRegistroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("View.OnClickListener().onClick()");
                registerComuAndUserComu();
            }
        });
    }

    void registerComuAndUserComu()
    {
        Timber.d("registerComuAndUserComu()");

        ComunidadBean comunidadBean = mRegComuFrg.getComunidadBean();
        UserAndComuFiller.makeComunidadBeanFromView(mRegComuFrg.getFragmentView(), comunidadBean);
        UsuarioComunidadBean usuarioComunidadBean = UserAndComuFiller.makeUserComuBeanFromView(mRegUserComuFrg
                .getFragmentView(), comunidadBean, null);

        // Validation of data.
        StringBuilder errorMsg = getErrorMsgBuilder(this);

        if (!usuarioComunidadBean.validate(getResources(), errorMsg)) {
            makeToast(this, errorMsg.toString(), com.didekinaar.R.color.deep_purple_100);
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            makeToast(this, R.string.no_internet_conn_toast);
        } else {
            new ComuAndUserComuRegister().execute(usuarioComunidadBean. getUsuarioComunidad());
            Intent intent = new Intent(this, SeeUserComuByUserAc.class);
            startActivity(intent);
        }
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

    class ComuAndUserComuRegister extends AsyncTask<UsuarioComunidad, Void, Boolean> {

        UiAarException uiException;

        @Override
        protected Boolean doInBackground(UsuarioComunidad... usuarioComunidad)
        {
            Timber.d("doInBackground()");
            boolean isRegistered = false;
            try {
                isRegistered = AarUserComuServ.regComuAndUserComu(usuarioComunidad[0]);
            } catch (UiAarException e) {
                uiException = e;
            }
            return isRegistered;
        }

        @Override
        protected void onPostExecute(Boolean rowInserted)
        {
            Timber.d("onPostExecute()");
            if (uiException != null) {
                uiException.processMe(RegComuAndUserComuAc.this, new Intent());
            } else {
                Objects.equals(rowInserted, true);
            }
        }
    }
}

