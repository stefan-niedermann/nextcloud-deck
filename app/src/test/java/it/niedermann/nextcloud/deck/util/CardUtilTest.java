package it.niedermann.nextcloud.deck.util;

import android.content.Context;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import com.google.common.collect.Lists;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;

import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.full.FullCard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.P})
public class CardUtilTest {

    @Test
    public void testGetCardContentAsString() {
        final Context appContext = ApplicationProvider.getApplicationContext();

        final Card card = new Card();
        final FullCard fullCard = new FullCard();
        card.setTitle("Foo");
        card.setDescription("Bar");
        card.setDueDate(null);
        fullCard.setCard(card);
        fullCard.setLabels(Collections.emptyList());

        assertEquals("Bar", CardUtil.getCardContentAsString(appContext, fullCard));

        final Instant testDate = LocalDateTime.of(2011, 10, 6, 6, 30, 50, 100000).toInstant(ZoneOffset.UTC);
        card.setDueDate(testDate);

        assertTrue(CardUtil.getCardContentAsString(appContext, fullCard).startsWith("Bar\n" +
                "Due date:"));

        card.setDueDate(null);
        final Label testLabel1 = new Label();
        testLabel1.setTitle("Baz");
        final Label testLabel2 = new Label();
        testLabel2.setTitle("Pow");
        fullCard.setLabels(Lists.newArrayList(testLabel1));

        assertEquals("Bar\n" +
                "Tags: Baz", CardUtil.getCardContentAsString(appContext, fullCard));

        fullCard.setLabels(Lists.newArrayList(testLabel1, testLabel2));

        assertEquals("Bar\n" +
                "Tags: Baz, Pow", CardUtil.getCardContentAsString(appContext, fullCard));

        card.setDueDate(testDate);

        assertTrue(CardUtil.getCardContentAsString(appContext, fullCard).startsWith("Bar\n" +
                "Due date:"));
        assertTrue(CardUtil.getCardContentAsString(appContext, fullCard).endsWith("\n" +
                "Tags: Baz, Pow"));
    }

    @Test
    public void testCardHasCommentsOrAttachments() {
        final FullCard fullCard = new FullCard();

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
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void isEmptyLine() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
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
    }
}
