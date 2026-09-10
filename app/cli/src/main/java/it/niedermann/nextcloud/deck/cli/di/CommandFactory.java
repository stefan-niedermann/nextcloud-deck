package it.niedermann.nextcloud.deck.cli.di;

import it.niedermann.nextcloud.deck.cli.commands.RootCmd;
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
import it.niedermann.nextcloud.deck.cli.commands.card.CardCmd;
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
import picocli.CommandLine;

public class CommandFactory implements CommandLine.IFactory {

    private final AppComponent appComponent;

    public CommandFactory(AppComponent appComponent) {
        this.appComponent = appComponent;
    }

    @Override
    public <K> K create(Class<K> cls) throws Exception {
        final K cmd = CommandLine.defaultFactory().create(cls);

        if (cls == RootCmd.class) {
            // Nothing to do :)

        } else if (cls == AccountCmd.class) {
            appComponent.inject((AccountCmd) cmd);
        } else if (cls == AccountAddCmd.class) {
            appComponent.inject((AccountAddCmd) cmd);
        } else if (cls == AccountListCmd.class) {
            appComponent.inject((AccountListCmd) cmd);
        } else if (cls == AccountRemoveCmd.class) {
            appComponent.inject((AccountRemoveCmd) cmd);

        } else if (cls == BoardCmd.class) {
            appComponent.inject((BoardCmd) cmd);
        } else if (cls == BoardAddCmd.class) {
            appComponent.inject((BoardAddCmd) cmd);
        } else if (cls == BoardDeleteCmd.class) {
            appComponent.inject((BoardDeleteCmd) cmd);
        } else if (cls == BoardGetCmd.class) {
            appComponent.inject((BoardGetCmd) cmd);
        } else if (cls == BoardListCmd.class) {
            appComponent.inject((BoardListCmd) cmd);
        } else if (cls == BoardUpdateCmd.class) {
            appComponent.inject((BoardUpdateCmd) cmd);
        } else if (cls == ColumnAddCmd.class) {
            appComponent.inject((ColumnAddCmd) cmd);
        } else if (cls == ColumnDeleteCmd.class) {
            appComponent.inject((ColumnDeleteCmd) cmd);
        } else if (cls == ColumnGetCmd.class) {
            appComponent.inject((ColumnGetCmd) cmd);
        } else if (cls == ColumnListCmd.class) {
            appComponent.inject((ColumnListCmd) cmd);
        } else if (cls == ColumnUpdateCmd.class) {
            appComponent.inject((ColumnUpdateCmd) cmd);
        } else if (cls == ExportCsvCmd.class) {
            appComponent.inject((ExportCsvCmd) cmd);
        } else if (cls == ExportMermaidCmd.class) {
            appComponent.inject((ExportMermaidCmd) cmd);
        } else if (cls == ExportOdtCmd.class) {
            appComponent.inject((ExportOdtCmd) cmd);
        } else if (cls == ExportPdfCmd.class) {
            appComponent.inject((ExportPdfCmd) cmd);
        } else if (cls == LabelAddCmd.class) {
            appComponent.inject((LabelAddCmd) cmd);
        } else if (cls == LabelDeleteCmd.class) {
            appComponent.inject((LabelDeleteCmd) cmd);
        } else if (cls == LabelListCmd.class) {
            appComponent.inject((LabelListCmd) cmd);
        } else if (cls == LabelSearchCmd.class) {
            appComponent.inject((LabelSearchCmd) cmd);
        } else if (cls == LabelUpdateCmd.class) {
            appComponent.inject((LabelUpdateCmd) cmd);
        } else if (cls == ShareAddCmd.class) {
            appComponent.inject((ShareAddCmd) cmd);
        } else if (cls == ShareListCmd.class) {
            appComponent.inject((ShareListCmd) cmd);
        } else if (cls == ShareRemoveCmd.class) {
            appComponent.inject((ShareRemoveCmd) cmd);
        } else if (cls == ShareUpdateCmd.class) {
            appComponent.inject((ShareUpdateCmd) cmd);
        } else if (cls == CardCmd.class) {
            // Nothing to do :)
        } else if (cls == CardAddCmd.class) {
            appComponent.inject((CardAddCmd) cmd);
        } else if (cls == CardDeleteCmd.class) {
            appComponent.inject((CardDeleteCmd) cmd);
        } else if (cls == CardGetCmd.class) {
            appComponent.inject((CardGetCmd) cmd);
        } else if (cls == CardListCmd.class) {
            appComponent.inject((CardListCmd) cmd);
        } else if (cls == CardUpdateCmd.class) {
            appComponent.inject((CardUpdateCmd) cmd);
        } else if (cls == CardActivityCmd.class) {
            appComponent.inject((CardActivityCmd) cmd);
        } else if (cls == CardAssignCmd.class) {
            appComponent.inject((CardAssignCmd) cmd);
        } else if (cls == AttachmentAddCmd.class) {
            appComponent.inject((AttachmentAddCmd) cmd);
        } else if (cls == AttachmentDownloadCmd.class) {
            appComponent.inject((AttachmentDownloadCmd) cmd);
        } else if (cls == AttachmentListCmd.class) {
            appComponent.inject((AttachmentListCmd) cmd);
        } else if (cls == CommentAddCmd.class) {
            appComponent.inject((CommentAddCmd) cmd);
        } else if (cls == CommentDeleteCmd.class) {
            appComponent.inject((CommentDeleteCmd) cmd);
        } else if (cls == CommentListCmd.class) {
            appComponent.inject((CommentListCmd) cmd);
        } else if (cls == CommentUpdateCmd.class) {
            appComponent.inject((CommentUpdateCmd) cmd);
        } else if (cls == CardCopyCmd.class) {
            appComponent.inject((CardCopyCmd) cmd);
        } else if (cls == CardExportOdtCmd.class) {
            appComponent.inject((CardExportOdtCmd) cmd);
        } else if (cls == CardExportPdfCmd.class) {
            appComponent.inject((CardExportPdfCmd) cmd);
        } else if (cls == CardMoveCmd.class) {
            appComponent.inject((CardMoveCmd) cmd);
        } else if (cls == CardSearchCmd.class) {
            appComponent.inject((CardSearchCmd) cmd);
        } else if (cls == CardUnassignCmd.class) {
            appComponent.inject((CardUnassignCmd) cmd);
        } else if (cls == SyncCmd.class) {
            appComponent.inject((SyncCmd) cmd);
        } else if (cls == SyncStatusCmd.class) {
            appComponent.inject((SyncStatusCmd) cmd);
        } else if (cls == ResetCmd.class) {
            appComponent.inject((ResetCmd) cmd);

        } else {
            System.out.println("Warn: No Injection for Class " + cls.getName());
        }

        return cmd;
    }
}
