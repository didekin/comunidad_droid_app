package com.didekindroid.comunidad;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.didekindroid.R;
import com.didekindroid.router.ActivityInitiator;
import com.didekindroid.security.IdentityCacher;
import com.didekindroid.security.OauthTokenReactorIf;
import com.didekindroid.util.ConnectionUtils;
import com.didekindroid.util.UIutils;

import timber.log.Timber;

import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_SEARCH;
import static com.didekindroid.comunidad.RegComuFr.makeComunidadBeanFromView;
import static com.didekindroid.router.ActivityRouter.acRouter;
import static com.didekindroid.security.OauthTokenReactor.tokenReactor;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.util.UIutils.doToolBar;
import static com.didekindroid.util.UIutils.makeToast;
import static com.didekinlib.model.common.dominio.ValidDataPatterns.LINE_BREAK;

/**
 * Postconditions:
 * <p/>
 * 1. An object comunidad, to be used as search criterium, is passed as an intent key with the following fields:
 * -- tipoVia.
 * -- nombreVia.
 * -- numero.
 * -- sufijoNumero (it can be an empty string).
 * -- municipio with codInProvincia and provinciaId.
 */
@SuppressWarnings("ConstantConditions")
public class ComuSearchAc extends AppCompatActivity {

    protected View mainView;
    RegComuFr regComuFrg;
    IdentityCacher identityCacher;
    OauthTokenReactorIf reactor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Timber.d("In onCreate()");

        mainView = getLayoutInflater().inflate(R.layout.comu_search_ac, null);
        setContentView(mainView);
        doToolBar(this, false);
        regComuFrg = (RegComuFr) getFragmentManager().findFragmentById(R.id.reg_comunidad_frg);

        Button mSearchButton = (Button) findViewById(R.id.searchComunidad_Bton);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("View.OnClickListener().onClickLinkToImportanciaUsers()");
                searchComunidad();
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        identityCacher = TKhandler;
        reactor = tokenReactor;
        // To initialize the token cache. This is the launch activity.
        identityCacher.refreshAccessToken(reactor);
    }

    void searchComunidad()
    {
        Timber.i("In searchComunidad()");

        ComunidadBean comunidadBean = regComuFrg.getComunidadBean();

        makeComunidadBeanFromView(regComuFrg.getFragmentView(), comunidadBean);

        // Validation of data.
        StringBuilder errorMsg = new StringBuilder(getResources().getText(R.string.error_validation_msg))
                .append(LINE_BREAK.getRegexp());

        if (!comunidadBean.validate(getResources(), errorMsg)) {
            UIutils.makeToast(this, errorMsg.toString());
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            makeToast(this, R.string.no_internet_conn_toast);
        } else {
            Intent intent = new Intent(this, acRouter.nextActivity(getClass()));
            intent.putExtra(COMUNIDAD_SEARCH.key, comunidadBean.getComunidad());
            startActivity(intent);
        }
    }

//    ============================================================
//    ..... ACTION BAR ....
//    ============================================================

    @SuppressWarnings("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Timber.d("onCreateOptionsMenu()");
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.comu_search_ac_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        Timber.d("onPrepareOptionsMenu()");
        if (identityCacher.isRegisteredUser()) {
            menu.findItem(R.id.see_usercomu_by_user_ac_mn).setVisible(true).setEnabled(true);
            menu.findItem(R.id.user_data_ac_mn).setVisible(true).setEnabled(true);
        } else {
            menu.findItem(R.id.login_ac_mn).setVisible(true).setEnabled(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");
        ActivityInitiator activityInitiator = new ActivityInitiator(this);
        int resourceId = item.getItemId();

        switch (resourceId) {
            case R.id.user_data_ac_mn:
            case R.id.see_usercomu_by_user_ac_mn:
            case R.id.login_ac_mn:
            case R.id.reg_nueva_comunidad_ac_mn:
                activityInitiator.initActivityFromMn(resourceId);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}