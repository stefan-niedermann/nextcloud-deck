package it.niedermann.nextcloud.deck;

import org.junit.Test;

import it.niedermann.nextcloud.deck.util.ProjectUtil;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThrows;

public class ProjectUtilTest {
    @Test
    public void extractBoardIdAndCardIdFromUrl() {
        // Valid board URLs with # and with index.php
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("index.php/apps/deck/#/board/4"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("/index.php/apps/deck/#/board/4"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("example.com/index.php/apps/deck/#/board/4"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("http://example.com/index.php/apps/deck/#/board/4"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/index.php/apps/deck/#/board/4"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/index.php/apps/deck/#/board/4"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/index.php/apps/deck/#/board/4/card"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/index.php/apps/deck/#/board/4/card/"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/index.php/apps/deck/#/board/4/card/0"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/index.php/apps/deck/#/board/4/foo"));

        // Valid board URLs with # and without index.php
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("apps/deck/#/board/4"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("/apps/deck/#/board/4"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("example.com/apps/deck/#/board/4"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("http://example.com/apps/deck/#/board/4"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/apps/deck/#/board/4"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/apps/deck/#/board/4"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/apps/deck/#/board/4/card"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/apps/deck/#/board/4/card/"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/apps/deck/#/board/4/card/0"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/apps/deck/#/board/4/foo"));

        // Valid board URLs without # and with index.php
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("index.php/apps/deck/board/4"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("/index.php/apps/deck/board/4"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("example.com/index.php/apps/deck/board/4"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("http://example.com/index.php/apps/deck/board/4"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/index.php/apps/deck/board/4"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/index.php/apps/deck/board/4"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/index.php/apps/deck/board/4/card"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/index.php/apps/deck/board/4/card/"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/index.php/apps/deck/board/4/card/0"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/index.php/apps/deck/board/4/foo"));

        // Valid board URLs without # and without index.php
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("apps/deck/board/4"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("/apps/deck/board/4"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("example.com/apps/deck/board/4"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("http://example.com/apps/deck/board/4"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/apps/deck/board/4"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/apps/deck/board/4"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/apps/deck/board/4/card"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/apps/deck/board/4/card/"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/apps/deck/board/4/card/0"));
        assertArrayEquals(new long[]{4}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/apps/deck/board/4/foo"));

        // Valid card URLs with # and with index.php
        assertArrayEquals(new long[]{4, 6}, ProjectUtil.extractBoardIdAndCardIdFromUrl("index.php/apps/deck/#/board/4/card/6"));
        assertArrayEquals(new long[]{4, 6}, ProjectUtil.extractBoardIdAndCardIdFromUrl("/index.php/apps/deck/#/board/4/card/6"));
        assertArrayEquals(new long[]{4, 6}, ProjectUtil.extractBoardIdAndCardIdFromUrl("example.com/index.php/apps/deck/#/board/4/card/6"));
        assertArrayEquals(new long[]{4, 6}, ProjectUtil.extractBoardIdAndCardIdFromUrl("http://example.com/index.php/apps/deck/#/board/4/card/6"));
        assertArrayEquals(new long[]{4, 6}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/index.php/apps/deck/#/board/4/card/6"));
        assertArrayEquals(new long[]{4, 6}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/index.php/apps/deck/#/board/4/card/6/"));
        assertArrayEquals(new long[]{4, 6}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/index.php/apps/deck/#/board/4/card/6"));

        // Valid card URLs with # and without index.php
        assertArrayEquals(new long[]{4, 6}, ProjectUtil.extractBoardIdAndCardIdFromUrl("apps/deck/#/board/4/card/6"));
        assertArrayEquals(new long[]{4, 6}, ProjectUtil.extractBoardIdAndCardIdFromUrl("/apps/deck/#/board/4/card/6"));
        assertArrayEquals(new long[]{4, 6}, ProjectUtil.extractBoardIdAndCardIdFromUrl("example.com/apps/deck/#/board/4/card/6"));
        assertArrayEquals(new long[]{4, 6}, ProjectUtil.extractBoardIdAndCardIdFromUrl("http://example.com/apps/deck/#/board/4/card/6"));
        assertArrayEquals(new long[]{4, 6}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/apps/deck/#/board/4/card/6"));
        assertArrayEquals(new long[]{4, 6}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/apps/deck/#/board/4/card/6/"));
        assertArrayEquals(new long[]{4, 6}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/apps/deck/#/board/4/card/6"));

        // Valid card URLs without # and with index.php
        assertArrayEquals(new long[]{4, 6}, ProjectUtil.extractBoardIdAndCardIdFromUrl("index.php/apps/deck/#/board/4/card/6"));
        assertArrayEquals(new long[]{4, 6}, ProjectUtil.extractBoardIdAndCardIdFromUrl("/index.php/apps/deck/#/board/4/card/6"));
        assertArrayEquals(new long[]{4, 6}, ProjectUtil.extractBoardIdAndCardIdFromUrl("example.com/index.php/apps/deck/#/board/4/card/6"));
        assertArrayEquals(new long[]{4, 6}, ProjectUtil.extractBoardIdAndCardIdFromUrl("http://example.com/index.php/apps/deck/#/board/4/card/6"));
        assertArrayEquals(new long[]{4, 6}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/index.php/apps/deck/#/board/4/card/6"));
        assertArrayEquals(new long[]{4, 6}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/index.php/apps/deck/#/board/4/card/6/"));
        assertArrayEquals(new long[]{4, 6}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/index.php/apps/deck/#/board/4/card/6"));

        // Valid card URLs without # and without index.php
        assertArrayEquals(new long[]{4, 6}, ProjectUtil.extractBoardIdAndCardIdFromUrl("apps/deck/#/board/4/card/6"));
        assertArrayEquals(new long[]{4, 6}, ProjectUtil.extractBoardIdAndCardIdFromUrl("/apps/deck/#/board/4/card/6"));
        assertArrayEquals(new long[]{4, 6}, ProjectUtil.extractBoardIdAndCardIdFromUrl("example.com/apps/deck/#/board/4/card/6"));
        assertArrayEquals(new long[]{4, 6}, ProjectUtil.extractBoardIdAndCardIdFromUrl("http://example.com/apps/deck/#/board/4/card/6"));
        assertArrayEquals(new long[]{4, 6}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/apps/deck/#/board/4/card/6"));
        assertArrayEquals(new long[]{4, 6}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/apps/deck/#/board/4/card/6/"));
        assertArrayEquals(new long[]{4, 6}, ProjectUtil.extractBoardIdAndCardIdFromUrl("https://example.com/nextcloud/apps/deck/#/board/4/card/6"));

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
