package de.devland.masterpassword.ui;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionItemTarget;
import com.github.amlcurran.showcaseview.targets.PointTarget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.R;
import de.devland.masterpassword.model.Site;
import de.devland.masterpassword.prefs.ShowCasePrefs;
import de.devland.masterpassword.util.ProKeyUtil;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;
import lombok.NoArgsConstructor;

/**
 * A simple {@link Fragment} subclass.
 */
@NoArgsConstructor
public class PasswordViewFragment extends Fragment implements Card.OnCardClickListener {

    private ShowCasePrefs showCasePrefs;

    @InjectView(R.id.cardList)
    protected CardListView cardListView;

    protected CardArrayAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        adapter = new CardArrayAdapter(getActivity(), new ArrayList<Card>());

        showCasePrefs = Esperandro.getPreferences(ShowCasePrefs.class, getActivity());
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.password_view, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (ProKeyUtil.INSTANCE.isPro()) {
            searchItem.setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent intent = new Intent(getActivity(), EditActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshCards();
    }

    private void refreshCards() {
        List<SiteCard> cards = new ArrayList<SiteCard>();

        Iterator<Site> siteIterator = Site.findAsIterator(Site.class, null, null, null, Site.SITE_NAME, null);
        while (siteIterator.hasNext()) {
            Site site = siteIterator.next();
            SiteCard siteCard = new SiteCard(getActivity(), site);
            siteCard.setOnUndoSwipeListListener(new Card.OnUndoSwipeListListener() {
                @Override
                public void onUndoSwipe(Card card) {
                    if (card instanceof SiteCard) {
                        SiteCard siteCard = (SiteCard) card;
                        siteCard.getSite().save();
                        refreshCards();
                    }

                }
            });
            siteCard.setOnClickListener(this);
            cards.add(siteCard);
        }

        adapter.clear();
        adapter.addAll(cards);
        adapter.setEnableUndo(true);
        adapter.notifyDataSetChanged();

        if (cards.size() > 0 && !showCasePrefs.firstCardShown()) {
            ShowcaseView.Builder showCaseBuilder = new ShowcaseView.Builder(getActivity(), true);
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            showCaseBuilder.hideOnTouchOutside()
                    .setStyle(R.style.ShowcaseLightTheme)
                    .setContentText("Swipe to delete.\nTap password to copy to clipboard.")
                    .setTarget(new PointTarget(size.x / 2, size.y / 4));
            showCaseBuilder.build().show();
            showCasePrefs.firstCardShown(true);
        }
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
        adapter.setEnableUndo(true);
        if (!getActivity().isFinishing()) {
            if (!showCasePrefs.addCardShown()) {
                ShowcaseView.Builder showCaseBuilder = new ShowcaseView.Builder(getActivity(), true);
                showCaseBuilder.hideOnTouchOutside()
                        .setContentTitle("Add Site")
                        .setStyle(R.style.ShowcaseLightTheme)
                        .setContentText("Add a site you want to generate a password for.")
                        .setTarget(new ActionItemTarget(getActivity(), R.id.action_add));
                showCaseBuilder.build().show();
                showCasePrefs.addCardShown(true);
            }
        }
    }

    @Override
    public void onClick(Card card, View view) {
        if (card instanceof SiteCard) {
            SiteCard siteCard = (SiteCard) card;
            Intent intent = new Intent(getActivity(), EditActivity.class);
            intent.putExtra(EditFragment.ARG_SITE_ID, siteCard.getSite().getId());
            startActivity(intent);

        }
    }
}
