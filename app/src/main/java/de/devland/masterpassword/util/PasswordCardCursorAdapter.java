package de.devland.masterpassword.util;

import android.content.Context;
import android.database.Cursor;

import de.devland.masterpassword.model.Site;
import de.devland.masterpassword.ui.SiteCard;
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
        Site site = Site.fromCursor(cursor);
        Card card = new SiteCard(getContext(), site);

        return card;
    }
}
