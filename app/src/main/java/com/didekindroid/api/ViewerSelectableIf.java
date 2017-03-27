package com.didekindroid.api;

import android.os.Bundle;
import android.widget.AdapterView;

/**
 * User: pedro@didekin
 * Date: 20/02/17
 * Time: 10:11
 */

public interface ViewerSelectableIf<T extends AdapterView,C extends ControllerIf> extends ViewerIf<T,C> {

    ViewerSelectableIf<T,C> initSelectedItem(Bundle savedState);
    long getSelectedItem();
    long getItemIdInIntent();
}
