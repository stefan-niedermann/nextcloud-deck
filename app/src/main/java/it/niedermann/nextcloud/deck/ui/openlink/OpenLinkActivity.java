package it.niedermann.nextcloud.deck.ui.openlink;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;

import com.nextcloud.android.sso.helper.SingleAccountHelper;

import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.MainActivity;
import it.niedermann.nextcloud.deck.ui.branding.Branded;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.util.UriUtils.parseBoardRemoteId;

public class OpenLinkActivity extends AppCompatActivity implements Branded {

    private SyncManager syncManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));

        syncManager = new SyncManager(this);

        final Intent intent = getIntent();
        if (intent == null) {
            throw new IllegalArgumentException("Could not retrieve intent");
        }

        final Uri uri = intent.getData();
        if (uri == null) {
            throw new IllegalArgumentException("Received uri is null");
        }

        DeckLog.info("uri: " + uri);
        try {
            long boardRemoteId = parseBoardRemoteId(uri.toString());
            @Nullable String userInfo = uri.getUserInfo();
            if (userInfo != null) {
                DeckLog.info("uri has userinfo: " + userInfo);
                observeOnce(syncManager.readAccount(userInfo + '@' + uri.getHost()), this, (account) -> {
                    if (account == null) {
                        DeckLog.info("didn't find account");
                        // Link has set an explicit user, but we don't have this user
                        // TODO display account picker
                        startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    } else {
                        DeckLog.info("found matching account: " + account.getUserName());
                        // TODO set this account as current account and start MainActivity
                        launchMainActivity(account, boardRemoteId);
                    }
                });
            } else {
                DeckLog.info("uri does not have userinfo. Looking for accounts on host " + uri.getHost());
                syncManager.readAccountsForHostWithReadAccessToBoard(uri.getHost(), boardRemoteId).observe(this, (accounts) -> {
                    if (accounts.size() == 0) {
                        DeckLog.info("found no account on host with read access to this board");
                        startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    } else {
                        DeckLog.info("found " + accounts.size() + " matching account, using first: " + accounts.get(0).getUserName());
                        launchMainActivity(accounts.get(0), boardRemoteId);
                        // TODO set this account as current account and start MainActivity
                        // TODO #2: Display this as a list to allow a choice by the user
                    }
                });
            }
        } catch (IllegalArgumentException e) {
            DeckLog.logError(e);
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }

    @Override
    public void applyBrand(int mainColor, int textColor) {

    }

    @UiThread
    private void launchMainActivity(@NonNull Account account, Long boardRemoteId) {
        observeOnce(syncManager.getBoard(account.getId(), boardRemoteId), this, (board) -> {
            try {
                Application.saveBrandColors(this, Color.parseColor(account.getColor()), Color.parseColor(account.getTextColor()));
            } catch (Throwable t) {
                DeckLog.logError(t);
            }
            Application.saveCurrentBoardId(this, account.getId(), board.getLocalId());
            SingleAccountHelper.setCurrentAccount(this, account.getName());
            DeckLog.info("starting " + MainActivity.class.getSimpleName() + " with [" + account + ", " + board.getLocalId() + "]");
            Intent intent = new Intent(this, MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    @UiThread
    private void launchEditActivity(@NonNull Account account, Long boardId, Long cardId) {
        try {
            Application.saveBrandColors(this, Color.parseColor(account.getColor()), Color.parseColor(account.getTextColor()));
        } catch (Throwable t) {
            DeckLog.logError(t);
        }
        SingleAccountHelper.setCurrentAccount(this, account.getName());
        DeckLog.info("starting " + EditActivity.class.getSimpleName() + " with [" + account + ", " + boardId + ", " + cardId + "]");
        startActivity(EditActivity.createEditCardIntent(this, account, boardId, cardId));
        finish();
    }

}
