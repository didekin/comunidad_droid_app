package com.didekindroid.usuario.delete;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.util.MenuRouter;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_be_registered;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_have_been_deleted;
import static com.didekindroid.usuario.delete.DeleteMeReactor.deleteReactor;
import static com.didekindroid.util.DefaultNextAcRouter.routerMap;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.destroySubscriptions;
import static com.didekindroid.util.UIutils.doToolBar;

/**
 * Preconditions:
 * 1. Registered user.
 * Postconditions:
 * 1. Unregistered user, if she chooses so. ComuSearchAc is to be showed.
 */
public class DeleteMeAc extends AppCompatActivity implements DeleteMeControllerIf {

    CompositeDisposable subscriptions;
    DeleteMeReactorIf reactor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        // Preconditions.
        assertTrue(TKhandler.isRegisteredUser(), user_should_be_registered);
        // Manual injection of reactor.
        reactor = deleteReactor;

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

    @Override
    protected void onDestroy()
    {
        Timber.d("onDestroy()");
        super.onDestroy();
        destroySubscriptions(subscriptions);
    }

    // ============================================================
    //    ..... CONTROLLER IMPLEMENTATION ....
    // ============================================================

    @Override
    public boolean unregisterUser()
    {
        Timber.d("unregisterUser()");
        return reactor.deleteMeInRemote(this);
    }

    @Override
    public CompositeDisposable getSubscriptions()
    {
        Timber.d("getSubscriptions()");
        if (subscriptions == null){
            subscriptions = new CompositeDisposable();
        }
        return  subscriptions;
    }

    @Override
    public void processBackDeleteMeRemote(Boolean isDeleted)
    {
        Timber.d("processBackDeleteMeRemote()");
        assertTrue(isDeleted, user_should_have_been_deleted);
        Intent intent = new Intent(this, routerMap.get(this.getClass()));
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void processErrorInReactor(Throwable e)
    {
        Timber.d("processBackErrorInReactor()");
        if (e instanceof UiException) {
            ((UiException) e).processMe(this, new Intent());
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
                MenuRouter.doUpMenu(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
