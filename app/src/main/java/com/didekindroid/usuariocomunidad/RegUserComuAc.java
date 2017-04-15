package com.didekindroid.usuariocomunidad;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.didekindroid.R;
import com.didekindroid.comunidad.ComunidadBean;
import com.didekindroid.exception.UiException;
import com.didekindroid.router.ActivityRouter;
import com.didekindroid.security.IdentityCacher;
import com.didekindroid.usuariocomunidad.listbycomu.SeeUserComuByComuAc;
import com.didekindroid.util.ConnectionUtils;
import com.didekindroid.util.UIutils;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import timber.log.Timber;

import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_be_registered;
import static com.didekindroid.usuariocomunidad.UserComuAssertionMsg.user_and_comunidad_should_be_registered;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.checkPostExecute;
import static com.didekindroid.util.UIutils.doToolBar;
import static com.didekindroid.util.UIutils.makeToast;
import static com.didekinlib.model.common.dominio.ValidDataPatterns.LINE_BREAK;

/**
 * User: pedro@didekin
 * Date: 11/05/15
 * Time: 19:13
 *
 * Preconditions:
 * 1. The user is already registered.
 * 2. The activity receives a comunidad object, as an intent key, with the following fields:
 * -- comunidadId.
 * -- nombreComunidad (with tipoVia,nombreVia, numero and sufijoNumero).
 * -- municipio, with codInProvincia and nombre.
 * -- provincia, with provinciaId and nombre.
 * The comunidad already exists in BD.
 * <p/>
 * Postconditions:
 * 1. A long comunidadId is passed as an intent key.
 * 2. The activity SeeUserComuByComuAc is started.
 */
@SuppressWarnings("ConstantConditions")
public class RegUserComuAc extends AppCompatActivity {

    RegUserComuFr mRegUserComuFr;
    IdentityCacher identityCacher;
    private Comunidad mComunidad;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate()");
        identityCacher = TKhandler;

        assertTrue(identityCacher.isRegisteredUser(), user_should_be_registered);
        Comunidad coomunidadIntent = (Comunidad) getIntent().getExtras()
                .getSerializable(COMUNIDAD_LIST_OBJECT.key);
        mComunidad = coomunidadIntent != null ? coomunidadIntent : null;

        setContentView(R.layout.reg_usercomu_ac);
        doToolBar(this, true);
        mRegUserComuFr = (RegUserComuFr) getFragmentManager().findFragmentById(R.id.reg_usercomu_frg);

        Button mRegisterButton = (Button) findViewById(R.id.reg_usercomu_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("View.OnClickListener().onClickLinkToImportanciaUsers()");
                doOnclick();
            }
        });
    }

    void doOnclick()
    {
        Timber.d("doOnclick()");

        // We don't need the user: it is already registered. As to comunidad, it is enough with its id in DB.
        UsuarioComunidadBean usuarioComunidadBean = RegUserComuFr.makeUserComuBeanFromView(
                mRegUserComuFr.getFragmentView(),
                new ComunidadBean(mComunidad.getC_Id(),
                        null, null, null, null, null),
                null);

        StringBuilder errorMsg = new StringBuilder(getResources().getText(R.string.error_validation_msg))
                .append(LINE_BREAK.getRegexp());

        if (!usuarioComunidadBean.validate(getResources(), errorMsg)) {  // error validation.
            makeToast(this, errorMsg.toString());
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            UIutils.makeToast(this, R.string.no_internet_conn_toast);
        } else {
            // Insert usuarioComunidad and go to SeeUserComuByComuAc activity.
            new UserComuRegister().execute(usuarioComunidadBean.getUsuarioComunidad());
            Intent intent = new Intent(this, SeeUserComuByComuAc.class);
            intent.putExtra(COMUNIDAD_ID.key, mComunidad.getC_Id());
            startActivity(intent);
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
                ActivityRouter.doUpMenu(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    @SuppressWarnings("WeakerAccess")
    class UserComuRegister extends AsyncTask<UsuarioComunidad, Void, Integer> {

        UiException uiException;

        @Override
        protected Integer doInBackground(UsuarioComunidad... usuarioComunidad)
        {
            Timber.d("doInBackground()");

            int i = 0;
            try {
                i = userComuDaoRemote.regUserComu(usuarioComunidad[0]);
            } catch (UiException e) {
                uiException = e;
            }
            return i;
        }

        @Override
        protected void onPostExecute(Integer rowInserted)
        {
            if (checkPostExecute(RegUserComuAc.this)) return;

            Timber.d("onPostExecute()");
            if (uiException != null) {
                uiException.processMe(RegUserComuAc.this, new Intent());
            } else {
                assertTrue(rowInserted == 1, user_and_comunidad_should_be_registered);
            }
        }
    }
}