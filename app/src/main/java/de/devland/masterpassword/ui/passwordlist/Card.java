package de.devland.masterpassword.ui.passwordlist;

import lombok.Getter;

/**
 * Created by deekay on 07.06.2015.
 */
public abstract class Card {
    @Getter
    protected int layoutId;

    public Card(int layoutId) {
        this.layoutId = layoutId;
    }

    public abstract void bindViewHolder(CardAdapter.CardViewHolder cardViewHolder);

    public abstract boolean isVisible(String filter);

    public String getTitle() {
        return "";
    }

}
