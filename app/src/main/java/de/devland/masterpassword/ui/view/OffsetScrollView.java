package de.devland.masterpassword.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by deekay on 28/02/15.
 */
public class OffsetScrollView extends ScrollView {
    private int scrollOffset = 0;

    public OffsetScrollView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollOffset(final int scrollOffset) {
        this.scrollOffset = scrollOffset;
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(final Rect rect) {
        // adjust by scroll offset
        int scrollDelta = super.computeScrollDeltaToGetChildRectOnScreen(rect);
        int newScrollDelta = (int) Math.signum(scrollDelta) * (scrollDelta + this.scrollOffset);
        return newScrollDelta;
    }
}
