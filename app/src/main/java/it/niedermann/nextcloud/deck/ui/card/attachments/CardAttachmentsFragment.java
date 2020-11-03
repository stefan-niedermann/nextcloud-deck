package it.niedermann.nextcloud.deck.ui.card.attachments;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

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
import it.niedermann.nextcloud.deck.ui.card.attachments.picker.GalleryAdapter;
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
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HALF_EXPANDED;
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
    private EditCardViewModel viewModel;
    private BottomSheetBehavior<LinearLayout> mBottomSheetBehaviour;

    private static final int REQUEST_CODE_ADD_FILE = 1;
    private static final int REQUEST_CODE_ADD_FILE_PERMISSION = 2;
    private static final int REQUEST_CODE_CAMERA = 3;
    private static final int REQUEST_CODE_CAMERA_PERMISSION = 4;
    private static final int REQUEST_CODE_PICK_CONTACT = 5;
    private static final int REQUEST_CODE_PICK_CONTACT_PERMISSION = 6;

    private SyncManager syncManager;
    private CardAttachmentAdapter adapter;

    private ImageView[] brandedViews;

    private int clickedItemPosition;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCardEditTabAttachmentsBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(EditCardViewModel.class);

        if (SDK_INT < LOLLIPOP) {
            binding.pickCamera.setVisibility(GONE);
        }
        brandedViews = new ImageView[]{binding.pickCameraIamge, binding.pickContactIamge, binding.pickFileIamge};
        binding.pickCamera.setOnClickListener((v) -> {
            if (SDK_INT >= LOLLIPOP) {
                pickCamera();
            } else {
                Toast.makeText(requireContext(), R.string.min_api_21, Toast.LENGTH_SHORT).show();
            }
        });
        binding.pickContact.setOnClickListener((v) -> pickContact());
        binding.pickFile.setOnClickListener((v) -> pickFile());

        // This might be a zombie fragment with an empty EditCardViewModel after Android killed the activity (but not the fragment instance
        // See https://github.com/stefan-niedermann/nextcloud-deck/issues/478
        if (viewModel.getFullCard() == null) {
            DeckLog.logError(new IllegalStateException("Cannot populate " + CardAttachmentsFragment.class.getSimpleName() + " because viewModel.getFullCard() is null"));
            return binding.getRoot();
        }

        syncManager = new SyncManager(requireContext());
        adapter = new CardAttachmentAdapter(
                getChildFragmentManager(),
                requireActivity().getMenuInflater(),
                this,
                viewModel.getAccount(),
                viewModel.getFullCard().getLocalId());
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

        mBottomSheetBehaviour = BottomSheetBehavior.from(binding.bottomSheetParent);
        mBottomSheetBehaviour.setDraggable(true);
        mBottomSheetBehaviour.setHideable(true);
        mBottomSheetBehaviour.setState(STATE_HIDDEN);
        mBottomSheetBehaviour.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case STATE_HIDDEN: {
                        hidePicker();
                        binding.fab.show();
                        break;
                    }
                    case STATE_EXPANDED:
                    case STATE_HALF_EXPANDED: {
                        showPicker();
                        break;
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        if (SDK_INT >= LOLLIPOP) {
            GalleryAdapter galleryAdapter = new GalleryAdapter(requireContext());
            binding.pickerRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
            binding.pickerRecyclerView.setAdapter(galleryAdapter);
        }

        final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int spanCount = (int) ((displayMetrics.widthPixels / displayMetrics.density) / getResources().getInteger(R.integer.max_dp_attachment_column));
        GridLayoutManager glm = new GridLayoutManager(getContext(), spanCount);
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
        if (!viewModel.isCreateMode()) {
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
            adapter.setAttachments(viewModel.getFullCard().getAttachments(), viewModel.getFullCard().getId());
        }

        if (viewModel.canEdit()) {
            binding.fab.setOnClickListener(v -> {
//                picker = CardAttachmentPicker.newInstance();
//                picker.show(getChildFragmentManager(), CardAttachmentPicker.class.getSimpleName());
                mBottomSheetBehaviour.setState(STATE_HALF_EXPANDED);
                showPicker();
                binding.fab.hide();
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
        @Nullable Context context = getContext();
        if (context != null) {
            if (isBrandingEnabled(context)) {
                applyBrand(readBrandMainColor(context));
            } else { // Make sure that without branding the icons are still white
                if (SDK_INT >= LOLLIPOP) {
                    final ColorStateList colorStateList = ColorStateList.valueOf(Color.WHITE);
                    for (ImageView v : brandedViews) {
                        v.setImageTintList(colorStateList);
                    }
                }
            }
        }
        return binding.getRoot();
    }

    @RequiresApi(LOLLIPOP)
    public void pickCamera() {
        if (isPermissionRequestNeeded(CAMERA)) {
            requestPermissions(new String[]{CAMERA}, REQUEST_CODE_CAMERA_PERMISSION);
        } else {
            startActivityForResult(TakePhotoActivity.createIntent(requireContext()), REQUEST_CODE_CAMERA);
        }
    }

    public void pickContact() {
        if (isPermissionRequestNeeded(READ_CONTACTS)) {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_CODE_PICK_CONTACT_PERMISSION);
        } else {
            final Intent intent = new Intent(Intent.ACTION_PICK).setType(ContactsContract.Contacts.CONTENT_TYPE);
            if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_CODE_PICK_CONTACT);
            }
        }
    }

    public void pickFile() {
        if (isPermissionRequestNeeded(READ_EXTERNAL_STORAGE)) {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, REQUEST_CODE_ADD_FILE_PERMISSION);
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                    .addCategory(Intent.CATEGORY_OPENABLE)
                    .setType("*/*");
            startActivityForResult(intent, REQUEST_CODE_ADD_FILE);
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_PICK_CONTACT:
            case REQUEST_CODE_CAMERA:
            case REQUEST_CODE_ADD_FILE: {
                if (resultCode == RESULT_OK) {
                    final Uri sourceUri = requestCode == REQUEST_CODE_PICK_CONTACT
                            ? VCardUtil.getVCardContentUri(requireContext(), Uri.parse(data.getDataString()))
                            : data.getData();
                    try {
                        uploadNewAttachmentFromUri(sourceUri, requestCode == REQUEST_CODE_CAMERA
                                ? data.getType()
                                : requireContext().getContentResolver().getType(sourceUri));
                        mBottomSheetBehaviour.setState(STATE_HIDDEN);
                    } catch (Exception e) {
                        ExceptionDialogFragment.newInstance(e, viewModel.getAccount()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                    }
                }
                break;
            }
            default: {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void uploadNewAttachmentFromUri(@NonNull Uri sourceUri, String mimeType) throws UploadAttachmentFailedException, IOException {
        if (sourceUri == null) {
            throw new UploadAttachmentFailedException("sourceUri is null");
        }
        switch (sourceUri.getScheme()) {
            case ContentResolver.SCHEME_CONTENT:
            case ContentResolver.SCHEME_FILE: {
                DeckLog.verbose("--- found content URL " + sourceUri.getPath());
                final File fileToUpload = copyContentUriToTempFile(requireContext(), sourceUri, viewModel.getAccount().getId(), viewModel.getFullCard().getLocalId());
                for (Attachment existingAttachment : viewModel.getFullCard().getAttachments()) {
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
                viewModel.getFullCard().getAttachments().add(0, a);
                adapter.addAttachment(a);
                if (!viewModel.isCreateMode()) {
                    WrappedLiveData<Attachment> liveData = syncManager.addAttachmentToCard(viewModel.getAccount().getId(), viewModel.getFullCard().getLocalId(), a.getMimetype(), fileToUpload);
                    observeOnce(liveData, getViewLifecycleOwner(), (next) -> {
                        if (liveData.hasError()) {
                            Throwable t = liveData.getError();
                            if (t instanceof NextcloudHttpRequestFailedException && ((NextcloudHttpRequestFailedException) t).getStatusCode() == HTTP_CONFLICT) {
                                // https://github.com/stefan-niedermann/nextcloud-deck/issues/534
                                viewModel.getFullCard().getAttachments().remove(a);
                                adapter.removeAttachment(a);
                                BrandedSnackbar.make(binding.coordinatorLayout, R.string.attachment_already_exists, Snackbar.LENGTH_LONG).show();
                            } else {
                                ExceptionDialogFragment.newInstance(new UploadAttachmentFailedException("Unknown URI scheme", t), viewModel.getAccount()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                            }
                        } else {
                            viewModel.getFullCard().getAttachments().remove(a);
                            viewModel.getFullCard().getAttachments().add(0, next);
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
            case REQUEST_CODE_ADD_FILE_PERMISSION: {
                if (checkSelfPermission(requireActivity(), READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
                    pickFile();
                } else {
                    Toast.makeText(requireContext(), R.string.cannot_upload_files_without_permission, Toast.LENGTH_LONG).show();
                }
                break;
            }
            case REQUEST_CODE_CAMERA_PERMISSION: {
                if (checkSelfPermission(requireActivity(), CAMERA) == PERMISSION_GRANTED) {
                    if (SDK_INT >= LOLLIPOP) {
                        pickCamera();
                    } else {
                        Toast.makeText(requireContext(), R.string.min_api_21, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), R.string.cannot_upload_files_without_permission, Toast.LENGTH_LONG).show();
                }
                break;
            }
            case REQUEST_CODE_PICK_CONTACT_PERMISSION: {
                if (checkSelfPermission(requireActivity(), READ_CONTACTS) == PERMISSION_GRANTED) {
                    pickContact();
                } else {
                    Toast.makeText(requireContext(), R.string.cannot_upload_files_without_permission, Toast.LENGTH_LONG).show();
                }
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void hidePicker() {
        binding.bottomNavigation
                .animate()
                .translationY(binding.bottomNavigation.getHeight())
                .setDuration(300)
                .start();
        binding.bottomNavigation.setVisibility(GONE);
    }

    private void showPicker() {
        binding.bottomNavigation.setVisibility(VISIBLE);
        binding.bottomNavigation
                .animate()
                .translationY(0)
                .setDuration(300)
                .start();
    }

    @Override
    public void onAttachmentDeleted(Attachment attachment) {
        adapter.removeAttachment(attachment);
        viewModel.getFullCard().getAttachments().remove(attachment);
        if (!viewModel.isCreateMode() && attachment.getLocalId() != null) {
            final WrappedLiveData<Void> deleteLiveData = syncManager.deleteAttachmentOfCard(viewModel.getAccount().getId(), viewModel.getFullCard().getLocalId(), attachment.getLocalId());
            observeOnce(deleteLiveData, this, (next) -> {
                if (deleteLiveData.hasError() && !SyncManager.ignoreExceptionOnVoidError(deleteLiveData.getError())) {
                    ExceptionDialogFragment.newInstance(deleteLiveData.getError(), viewModel.getAccount()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
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
        if (SDK_INT >= LOLLIPOP) {
            final ColorStateList backgroundColorStateList = ColorStateList.valueOf(mainColor);
            final ColorStateList foregroundColorStateList = ColorStateList.valueOf(
                    DeckColorUtil.contrastRatioIsSufficient(mainColor, Color.WHITE)
                            ? Color.WHITE
                            : Color.BLACK
            );
            for (ImageView v : brandedViews) {
                v.setBackgroundTintList(backgroundColorStateList);
                v.setImageTintList(foregroundColorStateList);
            }
        }
    }

    public static Fragment newInstance() {
        return new CardAttachmentsFragment();
    }
}
