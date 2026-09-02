package it.niedermann.nextcloud.deck.app.shared.di.modules;

import dagger.Module;
import dagger.Provides;
import it.niedermann.nextcloud.deck.app.shared.mapper.AuthMapper;
import it.niedermann.nextcloud.deck.data.local.mapper.AccessControlMapper;
import it.niedermann.nextcloud.deck.data.local.mapper.AccountMapper;
import it.niedermann.nextcloud.deck.data.local.mapper.ActivityMapper;
import it.niedermann.nextcloud.deck.data.local.mapper.AttachmentMapper;
import it.niedermann.nextcloud.deck.data.local.mapper.BoardMapper;
import it.niedermann.nextcloud.deck.data.local.mapper.CardMapper;
import it.niedermann.nextcloud.deck.data.local.mapper.ColumnMapper;
import it.niedermann.nextcloud.deck.data.local.mapper.CommentMapper;
import it.niedermann.nextcloud.deck.data.local.mapper.LabelMapper;
import it.niedermann.nextcloud.deck.data.local.mapper.UserMapper;
import jakarta.inject.Singleton;

@Module
public class MapperModule {

    @Provides
    @Singleton
    AuthMapper provideAuthMapper() {
        return AuthMapper.INSTANCE;
    }

    @Provides
    @Singleton
    AccountMapper provideAccountMapper() {
        return AccountMapper.INSTANCE;
    }

    @Provides
    @Singleton
    BoardMapper provideBoardMapper() {
        return BoardMapper.INSTANCE;
    }

    @Provides
    @Singleton
    ColumnMapper provideColumnMapper() {
        return ColumnMapper.INSTANCE;
    }

    @Provides
    @Singleton
    CardMapper provideCardMapper() {
        return CardMapper.INSTANCE;
    }

    @Provides
    @Singleton
    LabelMapper provideLabelMapper() {
        return LabelMapper.INSTANCE;
    }

    @Provides
    @Singleton
    AttachmentMapper provideAttachmentMapper() {
        return AttachmentMapper.INSTANCE;
    }

    @Provides
    @Singleton
    CommentMapper provideCommentMapper() {
        return CommentMapper.INSTANCE;
    }

    @Provides
    @Singleton
    AccessControlMapper provideAccessControlMapper() {
        return AccessControlMapper.INSTANCE;
    }

    @Provides
    @Singleton
    ActivityMapper provideActivityMapper() {
        return ActivityMapper.INSTANCE;
    }

    @Provides
    @Singleton
    UserMapper provideUserMapper() {
        return UserMapper.INSTANCE;
    }

}
