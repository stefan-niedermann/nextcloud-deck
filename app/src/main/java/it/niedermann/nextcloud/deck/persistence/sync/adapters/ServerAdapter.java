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

import io.reactivex.disposables.Disposable;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.ApiProvider;
import it.niedermann.nextcloud.deck.api.RequestHelper;
import it.niedermann.nextcloud.deck.api.ResponseCallback;
import it.niedermann.nextcloud.deck.exceptions.OfflineException;
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
import it.niedermann.nextcloud.deck.model.propagation.CardUpdate;
import it.niedermann.nextcloud.deck.model.propagation.Reorder;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import static it.niedermann.nextcloud.deck.util.MimeTypeUtil.TEXT_PLAIN;

public class ServerAdapter {

    private final String prefKeyWifiOnly;
    private final String prefKeyEtags;
    final SharedPreferences sharedPreferences;

    @NonNull
    private final Context applicationContext;
    private final ApiProvider provider;

    public ServerAdapter(@NonNull Context applicationContext, @Nullable String ssoAccountName) {
        this.applicationContext = applicationContext;
        prefKeyWifiOnly = applicationContext.getResources().getString(R.string.pref_key_wifi_only);
        prefKeyEtags = applicationContext.getResources().getString(R.string.pref_key_etags);
        provider = new ApiProvider(applicationContext, ssoAccountName);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
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

    public Disposable getBoards(@NonNull ResponseCallback<ParsedResponse<List<FullBoard>>> responseCallback) {
        return RequestHelper.request(provider, () -> isEtagsEnabled()
                ? provider.getDeckAPI().getBoards(true, getLastSyncDateFormatted(responseCallback.getAccount().getId()), responseCallback.getAccount().getBoardsEtag())
                : provider.getDeckAPI().getBoards(true, getLastSyncDateFormatted(responseCallback.getAccount().getId())), responseCallback);
    }

    public boolean isEtagsEnabled() {
        return sharedPreferences.getBoolean(prefKeyEtags, true);
    }

    public Disposable getCapabilities(String eTag, @NonNull ResponseCallback<ParsedResponse<Capabilities>> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getNextcloudAPI().getCapabilities(eTag), responseCallback);
    }

    public Disposable getProjectsForCard(long remoteCardId, @NonNull ResponseCallback<OcsProjectList> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getNextcloudAPI().getProjectsForCard(remoteCardId), responseCallback);
    }

    public Disposable searchUser(String searchTerm, @NonNull ResponseCallback<OcsUserList> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getNextcloudAPI().searchUser(searchTerm), responseCallback);
    }

    public Disposable getSingleUserData(String userUid, @NonNull ResponseCallback<OcsUser> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getNextcloudAPI().getSingleUserData(userUid), responseCallback);
    }

    public Disposable searchGroupMembers(String groupUID, @NonNull ResponseCallback<GroupMemberUIDs> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getNextcloudAPI().searchGroupMembers(groupUID), responseCallback);
    }

    public Disposable getActivitiesForCard(long cardId, @NonNull ResponseCallback<List<Activity>> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getNextcloudAPI().getActivitiesForCard(cardId), responseCallback);
    }

    public Disposable createBoard(Board board, @NonNull ResponseCallback<FullBoard> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getDeckAPI().createBoard(board), responseCallback);
    }

    public Disposable deleteBoard(Board board, @NonNull ResponseCallback<Void> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getDeckAPI().deleteBoard(board.getId()), responseCallback);
    }

    public Disposable updateBoard(Board board, @NonNull ResponseCallback<FullBoard> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getDeckAPI().updateBoard(board.getId(), board), responseCallback);
    }

    public Disposable createAccessControl(long remoteBoardId, AccessControl acl, @NonNull ResponseCallback<AccessControl> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getDeckAPI().createAccessControl(remoteBoardId, acl), responseCallback);
    }

    public Disposable updateAccessControl(long remoteBoardId, AccessControl acl, @NonNull ResponseCallback<AccessControl> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getDeckAPI().updateAccessControl(remoteBoardId, acl.getId(), acl), responseCallback);
    }

    public Disposable deleteAccessControl(long remoteBoardId, AccessControl acl, @NonNull ResponseCallback<Void> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getDeckAPI().deleteAccessControl(remoteBoardId, acl.getId(), acl), responseCallback);
    }

    public Disposable getStacks(long boardId, @NonNull ResponseCallback<List<FullStack>> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getDeckAPI().getStacks(boardId, getLastSyncDateFormatted(responseCallback.getAccount().getId())), responseCallback);
    }

    public Disposable getStack(long boardId, long stackId, @NonNull ResponseCallback<FullStack> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getDeckAPI().getStack(boardId, stackId, getLastSyncDateFormatted(responseCallback.getAccount().getId())), responseCallback);
    }

    public Disposable createStack(Board board, Stack stack, @NonNull ResponseCallback<FullStack> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getDeckAPI().createStack(board.getId(), stack), responseCallback);
    }

    public Disposable deleteStack(Board board, Stack stack, @NonNull ResponseCallback<Void> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getDeckAPI().deleteStack(board.getId(), stack.getId()), responseCallback);

    }

    public Disposable updateStack(Board board, Stack stack, @NonNull ResponseCallback<FullStack> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getDeckAPI().updateStack(board.getId(), stack.getId(), stack), responseCallback);
    }

    public Disposable getCard(long boardId, long stackId, long cardId, @NonNull ResponseCallback<FullCard> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> {
            final Account account = responseCallback.getAccount();
            if (account.getServerDeckVersionAsObject().supportsFileAttachments()) {
                return provider.getDeckAPI().getCard_1_1(boardId, stackId, cardId, getLastSyncDateFormatted(responseCallback.getAccount().getId()));
            }
            return provider.getDeckAPI().getCard_1_0(boardId, stackId, cardId, getLastSyncDateFormatted(responseCallback.getAccount().getId()));
        }, responseCallback);
    }

    public Disposable createCard(long boardId, long stackId, Card card, @NonNull ResponseCallback<FullCard> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getDeckAPI().createCard(boardId, stackId, card), responseCallback);
    }

    public Disposable deleteCard(long boardId, long stackId, Card card, @NonNull ResponseCallback<Void> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getDeckAPI().deleteCard(boardId, stackId, card.getId()), responseCallback);
    }

    public Disposable updateCard(long boardId, long stackId, CardUpdate card, @NonNull ResponseCallback<FullCard> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getDeckAPI().updateCard(boardId, stackId, card.getId(), card), responseCallback);
    }

    public Disposable assignUserToCard(long boardId, long stackId, long cardId, String userUID, @NonNull ResponseCallback<Void> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getDeckAPI().assignUserToCard(boardId, stackId, cardId, userUID), responseCallback);
    }

    public Disposable unassignUserFromCard(long boardId, long stackId, long cardId, String userUID, @NonNull ResponseCallback<Void> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getDeckAPI().unassignUserFromCard(boardId, stackId, cardId, userUID), responseCallback);
    }

    public Disposable assignLabelToCard(long boardId, long stackId, long cardId, long labelId, @NonNull ResponseCallback<Void> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getDeckAPI().assignLabelToCard(boardId, stackId, cardId, labelId), responseCallback);
    }

    public Disposable unassignLabelFromCard(long boardId, long stackId, long cardId, long labelId, @NonNull ResponseCallback<Void> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getDeckAPI().unassignLabelFromCard(boardId, stackId, cardId, labelId), responseCallback);
    }


    // Labels

    public Disposable createLabel(long boardId, Label label, @NonNull ResponseCallback<Label> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getDeckAPI().createLabel(boardId, label), responseCallback);
    }

    public Disposable deleteLabel(long boardId, Label label, @NonNull ResponseCallback<Void> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getDeckAPI().deleteLabel(boardId, label.getId()), responseCallback);
    }

    public Disposable updateLabel(long boardId, Label label, @NonNull ResponseCallback<Label> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getDeckAPI().updateLabel(boardId, label.getId(), label), responseCallback);
    }

    public Disposable reorder(long boardId, long currentStackId, long cardId, long newStackId, int newPosition, @NonNull ResponseCallback<List<FullCard>> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getDeckAPI().moveCard(boardId, currentStackId, cardId, new Reorder(newPosition, (int) newStackId)), responseCallback);
    }


    // Attachments

    public Disposable uploadAttachment(Long remoteBoardId, long remoteStackId, long remoteCardId, File attachment, @NonNull ResponseCallback<Attachment> responseCallback) {
        ensureInternetConnection();
        final Account account = responseCallback.getAccount();
        final String type = account.getServerDeckVersionAsObject().supportsFileAttachments()
                ? EAttachmentType.FILE.getValue()
                : EAttachmentType.DECK_FILE.getValue();
        final MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", attachment.getName(), RequestBody.create(MediaType.parse(getMimeType(attachment)), attachment));
        final MultipartBody.Part typePart = MultipartBody.Part.createFormData("type", null, RequestBody.create(MediaType.parse(TEXT_PLAIN), type));
        return RequestHelper.request(provider, () -> provider.getDeckAPI().uploadAttachment(remoteBoardId, remoteStackId, remoteCardId, typePart, filePart), responseCallback);
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

    public Disposable updateAttachment(Long remoteBoardId, long remoteStackId, long remoteCardId, long remoteAttachmentId, String contentType, Uri attachmentUri, @NonNull ResponseCallback<Attachment> responseCallback) {
        ensureInternetConnection();
        final File attachment = new File(attachmentUri.getPath());
        final String type = responseCallback.getAccount().getServerDeckVersionAsObject().supportsFileAttachments()
                ? EAttachmentType.FILE.getValue()
                : EAttachmentType.DECK_FILE.getValue();
        final MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", attachment.getName(), RequestBody.create(MediaType.parse(contentType), attachment));
        final MultipartBody.Part typePart = MultipartBody.Part.createFormData("type", attachment.getName(), RequestBody.create(MediaType.parse(TEXT_PLAIN), type));
        return RequestHelper.request(provider, () -> provider.getDeckAPI().updateAttachment(remoteBoardId, remoteStackId, remoteCardId, remoteAttachmentId, typePart, filePart), responseCallback);
    }

    public Disposable downloadAttachment(Long remoteBoardId, long remoteStackId, long remoteCardId, long remoteAttachmentId, @NonNull ResponseCallback<ResponseBody> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getDeckAPI().downloadAttachment(remoteBoardId, remoteStackId, remoteCardId, remoteAttachmentId), responseCallback);
    }

    public Disposable deleteAttachment(Long remoteBoardId, long remoteStackId, long remoteCardId, long remoteAttachmentId, @NonNull ResponseCallback<Void> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getDeckAPI().deleteAttachment(remoteBoardId, remoteStackId, remoteCardId, remoteAttachmentId), responseCallback);
    }

    public Disposable restoreAttachment(Long remoteBoardId, long remoteStackId, long remoteCardId, long remoteAttachmentId, @NonNull ResponseCallback<Attachment> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getDeckAPI().restoreAttachment(remoteBoardId, remoteStackId, remoteCardId, remoteAttachmentId), responseCallback);
    }

    public Disposable getCommentsForRemoteCardId(Long remoteCardId, @NonNull ResponseCallback<OcsComment> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getNextcloudAPI().getCommentsForCard(remoteCardId), responseCallback);
    }

    public Disposable createCommentForCard(DeckComment comment, @NonNull ResponseCallback<OcsComment> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getNextcloudAPI().createCommentForCard(comment.getObjectId(), comment), responseCallback);
    }

    public Disposable updateCommentForCard(DeckComment comment, @NonNull ResponseCallback<OcsComment> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getNextcloudAPI().updateCommentForCard(comment.getObjectId(), comment.getId(), comment), responseCallback);
    }

    public Disposable deleteCommentForCard(DeckComment comment, @NonNull ResponseCallback<Void> responseCallback) {
        ensureInternetConnection();
        return RequestHelper.request(provider, () -> provider.getNextcloudAPI().deleteCommentForCard(comment.getObjectId(), comment.getId()), responseCallback);
    }
}
