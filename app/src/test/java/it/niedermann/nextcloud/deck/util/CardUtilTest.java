package it.niedermann.nextcloud.deck.util;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CardUtilTest {

    @Test
    public void generateTitleFromDescription() {
        final String content = "" +
                "# Heading\n" +
                "- Test \n" +
                "\n" +
                "\n" +
                "This is **bold**";
        assertEquals("Heading", CardUtil.generateTitleFromDescription(content));
        assertEquals("Test", CardUtil.generateTitleFromDescription("Test"));
        assertEquals("", CardUtil.generateTitleFromDescription(null));
    }

    @Test
    public void getLineWithoutMarkDown() {
        try {
            final Method getLineWithoutMarkDown = CardUtil.class.getDeclaredMethod("getLineWithoutMarkDown", String.class, int.class);
            getLineWithoutMarkDown.setAccessible(true);
            final String content = "" +
                    "# Heading\n" +
                    "- Test \n" +
                    "\n" +
                    "\n" +
                    "This is **bold**";
            assertEquals("Test", getLineWithoutMarkDown.invoke(null, "Test", 0));
            assertEquals("Heading", getLineWithoutMarkDown.invoke(null, content, 0));
            assertEquals("Test", getLineWithoutMarkDown.invoke(null, content, 1));
            assertEquals("This is bold", getLineWithoutMarkDown.invoke(null, content, 2));
            assertEquals("This is bold", getLineWithoutMarkDown.invoke(null, content, 3));
            assertEquals("This is bold", getLineWithoutMarkDown.invoke(null, content, 4));
            assertEquals("", getLineWithoutMarkDown.invoke(null, content, 5));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void isEmptyLine() {
        try {
            final Method isEmptyLine = CardUtil.class.getDeclaredMethod("isEmptyLine", String.class);
            isEmptyLine.setAccessible(true);
            assertTrue((Boolean) isEmptyLine.invoke(null, ""));
            assertTrue((Boolean) isEmptyLine.invoke(null, "#   "));
            assertTrue((Boolean) isEmptyLine.invoke(null, "\n"));
            assertTrue((Boolean) isEmptyLine.invoke(null, "- "));
            assertTrue((Boolean) isEmptyLine.invoke(null, "****"));
            assertFalse((Boolean) isEmptyLine.invoke(null, "*italic*"));
            assertFalse((Boolean) isEmptyLine.invoke(null, "__bold__"));
            assertFalse((Boolean) isEmptyLine.invoke(null, "_italic_"));
            assertFalse((Boolean) isEmptyLine.invoke(null, "# Headline\n\n**bold** \n_italic_"));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void removeMarkDown() {
        try {
            final Method removeMarkDown = CardUtil.class.getDeclaredMethod("removeMarkDown", String.class);
            removeMarkDown.setAccessible(true);
            assertEquals("", removeMarkDown.invoke(null, ""));
            assertEquals("Headline", removeMarkDown.invoke(null, "# Headline"));
            assertEquals("Item", removeMarkDown.invoke(null, "- Item"));
            assertEquals("bold", removeMarkDown.invoke(null, "**bold**"));
            assertEquals("italic", removeMarkDown.invoke(null, "*italic*"));
            assertEquals("bold", removeMarkDown.invoke(null, "__bold__"));
            assertEquals("italic", removeMarkDown.invoke(null, "_italic_"));
            assertEquals("Headline\nbold\nitalic", removeMarkDown.invoke(null, "# Headline\n\n**bold** \n_italic_"));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
