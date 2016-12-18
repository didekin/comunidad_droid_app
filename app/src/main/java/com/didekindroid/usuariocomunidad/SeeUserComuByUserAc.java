package com.didekindroid.usuariocomunidad;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekinaar.R;
import com.didekinaar.usuario.UserItemMenu;
import com.didekinaar.utils.UIutils;

import java.util.Objects;

import timber.log.Timber;

import static com.didekindroid.comunidad.ComunidadMenu.COMU_SEARCH_AC;
import static com.didekindroid.usuariocomunidad.UserComuBundleKey.USERCOMU_LIST_OBJECT;
import static com.didekinaar.utils.UIutils.doToolBar;
import static com.didekinaar.utils.UIutils.isRegisteredUser;

/**
 * Preconditions:
 * 1. The user is registered.
 * Postconditions:
 * 1. An object UsuarioComunidad is passed as an intent key with:
 * -- an object Comunidad fully initialized.
 * -- an object Usuario fully initialized.
 * -- the rest of data of an object UsuarioComunidad fully initialized.
 */
public class SeeUserComuByUserAc extends AppCompatActivity implements
        SeeUserComuByUserFr.SeeUserComuByUserFrListener {

    SeeUserComuByUserFr mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        // Preconditions: the user is registered.
        Objects.equals(isRegisteredUser(this), true);

        setContentView(R.layout.see_usercomu_by_user_ac);
        doToolBar(this, true);
        mFragment = (SeeUserComuByUserFr) getSupportFragmentManager().findFragmentById(R.id.see_usercomu_by_user_frg);
    }

    // ============================================================
    //    ..... ACTION BAR ....
    // ============================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Timber.d("onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.see_usercomu_by_user_ac_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");

        int resourceId = item.getItemId();

        if (resourceId == android.R.id.home) {
            UIutils.doUpMenu(this);
            return true;
        } else if (resourceId == R.id.user_data_ac_mn) {
            UserItemMenu.USER_DATA_AC.doMenuItem(this);
            return true;
        } else if (resourceId == R.id.comu_search_ac_mn) {
            COMU_SEARCH_AC.doMenuItem(this);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

//    ============================================================
//    .......... LISTENER IMPLEMENTATION AND AUXILIARY METHODS .......
//    ============================================================

    @Override
    public void onUserComuSelected(UsuarioComunidad userComu, int position)
    {
        Timber.d("onUserComuSelected()");
        Intent intent = new Intent(this, UserComuDataAc.class);
        intent.putExtra(USERCOMU_LIST_OBJECT.key, userComu);
        startActivity(intent);
    }
}
