package it.niedermann.nextcloud.deck.persistence.sync.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.ApiProvider;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.api.LastSyncUtil;
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
import it.niedermann.nextcloud.deck.model.propagation.CardUpdate;
import it.niedermann.nextcloud.deck.model.propagation.Reorder;
import it.niedermann.nextcloud.deck.persistence.util.RealPathUtils;
import it.niedermann.nextcloud.deck.util.DateUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class ServerAdapter {

    String prefKeyWifiOnly;

    private static final DateFormat API_FORMAT =
            new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss z", Locale.US);

    static {
        API_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private Context applicationContext;
    private ApiProvider provider;
    @Nullable private Activity sourceActivity;
    private SharedPreferences lastSyncPref;

    public ServerAdapter(Context applicationContext, @Nullable Activity sourceActivity) {
        this(applicationContext, sourceActivity, null);
    }
    public ServerAdapter(Context applicationContext, @Nullable Activity sourceActivity, String ssoAccountName) {
        this.applicationContext = applicationContext;
        this.sourceActivity = sourceActivity;
        prefKeyWifiOnly = applicationContext.getResources().getString(R.string.pref_key_wifi_only);
        provider = new ApiProvider(applicationContext, ssoAccountName);
        lastSyncPref = applicationContext.getSharedPreferences(
                applicationContext.getString(R.string.shared_preference_last_sync), Context.MODE_PRIVATE);
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
        if (!isConnected){
            throw new OfflineException();
        }
    }

    public boolean hasInternetConnection(){
        ConnectivityManager cm = (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
            if (sharedPreferences.getBoolean(prefKeyWifiOnly, false)){
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

    private Date getLastSync(long accountId) {
        Date lastSync = DateUtil.nowInGMT();
        lastSync.setTime(LastSyncUtil.getLastSync(accountId));

        return lastSync;
    }

    public void getBoards(IResponseCallback<List<FullBoard>> responseCallback) {
        RequestHelper.request(sourceActivity, provider, () ->
                provider.getDeckAPI().getBoards(true, getLastSyncDateFormatted(responseCallback.getAccount().getId())),
                responseCallback);
    }
    public void getCapabilities(IResponseCallback<Capabilities> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getNextcloudAPI().getCapabilities(), responseCallback);
    }

    public void getActivitiesForCard(long cardId, IResponseCallback<List<it.niedermann.nextcloud.deck.model.ocs.Activity>> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getNextcloudAPI().getActivitiesForCard(cardId), responseCallback);
    }

    public void createBoard(Board board, IResponseCallback<FullBoard> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getDeckAPI().createBoard(board), responseCallback);
    }


    public void deleteBoard(Board board, IResponseCallback<Void> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getDeckAPI().deleteBoard(board.getId()), responseCallback);
    }

    public void updateBoard(Board board, IResponseCallback<FullBoard> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getDeckAPI().updateBoard(board.getId(), board), responseCallback);
    }

    public void createAccessControl(long remoteBoardId, AccessControl acl, IResponseCallback<AccessControl> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getDeckAPI().createAccessControl(remoteBoardId, acl), responseCallback);
    }

    public void updateAccessControl(long remoteBoardId, AccessControl acl, IResponseCallback<AccessControl> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getDeckAPI().updateAccessControl(remoteBoardId, acl.getId(), acl), responseCallback);
    }

    public void deleteAccessControl(long remoteBoardId, AccessControl acl, IResponseCallback<Void> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getDeckAPI().deleteAccessControl(remoteBoardId, acl.getId(), acl), responseCallback);
    }

    public void getStacks(long boardId, IResponseCallback<List<FullStack>> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getDeckAPI().getStacks(boardId, getLastSyncDateFormatted(responseCallback.getAccount().getId())), responseCallback);
    }

    public void getStack(long boardId, long stackId, IResponseCallback<FullStack> responseCallback) {
        RequestHelper.request(sourceActivity, provider, () -> provider.getDeckAPI().getStack(boardId, stackId, getLastSyncDateFormatted(responseCallback.getAccount().getId())), responseCallback);
    }

    public void createStack(Board board, Stack stack, IResponseCallback<FullStack> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getDeckAPI().createStack(board.getId(), stack), responseCallback);
    }

    public void deleteStack(Board board, Stack stack, IResponseCallback<Void> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getDeckAPI().deleteStack(board.getId(), stack.getId()), responseCallback);

    }

    public void updateStack(Board board, Stack stack, IResponseCallback<FullStack> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getDeckAPI().updateStack(board.getId(), stack.getId(), stack), responseCallback);

    }

    public void getCard(long boardId, long stackId, long cardId, IResponseCallback<FullCard> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getDeckAPI().getCard(boardId, stackId, cardId, getLastSyncDateFormatted(responseCallback.getAccount().getId())), responseCallback);
    }

    public void createCard(long boardId, long stackId, Card card, IResponseCallback<FullCard> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getDeckAPI().createCard(boardId, stackId, card), responseCallback);
    }

    public void deleteCard(long boardId, long stackId, Card card, IResponseCallback<Void> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getDeckAPI().deleteCard(boardId, stackId, card.getId()), responseCallback);
    }

    public void updateCard(long boardId, long stackId, CardUpdate card, IResponseCallback<FullCard> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getDeckAPI().updateCard(boardId, stackId, card.getId(), card), responseCallback);
    }

    public void assignUserToCard(long boardId, long stackId, long cardId, String userUID, IResponseCallback<Void> responseCallback){
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getDeckAPI().assignUserToCard(boardId, stackId, cardId, userUID), responseCallback);
    }

    public void unassignUserFromCard(long boardId, long stackId, long cardId, String userUID, IResponseCallback<Void> responseCallback){
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getDeckAPI().unassignUserFromCard(boardId, stackId, cardId, userUID), responseCallback);
    }

    public void assignLabelToCard(long boardId, long stackId, long cardId, long labelId, IResponseCallback<Void> responseCallback){
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getDeckAPI().assignLabelToCard(boardId, stackId, cardId, labelId), responseCallback);
    }

    public void unassignLabelFromCard(long boardId, long stackId, long cardId, long labelId, IResponseCallback<Void> responseCallback){
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getDeckAPI().unassignLabelFromCard(boardId, stackId, cardId, labelId), responseCallback);
    }


    // ## LABELS
    public void createLabel(long boardId, Label label, IResponseCallback<Label> responseCallback){
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getDeckAPI().createLabel(boardId, label), responseCallback);
    }
    public void deleteLabel(long boardId, Label label, IResponseCallback<Void> responseCallback){
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getDeckAPI().deleteLabel(boardId, label.getId()), responseCallback);
    }
    public void updateLabel(long boardId, Label label, IResponseCallback<Label> responseCallback){
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getDeckAPI().updateLabel(boardId, label.getId(), label), responseCallback);
    }

    public void reorder(Long boardId, FullCard movedCard, long newStackId, int newPosition, IResponseCallback<List<FullCard>> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getDeckAPI().moveCard(boardId, movedCard.getCard().getStackId(), movedCard.getCard().getId(), new Reorder(newPosition, (int)newStackId)), responseCallback);
    }

    // ## ATTACHMENTS
    public void uploadAttachment(Long remoteBoardId, long remoteStackId, long remoteCardId, String contentType, Uri attachmentUri, IResponseCallback<Attachment> responseCallback) {
        ensureInternetConnection();
//        File attachment = new File(getUriRealPath( applicationContext, attachmentUri));
        File attachment = RealPathUtils.getRealPath(applicationContext, attachmentUri);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", attachment.getName(), RequestBody.create(MediaType.parse(contentType), attachment));
        MultipartBody.Part typePart = MultipartBody.Part.createFormData("type", attachment.getName(), RequestBody.create(MediaType.parse("text/plain"), "deck_file"));
        RequestHelper.request(sourceActivity, provider, () -> provider.getDeckAPI().uploadAttachment(remoteBoardId, remoteStackId, remoteCardId, typePart, filePart), responseCallback);
    }

    public void getAttachmentsForCard(Long remoteBoardId, long remoteStackId, long remoteCardId, String contentType, Uri attachmentUri, IResponseCallback<List<Attachment>> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getDeckAPI().getAttachments(remoteBoardId, remoteStackId, remoteCardId), responseCallback);
    }


    /* Get uri related content real local file path. */
    private String getUriRealPath(Context ctx, Uri uri)
    {
        String ret = "";

        if( isAboveKitKat() )
        {
            // Android OS above sdk version 19.
            ret = getUriRealPathAboveKitkat(ctx, uri);
        }else
        {
            // Android OS below sdk version 19
            ret = getImageRealPath(ctx.getContentResolver(), uri, null);
        }

        return ret;
    }

    @SuppressLint("NewApi")
    private String getUriRealPathAboveKitkat(Context ctx, Uri uri)
    {
        String ret = "";

        if(ctx != null && uri != null) {

            if(isContentUri(uri))
            {
                if(isGooglePhotoDoc(uri.getAuthority()))
                {
                    ret = uri.getLastPathSegment();
                }else {
                    ret = getImageRealPath(ctx.getContentResolver(), uri, null);
                }
            }else if(isFileUri(uri)) {
                ret = uri.getPath();
            }else if(isDocumentUri(ctx, uri)){

                // Get uri related document id.
                String documentId = DocumentsContract.getDocumentId(uri);

                // Get uri authority.
                String uriAuthority = uri.getAuthority();

                if(isMediaDoc(uriAuthority))
                {
                    String idArr[] = documentId.split(":");
                    if(idArr.length == 2)
                    {
                        // First item is document type.
                        String docType = idArr[0];

                        // Second item is document real id.
                        String realDocId = idArr[1];

                        // Get content uri by document type.
                        Uri mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        if("image".equals(docType))
                        {
                            mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        }else if("video".equals(docType))
                        {
                            mediaContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        }else if("audio".equals(docType))
                        {
                            mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }

                        // Get where clause with real document id.
                        String whereClause = MediaStore.Images.Media._ID + " = " + realDocId;

                        ret = getImageRealPath(ctx.getContentResolver(), mediaContentUri, whereClause);
                    }

                }else if(isDownloadDoc(uriAuthority))
                {
                    // Build download uri.
                    Uri downloadUri = Uri.parse("content://downloads/public_downloads");

                    // Append download document id at uri end.
                    Uri downloadUriAppendId = ContentUris.withAppendedId(downloadUri, Long.valueOf(documentId));

                    ret = getImageRealPath(ctx.getContentResolver(), downloadUriAppendId, null);

                }else if(isExternalStoreDoc(uriAuthority))
                {
                    String idArr[] = documentId.split(":");
                    if(idArr.length == 2)
                    {
                        String type = idArr[0];
                        String realDocId = idArr[1];

                        if("primary".equalsIgnoreCase(type))
                        {
                            ret = Environment.getExternalStorageDirectory() + "/" + realDocId;
                        }
                    }
                }
            }
        }

        return ret;
    }

    /* Check whether current android os version is bigger than kitkat or not. */
    private boolean isAboveKitKat()
    {
        boolean ret = false;
        ret = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        return ret;
    }

    /* Check whether this uri represent a document or not. */
    @SuppressLint("NewApi")
    private boolean isDocumentUri(Context ctx, Uri uri)
    {
        boolean ret = false;
        if(ctx != null && uri != null) {
            ret = DocumentsContract.isDocumentUri(ctx, uri);
        }
        return ret;
    }

    /* Check whether this uri is a content uri or not.
     *  content uri like content://media/external/images/media/1302716
     *  */
    private boolean isContentUri(Uri uri)
    {
        boolean ret = false;
        if(uri != null) {
            String uriSchema = uri.getScheme();
            if("content".equalsIgnoreCase(uriSchema))
            {
                ret = true;
            }
        }
        return ret;
    }

    /* Check whether this uri is a file uri or not.
     *  file uri like file:///storage/41B7-12F1/DCIM/Camera/IMG_20180211_095139.jpg
     * */
    private boolean isFileUri(Uri uri)
    {
        boolean ret = false;
        if(uri != null) {
            String uriSchema = uri.getScheme();
            if("file".equalsIgnoreCase(uriSchema))
            {
                ret = true;
            }
        }
        return ret;
    }


    /* Check whether this document is provided by ExternalStorageProvider. */
    private boolean isExternalStoreDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.android.externalstorage.documents".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by DownloadsProvider. */
    private boolean isDownloadDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.android.providers.downloads.documents".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by MediaProvider. */
    private boolean isMediaDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.android.providers.media.documents".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by google photos. */
    private boolean isGooglePhotoDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.google.android.apps.photos.content".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /* Return uri represented document file real local path.*/
    private String getImageRealPath(ContentResolver contentResolver, Uri uri, String whereClause)
    {
        String ret = "";

        // Query the uri with condition.
        Cursor cursor = contentResolver.query(uri, null, whereClause, null, null);

        if(cursor!=null)
        {
            boolean moveToFirst = cursor.moveToFirst();
            if(moveToFirst)
            {

                // Get columns name by uri type.
                String columnName = MediaStore.Images.Media.DATA;

                if( uri==MediaStore.Images.Media.EXTERNAL_CONTENT_URI )
                {
                    columnName = MediaStore.Images.Media.DATA;
                }else if( uri==MediaStore.Audio.Media.EXTERNAL_CONTENT_URI )
                {
                    columnName = MediaStore.Audio.Media.DATA;
                }else if( uri==MediaStore.Video.Media.EXTERNAL_CONTENT_URI )
                {
                    columnName = MediaStore.Video.Media.DATA;
                }

                // Get column index.
                int imageColumnIndex = cursor.getColumnIndex(columnName);

                // Get column value which is the uri related file local path.
                ret = cursor.getString(imageColumnIndex);
            }
        }

        return ret;
    }

    public void updateAttachment(Long remoteBoardId, long remoteStackId, long remoteCardId, long remoteAttachmentId, String contentType, Uri attachmentUri, IResponseCallback<Attachment> responseCallback) {
        ensureInternetConnection();
        File attachment = new File(attachmentUri.getPath());
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", attachment.getName(), RequestBody.create(MediaType.parse(contentType), attachment));
        MultipartBody.Part typePart = MultipartBody.Part.createFormData("type", attachment.getName(), RequestBody.create(MediaType.parse("text/plain"), "deck_file"));
        RequestHelper.request(sourceActivity, provider, () -> provider.getDeckAPI().updateAttachment(remoteBoardId, remoteStackId, remoteCardId, remoteAttachmentId, typePart, filePart), responseCallback);
    }

    public void downloadAttachment(Long remoteBoardId, long remoteStackId, long remoteCardId, long remoteAttachmentId, IResponseCallback<ResponseBody> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getDeckAPI().downloadAttachment(remoteBoardId, remoteStackId, remoteCardId, remoteAttachmentId), responseCallback);
    }

    public void deleteAttachment(Long remoteBoardId, long remoteStackId, long remoteCardId, long remoteAttachmentId, IResponseCallback<Void> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getDeckAPI().deleteAttachment(remoteBoardId, remoteStackId, remoteCardId, remoteAttachmentId), responseCallback);
    }
    public void restoreAttachment(Long remoteBoardId, long remoteStackId, long remoteCardId, long remoteAttachmentId, IResponseCallback<Attachment> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getDeckAPI().restoreAttachment(remoteBoardId, remoteStackId, remoteCardId, remoteAttachmentId), responseCallback);
    }
}
