package it.niedermann.nextcloud.deck.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import it.niedermann.android.util.DimensionUtil;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.User;

public class OverlappingAvatars extends RelativeLayout {
    final int maxAvatarCount;
    @Px
    final int avatarSize;
    @Px
    final int avatarBorderSize;
    final Drawable borderDrawable;
    @Px
    final int overlapPx;

    public OverlappingAvatars(Context context) {
        this(context, null);
    }

    public OverlappingAvatars(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverlappingAvatars(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        maxAvatarCount = context.getResources().getInteger(R.integer.max_avatar_count);
        avatarBorderSize = DimensionUtil.INSTANCE.dpToPx(context, R.dimen.avatar_size_small_overlapping_border);
        avatarSize = DimensionUtil.INSTANCE.dpToPx(context, R.dimen.avatar_size_small) + avatarBorderSize * 2;
        overlapPx = DimensionUtil.INSTANCE.dpToPx(context, R.dimen.avatar_size_small_overlapping);
        borderDrawable = ContextCompat.getDrawable(context, R.drawable.avatar_border);
        assert borderDrawable != null;
        DrawableCompat.setTint(borderDrawable, ContextCompat.getColor(context, R.color.bg_card));
    }

    public void setAvatars(@NonNull Account account, @NonNull List<User> assignedUsers) {
        @NonNull Context context = getContext();
        removeAllViews();
        RelativeLayout.LayoutParams avatarLayoutParams;
        int avatarCount;
        for (avatarCount = 0; avatarCount < assignedUsers.size() && avatarCount < maxAvatarCount; avatarCount++) {
            avatarLayoutParams = new RelativeLayout.LayoutParams(avatarSize, avatarSize);
            avatarLayoutParams.setMargins(0, 0, avatarCount * overlapPx, 0);
            avatarLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

            final ImageView avatar = new ImageView(context);
            avatar.setLayoutParams(avatarLayoutParams);
            avatar.setPadding(avatarBorderSize, avatarBorderSize, avatarBorderSize, avatarBorderSize);

            avatar.setBackground(borderDrawable);
            addView(avatar);
            avatar.requestLayout();
            Glide.with(context)
                    .load(account.getUrl() + "/index.php/avatar/" + Uri.encode(assignedUsers.get(avatarCount).getUid()) + "/" + avatarSize)
                    .placeholder(R.drawable.ic_person_grey600_24dp)
                    .error(R.drawable.ic_person_grey600_24dp)
                    .apply(RequestOptions.circleCropTransform())
                    .into(avatar);
        }

        // Recalculate container size based on avatar count
        int size = overlapPx * (avatarCount - 1) + avatarSize;
        ViewGroup.LayoutParams rememberParam = getLayoutParams();
        rememberParam.width = size;
        setLayoutParams(rememberParam);
    }
}
