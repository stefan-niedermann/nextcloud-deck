package it.niedermann.nextcloud.deck.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.core.graphics.drawable.DrawableCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Date;

import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.R;

public final class ViewUtil {
    private ViewUtil() {
    }

    public static void addAvatar(Context context, ImageView avatar, String baseUrl, String userId, @DrawableRes int errorResource) {
        addAvatar(context, avatar, baseUrl, userId, DimensionUtil.getAvatarDimension(context), errorResource);
    }

    public static void addAvatar(Context context, ImageView avatar, String baseUrl, String userId, int avatarSize, @DrawableRes int errorResource) {
        String uri = baseUrl + "/index.php/avatar/" + Uri.encode(userId) + "/" + avatarSize;
        Glide.with(context)
                .load(uri)
                .error(errorResource)
                .apply(RequestOptions.circleCropTransform())
                .into(avatar);
    }

    public static void themeDueDate(Context context, TextView cardDueDate, Date dueDate) {
        long diff = DateUtil.getDayDifference(new Date(), dueDate);

        int backgroundDrawable = 0;
        int textColor = Application.getAppTheme(context) ? R.color.dark_fg_primary : R.color.black;

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
    }

    public static Drawable getTintedImageView(Context context, int imageId, String color) {
        Drawable drawable;
        Drawable wrapped;
        drawable = context.getResources().getDrawable(imageId);
        wrapped = DrawableCompat.wrap(drawable).mutate();
        DrawableCompat.setTint(wrapped, Color.parseColor(color));
        return drawable;
    }

    public static Drawable getTintedImageView(Context context, int imageId, int colorId) {
        return getTintedImageView(context, imageId, context.getResources().getString(colorId));
    }
}
