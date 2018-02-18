package com.didekindroid.lib_one.api;

import android.support.annotation.NonNull;
import android.view.View;

import java.util.List;

/**
 * User: pedro@didekin
 * Date: 08/05/17
 * Time: 14:29
 * <p>
 * Interface implemented by viewers (parent viewers) with others viewers as fields (child viewers).
 * Child viewers are injected in parent viewers by Android components, as activities, implementing InjectorOfParentViewerIf interface.
 */

public interface ParentViewerIf<T extends View, C extends ControllerIf> extends
        ViewerIf<T, C> {

    void setChildViewer(@NonNull ViewerIf childViewer);

    <H extends ViewerIf> H getChildViewer(@NonNull Class<H> viewerChildClass);

    <H extends ViewerIf> List<H> getChildViewersFromSuperClass(@NonNull Class<H> viewerChildClass);
}
