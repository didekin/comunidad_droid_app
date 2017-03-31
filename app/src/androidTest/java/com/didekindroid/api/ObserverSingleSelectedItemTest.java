package com.didekindroid.api;

import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Single;

import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 28/03/17
 * Time: 09:30
 */
@RunWith(AndroidJUnit4.class)
public class ObserverSingleSelectedItemTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    ObserverSingleSelectedItem<Integer> observerSingleSelectedItem;

    @Before
    public void setUp(){
        observerSingleSelectedItem = new ObserverSingleSelectedItem<>(new CtrlerSelectItemForTest(new ViewerMock<View,CtrlerSelectableItemIf>(null,null,null)));
    }

    @Test
    public void onSuccess() throws Exception
    {
        Single.just(111).subscribeWith(observerSingleSelectedItem);
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }

    //    =========================== HELPERS =============================

   static class CtrlerSelectItemForTest extends CtrlerIdentity<View> implements
           CtrlerSelectableItemIf<String,Integer> {

       protected CtrlerSelectItemForTest(ViewerIf<View, ? extends ControllerIf> viewer)
       {
           super(viewer);
       }

       @Override
       public boolean selectItem(String item)
       {
           return false;
       }

       @Override   // IN TEST.
       public void onSuccessSelectedItem(Integer itemBack)
       {
           assertThat(itemBack, is(111));
           assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
       }

       @Override
       public boolean loadItemsByEntitiyId(long entityId)
       {
           return false;
       }

       @Override
       public void onSuccessLoadItemsById(List<String> itemList)
       {
       }
   }
}