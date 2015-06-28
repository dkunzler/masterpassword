package de.devland.masterpassword.ui.passwordlist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import xyz.danoz.recyclerviewfastscroller.sectionindicator.title.SectionTitleIndicator;

/**
 * Created by deekay on 24.06.2015.
 */
public class CardSectionIndicator extends SectionTitleIndicator<Card> {

    private boolean enabled = true;

    public CardSectionIndicator(Context context) {
        super(context);
    }

    public CardSectionIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CardSectionIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setSection(Card card) {
        if (card.getTitle().length() > 0) {
            setTitleText(card.getTitle().substring(0, 1).toUpperCase());
        }
    }

    @Override
    public void setVisibility(int visibility) {
        if (enabled) {
            super.setVisibility(visibility);
        } else {
            super.setVisibility(View.GONE);
        }
    }

    public void disable() {
        enabled = false;
    }

    public void enable() {
        enabled = true;
    }
}
