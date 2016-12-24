package com.didekindroid.usuariocomunidad;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.didekinaar.R;
import com.didekinaar.usuario.UserItemMenu;
import com.didekinaar.utils.UIutils;

import java.util.Objects;

import timber.log.Timber;

import static com.didekinaar.utils.UIutils.doToolBar;
import static com.didekinaar.security.TokenIdentityCacher.TKhandler.isRegisteredUser;
import static com.didekindroid.comunidad.ComunidadMenu.COMU_SEARCH_AC;
import static com.didekindroid.usuariocomunidad.UserComuMenu.SEE_USERCOMU_BY_USER_AC;

/**
 * User: pedro@didekin
 * Date: 25/08/15
 * Time: 16:30
 */
/**
 *  Preconditions:
 *  1. a long comunidadId is passed as an intent key.
 *  2. the user is registered.
 */
public class SeeUserComuByComuAc extends AppCompatActivity {

    SeeUserComuByComuFr mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate()");

        // Preconditions: the user is registered.
        Objects.equals(TKhandler.isRegisteredUser(this), true);

        setContentView(R.layout.see_usercomu_by_comu_ac);
        doToolBar(this, true);
        mFragment = (SeeUserComuByComuFr) getSupportFragmentManager().findFragmentById(R.id.see_usercomu_by_comu_frg);
    }

    // ============================================================
    //    ..... ACTION BAR ....
    // ============================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Timber.d("onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.see_usercomu_by_comu_ac_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");

        int resourceId = item.getItemId();

        if (resourceId == android.R.id.home) {
            UIutils.doUpMenu(this);
            return true;
        } else if (resourceId == R.id.see_usercomu_by_user_ac_mn) {
            SEE_USERCOMU_BY_USER_AC.doMenuItem(this);
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
}
