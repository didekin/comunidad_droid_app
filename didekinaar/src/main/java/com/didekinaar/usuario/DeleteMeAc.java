package com.didekinaar.usuario;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.didekinaar.R;
import com.didekinaar.comunidad.ComuSearchAc;
import com.didekinaar.exception.UiAarException;

import java.util.Objects;

import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.didekinaar.security.TokenHandler.TKhandler;
import static com.didekinaar.utils.UIutils.doToolBar;
import static com.didekinaar.utils.UIutils.isRegisteredUser;
import static com.didekinaar.utils.UIutils.updateIsRegistered;

/**
 * Preconditions:
 * 1. Registered user.
 * Postconditions:
 * 1. Unregistered user, if she chooses so. ComuSearchAc is to be showed.
 */
@SuppressWarnings("ConstantConditions")
public class DeleteMeAc extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate()");

        // Preconditions.
        Objects.equals(isRegisteredUser(this), true);

        View mAcView = getLayoutInflater().inflate(R.layout.delete_me_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);

        Button mUnregisterButton = (Button) findViewById(R.id.delete_me_ac_unreg_button);
        mUnregisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("mUnregisterButton.OnClickListener().onClick()");
                unregisterUser();
            }
        });
    }

    void unregisterUser()
    {
        Timber.d("unregisterUser()");
        new UserDataEraser().execute();
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
                UserMenu.doUpMenu(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    class UserDataEraser extends AsyncTask<Void, Void, Boolean> {

        UiAarException uiException;

        @Override
        protected Boolean doInBackground(Void... params)
        {
            Timber.d("doInBackground()");

            boolean isDeleted = false;
            try {
                isDeleted = AarUsuarioService.AarUserServ.deleteUser();
                TKhandler.cleanTokenAndBackFile();
                updateIsRegistered(false, DeleteMeAc.this);
            } catch (UiAarException e) {
                uiException = e;
            }
            return isDeleted;
        }

        @Override
        protected void onPostExecute(Boolean isDeleted)
        {
            Timber.d("onPostExecute()");

            if (uiException != null) {
                uiException.processMe(DeleteMeAc.this, new Intent());
            } else {
                Intent intent = new Intent(DeleteMeAc.this, ComuSearchAc.class);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                DeleteMeAc.this.finish();
            }
        }
    }
}
