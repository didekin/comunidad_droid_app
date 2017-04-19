package com.didekindroid.api;

import android.os.Bundle;
import android.widget.AdapterView;

import java.io.Serializable;
import java.util.List;

/**
 * User: pedro@didekin
 * Date: 20/02/17
 * Time: 10:11
 */

public interface ViewerSelectionListIf<T extends AdapterView, C extends CtrlerSelectionListIf<E>,E extends Serializable> extends
        ViewerIf<T, C> {

    void onSuccessLoadItems(List<E> incidCloseList);

    void initSelectedItemId(Bundle savedState);

    long getSelectedItemId();

    int getSelectedViewFromItemId(long itemId);

    void setItemSelectedId(long itemSelectedId);
}
