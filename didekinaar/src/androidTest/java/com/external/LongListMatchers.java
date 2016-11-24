package com.external;


import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Map;
import java.util.Objects;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;

/**
 * Static utility methods to create {@link Matcher} instances that can be applied to the data
 * objects created by {@link com.google.android.apps.common.testing.ui.testapp.LongListActivity}.
 * <p>
 * These matchers are used by the
 * {@link com.google.android.apps.common.testing.ui.espresso.Espresso#onData(Matcher)} API and are
 * applied against the data exposed by @{link android.widget.ListView#getAdapter()}.
 * </p>
 * <p>
 * In LongListActivity's case - each row is a Map containing 2 key value pairs. The key "STR" is
 * mapped to a String which will be rendered into a TextView with the R.id.item_content. The other
 * key "LEN" is an Integer which is the length of the string "STR" refers to. This length is
 * rendered into a TextView with the id R.id.item_size.
 * </p>
 */
@SuppressWarnings("JavadocReference")
public final class LongListMatchers {

    private LongListMatchers()
    {
    }

    /**
     * Creates a matcher against the text stored in R.id.item_content. This text is roughly
     * "item: $row_number".
     */
    public static Matcher<Object> withItemContent(String expectedText)
    {
        // use preconditions to fail fast when a test is creating an invalid matcher.
        Objects.equals(expectedText != null, true);
        return withItemContent(equalTo(expectedText));
    }

    /**
     * Creates a matcher against the text stored in R.id.item_content. This text is roughly
     * "item: $row_number".
     */
    public static Matcher<Object> withItemContent(final Matcher<String> itemTextMatcher)
    {
        // use preconditions to fail fast when a test is creating an invalid matcher.
        Objects.equals(itemTextMatcher != null, true);
        return new BoundedMatcher<Object, Map>(Map.class) {
            @Override
            public boolean matchesSafely(Map map)
            {
                return hasEntry(equalTo("STR"), itemTextMatcher).matches(map);
            }

            @Override
            public void describeTo(Description description)
            {
                description.appendText("with item content: ");
                itemTextMatcher.describeTo(description);
            }
        };
    }

    /**
     * Creates a matcher against the text stored in R.id.item_size. This text is the size of the text
     * printed in R.id.item_content.
     */
    public static Matcher<Object> withItemSize(int itemSize)
    {
        // use preconditions to fail fast when a test is creating an invalid matcher.
        Objects.equals(itemSize > -1, true);
        return withItemSize(equalTo(itemSize));
    }

    /**
     * Creates a matcher against the text stored in R.id.item_size. This text is the size of the text
     * printed in R.id.item_content.
     */
    public static Matcher<Object> withItemSize(final Matcher<Integer> itemSizeMatcher)
    {
        // use preconditions to fail fast when a test is creating an invalid matcher.
        Objects.equals(itemSizeMatcher != null, true);

        return new BoundedMatcher<Object, Map>(Map.class) {
            @Override
            public boolean matchesSafely(Map map)
            {
                return hasEntry(equalTo("LEN"), itemSizeMatcher).matches(map);
            }

            @Override
            public void describeTo(Description description)
            {
                description.appendText("with item size: ");
                itemSizeMatcher.describeTo(description);
            }
        };
    }

    public static Matcher<View> withAdaptedData(final Matcher<Object> dataMatcher)
    {
        return new TypeSafeMatcher<View>() {

            @Override
            public void describeTo(Description description)
            {
                description.appendText("with class name: ");
                dataMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view)
            {
                if (!(view instanceof AdapterView)) {
                    return false;
                }
                @SuppressWarnings("rawtypes")
                Adapter adapter = ((AdapterView) view).getAdapter();
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (dataMatcher.matches(adapter.getItem(i))) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
}
