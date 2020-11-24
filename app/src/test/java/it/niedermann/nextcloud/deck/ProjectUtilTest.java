package it.niedermann.nextcloud.deck;

import org.junit.Test;

import it.niedermann.nextcloud.deck.util.ProjectUtil;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThrows;

public class ProjectUtilTest {
    @Test
    public void extractBoardIdAndCardIdFromUrl() {
        // Valid board URLs with # and with index.php
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("index.php/apps/deck/#/board/4"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("/index.php/apps/deck/#/board/4"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("example.com/index.php/apps/deck/#/board/4"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("http://example.com/index.php/apps/deck/#/board/4"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/index.php/apps/deck/#/board/4"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/index.php/apps/deck/#/board/4"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/index.php/apps/deck/#/board/4/card"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/index.php/apps/deck/#/board/4/card/"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/index.php/apps/deck/#/board/4/card/0"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/index.php/apps/deck/#/board/4/foo"), new long[]{4});

        // Valid board URLs with # and without index.php
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("apps/deck/#/board/4"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("/apps/deck/#/board/4"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("example.com/apps/deck/#/board/4"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("http://example.com/apps/deck/#/board/4"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/apps/deck/#/board/4"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/apps/deck/#/board/4"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/apps/deck/#/board/4/card"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/apps/deck/#/board/4/card/"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/apps/deck/#/board/4/card/0"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/apps/deck/#/board/4/foo"), new long[]{4});

        // Valid board URLs without # and with index.php
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("index.php/apps/deck/board/4"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("/index.php/apps/deck/board/4"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("example.com/index.php/apps/deck/board/4"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("http://example.com/index.php/apps/deck/board/4"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/index.php/apps/deck/board/4"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/index.php/apps/deck/board/4"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/index.php/apps/deck/board/4/card"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/index.php/apps/deck/board/4/card/"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/index.php/apps/deck/board/4/card/0"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/index.php/apps/deck/board/4/foo"), new long[]{4});

        // Valid board URLs without # and without index.php
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("apps/deck/board/4"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("/apps/deck/board/4"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("example.com/apps/deck/board/4"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("http://example.com/apps/deck/board/4"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/apps/deck/board/4"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/apps/deck/board/4"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/apps/deck/board/4/card"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/apps/deck/board/4/card/"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/apps/deck/board/4/card/0"), new long[]{4});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/apps/deck/board/4/foo"), new long[]{4});

        // Valid card URLs with # and with index.php
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("index.php/apps/deck/#/board/4/card/6"), new long[]{4, 6});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("/index.php/apps/deck/#/board/4/card/6"), new long[]{4, 6});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("example.com/index.php/apps/deck/#/board/4/card/6"), new long[]{4, 6});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("http://example.com/index.php/apps/deck/#/board/4/card/6"), new long[]{4, 6});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/index.php/apps/deck/#/board/4/card/6"), new long[]{4, 6});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/index.php/apps/deck/#/board/4/card/6/"), new long[]{4, 6});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/index.php/apps/deck/#/board/4/card/6"), new long[]{4, 6});

        // Valid card URLs with # and without index.php
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("apps/deck/#/board/4/card/6"), new long[]{4, 6});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("/apps/deck/#/board/4/card/6"), new long[]{4, 6});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("example.com/apps/deck/#/board/4/card/6"), new long[]{4, 6});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("http://example.com/apps/deck/#/board/4/card/6"), new long[]{4, 6});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/apps/deck/#/board/4/card/6"), new long[]{4, 6});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/apps/deck/#/board/4/card/6/"), new long[]{4, 6});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/apps/deck/#/board/4/card/6"), new long[]{4, 6});

        // Valid card URLs without # and with index.php
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("index.php/apps/deck/#/board/4/card/6"), new long[]{4, 6});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("/index.php/apps/deck/#/board/4/card/6"), new long[]{4, 6});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("example.com/index.php/apps/deck/#/board/4/card/6"), new long[]{4, 6});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("http://example.com/index.php/apps/deck/#/board/4/card/6"), new long[]{4, 6});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/index.php/apps/deck/#/board/4/card/6"), new long[]{4, 6});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/index.php/apps/deck/#/board/4/card/6/"), new long[]{4, 6});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/index.php/apps/deck/#/board/4/card/6"), new long[]{4, 6});

        // Valid card URLs without # and without index.php
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("apps/deck/#/board/4/card/6"), new long[]{4, 6});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("/apps/deck/#/board/4/card/6"), new long[]{4, 6});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("example.com/apps/deck/#/board/4/card/6"), new long[]{4, 6});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("http://example.com/apps/deck/#/board/4/card/6"), new long[]{4, 6});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/apps/deck/#/board/4/card/6"), new long[]{4, 6});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/apps/deck/#/board/4/card/6/"), new long[]{4, 6});
        assertArrayEquals(ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/apps/deck/#/board/4/card/6"), new long[]{4, 6});

        // URLs to talk
        assertThrows(IllegalArgumentException.class, () -> ProjectUtil.extractBoardIdAndCardIdFromUrl("index.php/call/qkzhe5k2"));
        assertThrows(IllegalArgumentException.class, () -> ProjectUtil.extractBoardIdAndCardIdFromUrl("/index.php/call/qkzhe5k2"));
        assertThrows(IllegalArgumentException.class, () -> ProjectUtil.extractBoardIdAndCardIdFromUrl("example.com/index.php/call/qkzhe5k2"));
        assertThrows(IllegalArgumentException.class, () -> ProjectUtil.extractBoardIdAndCardIdFromUrl("http://example.com/index.php/call/qkzhe5k2"));
        assertThrows(IllegalArgumentException.class, () -> ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/index.php/call/qkzhe5k2"));
        assertThrows(IllegalArgumentException.class, () -> ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/index.php/call/qkzhe5k2"));

        // URLs to files
        assertThrows(IllegalArgumentException.class, () -> ProjectUtil.extractBoardIdAndCardIdFromUrl("index.php/call/qkzhe5k2"));
        assertThrows(IllegalArgumentException.class, () -> ProjectUtil.extractBoardIdAndCardIdFromUrl("/index.php/call/qkzhe5k2"));
        assertThrows(IllegalArgumentException.class, () -> ProjectUtil.extractBoardIdAndCardIdFromUrl("example.com/index.php/call/qkzhe5k2"));
        assertThrows(IllegalArgumentException.class, () -> ProjectUtil.extractBoardIdAndCardIdFromUrl("http://example.com/index.php/call/qkzhe5k2"));
        assertThrows(IllegalArgumentException.class, () -> ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/index.php/call/qkzhe5k2"));
        assertThrows(IllegalArgumentException.class, () -> ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/index.php/call/qkzhe5k2"));

        // Invalid URLs
        //noinspection ConstantConditions
        assertThrows(IllegalArgumentException.class, () -> ProjectUtil.extractBoardIdAndCardIdFromUrl(null));
        assertThrows(IllegalArgumentException.class, () -> ProjectUtil.extractBoardIdAndCardIdFromUrl(""));
        assertThrows(IllegalArgumentException.class, () -> ProjectUtil.extractBoardIdAndCardIdFromUrl("/index.php/apps/deck/#/board/0"));
        assertThrows(IllegalArgumentException.class, () -> ProjectUtil.extractBoardIdAndCardIdFromUrl("/index.php/apps/deck/#/board/0/card/3"));
        assertThrows(IllegalArgumentException.class, () -> ProjectUtil.extractBoardIdAndCardIdFromUrl("/index.php/apps/deck/#/board//card/3"));
        assertThrows(IllegalArgumentException.class, () -> ProjectUtil.extractBoardIdAndCardIdFromUrl("/index.php/apps/deck/board/0"));
        assertThrows(IllegalArgumentException.class, () -> ProjectUtil.extractBoardIdAndCardIdFromUrl("/index.php/apps/deck/board/0/card/3"));
        assertThrows(IllegalArgumentException.class, () -> ProjectUtil.extractBoardIdAndCardIdFromUrl("/index.php/apps/deck/board//card/3"));
    }
}
