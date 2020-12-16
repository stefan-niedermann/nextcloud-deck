package it.niedermann.android.markdown;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

import it.niedermann.android.markdown.markwon.MarkwonMarkdownUtil;

/**
 * Tests the NoteUtil
 * Created by stefan on 06.10.15.
 */
public class MarkwonMarkdownUtilTest extends TestCase {

    public void testGetStartOfLine() {
        //language=md
        StringBuilder test = new StringBuilder(
                "# Test-Note\n" + // line start 0
                        "\n" + // line start 12
                        "- [ ] this is a test note\n" + // line start 13
                        "- [x] test\n" + // line start 39
                        "[test](https://example.com)\n" + // line start 50
                        "\n" + // line start 77
                        "\n" // line start 78
        );

        for (int i = 0; i < test.length(); i++) {
            int startOfLine = MarkwonMarkdownUtil.getStartOfLine(test, i);
            if (i <= 11) {
                assertEquals(0, startOfLine);
            } else if (i <= 12) {
                assertEquals(12, startOfLine);
            } else if (i <= 38) {
                assertEquals(13, startOfLine);
            } else if (i <= 49) {
                assertEquals(39, startOfLine);
            } else if (i <= 77) {
                assertEquals(50, startOfLine);
            } else if (i <= 78) {
                assertEquals(78, startOfLine);
            } else if (i <= 79) {
                assertEquals(79, startOfLine);
            }
        }
    }

    public void testGetEndOfLine() {
        //language=md
        StringBuilder test = new StringBuilder(
                "# Test-Note\n" + // line 0 - 11
                        "\n" + // line 12 - 12
                        "- [ ] this is a test note\n" + // line 13 - 38
                        "- [x] test\n" + // line start 39 - 49
                        "[test](https://example.com)\n" + // line 50 - 77
                        "\n" + // line 77 - 78
                        "\n" // line 78 - 79
        );

        for (int i = 0; i < test.length(); i++) {
            int endOfLine = MarkwonMarkdownUtil.getEndOfLine(test, i);
            if (i <= 11) {
                assertEquals(11, endOfLine);
            } else if (i <= 12) {
                assertEquals(12, endOfLine);
            } else if (i <= 38) {
                assertEquals(38, endOfLine);
            } else if (i <= 49) {
                assertEquals(49, endOfLine);
            } else if (i <= 77) {
                assertEquals(77, endOfLine);
            } else if (i <= 78) {
                assertEquals(78, endOfLine);
            } else if (i <= 79) {
                assertEquals(79, endOfLine);
            }
        }
    }

    public void testLineStartsWithCheckbox() {
        Map<String, Boolean> lines = new HashMap<>();
        lines.put("- [ ] ", true);
        lines.put("- [x] ", true);
        lines.put("* [ ] ", true);
        lines.put("* [x] ", true);
        lines.put("- [ ]", true);
        lines.put("- [x]", true);
        lines.put("* [ ]", true);
        lines.put("* [x]", true);

        lines.put("-[ ] ", false);
        lines.put("-[x] ", false);
        lines.put("*[ ] ", false);
        lines.put("*[x] ", false);
        lines.put("-[ ]", false);
        lines.put("-[x]", false);
        lines.put("*[ ]", false);
        lines.put("*[x]", false);

        lines.put("- [] ", false);
        lines.put("* [] ", false);
        lines.put("- []", false);
        lines.put("* []", false);

        lines.put("-[] ", false);
        lines.put("*[] ", false);
        lines.put("-[]", false);
        lines.put("*[]", false);

        lines.forEach((key, value) -> assertEquals(value, (Boolean) MarkwonMarkdownUtil.lineStartsWithCheckbox(key)));
    }

    public void testTogglePunctuation() {
        StringBuilder builder;

        // Add italic
        builder = new StringBuilder("Lorem ipsum dolor sit amet.");
        assertEquals(13, MarkwonMarkdownUtil.togglePunctuation(builder, 6, 11, "*"));
        assertEquals("Lorem *ipsum* dolor sit amet.", builder.toString());

        // Remove italic
        builder = new StringBuilder("Lorem *ipsum* dolor sit amet.");
        assertEquals(11, MarkwonMarkdownUtil.togglePunctuation(builder, 7, 12, "*"));
        assertEquals("Lorem ipsum dolor sit amet.", builder.toString());

        // Add bold
        builder = new StringBuilder("Lorem ipsum dolor sit amet.");
        assertEquals(15, MarkwonMarkdownUtil.togglePunctuation(builder, 6, 11, "**"));
        assertEquals("Lorem **ipsum** dolor sit amet.", builder.toString());

        // Remove bold
        builder = new StringBuilder("Lorem **ipsum** dolor sit amet.");
        assertEquals(11, MarkwonMarkdownUtil.togglePunctuation(builder, 8, 13, "**"));
        assertEquals("Lorem ipsum dolor sit amet.", builder.toString());

        // Add strike
        builder = new StringBuilder("Lorem ipsum dolor sit amet.");
        assertEquals(15, MarkwonMarkdownUtil.togglePunctuation(builder, 6, 11, "~~"));
        assertEquals("Lorem ~~ipsum~~ dolor sit amet.", builder.toString());

        // Remove strike
        builder = new StringBuilder("Lorem ~~ipsum~~ dolor sit amet.");
        assertEquals(11, MarkwonMarkdownUtil.togglePunctuation(builder, 8, 13, "~~"));
        assertEquals("Lorem ipsum dolor sit amet.", builder.toString());

        // Add italic at first position
        builder = new StringBuilder("Lorem ipsum dolor sit amet.");
        assertEquals(7, MarkwonMarkdownUtil.togglePunctuation(builder, 0, 5, "*"));
        assertEquals("*Lorem* ipsum dolor sit amet.", builder.toString());

        // Remove italic from first position
        builder = new StringBuilder("*Lorem* ipsum dolor sit amet.");
        assertEquals(5, MarkwonMarkdownUtil.togglePunctuation(builder, 1, 6, "*"));
        assertEquals("Lorem ipsum dolor sit amet.", builder.toString());

        // Add italic at last position
        builder = new StringBuilder("Lorem ipsum dolor sit amet.");
        assertEquals(29, MarkwonMarkdownUtil.togglePunctuation(builder, 22, 27, "*"));
        assertEquals("Lorem ipsum dolor sit *amet.*", builder.toString());

        // Remove italic from last position
        builder = new StringBuilder("Lorem ipsum dolor sit *amet.*");
        assertEquals(27, MarkwonMarkdownUtil.togglePunctuation(builder, 23, 28, "*"));
        assertEquals("Lorem ipsum dolor sit amet.", builder.toString());

        // Special use-case: toggle from italic to bold and back

        // Toggle italic on bold text
//        builder = new StringBuilder("Lorem **ipsum** dolor sit amet.");
//        assertEquals(17, MarkwonMarkdownUtil.togglePunctuation(builder, 8, 13, "*"));
//        assertEquals("Lorem ***ipsum*** dolor sit amet.", builder.toString());

        // Toggle bold on italic text
//        builder = new StringBuilder("Lorem *ipsum* dolor sit amet.");
//        assertEquals(17, MarkwonMarkdownUtil.togglePunctuation(builder, 7, 12, "**"));
//        assertEquals("Lorem ***ipsum*** dolor sit amet.", builder.toString());
    }

    public void testInsertLink() {
        StringBuilder builder;

        // Add link without clipboardUrl to normal text
        builder = new StringBuilder("Lorem ipsum dolor sit amet.");
        assertEquals(14, MarkwonMarkdownUtil.insertLink(builder, 6, 11, null));
        assertEquals("Lorem [ipsum]() dolor sit amet.", builder.toString());

        // Add link without clipboardUrl to url
        builder = new StringBuilder("Lorem https://example.com dolor sit amet.");
        assertEquals(7, MarkwonMarkdownUtil.insertLink(builder, 6, 25, null));
        assertEquals("Lorem [](https://example.com) dolor sit amet.", builder.toString());

        // Add link with clipboardUrl to normal text
        builder = new StringBuilder("Lorem ipsum dolor sit amet.");
        assertEquals(33, MarkwonMarkdownUtil.insertLink(builder, 6, 11, "https://example.com"));
        assertEquals("Lorem [ipsum](https://example.com) dolor sit amet.", builder.toString());

        // Add link with clipboardUrl to url
        builder = new StringBuilder("Lorem https://example.com dolor sit amet.");
        assertEquals(46, MarkwonMarkdownUtil.insertLink(builder, 6, 25, "https://example.de"));
        assertEquals("Lorem [https://example.com](https://example.de) dolor sit amet.", builder.toString());
    }
}