package de.devland.masterpassword.ui.passwordlist;

import de.devland.masterpassword.R;

/**
 * Created by deekay on 07.06.2015.
 */
public class DummyCard extends Card {
    public DummyCard() {
        super(R.layout.card_dummy);
    }

    @Override
    public void bindViewHolder(CardAdapter.CardViewHolder cardViewHolder) {
    }

    @Override
    public boolean isVisible(String filter) {
        return true;
    }

    @Override
    public long getId() {
        return Long.MAX_VALUE;
    }
}
