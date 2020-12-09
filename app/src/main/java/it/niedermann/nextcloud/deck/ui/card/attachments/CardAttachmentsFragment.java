package it.niedermann.nextcloud.deck.ui.card.attachments;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import it.niedermann.android.util.DimensionUtil;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.FragmentCardEditTabAttachmentsBinding;
import it.niedermann.nextcloud.deck.exceptions.UploadAttachmentFailedException;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.WrappedLiveData;
import it.niedermann.nextcloud.deck.ui.branding.BrandedFragment;
import it.niedermann.nextcloud.deck.ui.branding.BrandedSnackbar;
import it.niedermann.nextcloud.deck.ui.card.EditCardViewModel;
import it.niedermann.nextcloud.deck.ui.card.attachments.picker.AbstractPickerAdapter;
import it.niedermann.nextcloud.deck.ui.card.attachments.picker.ContactAdapter;
import it.niedermann.nextcloud.deck.ui.card.attachments.picker.FileAdapter;
import it.niedermann.nextcloud.deck.ui.card.attachments.picker.FileAdapterLegacy;
import it.niedermann.nextcloud.deck.ui.card.attachments.picker.GalleryAdapter;
import it.niedermann.nextcloud.deck.ui.card.attachments.picker.GalleryItemDecoration;
import it.niedermann.nextcloud.deck.ui.card.attachments.previewdialog.PreviewDialog;
import it.niedermann.nextcloud.deck.ui.card.attachments.previewdialog.PreviewDialogViewModel;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.deck.ui.takephoto.TakePhotoActivity;
import it.niedermann.nextcloud.deck.util.DeckColorUtil;
import it.niedermann.nextcloud.deck.util.VCardUtil;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.os.Build.VERSION_CODES.M;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;
import static androidx.core.content.PermissionChecker.checkSelfPermission;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN;
import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.applyBrandToFAB;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.isBrandingEnabled;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.readBrandMainColor;
import static it.niedermann.nextcloud.deck.ui.card.attachments.CardAttachmentAdapter.VIEW_TYPE_DEFAULT;
import static it.niedermann.nextcloud.deck.ui.card.attachments.CardAttachmentAdapter.VIEW_TYPE_IMAGE;
import static it.niedermann.nextcloud.deck.util.AttachmentUtil.copyContentUriToTempFile;
import static java.net.HttpURLConnection.HTTP_CONFLICT;

public class CardAttachmentsFragment extends BrandedFragment implements AttachmentDeletedListener, AttachmentClickedListener {

    private FragmentCardEditTabAttachmentsBinding binding;
    private EditCardViewModel editViewModel;
    private PreviewDialogViewModel previewViewModel;
    private BottomSheetBehavior<LinearLayout> mBottomSheetBehaviour;

    private RecyclerView.ItemDecoration galleryItemDecoration;

    private static final int REQUEST_CODE_PICK_FILE = 1;
    private static final int REQUEST_CODE_PICK_FILE_PERMISSION = 2;
    private static final int REQUEST_CODE_PICK_CAMERA = 3;
    private static final int REQUEST_CODE_PICK_GALLERY_PERMISSION = 4;
    private static final int REQUEST_CODE_PICK_CONTACT = 5;
    private static final int REQUEST_CODE_PICK_CONTACT_PICKER_PERMISSION = 6;

    @ColorInt
    private int accentColor;
    @ColorInt
    private int primaryColor;

    private CardAttachmentAdapter adapter;

    private AbstractPickerAdapter<?> pickerAdapter;

    private final OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            mBottomSheetBehaviour.setState(STATE_HIDDEN);
        }
    };

    private int clickedItemPosition;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCardEditTabAttachmentsBinding.inflate(inflater, container, false);
        editViewModel = new ViewModelProvider(requireActivity()).get(EditCardViewModel.class);
        previewViewModel = new ViewModelProvider(requireActivity()).get(PreviewDialogViewModel.class);
        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.gallery) {
                showGalleryPicker();
            } else if (item.getItemId() == R.id.contacts) {
                showContactPicker();
            } else if (item.getItemId() == R.id.files) {
                showFilePicker();
            }
            return true;
        });
        accentColor = ContextCompat.getColor(requireContext(), R.color.accent);
        primaryColor = ContextCompat.getColor(requireContext(), R.color.primary);

        // This might be a zombie fragment with an empty EditCardViewModel after Android killed the activity (but not the fragment instance
        // See https://github.com/stefan-niedermann/nextcloud-deck/issues/478
        if (editViewModel.getFullCard() == null) {
            DeckLog.logError(new IllegalStateException("Cannot populate " + CardAttachmentsFragment.class.getSimpleName() + " because viewModel.getFullCard() is null"));
            return binding.getRoot();
        }

        adapter = new CardAttachmentAdapter(
                getChildFragmentManager(),
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
        galleryItemDecoration = new GalleryItemDecoration(DimensionUtil.INSTANCE.dpToPx(requireContext(), R.dimen.spacer_1qx));
        mBottomSheetBehaviour = BottomSheetBehavior.from(binding.bottomSheetParent);
        mBottomSheetBehaviour.setDraggable(true);
        mBottomSheetBehaviour.setHideable(true);
        mBottomSheetBehaviour.setState(STATE_HIDDEN);
        mBottomSheetBehaviour.addBottomSheetCallback(new CardAttachmentsBottomsheetBehaviorCallback(
                requireContext(), backPressedCallback, binding.fab, binding.pickerBackdrop, binding.bottomNavigation,
                R.color.mdtp_transparent_black, android.R.color.transparent, R.dimen.attachments_bottom_navigation_height));
        binding.pickerBackdrop.setOnClickListener(v -> mBottomSheetBehaviour.setState(STATE_HIDDEN));

        final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        final int spanCount = (int) ((displayMetrics.widthPixels / displayMetrics.density) / getResources().getInteger(R.integer.max_dp_attachment_column));
        final GridLayoutManager glm = new GridLayoutManager(getContext(), spanCount);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case VIEW_TYPE_IMAGE:
                        return 1;
                    case VIEW_TYPE_DEFAULT:
                    default:
                        return spanCount;
                }
            }
        });
        binding.attachmentsList.setLayoutManager(glm);
        if (!editViewModel.isCreateMode()) {
            // https://android-developers.googleblog.com/2018/02/continuous-shared-element-transitions.html?m=1
            // https://github.com/android/animation-samples/blob/master/GridToPager/app/src/main/java/com/google/samples/gridtopager/fragment/ImagePagerFragment.java
            setExitSharedElementCallback(new SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    AttachmentViewHolder selectedViewHolder = (AttachmentViewHolder) binding.attachmentsList
                            .findViewHolderForAdapterPosition(clickedItemPosition);
                    if (selectedViewHolder != null) {
                        sharedElements.put(names.get(0), selectedViewHolder.getPreview());
                    }
                }
            });
            adapter.setAttachments(editViewModel.getFullCard().getAttachments(), editViewModel.getFullCard().getId());
        }

        if (editViewModel.canEdit()) {
            binding.fab.setOnClickListener(v -> {
                if (SDK_INT < LOLLIPOP) {
                    openNativeFilePicker();
                } else {
                    binding.bottomNavigation.setSelectedItemId(R.id.gallery);
                    showGalleryPicker();
                    mBottomSheetBehaviour.setState(STATE_COLLAPSED);
                    backPressedCallback.setEnabled(true);
                    requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), backPressedCallback);
                }
            });
            binding.fab.show();
            binding.attachmentsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0)
                        binding.fab.hide();
                    else if (dy < 0)
                        binding.fab.show();
                }
            });
        } else {
            binding.fab.hide();
            binding.emptyContentView.hideDescription();
        }
        @Nullable Context context = requireContext();
        applyBrand(isBrandingEnabled(context)
                ? readBrandMainColor(context)
                : ContextCompat.getColor(context, R.color.defaultBrand));
        return binding.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();
        backPressedCallback.setEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        backPressedCallback.setEnabled(binding.bottomNavigation.getTranslationY() == 0);
    }

    private void showGalleryPicker() {
        if (!(pickerAdapter instanceof GalleryAdapter)) {
            if (isPermissionRequestNeeded(READ_EXTERNAL_STORAGE) || isPermissionRequestNeeded(CAMERA)) {
                requestPermissions(new String[]{READ_EXTERNAL_STORAGE, CAMERA}, REQUEST_CODE_PICK_GALLERY_PERMISSION);
            } else {
                unbindPickerAdapter();
                pickerAdapter = new GalleryAdapter(requireContext(), (uri, pair) -> {
                    previewViewModel.prepareDialog(pair.first, pair.second);
                    PreviewDialog.newInstance().show(getChildFragmentManager(), PreviewDialog.class.getSimpleName());
                    observeOnce(previewViewModel.getResult(), getViewLifecycleOwner(), (submitPositive) -> {
                        if (submitPositive) {
                            onActivityResult(REQUEST_CODE_PICK_FILE, RESULT_OK, new Intent().setData(uri));
                        }
                    });
                }, this::openNativeCameraPicker, getViewLifecycleOwner());
                if (binding.pickerRecyclerView.getItemDecorationCount() == 0) {
                    binding.pickerRecyclerView.addItemDecoration(galleryItemDecoration);
                }
                binding.pickerRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
                binding.pickerRecyclerView.setAdapter(pickerAdapter);
            }
        }
    }

    private void showContactPicker() {
        if (!(pickerAdapter instanceof ContactAdapter)) {
            if (isPermissionRequestNeeded(READ_CONTACTS)) {
                requestPermissions(new String[]{READ_CONTACTS}, REQUEST_CODE_PICK_CONTACT_PICKER_PERMISSION);
            } else {
                unbindPickerAdapter();
                pickerAdapter = new ContactAdapter(requireContext(), (uri, pair) -> {
                    previewViewModel.prepareDialog(pair.first, pair.second);
                    PreviewDialog.newInstance().show(getChildFragmentManager(), PreviewDialog.class.getSimpleName());
                    observeOnce(previewViewModel.getResult(), getViewLifecycleOwner(), (submitPositive) -> {
                        if (submitPositive) {
                            onActivityResult(REQUEST_CODE_PICK_CONTACT, RESULT_OK, new Intent().setData(uri));
                        }
                    });
                }, this::openNativeContactPicker);
                removeGalleryItemDecoration();
                binding.pickerRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                binding.pickerRecyclerView.setAdapter(pickerAdapter);
            }
        }
    }

    private void showFilePicker() {
        if (!(pickerAdapter instanceof FileAdapter) && !(pickerAdapter instanceof FileAdapterLegacy)) {
            if (isPermissionRequestNeeded(READ_EXTERNAL_STORAGE)) {
                requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, REQUEST_CODE_PICK_FILE_PERMISSION);
            } else {
                unbindPickerAdapter();
                if (SDK_INT >= LOLLIPOP) {
//                    if (SDK_INT >= Build.VERSION_CODES.Q) {
//                        // TODO Only usable with Scoped Storage
//                        pickerAdapter = new FileAdapter(requireContext(), uri -> onActivityResult(REQUEST_CODE_PICK_FILE, RESULT_OK, new Intent().setData(uri)), this::openNativeFilePicker);
//                    } else {
                    pickerAdapter = new FileAdapterLegacy((uri, pair) -> {
                        previewViewModel.prepareDialog(pair.first, pair.second);
                        PreviewDialog.newInstance().show(getChildFragmentManager(), PreviewDialog.class.getSimpleName());
                        observeOnce(previewViewModel.getResult(), getViewLifecycleOwner(), (submitPositive) -> {
                            if (submitPositive) {
                                onActivityResult(REQUEST_CODE_PICK_FILE, RESULT_OK, new Intent().setData(uri));
                            }
                        });
                    }, this::openNativeFilePicker);
//                    }
                    removeGalleryItemDecoration();
                    binding.pickerRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    binding.pickerRecyclerView.setAdapter(pickerAdapter);
                }
            }
        }
    }

    private void openNativeCameraPicker() {
        if (SDK_INT >= LOLLIPOP) {
            startActivityForResult(TakePhotoActivity.createIntent(requireContext()), REQUEST_CODE_PICK_CAMERA);
        } else {
            ExceptionDialogFragment.newInstance(new UnsupportedOperationException("This feature requires Android 5"), editViewModel.getAccount()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
        }
    }

    private void openNativeContactPicker() {
        final Intent intent = new Intent(Intent.ACTION_PICK).setType(ContactsContract.Contacts.CONTENT_TYPE);
        if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_PICK_CONTACT);
        }
    }

    private void openNativeFilePicker() {
        startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType("*/*"), REQUEST_CODE_PICK_FILE);
    }

    /**
     * Checks the current Android version and whether the permission has already been granted.
     *
     * @param permission see {@link android.Manifest.permission}
     * @return whether or not requesting permission is needed
     */
    private boolean isPermissionRequestNeeded(@NonNull String permission) {
        return SDK_INT >= M && checkSelfPermission(requireActivity(), permission) != PERMISSION_GRANTED;
    }

    private void unbindPickerAdapter() {
        if (pickerAdapter != null) {
            pickerAdapter.onDestroy();
        }
    }

    private void removeGalleryItemDecoration() {
        if (binding.pickerRecyclerView.getItemDecorationCount() > 0) {
            binding.pickerRecyclerView.removeItemDecoration(galleryItemDecoration);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_PICK_CONTACT:
            case REQUEST_CODE_PICK_CAMERA:
            case REQUEST_CODE_PICK_FILE: {
                if (resultCode == RESULT_OK) {
                    final Uri sourceUri = requestCode == REQUEST_CODE_PICK_CONTACT
                            ? VCardUtil.getVCardContentUri(requireContext(), Uri.parse(data.getDataString()))
                            : data.getData();
                    try {
                        uploadNewAttachmentFromUri(sourceUri, requestCode == REQUEST_CODE_PICK_CAMERA
                                ? data.getType()
                                : requireContext().getContentResolver().getType(sourceUri));
                        mBottomSheetBehaviour.setState(STATE_HIDDEN);
                    } catch (Exception e) {
                        ExceptionDialogFragment.newInstance(e, editViewModel.getAccount()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                    }
                }
                break;
            }
            default: {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onDestroy() {
        if (this.pickerAdapter != null) {
            this.pickerAdapter.onDestroy();
            this.binding.pickerRecyclerView.setAdapter(null);
        }
        super.onDestroy();
    }

    private void uploadNewAttachmentFromUri(@NonNull Uri sourceUri, String mimeType) throws UploadAttachmentFailedException, IOException {
        if (sourceUri == null) {
            throw new UploadAttachmentFailedException("sourceUri is null");
        }
        switch (sourceUri.getScheme()) {
            case ContentResolver.SCHEME_CONTENT:
            case ContentResolver.SCHEME_FILE: {
                DeckLog.verbose("--- found content URL " + sourceUri.getPath());
                final File fileToUpload = copyContentUriToTempFile(requireContext(), sourceUri, editViewModel.getAccount().getId(), editViewModel.getFullCard().getLocalId());
                for (Attachment existingAttachment : editViewModel.getFullCard().getAttachments()) {
                    final String existingPath = existingAttachment.getLocalPath();
                    if (existingPath != null && existingPath.equals(fileToUpload.getAbsolutePath())) {
                        BrandedSnackbar.make(binding.coordinatorLayout, R.string.attachment_already_exists, Snackbar.LENGTH_LONG).show();
                        return;
                    }
                }
                final Instant now = Instant.now();
                final Attachment a = new Attachment();
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
                if (!editViewModel.isCreateMode()) {
                    WrappedLiveData<Attachment> liveData = editViewModel.addAttachmentToCard(editViewModel.getAccount().getId(), editViewModel.getFullCard().getLocalId(), a.getMimetype(), fileToUpload);
                    observeOnce(liveData, getViewLifecycleOwner(), (next) -> {
                        if (liveData.hasError()) {
                            Throwable t = liveData.getError();
                            if (t instanceof NextcloudHttpRequestFailedException && ((NextcloudHttpRequestFailedException) t).getStatusCode() == HTTP_CONFLICT) {
                                // https://github.com/stefan-niedermann/nextcloud-deck/issues/534
                                editViewModel.getFullCard().getAttachments().remove(a);
                                adapter.removeAttachment(a);
                                BrandedSnackbar.make(binding.coordinatorLayout, R.string.attachment_already_exists, Snackbar.LENGTH_LONG).show();
                            } else {
                                ExceptionDialogFragment.newInstance(new UploadAttachmentFailedException("Unknown URI scheme", t), editViewModel.getAccount()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                            }
                        } else {
                            editViewModel.getFullCard().getAttachments().remove(a);
                            editViewModel.getFullCard().getAttachments().add(0, next);
                            adapter.replaceAttachment(a, next);
                        }
                    });
                }
                break;
            }
            default: {
                throw new UploadAttachmentFailedException("Unknown URI scheme: " + sourceUri.getScheme());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PICK_FILE_PERMISSION: {
                if (checkSelfPermission(requireActivity(), READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
                    showFilePicker();
                } else {
                    Toast.makeText(requireContext(), R.string.cannot_upload_files_without_permission, Toast.LENGTH_LONG).show();
                }
                break;
            }
            case REQUEST_CODE_PICK_GALLERY_PERMISSION: {
                if (checkSelfPermission(requireActivity(), READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED && checkSelfPermission(requireActivity(), CAMERA) == PERMISSION_GRANTED) {
                    showGalleryPicker();
                } else {
                    Toast.makeText(requireContext(), R.string.cannot_upload_files_without_permission, Toast.LENGTH_LONG).show();
                }
                break;
            }
            case REQUEST_CODE_PICK_CONTACT_PICKER_PERMISSION: {
                if (checkSelfPermission(requireActivity(), READ_CONTACTS) == PERMISSION_GRANTED) {
                    showContactPicker();
                } else {
                    Toast.makeText(requireContext(), R.string.cannot_upload_files_without_permission, Toast.LENGTH_LONG).show();
                }
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onAttachmentDeleted(Attachment attachment) {
        adapter.removeAttachment(attachment);
        editViewModel.getFullCard().getAttachments().remove(attachment);
        if (!editViewModel.isCreateMode() && attachment.getLocalId() != null) {
            final WrappedLiveData<Void> deleteLiveData = editViewModel.deleteAttachmentOfCard(editViewModel.getAccount().getId(), editViewModel.getFullCard().getLocalId(), attachment.getLocalId());
            observeOnce(deleteLiveData, this, (next) -> {
                if (deleteLiveData.hasError() && !SyncManager.ignoreExceptionOnVoidError(deleteLiveData.getError())) {
                    ExceptionDialogFragment.newInstance(deleteLiveData.getError(), editViewModel.getAccount()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                }
            });
        }
    }

    @Override
    public void onAttachmentClicked(int position) {
        this.clickedItemPosition = position;
    }

    @Override
    public void applyBrand(int mainColor) {
        applyBrandToFAB(mainColor, binding.fab);
        adapter.applyBrand(mainColor);
        @ColorInt final int finalMainColor = DeckColorUtil.contrastRatioIsSufficient(mainColor, primaryColor)
                ? mainColor
                : accentColor;
        final ColorStateList list = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{
                        finalMainColor,
                        accentColor
                }
        );
        binding.bottomNavigation.setItemIconTintList(list);
        binding.bottomNavigation.setItemTextColor(list);
    }

    public static Fragment newInstance() {
        return new CardAttachmentsFragment();
    }
}
