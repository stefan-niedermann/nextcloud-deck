package it.niedermann.nextcloud.deck.ui.card.comments;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.lifecycle.LifecycleOwner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.card.comments.util.CommentsUtil;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.util.DimensionUtil.dpToPx;

public class CardCommentsMentionProposer implements TextWatcher {

    private final int avatarSize;
    @NonNull
    private final SyncManager syncManager;
    @NonNull
    private final LinearLayout.LayoutParams layoutParams;
    @NonNull
    private final LifecycleOwner owner;
    @NonNull
    private final Account account;
    private final long boardLocalId;
    @NonNull
    private final EditText editText;
    @NonNull
    private final LinearLayout mentionProposerLayout;

    @NonNull
    private final List<User> users = new ArrayList<>();

    public CardCommentsMentionProposer(@NonNull LifecycleOwner owner, @NonNull Account account, long boardLocalId, @NonNull EditText editText, @NonNull LinearLayout avatarProposerLayout) {
        this.owner = owner;
        this.account = account;
        this.boardLocalId = boardLocalId;
        this.editText = editText;
        this.mentionProposerLayout = avatarProposerLayout;
        syncManager = new SyncManager(editText.getContext());
        avatarSize = dpToPx(mentionProposerLayout.getContext(), R.dimen.avatar_size_small);
        layoutParams = new LinearLayout.LayoutParams(avatarSize, avatarSize);
        layoutParams.setMarginEnd(dpToPx(mentionProposerLayout.getContext(), R.dimen.spacer_1x));
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        final int selectionStart = editText.getSelectionStart();
        final int selectionEnd = editText.getSelectionEnd();
        final Pair<String, Integer> mentionProposal = CommentsUtil.getUserNameForMentionProposal(s.toString(), selectionStart);
        if (mentionProposal == null || (mentionProposal.first != null && mentionProposal.first.length() == 0) || selectionStart != selectionEnd) {
            mentionProposerLayout.removeAllViews();
            mentionProposerLayout.setVisibility(View.GONE);
            this.users.clear();
        } else {
            if (mentionProposal.first != null && mentionProposal.second != null) {
                observeOnce(syncManager.searchUserByUidOrDisplayName(account.getId(), boardLocalId, -1L, mentionProposal.first), owner, (users) -> {
                    if (!users.equals(this.users)) {
                        mentionProposerLayout.removeAllViews();
                        for (User user : users) {
                            final ImageView avatar = new ImageView(mentionProposerLayout.getContext());
                            avatar.setLayoutParams(layoutParams);
                            updateListenerOfView(avatar, s, mentionProposal, user);

                            mentionProposerLayout.addView(avatar);

                            Glide.with(avatar.getContext())
                                    .load(account.getUrl() + "/index.php/avatar/" + Uri.encode(user.getUid()) + "/" + avatarSize)
                                    .error(R.drawable.ic_person_grey600_24dp)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(avatar);
                        }
                        this.users.clear();
                        this.users.addAll(users);
                    } else {
                        int i = 0;
                        for (User user : users) {
                            updateListenerOfView(mentionProposerLayout.getChildAt(i), s, mentionProposal, user);
                            i++;
                        }
                    }
                });
            } else {
                this.users.clear();
                mentionProposerLayout.removeAllViews();
            }
            mentionProposerLayout.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateListenerOfView(View avatar, CharSequence s, Pair<String, Integer> mentionProposal, User user) {
        avatar.setOnClickListener((c) -> {
            editText.setText(
                    s.subSequence(0, mentionProposal.second) +
                            user.getUid() +
                            s.subSequence(mentionProposal.second + mentionProposal.first.length(), s.length())
            );
            editText.setSelection(mentionProposal.second + user.getUid().length());
            mentionProposerLayout.setVisibility(View.GONE);
        });
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
