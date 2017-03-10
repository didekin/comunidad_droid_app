package com.didekindroid.api;

import android.os.Bundle;
import android.widget.AdapterView;

/**
 * User: pedro@didekin
 * Date: 20/02/17
 * Time: 10:11
 */

public interface ViewerWithSelectIf<T extends AdapterView,B> extends ManagerIf.ViewerIf<T,B> {

    ViewerWithSelectIf<T,B> initSelectedIndex(Bundle savedState);
    void saveSelectedIndex(Bundle savedState);
}
