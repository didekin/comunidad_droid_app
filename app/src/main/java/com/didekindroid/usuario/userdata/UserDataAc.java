package com.didekindroid.usuario.userdata;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.security.IdentityCacher;
import com.didekindroid.usuario.UsuarioBean;
import com.didekindroid.util.MenuRouter;
import com.didekindroid.util.UIutils;
import com.didekinlib.http.ErrorBean;
import com.didekinlib.model.usuario.Usuario;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_name_password_should_be_initialized;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_should_be_registered;
import static com.didekindroid.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.usuario.userdata.UserDataControllerIf.UserChangeToMake.alias_only;
import static com.didekindroid.usuario.userdata.UserDataControllerIf.UserChangeToMake.nothing;
import static com.didekindroid.usuario.userdata.UserDataControllerIf.UserChangeToMake.userName;
import static com.didekindroid.usuario.userdata.UserDataReactor.userDataReactor;
import static com.didekindroid.util.DefaultNextAcRouter.routerMap;
import static com.didekindroid.util.ItemMenu.mn_handler;
import static com.didekindroid.util.MenuRouter.doUpMenu;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.destroySubscriptions;
import static com.didekindroid.util.UIutils.doToolBar;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.util.UIutils.makeToast;
import static com.didekinlib.http.GenericExceptionMsg.BAD_REQUEST;
import static com.didekinlib.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;

/**
 * Preconditions:
 * 1. Registered user.
 * Postconditions:
 * 1. Registered user with modified data. Once done, it goes to SeeUserComuByUserAc.
 * 2. An intent is created for menu options with the old user data, once they have been loaded.
 */
public class UserDataAc extends AppCompatActivity implements UserDataControllerIf, UserDataViewIf {

    View mAcView;
    Usuario oldUser;
    Usuario newUser;
    UsuarioBean usuarioBean;
    CompositeDisposable subscriptions;
    UserDataReactorIf reactor;
    IdentityCacher identityCacher;
    Intent intentForMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate()");

        // Initialize tokenCacher.
        identityCacher = TKhandler;
        // Preconditions.
        assertTrue(identityCacher.isRegisteredUser(), user_should_be_registered);
        // Initialize user data.
        reactor = userDataReactor;
        loadUserData();

        mAcView = getLayoutInflater().inflate(R.layout.user_data_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);

        Button mModifyButton = (Button) findViewById(R.id.user_data_modif_button);
        mModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("mModifyButton.OnClickListener().onClick()");
                if (checkLoginData()) {
                    modifyUserData(whatDataChangeToMake());
                }
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        Timber.d("onCreate()");
        super.onDestroy();
        destroySubscriptions(subscriptions);
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
        reactor.getUserInRemote(this);
    }

    @Override
    public boolean checkLoginData()
    {
        Timber.d("checkLoginData()");
        usuarioBean = new UsuarioBean(
                getDataChangedFromView()[0],
                getDataChangedFromView()[1],
                getDataChangedFromView()[2],
                null
        );

        StringBuilder errorBuilder = getErrorMsgBuilder(this);

        if (!usuarioBean.validateLoginData(getResources(), errorBuilder)) {
            makeToast(this, errorBuilder.toString());
            return false;
        }
        return !UIutils.checkInternet(this);
    }

    public UserChangeToMake whatDataChangeToMake()
    {
        Timber.d("whatDataChangeToMake()");

        if (oldUser.getAlias().equals(usuarioBean.getAlias()) && oldUser.getUserName().equals(usuarioBean.getUserName())) {
            return nothing;
        }

        // Inicializo password en el antiguo usuario.
        oldUser = new Usuario.UsuarioBuilder()
                .copyUsuario(oldUser)
                .password(usuarioBean.getPassword())
                .build();
        // Inicializo datos en nuevo usuario, salvo PK.
        newUser = new Usuario.UsuarioBuilder().copyUsuario(usuarioBean.getUsuario())
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

    @Override
    public void modifyUserData(UserChangeToMake userChangeToMake)
    {
        Timber.d("modifyUserData()");
        reactor.modifyUserInRemote(this, userChangeToMake, oldUser, newUser);
    }

    @Override
    public void processBackUserDataLoaded(Usuario usuario)
    {
        Timber.d("processBackUserDataLoaded()");
        oldUser = usuario;
        initUserDataInView();
        intentForMenu = new Intent().putExtra(user_name.key, oldUser.getUserName());
        // Force update of intent in menu.
        invalidateOptionsMenu();
    }

    /**
     * Preconditions: newUser has been initialized with new user name and password.
     */
    @Override
    public void processBackUserDataUpdated(boolean toInitTokenCache)
    {
        Timber.d("processBackUserDataUpdated()");
        assertTrue(newUser.getUserName() != null && newUser.getPassword() != null, user_name_password_should_be_initialized);
        if (toInitTokenCache) {
            reactor.updateAndInitTokenCache(newUser);
        }
        processBackGenericUpdated();
    }

    @Override
    public void processBackGenericUpdated()
    {
        Timber.d("processBackGenericUpdated()");
        Intent intent = new Intent(UserDataAc.this, routerMap.get(UserDataAc.this.getClass()));
        startActivity(intent);
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
            new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR)).processMe(this, new Intent());
        }
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
        // Mostramos el menú si el usuario está registrado. TODO: probar esto y actualización intent.
        if (identityCacher.isRegisteredUser()) {
            menu.findItem(R.id.see_usercomu_by_user_ac_mn).setVisible(true).setEnabled(true);
        }
        // Update intent in activity with user data.
        if (intentForMenu != null){
            setIntent(intentForMenu);
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
