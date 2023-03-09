package it.niedermann.nextcloud.deck.ui.theme;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceViewHolder;

import it.niedermann.nextcloud.deck.repository.BaseRepository;

public class ThemedPreferenceCategory extends PreferenceCategory {

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        final var view = holder.itemView.findViewById(android.R.id.title);
        final var context = getContext();
        final var repo = new BaseRepository(context);

        if (view instanceof TextView) {
            repo.getCurrentAccountId()
                    .thenComposeAsync(repo::getCurrentAccountColor)
                    .thenAcceptAsync(accountColor -> {
                        final var utils = ThemeUtils.of(accountColor, context);
                        utils.platform.colorTextView((TextView) view);
                    });
        }
    }

    public ThemedPreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ThemedPreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ThemedPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThemedPreferenceCategory(Context context) {
        super(context);
    }
}
