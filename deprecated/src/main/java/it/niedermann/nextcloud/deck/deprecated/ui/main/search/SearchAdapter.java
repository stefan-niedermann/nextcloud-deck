package it.niedermann.nextcloud.deck.deprecated.ui.main.search;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import it.niedermann.nextcloud.deck.deprecated.util.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemSearchCardBinding;
import it.niedermann.nextcloud.deck.databinding.ItemSearchStackBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.interfaces.IRemoteEntity;
import it.niedermann.nextcloud.deck.deprecated.ui.card.CardActionListener;
import it.niedermann.nextcloud.deck.deprecated.ui.card.CardOptionsItemSelectedListener;

public class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> implements CardOptionsItemSelectedListener {

    private static final int TYPE_STACK = 0;
    private static final int TYPE_CARD = 1;

    @NonNull
    private CardActionListener cardActionListener;
    @Nullable
    private Account account;
    @Nullable
    private FullBoard fullBoard;
    private final List<IRemoteEntity> items = new ArrayList<>();
    @NonNull
    private String term = "";

    public SearchAdapter(@NonNull CardActionListener cardActionListener) {
        this.cardActionListener = cardActionListener;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final var context = parent.getContext();
        switch (viewType) {
            case TYPE_STACK: {
                return new SearchStackViewHolder(ItemSearchStackBinding.inflate(LayoutInflater.from(context), parent, false));
            }
            case TYPE_CARD: {
                return new SearchCardViewHolder(ItemSearchCardBinding.inflate(LayoutInflater.from(context), parent, false));
            }
            default: {
                throw new UnsupportedOperationException("Unknown view type: " + viewType);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_STACK: {
                final var localId = -getItemId(position);
                items.stream()
                        .filter(item -> item.getClass() == Stack.class)
                        .filter(item -> item.getLocalId() == localId)
                        .findAny()
                        .map(item -> (Stack) item)
                        .ifPresent(stack -> {
                            final var searchStackViewHolder = (SearchStackViewHolder) holder;
                            searchStackViewHolder.bind(stack);

                            if (fullBoard == null) {
                                DeckLog.logError(new IllegalStateException("board is null"));
                                return;
                            }
                            searchStackViewHolder.applyTheme(fullBoard.getBoard().getColor());
                        });
                break;
            }
            case TYPE_CARD: {
                if (account == null || fullBoard == null) {
                    DeckLog.logError(new IllegalStateException("account or board is null"));
                    break;
                }
                final var localId = getItemId(position);
                items.stream()
                        .filter(item -> item.getClass() == FullCard.class)
                        .filter(item -> item.getLocalId() == localId)
                        .findAny()
                        .map(item -> (FullCard) item)
                        .ifPresent(fullCard -> {
                            final var searchCardViewHolder = (SearchCardViewHolder) holder;
                            searchCardViewHolder.bind(account, fullBoard.getLocalId(), fullCard, fullBoard.getId(), R.menu.card_menu, this);
                            searchCardViewHolder.applyTheme(fullBoard.getBoard().getColor(), term);
                        });
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown view type for position " + position);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getItemId(position) > 0 ? TYPE_CARD : TYPE_STACK;
    }

    /**
     * @return {@link FullCard#getLocalId()} or <strong>negated</strong> {@link Stack#getLocalId()}
     */
    @Override
    public long getItemId(int position) {
        final var item = items.get(position);
        final var clazz = item.getClass();
        if (clazz == Stack.class) {
            return -item.getLocalId();
        } else if (clazz == FullCard.class) {
            return item.getLocalId();
        }
        throw new UnsupportedOperationException("Expected item list to only contain " + Stack.class.getSimpleName() + " or " + FullCard.class.getSimpleName() + " but found " + clazz.getSimpleName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(@NonNull SearchResults results) {
        this.account = results.account;
        this.fullBoard = results.fullBoard;
        this.term = results.term;

        this.items.clear();
        results.result.entrySet()
                .stream()
                .sorted(Comparator.comparingLong(o -> o.getKey().getOrder()))
                .forEach(entry -> {
                    this.items.add(entry.getKey());
                    this.items.addAll(entry.getValue());
                });

        notifyDataSetChanged();
    }

    @Override
    public boolean onCardOptionsItemSelected(@NonNull MenuItem menuItem, @NonNull FullCard fullCard) {
        final int itemId = menuItem.getItemId();
        if (itemId == R.id.share_link) {
            if (fullBoard == null) {
                DeckLog.warn("Can not share link to card", fullCard.getCard().getTitle(), "because fullBoard is null");
                return false;
            }
            cardActionListener.onShareLink(fullBoard, fullCard);
            return true;
        } else if (itemId == R.id.share_content) {
            cardActionListener.onShareContent(fullCard);
        } else if (itemId == R.id.action_card_assign) {
            cardActionListener.onAssignCurrentUser(fullCard);
            return true;
        } else if (itemId == R.id.action_card_unassign) {
            cardActionListener.onUnassignCurrentUser(fullCard);
            return true;
        } else if (itemId == R.id.action_card_move) {
            if (fullBoard == null) {
                DeckLog.warn("Can not move card", fullCard.getCard().getTitle(), "because fullBoard is null");
                return false;
            }
            cardActionListener.onMove(fullBoard, fullCard);
            return true;
        } else if (itemId == R.id.action_card_archive) {
            cardActionListener.onArchive(fullCard);
            return true;
        } else if (itemId == R.id.action_card_delete) {
            cardActionListener.onDelete(fullCard);
            return true;
        }
        return true;
    }
}
