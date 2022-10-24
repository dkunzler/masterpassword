package de.devland.masterpassword.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filter;

import com.lyndir.lhunath.opal.system.util.StringUtils;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.BuildConfig;
import de.devland.masterpassword.R;
import de.devland.masterpassword.base.ui.BaseFragment;
import de.devland.masterpassword.model.Category;
import de.devland.masterpassword.model.Site;
import de.devland.masterpassword.prefs.DefaultPrefs;
import de.devland.masterpassword.ui.passwordlist.Card;
import de.devland.masterpassword.ui.passwordlist.CardAdapter;
import de.devland.masterpassword.ui.passwordlist.CardSectionIndicator;
import de.devland.masterpassword.ui.passwordlist.DummyCard;
import de.devland.masterpassword.ui.passwordlist.SiteCard;
import de.devland.masterpassword.util.ShowCaseManager;
import de.devland.masterpassword.util.event.CategoryChangeEvent;
import de.devland.masterpassword.util.event.PasswordCopyEvent;
import de.devland.masterpassword.util.event.ProStatusChangeEvent;
import de.devland.masterpassword.util.event.SiteCardEditEvent;
import de.devland.masterpassword.util.event.SiteDeleteEvent;
import lombok.NoArgsConstructor;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

/**
 * A simple {@link Fragment} subclass.
 */
@NoArgsConstructor
public class PasswordViewFragment extends BaseFragment implements
        SearchView.OnQueryTextListener {
    private static final String STATE_CATEGORY = "de.devland.PasswordViewFragment.STATE_CATEGORY";
    private static final String STATE_FIRSTITEM = "de.devland.PasswordViewFragment.STATE_FIRSTITEM";

    @BindView(R.id.cardList)
    protected RecyclerView cardListView;
    @BindView(R.id.fast_scroller)
    protected VerticalRecyclerViewFastScroller fastScroller;
    @BindView(R.id.rvfs_scroll_section_indicator)
    protected CardSectionIndicator sectionIndicator;

    protected MenuItem searchItem;
    protected SearchView searchView;
    protected DefaultPrefs defaultPrefs;
    protected Category activeCategory;
    protected String filterText;
    protected CardFilter filter;

    protected CardAdapter adapter;
    private LinearLayoutManager cardListLayoutManager;
    private long currentVisibleItem = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, getActivity());

        adapter = new CardAdapter();

        if (!BuildConfig.DEBUG) {
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_CATEGORY)) {
                activeCategory = new Category(savedInstanceState.getString(STATE_CATEGORY));
            }
            if (savedInstanceState.containsKey(STATE_FIRSTITEM)) {
                currentVisibleItem = savedInstanceState.getLong(STATE_FIRSTITEM);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (cardListLayoutManager != null) {
            long firstVisibleItemId = getCurrentVisibleItemId();
            outState.putLong(STATE_FIRSTITEM, firstVisibleItemId);
        }
        if (activeCategory != null) {
            outState.putString(STATE_CATEGORY,
                    activeCategory.getName());
        }
    }

    private long getCurrentVisibleItemId() {
        int firstVisibleItemPosition = cardListLayoutManager.findFirstVisibleItemPosition();
        return adapter.getItemId(firstVisibleItemPosition);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.password_view, menu);

        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
    }

    @OnClick(R.id.floating_action_search)
    public void expandSearchView() {
        searchItem.expandActionView();
        searchView.requestFocus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String currentSortBy = defaultPrefs.sortBy();

        switch (id) {
            case R.id.menuSortAlphabetically:
                defaultPrefs.sortBy(Site.SITE_NAME + Site.NOCASE_ORDER_SUFFIX);
                sectionIndicator.enable();
//                fastScroller.setSectionIndicator(sectionIndicator);
                break;
            case R.id.menuSortLastUsed:
                defaultPrefs.sortBy(Site.LAST_USED + Site.DESC_ORDER_SUFFIX);
                sectionIndicator.disable();
//                fastScroller.setSectionIndicator(null);
//                sectionIndicator.setVisibility(View.GONE);
                break;
            case R.id.action_add:
                onAddClick();
                break;
        }

        if (!defaultPrefs.sortBy().equals(currentSortBy)) {
            refreshCards();
        }

        return super.onOptionsItemSelected(item);
    }

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
    public void onSiteDelete(SiteDeleteEvent e) {
        int cardPosition = adapter.remove(e.getCard());
        adapter.notifyItemRemoved(cardPosition);
    }

    @Subscribe
    public void onPasswordCopy(PasswordCopyEvent e) {
        if (defaultPrefs.sortBy().contains(Site.LAST_USED)) {
            SiteCard card = e.getCard();
            int oldPosition = adapter.move(card, 0);
            adapter.notifyItemMoved(oldPosition, 0);
            cardListView.scrollToPosition(0);
        }
    }

    @Subscribe
    public void onSiteCardEdit(SiteCardEditEvent e) {
        Intent intent = new Intent(getActivity(), EditActivity.class);
        intent.putExtra(EditFragment.ARG_SITE_ID, e.getCard().getSite().getId());
        startActivity(intent);
//        ViewFragment viewFragment = new ViewFragment();
//        Bundle args = new Bundle();
//        args.putLong(ViewFragment.ARG_SITE_ID, e.getCard().getSite().getId());
//        viewFragment.setArguments(args);
//        viewFragment.show(getActivity().getSupportFragmentManager(), null);
    }

    @Subscribe
    public void onProStatusChange(@SuppressWarnings("UnusedParameters") ProStatusChangeEvent unused) {
        refreshCards();
    }

    private void refreshCards() {
        currentVisibleItem = getCurrentVisibleItemId();
        List<Card> cards = new ArrayList<>();

        String where = null;
        if (activeCategory != null && !activeCategory.equals(Category.all(getActivity()))) {
            where = Site.CATEGORY + " = '" + activeCategory.getName() + "'";
        }
        List<Site> sites = Site.find(Site.class, where, null, null, defaultPrefs.sortBy(), null);
        filter = new CardFilter(sites);

        int currentVisible = 0;
        int position = 0;
        for (Site site : sites) {
            if (site.getId() == currentVisibleItem) {
                currentVisible = position;
            }
            SiteCard siteCard = new SiteCard(getActivity(), site);
            cards.add(siteCard);
            position++;
        }
        if (cards.size() > 0) {
            ShowCaseManager.INSTANCE.showFirstCardShowCase(getActivity());
        }

        cards.add(new DummyCard());
        adapter = new CardAdapter();
        adapter.addAll(cards);
        if (!StringUtils.isEmpty(filterText)) {
            filter.filter(filterText);
        }
        cardListLayoutManager.scrollToPosition(currentVisible);
        cardListView.swapAdapter(adapter, true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_password_view, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cardListLayoutManager = new LinearLayoutManager(getActivity());
        cardListView.setLayoutManager(cardListLayoutManager);
        cardListView.swapAdapter(adapter, false);
        cardListView.setBackgroundResource(R.color.card_list_background);

        fastScroller.setRecyclerView(cardListView);
        if (!defaultPrefs.sortBy().startsWith(Site.SITE_NAME)) {
            sectionIndicator.disable();
        }
        cardListView.addOnScrollListener(fastScroller.getOnScrollListener());

        if (!getActivity().isFinishing()) {
            ShowCaseManager.INSTANCE.showAddShowCase(getActivity());
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
        filterText = s;
        filter.filter(s);
        //onScrollListener.show();
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

    class CardFilter extends Filter {

        private final List<Site> allSites;

        public CardFilter(List<Site> sites) {
            allSites = sites;
            // pre-cache passwords and usernames
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (Site site : allSites) {
                        site.getCurrentPassword();
                        site.getCurrentUserName();
                    }
                }
            }).start();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Card> cards = new ArrayList<>();
            String constraintString = constraint.toString().toLowerCase();

            for (Site site : allSites) {
                // search in site name and password
                boolean matches;
                // site name matches
                matches = site.getSiteName() != null
                        && site.getSiteName().toLowerCase().contains(constraintString);
                // site user matches
                String currentUserName = site.getCurrentUserName();
                matches = matches || (currentUserName != null
                        && currentUserName.toLowerCase().contains(constraintString));
                // site password matches
                String currentPassword = site.getCurrentPassword();
                matches = matches || ((currentPassword != null)
                        && currentPassword.toLowerCase().contains(constraintString));
                if (matches) {
                    SiteCard siteCard = new SiteCard(getActivity(), site);
                    cards.add(siteCard);
                }
            }
            if (cards.size() > 0) {
                ShowCaseManager.INSTANCE.showFirstCardShowCase(getActivity());
            }

            cards.add(new DummyCard());

            FilterResults filterResults = new FilterResults();
            filterResults.values = cards;
            filterResults.count = cards.size();
            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            CardAdapter adapter = new CardAdapter();
            adapter.addAll((List<Card>) results.values);
            PasswordViewFragment.this.adapter = adapter;
            cardListView.swapAdapter(adapter, true);
        }
    }
}
