package com.didekinaar.usuario.delete;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.didekinaar.R;
import com.didekinaar.exception.UiException;
import com.didekinaar.utils.UIutils;

import java.util.Objects;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.didekinaar.security.TokenIdentityCacher.TKhandler;
import static com.didekinaar.usuario.delete.DeleteObservable.getDeleteMeSingle;
import static com.didekinaar.utils.UIutils.doToolBar;

/**
 * Preconditions:
 * 1. Registered user.
 * Postconditions:
 * 1. Unregistered user, if she chooses so. ComuSearchAc is to be showed.
 */
@SuppressWarnings("AbstractClassExtendsConcreteClass")
public abstract class DeleteMeAc extends AppCompatActivity implements DeleteMeControllerIf {

    Subscription subscription;
    protected Class<? extends Activity> defaultActivityClassToGo;

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
                unregisterUser(DeleteMeAc.this, defaultActivityClassToGo);
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

    @Override
    public void unregisterUser(final Context context, final Class<? extends Activity> nextActivityClass)
    {   // TODO: to test.
        Timber.d("unregisterUser()");

        subscription = getDeleteMeSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted()
                    {
                        finish();
                        unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        if (e instanceof UiException) {
                            ((UiException) e).processMe(DeleteMeAc.this, new Intent());
                            // TODO: en todos los 'processMe' de didekinaar hay que verificar que el mensaje en UiException es GENERIC_ERROR.
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean)
                    {
                        Objects.equals(Boolean.TRUE, aBoolean);
                        Intent intent = new Intent(context, nextActivityClass);
                        intent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
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
                UIutils.doUpMenu(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
