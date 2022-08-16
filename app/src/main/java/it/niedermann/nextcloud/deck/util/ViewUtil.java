package it.niedermann.nextcloud.deck.util;

import static java.time.temporal.ChronoUnit.DAYS;
import static it.niedermann.nextcloud.deck.DeckApplication.isDarkTheme;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.TextViewCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.time.LocalDate;

import it.niedermann.android.util.DimensionUtil;
import it.niedermann.nextcloud.deck.R;

public final class ViewUtil {

    private ViewUtil() {
        throw new UnsupportedOperationException("This class must not get instantiated");
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
        final var drawable = ContextCompat.getDrawable(context, imageId);
        assert drawable != null;
        final var wrapped = DrawableCompat.wrap(drawable).mutate();
        DrawableCompat.setTint(wrapped, color);
        return drawable;
    }

    public static void setImageColor(@NonNull Context context, @NonNull ImageView imageView, @ColorRes int colorRes) {
        imageView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, colorRes)));
    }
}
