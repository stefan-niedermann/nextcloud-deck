package it.niedermann.nextcloud.deck.domain.di;

import org.jetbrains.annotations.NotNull;
import org.reactivestreams.FlowAdapters;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.data.local.DeckDatabase;
import it.niedermann.nextcloud.deck.data.local.dao.AccessControlDao;
import it.niedermann.nextcloud.deck.data.local.dao.AccountDao;
import it.niedermann.nextcloud.deck.data.local.dao.ActivityDao;
import it.niedermann.nextcloud.deck.data.local.dao.AttachmentDao;
import it.niedermann.nextcloud.deck.data.local.dao.BoardDao;
import it.niedermann.nextcloud.deck.data.local.dao.CardDao;
import it.niedermann.nextcloud.deck.data.local.dao.ColumnDao;
import it.niedermann.nextcloud.deck.data.local.dao.CommentDao;
import it.niedermann.nextcloud.deck.data.local.dao.JoinBoardWithLabelDao;
import it.niedermann.nextcloud.deck.data.local.dao.JoinBoardWithPermissionDao;
import it.niedermann.nextcloud.deck.data.local.dao.JoinBoardWithUserDao;
import it.niedermann.nextcloud.deck.data.local.dao.JoinCardWithLabelDao;
import it.niedermann.nextcloud.deck.data.local.dao.JoinCardWithUserDao;
import it.niedermann.nextcloud.deck.data.local.dao.LabelDao;
import it.niedermann.nextcloud.deck.data.local.dao.UserDao;
import it.niedermann.nextcloud.deck.domain.state.KeyValueStore;

@Module
public class TestLocalModule {

    @Provides
    @VirtualDeviceScope
    DeckDatabase provideDeckDatabase() {
        return DeckDatabase.Companion
                .getInMemoryDatabaseBuilder()
                .build();
    }

    @Provides
    @VirtualDeviceScope
    KeyValueStore provideKeyValueStore() {
        return new InMemoryKeyValueStore();
    }

    @Provides
    @VirtualDeviceScope
    AccountDao provideAccountDao(DeckDatabase db) {
        return db.getAccountDao();
    }

    @Provides
    @VirtualDeviceScope
    BoardDao provideBoardDao(DeckDatabase db) {
        return db.getBoardDao();
    }

    @Provides
    @VirtualDeviceScope
    ColumnDao provideColumnDao(DeckDatabase db) {
        return db.getColumnDao();
    }

    @Provides
    @VirtualDeviceScope
    CardDao provideCardDao(DeckDatabase db) {
        return db.getCardDao();
    }

    @Provides
    @VirtualDeviceScope
    LabelDao provideLabelDao(DeckDatabase db) {
        return db.getLabelDao();
    }

    @Provides
    @VirtualDeviceScope
    UserDao provideUserDao(DeckDatabase db) {
        return db.getUserDao();
    }

    @Provides
    @VirtualDeviceScope
    AttachmentDao provideAttachmentDao(DeckDatabase db) {
        return db.getAttachmentDao();
    }

    @Provides
    @VirtualDeviceScope
    CommentDao provideCommentDao(DeckDatabase db) {
        return db.getCommentDao();
    }

    @Provides
    @VirtualDeviceScope
    ActivityDao provideActivityDao(DeckDatabase db) {
        return db.getActivityDao();
    }

    @Provides
    @VirtualDeviceScope
    AccessControlDao provideAccessControlDao(DeckDatabase db) {
        return db.getAccessControlDao();
    }

    @Provides
    @VirtualDeviceScope
    JoinBoardWithUserDao provideJoinBoardWithUserDao(DeckDatabase db) {
        return db.getJoinBoardWithUserDao();
    }

    @Provides
    @VirtualDeviceScope
    JoinBoardWithPermissionDao provideJoinBoardWithPermissionDao(DeckDatabase db) {
        return db.getJoinBoardWithPermissionDao();
    }

    @Provides
    @VirtualDeviceScope
    JoinBoardWithLabelDao provideJoinBoardWithLabelDao(DeckDatabase db) {
        return db.getJoinBoardWithLabelDao();
    }

    @Provides
    @VirtualDeviceScope
    JoinCardWithUserDao provideJoinCardWithUserDao(DeckDatabase db) {
        return db.getJoinCardWithUserDao();
    }

    @Provides
    @VirtualDeviceScope
    JoinCardWithLabelDao provideJoinCardWithLabelDao(DeckDatabase db) {
        return db.getJoinCardWithLabelDao();
    }

    private static class InMemoryKeyValueStore implements KeyValueStore {
        private final Map<String, Object> store = new HashMap<>();
        @Override public CompletableFuture<Void> putString(@NotNull String key, @NotNull String value) { store.put(key, value); return CompletableFuture.completedFuture(null); }
        @Override public CompletableFuture<Void> putLong(@NotNull String key, long value) { store.put(key, value); return CompletableFuture.completedFuture(null); }
        @Override public CompletableFuture<Void> putBoolean(@NotNull String key, boolean value) { store.put(key, value); return CompletableFuture.completedFuture(null); }
        @Override public Flow.@NotNull Publisher<@NotNull String> getString(@NotNull String key, @NotNull String defaultValue) { final var value = store.get(key); return FlowAdapters.toFlowPublisher(Flowable.just(value != null ? value.toString() : defaultValue)); }
        @Override public Flow.@NotNull Publisher<@NotNull Long> getLong(@NotNull String key, long defaultValue) { final var value = store.get(key); return FlowAdapters.toFlowPublisher(Flowable.just(value instanceof Long ? (Long) value : defaultValue)); }
        @Override public Flow.@NotNull Publisher<@NotNull Boolean> getBoolean(@NotNull String key, boolean defaultValue) { final var value = store.get(key); return FlowAdapters.toFlowPublisher(Flowable.just(value instanceof Boolean ? (Boolean) value : defaultValue)); }
        @Override public CompletableFuture<Boolean> containsKey(@NotNull String key) { return CompletableFuture.completedFuture(store.containsKey(key)); }
        @Override public CompletableFuture<Void> clear() { store.clear(); return CompletableFuture.completedFuture(null); }
        @Override public CompletableFuture<Void> remove(@NotNull String key) { store.remove(key); return CompletableFuture.completedFuture(null); }
    }
}
