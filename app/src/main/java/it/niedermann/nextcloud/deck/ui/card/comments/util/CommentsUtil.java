package it.niedermann.nextcloud.deck.ui.card.comments.util;

public class CommentsUtil {

    public static String getUserNameForMentionProposal(String text, int cursorPosition) {
        String result = null;

        if (text != null) {
            // find start of relevant substring
            int cursor = cursorPosition;
            if (cursor < 1) {
                return null;
            }
            int start = 0;
            while (cursor > 0) {
                cursor--;
                if (Character.isWhitespace(text.charAt(cursor))) {
                    start = cursor + 1;
                    break;
                }
            }
            if (text.length()-1 < start || text.charAt(start) != '@') {
                return null;
            }

            // find end of relevant substring
            cursor = cursorPosition;
            int textLength = text.length();
            int end = textLength;
            while (cursor < textLength) {
                if (Character.isWhitespace(text.charAt(cursor))) {
                    end = cursor;
                    break;
                }
                cursor++;
            }

            result = text.substring(start+1, end);

        }

        return result;
    }
}
