package it.niedermann.nextcloud.deck.ui.card;

import android.content.ClipData;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.ColorUtil;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.SupportUtil;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.full.FullCard;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    private static final String TAG = CardAdapter.class.getCanonicalName();

    private Context context;
    private List<FullCard> cardList = new ArrayList<>();

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        this.context = viewGroup.getContext();
        View v = LayoutInflater.from(this.context).inflate(R.layout.fragment_card, viewGroup, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder viewHolder, int position) {
        FullCard card = cardList.get(position);
        viewHolder.cardTitle.setText(card.getCard().getTitle());

        if (card.getCard().getDescription() != null && !card.getCard().getDescription().isEmpty()) {
            viewHolder.cardDescription.setText(card.getCard().getDescription());
            viewHolder.cardDescription.setVisibility(View.VISIBLE);
        } else {
            viewHolder.cardDescription.setVisibility(View.GONE);
        }

        if (card.getCard().getDueDate() != null) {
            viewHolder.cardDueDate.setText(
                    SupportUtil.getRelativeDateTimeString(
                            this.context,
                            card.getCard().getDueDate().getTime())
            );
            themeDueDate(this.context, viewHolder.cardDueDate, card.getCard().getDueDate());
            viewHolder.cardDueDate.setVisibility(View.VISIBLE);
        } else {
            viewHolder.cardDueDate.setVisibility(View.GONE);
        }

        Chip chip;
        viewHolder.labels.removeAllViews();
        if (card.getLabels()!= null && card.getLabels().size() > 0) {
            for (Label label : card.getLabels()) {
                chip = new Chip(context);
                chip.setText(label.getTitle());

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
    }

    private void themeDueDate(Context context, TextView cardDueDate, Date dueDate) {
        long diff = getDayDifference(new Date(), dueDate);

        int backgroundDrawable = 0;
        int textColor = R.color.default_text_color;
        int icon = R.drawable.calendar_blank_grey600_24dp;

        if (diff == 1) {
            // due date: tommorow
            backgroundDrawable = R.drawable.due_tomorrow_background;
        } else if (diff == 0) {
            // due date: today
            backgroundDrawable = R.drawable.due_today_background;
        } else if (diff < 0) {
            // due date: overdue
            backgroundDrawable = R.drawable.due_overdue_background;
            textColor = R.color.overdue_text_color;
            icon = R.drawable.calendar_blank_white_24dp;
        }

        themeDueDate(context, cardDueDate, backgroundDrawable, textColor, icon);
    }

    private void themeDueDate(Context context, TextView cardDueDate, @DrawableRes int background, @ColorRes int textColor, @DrawableRes int icon) {
        cardDueDate.setBackgroundResource(background);
        cardDueDate.setTextColor(context.getResources().getColor(textColor));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            cardDueDate.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    context.getResources().getDrawable(icon),
                    null,
                    null,
                    null
            );
        } else {
            cardDueDate.setCompoundDrawablesWithIntrinsicBounds(
                    context.getResources().getDrawable(icon),
                    null,
                    null,
                    null
            );
        }
    }

    /**
     * difference between 2 dates in days (hours, minutes will be set to zero).
     *
     * @param dateFrom  start date
     * @param dateUntil end date
     * @return difference between the to dates in days.
     */
    public static long getDayDifference(Date dateFrom, Date dateUntil) {
        dateFrom.setHours(0);
        dateFrom.setMinutes(0);

        dateUntil.setHours(0);
        dateUntil.setMinutes(0);

        return TimeUnit.DAYS.convert(dateUntil.getTime() - dateFrom.getTime(), TimeUnit.MILLISECONDS);
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

    static class CardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card)
        MaterialCardView card;
        @BindView(R.id.card_title)
        TextView cardTitle;
        @BindView(R.id.card_description)
        TextView cardDescription;
        @BindView(R.id.labels)
        ChipGroup labels;
        @BindView(R.id.card_due_date)
        TextView cardDueDate;

        private CardViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            card.setOnClickListener((View clickedView) -> {
                if (Build.VERSION.SDK_INT >= 16) {
                    cardDescription.setMaxLines(cardDescription.getMaxLines() == 3 ? Integer.MAX_VALUE : 3);
                }
            });
            card.setOnLongClickListener((View draggedView) -> {

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
                view.setVisibility(View.INVISIBLE);
                DeckLog.log("onLongClickListener");
                return true;
            });
        }
    }
}
