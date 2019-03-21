package it.niedermann.nextcloud.deck.util;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Date;

import it.niedermann.nextcloud.deck.R;

public final class ViewUtil {
    private ViewUtil() {}

    public static void addAvatar(Context context, ImageView avatar, String baseUrl, String userId) {
        addAvatar(context, avatar, baseUrl, userId, DimensionUtil.getAvatarDimension(context));
    }

    public static void addAvatar(Context context, ImageView avatar, String baseUrl, String userId, int avatarSize) {
        String uri = baseUrl + "/index.php/avatar/" + Uri.encode(userId) + "/" + avatarSize;
        Glide.with(context)
                .load(uri)
                .apply(RequestOptions.circleCropTransform())
                .into(avatar);
    }

    public static void themeDueDate(Context context, TextView cardDueDate, Date dueDate) {
        long diff = DateUtil.getDayDifference(new Date(), dueDate);

        int backgroundDrawable = 0;
        int textColor = R.color.default_text_color;

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
}
