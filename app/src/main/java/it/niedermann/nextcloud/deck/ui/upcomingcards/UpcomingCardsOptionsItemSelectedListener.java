package it.niedermann.nextcloud.deck.ui.upcomingcards;

import static it.niedermann.nextcloud.deck.util.MimeTypeUtil.TEXT_PLAIN;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.ui.card.CardOptionsItemSelectedListener;
import it.niedermann.nextcloud.deck.ui.movecard.MoveCardDialogFragment;
import it.niedermann.nextcloud.deck.util.CardUtil;

public class UpcomingCardsOptionsItemSelectedListener implements CardOptionsItemSelectedListener {
    @NonNull
    private final Account account;
    @NonNull
    private final Activity activity;
    @NonNull
    private final FragmentManager fragmentManager;
    @Nullable
    private final Long boardRemoteId;
    private final long boardLocalId;
    @NonNull
    private final BiConsumer<Account, Card> assignCard;
    @NonNull
    private final BiConsumer<Account, Card> unassignCard;
    @NonNull
    private final Consumer<FullCard> archiveCard;
    @NonNull
    private final Consumer<Card> deleteCard;

    public UpcomingCardsOptionsItemSelectedListener(@NonNull Account account,
                                                    @NonNull Activity activity,
                                                    @NonNull FragmentManager fragmentManager,
                                                    @Nullable Long boardRemoteId,
                                                    long boardLocalId,
                                                    @NonNull BiConsumer<Account, Card> assignCard,
                                                    @NonNull BiConsumer<Account, Card> unassignCard,
                                                    @NonNull Consumer<FullCard> archiveCard,
                                                    @NonNull Consumer<Card> deleteCard
    ) {
        this.account = account;
        this.activity = activity;
        this.fragmentManager = fragmentManager;
        this.boardRemoteId = boardRemoteId;
        this.boardLocalId = boardLocalId;
        this.assignCard = assignCard;
        this.unassignCard = unassignCard;
        this.archiveCard = archiveCard;
        this.deleteCard = deleteCard;
    }

    @Override
    public boolean onCardOptionsItemSelected(@NonNull MenuItem menuItem, @NonNull FullCard fullCard) {
        final int itemId = menuItem.getItemId();
        if (itemId == R.id.share_link) {
            final var shareIntent = new Intent()
                    .setAction(Intent.ACTION_SEND)
                    .setType(TEXT_PLAIN)
                    .putExtra(Intent.EXTRA_SUBJECT, fullCard.getCard().getTitle())
                    .putExtra(Intent.EXTRA_TITLE, fullCard.getCard().getTitle())
                    .putExtra(Intent.EXTRA_TEXT, account.getUrl() + activity.getString(account.getServerDeckVersionAsObject().getShareLinkResource(), boardRemoteId, fullCard.getCard().getId()));
            activity.startActivity(Intent.createChooser(shareIntent, fullCard.getCard().getTitle()));
            return true;
        } else if (itemId == R.id.share_content) {
            final var shareIntent = new Intent()
                    .setAction(Intent.ACTION_SEND)
                    .setType(TEXT_PLAIN)
                    .putExtra(Intent.EXTRA_SUBJECT, fullCard.getCard().getTitle())
                    .putExtra(Intent.EXTRA_TITLE, fullCard.getCard().getTitle())
                    .putExtra(Intent.EXTRA_TEXT, CardUtil.getCardContentAsString(activity, fullCard));
            activity.startActivity(Intent.createChooser(shareIntent, fullCard.getCard().getTitle()));
        } else if (itemId == R.id.action_card_assign) {
            assignCard.accept(account, fullCard.getCard());
            return true;
        } else if (itemId == R.id.action_card_unassign) {
            unassignCard.accept(account, fullCard.getCard());
            return true;
        } else if (itemId == R.id.action_card_move) {
            DeckLog.verbose("[Move card] Launch move dialog for " + Card.class.getSimpleName() + " \"" + fullCard.getCard().getTitle() + "\" (#" + fullCard.getLocalId() + ")");
            MoveCardDialogFragment
                    .newInstance(fullCard.getAccountId(), boardLocalId, fullCard.getCard().getTitle(), fullCard.getLocalId(), CardUtil.cardHasCommentsOrAttachments(fullCard))
                    .show(fragmentManager, MoveCardDialogFragment.class.getSimpleName());
            return true;
        } else if (itemId == R.id.action_card_archive) {
            archiveCard.accept(fullCard);
            return true;
        } else if (itemId == R.id.action_card_delete) {
            deleteCard.accept(fullCard.getCard());
            return true;
        }
        return true;
    }
}