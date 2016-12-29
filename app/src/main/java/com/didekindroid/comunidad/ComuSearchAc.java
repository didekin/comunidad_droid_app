package com.didekindroid.comunidad;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.didekin.oauth2.SpringOauthToken;
import com.didekinaar.exception.UiException;
import com.didekinaar.utils.ConnectionUtils;
import com.didekinaar.utils.UIutils;
import com.didekindroid.R;
import com.didekindroid.usuario.login.LoginAppAc;
import com.didekindroid.usuariocomunidad.RegComuAndUserAndUserComuAc;
import com.didekindroid.usuariocomunidad.RegComuAndUserComuAc;
import com.didekindroid.usuariocomunidad.SeeUserComuByUserAc;
import com.didekindroid.usuariocomunidad.UserComuDataAc;

import timber.log.Timber;

import static com.didekin.common.dominio.ValidDataPatterns.LINE_BREAK;
import static com.didekinaar.security.TokenIdentityCacher.TKhandler;
import static com.didekinaar.usuario.ItemMenu.mn_handler;
import static com.didekinaar.utils.UIutils.checkPostExecute;
import static com.didekinaar.utils.UIutils.doToolBar;
import static com.didekinaar.utils.UIutils.makeToast;
import static com.didekindroid.comunidad.RegComuFr.makeComunidadBeanFromView;
import static com.didekindroid.usuariocomunidad.UserComuMenu.REG_COMU_USERCOMU_AC;
import static com.didekindroid.usuariocomunidad.UserComuMenu.REG_COMU_USER_USERCOMU_AC;
import static com.didekindroid.usuariocomunidad.UserComuMenu.SEE_USERCOMU_BY_USER_AC;

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

    Menu mMenu;
    RegComuFr mRegComuFrg;
    protected View mMainView;
    boolean hasLoginToRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Timber.d("In onCreate()");

        // To initilize the token cache. This is the launch activity.
        new CheckerTokenInCache().execute();

        mMainView = getLayoutInflater().inflate(R.layout.comu_search_ac, null);
        setContentView(mMainView);
        doToolBar(this, false);
        mRegComuFrg = (RegComuFr) getFragmentManager().findFragmentById(R.id.reg_comunidad_frg);

        Button mSearchButton = (Button) findViewById(R.id.searchComunidad_Bton);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("View.OnClickListener().onClick()");
                searchComunidad();
            }
        });
    }

    void searchComunidad()
    {
        Timber.i("In searchComunidad()");

        ComunidadBean comunidadBean = mRegComuFrg.getComunidadBean();

        makeComunidadBeanFromView(mRegComuFrg.getFragmentView(), comunidadBean);

        // Validation of data.
        StringBuilder errorMsg = new StringBuilder(getResources().getText(R.string.error_validation_msg))
                .append(LINE_BREAK.getRegexp());

        if (!comunidadBean.validate(getResources(), errorMsg)) {
            UIutils.makeToast(this, errorMsg.toString(), R.color.deep_purple_100);
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            makeToast(this, R.string.no_internet_conn_toast);
        } else {
            Intent intent = new Intent(this, ComuSearchResultsAc.class);
            intent.putExtra(ComuBundleKey.COMUNIDAD_SEARCH.key, comunidadBean.getComunidad());
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy()
    {
        Timber.d("onDestroy()");
        super.onDestroy();
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
        mMenu = menu;
        if (hasLoginToRemove) {
            mMenu.removeItem(R.id.login_ac_mn);
            mMenu.add(R.id.user_data_ac_mn);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");

        int resourceId = item.getItemId();

        if (resourceId == R.id.user_data_ac_mn) {
            mn_handler.doMenuItem(this, UserComuDataAc.class);
            return true;
        } else if (resourceId == R.id.see_usercomu_by_user_ac_mn) {
            SEE_USERCOMU_BY_USER_AC.doMenuItem(this, SeeUserComuByUserAc.class);
            return true;
        } else if (resourceId == R.id.reg_nueva_comunidad_ac_mn) {
            if (TKhandler.isRegisteredUser()) {
                REG_COMU_USERCOMU_AC.doMenuItem(this, RegComuAndUserComuAc.class);
            } else {
                REG_COMU_USER_USERCOMU_AC.doMenuItem(this, RegComuAndUserAndUserComuAc.class);
            }
            return true;
        } else if (resourceId == R.id.login_ac_mn) {
            mn_handler.doMenuItem(this, LoginAppAc.class);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    /*    ============================================================*/

    /* This class should be in the launcher activity */
    class CheckerTokenInCache extends AsyncTask<Void, Void, SpringOauthToken> {

        UiException uiException;

        @Override
        protected SpringOauthToken doInBackground(Void... params)
        {
            Timber.d("CheckerTokenInCache.doInBackground");

            SpringOauthToken springOauthTokenInCache = null;
            try {
                springOauthTokenInCache = TKhandler.getAccessTokenInCache();
            } catch (UiException e) {
                uiException = e;
            }
            return springOauthTokenInCache;
        }

        @SuppressWarnings("ResourceType")
        @Override
        protected void onPostExecute(SpringOauthToken springOauthToken)
        {
            if (checkPostExecute(ComuSearchAc.this)) return;

            Timber.d("CheckerTokenInCache.onPostExecute() springOauthToken null = %b%n", springOauthToken == null);

            if (uiException == null && springOauthToken != null) {
                TKhandler.updateIsRegistered(true);
                if (mMenu != null) {
                    mMenu.removeItem(R.id.login_ac_mn);
                    mMenu.add(R.id.user_data_ac_mn);
                } else {
                    hasLoginToRemove = true;
                }
            }
        }
    }
}