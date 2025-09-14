package it.niedermann.nextcloud.deck.deprecated.ui.exception.tips;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

@SuppressWarnings("WeakerAccess")
public class TipsModel {
    @StringRes
    private final int text;
    @Nullable
    private final Intent actionIntent;

    TipsModel(@StringRes int text, @Nullable Intent actionIntent) {
        this.text = text;
        this.actionIntent = actionIntent;
    }

    @StringRes
    public int getText() {
        return this.text;
    }

    @Nullable
    public Intent getActionIntent() {
        return this.actionIntent;
    }
}