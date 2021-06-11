package it.niedermann.nextcloud.deck.ui.preparecreate;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.exceptions.UploadAttachmentFailedException;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.ui.PickStackActivity;
import it.niedermann.nextcloud.deck.ui.sharetarget.ShareProgressDialogFragment;
import it.niedermann.nextcloud.deck.ui.sharetarget.ShareProgressViewModel;
import it.niedermann.nextcloud.deck.util.MimeTypeUtil;

import static it.niedermann.nextcloud.deck.DeckApplication.saveCurrentAccount;
import static it.niedermann.nextcloud.deck.DeckApplication.saveCurrentBoardId;
import static it.niedermann.nextcloud.deck.DeckApplication.saveCurrentStackId;
import static it.niedermann.nextcloud.deck.util.FilesUtil.copyContentUriToTempFile;
import static java.net.HttpURLConnection.HTTP_CONFLICT;

public class PrepareCreateActivity extends PickStackActivity {

    private PrepareCreateViewModel viewModel;
    private ShareProgressViewModel shareProgressViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.add_card);
        }

        viewModel = new ViewModelProvider(this).get(PrepareCreateViewModel.class);
        shareProgressViewModel = new ViewModelProvider(this).get(ShareProgressViewModel.class);
    }

    @Override
    protected void onSubmit(Account account, long boardId, long stackId, @NonNull IResponseCallback<Void> callback) {
        final FullCard fullCard;
        if (requireContent()) {
            fullCard = viewModel.createFullCard(account.getServerDeckVersionAsObject(), getContent());
        } else {
            final Intent intent = getIntent();
            if (intent == null) {
                throw new IllegalStateException("Intent should not be null because title is required.");
            }
            fullCard = viewModel.createFullCard(
                    account.getServerDeckVersionAsObject(),
                    intent.getStringExtra(Intent.EXTRA_SUBJECT),
                    intent.getStringExtra(Intent.EXTRA_TITLE),
                    intent.getStringExtra(Intent.EXTRA_TEXT)
            );
        }

        viewModel.saveCard(account.getId(), boardId, stackId, fullCard, new IResponseCallback<FullCard>() {
            @Override
            public void onResponse(FullCard response) {
                saveCurrentAccount(PrepareCreateActivity.this, account);
                saveCurrentBoardId(PrepareCreateActivity.this, account.getId(), boardId);
                saveCurrentStackId(PrepareCreateActivity.this, account.getId(), boardId, stackId);

                runOnUiThread(() -> {
                    attachSharedFiles(Executors.newSingleThreadExecutor(), account, boardId, fullCard, getIntent());
                    shareProgressViewModel.getProgress().observe(PrepareCreateActivity.this, (progress) -> {
                        if (Objects.equals(progress, shareProgressViewModel.getMaxValue())) {
                            final Collection<Throwable> exceptions = shareProgressViewModel.getExceptions().getValue();
                            if (exceptions == null || exceptions.size() == 0) {
                                callback.onResponse(null);
                            }
                        }
                    });
                });
            }

            @Override
            @SuppressLint("MissingSuperCall")
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }

    private void attachSharedFiles(@NonNull ExecutorService executor, @NonNull Account account, long boardLocalId, @NonNull FullCard fullCard, @Nullable Intent intent) {
        if (intent == null) {
            return;
        }

        final Intent receivedIntent = getIntent();
        final String receivedAction = receivedIntent.getAction();
        final String receivedType = receivedIntent.getType();
        DeckLog.info(receivedAction);
        DeckLog.info(receivedType);

        final boolean isFile = !MimeTypeUtil.isTextPlain(receivedType);
        if (isFile) {
            if (Intent.ACTION_SEND.equals(receivedIntent.getAction())) {
                appendFilesAndFinish(executor, account, boardLocalId, fullCard, Collections.singleton(receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM)));
            } else if (Intent.ACTION_SEND_MULTIPLE.equals(receivedIntent.getAction())) {
                final List<Parcelable> mStreamsToUpload = new ArrayList<>(1);
                @Nullable List<Parcelable> listOfParcelables = receivedIntent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                if (listOfParcelables != null) {
                    mStreamsToUpload.addAll(listOfParcelables);
                }
                appendFilesAndFinish(executor, account, boardLocalId, fullCard, mStreamsToUpload);
            } else {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.error)
                        .setMessage(R.string.operation_not_yet_supported)
                        .setPositiveButton(R.string.simple_close, (a, b) -> finish())
                        .create().show();
            }
        }
    }

    private void appendFilesAndFinish(@NonNull ExecutorService executor, @NonNull Account account, long boardLocalId, @NonNull FullCard fullCard, @NonNull Collection<Parcelable> mStreamsToUpload) {
        ShareProgressDialogFragment.newInstance(account, boardLocalId, fullCard.getLocalId())
                .show(getSupportFragmentManager(), ShareProgressDialogFragment.class.getSimpleName());
        shareProgressViewModel.setMax(mStreamsToUpload.size());
        shareProgressViewModel.targetCardTitle = fullCard.getCard().getTitle();

        for (Parcelable sourceStream : mStreamsToUpload) {
            if (!(sourceStream instanceof Uri)) {
                shareProgressViewModel.addException(new UploadAttachmentFailedException("Expected sourceStream to be " + Uri.class.getSimpleName() + " but was: " + (sourceStream == null ? null : sourceStream.getClass().getSimpleName())));
                return;
            }
            final Uri uri = (Uri) sourceStream;
            if (!ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
                shareProgressViewModel.addException(new UploadAttachmentFailedException("Unhandled URI scheme: " + uri.getScheme()));
                return;
            }

            executor.submit(() -> {
                try {
                    final File tempFile = copyContentUriToTempFile(this, uri, fullCard.getAccountId(), fullCard.getCard().getLocalId());
                    final String mimeType = getContentResolver().getType(uri);
                    if (mimeType == null) {
                        throw new IllegalArgumentException("MimeType of uri is null. [" + uri + "]");
                    }
                    viewModel.addAttachmentToCard(fullCard.getAccountId(), fullCard.getCard().getLocalId(), mimeType, tempFile, new IResponseCallback<Attachment>() {
                        @Override
                        public void onResponse(Attachment response) {
                            tempFile.delete();
                            runOnUiThread(shareProgressViewModel::increaseProgress);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            runOnUiThread(() -> {
                                if (throwable instanceof NextcloudHttpRequestFailedException && ((NextcloudHttpRequestFailedException) throwable).getStatusCode() == HTTP_CONFLICT) {
                                    IResponseCallback.super.onError(throwable);
                                    shareProgressViewModel.addDuplicateAttachment(tempFile.getName());
                                } else {
                                    shareProgressViewModel.addException(throwable);
                                }
                            });
                        }
                    });
                } catch (Throwable t) {
                    runOnUiThread(() -> shareProgressViewModel.addException(new UploadAttachmentFailedException("Error while uploading attachment for uri [" + uri + "]", t)));
                }
            });
        }
    }

    @Override
    protected boolean showBoardsWithoutEditPermission() {
        return false;
    }

    @Override
    protected boolean requireContent() {
        final Intent intent = getIntent();
        return intent == null || (TextUtils.isEmpty(intent.getStringExtra(Intent.EXTRA_SUBJECT)) &&
                TextUtils.isEmpty(intent.getStringExtra(Intent.EXTRA_TITLE)) &&
                TextUtils.isEmpty(intent.getStringExtra(Intent.EXTRA_TEXT)));
    }

}