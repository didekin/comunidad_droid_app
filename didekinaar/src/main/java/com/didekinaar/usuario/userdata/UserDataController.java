package com.didekinaar.usuario.userdata;

import android.content.Intent;
import android.widget.EditText;

import com.didekin.usuario.Usuario;
import com.didekinaar.R;
import com.didekinaar.exception.UiException;
import com.didekinaar.usuario.UsuarioBean;
import com.didekinaar.utils.ConnectionUtils;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.didekinaar.usuario.UsuarioDaoRemote.usuarioDaoRemote;
import static com.didekinaar.usuario.userdata.UserDataObservable.getUserDataSingle;
import static com.didekinaar.utils.UIutils.getErrorMsgBuilder;
import static com.didekinaar.utils.UIutils.makeToast;

/**
 * User: pedro@didekin
 * Date: 29/11/16
 * Time: 15:28
 */

@SuppressWarnings("WeakerAccess")
class UserDataController implements UserDataControllerIf {

    final UserDataAc userDataAc;
    CompositeSubscription subscriptions;

    UserDataController(UserDataAc userDataAc)
    {
        this.userDataAc = userDataAc;
    }

    @Override
    public UsuarioBean makeUserBeanFromUserDataAcView()
    {
        Timber.d("makeUserBeanFromUserDataAcView()");
        return new UsuarioBean(
                ((EditText) userDataAc.mAcView.findViewById(R.id.reg_usuario_email_editT)).getText()
                        .toString(),
                ((EditText) userDataAc.mAcView.findViewById(R.id.reg_usuario_alias_ediT)).getText()
                        .toString(),
                ((EditText) userDataAc.mAcView.findViewById(R.id.user_data_ac_password_ediT)).getText()
                        .toString(),
                null
        );
    }

    @Override
    public void loadUserData()
    {
        Timber.d("loadUserData()");
        subscriptions.add(getUserDataSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new UserOldDataSubscriber(userDataAc)));
    }

    @Override
    public void modifyUserData()
    {
        Timber.d("modifyUserData()");
        // TODO: send an email with a number, once the user hass pressed Modify,
        // and show in the activity an EditField to introduce it.
        // Only for changes of password.

        UsuarioBean usuarioBean = makeUserBeanFromUserDataAcView();
        StringBuilder errorBuilder = getErrorMsgBuilder(userDataAc);

        if (!usuarioBean.validateWithOnePassword(userDataAc.getResources(), errorBuilder)) {
            makeToast(userDataAc, errorBuilder.toString(), com.didekinaar.R.color.deep_purple_100);
        }
        if (!ConnectionUtils.isInternetConnected(userDataAc)) {
            makeToast(userDataAc, R.string.no_internet_conn_toast);
        }

        Usuario newUser = new Usuario.UsuarioBuilder().copyUsuario(usuarioBean.getUsuario())
                .uId(userDataAc.mOldUser.getuId())
                .build();

        // El usuario no ha cambiado ningún dato: no hacemos nada.
        if (!isDifferentAlias(newUser) && !isDifferentUserName(newUser)) {
            return;
        }

        // Cambia sólo el alias.
        if (isDifferentAlias(newUser) && !isDifferentUserName(newUser)) {
            modifyOnlyAlias(newUser);
        }

        // Cambia el userName y, quizás, también el alias.
        if (isDifferentUserName(newUser)) {
            modifyUserName(newUser);
        }
    }

    private boolean modifyUserName(Usuario newUser)
    {
        /*Usuario usuarioIn = new Usuario.UsuarioBuilder()
                .userName(newUser.getUserName())
                .alias(newUser.getAlias())
                .uId(newUser.getuId())
                .build();

        try {
            usuarioDaoRemote.modifyUser(usuarioIn);
            TKhandler.cleanIdentityCache();
        } catch (UiException e) {
            uiException = e;
            Timber.d((e.getErrorBean() != null ?
                    e.getErrorBean().getMessage() : "token null in cache"));
            return false; // Authentication error with old credentials.
        }

        try {
            SpringOauthToken token_2 = Oauth2.getPasswordUserToken(usuarioIn.getUserName(), newUser.getPassword());
            TKhandler.initIdentityCache(token_2);
        } catch (UiException e) {
            // Authentication error with new credentials.
            Timber.d(e.getErrorBean().getMessage());
        }
        try {
            usuarioDaoRemote.deleteAccessToken(token_1.getValue());
        } catch (UiException e) {
            // No token in cache
            Timber.d(e.getErrorBean().getMessage());
            e.processMe(UserDataAc.this, new Intent());
        }*/
        return false;
    }

    private boolean modifyOnlyAlias(Usuario newUser)
    {
        Timber.d("modifyOnlyAlias()");
        Usuario usuarioIn = new Usuario.UsuarioBuilder()
                .alias(newUser.getAlias())
                .uId(newUser.getuId())
                .build();
        try {
            usuarioDaoRemote.modifyUser(usuarioIn);
        } catch (UiException e) {
//            uiException = e;
            Timber.d((e.getErrorBean() != null ?
                    e.getErrorBean().getMessage() : "token null in cache"));
            return false;
        }
        return false;
    }

    boolean isDifferentAlias(Usuario newUser)
    {
        return userDataAc.mOldUser.getAlias().equals(newUser.getAlias());
    }

    boolean isDifferentUserName(Usuario newUser)
    {
        return userDataAc.mOldUser.getUserName().equals(newUser.getUserName());
    }

//    =====================================================================================================
//    .................................... INNER CLASSES .................................
//    =====================================================================================================

    static class UserOldDataSubscriber extends Subscriber<Usuario> {

        private final UserDataAc userDataActivity;

        UserOldDataSubscriber(UserDataAc userDataAc)
        {
            userDataActivity = userDataAc;
        }

        @Override
        public void onCompleted()
        {
            Timber.d("onCompleted");
            unsubscribe();
        }

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onError");
            if (e instanceof UiException) {
                ((UiException) e).processMe(userDataActivity, new Intent());
            }
        }

        @Override
        public void onNext(Usuario usuario)
        {
            Timber.d("onNext()");
            userDataActivity.mOldUser = usuario;
            userDataActivity.initUserDataInView();
        }
    }

    static class UserDataUpdateSubscriber extends Subscriber<Boolean> {

        private final UserDataAc userDataActivity;

        UserDataUpdateSubscriber(UserDataAc userDataActivity)
        {
            this.userDataActivity = userDataActivity;
        }

        @Override
        public void onCompleted()
        {
            Timber.d("onCompleted");
            unsubscribe();
        }

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onError");
            if (e instanceof UiException) {
                ((UiException) e).processMe(userDataActivity, new Intent());
            }
        }

        @Override
        public void onNext(Boolean isPswdOk)
        {
            Timber.d("onNext");
            if (!isPswdOk) {
                Timber.d("onPostExecute(): password wrong");
                makeToast(userDataActivity, R.string.password_wrong);
            } else {
                Intent intent = new Intent(userDataActivity, userDataActivity.activityClassToGo);
                userDataActivity.startActivity(intent);
            }
        }
    }
}

/*Observable.fromCallable(createNewUser())
        .subscribeOn(Schedulers.io())
        .flatMap(new Func1<User, Observable<Pair<User, Settings>>>() {
            @Override
            public Observable<Pair<User, Settings>> call(final User user) {
                return Observable.from(fetchUserSettings(user))
                        .map(new Func1<Settings, Pair<User, Settings>>() {
                            @Override
                            public Pair<User, Settings> call(Settings o) {
                                return Pair.create(user, o);
                            }
                        });

            }
        });*/