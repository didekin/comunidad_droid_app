package com.didekindroid.usuario.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.didekin.retrofitcl.OauthToken.AccessToken;
import com.didekin.serviceone.domain.DataPatterns;
import com.didekindroid.R;
import com.didekindroid.uiutils.UIutils;
import com.didekindroid.usuario.dominio.ComunidadBean;

import static com.didekindroid.security.TokenHandler.TKhandler;
import static com.didekindroid.uiutils.UIutils.updateIsRegistered;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeComunidadBeanFromView;
import static com.didekindroid.usuario.activity.utils.UserIntentExtras.COMUNIDAD_SEARCH;
import static com.didekindroid.usuario.activity.utils.UserMenu.*;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Postconditions:
 * <p/>
 * 1. An object comunidad, to be used as search criterium, is passed as an intent extra with the following fields:
 * -- tipoVia.
 * -- nombreVia.
 * -- numero.
 * -- sufijoNumero (it can be an empty string).
 * -- municipio with codInProvincia and provinciaId.
 */
public class ComuSearchAc extends Activity {

    private static final String TAG = ComuSearchAc.class.getCanonicalName();

    RegComuFr mRegComuFrg;
    protected View mMainView;
    private Button mSearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "In onCreate()");

        // To initilize the token cache. This is the launch activity.
        new CheckerTokenInCache().execute();

        mMainView = getLayoutInflater().inflate(R.layout.comu_search_ac, null);
        setContentView(mMainView);
        mRegComuFrg = (RegComuFr) getFragmentManager().findFragmentById(R.id.reg_comunidad_frg);

        mSearchButton = (Button) findViewById(R.id.searchComunidad_Bton);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "View.OnClickListener().onClick()");
                searchComunidad();
            }
        });
    }

    void searchComunidad()
    {
        Log.i(TAG, "In searchComunidad()");

        ComunidadBean comunidadBean = mRegComuFrg.getComunidadBean();

        makeComunidadBeanFromView(mRegComuFrg.getFragmentView(), comunidadBean);

        // Validation of data.
        StringBuilder errorMsg = new StringBuilder(getResources().getText(R.string.error_validation_msg))
                .append(DataPatterns.LINE_BREAK.getRegexp());

        if (!comunidadBean.validate(getResources(), errorMsg)) {
            UIutils.makeToast(this, errorMsg.toString());

        } else {
            Intent intent = new Intent(this, ComuSearchResultsAc.class);
            intent.putExtra(COMUNIDAD_SEARCH.extra, comunidadBean.getComunidad());
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy()
    {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }

//    ============================================================
//    ..... ACTION BAR ....
//    ============================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.d(TAG, "onCreateOptionsMenu()");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.comu_search_ac_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.d(TAG, "onOptionsItemSelected()");

        int resourceId = checkNotNull(item.getItemId());

        switch (resourceId) {
            case R.id.user_data_ac_mn:
                USER_DATA_AC.doMenuItem(this);
                return true;
            case R.id.see_usercomu_by_user_ac_mn:
                SEE_USERCOMU_BY_USER_AC.doMenuItem(this);
                return true;
            case R.id.reg_comu_user_usercomu_ac_mn:
                REG_COMU_USER_USERCOMU_AC.doMenuItem(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================


    /* This class should be in the launcher activity */
    private class CheckerTokenInCache extends AsyncTask<Void, Void, AccessToken> {

        @Override
        protected AccessToken doInBackground(Void... params)
        {
            return TKhandler.getAccessTokenInCache();
        }

        @Override
        protected void onPostExecute(AccessToken accessToken)
        {
            boolean isRegisteredUser = (accessToken != null ? true : false);
            Log.d(TAG, "CheckerTokenInCache.onPostExecute(): isRegisteredUser = " + isRegisteredUser);
            if (!isRegisteredUser) {
                updateIsRegistered(isRegisteredUser, ComuSearchAc.this);
            }
        }
    }
}