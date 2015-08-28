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
import com.didekindroid.R;
import com.didekindroid.common.ui.CommonPatterns;
import com.didekindroid.common.ui.UIutils;
import com.didekindroid.usuario.dominio.ComunidadBean;
import com.didekindroid.usuario.dominio.AccessToken;

import static com.didekindroid.common.ui.UIutils.updateIsRegistered;
import static com.didekindroid.usuario.common.UserIntentExtras.COMUNIDAD_SEARCH;
import static com.didekindroid.usuario.common.UserMenu.SEE_COMU_AND_USERCOMU_BY_USER_AC;
import static com.didekindroid.usuario.common.UserMenu.REG_COMU_USER_USERCOMU_AC;
import static com.didekindroid.usuario.common.UserMenu.USER_DATA_AC;
import static com.didekindroid.usuario.beanfiller.UserAndComuFiller.makeComunidadBeanFromView;
import static com.didekindroid.usuario.common.TokenHandler.TKhandler;
import static com.google.common.base.Preconditions.checkNotNull;

public class ComuSearchAc extends Activity {

    private static final String TAG = ComuSearchAc.class.getCanonicalName();

    RegComuFr mRegComuFrg;
    protected View mMainView;
    private Button mSearchButton;
    private boolean isRegisteredUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "In onCreate()");

        // To initilize the token cache. This is the launch activity.
        new CheckerTokenInCache().execute(); // TODO: hay que moverlo al sign-up.

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
                .append(CommonPatterns.LINE_BREAK.literal);

        if (!comunidadBean.validate(getResources(), errorMsg)) {
            UIutils.makeToast(this, errorMsg.toString());

        } else {
            Intent intent = new Intent(this, ComuSearchResultsAc.class);
            intent.putExtra(COMUNIDAD_SEARCH.extra,comunidadBean.getComunidad());
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
            case R.id.comu_by_user_list_ac_mn:
                SEE_COMU_AND_USERCOMU_BY_USER_AC.doMenuItem(this);
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

    boolean isTokenInCache()
    {
        return isRegisteredUser;
    }

    /* This class should be in the launcher activity */ // TODO: esta tarea hay que moverla al signup.
    private class CheckerTokenInCache extends AsyncTask<Void, Void, AccessToken> {

        protected AccessToken doInBackground(Void... params)
        {
            return TKhandler.getAccessTokenInCache();
        }

        @Override
        protected void onPostExecute(AccessToken accessToken)
        {
            isRegisteredUser = (accessToken != null ? true : false);
            Log.d(TAG, "CheckerTokenInCache.onPostExecute(): isRegisteredUser = " + isRegisteredUser);
            updateIsRegistered(isRegisteredUser, ComuSearchAc.this);
        }
    }
}