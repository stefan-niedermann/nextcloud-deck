package it.niedermann.nextcloud.deck.ui.card;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NoCurrentAccountSelectedException;
import com.nextcloud.android.sso.helper.SingleAccountHelper;
import com.nextcloud.android.sso.model.SingleSignOnAccount;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.ui.EditActivity;
import it.niedermann.nextcloud.deck.util.ColorUtil;
import it.niedermann.nextcloud.deck.util.DateUtil;
import it.niedermann.nextcloud.deck.util.DimensionUtil;
import it.niedermann.nextcloud.deck.util.ViewUtil;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    private static final String TAG = CardAdapter.class.getCanonicalName();
    public static final String BUNDLE_KEY_ACCOUNT_ID = "accountId";
    public static final String BUNDLE_KEY_LOCAL_ID = "localId";

    private Context context;
    private List<FullCard> cardList = new ArrayList<>();
    private SingleSignOnAccount account;

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        this.context = viewGroup.getContext();
        View v = LayoutInflater.from(this.context).inflate(R.layout.fragment_card, viewGroup, false);
        try {
            account = SingleAccountHelper.getCurrentSingleSignOnAccount(context);
        } catch (NextcloudFilesAppAccountNotFoundException e) {
            DeckLog.logError(e);
        } catch (NoCurrentAccountSelectedException e) {
            DeckLog.logError(e);
        }
        return new CardViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CardViewHolder viewHolder, int position) {
        FullCard card = cardList.get(position);

        viewHolder.card.setOnClickListener((View clickedView) -> {
            Intent intent = new Intent(clickedView.getContext(), EditActivity.class);
            intent.putExtra(BUNDLE_KEY_ACCOUNT_ID, card.getAccountId());
            intent.putExtra(BUNDLE_KEY_LOCAL_ID, card.getLocalId());
            context.startActivity(intent);
        });
        viewHolder.card.setOnLongClickListener((View draggedView) -> {

            // Create a new ClipData.
            // This is done in two steps to provide clarity. The convenience method
            // ClipData.newPlainText() can create a plain text ClipData in one step.

            // Create a new ClipData.Item from the ImageView object's tag
            ClipData dragData = ClipData.newPlainText("TEST", "TEST2");

            // Starts the drag
            draggedView.startDrag(dragData,  // the data to be dragged
                    new View.DragShadowBuilder(draggedView),  // the drag shadow builder
                    draggedView,      // no need to use local data
                    0          // flags (not currently used, set to 0)
            );
            viewHolder.card.setVisibility(View.INVISIBLE);
            DeckLog.log("onLongClickListener");
            return true;
        });

        viewHolder.cardTitle.setText(card.getCard().getTitle());
        if (card.getCard().getDescription().length() > 0) {
            viewHolder.cardDescription.setText(card.getCard().getDescription());
            viewHolder.cardDescription.setVisibility(View.VISIBLE);
        } else {
            viewHolder.cardDescription.setVisibility(View.GONE);
        }


        if (card.getAssignedUsers() != null && card.getAssignedUsers().size() > 0 && account.url != null) {
            int avatarSize = DimensionUtil.getAvatarDimension(context, R.dimen.avatar_size_small);
            viewHolder.peopleList.removeAllViews();
            RelativeLayout.LayoutParams avatarLayoutParams;
            viewHolder.peopleList.setVisibility(View.VISIBLE);
            for (int i = 0; i < card.getAssignedUsers().size(); i++) {
                avatarLayoutParams = new RelativeLayout.LayoutParams(avatarSize, avatarSize);
                avatarLayoutParams.setMargins(0, 0, i * context.getResources().getDimensionPixelSize(R.dimen.avatar_overlapping_small), 0);
                avatarLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                ImageView avatar = new ImageView(context);
                avatar.setLayoutParams(avatarLayoutParams);
                viewHolder.peopleList.addView(avatar);
                avatar.requestLayout();
                ViewUtil.addAvatar(context, avatar, account.url, card.getAssignedUsers().get(i).getUid(), avatarSize);
            }
        } else {
            viewHolder.peopleList.setVisibility(View.GONE);
        }


        if (card.getCard().getDueDate() != null) {
            viewHolder.cardDueDate.setText(
                    DateUtil.getRelativeDateTimeString(
                            this.context,
                            card.getCard().getDueDate().getTime())
            );
            ViewUtil.themeDueDate(this.context, viewHolder.cardDueDate, card.getCard().getDueDate());
            viewHolder.cardDueDate.setVisibility(View.VISIBLE);
        } else {
            viewHolder.cardDueDate.setVisibility(View.GONE);
        }

        final int attachmentsCount = card.getCard().getAttachmentCount();

        if(attachmentsCount == 0) {
            viewHolder.cardCountAttachments.setVisibility(View.GONE);
        } else {
            viewHolder.cardCountAttachments.setVisibility(View.VISIBLE);
        }
        if (attachmentsCount > 99) {
            viewHolder.cardCountAttachments.setText(context.getString(R.string.attachment_count_max_value));
        } else if(attachmentsCount > 1) {
            viewHolder.cardCountAttachments.setText(attachmentsCount + "");
        } else if(attachmentsCount == 1) {
            viewHolder.cardCountAttachments.setText("");
        }


        Chip chip;
        int maxLabelsShown = Integer.valueOf(context.getString(R.string.max_labels_shown));
        int maxLabelsChars = Integer.valueOf(context.getString(R.string.max_labels_chars));
        viewHolder.labels.removeAllViews();
        if (card.getLabels() != null && card.getLabels().size() > 0) {
            for (int i = 0; i < card.getLabels().size(); i++) {
                if (i > maxLabelsShown - 1 && card.getLabels().size() > maxLabelsShown) {
                    chip = new Chip(context);
                    chip.setChipIcon(ContextCompat.getDrawable(context, R.drawable.ic_more_horiz_black_24dp));
                    chip.setCloseIconStartPadding(0);
                    chip.setCloseIconEndPadding(0);
                    chip.setTextStartPadding(0);
                    chip.setTextEndPadding(0);
                    viewHolder.labels.addView(chip);
                    break;
                }
                Label label = card.getLabels().get(i);
                chip = new Chip(context);
                String labelTitle = label.getTitle();
                if (labelTitle.length() > maxLabelsChars - 1) {
                    chip.setText(labelTitle.substring(0, maxLabelsChars));
                } else {
                    chip.setText(" " + labelTitle.substring(0, 1) + " ");
                }

                try {
                    int labelColor = Color.parseColor("#" + label.getColor());
                    ColorStateList c = ColorStateList.valueOf(labelColor);
                    chip.setChipBackgroundColor(c);
                    chip.setTextColor(ColorUtil.getForegroundColorForBackgroundColor(labelColor));
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "error parsing label color", e);
                }

                viewHolder.labels.addView(chip);
            }
            viewHolder.labels.setVisibility(View.VISIBLE);
        } else {
            viewHolder.labels.setVisibility(View.GONE);
        }

        viewHolder.cardMenu.setOnClickListener(v ->

        {
            onOverflowIconClicked(v, card);
        });
    }

    @Override
    public int getItemCount() {
        return cardList == null ? 0 : cardList.size();
    }

    public void setCardList(@NonNull List<FullCard> cardList) {
        this.cardList = cardList;
        notifyDataSetChanged();
    }

    public void moveItem(int fromPosition, int toPosition) {
        cardList.add(toPosition, cardList.remove(fromPosition));
        notifyItemMoved(fromPosition, toPosition);
    }

    private void onOverflowIconClicked(View view, FullCard card) {
        PopupMenu popup = new PopupMenu(context, view);
        popup.inflate(R.menu.card_menu);
        prepareOptionsMenu(popup.getMenu(), card);

        popup.setOnMenuItemClickListener(item -> optionsItemSelected(item, card));
        popup.show();
    }

    private void prepareOptionsMenu(Menu menu, FullCard card) {
        if (containsUser(card.getAssignedUsers(), account.username)) {
            menu.removeItem(menu.findItem(R.id.action_card_assign).getItemId());
        } else {
            menu.removeItem(menu.findItem(R.id.action_card_unassign).getItemId());
        }
    }

    private boolean containsUser(List<User> userList, String username) {
        if (userList != null) {
            for (User user : userList) {
                if (user.getPrimaryKey().equals(username)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean optionsItemSelected(MenuItem item, FullCard card) {
        Toast.makeText(context, "Not implemented yet", Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.action_card_assign: {
                return true;
            }
            case R.id.action_card_unassign: {
                return true;
            }
            case R.id.action_card_archive: {
                return true;
            }
            case R.id.action_card_delete: {
                return true;
            }
        }
        return true;
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card)
        MaterialCardView card;
        @BindView(R.id.card_title)
        TextView cardTitle;
        @BindView(R.id.card_description)
        TextView cardDescription;
        @BindView(R.id.peopleList)
        RelativeLayout peopleList;
        @BindView(R.id.labels)
        ChipGroup labels;
        @BindView(R.id.card_due_date)
        TextView cardDueDate;
        @BindView(R.id.card_count_attachments)
        TextView cardCountAttachments;
        @BindView(R.id.card_menu)
        ImageView cardMenu;

        private CardViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
