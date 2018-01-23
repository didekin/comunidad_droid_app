package com.didekindroid.usuariocomunidad.testutil;

import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import retrofit2.Call;

import static com.didekindroid.AppInitializer.creator;

/**
 * User: pedro@didekin
 * Date: 21/11/16
 * Time: 19:15
 */

@SuppressWarnings("WeakerAccess")
public final class UserComuMockDaoRemote implements UserComuMockEndPoints {

    public static final UserComuMockDaoRemote userComuMockDao = new UserComuMockDaoRemote();
    private final UserComuMockEndPoints endPoint;

    private UserComuMockDaoRemote()
    {
        this(creator.get().getRetrofitHandler().getService(UserComuMockEndPoints.class));
    }

    public UserComuMockDaoRemote(UserComuMockEndPoints endPoint)
    {
        this.endPoint = endPoint;
    }

    @Override
    public Call<Boolean> deleteUser(String userName)
    {
        return endPoint.deleteUser(userName);
    }

    @Override
    public Call<Boolean> regComuAndUserAndUserComu(UsuarioComunidad usuarioCom)
    {
        return endPoint.regComuAndUserAndUserComu(usuarioCom);
    }

    @Override
    public Call<Boolean> regUserAndUserComu(UsuarioComunidad userCom)
    {
        return endPoint.regUserAndUserComu(userCom);
    }
}
