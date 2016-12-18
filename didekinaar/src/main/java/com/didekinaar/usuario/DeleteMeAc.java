package com.didekinaar.usuario;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.didekinaar.exception.UiException;

import java.util.Objects;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.didekinaar.usuario.UsuarioObservables.getDeleteMeSingle;

public class DeleteMeAc extends AppCompatActivity {

    Subscription subscription;

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (subscription != null){
            subscription.unsubscribe();
        }
    }

    void unregisterUser(final Context context, final Class<? extends Activity> nextActivityClass)
    {
        Timber.d("unregisterUser()");

        subscription = getDeleteMeSingle().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted()
                    {
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
                        finish();
                    }
                });
    }
}
