package de.devland.masterpassword.ui.passwordlist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by deekay on 07.06.2015.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> implements SectionIndexer {

    private List<Card> cards = new ArrayList<>();

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(viewType, viewGroup, false);
        return CardViewHolderFactory.create(viewType, view);
    }

    @Override
    public void onBindViewHolder(CardViewHolder cardViewHolder, int position) {
        Card card = cards.get(position);
        card.bindViewHolder(cardViewHolder);
    }

    @Override
    public int getItemViewType(int position) {
        return cards.get(position).getLayoutId();
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    @Override
    public long getItemId(int position) {
        if (position < cards.size() && position >= 0) {
            return cards.get(position).getId();
        } else {
            return super.getItemId(position);
        }
    }

    public int remove(Card card) {
        int cardIndex = cards.indexOf(card);
        if (cardIndex >= 0) {
            cards.remove(cardIndex);
        }
        return cardIndex;
    }

    public void clear() {
        cards.clear();
    }

    public void addAll(List<Card> cards) {
        this.cards.addAll(cards);
    }

    public void add(Card card, int position) {
        cards.add(position, card);
    }

    public int move(Card card, int newPosition) {
        int oldPosition = cards.indexOf(card);
        if (oldPosition >= 0) {
            cards.remove(oldPosition);
            cards.add(newPosition, card);
        }
        return oldPosition;
    }

    @Override
    public Object[] getSections() {
        return cards.toArray();
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        if (position >= cards.size()) {
            position = cards.size() - 1;
        }

        return position;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        public CardViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
