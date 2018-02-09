package com.didekindroid.lib_one.api;

import android.os.Bundle;

/**
 * User: pedro@didekin
 * Date: 16/09/17
 * Time: 13:39
 */

public interface ViewerManagerIf {

    void initViewers(Bundle savedInstanceState);

    void clearViewersSubscr();

    void savedStateViewers(Bundle outState);
}
