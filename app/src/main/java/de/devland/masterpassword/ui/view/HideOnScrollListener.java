package de.devland.masterpassword.ui.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import de.devland.masterpassword.shared.util.Utils;

/**
 * Created by deekay on 08.06.2015.
 */
public class HideOnScrollListener extends RecyclerView.OnScrollListener {
    private static final int HIDE_THRESHOLD = 20;
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;

    private final View view;
    private final float amountDp;
    private float originalY;

    public HideOnScrollListener(View view, float amountDp) {
        this.view = view;
        this.amountDp = amountDp;

        originalY = view.getY();
    }


    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
            onHide();
            controlsVisible = false;
            scrolledDistance = 0;
        } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
            onShow();
            controlsVisible = true;
            scrolledDistance = 0;
        }

        if((controlsVisible && dy>0) || (!controlsVisible && dy<0)) {
            scrolledDistance += dy;
        }
    }

    public void onHide() {
        if (originalY == 0) {
            originalY = view.getY();
        }
        view.animate().y(originalY + Utils.convertDpToPixel(amountDp, view.getContext())).setDuration(200);
    }
    public void onShow() {
        view.animate().y(originalY).setDuration(200);
    }
}
