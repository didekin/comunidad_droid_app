package com.didekinaar.usuario.userdata;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.didekin.usuario.Usuario;
import com.didekinaar.ActivitySubscriber;

import com.didekinaar.R;
import com.didekinaar.usuario.UsuarioBean;
import com.didekinaar.usuario.userdata.UserDataAcObservable.UserDataUpdateSubscriber;
import com.didekinaar.utils.ConnectionUtils;

import java.util.Objects;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.didekinaar.R.color.deep_purple_100;
import static com.didekinaar.security.TokenIdentityCacher.TKhandler;
import static com.didekinaar.usuario.userdata.UserDataAcObservable.aliasModified;
import static com.didekinaar.usuario.userdata.UserDataAcObservable.getUserDataSingle;
import static com.didekinaar.usuario.userdata.UserDataAcObservable.tokenAndUserModified;
import static com.didekinaar.usuario.userdata.UserDataViewIf.UserChangeToMake.alias_only;
import static com.didekinaar.usuario.userdata.UserDataViewIf.UserChangeToMake.nothing;
import static com.didekinaar.usuario.userdata.UserDataViewIf.UserChangeToMake.userName;
import static com.didekinaar.utils.UIutils.doToolBar;
import static com.didekinaar.utils.UIutils.getErrorMsgBuilder;
import static com.didekinaar.utils.UIutils.makeToast;
import static rx.android.schedulers.AndroidSchedulers.mainThread;
import static rx.schedulers.Schedulers.io;

/**
 * Preconditions:
 * 1. Registered user.
 * Postconditions:
 * 1. Regitered user with modified data. Once done, it goes to SeeUserComuByUserAc.
 */
@SuppressWarnings({"ConstantConditions", "AbstractClassExtendsConcreteClass"})
public abstract class UserDataAc extends AppCompatActivity implements UserDataControllerIf,
        UserDataViewIf {

    View mAcView;
    Usuario oldUser;
    Usuario newUser;
    UserDataViewIf userDataPresenter;
    protected Class<? extends Activity> activityClassToGo;
    CompositeSubscription subscriptions;

    protected abstract void setDefaultActivityClassToGo(Class<? extends Activity> activityClassToGo);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate()");

        // Preconditions.
        Objects.equals(TKhandler.isRegisteredUser(), true);

        mAcView = getLayoutInflater().inflate(R.layout.user_data_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);

        // Initialize subscriptions and user data.
        subscriptions = new CompositeSubscription();
        loadUserData();

        Button mModifyButton = (Button) findViewById(R.id.user_data_modif_button);
        mModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("mModifyButton.OnClickListener().onClick()");
                UserChangeToMake changeToMake = getDataChangedFromAcView();
                if (changeToMake == alias_only) {
                    modifyOnlyAlias();
                }
                if (changeToMake == userName) {
                    modifyUserName();
                }
                Intent intent = new Intent(UserDataAc.this, activityClassToGo);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        Timber.d("onCreate()");
        super.onDestroy();
        if (subscriptions != null && subscriptions.hasSubscriptions()) {
            subscriptions.unsubscribe();
        }
    }

    // ============================================================
    //    ..... PRESENTER IMPLEMENTATION ....
    // ============================================================

    @Override
    public void initUserDataInView()
    {
        ((EditText) mAcView.findViewById(R.id.reg_usuario_email_editT)).setText(oldUser.getUserName());
        ((EditText) mAcView.findViewById(R.id.reg_usuario_alias_ediT)).setText(oldUser.getAlias());
        ((EditText) mAcView.findViewById(R.id.user_data_ac_password_ediT))
                .setHint(R.string.user_data_ac_password_hint);
    }

    public UserChangeToMake getDataChangedFromAcView()
    {
        Timber.d("getDataChangedFromAcView()");
        UsuarioBean bean = new UsuarioBean(
                ((EditText) mAcView.findViewById(R.id.reg_usuario_email_editT)).getText()
                        .toString(),
                ((EditText) mAcView.findViewById(R.id.reg_usuario_alias_ediT)).getText()
                        .toString(),
                ((EditText) mAcView.findViewById(R.id.user_data_ac_password_ediT)).getText()
                        .toString(),
                null
        );

        StringBuilder errorBuilder = getErrorMsgBuilder(this);

        if (!bean.validateWithOnePassword(getResources(), errorBuilder)) {
            makeToast(this, errorBuilder.toString(), deep_purple_100);
            return nothing;
        }
        if (!ConnectionUtils.isInternetConnected(this)) {
            makeToast(this, R.string.no_internet_conn_toast);
            return nothing;
        }

        if (oldUser.getAlias().equals(bean.getAlias()) && oldUser.getUserName().equals(bean.getUserName())) {
            return nothing;
        }

        // Inicializao password en el antiguo usuario.
        oldUser = new Usuario.UsuarioBuilder()
                .copyUsuario(oldUser)
                .password(bean.getPassword())
                .build();
        // Inicializo datos en nuevo usuario, salvo PK.
        newUser = new Usuario.UsuarioBuilder().copyUsuario(bean.getUsuario())
                .uId(oldUser.getuId())
                .build();

        if (!oldUser.getAlias().equals(newUser.getAlias()) && oldUser.getUserName().equals(newUser.getUserName())) {
            return alias_only;
        }

        if (!oldUser.getUserName().equals(newUser.getUserName())) {
            return userName;
        }

        return nothing;
    }

    // ============================================================
    //    ..... CONTROLLER IMPLEMENTATION ....
    // ============================================================

    @SuppressWarnings("AnonymousInnerClassMayBeStatic")
    @Override
    public void loadUserData()
    {
        Timber.d("loadUserData()");
        subscriptions.add(
                getUserDataSingle()
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribe(new ActivitySubscriber<Usuario, UserDataAc>(this) {
                            @Override
                            public void onNext(Usuario usuario)
                            {
                                Timber.d("onNext()");
                                oldUser = usuario;
                                initUserDataInView();
                            }
                        })
        );
    }

    @Override
    public void modifyUserName()
    {
        Timber.d("modifyUserName()");
        subscriptions.add(
                tokenAndUserModified(oldUser, newUser)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new UserDataUpdateSubscriber(this))
        );
    }

    @Override
    public void modifyOnlyAlias()
    {
        Timber.d("modifyOnlyAlias()");
        subscriptions.add(
                aliasModified(newUser)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new UserDataUpdateSubscriber(this))
        );
    }
}
