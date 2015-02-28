/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Changes made by David Kunzler:
 * - removed special handling for row image
 * - changed to work with SiteCardArrayAdapter
 */

package de.devland.masterpassword.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.devland.masterpassword.util.SiteCardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

/**
 * This ListView displays a set of ListItemObjects. By calling addRow with a new
 * ListItemObject, it is added to the top of the ListView and the new row is animated
 * in. If the ListView content is at the top (the scroll offset is 0), the animation of
 * the new row is accompanied by an extra image animation that pops into place in its
 * corresponding item in the ListView.
 */
public class InsertionAnimationCardListView extends CardListView {

    private static final int NEW_ROW_DURATION = 400;

    private List<BitmapDrawable> mCellBitmapDrawables;

    public InsertionAnimationCardListView(Context context) {
        super(context);
        init();
    }

    public InsertionAnimationCardListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InsertionAnimationCardListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        setDivider(null);
        mCellBitmapDrawables = new ArrayList<>();
    }

    /**
     * Modifies the underlying data set and adapter through the addition of the new object
     * to the first item of the ListView. The new cell is then animated into place from
     * above the bounds of the ListView.
     */
    public void addRow(SiteCard newObj, final AnimatorListenerAdapter animatorListenerAdapter) {

        final SiteCardArrayAdapter adapter = (SiteCardArrayAdapter) getAdapter();

        /**
         * Stores the starting bounds and the corresponding bitmap drawables of every
         * cell present in the ListView before the data set change takes place.
         */
        final HashMap<Long, Rect> listViewItemBounds = new HashMap<>();
        final HashMap<Long, BitmapDrawable> listViewItemDrawables = new HashMap<>();

        int firstVisiblePosition = getFirstVisiblePosition();
        for (int i = 0; i < getChildCount(); ++i) {
            View child = getChildAt(i);
            int position = firstVisiblePosition + i;
            long itemID = adapter.getItemId(position);
            Rect startRect = new Rect(child.getLeft(), child.getTop(), child.getRight(),
                    child.getBottom());
            listViewItemBounds.put(itemID, startRect);
            listViewItemDrawables.put(itemID, getBitmapDrawableFromView(child));
        }

        /** Adds the new object to the data set, thereby modifying the adapter,
         *  as well as adding a stable Id for that specified object.*/
        adapter.insert(newObj, 0);
        adapter.notifyDataSetChanged();

        final ViewTreeObserver observer = getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);

                ArrayList<Animator> animations = new ArrayList<>();

                final View newCell = getChildAt(0);

                int firstVisiblePosition = getFirstVisiblePosition();
                final boolean shouldAnimateInNewRow = shouldAnimateInNewRow();

                if (shouldAnimateInNewRow) {
                    /** Fades in the text of the first cell. */
                    ObjectAnimator cellAlphaAnimator = ObjectAnimator.ofFloat(newCell,
                            View.ALPHA, 0.0f, 1.0f);
                    animations.add(cellAlphaAnimator);

                    /** Animates in the extra hover view corresponding to the image
                     * in the top row of the ListView. */
                }

                /** Loops through all the current visible cells in the ListView and animates
                 * all of them into their post layout positions from their original positions.*/
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    int position = firstVisiblePosition + i;
                    long itemId = adapter.getItemId(position);
                    Rect startRect = listViewItemBounds.get(itemId);
                    int top = child.getTop();
                    if (startRect != null) {
                        /** If the cell was visible before the data set change and
                         * after the data set change, then animate the cell between
                         * the two positions.*/
                        int startTop = startRect.top;
                        int delta = startTop - top;
                        ObjectAnimator animation = ObjectAnimator.ofFloat(child,
                                View.TRANSLATION_Y, delta, 0);
                        animations.add(animation);
                    } else {
                        /** If the cell was not visible (or present) before the data set
                         * change but is visible after the data set change, then use its
                         * height to determine the delta by which it should be animated.*/
                        int childHeight = child.getHeight() + getDividerHeight();
                        int startTop = top + (i > 0 ? childHeight : -childHeight);
                        int delta = startTop - top;
                        ObjectAnimator animation = ObjectAnimator.ofFloat(child,
                                View.TRANSLATION_Y, delta, 0);
                        animations.add(animation);
                    }
                    listViewItemBounds.remove(itemId);
                    listViewItemDrawables.remove(itemId);
                }

                /**
                 * Loops through all the cells that were visible before the data set
                 * changed but not after, and keeps track of their corresponding
                 * drawables. The bounds of each drawable are then animated from the
                 * original state to the new one (off the screen). By storing all
                 * the drawables that meet this criteria, they can be redrawn on top
                 * of the ListView via dispatchDraw as they are animating.
                 */
                for (Long itemId : listViewItemBounds.keySet()) {
                    BitmapDrawable bitmapDrawable = listViewItemDrawables.get(itemId);
                    Rect startBounds = listViewItemBounds.get(itemId);
                    bitmapDrawable.setBounds(startBounds);

                    int childHeight = startBounds.bottom - startBounds.top + getDividerHeight();
                    Rect endBounds = new Rect(startBounds);
                    endBounds.offset(0, childHeight);

                    ObjectAnimator animation = ObjectAnimator.ofObject(bitmapDrawable,
                            "bounds", sBoundsEvaluator, startBounds, endBounds);
                    animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        private Rect mLastBound = null;
                        private Rect mCurrentBound = new Rect();

                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            Rect bounds = (Rect) valueAnimator.getAnimatedValue();
                            mCurrentBound.set(bounds);
                            if (mLastBound != null) {
                                mCurrentBound.union(mLastBound);
                            }
                            mLastBound = bounds;
                            invalidate(mCurrentBound);
                        }
                    });

                    listViewItemBounds.remove(itemId);
                    listViewItemDrawables.remove(itemId);

                    mCellBitmapDrawables.add(bitmapDrawable);

                    animations.add(animation);
                }

                /** Animates all the cells from their old position to their new position
                 *  at the same time.*/
                setEnabled(false);
                AnimatorSet set = new AnimatorSet();
                set.setDuration(NEW_ROW_DURATION);
                set.playTogether(animations);
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mCellBitmapDrawables.clear();
                        setEnabled(true);
                        invalidate();
                        if (animatorListenerAdapter != null) {
                            animatorListenerAdapter.onAnimationEnd(animation);
                        }
                    }
                });
                set.start();

                listViewItemBounds.clear();
                listViewItemDrawables.clear();
                return true;
            }
        });
    }

    /**
     * By overriding dispatchDraw, the BitmapDrawables of all the cells that were on the
     * screen before (but not after) the layout are drawn and animated off the screen.
     */
    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mCellBitmapDrawables.size() > 0) {
            for (BitmapDrawable bitmapDrawable : mCellBitmapDrawables) {
                bitmapDrawable.draw(canvas);
            }
        }
    }

    public boolean shouldAnimateInNewRow() {
        int firstVisiblePosition = getFirstVisiblePosition();
        return (firstVisiblePosition == 0);
    }

    /**
     * Returns a bitmap drawable showing a screenshot of the view passed in.
     */
    private BitmapDrawable getBitmapDrawableFromView(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return new BitmapDrawable(getResources(), bitmap);
    }

    /**
     * This TypeEvaluator is used to animate the position of a BitmapDrawable
     * by updating its bounds.
     */
    static final TypeEvaluator<Rect> sBoundsEvaluator = new TypeEvaluator<Rect>() {
        public Rect evaluate(float fraction, Rect startValue, Rect endValue) {
            return new Rect(interpolate(startValue.left, endValue.left, fraction),
                    interpolate(startValue.top, endValue.top, fraction),
                    interpolate(startValue.right, endValue.right, fraction),
                    interpolate(startValue.bottom, endValue.bottom, fraction));
        }

        public int interpolate(int start, int end, float fraction) {
            return (int) (start + fraction * (end - start));
        }
    };

}
