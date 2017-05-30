package com.didekindroid.api;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * User: pedro@didekin
 * Date: 08/05/17
 * Time: 14:29
 */

public interface ViewerParentInjectedIf<T extends View,C extends ControllerIf> extends ViewerIf<T,C>{
    void setChildViewer(@NonNull ViewerIf childViewer);
    <H extends ViewerIf> H getChildViewer(Class<H> viewerChildClass);
}
