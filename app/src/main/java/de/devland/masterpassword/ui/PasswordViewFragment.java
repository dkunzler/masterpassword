package de.devland.masterpassword.ui;


import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.devland.masterpassword.R;
import de.devland.masterpassword.model.Site;
import de.devland.masterpassword.util.PasswordCardCursorAdapter;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class PasswordViewFragment extends Fragment {

    @InjectView(R.id.cardList)
    CardListView cardListView;

    //CardArrayAdapter adapter;
    PasswordCardCursorAdapter adapter;

    public PasswordViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //adapter = new CardArrayAdapter(getActivity(), new ArrayList<Card>());
        adapter = new PasswordCardCursorAdapter(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        Cursor cursor = Site.findAll();
        adapter.swapCursor(cursor);
        List<Card> cards = new ArrayList<Card>();

        //Iterator<Site> siteIterator = Site.findAll(Site.class);
        //while (siteIterator.hasNext()) {
        //    Site site = siteIterator.next();
        //    cards.add(new SiteCard(getActivity(), site));
        //}

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_password_view, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cardListView.setAdapter(adapter);
    }
}
