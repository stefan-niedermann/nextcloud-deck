package it.niedermann.nextcloud.deck.deprecated.ui.card.attachments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN;
import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static it.niedermann.nextcloud.deck.deprecated.ui.card.attachments.CardAttachmentAdapter.VIEW_TYPE_IMAGE;
import static it.niedermann.nextcloud.deck.deprecated.util.FilesUtil.copyContentUriToTempFile;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.nextcloud.android.common.ui.theme.utils.ColorRole;
import com.nextcloud.android.sso.api.EmptyResponse;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import id.zelory.compressor.constraint.FormatConstraint;
import id.zelory.compressor.constraint.QualityConstraint;
import id.zelory.compressor.constraint.ResolutionConstraint;
import id.zelory.compressor.constraint.SizeConstraint;
import it.niedermann.nextcloud.deck.deprecated.util.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.FragmentCardEditTabAttachmentsBinding;
import it.niedermann.nextcloud.deck.exceptions.UploadAttachmentFailedException;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.repository.SyncRepository;
import it.niedermann.nextcloud.deck.deprecated.ui.card.EditCardViewModel;
import it.niedermann.nextcloud.deck.deprecated.ui.card.attachments.picker.AttachmentPicker;
import it.niedermann.nextcloud.deck.deprecated.ui.card.attachments.picker.AttachmentPickerAdapter;
import it.niedermann.nextcloud.deck.deprecated.ui.card.attachments.previewdialog.PreviewDialogViewModel;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.deck.deprecated.ui.takephoto.TakePhotoActivity;
import it.niedermann.nextcloud.deck.deprecated.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.deprecated.ui.theme.ThemedSnackbar;
import it.niedermann.nextcloud.deck.deprecated.util.JavaCompressor;
import it.niedermann.nextcloud.deck.deprecated.util.MimeTypeUtil;
import it.niedermann.nextcloud.deck.deprecated.util.VCardUtil;
import okhttp3.Headers;

public class CardAttachmentsFragment extends Fragment implements AttachmentDeletedListener, AttachmentInteractionListener, Consumer<CompletableFuture<List<Uri>>> {

    private FragmentCardEditTabAttachmentsBinding binding;
    private EditCardViewModel editViewModel;
    private PreviewDialogViewModel previewViewModel;
    private BottomSheetBehavior<LinearLayout> mBottomSheetBehaviour;
    private boolean compressImagesOnUpload = true;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final List<AttachmentPicker<?, ?>> pickers = new ArrayList<>();
    private CardAttachmentAdapter adapter;
    private AttachmentPickerAdapter attachmentPickerAdapter;


    private final OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            mBottomSheetBehaviour.setState(STATE_HIDDEN);
        }
    };

    private int clickedItemPosition;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final var registry = requireActivity().getActivityResultRegistry();
        final var cr = requireContext().getContentResolver();

        pickers.addAll(List.of(

                new AttachmentPicker.MultiBuilder<>(registry, R.string.files, R.drawable.type_file_36dp,
                        new ActivityResultContracts.GetMultipleContents())
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .setInput("*/*")
                        .build(),

                new AttachmentPicker.SingleBuilder<>(registry, R.string.camera, R.drawable.ic_photo_camera_24,
                        new TakePhotoActivity.TakePhoto())
                        .setPermissions(Manifest.permission.CAMERA)
                        .build(),

                new AttachmentPicker.MultiBuilder<>(registry, R.string.gallery, R.drawable.ic_image_24dp,
                        new ActivityResultContracts.PickMultipleVisualMedia())
                        .setResultMapper((Consumer<List<Uri>>) uris -> uris.forEach(uri -> cr.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)))
                        .setInput(new PickVisualMediaRequest.Builder()
                                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                                .build())
                        .build(),

//                new AttachmentPicker.SingleBuilder<>(registry, R.string.voice_recorder, R.drawable.ic_music_note_24dp,
//                        new VoiceRecorder())
//                        .setResultMapper((Function<Uri, Uri>) uri -> {
//                            String[] proj = {MediaStore.Audio.Media.DATA};
//                            //Cursor cursor = managedQuery(contentUri, proj, null, null, null);
//                            Cursor cursor = requireContext().getContentResolver().query(uri, proj, null, null, null); //Since manageQuery is deprecated
//                            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
//                            cursor.moveToFirst();
//                            final var newUri = cursor.getString(column_index);
//                            return Uri.parse(newUri);
//                        })
//                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
//                        .build(),


                new AttachmentPicker.MultiBuilder<>(registry, R.string.videos, R.drawable.ic_local_movies_24dp,
                        new ActivityResultContracts.PickMultipleVisualMedia())
                        .setInput(new PickVisualMediaRequest.Builder()
                                .setMediaType(ActivityResultContracts.PickVisualMedia.VideoOnly.INSTANCE)
                                .build())
                        .build(),

                new AttachmentPicker.SingleBuilder<>(registry, R.string.contacts, R.drawable.ic_person_24dp,
                        new ActivityResultContracts.PickContact())
                        .setPermissions(Manifest.permission.READ_CONTACTS)
                        .setResultMapper(uri -> uri == null ? null : VCardUtil.getVCardContentUri(requireContext(), uri))
                        .build()
        ));

        final var lifecycle = getLifecycle();
        pickers.forEach(lifecycle::addObserver);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCardEditTabAttachmentsBinding.inflate(inflater, container, false);
        editViewModel = new ViewModelProvider(requireActivity()).get(EditCardViewModel.class);
        previewViewModel = new ViewModelProvider(requireActivity()).get(PreviewDialogViewModel.class);

        // This might be a zombie fragment with an empty EditCardViewModel after Android killed the activity (but not the fragment instance
        // See https://github.com/stefan-niedermann/nextcloud-deck/issues/478
        if (editViewModel.getFullCard() == null) {
            DeckLog.logError(new IllegalStateException("Cannot populate " + CardAttachmentsFragment.class.getSimpleName() + " because viewModel.getFullCard() is null"));
            return binding.getRoot();
        }

        setupAttachments();
        setupPickers();

        if (editViewModel.canEdit()) {
            binding.fab.setOnClickListener(v -> {
                mBottomSheetBehaviour.setState(STATE_COLLAPSED);
                backPressedCallback.setEnabled(true);
                requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), backPressedCallback);
            });
            binding.fab.show();
            binding.attachmentsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) binding.fab.hide();
                    else if (dy < 0) binding.fab.show();
                }
            });
        } else {
            binding.fab.hide();
            binding.emptyContentView.hideDescription();
        }
        final var sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        compressImagesOnUpload = sharedPreferences.getBoolean(getString(R.string.pref_key_compress_image_attachments), true);
        editViewModel.getBoardColor().observe(getViewLifecycleOwner(), this::applyTheme);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        backPressedCallback.setEnabled(editViewModel.getAttachmentsBackPressedCallbackStatus());
    }

    private void setupAttachments() {
        adapter = new CardAttachmentAdapter(getChildFragmentManager(),
                requireActivity().getMenuInflater(),
                this,
                editViewModel.getAccount(),
                editViewModel.getFullCard().getLocalId());
        binding.attachmentsList.setAdapter(adapter);

        adapter.isEmpty().observe(getViewLifecycleOwner(), (isEmpty) -> {
            if (isEmpty) {
                this.binding.emptyContentView.setVisibility(VISIBLE);
                this.binding.attachmentsList.setVisibility(GONE);
            } else {
                this.binding.emptyContentView.setVisibility(GONE);
                this.binding.attachmentsList.setVisibility(VISIBLE);
            }
        });

        final var displayMetrics = getResources().getDisplayMetrics();
        final int spanCount = (int) ((displayMetrics.widthPixels / displayMetrics.density) / getResources().getInteger(R.integer.max_dp_attachment_picker));
        final var attachmentGlm = new GridLayoutManager(getContext(), spanCount);
        attachmentGlm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return switch (adapter.getItemViewType(position)) {
                    case VIEW_TYPE_IMAGE -> 1;
                    default -> spanCount;
                };
            }
        });
        binding.attachmentsList.setLayoutManager(attachmentGlm);
        // https://android-developers.googleblog.com/2018/02/continuous-shared-element-transitions.html?m=1
        // https://github.com/android/animation-samples/blob/master/GridToPager/app/src/main/java/com/google/samples/gridtopager/fragment/ImagePagerFragment.java
        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                final var selectedViewHolder = (AttachmentViewHolder) binding.attachmentsList.findViewHolderForAdapterPosition(clickedItemPosition);
                if (selectedViewHolder != null) {
                    sharedElements.put(names.get(0), selectedViewHolder.getPreview());
                }
            }
        });
        adapter.setAttachments(editViewModel.getFullCard().getAttachments(), editViewModel.getFullCard().getId());
    }

    private void setupPickers() {
        final var displayMetrics = getResources().getDisplayMetrics();
        final int spanCount = (int) ((displayMetrics.widthPixels / displayMetrics.density) / getResources().getInteger(R.integer.max_dp_attachment_picker));

        attachmentPickerAdapter = new AttachmentPickerAdapter(pickers, this);
        binding.attachmentPicker.setAdapter(attachmentPickerAdapter);
        binding.attachmentPicker.setLayoutManager(new GridLayoutManager(requireContext(), spanCount));

        mBottomSheetBehaviour = BottomSheetBehavior.from(binding.bottomSheetParent);
        mBottomSheetBehaviour.setDraggable(true);
        mBottomSheetBehaviour.setHideable(true);
        mBottomSheetBehaviour.setState(STATE_HIDDEN);
        mBottomSheetBehaviour.addBottomSheetCallback(new CardAttachmentsBottomsheetBehaviorCallback(requireContext(), backPressedCallback, binding.fab, binding.pickerBackdrop));
        binding.pickerBackdrop.setOnClickListener(v -> mBottomSheetBehaviour.setState(STATE_HIDDEN));
    }

    @Override
    public void accept(@NonNull CompletableFuture<List<Uri>> result) {
        result.whenComplete((uris, throwable) -> {
            if (throwable != null) {
                handlePickerException(throwable);

            } else if (uris == null || uris.isEmpty()) {
                DeckLog.info("No items selected");

            } else {
                DeckLog.verbose("Number of items selected: " + uris.size());
                try {
                    for (final var uri : uris) {
                        // TODO parallel?
                        uploadNewAttachmentFromUri(uri);
                    }
                } catch (Throwable t) {
                    ExceptionDialogFragment.newInstance(t, editViewModel.getAccount()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                } finally {
                    mBottomSheetBehaviour.setState(STATE_HIDDEN);
                }
            }
        });
    }

    private void handlePickerException(@NonNull Throwable throwable) {
        final var cause = throwable instanceof CompletionException ? throwable.getCause() : throwable;

        if (cause instanceof SecurityException) {
            Toast.makeText(requireContext(), R.string.cannot_upload_files_without_permission, Toast.LENGTH_LONG).show();

        } else if (cause instanceof ActivityNotFoundException) {
            Toast.makeText(requireContext(), R.string.no_matching_app_installed, Toast.LENGTH_LONG).show();

        } else {
            mBottomSheetBehaviour.setState(STATE_HIDDEN);
            ExceptionDialogFragment.newInstance(cause == null ? throwable : cause, editViewModel.getAccount()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
        }
    }

    private void uploadNewAttachmentFromUri(@NonNull Uri sourceUri) throws UploadAttachmentFailedException {
        switch (sourceUri.getScheme()) {
            case ContentResolver.SCHEME_CONTENT, ContentResolver.SCHEME_FILE -> {
                final var mimeType = requireContext().getContentResolver().getType(sourceUri);
                DeckLog.verbose("--- found content URL", sourceUri.getPath());
                // Separate Thread required because picked file might not yet be locally available
                // https://github.com/stefan-niedermann/nextcloud-deck/issues/814
                executor.submit(() -> {
                    try {
                        final File originalFile = copyContentUriToTempFile(requireContext(), sourceUri, editViewModel.getAccount().getId(), editViewModel.getFullCard().getLocalId());
                        requireActivity().runOnUiThread(() -> {
                            if (compressImagesOnUpload && MimeTypeUtil.isImage(mimeType)) {
                                try {
                                    JavaCompressor.compress((AppCompatActivity) requireActivity(), originalFile, (status, file) -> uploadNewAttachmentFromFile(status && file != null ? file : originalFile, mimeType), new ResolutionConstraint(1920, 1920), new SizeConstraint(1_000_000, 10, 10, 10), new FormatConstraint(Bitmap.CompressFormat.JPEG), new QualityConstraint(80));
                                } catch (Throwable t) {
                                    DeckLog.logError(t);
                                    uploadNewAttachmentFromFile(originalFile, mimeType);
                                }
                            } else {
                                uploadNewAttachmentFromFile(originalFile, mimeType);
                            }
                        });
                    } catch (IOException e) {
                        requireActivity().runOnUiThread(() -> ExceptionDialogFragment.newInstance(e, editViewModel.getAccount()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName()));
                    }
                });
            }
            default ->
                    throw new UploadAttachmentFailedException("Unknown URI scheme: " + sourceUri.getScheme());
        }
    }

    private void uploadNewAttachmentFromFile(@NonNull File fileToUpload, String mimeType) {
        final int color = editViewModel.getAccount().getColor();
        for (final var existingAttachment : editViewModel.getFullCard().getAttachments()) {
            final String existingPath = existingAttachment.getLocalPath();
            if (existingPath != null && existingPath.equals(fileToUpload.getAbsolutePath())) {
                ThemedSnackbar.make(binding.coordinatorLayout, R.string.attachment_already_exists, Snackbar.LENGTH_LONG, color).show();
                return;
            }
        }
        final var now = Instant.now();
        final var a = new Attachment();
        a.setMimetype(mimeType);
        a.setData(fileToUpload.getName());
        a.setFilename(fileToUpload.getName());
        a.setBasename(fileToUpload.getName());
        a.setFilesize(fileToUpload.length());
        a.setLocalPath(fileToUpload.getAbsolutePath());
        a.setLastModifiedLocal(now);
        a.setCreatedAt(now);
        a.setStatusEnum(DBStatus.LOCAL_EDITED);
        editViewModel.getFullCard().getAttachments().add(0, a);
        adapter.addAttachment(a);
        editViewModel.addAttachmentToCard(editViewModel.getAccount().getId(), editViewModel.getFullCard().getLocalId(), a.getMimetype(), fileToUpload, new IResponseCallback<>() {
            @Override
            public void onResponse(Attachment response, Headers headers) {
                requireActivity().runOnUiThread(() -> {
                    editViewModel.getFullCard().getAttachments().remove(a);
                    editViewModel.getFullCard().getAttachments().add(0, response);
                    adapter.replaceAttachment(a, response);
                });
            }

            @Override
            public void onError(Throwable throwable) {
                if (throwable instanceof NextcloudHttpRequestFailedException && ((NextcloudHttpRequestFailedException) throwable).getStatusCode() == HTTP_CONFLICT) {
                    IResponseCallback.super.onError(throwable);
                    // https://github.com/stefan-niedermann/nextcloud-deck/issues/534
                    editViewModel.getFullCard().getAttachments().remove(a);
                    adapter.removeAttachment(a);
                    ThemedSnackbar.make(binding.coordinatorLayout, R.string.attachment_already_exists, Snackbar.LENGTH_LONG, color).show();
                } else {
                    ExceptionDialogFragment.newInstance(new UploadAttachmentFailedException("Unknown URI scheme", throwable), editViewModel.getAccount()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                }
            }
        });
    }

    @Override
    public void onAttachmentDeleted(Attachment attachment) {
        adapter.removeAttachment(attachment);
        editViewModel.getFullCard().getAttachments().remove(attachment);
        if (attachment.getLocalId() != null) {
            editViewModel.deleteAttachmentOfCard(editViewModel.getAccount().getId(), editViewModel.getFullCard().getLocalId(), attachment.getLocalId(), new IResponseCallback<>() {
                @Override
                public void onResponse(EmptyResponse response, Headers headers) {
                    DeckLog.info("Successfully delete", Attachment.class.getSimpleName(), attachment.getFilename(), "from", Card.class.getSimpleName(), editViewModel.getFullCard().getCard().getTitle());
                }

                @Override
                public void onError(Throwable throwable) {
                    if (SyncRepository.isNoOnVoidError(throwable)) {
                        IResponseCallback.super.onError(throwable);
                        requireActivity().runOnUiThread(() -> ExceptionDialogFragment.newInstance(throwable, editViewModel.getAccount()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName()));
                    }
                }
            });
        }
    }

    @Override
    public void onAttachmentClicked(int position) {
        this.clickedItemPosition = position;
    }

    @Override
    public void onAppendToDescription(@NonNull String markdown) {
        if (editViewModel.canEdit()) {
            final var oldDescription = editViewModel.getFullCard().getCard().getDescription();
            if (TextUtils.isEmpty(oldDescription)) {
                editViewModel.changeDescriptionFromExternal(markdown);
            } else {
                editViewModel.changeDescriptionFromExternal(oldDescription + "\n\n" + markdown);
            }
        } else {
            Toast.makeText(requireContext(), R.string.insufficient_permission, Toast.LENGTH_LONG).show();
        }
    }

    private void applyTheme(@ColorInt int color) {
        final var utils = ThemeUtils.of(color, requireContext());

        utils.deck.themeEmptyContentView(binding.emptyContentView);
        utils.material.themeFAB(binding.fab);
        utils.platform.colorViewBackground(binding.bottomSheetParent, ColorRole.SURFACE);
        utils.material.themeDragHandleView(binding.dragHandle);

        adapter.applyTheme(color);
        attachmentPickerAdapter.applyTheme(color);
    }

    @Override
    public void onPause() {
        editViewModel.setAttachmentsBackPressedCallbackStatus(backPressedCallback.isEnabled());
        backPressedCallback.setEnabled(false);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        this.binding = null;
        super.onDestroy();
    }

    public static Fragment newInstance() {
        return new CardAttachmentsFragment();
    }

// THROWS SECURITYEXCEPTION
//    private static final class VoiceRecorder extends ActivityResultContract<Void, Uri> {
//        @NonNull
//        @Override
//        public Intent createIntent(@NonNull Context context, Void unused) {
//            return new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
//        }
//
//        @Override
//        public Uri parseResult(int resultCode, @Nullable Intent data) {
//            if (data == null) {
//                DeckLog.error("Recording voice failed.");
//                return null;
//            }
//
//            final var uri = data.getData();
//
//            if (uri == null) {
//                DeckLog.error("Recording voice failed.");
//                return null;
//            }
//
//            return uri;//Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, uri.getLastPathSegment());
//        }
//    }
}
