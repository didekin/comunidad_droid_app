package com.didekindroid.lib_one.api;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.didekindroid.R;

import java.io.Serializable;
import java.util.List;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 18/04/17
 * Time: 13:56
 */
@SuppressWarnings("AbstractClassExtendsConcreteClass")
public abstract class ViewerSelectList<T extends AdapterView<? super ArrayAdapter<E>>, C extends CtrlerSelectListIf<E>, E extends Serializable>
        extends Viewer<T, C>
        implements ViewerSelectListIf<T, C, E> {

    private static final int spinner_view_layout = R.layout.app_spinner_1_dropdown_item;     // TODO: quitar resource. Está en library.
    private static final int spinner_text_view = R.id.app_spinner_1_dropdown_item;
    /**
     * This itemId can be set, in subclasses, in three ways:
     * 1. The user selects one item in the list.
     * 2. The id is retrieved from savedInstanceState.
     * 3. The id is passed from the activity (in FCM notifications, p.e.) in a intent.
     * 4. The id is retrieved from an activity intent passed on a viewBean.
     */
    @SuppressWarnings("WeakerAccess")
    protected long itemSelectedId;

    protected ViewerSelectList(T view, Activity activity, ViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
    }

    protected ArrayAdapter<E> getArrayAdapterForSpinner(Activity activity)
    {
        Timber.d("getArrayAdapterForSpinner()");
        return new ArrayAdapter<>(activity, spinner_view_layout, spinner_text_view);
    }

    @Override
    public long getSelectedItemId()
    {
        Timber.d("getSelectedItemId()");
        return itemSelectedId;
    }

    /* Mainly for tests */
    @Override
    public void setSelectedItemId(long itemSelectedId)
    {
        Timber.d("setSelectedItemId()");
        this.itemSelectedId = itemSelectedId;
    }

    @Override
    public int getSelectedPositionFromItemId(long itemId)
    {
        Timber.d("getSelectedPositionFromItemId()");
        return (int) itemId;
    }

    @Override
    public void onSuccessLoadItemList(List<E> itemsList)
    {
        Timber.d("onSuccessLoadItemList()");
        ArrayAdapter<E> adapter = getArrayAdapterForSpinner(activity);
        adapter.addAll(itemsList);
        view.setAdapter(adapter);
        view.setSelection(getSelectedPositionFromItemId(itemSelectedId));
    }

    @Override
    public void onSuccessLoadSelectedItem(@NonNull Bundle bundle)
    {
        Timber.d("onSuccessLoadSelectedItem()");
    }
}
