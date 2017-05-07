package com.didekindroid.api;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.io.Serializable;
import java.util.List;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 18/04/17
 * Time: 13:25
 */
@SuppressWarnings("AbstractClassExtendsConcreteClass")
public abstract class CtrlerSelectionList<E extends Serializable> extends Controller
        implements CtrlerSelectionListIf<E> {

    private final ViewerSelectionList<? extends AdapterView<? super ArrayAdapter<E>>, CtrlerSelectionList<E>, E> viewerCast;

    protected CtrlerSelectionList(ViewerSelectionList<? extends AdapterView, CtrlerSelectionList<E>, E> viewer)
    {
        super(viewer);
        viewerCast = viewer;
    }

    @Override
    public void onSuccessLoadItemsInList(List<E> itemList)
    {
        Timber.d("onSuccessLoadItemsInList()");
        viewerCast.onSuccessLoadItems(itemList);
    }
}
