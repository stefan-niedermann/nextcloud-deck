package it.niedermann.nextcloud.deck.domain.di;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import it.niedermann.nextcloud.deck.data.repository.AccountRepositoryImpl;
import it.niedermann.nextcloud.deck.data.repository.ActivityRepositoryImpl;
import it.niedermann.nextcloud.deck.data.repository.AttachmentRepositoryImpl;
import it.niedermann.nextcloud.deck.data.repository.BoardRepositoryImpl;
import it.niedermann.nextcloud.deck.data.repository.CardRepositoryImpl;
import it.niedermann.nextcloud.deck.data.repository.ColumnRepositoryImpl;
import it.niedermann.nextcloud.deck.data.repository.CommentRepositoryImpl;
import it.niedermann.nextcloud.deck.data.repository.LabelRepositoryImpl;
import it.niedermann.nextcloud.deck.data.repository.ShareRepositoryImpl;
import it.niedermann.nextcloud.deck.data.repository.StateRepositoryImpl;
import it.niedermann.nextcloud.deck.data.repository.UserRepositoryImpl;
import it.niedermann.nextcloud.deck.data.sync.QueueingSyncScheduler;
import it.niedermann.nextcloud.deck.data.sync.SyncManager;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import it.niedermann.nextcloud.deck.domain.repository.ActivityRepository;
import it.niedermann.nextcloud.deck.domain.repository.AttachmentRepository;
import it.niedermann.nextcloud.deck.domain.repository.BoardRepository;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import it.niedermann.nextcloud.deck.domain.repository.ColumnRepository;
import it.niedermann.nextcloud.deck.domain.repository.CommentRepository;
import it.niedermann.nextcloud.deck.domain.repository.LabelRepository;
import it.niedermann.nextcloud.deck.domain.repository.ShareRepository;
import it.niedermann.nextcloud.deck.domain.repository.UserRepository;
import it.niedermann.nextcloud.deck.domain.state.StateRepository;
import it.niedermann.nextcloud.deck.domain.sync.SyncScheduler;

@Module
public abstract class TestVirtualDeviceModule {

    @Binds
    @VirtualDeviceScope
    abstract AccountRepository bindAccountRepository(AccountRepositoryImpl impl);

    @Binds
    @VirtualDeviceScope
    abstract BoardRepository bindBoardRepository(BoardRepositoryImpl impl);

    @Binds
    @VirtualDeviceScope
    abstract ColumnRepository bindColumnRepository(ColumnRepositoryImpl impl);

    @Binds
    @VirtualDeviceScope
    abstract CardRepository bindCardRepository(CardRepositoryImpl impl);

    @Binds
    @VirtualDeviceScope
    abstract UserRepository bindUserRepository(UserRepositoryImpl impl);

    @Binds
    @VirtualDeviceScope
    abstract AttachmentRepository bindAttachmentRepository(AttachmentRepositoryImpl impl);

    @Binds
    @VirtualDeviceScope
    abstract LabelRepository bindLabelRepository(LabelRepositoryImpl impl);

    @Binds
    @VirtualDeviceScope
    abstract CommentRepository bindCommentRepository(CommentRepositoryImpl impl);

    @Binds
    @VirtualDeviceScope
    abstract ActivityRepository bindActivityRepository(ActivityRepositoryImpl impl);

    @Binds
    @VirtualDeviceScope
    abstract ShareRepository bindShareRepository(ShareRepositoryImpl impl);

    @Binds
    @VirtualDeviceScope
    abstract StateRepository bindStateRepository(StateRepositoryImpl impl);

    @Binds
    @VirtualDeviceScope
    abstract SyncScheduler bindSyncScheduler(QueueingSyncScheduler impl);

    @Provides
    @VirtualDeviceScope
    static it.niedermann.nextcloud.deck.data.sync.provider.UserSyncHelper provideUserSyncHelper(
            it.niedermann.nextcloud.deck.data.local.dao.UserDao userDao
    ) {
        return new it.niedermann.nextcloud.deck.data.sync.provider.UserSyncHelper(userDao);
    }

    @Provides
    @VirtualDeviceScope
    static SyncManager provideSyncManager(
            it.niedermann.nextcloud.remote.ApiProvider.Factory apiProviderFactory,
            it.niedermann.nextcloud.deck.data.sync.provider.BoardSyncProvider boardSyncProvider,
            it.niedermann.nextcloud.deck.data.sync.provider.ColumnSyncProvider columnSyncProvider,
            it.niedermann.nextcloud.deck.data.sync.provider.CardSyncProvider cardSyncProvider,
            it.niedermann.nextcloud.deck.data.sync.provider.LabelSyncProvider labelSyncProvider,
            it.niedermann.nextcloud.deck.data.sync.provider.AttachmentSyncProvider attachmentSyncProvider,
            it.niedermann.nextcloud.deck.data.sync.provider.CommentSyncProvider commentSyncProvider,
            it.niedermann.nextcloud.deck.data.sync.provider.AccessControlSyncProvider accessControlSyncProvider
    ) {
        return new SyncManager(
                apiProviderFactory,
                boardSyncProvider,
                columnSyncProvider,
                cardSyncProvider,
                labelSyncProvider,
                attachmentSyncProvider,
                commentSyncProvider,
                accessControlSyncProvider
        );
    }
}
