package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.didekin.oauth2.OauthToken.AccessToken;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.common.utils.ConnectionUtils;
import com.didekindroid.common.utils.UIutils;
import com.didekindroid.usuario.dominio.ComunidadBean;

import static android.widget.Toast.LENGTH_SHORT;
import static com.didekin.common.dominio.DataPatterns.LINE_BREAK;
import static com.didekindroid.common.activity.BundleKey.COMUNIDAD_SEARCH;
import static com.didekindroid.common.activity.TokenHandler.TKhandler;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.common.utils.UIutils.makeToast;
import static com.didekindroid.common.utils.UIutils.updateIsRegistered;
import static com.didekindroid.usuario.activity.utils.UserAndComuFiller.makeComunidadBeanFromView;
import static com.didekindroid.usuario.activity.utils.UserMenu.LOGIN_AC;
import static com.didekindroid.usuario.activity.utils.UserMenu.REG_COMU_USER_USERCOMU_AC;
import static com.didekindroid.usuario.activity.utils.UserMenu.SEE_USERCOMU_BY_USER_AC;
import static com.didekindroid.usuario.activity.utils.UserMenu.USER_DATA_AC;
import static com.google.common.base.Preconditions.checkNotNull;

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

    private static final String TAG = ComuSearchAc.class.getCanonicalName();

    private Menu mMenu;
    RegComuFr mRegComuFrg;
    protected View mMainView;
    private boolean hasLoginToRemove;

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

        doToolBar(this, false);

        Button mSearchButton = (Button) findViewById(R.id.searchComunidad_Bton);
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
                .append(LINE_BREAK.getRegexp());

        if (!comunidadBean.validate(getResources(), errorMsg)) {
            UIutils.makeToast(this, errorMsg.toString(), Toast.LENGTH_SHORT);
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            makeToast(this, R.string.no_internet_conn_toast, LENGTH_SHORT);
        } else {
            Intent intent = new Intent(this, ComuSearchResultsAc.class);
            intent.putExtra(COMUNIDAD_SEARCH.key, comunidadBean.getComunidad());
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
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.comu_search_ac_menu, menu);
        mMenu = menu;
        if (hasLoginToRemove) {
            mMenu.removeItem(R.id.login_ac_mn);
        }
        return true;
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
            case R.id.login_ac_mn:
                LOGIN_AC.doMenuItem(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================


    /* This class should be in the launcher activity */
    class CheckerTokenInCache extends AsyncTask<Void, Void, AccessToken> {
    // TODO: to persist the task during restarts and properly cancel the task when the activity is destroyed. (Example in Shelves)

        UiException uiException;

        @Override
        protected AccessToken doInBackground(Void... params)
        {
            Log.d(TAG, "CheckerTokenInCache.doInBackground");

            AccessToken accessTokenInCache = null;
            try {
                accessTokenInCache = TKhandler.getAccessTokenInCache();
            } catch (UiException e) {
                uiException = e;
            }
            return accessTokenInCache;
        }

        @Override
        protected void onPostExecute(AccessToken accessToken)
        {
            Log.d(TAG, "CheckerTokenInCache.onPostExecute() accessToken null = "
                    + (accessToken == null));

            if (uiException == null && accessToken != null) {
                updateIsRegistered(true, ComuSearchAc.this);
                if (mMenu != null) {
                    mMenu.removeItem(R.id.login_ac_mn);
                } else {
                    hasLoginToRemove = true;
                }
            }
        }
    }
}