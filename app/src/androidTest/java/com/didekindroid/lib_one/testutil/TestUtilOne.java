package com.didekindroid.lib_one.testutil;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.CtrlerSelectListIf;
import com.didekindroid.lib_one.api.ViewerSelectListIf;
import com.didekindroid.lib_one.util.BundleKey;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;

import java.io.Serializable;
import java.util.List;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 11/02/2018
 * Time: 15:22
 */

public class TestUtilOne {

    public static <E extends Serializable> void checkSpinnerCtrlerLoadItems(CtrlerSelectListIf<E> controller, Long... entityId)
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            Timber.d("checkSpinnerCtrlerLoadItems(), Thread: %s", Thread.currentThread().getName());
            assertThat(controller.loadItemsByEntitiyId(new DisposableSingleObserver<List<E>>() {
                @Override
                public void onSuccess(List<E> es)
                {
                }

                @Override
                public void onError(Throwable e)
                {
                    fail();
                }
            }, entityId), is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
    }

    public static void checkSavedStateWithItemSelected(ViewerSelectListIf viewer, BundleKey bundleKey)
    {
        viewer.setSelectedItemId(18L);
        Bundle bundle = new Bundle(1);
        viewer.saveState(bundle);
        Assert.assertThat(bundle.getLong(bundleKey.getKey()), CoreMatchers.is(18L));
    }

    public static View doFragmentTextView(int resourdeIdLayout, String description)
    {
        LayoutInflater inflater = (LayoutInflater) getTargetContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View frView = inflater.inflate(resourdeIdLayout, null);
        EditText editText = frView.findViewById(R.id.incid_reg_desc_ed);
        editText.setText(description);
        return frView;
    }
}
