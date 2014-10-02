package de.devland.masterpassword.ui;

import android.content.Context;

import de.devland.masterpassword.R;
import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by David Kunzler on 02.10.2014.
 */
public class DummyCard extends Card {

    public DummyCard(Context context) {
        super(context, R.layout.card_dummy);
        setBackgroundResourceId(android.R.color.transparent);
        setShadow(false);
    }
}
