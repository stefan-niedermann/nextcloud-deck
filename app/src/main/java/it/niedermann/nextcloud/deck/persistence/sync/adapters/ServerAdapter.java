package it.niedermann.nextcloud.deck.persistence.sync.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.nextcloud.android.sso.api.ParsedResponse;

import java.io.File;
import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.ApiProvider;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.api.RequestHelper;
import it.niedermann.nextcloud.deck.exceptions.OfflineException;
import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.model.ocs.comment.OcsComment;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProjectList;
import it.niedermann.nextcloud.deck.model.ocs.user.GroupMemberUIDs;
import it.niedermann.nextcloud.deck.model.ocs.user.OcsUser;
import it.niedermann.nextcloud.deck.model.ocs.user.OcsUserList;
import it.niedermann.nextcloud.deck.model.propagation.CardUpdate;
import it.niedermann.nextcloud.deck.model.propagation.Reorder;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import static it.niedermann.nextcloud.deck.util.MimeTypeUtil.TEXT_PLAIN;

public class ServerAdapter {

    private final String prefKeyWifiOnly;

    @NonNull
    private final Context applicationContext;
    private final ApiProvider provider;

    public ServerAdapter(@NonNull Context applicationContext) {
        this(applicationContext, null);
    }

    public ServerAdapter(@NonNull Context applicationContext, @Nullable String ssoAccountName) {
        this.applicationContext = applicationContext;
        prefKeyWifiOnly = applicationContext.getResources().getString(R.string.pref_key_wifi_only);
        provider = new ApiProvider(applicationContext, ssoAccountName);
    }

    public String getServerUrl() {
        return provider.getServerUrl();
    }

    public String getApiPath() {
        return provider.getApiPath();
    }

    public String getApiUrl() {
        return provider.getApiUrl();
    }

    public void ensureInternetConnection() {
        boolean isConnected = hasInternetConnection();
        if (!isConnected) {
            throw new OfflineException();
        }
    }

    public boolean hasInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
            if (sharedPreferences.getBoolean(prefKeyWifiOnly, false)) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    Network network = cm.getActiveNetwork();
                    NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
                    if (capabilities == null) {
                        return false;
                    }
                    return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                } else {
                    NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    if (networkInfo == null) {
                        return false;
                    }
                    return networkInfo.isConnected();
                }


            } else {
                return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
            }
        }
        return false;
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

    public void getBoards(IResponseCallback<ParsedResponse<List<FullBoard>>> responseCallback) {
        RequestHelper.request(provider, () ->
                        provider.getDeckAPI().getBoards(true, getLastSyncDateFormatted(responseCallback.getAccount().getId()), responseCallback.getAccount().getBoardsEtag()),
                responseCallback);
    }

    public void getCapabilities(String eTag, IResponseCallback<ParsedResponse<Capabilities>> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getNextcloudAPI().getCapabilities(eTag), responseCallback);
    }

    public void getProjectsForCard(long remoteCardId, IResponseCallback<OcsProjectList> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getNextcloudAPI().getProjectsForCard(remoteCardId), responseCallback);
    }

    public void searchUser(String searchTerm, IResponseCallback<OcsUserList> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getNextcloudAPI().searchUser(searchTerm), responseCallback);
    }

    public void getSingleUserData(String userUid, IResponseCallback<OcsUser> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getNextcloudAPI().getSingleUserData(userUid), responseCallback);
    }

    public void searchGroupMembers(String groupUID, IResponseCallback<GroupMemberUIDs> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getNextcloudAPI().searchGroupMembers(groupUID), responseCallback);
    }

    public void getActivitiesForCard(long cardId, IResponseCallback<List<it.niedermann.nextcloud.deck.model.ocs.Activity>> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getNextcloudAPI().getActivitiesForCard(cardId), responseCallback);
    }

    public void createBoard(Board board, IResponseCallback<FullBoard> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getDeckAPI().createBoard(board), responseCallback);
    }


    public void deleteBoard(Board board, IResponseCallback<Void> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getDeckAPI().deleteBoard(board.getId()), responseCallback);
    }

    public void updateBoard(Board board, IResponseCallback<FullBoard> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getDeckAPI().updateBoard(board.getId(), board), responseCallback);
    }

    public void createAccessControl(long remoteBoardId, AccessControl acl, IResponseCallback<AccessControl> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getDeckAPI().createAccessControl(remoteBoardId, acl), responseCallback);
    }

    public void updateAccessControl(long remoteBoardId, AccessControl acl, IResponseCallback<AccessControl> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getDeckAPI().updateAccessControl(remoteBoardId, acl.getId(), acl), responseCallback);
    }

    public void deleteAccessControl(long remoteBoardId, AccessControl acl, IResponseCallback<Void> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getDeckAPI().deleteAccessControl(remoteBoardId, acl.getId(), acl), responseCallback);
    }

    public void getStacks(long boardId, IResponseCallback<List<FullStack>> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getDeckAPI().getStacks(boardId, getLastSyncDateFormatted(responseCallback.getAccount().getId())), responseCallback);
    }

    public void getStack(long boardId, long stackId, IResponseCallback<FullStack> responseCallback) {
        RequestHelper.request(provider, () -> provider.getDeckAPI().getStack(boardId, stackId, getLastSyncDateFormatted(responseCallback.getAccount().getId())), responseCallback);
    }

    public void createStack(Board board, Stack stack, IResponseCallback<FullStack> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getDeckAPI().createStack(board.getId(), stack), responseCallback);
    }

    public void deleteStack(Board board, Stack stack, IResponseCallback<Void> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getDeckAPI().deleteStack(board.getId(), stack.getId()), responseCallback);

    }

    public void updateStack(Board board, Stack stack, IResponseCallback<FullStack> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getDeckAPI().updateStack(board.getId(), stack.getId(), stack), responseCallback);

    }

    public void getCard(long boardId, long stackId, long cardId, IResponseCallback<FullCard> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getDeckAPI().getCard(boardId, stackId, cardId, getLastSyncDateFormatted(responseCallback.getAccount().getId())), responseCallback);
    }

    public void createCard(long boardId, long stackId, Card card, IResponseCallback<FullCard> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getDeckAPI().createCard(boardId, stackId, card), responseCallback);
    }

    public void deleteCard(long boardId, long stackId, Card card, IResponseCallback<Void> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getDeckAPI().deleteCard(boardId, stackId, card.getId()), responseCallback);
    }

    public void updateCard(long boardId, long stackId, CardUpdate card, IResponseCallback<FullCard> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getDeckAPI().updateCard(boardId, stackId, card.getId(), card), responseCallback);
    }

    public void assignUserToCard(long boardId, long stackId, long cardId, String userUID, IResponseCallback<Void> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getDeckAPI().assignUserToCard(boardId, stackId, cardId, userUID), responseCallback);
    }

    public void unassignUserFromCard(long boardId, long stackId, long cardId, String userUID, IResponseCallback<Void> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getDeckAPI().unassignUserFromCard(boardId, stackId, cardId, userUID), responseCallback);
    }

    public void assignLabelToCard(long boardId, long stackId, long cardId, long labelId, IResponseCallback<Void> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getDeckAPI().assignLabelToCard(boardId, stackId, cardId, labelId), responseCallback);
    }

    public void unassignLabelFromCard(long boardId, long stackId, long cardId, long labelId, IResponseCallback<Void> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getDeckAPI().unassignLabelFromCard(boardId, stackId, cardId, labelId), responseCallback);
    }


    // ## LABELS
    public void createLabel(long boardId, Label label, IResponseCallback<Label> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getDeckAPI().createLabel(boardId, label), responseCallback);
    }

    public void deleteLabel(long boardId, Label label, IResponseCallback<Void> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getDeckAPI().deleteLabel(boardId, label.getId()), responseCallback);
    }

    public void updateLabel(long boardId, Label label, IResponseCallback<Label> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getDeckAPI().updateLabel(boardId, label.getId(), label), responseCallback);
    }

    public void reorder(long boardId, long currentStackId, long cardId, long newStackId, int newPosition, IResponseCallback<List<FullCard>> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getDeckAPI().moveCard(boardId, currentStackId, cardId, new Reorder(newPosition, (int) newStackId)), responseCallback);
    }

    // ## ATTACHMENTS
    public void uploadAttachment(Long remoteBoardId, long remoteStackId, long remoteCardId, String contentType, File attachment, IResponseCallback<Attachment> responseCallback) {
        ensureInternetConnection();
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", attachment.getName(), RequestBody.create(MediaType.parse(getMimeType(attachment)), attachment));
        MultipartBody.Part typePart = MultipartBody.Part.createFormData("type", null, RequestBody.create(MediaType.parse(TEXT_PLAIN), "deck_file"));
        RequestHelper.request(provider, () -> provider.getDeckAPI().uploadAttachment(remoteBoardId, remoteStackId, remoteCardId, typePart, filePart), responseCallback);
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

    public void updateAttachment(Long remoteBoardId, long remoteStackId, long remoteCardId, long remoteAttachmentId, String contentType, Uri attachmentUri, IResponseCallback<Attachment> responseCallback) {
        ensureInternetConnection();
        File attachment = new File(attachmentUri.getPath());
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", attachment.getName(), RequestBody.create(MediaType.parse(contentType), attachment));
        MultipartBody.Part typePart = MultipartBody.Part.createFormData("type", attachment.getName(), RequestBody.create(MediaType.parse(TEXT_PLAIN), "deck_file"));
        RequestHelper.request(provider, () -> provider.getDeckAPI().updateAttachment(remoteBoardId, remoteStackId, remoteCardId, remoteAttachmentId, typePart, filePart), responseCallback);
    }

    public void downloadAttachment(Long remoteBoardId, long remoteStackId, long remoteCardId, long remoteAttachmentId, IResponseCallback<ResponseBody> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getDeckAPI().downloadAttachment(remoteBoardId, remoteStackId, remoteCardId, remoteAttachmentId), responseCallback);
    }

    public void deleteAttachment(Long remoteBoardId, long remoteStackId, long remoteCardId, long remoteAttachmentId, IResponseCallback<Void> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getDeckAPI().deleteAttachment(remoteBoardId, remoteStackId, remoteCardId, remoteAttachmentId), responseCallback);
    }

    public void restoreAttachment(Long remoteBoardId, long remoteStackId, long remoteCardId, long remoteAttachmentId, IResponseCallback<Attachment> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getDeckAPI().restoreAttachment(remoteBoardId, remoteStackId, remoteCardId, remoteAttachmentId), responseCallback);
    }

    public void getCommentsForRemoteCardId(Long remoteCardId, IResponseCallback<OcsComment> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getNextcloudAPI().getCommentsForCard(remoteCardId), responseCallback);
    }

    public void createCommentForCard(DeckComment comment, IResponseCallback<OcsComment> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getNextcloudAPI().createCommentForCard(comment.getObjectId(), comment), responseCallback);
    }

    public void updateCommentForCard(DeckComment comment, IResponseCallback<OcsComment> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getNextcloudAPI().updateCommentForCard(comment.getObjectId(), comment.getId(), comment), responseCallback);
    }

    public void deleteCommentForCard(DeckComment comment, IResponseCallback<Void> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(provider, () -> provider.getNextcloudAPI().deleteCommentForCard(comment.getObjectId(), comment.getId()), responseCallback);
    }
}
