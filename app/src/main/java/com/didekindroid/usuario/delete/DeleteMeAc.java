package com.didekindroid.usuario.delete;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.didekindroid.R;
import com.didekindroid.router.ActivityInitiator;
import com.didekindroid.router.ActivityRouter;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_be_registered;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_have_been_deleted;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.doToolBar;
import static com.didekindroid.util.UIutils.getUiExceptionFromThrowable;

/**
 * Preconditions:
 * 1. Registered user.
 * Postconditions:
 * 1. Unregistered user, if she chooses so. ComuSearchAc is to be showed.
 */
public class DeleteMeAc extends AppCompatActivity {

    View acView;
    CtrlerDeleteMeIf controller;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        acView = getLayoutInflater().inflate(R.layout.delete_me_ac, null);
        setContentView(acView);
        doToolBar(this, true);

        Button mUnregisterButton = (Button) findViewById(R.id.delete_me_ac_unreg_button);
        mUnregisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("mUnregisterButton.OnClickListener().onClickLinkToImportanciaUsers()");
                controller.deleteMeRemote(new DeleteMeSingleObserver());
            }
        });
    }

    @Override
    protected void onStart()
    {
        Timber.d("onStart()");
        super.onStart();
        // Controller initialization.
        controller = new CtrlerDeleteMe();
        // Preconditions.
        assertTrue(controller.isRegisteredUser(), user_should_be_registered);
    }

    @Override
    public void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
        controller.clearSubscriptions();
    }

    public void replaceComponent(Bundle bundle)
    {
        Timber.d("initAcWithBundle()");
        new ActivityInitiator(this).initAcWithFlag(bundle, FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);
    }

    // ============================================================
    //    .................... ACTION BAR ........................
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

    // .............................. HELPERS ..................................

    @SuppressWarnings("WeakerAccess")
    class DeleteMeSingleObserver extends DisposableSingleObserver<Boolean> {

        @Override
        public void onSuccess(Boolean isDeleted)
        {
            Timber.d("onSuccess(), Thread: %s", Thread.currentThread().getName());
            assertTrue(isDeleted, user_should_have_been_deleted);
            replaceComponent(new Bundle());
        }

        @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
        @Override
        public void onError(Throwable e)
        {
            Timber.d("onErrorObserver, Thread: %s", Thread.currentThread().getName());
            getUiExceptionFromThrowable(e).processMe(DeleteMeAc.this, new Intent());
        }
    }
}
