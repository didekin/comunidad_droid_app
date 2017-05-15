package com.didekindroid.api;

import android.app.Activity;
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
public abstract class ViewerSelectionList<T extends AdapterView<? super ArrayAdapter<E>>, C extends CtrlerSelectionListIf<E>, E extends Serializable>
        extends Viewer<T, C>
        implements ViewerSelectionListIf<T, C, E> {

    private static final int spinner_view_layout = R.layout.app_spinner_1_dropdown_item;
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

    protected ViewerSelectionList(T view, Activity activity, ViewerIf parentViewer)
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
    public void setItemSelectedId(long itemSelectedId)
    {
        Timber.d("setItemSelectedId()");
        this.itemSelectedId = itemSelectedId;
    }

    @Override
    public int getSelectedPositionFromItemId(long itemId)
    {
        Timber.d("getSelectedPositionFromItemId()");
        return (int) itemId;
    }

    @Override
    public void onSuccessLoadItems(List<E> itemsList)
    {
        Timber.d("onSuccessLoadItems()");
        ArrayAdapter<E> adapter = getArrayAdapterForSpinner(activity);
        adapter.addAll(itemsList);
        view.setAdapter(adapter);
        view.setSelection(getSelectedPositionFromItemId(itemSelectedId));
    }
}
