package de.devland.masterpassword.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;

import com.melnykov.fab.FloatingActionButton;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.App;
import de.devland.masterpassword.R;
import de.devland.masterpassword.model.Category;
import de.devland.masterpassword.model.Site;
import de.devland.masterpassword.prefs.DefaultPrefs;
import de.devland.masterpassword.util.ShowCaseManager;
import de.devland.masterpassword.util.SiteCardArrayAdapter;
import de.devland.masterpassword.util.event.CategoryChangeEvent;
import de.devland.masterpassword.util.event.PasswordCopyEvent;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.listener.SwipeOnScrollListener;
import lombok.NoArgsConstructor;

/**
 * A simple {@link Fragment} subclass.
 */
@NoArgsConstructor
public class PasswordViewFragment extends Fragment implements Card.OnCardClickListener,
        SearchView.OnQueryTextListener {
    private static final String STATE_CATEGORY = "de.devland.PasswordViewFragment.STATE_CATEGORY";

    @InjectView(R.id.cardList)
    protected InsertionAnimationCardListView cardListView;
    @InjectView(R.id.floating_action_add)
    protected FloatingActionButton addButton;

    protected MenuItem searchItem;
    protected SearchView searchView;
    protected DefaultPrefs defaultPrefs;
    protected Category activeCategory;

    protected SiteCardArrayAdapter adapter;
    SwipeOnScrollListener hideFloatingButtonScrollListener = new SwipeOnScrollListener() {
        private int mScrollY = 0;

        protected int getListViewScrollY() {
            View topChild = cardListView.getChildAt(0);
            return topChild == null ? 0 : cardListView.getFirstVisiblePosition() * topChild.getHeight() -
                    topChild.getTop();
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                             int totalItemCount) {
            super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            int newScrollY = getListViewScrollY();
            if (newScrollY == mScrollY) {
                return;
            }

            if (newScrollY > mScrollY) {
                // Scrolling up
                addButton.hide();
            } else if (newScrollY < mScrollY) {
                // Scrolling down
                addButton.show();
            }
            mScrollY = newScrollY;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_CATEGORY)) {
                activeCategory = new Category(savedInstanceState.getString(STATE_CATEGORY));
            }
        }

        defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, getActivity());

        adapter = new SiteCardArrayAdapter(getActivity(), new ArrayList<Card>());

        App.get().getBus().register(this);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (activeCategory != null) {
            outState.putString(STATE_CATEGORY,
                    activeCategory.getName());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.password_view, menu);

        searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(true);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String currentSortBy = defaultPrefs.sortBy();

        switch (id) {
            case R.id.menuSortAlphabetically:
                defaultPrefs.sortBy(Site.SITE_NAME + Site.NOCASE_ORDER_SUFFIX);
                break;
            case R.id.menuSortLastUsed:
                defaultPrefs.sortBy(Site.LAST_USED + Site.DESC_ORDER_SUFFIX);
                break;
        }

        if (!defaultPrefs.sortBy().equals(currentSortBy)) {
            refreshCards();
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.floating_action_add)
    protected void onAddClick() {
        Intent intent = new Intent(getActivity(), EditActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshCards();
    }


    @Subscribe
    public void onPasswordCopy(PasswordCopyEvent e) {
        if (defaultPrefs.sortBy().contains(Site.LAST_USED)) {
            final SiteCard card = e.getCard();
            final SiteCard newCard = new SiteCard(getActivity(), card.getSite(), adapter);

            if (adapter.getPosition(card) != 0) {

                cardListView.addRow(newCard);
                adapter.remove(card);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void refreshCards() {
        List<Card> cards = new ArrayList<>();

        String where = null;
        if (activeCategory != null && !activeCategory.equals(Category.all(getActivity()))) {
            where = Site.CATEGORY + " = '" + activeCategory.getName() + "'";
        }

        Iterator<Site> siteIterator = Site.findAsIterator(Site.class, where, null, null,
                defaultPrefs.sortBy(), null);
        while (siteIterator.hasNext()) {
            Site site = siteIterator.next();
            SiteCard siteCard = new SiteCard(getActivity(), site, adapter);
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
        if (cards.size() > 0) {
            ShowCaseManager.INSTANCE.showFirstCardShowCase(getActivity());
        }

        cards.add(new DummyCard(getActivity()));
        adapter.clear();
        adapter.addAll(cards);
        adapter.notifyDataSetChanged();
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
        cardListView.setBackgroundResource(R.color.card_list_background_light);

        cardListView.setOnScrollListener(hideFloatingButtonScrollListener);

        if (!getActivity().isFinishing()) {
            ShowCaseManager.INSTANCE.showAddShowCase(getActivity());
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

    @Override
    public boolean onQueryTextSubmit(String s) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        adapter.getFilter().filter(s);
        return true;
    }

    @Subscribe
    public void onCategoryChange(CategoryChangeEvent event) {
        if (this.activeCategory != event.getCategory()) {
            this.activeCategory = event.getCategory();
            refreshCards();
        }
    }

    @Produce
    public CategoryChangeEvent retrieveActiveCategory() {
        CategoryChangeEvent event;
        if (activeCategory != null) {
            event = new CategoryChangeEvent(activeCategory);
        } else {
            event = new CategoryChangeEvent(Category.all(getActivity()));
        }
        return event;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        App.get().getBus().unregister(this);
    }
}
