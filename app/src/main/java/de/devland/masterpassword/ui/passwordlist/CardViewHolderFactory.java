package de.devland.masterpassword.ui.passwordlist;

import android.view.View;

import de.devland.masterpassword.R;

/**
 * Created by deekay on 07.06.2015.
 */
public class CardViewHolderFactory {
    public static CardAdapter.CardViewHolder create(int layoutId, View view) {
        switch (layoutId) {
            case R.layout.card_site_inputstick:
            case R.layout.card_site:
                return new SiteCard.SiteCardViewHolder(view);
            case R.layout.card_dummy:
                return new CardAdapter.CardViewHolder(view);
        }
        return new CardAdapter.CardViewHolder(view);
    }
}
