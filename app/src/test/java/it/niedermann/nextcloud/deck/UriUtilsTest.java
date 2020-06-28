package it.niedermann.nextcloud.deck;

import org.junit.Test;

import it.niedermann.nextcloud.deck.util.UriUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class UriUtilsTest {

    @Test
    public void testParseBoardRemoteId() {
        assertEquals(45, UriUtils.parseBoardRemoteId("https://example.com/index.php/apps/deck/#/board/45/card/535"));
        assertEquals(13, UriUtils.parseBoardRemoteId("https://example.com/index.php/apps/deck/#/board/13/card/535"));
        assertEquals(1, UriUtils.parseBoardRemoteId("https://example.com/index.php/apps/deck/#/board/1/card/535"));

        final String[] invalidUris = new String[]{
                "",
                "https://example.com/index.php/apps/deck/#/board/card/535",
                "https://example.com/index.php/apps/deck/#/board/blah/card/535",
                "https://example.com/index.php/apps/deck/#/board/-1/card/535"
        };
        for (String invalidUri : invalidUris) {
            assertThrows(invalidUri, IllegalArgumentException.class, () -> UriUtils.parseBoardRemoteId(invalidUri));
        }
    }
}
