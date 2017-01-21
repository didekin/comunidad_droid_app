package com.didekindroid.usuario.userdata;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.didekin.http.ErrorBean;
import com.didekin.usuario.Usuario;
import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.UsuarioBean;
import com.didekindroid.util.MenuRouter;
import com.didekindroid.util.UIutils;

import java.util.Objects;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.didekin.http.GenericExceptionMsg.BAD_REQUEST;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.usuario.userdata.UserDataReactor.userDataReactor;
import static com.didekindroid.util.ItemMenu.mn_handler;
import static com.didekindroid.util.MenuRouter.doUpMenu;
import static com.didekindroid.util.UIutils.doToolBar;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.util.UIutils.makeToast;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;

/**
 * Preconditions:
 * 1. Registered user.
 * Postconditions:
 * 1. Regitered user with modified data. Once done, it goes to SeeUserComuByUserAc.
 */
public class UserDataAc extends AppCompatActivity implements UserDataControllerIf, UserDataViewIf {

    View mAcView;
    Usuario oldUser;
    Usuario newUser;
    UsuarioBean usuarioBean;
    CompositeDisposable subscriptions;
    UserDataReactorIf reactor;

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

        // Initialize user data.
        reactor = userDataReactor;
        loadUserData();

        Button mModifyButton = (Button) findViewById(R.id.user_data_modif_button);
        mModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("mModifyButton.OnClickListener().onClick()");
                if (checkLoginData()) {
                    modifyUserData(whatDataChangeToMake());

                    /*UserChangeToMake changeToMake = whatDataChangeToMake();
                    if (changeToMake == UserChangeToMake.alias_only) {
                        modifyOnlyAlias();
                    }
                    if (changeToMake == UserChangeToMake.userName) {
                        modifyUserData();
                    }
                    // TODO: esto hay que moverlo al onSuccess.
                    Intent intent = new Intent(UserDataAc.this, routerMap.get(UserDataAc.this.getClass()));
                    startActivity(intent);*/
                }
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        Timber.d("onCreate()");
        super.onDestroy();
        if (subscriptions != null) {
            subscriptions.clear();
        }
    }

    // ============================================================
    //    ..... PRESENTER IMPLEMENTATION ....
    // ============================================================

    @Override
    public void initUserDataInView()
    {
        Timber.d("initUserDataInView()");
        ((EditText) mAcView.findViewById(R.id.reg_usuario_email_editT)).setText(oldUser.getUserName());
        ((EditText) mAcView.findViewById(R.id.reg_usuario_alias_ediT)).setText(oldUser.getAlias());
        ((EditText) mAcView.findViewById(R.id.user_data_ac_password_ediT))
                .setHint(R.string.user_data_ac_password_hint);
    }

    @Override
    public String[] getDataChangedFromView()
    {

        Timber.d("getDataChangedFromView()");
        return new String[]{
                ((EditText) mAcView.findViewById(R.id.reg_usuario_email_editT)).getText().toString(),
                ((EditText) mAcView.findViewById(R.id.reg_usuario_alias_ediT)).getText().toString(),
                ((EditText) mAcView.findViewById(R.id.user_data_ac_password_ediT)).getText().toString()
        };
    }

    // ============================================================
    //    ..... CONTROLLER IMPLEMENTATION ....
    // ============================================================

    @Override
    public void loadUserData()
    {
        Timber.d("loadUserData()");
        reactor.getUserDataRemote(this);
    }

    @Override
    public void processBackGetUserData(Usuario usuario)
    {
        Timber.d("processBackGetUserData()");
        oldUser = usuario;
        initUserDataInView();
    }

    @Override
    public void processBackErrorInReactor(Throwable e)
    {
        if (e instanceof UiException) {
            UiException ui = (UiException) e;
            if (ui.getErrorBean().getMessage().equals(BAD_REQUEST.getHttpMessage())) {
                makeToast(this, R.string.password_wrong);
                if (isDestroyed() || isFinishing()) {
                    recreate();
                }
            } else {
                ui.processMe(this, new Intent());
            }
        } else {
            new UiException(ErrorBean.GENERIC_ERROR).processMe(this, new Intent());
        }
    }

    @Override
    public boolean checkLoginData()
    {  // TODO: test.
        Timber.d("checkLoginData()");
        usuarioBean = new UsuarioBean(
                getDataChangedFromView()[0],
                getDataChangedFromView()[1],
                getDataChangedFromView()[2],
                null
        );

        StringBuilder errorBuilder = getErrorMsgBuilder(this);

        if (!usuarioBean.validateLoginData(getResources(), errorBuilder)) {
            makeToast(this, errorBuilder.toString(), R.color.deep_purple_100);
            return false;
        }
        return !UIutils.checkInternet(this);
    }

    public UserChangeToMake whatDataChangeToMake()
    {
        Timber.d("whatDataChangeToMake()");

        if (oldUser.getAlias().equals(usuarioBean.getAlias()) && oldUser.getUserName().equals(usuarioBean.getUserName())) {
            return UserChangeToMake.nothing;
        }

        // Inicializao password en el antiguo usuario.
        oldUser = new Usuario.UsuarioBuilder()
                .copyUsuario(oldUser)
                .password(usuarioBean.getPassword())
                .build();
        // Inicializo datos en nuevo usuario, salvo PK.
        newUser = new Usuario.UsuarioBuilder().copyUsuario(usuarioBean.getUsuario())
                .uId(oldUser.getuId())
                .build();

        if (!oldUser.getAlias().equals(newUser.getAlias()) && oldUser.getUserName().equals(newUser.getUserName())) {
            return UserChangeToMake.alias_only;
        }

        if (!oldUser.getUserName().equals(newUser.getUserName())) {
            return UserChangeToMake.userName;
        }

        return UserChangeToMake.nothing;
    }

    @Override
    public void modifyUserData(UserChangeToMake userChangeToMake)
    {
        Timber.d("modifyUserData()");
        reactor.modifyUserInRemote(userChangeToMake, oldUser, newUser);

        subscriptions.add(
                UserDataReactor.tokenAndUserModified(oldUser, newUser)
                        .subscribeOn(Schedulers.io())
                        .observeOn(mainThread())
                        .subscribe(new UserDataReactor.UserDataUpdateSingleObserver(this))
        );
    }

    @Override
    public void modifyOnlyAlias()
    {
        Timber.d("modifyOnlyAlias()");
        subscriptions.add(
                UserDataReactor.aliasModified(newUser)
                        .subscribeOn(Schedulers.io())
                        .observeOn(mainThread())
                        .subscribe(new UserDataReactor.UserDataUpdateSingleObserver(this))
        );
    }

    @Override
    public CompositeDisposable getSubscriptions()
    {
        Timber.d("getSubscriptions()");
        if (subscriptions == null) {
            subscriptions = new CompositeDisposable();
        }
        return subscriptions;
    }

//    ============================================================
//    ..... ACTION BAR ....
/*    ============================================================*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Timber.d("onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.user_data_ac_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        Timber.d("onPrepareOptionsMenu()");
        // Mostramos el menú si el usuario está registrado. TODO: probar.
        if (TKhandler.isRegisteredUser()) {
            menu.findItem(R.id.see_usercomu_by_user_ac_mn).setVisible(true).setEnabled(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");

        int resourceId = item.getItemId();
        switch (resourceId) {
            case android.R.id.home:
                doUpMenu(this);
                return true;
            case R.id.password_change_ac_mn:
            case R.id.delete_me_ac_mn:
            case R.id.see_usercomu_by_user_ac_mn:
            case R.id.comu_search_ac_mn:
            case R.id.incid_see_open_by_comu_ac_mn:
                mn_handler.doMenuItem(this, MenuRouter.routerMap.get(resourceId));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
