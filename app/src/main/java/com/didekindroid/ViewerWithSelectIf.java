package com.didekindroid;

import android.os.Bundle;
import android.widget.AdapterView;

import com.didekindroid.ManagerIf.ViewerIf;

/**
 * User: pedro@didekin
 * Date: 20/02/17
 * Time: 10:11
 */

public interface ViewerWithSelectIf<T extends AdapterView,B> extends ViewerIf<T,B> {

    ViewerWithSelectIf initSelectedIndex(Bundle savedState);
    void saveSelectedIndex(Bundle savedState);
}
