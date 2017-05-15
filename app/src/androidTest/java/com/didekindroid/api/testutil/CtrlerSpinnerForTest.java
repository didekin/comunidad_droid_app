package com.didekindroid.api.testutil;

import android.widget.AdapterView;

import com.didekindroid.api.CtrlerSelectionList;
import com.didekindroid.api.ViewerSelectionList;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_WITH_EXCEPTION_EXEC;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 11/05/17
 * Time: 12:42
 */
public class CtrlerSpinnerForTest extends CtrlerSelectionList<String> {

    public final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    public CtrlerSpinnerForTest(ViewerSelectionList<? extends AdapterView, CtrlerSelectionList<String>, String> viewer)
    {
        super(viewer);
    }

    @Override
    public void onSuccessLoadItemsInList(List<String> itemList)
    {
        assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
    }

    @Override
    public void onErrorCtrl(Throwable e)
    {
        assertThat(flagMethodExec.getAndSet(AFTER_METHOD_WITH_EXCEPTION_EXEC), is(BEFORE_METHOD_EXEC));
    }

    @Override
    public boolean loadItemsByEntitiyId(Long... entityId)
    {
        fail();
        return false;
    }
}
