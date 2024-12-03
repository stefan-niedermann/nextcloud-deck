package it.niedermann.nextcloud.deck.remote.adapters;

import static it.niedermann.nextcloud.deck.util.MimeTypeUtil.TEXT_PLAIN;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.nextcloud.android.sso.api.EmptyResponse;
import com.nextcloud.android.sso.model.SingleSignOnAccount;

import java.io.File;
import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.enums.EAttachmentType;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.model.ocs.Activity;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.model.ocs.comment.OcsComment;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProjectList;
import it.niedermann.nextcloud.deck.model.ocs.user.GroupMemberUIDs;
import it.niedermann.nextcloud.deck.model.ocs.user.OcsUser;
import it.niedermann.nextcloud.deck.model.ocs.user.OcsUserList;
import it.niedermann.nextcloud.deck.model.ocs.user.UserForAssignment;
import it.niedermann.nextcloud.deck.model.propagation.CardUpdate;
import it.niedermann.nextcloud.deck.model.propagation.Reorder;
import it.niedermann.nextcloud.deck.remote.api.ApiProvider;
import it.niedermann.nextcloud.deck.remote.api.RequestHelper;
import it.niedermann.nextcloud.deck.remote.api.ResponseCallback;
import it.niedermann.nextcloud.deck.remote.helpers.util.ConnectivityUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class ServerAdapter {

    private final ConnectivityUtil connectivityUtil;
    private final String prefKeyEtags;
    private final SharedPreferences sharedPreferences;
    private final ApiProvider provider;
    private final RequestHelper requestHelper;

    public ServerAdapter(@NonNull Context context,
                         @NonNull SingleSignOnAccount ssoAccount,
                         @NonNull ConnectivityUtil connectivityUtil) {
        this(context, new ApiProvider(context, ssoAccount), connectivityUtil);
    }

    public ServerAdapter(@NonNull Context context,
                         @NonNull ApiProvider apiProvider,
                         @NonNull ConnectivityUtil connectivityUtil) {
        this(context, apiProvider, connectivityUtil, new RequestHelper(apiProvider, connectivityUtil));
    }

    public ServerAdapter(@NonNull Context context,
                         @NonNull ApiProvider apiProvider,
                         @NonNull ConnectivityUtil connectivityUtil,
                         @NonNull RequestHelper requestHelper) {
        this.prefKeyEtags = context.getResources().getString(R.string.pref_key_etags);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.connectivityUtil = connectivityUtil;
        this.provider = apiProvider;
        this.requestHelper = requestHelper;
    }

    @Deprecated()
    public boolean hasInternetConnection() {
        return connectivityUtil.hasInternetConnection();
    }

    // TODO what is this?
    private String getLastSyncDateFormatted(long accountId) {
        return null;
//        String lastSyncHeader = API_FORMAT.format(getLastSync(accountId));
//        // omit Offset of timezone (e.g.: +01:00)
//        if (lastSyncHeader.matches("^.*\\+[0-9]{2}:[0-9]{2}$")) {
//            lastSyncHeader = lastSyncHeader.substring(0, lastSyncHeader.length()-6);
//        }
//        DeckLog.log("lastSync "+lastSyncHeader);
//        return lastSyncHeader;
    }

    public void getBoards(@NonNull ResponseCallback<List<FullBoard>> responseCallback) {
        this.requestHelper.request(() -> isEtagsEnabled()
                ? provider.getDeckAPI().getBoards(true, getLastSyncDateFormatted(responseCallback.getAccount().getId()), responseCallback.getAccount().getBoardsEtag())
                : provider.getDeckAPI().getBoards(true, getLastSyncDateFormatted(responseCallback.getAccount().getId())), responseCallback);
    }

    public boolean isEtagsEnabled() {
        return sharedPreferences.getBoolean(prefKeyEtags, true);
    }

    public void getCapabilities(String eTag, @NonNull ResponseCallback<Capabilities> responseCallback) {
        this.requestHelper.request(() -> provider.getNextcloudAPI().getCapabilities(eTag), responseCallback);
    }

    public void getProjectsForCard(long remoteCardId, @NonNull ResponseCallback<OcsProjectList> responseCallback) {
        this.requestHelper.request(() -> provider.getNextcloudAPI().getProjectsForCard(remoteCardId), responseCallback);
    }

    public void searchUser(String searchTerm, @NonNull ResponseCallback<OcsUserList> responseCallback) {
        this.requestHelper.request(() -> provider.getNextcloudAPI().searchUser(searchTerm), responseCallback);
    }

    public void getSingleUserData(String userUid, @NonNull ResponseCallback<OcsUser> responseCallback) {
        this.requestHelper.request(() -> provider.getNextcloudAPI().getSingleUserData(userUid), responseCallback);
    }

    public void searchGroupMembers(String groupUID, @NonNull ResponseCallback<GroupMemberUIDs> responseCallback) {
        this.requestHelper.request(() -> provider.getNextcloudAPI().searchGroupMembers(groupUID), responseCallback);
    }

    public void getActivitiesForCard(long cardId, @NonNull ResponseCallback<List<Activity>> responseCallback) {
        this.requestHelper.request(() -> provider.getNextcloudAPI().getActivitiesForCard(cardId), responseCallback);
    }

    public void createBoard(Board board, @NonNull ResponseCallback<FullBoard> responseCallback) {
        this.requestHelper.request(() -> provider.getDeckAPI().createBoard(board), responseCallback);
    }

    public void deleteBoard(Board board, @NonNull ResponseCallback<EmptyResponse> responseCallback) {
        this.requestHelper.request(() -> provider.getDeckAPI().deleteBoard(board.getId()), responseCallback);
    }

    public void updateBoard(Board board, @NonNull ResponseCallback<FullBoard> responseCallback) {
        this.requestHelper.request(() -> provider.getDeckAPI().updateBoard(board.getId(), board), responseCallback);
    }

    public void createAccessControl(long remoteBoardId, AccessControl acl, @NonNull ResponseCallback<AccessControl> responseCallback) {
        this.requestHelper.request(() -> provider.getDeckAPI().createAccessControl(remoteBoardId, acl), responseCallback);
    }

    public void updateAccessControl(long remoteBoardId, AccessControl acl, @NonNull ResponseCallback<AccessControl> responseCallback) {
        this.requestHelper.request(() -> provider.getDeckAPI().updateAccessControl(remoteBoardId, acl.getId(), acl), responseCallback);
    }

    public void deleteAccessControl(long remoteBoardId, AccessControl acl, @NonNull ResponseCallback<EmptyResponse> responseCallback) {
        this.requestHelper.request(() -> provider.getDeckAPI().deleteAccessControl(remoteBoardId, acl.getId(), acl), responseCallback);
    }

    public void getStacks(long boardId, @NonNull ResponseCallback<List<FullStack>> responseCallback) {
        this.requestHelper.request(() -> provider.getDeckAPI().getStacks(boardId, getLastSyncDateFormatted(responseCallback.getAccount().getId())), responseCallback);
    }

    public void getStack(long boardId, long stackId, @NonNull ResponseCallback<FullStack> responseCallback) {
        this.requestHelper.request(() -> provider.getDeckAPI().getStack(boardId, stackId, getLastSyncDateFormatted(responseCallback.getAccount().getId())), responseCallback);
    }

    public void createStack(Board board, Stack stack, @NonNull ResponseCallback<FullStack> responseCallback) {
        this.requestHelper.request(() -> provider.getDeckAPI().createStack(board.getId(), stack), responseCallback);
    }

    public void deleteStack(Board board, Stack stack, @NonNull ResponseCallback<EmptyResponse> responseCallback) {
        this.requestHelper.request(() -> provider.getDeckAPI().deleteStack(board.getId(), stack.getId()), responseCallback);

    }

    public void updateStack(Board board, Stack stack, @NonNull ResponseCallback<FullStack> responseCallback) {
        this.requestHelper.request(() -> provider.getDeckAPI().updateStack(board.getId(), stack.getId(), stack), responseCallback);

    }

    public void getCard(long boardId, long stackId, long cardId, @NonNull ResponseCallback<FullCard> responseCallback) {
        this.requestHelper.request(() -> {
            final Account account = responseCallback.getAccount();
            if (account.getServerDeckVersionAsObject().supportsFileAttachments()) {
                return provider.getDeckAPI().getCard_1_1(boardId, stackId, cardId, getLastSyncDateFormatted(responseCallback.getAccount().getId()));
            }
            return provider.getDeckAPI().getCard_1_0(boardId, stackId, cardId, getLastSyncDateFormatted(responseCallback.getAccount().getId()));
        }, responseCallback);
    }

    public void createCard(long boardId, long stackId, Card card, @NonNull ResponseCallback<FullCard> responseCallback) {
        this.requestHelper.request(() -> provider.getDeckAPI().createCard(boardId, stackId, card), responseCallback);
    }

    public void deleteCard(long boardId, long stackId, Card card, @NonNull ResponseCallback<EmptyResponse> responseCallback) {
        this.requestHelper.request(() -> provider.getDeckAPI().deleteCard(boardId, stackId, card.getId()), responseCallback);
    }

    public void updateCard(long boardId, long stackId, CardUpdate card, @NonNull ResponseCallback<FullCard> responseCallback) {
        this.requestHelper.request(() -> provider.getDeckAPI().updateCard(boardId, stackId, card.getId(), card), responseCallback);
    }

    public void assignUserToCard(long boardId, long stackId, long cardId, UserForAssignment userAssignment,  @NonNull ResponseCallback<EmptyResponse> responseCallback) {
        this.requestHelper.request(() -> provider.getDeckAPI().assignUserToCard(boardId, stackId, cardId, userAssignment), responseCallback);
    }

    public void unassignUserFromCard(long boardId, long stackId, long cardId, UserForAssignment userAssignment, @NonNull ResponseCallback<EmptyResponse> responseCallback) {
        this.requestHelper.request(() -> provider.getDeckAPI().unassignUserFromCard(boardId, stackId, cardId, userAssignment), responseCallback);
    }

    public void assignLabelToCard(long boardId, long stackId, long cardId, long labelId, @NonNull ResponseCallback<EmptyResponse> responseCallback) {
        this.requestHelper.request(() -> provider.getDeckAPI().assignLabelToCard(boardId, stackId, cardId, labelId), responseCallback);
    }

    public void unassignLabelFromCard(long boardId, long stackId, long cardId, long labelId, @NonNull ResponseCallback<EmptyResponse> responseCallback) {
        this.requestHelper.request(() -> provider.getDeckAPI().unassignLabelFromCard(boardId, stackId, cardId, labelId), responseCallback);
    }


    // Labels

    public void createLabel(long boardId, Label label, @NonNull ResponseCallback<Label> responseCallback) {
        this.requestHelper.request(() -> provider.getDeckAPI().createLabel(boardId, label), responseCallback);
    }

    public void deleteLabel(long boardId, Label label, @NonNull ResponseCallback<EmptyResponse> responseCallback) {
        this.requestHelper.request(() -> provider.getDeckAPI().deleteLabel(boardId, label.getId()), responseCallback);
    }

    public void updateLabel(long boardId, Label label, @NonNull ResponseCallback<Label> responseCallback) {
        this.requestHelper.request(() -> provider.getDeckAPI().updateLabel(boardId, label.getId(), label), responseCallback);
    }

    public void reorder(long boardId, long currentStackId, long cardId, long newStackId, int newPosition, @NonNull ResponseCallback<List<FullCard>> responseCallback) {
        this.requestHelper.request(() -> provider.getDeckAPI().moveCard(boardId, currentStackId, cardId, new Reorder(newPosition, (int) newStackId)), responseCallback);
    }


    // Attachments

    public void uploadAttachment(Long remoteBoardId, long remoteStackId, long remoteCardId, File attachment, @NonNull ResponseCallback<Attachment> responseCallback) {
        final Account account = responseCallback.getAccount();
        final String type = account.getServerDeckVersionAsObject().supportsFileAttachments()
                ? EAttachmentType.FILE.getValue()
                : EAttachmentType.DECK_FILE.getValue();
        final MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", attachment.getName(), RequestBody.create(MediaType.parse(getMimeType(attachment)), attachment));
        final MultipartBody.Part typePart = MultipartBody.Part.createFormData("type", null, RequestBody.create(MediaType.parse(TEXT_PLAIN), type));
        this.requestHelper.request(() -> provider.getDeckAPI().uploadAttachment(remoteBoardId, remoteStackId, remoteCardId, typePart, filePart), responseCallback);
    }

    @NonNull
    static String getMimeType(@NonNull File file) {
        String type = null;
        final String url = file.toString();
        final String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        }
        if (type == null) {
            type = "*/*";
        }
        return type;
    }

    public void updateAttachment(Long remoteBoardId, long remoteStackId, long remoteCardId, long remoteAttachmentId, String contentType, Uri attachmentUri, @NonNull ResponseCallback<Attachment> responseCallback) {
        final File attachment = new File(attachmentUri.getPath());
        final String type = responseCallback.getAccount().getServerDeckVersionAsObject().supportsFileAttachments()
                ? EAttachmentType.FILE.getValue()
                : EAttachmentType.DECK_FILE.getValue();
        final MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", attachment.getName(), RequestBody.create(MediaType.parse(contentType), attachment));
        final MultipartBody.Part typePart = MultipartBody.Part.createFormData("type", attachment.getName(), RequestBody.create(MediaType.parse(TEXT_PLAIN), type));
        this.requestHelper.request(() -> provider.getDeckAPI().updateAttachment(remoteBoardId, remoteStackId, remoteCardId, remoteAttachmentId, typePart, filePart), responseCallback);
    }

    public void downloadAttachment(Long remoteBoardId, long remoteStackId, long remoteCardId, long remoteAttachmentId, @NonNull ResponseCallback<ResponseBody> responseCallback) {
        this.requestHelper.request(() -> provider.getDeckAPI().downloadAttachment(remoteBoardId, remoteStackId, remoteCardId, remoteAttachmentId), responseCallback);
    }

    public void deleteAttachment(Long remoteBoardId, long remoteStackId, long remoteCardId, @NonNull Attachment attachment, @NonNull ResponseCallback<EmptyResponse> responseCallback) {
        this.requestHelper.request(() -> provider.getDeckAPI().deleteAttachment(attachment.getType().getValue(), remoteBoardId, remoteStackId, remoteCardId, attachment.getId()), responseCallback);
    }

    public void restoreAttachment(Long remoteBoardId, long remoteStackId, long remoteCardId, long remoteAttachmentId, @NonNull ResponseCallback<Attachment> responseCallback) {
        this.requestHelper.request(() -> provider.getDeckAPI().restoreAttachment(remoteBoardId, remoteStackId, remoteCardId, remoteAttachmentId), responseCallback);
    }

    public void getCommentsForRemoteCardId(Long remoteCardId, @NonNull ResponseCallback<OcsComment> responseCallback) {
        this.requestHelper.request(() -> provider.getNextcloudAPI().getCommentsForCard(remoteCardId), responseCallback);
    }

    public void createCommentForCard(DeckComment comment, @NonNull ResponseCallback<OcsComment> responseCallback) {
        this.requestHelper.request(() -> provider.getNextcloudAPI().createCommentForCard(comment.getObjectId(), comment), responseCallback);
    }

    public void updateCommentForCard(DeckComment comment, @NonNull ResponseCallback<OcsComment> responseCallback) {
        this.requestHelper.request(() -> provider.getNextcloudAPI().updateCommentForCard(comment.getObjectId(), comment.getId(), comment), responseCallback);
    }

    public void deleteCommentForCard(DeckComment comment, @NonNull ResponseCallback<EmptyResponse> responseCallback) {
        this.requestHelper.request(() -> provider.getNextcloudAPI().deleteCommentForCard(comment.getObjectId(), comment.getId()), responseCallback);
    }
}
