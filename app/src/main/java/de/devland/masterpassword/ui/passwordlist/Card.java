package de.devland.masterpassword.ui.passwordlist;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by deekay on 07.06.2015.
 */
@NoArgsConstructor
public abstract class Card {
    @Getter
    protected int layoutId;

    public Card(int layoutId) {
        this.layoutId = layoutId;
    }

    public abstract void bindViewHolder(CardAdapter.CardViewHolder cardViewHolder);

    public abstract long getId();

    public String getTitle() {
        return "";
    }


}
