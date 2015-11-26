package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.didekindroid.R;
import com.didekindroid.incidencia.gcm.AppGcmRegistrationServ;

import static com.didekindroid.common.utils.UIutils.checkPlayServices;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.common.utils.UIutils.isGcmTokenSentServer;

/**
 * This activity is a point of registration for receiving notifications of new incidencias.
 */
public class IncidSeeByUserComuAc extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (checkPlayServices(this) && !isGcmTokenSentServer(this)){
            Intent intent = new Intent(this, AppGcmRegistrationServ.class);
            startService(intent);
        }

        setContentView(R.layout.incid_see_by_user_comu_ac);
        doToolBar(this, false);

    }
}
