package it.niedermann.nextcloud.deck.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.TextViewCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Date;

import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.R;

public final class ViewUtil {
    private ViewUtil() {
    }

    public static void addAvatar(@NonNull ImageView avatar, @NonNull String baseUrl, @NonNull String userId, @DrawableRes int errorResource) {
        addAvatar(avatar, baseUrl, userId, DimensionUtil.dpToPx(avatar.getContext(), R.dimen.avatar_size), errorResource);
    }

    public static void addAvatar(@NonNull ImageView avatar, @NonNull String baseUrl, @NonNull String userId, @Px int avatarSizeInPx, @DrawableRes int errorResource) {
        final String uri = baseUrl + "/index.php/avatar/" + Uri.encode(userId) + "/" + avatarSizeInPx;
        Glide.with(avatar.getContext())
                .load(uri)
                .error(errorResource)
                .apply(RequestOptions.circleCropTransform())
                .into(avatar);
    }

    public static void themeDueDate(Context context, TextView cardDueDate, Date dueDate) {
        long diff = DateUtil.getDayDifference(new Date(), dueDate);

        int backgroundDrawable = 0;
        int textColor = Application.getAppTheme(context) ? R.color.dark_fg_primary : R.color.grey600;

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
        cardDueDate.setTextColor(context.getResources().getColor(textColor));
        TextViewCompat.setCompoundDrawableTintList(cardDueDate, ColorStateList.valueOf(context.getResources().getColor(textColor)));
    }

    public static Drawable getTintedImageView(@NonNull Context context, @DrawableRes int imageId, @NonNull String color) {
        final Drawable drawable = context.getResources().getDrawable(imageId);
        final Drawable wrapped = DrawableCompat.wrap(drawable).mutate();
        DrawableCompat.setTint(wrapped, Color.parseColor(color));
        return drawable;
    }

    public static Drawable getTintedImageView(@NonNull Context context, @DrawableRes int imageId, int colorId) {
        return getTintedImageView(context, imageId, context.getResources().getString(colorId));
    }
}
