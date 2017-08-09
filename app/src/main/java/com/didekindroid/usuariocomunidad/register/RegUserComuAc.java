package com.didekindroid.usuariocomunidad.register;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.didekindroid.R;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerParentInjectedIf;
import com.didekindroid.api.ViewerParentInjectorIf;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import timber.log.Timber;

import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.router.ActivityRouter.doUpMenu;
import static com.didekindroid.usuariocomunidad.repository.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.register.ViewerRegUserComuAc.newViewerRegUserComuAc;
import static com.didekindroid.usuariocomunidad.util.UserComuAssertionMsg.user_and_comunidad_should_be_registered;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.checkPostExecute;
import static com.didekindroid.util.UIutils.doToolBar;

/**
 * User: pedro@didekin
 * Date: 11/05/15
 * Time: 19:13
 * <p>
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
 * 2. The activity SeeUserComuByUserAc is started.
 */
@SuppressWarnings("ConstantConditions")
public class RegUserComuAc extends AppCompatActivity implements ViewerParentInjectorIf {

    RegUserComuFr regUserComuFr;
    View acView;
    ViewerRegUserComuAc viewer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.i("onCreate()");
        super.onCreate(savedInstanceState);

        Comunidad coomunidadIntent = (Comunidad) getIntent().getExtras()
                .getSerializable(COMUNIDAD_LIST_OBJECT.key);

        acView = getLayoutInflater().inflate(R.layout.reg_usercomu_ac, null);
        setContentView(acView);
        doToolBar(this, true);

        viewer = newViewerRegUserComuAc(this);
        viewer.doViewInViewer(savedInstanceState,
                new Comunidad.ComunidadBuilder().copyComunidadNonNullValues(
                        (Comunidad) getIntent().getExtras()
                                .getSerializable(COMUNIDAD_LIST_OBJECT.key)
                ).build());

        regUserComuFr = (RegUserComuFr) getSupportFragmentManager().findFragmentById(R.id.reg_usercomu_frg);
    }

    @Override
    public void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
        viewer.clearSubscriptions();
    }

    // ==================================  ViewerParentInjectorIf  =================================

    @Override
    public ViewerParentInjectedIf getViewerAsParent()
    {
        Timber.d("getViewerAsParent()");
        return viewer;
    }

    @Override
    public void setChildInViewer(ViewerIf viewerChild)
    {
        Timber.d("setChildInViewer()");
        viewer.setChildViewer(viewerChild);
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