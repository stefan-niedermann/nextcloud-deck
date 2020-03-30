package it.niedermann.nextcloud.deck.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;

import static android.content.Context.CLIPBOARD_SERVICE;

public class ClipboardUtil {

    private ClipboardUtil() {
    }

    public static boolean copyToClipboard(@NonNull Context context, @Nullable String text) {
        return copyToClipboard(context, text, text);
    }

    public static boolean copyToClipboard(@NonNull Context context, @Nullable String label, @Nullable String text) {
        final ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        if (clipboardManager == null) {
            DeckLog.error("ClipboardManager is null");
            Toast.makeText(context, R.string.could_not_copy_to_clipboard, Toast.LENGTH_LONG).show();
            return false;
        }
        final ClipData clipData = ClipData.newPlainText(label, text);
        clipboardManager.setPrimaryClip(clipData);
        DeckLog.info("Copied to clipboard: [" + label + "] \"" + text + "\"");
        Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
        return true;
    }
}
