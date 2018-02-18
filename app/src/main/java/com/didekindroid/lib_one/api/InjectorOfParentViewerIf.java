package com.didekindroid.lib_one.api;

/**
 * User: pedro@didekin
 * Date: 03/04/17
 * Time: 15:38
 */

public interface InjectorOfParentViewerIf {

    ParentViewerIf getInjectedParentViewer();

    void setChildInParentViewer(ViewerIf childViewer);
}
