package it.niedermann.nextcloud.deck.util;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.stream.Collectors;

import it.niedermann.android.markdown.MarkdownUtil;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.full.FullCard;

public class CardUtil {

    private CardUtil() {
        throw new UnsupportedOperationException("This class must not get instantiated");
    }

    /**
     * @return a human readable String containing the description, due date and tags of the given {@param fullCard}
     */
    @NonNull
    public static String getCardContentAsString(@NonNull Context context, @NonNull FullCard fullCard) {
        final var card = fullCard.getCard();
        final var text = new StringBuilder(card.getDescription());
        if(card.getDueDate() != null) {
            if(!TextUtils.isEmpty(text)) {
                text.append("\n");
            }
            text.append(context.getString(R.string.share_content_duedate, card.getDueDate().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))));
        }
        if(fullCard.getLabels() != null && !fullCard.getLabels().isEmpty()) {
            if(!TextUtils.isEmpty(text)) {
                text.append("\n");
            }
            text.append(context.getString(R.string.share_content_labels, fullCard.getLabels().stream().map(Label::getTitle).collect(Collectors.joining(", "))));
        }
        return text.toString();
    }

    public static boolean cardHasCommentsOrAttachments(@NonNull FullCard fullCard) {
        return fullCard.getCommentCount() > 0 || (fullCard.getAttachments() != null && !fullCard.getAttachments().isEmpty());
    }

    @NonNull
    public static String generateTitleFromDescription(@Nullable String description) {
        if(description == null) return "";
        return getLineWithoutMarkDown(description, 0);
    }

    /**
     * Reads the requested line and strips all MarkDown. If line is empty, it will go ahead to find the next not-empty line.
     *
     * @param content    String
     * @param lineNumber int
     * @return lineContent String
     */
    @NonNull
    private static String getLineWithoutMarkDown(@NonNull String content, @SuppressWarnings("SameParameterValue") int lineNumber) {
        if (content.contains("\n")) {
            final var lines = content.split("\n");
            int currentLine = lineNumber;
            while (currentLine < lines.length && isEmptyLine(lines[currentLine])) {
                currentLine++;
            }
            if (currentLine < lines.length) {
                return MarkdownUtil.removeMarkdown(lines[currentLine]);
            }
        } else {
            return content;
        }
        return "";
    }

    /**
     * Checks if a line is empty.
     * <pre>
     * " "    -> empty
     * "\n"   -> empty
     * "\n "  -> empty
     * " \n"  -> empty
     * " \n " -> empty
     * </pre>
     *
     * @param line String - a single Line which ends with \n
     * @return boolean isEmpty
     */
    private static boolean isEmptyLine(@NonNull String line) {
        return MarkdownUtil.removeMarkdown(line).trim().length() == 0;
    }
}
