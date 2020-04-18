package it.niedermann.nextcloud.deck.ui.view;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.User;

import static it.niedermann.nextcloud.deck.util.DimensionUtil.dpToPx;

public class OverlappingAvatars extends RelativeLayout {
    final int maxAvatarCount;
    final int avatarSize;
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
        avatarSize = dpToPx(context, R.dimen.avatar_size_small);
        overlapPx = dpToPx(context, R.dimen.avatar_size_small_overlapping);
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
            addView(avatar);
            avatar.requestLayout();
            Glide.with(context)
                    .load(account.getUrl() + "/index.php/avatar/" + Uri.encode(assignedUsers.get(avatarCount).getUid()) + "/" + avatarSize)
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
