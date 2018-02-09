package com.didekindroid.lib_one.api;

/**
 * User: pedro@didekin
 * Date: 03/04/17
 * Time: 15:38
 */

public interface ChildViewersInjectorIf {

    ParentViewerInjectedIf getParentViewer();

    void setChildInParentViewer(ViewerIf childViewer);
}
