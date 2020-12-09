package it.niedermann.nextcloud.deck.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.TextViewCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.time.LocalDate;
import java.util.List;

import it.niedermann.android.util.DimensionUtil;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.ocs.comment.Mention;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static it.niedermann.nextcloud.deck.DeckApplication.isDarkTheme;
import static java.time.temporal.ChronoUnit.DAYS;

public final class ViewUtil {
    private ViewUtil() {
    }

    public static void addAvatar(@NonNull ImageView avatar, @NonNull String baseUrl, @NonNull String userId, @DrawableRes int errorResource) {
        addAvatar(avatar, baseUrl, userId, DimensionUtil.INSTANCE.dpToPx(avatar.getContext(), R.dimen.avatar_size), errorResource);
    }

    public static void addAvatar(@NonNull ImageView avatar, @NonNull String baseUrl, @NonNull String userId, @Px int avatarSizeInPx, @DrawableRes int errorResource) {
        final String uri = baseUrl + "/index.php/avatar/" + Uri.encode(userId) + "/" + avatarSizeInPx;
        Glide.with(avatar.getContext())
                .load(uri)
                .placeholder(errorResource)
                .error(errorResource)
                .apply(RequestOptions.circleCropTransform())
                .into(avatar);
    }

    public static void themeDueDate(@NonNull Context context, @NonNull TextView cardDueDate, @NonNull LocalDate dueDate) {
        long diff = DAYS.between(LocalDate.now(), dueDate);

        int backgroundDrawable = 0;
        int textColor = isDarkTheme(context) ? R.color.dark_fg_primary : R.color.grey600;

        if (diff == 1) {
            // due date: tomorrow
            backgroundDrawable = R.drawable.due_tomorrow_background;
        } else if (diff == 0) {
            // due date: today
            backgroundDrawable = R.drawable.due_today_background;
        } else if (diff < 0) {
            // due date: overdue
            backgroundDrawable = R.drawable.due_overdue_background;
            textColor = R.color.overdue_text_color;
        }

        cardDueDate.setBackgroundResource(backgroundDrawable);
        cardDueDate.setTextColor(ContextCompat.getColor(context, textColor));
        TextViewCompat.setCompoundDrawableTintList(cardDueDate, ColorStateList.valueOf(ContextCompat.getColor(context, textColor)));
    }

    public static Drawable getTintedImageView(@NonNull Context context, @DrawableRes int imageId, @ColorInt int color) {
        final Drawable drawable = ContextCompat.getDrawable(context, imageId);
        assert drawable != null;
        final Drawable wrapped = DrawableCompat.wrap(drawable).mutate();
        DrawableCompat.setTint(wrapped, color);
        return drawable;
    }

    /**
     * Replaces all mentions in the textView with an avatar and the display name
     *
     * @param account  {@link Account} where the users of those mentions belong to
     * @param mentions {@link List} of all mentions that should be substituted
     * @param textView target {@link TextView}
     */
    public static void setupMentions(@NonNull Account account, @NonNull List<Mention> mentions, TextView textView) {
        Context context = textView.getContext();
        SpannableStringBuilder messageBuilder = new SpannableStringBuilder(textView.getText());

        // Step 1
        // Add avatar icons and display names
        for (Mention m : mentions) {
            final String mentionId = "@" + m.getMentionId();
            final String mentionDisplayName = " " + m.getMentionDisplayName();
            int index = messageBuilder.toString().lastIndexOf(mentionId);
            while (index >= 0) {
                messageBuilder.setSpan(new ImageSpan(context, R.drawable.ic_person_grey600_24dp), index, index + mentionId.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                messageBuilder.insert(index + mentionId.length(), mentionDisplayName);
                index = messageBuilder.toString().substring(0, index).lastIndexOf(mentionId);
            }
        }
        textView.setText(messageBuilder);

        // Step 2
        // Replace avatar icons with real avatars
        final ImageSpan[] list = messageBuilder.getSpans(0, messageBuilder.length(), ImageSpan.class);
        for (ImageSpan span : list) {
            final int spanStart = messageBuilder.getSpanStart(span);
            final int spanEnd = messageBuilder.getSpanEnd(span);
            Glide.with(context)
                    .asBitmap()
                    .placeholder(R.drawable.ic_person_grey600_24dp)
                    .load(account.getUrl() + "/index.php/avatar/" + messageBuilder.subSequence(spanStart + 1, spanEnd).toString() + "/" + DimensionUtil.INSTANCE.dpToPx(context, R.dimen.icon_size_details))
                    .apply(RequestOptions.circleCropTransform())
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            messageBuilder.removeSpan(span);
                            messageBuilder.setSpan(new ImageSpan(context, resource), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // silence is gold
                        }
                    });
        }
        textView.setText(messageBuilder);
    }

    public static void setImageColor(@NonNull Context context, @NonNull ImageView imageView, @ColorRes int colorRes) {
        if (SDK_INT >= LOLLIPOP) {
            imageView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, colorRes)));
        } else {
            imageView.setColorFilter(ContextCompat.getColor(context, colorRes));
        }
    }
}
