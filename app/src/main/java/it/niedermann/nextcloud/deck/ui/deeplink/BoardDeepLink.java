package it.niedermann.nextcloud.deck.ui.deeplink;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Optional;

public final class BoardDeepLink {

    private static final String SCHEME = "nextclouddeck";
    private static final String BOARD_PATH = "/board/";

    @NonNull
    private final String accountName;
    private final long boardRemoteId;

    private BoardDeepLink(@NonNull String accountName, long boardRemoteId) {
        this.accountName = accountName;
        this.boardRemoteId = boardRemoteId;
    }

    @NonNull
    public String getAccountName() {
        return accountName;
    }

    public long getBoardRemoteId() {
        return boardRemoteId;
    }

    @NonNull
    public static Optional<BoardDeepLink> from(@Nullable Intent intent) {
        if (intent == null || !Intent.ACTION_VIEW.equals(intent.getAction())) {
            return Optional.empty();
        }
        return parse(intent.getData());
    }

    @NonNull
    private static Optional<BoardDeepLink> parse(@Nullable Uri uri) {
        if (uri == null || !SCHEME.equals(uri.getScheme()) || uri.getAuthority() == null || uri.getPath() == null) {
            return Optional.empty();
        }

        final var path = uri.getPath();
        final int boardPathIndex = path.lastIndexOf(BOARD_PATH);
        if (boardPathIndex < 0) {
            return Optional.empty();
        }

        try {
            final long boardRemoteId = Long.parseLong(path.substring(boardPathIndex + BOARD_PATH.length()));
            return Optional.of(new BoardDeepLink(uri.getAuthority() + path.substring(0, boardPathIndex), boardRemoteId));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
