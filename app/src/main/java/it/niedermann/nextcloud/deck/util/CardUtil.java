package it.niedermann.nextcloud.deck.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.regex.Pattern;

public class CardUtil {
    private static final Pattern pLists = Pattern.compile("^\\s*[*+-]\\s+", Pattern.MULTILINE);
    private static final Pattern pHeadings = Pattern.compile("^#+\\s+(.*?)\\s*#*$", Pattern.MULTILINE);
    private static final Pattern pHeadingLine = Pattern.compile("^(?:=*|-*)$", Pattern.MULTILINE);
    private static final Pattern pEmphasis = Pattern.compile("(\\*+|_+)(.*?)\\1", Pattern.MULTILINE);
    private static final Pattern pSpace1 = Pattern.compile("^\\s+", Pattern.MULTILINE);
    private static final Pattern pSpace2 = Pattern.compile("\\s+$", Pattern.MULTILINE);

    private CardUtil() {
        // You shall not pass
    }

    @NonNull
    public static String generateTitleFromDescription(String description) {
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
        String line = "";
        if (content.contains("\n")) {
            String[] lines = content.split("\n");
            int currentLine = lineNumber;
            while (currentLine < lines.length && isEmptyLine(lines[currentLine])) {
                currentLine++;
            }
            if (currentLine < lines.length) {
                line = removeMarkDown(lines[currentLine]);
            }
        } else {
            line = content;
        }
        return line;
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
    private static boolean isEmptyLine(@Nullable String line) {
        return removeMarkDown(line).trim().length() == 0;
    }

    /**
     * Strips all MarkDown from the given String
     *
     * @param s String - MarkDown
     * @return Plain Text-String
     */
    @NonNull
    private static String removeMarkDown(@Nullable String s) {
        if (s == null)
            return "";
        s = pLists.matcher(s).replaceAll("");
        s = pHeadings.matcher(s).replaceAll("$1");
        s = pHeadingLine.matcher(s).replaceAll("");
        s = pEmphasis.matcher(s).replaceAll("$2");
        s = pSpace1.matcher(s).replaceAll("");
        s = pSpace2.matcher(s).replaceAll("");
        return s;
    }
}
