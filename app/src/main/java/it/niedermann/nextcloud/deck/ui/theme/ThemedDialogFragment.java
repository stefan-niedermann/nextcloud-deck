package it.niedermann.nextcloud.deck.ui.theme;

import static it.niedermann.nextcloud.deck.ui.theme.ThemeUtils.readBrandMainColor;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public abstract class ThemedDialogFragment extends DialogFragment implements Themed {

    @Override
    public void onStart() {
        super.onStart();

        @Nullable final var context = getContext();
        if (context != null) {
            applyTheme(readBrandMainColor(context));
        }
    }
}
