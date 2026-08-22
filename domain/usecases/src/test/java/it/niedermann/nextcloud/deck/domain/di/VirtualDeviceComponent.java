package it.niedermann.nextcloud.deck.domain.di;

import dagger.BindsInstance;
import dagger.Subcomponent;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.HasAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.ImportAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.RemoveAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.activities.ListActivityUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.activities.ListPreviewActivitiesUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.attachments.AddAttachmentUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.attachments.DownloadAttachmentUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.attachments.ListAttachmentsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.AddBoardShareUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.AddBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.DeleteBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.ListBoardSharesUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.ListBoardsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.RemoveBoardShareUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.UpdateBoardShareUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.UpdateBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.AddCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.AssignCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.CopyCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.DeleteCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.GetCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.ListCardPreviewsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.ListCardsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.MoveCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.UnassignCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.UpdateCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.AddColumnUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.DeleteColumnUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.GetColumnUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.ListColumnIDsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.ListColumnsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.UpdateColumnUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.comments.AddCommentUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.comments.DeleteCommentUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.comments.ListCommentsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.comments.ListPreviewCommentsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.comments.UpdateCommentUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.labels.AddLabelUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.labels.DeleteLabelUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.labels.ListLabelsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.labels.SearchLabelsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.labels.UpdateLabelUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.sync.GetSyncStatusUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.sync.ScheduleSyncUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.users.GetAvatarUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.users.ListUsersUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.users.SearchUserUseCase;

@VirtualDeviceScope
@Subcomponent(modules = {
        TestLocalModule.class,
        TestVirtualDeviceModule.class,
})
public interface VirtualDeviceComponent {

    @Subcomponent.Factory
    interface Factory {
        VirtualDeviceComponent create(@BindsInstance @NamedDeviceName String deviceName);
    }

    @NamedDeviceName
    String getDeviceName();

    GetAccountsUseCase getGetAccountsUseCase();

    GetAccountUseCase getGetAccountUseCase();

    HasAccountsUseCase getHasAccountsUseCase();

    ImportAccountUseCase getImportAccountUseCase();

    RemoveAccountUseCase getRemoveAccountUseCase();

    ListActivityUseCase getListActivityUseCase();

    ListPreviewActivitiesUseCase getListPreviewActivitiesUseCase();

    AddAttachmentUseCase getAddAttachmentUseCase();

    DownloadAttachmentUseCase getDownloadAttachmentUseCase();

    ListAttachmentsUseCase getListAttachmentsUseCase();

    AddBoardShareUseCase getAddBoardShareUseCase();

    AddBoardUseCase getAddBoardUseCase();

    DeleteBoardUseCase getDeleteBoardUseCase();

    GetBoardUseCase getGetBoardUseCase();

    ListBoardSharesUseCase getListBoardSharesUseCase();

    ListBoardsUseCase getListBoardsUseCase();

    RemoveBoardShareUseCase getRemoveBoardShareUseCase();

    UpdateBoardShareUseCase getUpdateBoardShareUseCase();

    UpdateBoardUseCase getUpdateBoardUseCase();

    AddCardUseCase getAddCardUseCase();

    AssignCardUseCase getAssignCardUseCase();

    CopyCardUseCase getCopyCardUseCase();

    DeleteCardUseCase getDeleteCardUseCase();

    GetCardUseCase getGetCardUseCase();

    ListCardPreviewsUseCase getListCardPreviewsUseCase();

    ListCardsUseCase getListCardsUseCase();

    MoveCardUseCase getMoveCardUseCase();

    UnassignCardUseCase getUnassignCardUseCase();

    UpdateCardUseCase getUpdateCardUseCase();

    AddColumnUseCase getAddColumnUseCase();

    DeleteColumnUseCase getDeleteColumnUseCase();

    GetColumnUseCase getGetColumnUseCase();

    ListColumnIDsUseCase getListColumnIDsUseCase();

    ListColumnsUseCase getListColumnsUseCase();

    UpdateColumnUseCase getUpdateColumnUseCase();

    AddCommentUseCase getAddCommentUseCase();

    DeleteCommentUseCase getDeleteCommentUseCase();

    ListCommentsUseCase getListCommentsUseCase();

    ListPreviewCommentsUseCase getListPreviewCommentsUseCase();

    UpdateCommentUseCase getUpdateCommentUseCase();

    AddLabelUseCase getAddLabelUseCase();

    DeleteLabelUseCase getDeleteLabelUseCase();

    ListLabelsUseCase getListLabelsUseCase();

    SearchLabelsUseCase getSearchLabelsUseCase();

    UpdateLabelUseCase getUpdateLabelUseCase();

    GetCurrentAccountUseCase getGetCurrentAccountUseCase();

    GetCurrentBoardUseCase getGetCurrentBoardUseCase();

    SetCurrentAccountUseCase getSetCurrentAccountUseCase();

    SetCurrentBoardUseCase getSetCurrentBoardUseCase();

    GetSyncStatusUseCase getGetSyncStatusUseCase();

    ScheduleSyncUseCase getScheduleSyncUseCase();

    GetAvatarUseCase getGetAvatarUseCase();

    ListUsersUseCase getListUsersUseCase();

    SearchUserUseCase getSearchUserUseCase();
}
