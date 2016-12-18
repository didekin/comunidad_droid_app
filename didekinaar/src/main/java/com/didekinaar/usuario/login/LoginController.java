package com.didekinaar.usuario.login;

import android.content.Intent;
import android.widget.EditText;

import com.didekin.usuario.Usuario;
import com.didekinaar.R;
import com.didekinaar.exception.UiException;
import com.didekinaar.usuario.UsuarioBean;
import com.didekinaar.utils.ConnectionUtils;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.didekinaar.usuario.UsuarioObservables.getLoginMailSingle;
import static com.didekinaar.usuario.UsuarioObservables.getZipLoginSingle;
import static com.didekinaar.utils.UIutils.getErrorMsgBuilder;
import static com.didekinaar.utils.UIutils.makeToast;

/**
 * User: pedro
 * Date: 15/12/14
 * Time: 10:04 her confirmation.
 */
@SuppressWarnings("WeakerAccess")
class LoginController implements LoginControllerIf {

    final LoginAc loginAc;
    CompositeSubscription subscriptions;

    public LoginController(LoginAc loginAc)
    {
        this.loginAc = loginAc;
    }

    @Override
    public void doLoginValidate()
    {
        Timber.i("doLoginValidate()");

        UsuarioBean usuarioBean = new UsuarioBean(
                ((EditText) loginAc.mAcView.findViewById(R.id.reg_usuario_email_editT)).getText().toString(),
                null,
                ((EditText) loginAc.mAcView.findViewById(R.id.reg_usuario_password_ediT)).getText().toString(),
                null
        );

        StringBuilder errorBuilder = getErrorMsgBuilder(loginAc);
        if (!usuarioBean.validateLoginData(loginAc.getResources(), errorBuilder)) {
            makeToast(loginAc, errorBuilder.toString(), R.color.deep_purple_100);
        } else if (!ConnectionUtils.isInternetConnected(loginAc)) {
            makeToast(loginAc, R.string.no_internet_conn_toast);
        } else {
            subscriptions.add(getZipLoginSingle(usuarioBean.getUsuario())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new LoginValidateSubscriber(loginAc, usuarioBean.getUsuario())));
        }
    }

    @Override
    public void doDialogPositiveClick(String email)
    {
        Timber.d("doDialogPositiveClick()");
        Subscription subscriptionMail = getLoginMailSingle(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LoginMailSubscriber(loginAc));
        subscriptions.add(getLoginMailSingle(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LoginMailSubscriber(loginAc)));
    }

    @Override
    public void doDialogNegativeClick()
    {
        Timber.d("doDialogNegativeClick()");

        Intent intent = new Intent(loginAc, loginAc.defaultActivityClassToGo);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        loginAc.startActivity(intent);
        loginAc.finish();
    }

//    =====================================================================================================
//    .................................... INNER CLASSES .................................
//    =====================================================================================================

    @SuppressWarnings("WeakerAccess")
    static class LoginValidateSubscriber extends Subscriber<Boolean> {

        private final LoginAc loginActivity;
        private final Usuario usuario;

        LoginValidateSubscriber(final LoginAc loginActivity,
                                final Usuario usuario)
        {
            this.loginActivity = loginActivity;
            this.usuario = usuario;
        }

        @Override
        public void onCompleted()
        {
            Timber.d("onCompleted");
        }

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onError");
            if (e instanceof UiException) {
                ((UiException) e).processMe(loginActivity, new Intent());
            }
        }

        @Override
        public void onNext(Boolean isLoginOk)
        {
            Timber.d("onNext");
            if (isLoginOk) {
                Timber.d("LoginValidator.onPostExecute(): login OK");
                Intent intent = new Intent(loginActivity, loginActivity.defaultActivityClassToGo);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                loginActivity.startActivity(intent);
                loginActivity.finish();
            } else if (++loginActivity.counterWrong > 3) { // Password wrong
                loginActivity.showDialog(usuario.getUserName());
            } else {
                Timber.d("LoginSubsriber.onNext(): password wrong, counterWrong = %d%n", loginActivity.counterWrong);
                makeToast(loginActivity, R.string.password_wrong_in_login);
            }
            unsubscribe();
        }
    }

    static class LoginMailSubscriber extends Subscriber<Boolean> {

        private final LoginAc loginActivity;

        LoginMailSubscriber(LoginAc loginActivity)
        {
            this.loginActivity = loginActivity;
        }

        @Override
        public void onCompleted()
        {
            Timber.d("onCompleted()");
        }

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onError");
            if (e instanceof UiException) {
                ((UiException) e).processMe(loginActivity, new Intent());
            }
        }

        @Override
        public void onNext(Boolean isMailOk)
        {
            Timber.d("onNext()");
            if (isMailOk) {
                makeToast(loginActivity, R.string.password_new_in_login);
                loginActivity.recreate();
            }
            unsubscribe();
        }
    }
}
