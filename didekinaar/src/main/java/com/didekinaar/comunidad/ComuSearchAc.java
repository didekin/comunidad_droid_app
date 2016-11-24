package com.didekinaar.comunidad;

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
import com.didekinaar.R;
import com.didekinaar.exception.UiAarException;
import com.didekinaar.usuario.UserMenu;
import com.didekinaar.usuariocomunidad.UserAndComuFiller;
import com.didekinaar.utils.ConnectionUtils;
import com.didekinaar.utils.UIutils;

import timber.log.Timber;

import static com.didekin.common.dominio.ValidDataPatterns.LINE_BREAK;
import static com.didekinaar.comunidad.ComuBundleKey.COMUNIDAD_SEARCH;
import static com.didekinaar.security.TokenHandler.TKhandler;
import static com.didekinaar.utils.UIutils.doToolBar;
import static com.didekinaar.utils.UIutils.isRegisteredUser;
import static com.didekinaar.utils.UIutils.makeToast;
import static com.didekinaar.utils.UIutils.updateIsRegistered;

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

        UserAndComuFiller.makeComunidadBeanFromView(mRegComuFrg.getFragmentView(), comunidadBean);

        // Validation of data.
        StringBuilder errorMsg = new StringBuilder(getResources().getText(R.string.error_validation_msg))
                .append(LINE_BREAK.getRegexp());

        if (!comunidadBean.validate(getResources(), errorMsg)) {
            UIutils.makeToast(this, errorMsg.toString(), com.didekinaar.R.color.deep_purple_100);
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            makeToast(this, R.string.no_internet_conn_toast);
        } else {
            Intent intent = new Intent(this, ComuSearchResultsAc.class);
            intent.putExtra(COMUNIDAD_SEARCH.key, comunidadBean.getComunidad());
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
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");

        int resourceId = item.getItemId();

        if (resourceId == R.id.user_data_ac_mn) {
            UserMenu.USER_DATA_AC.doMenuItem(this);
            return true;
        } else if (resourceId == R.id.see_usercomu_by_user_ac_mn) {
            UserMenu.SEE_USERCOMU_BY_USER_AC.doMenuItem(this);
            return true;
        } else if (resourceId == R.id.reg_nueva_comunidad_ac_mn) {
            if (isRegisteredUser(this)) {
                UserMenu.REG_COMU_USERCOMU_AC.doMenuItem(this);
            } else {
                UserMenu.REG_COMU_USER_USERCOMU_AC.doMenuItem(this);
            }
            return true;
        } else if (resourceId == R.id.login_ac_mn) {
            UserMenu.LOGIN_AC.doMenuItem(this);
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

        UiAarException uiException;

        @Override
        protected SpringOauthToken doInBackground(Void... params)
        {
            Timber.d("CheckerTokenInCache.doInBackground");

            SpringOauthToken springOauthTokenInCache = null;
            try {
                springOauthTokenInCache = TKhandler.getAccessTokenInCache();
            } catch (UiAarException e) {
                uiException = e;
            }
            return springOauthTokenInCache;
        }

        @Override
        protected void onPostExecute(SpringOauthToken springOauthToken)
        {
            Timber.d("CheckerTokenInCache.onPostExecute() springOauthToken null = %b%n", springOauthToken == null);

            if (uiException == null && springOauthToken != null) {
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