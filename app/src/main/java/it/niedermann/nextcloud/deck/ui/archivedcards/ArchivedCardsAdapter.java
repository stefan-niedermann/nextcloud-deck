package it.niedermann.nextcloud.deck.ui.archivedcards;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;

import org.jetbrains.annotations.NotNull;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.card.CardAdapter;
import it.niedermann.nextcloud.deck.ui.card.ItemCardViewHolder;

public class ArchivedCardsAdapter extends CardAdapter {

    @SuppressWarnings("WeakerAccess")
    public ArchivedCardsAdapter(@NonNull Context context, @NonNull FragmentManager fragmentManager, @NonNull Account account, long boardId, boolean canEdit, @NonNull SyncManager syncManager, @NonNull LifecycleOwner lifecycleOwner) {
        super(context, fragmentManager, account, boardId, 0L, 0L, canEdit, syncManager, lifecycleOwner, null);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemCardViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        viewHolder.binding.card.setOnClickListener(null);
        viewHolder.binding.card.setOnLongClickListener(null);
    }

    protected void onOverflowIconClicked(@NotNull View view, FullCard card) {
        final Context context = view.getContext();
        final PopupMenu popup = new PopupMenu(context, view);
        popup.inflate(R.menu.card_menu);
        prepareOptionsMenu(popup.getMenu(), card);

        popup.setOnMenuItemClickListener(item -> optionsItemSelected(context, item, card));
        popup.show();
    }

    protected void prepareOptionsMenu(Menu menu, @NotNull FullCard card) {
        // Nothing to do
    }

    protected boolean optionsItemSelected(@NonNull Context context, @NotNull MenuItem item, FullCard fullCard) {
        switch (item.getItemId()) {
            case R.id.action_card_dearchive: {
                // TODO error handling
                new Thread(() -> syncManager.dearchiveCard(fullCard)).start();
                return true;
            }
            case R.id.action_card_delete: {
                // TODO error handling
                syncManager.deleteCard(fullCard.getCard());
                return true;
            }
            default: {
                return false;
            }
        }
    }
}
