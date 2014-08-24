package de.devland.masterpassword.util;

import android.content.Context;
import android.database.Cursor;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardCursorAdapter;
import it.gmariotti.cardslib.library.view.CardView;

/**
 * Created by David Kunzler on 24.08.2014.
 */
public class PasswordCardCursorAdapter extends CardCursorAdapter {

    public PasswordCardCursorAdapter(Context context) {
        super(context, null, 0);
    }

    protected PasswordCardCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    protected PasswordCardCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    protected Card getCardFromCursor(Cursor cursor) {
        Card card = new Card(getContext());
        return card;
    }
}
