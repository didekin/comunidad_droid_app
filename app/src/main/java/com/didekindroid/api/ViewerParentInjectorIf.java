package com.didekindroid.api;

/**
 * User: pedro@didekin
 * Date: 03/04/17
 * Time: 15:38
 */

public interface ViewerParentInjectorIf {

    ViewerIf getViewerAsParent();

    void setChildInViewer(ViewerIf childInViewer);
}