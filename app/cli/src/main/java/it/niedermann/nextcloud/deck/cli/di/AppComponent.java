package it.niedermann.nextcloud.deck.cli.di;


import dagger.BindsInstance;
import dagger.Component;
import it.niedermann.nextcloud.deck.app.shared.di.NamedVerbose;
import it.niedermann.nextcloud.deck.app.shared.di.SharedModule;
import it.niedermann.nextcloud.deck.cli.commands.account.AccountCmd;
import it.niedermann.nextcloud.deck.cli.commands.account.subcommands.AccountAddCmd;
import it.niedermann.nextcloud.deck.cli.commands.account.subcommands.AccountListCmd;
import it.niedermann.nextcloud.deck.cli.commands.account.subcommands.AccountRemoveCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.BoardCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.BoardAddCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.BoardDeleteCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.BoardGetCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.BoardListCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.BoardUpdateCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.column.ColumnAddCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.column.ColumnDeleteCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.column.ColumnGetCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.column.ColumnListCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.column.ColumnUpdateCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.export.ExportCsvCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.export.ExportMermaidCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.export.ExportOdtCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.export.ExportPdfCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.label.LabelAddCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.label.LabelDeleteCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.label.LabelListCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.label.LabelSearchCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.label.LabelUpdateCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.share.ShareAddCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.share.ShareListCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.share.ShareRemoveCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.share.ShareUpdateCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.CardAddCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.CardDeleteCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.CardGetCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.CardListCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.CardUpdateCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.activity.CardActivityCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.assign.CardAssignCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.attachment.AttachmentAddCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.attachment.AttachmentDownloadCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.attachment.AttachmentListCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.comment.CommentAddCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.comment.CommentDeleteCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.comment.CommentListCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.comment.CommentUpdateCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.copy.CardCopyCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.export.CardExportOdtCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.export.CardExportPdfCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.move.CardMoveCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.search.CardSearchCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.unassign.CardUnassignCmd;
import it.niedermann.nextcloud.deck.cli.commands.reset.ResetCmd;
import it.niedermann.nextcloud.deck.cli.commands.sync.SyncCmd;
import it.niedermann.nextcloud.deck.cli.commands.sync.SyncStatusCmd;
import it.niedermann.nextcloud.deck.data.local.DeckDatabase;
import it.niedermann.nextcloud.deck.domain.state.KeyValueStore;

import jakarta.inject.Singleton;

@Singleton
@Component(modules = {
        SharedModule.class,
        AppModule.class,
})
public interface AppComponent {

    @Component.Factory
    interface Factory {
        AppComponent create(@BindsInstance DeckDatabase database,
                            @BindsInstance KeyValueStore keyValueStore,
                            @BindsInstance @NamedVerbose boolean verbose);
    }

    void inject(AccountCmd cmd);

    void inject(AccountAddCmd cmd);

    void inject(AccountListCmd cmd);

    void inject(AccountRemoveCmd cmd);

    void inject(BoardCmd cmd);

    void inject(BoardAddCmd cmd);

    void inject(BoardDeleteCmd cmd);

    void inject(BoardGetCmd cmd);

    void inject(BoardListCmd cmd);

    void inject(BoardUpdateCmd cmd);

    void inject(ColumnAddCmd cmd);

    void inject(ColumnDeleteCmd cmd);

    void inject(ColumnGetCmd cmd);

    void inject(ColumnListCmd cmd);

    void inject(ColumnUpdateCmd cmd);

    void inject(ExportCsvCmd cmd);

    void inject(ExportMermaidCmd cmd);

    void inject(ExportOdtCmd cmd);

    void inject(ExportPdfCmd cmd);

    void inject(LabelAddCmd cmd);

    void inject(LabelDeleteCmd cmd);

    void inject(LabelListCmd cmd);

    void inject(LabelSearchCmd cmd);

    void inject(LabelUpdateCmd cmd);

    void inject(ShareAddCmd cmd);

    void inject(ShareListCmd cmd);

    void inject(ShareRemoveCmd cmd);

    void inject(ShareUpdateCmd cmd);

    void inject(CardAddCmd cmd);

    void inject(CardDeleteCmd cmd);

    void inject(CardGetCmd cmd);

    void inject(CardListCmd cmd);

    void inject(CardUpdateCmd cmd);

    void inject(CardActivityCmd cmd);

    void inject(CardAssignCmd cmd);

    void inject(AttachmentAddCmd cmd);

    void inject(AttachmentDownloadCmd cmd);

    void inject(AttachmentListCmd cmd);

    void inject(CommentAddCmd cmd);

    void inject(CommentDeleteCmd cmd);

    void inject(CommentListCmd cmd);

    void inject(CommentUpdateCmd cmd);

    void inject(CardCopyCmd cmd);

    void inject(CardExportOdtCmd cmd);

    void inject(CardExportPdfCmd cmd);

    void inject(CardMoveCmd cmd);

    void inject(CardSearchCmd cmd);

    void inject(CardUnassignCmd cmd);

    void inject(SyncCmd cmd);

    void inject(SyncStatusCmd cmd);

    void inject(ResetCmd cmd);
}
