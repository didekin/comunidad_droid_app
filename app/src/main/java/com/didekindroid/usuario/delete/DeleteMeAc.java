package com.didekindroid.usuario.delete;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.didekindroid.R;

import java.util.Objects;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.util.UIutils.doToolBar;

/**
 * Preconditions:
 * 1. Registered user.
 * Postconditions:
 * 1. Unregistered user, if she chooses so. ComuSearchAc is to be showed.
 */
@SuppressWarnings("AbstractClassExtendsConcreteClass")
public abstract class DeleteMeAc extends AppCompatActivity implements DeleteMeControllerIf {

    protected Class<? extends Activity> defaultActivityClassToGo;
    Subscription subscription;

    // Template method to be overwritten in the apps.
    protected abstract void setDefaultActivityClassToGo(Class<? extends Activity> activityClassToGo);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        // Preconditions.
        Objects.equals(TKhandler.isRegisteredUser(), true);

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
        super.onDestroy();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    // ============================================================
    //    ..... CONTROLLER IMPLEMENTATION ....
    // ============================================================

    @Override
    public void unregisterUser()
    {   // TODO: to test.
        Timber.d("unregisterUser()");

        subscription = DeleteObservable.getDeleteMeSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DeleteObservable.DeleteMeSubscriber(this));
    }
}
