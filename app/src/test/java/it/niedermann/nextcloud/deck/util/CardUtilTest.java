package it.niedermann.nextcloud.deck.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ApplicationProvider;

import com.google.common.collect.Lists;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;

import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.full.FullCard;

@RunWith(RobolectricTestRunner.class)
public class CardUtilTest {

    @Test
    public void testGetCardContentAsString() {
        final var context = ApplicationProvider.getApplicationContext();

        final var card = new Card();
        final var fullCard = new FullCard();
        card.setTitle("Foo");
        card.setDescription("Bar");
        card.setDueDate(null);
        fullCard.setCard(card);
        fullCard.setLabels(Collections.emptyList());

        assertEquals("Bar", CardUtil.getCardContentAsString(context, fullCard));

        final var testDate = LocalDateTime.of(2011, 10, 6, 6, 30, 50, 100000).toInstant(ZoneOffset.UTC);
        card.setDueDate(testDate);

        assertTrue(CardUtil.getCardContentAsString(context, fullCard).startsWith("Bar\n" +
                "Due date:"));

        card.setDueDate(null);
        final var testLabel1 = new Label();
        testLabel1.setTitle("Baz");
        final var testLabel2 = new Label();
        testLabel2.setTitle("Pow");
        fullCard.setLabels(Lists.newArrayList(testLabel1));

        assertEquals("Bar\n" +
                "Tags: Baz", CardUtil.getCardContentAsString(context, fullCard));

        fullCard.setLabels(Lists.newArrayList(testLabel1, testLabel2));

        assertEquals("Bar\n" +
                "Tags: Baz, Pow", CardUtil.getCardContentAsString(context, fullCard));

        card.setDueDate(testDate);

        assertTrue(CardUtil.getCardContentAsString(context, fullCard).startsWith("Bar\n" +
                "Due date:"));
        assertTrue(CardUtil.getCardContentAsString(context, fullCard).endsWith("\n" +
                "Tags: Baz, Pow"));
    }

    @Test
    public void testCardHasCommentsOrAttachments() {
        final var fullCard = new FullCard();

        assertFalse(CardUtil.cardHasCommentsOrAttachments(fullCard));

        fullCard.setCommentIDs(Collections.singletonList(1L));
        assertTrue(CardUtil.cardHasCommentsOrAttachments(fullCard));

        fullCard.setCommentIDs(null);
        assertFalse(CardUtil.cardHasCommentsOrAttachments(fullCard));

        fullCard.setAttachments(Collections.singletonList(new Attachment()));
        assertTrue(CardUtil.cardHasCommentsOrAttachments(fullCard));

        fullCard.setCommentIDs(Collections.singletonList(1L));
        assertTrue(CardUtil.cardHasCommentsOrAttachments(fullCard));
    }

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
    public void getLineWithoutMarkDown() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final var getLineWithoutMarkDown = CardUtil.class.getDeclaredMethod("getLineWithoutMarkDown", String.class, int.class);
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
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void isEmptyLine() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        final var isEmptyLine = CardUtil.class.getDeclaredMethod("isEmptyLine", String.class);
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
    }
}
