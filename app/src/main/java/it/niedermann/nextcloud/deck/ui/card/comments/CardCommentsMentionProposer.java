package it.niedermann.nextcloud.deck.ui.card.comments;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

public class CardCommentsMentionProposer implements TextWatcher {

    @NonNull
    private final LinearLayout mentionProposerLayout;

    public CardCommentsMentionProposer(@NonNull LinearLayout avatarProposerLayout) {
        this.mentionProposerLayout = avatarProposerLayout;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(TextUtils.isEmpty(s) || s.charAt(s.length() -1 ) == ' ') {
            mentionProposerLayout.removeAllViews();
            return;
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
