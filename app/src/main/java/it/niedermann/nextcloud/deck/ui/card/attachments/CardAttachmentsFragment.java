package it.niedermann.nextcloud.deck.ui.card.attachments;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import it.niedermann.nextcloud.deck.ui.card.attachments.picker.CardAttachmentPicker;
import it.niedermann.nextcloud.deck.ui.card.attachments.picker.CardAttachmentPickerListener;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.deck.ui.takephoto.TakePhotoActivity;
import it.niedermann.nextcloud.deck.util.VCardUtil;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.os.Build.VERSION_CODES.M;
import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;
import static androidx.core.content.PermissionChecker.checkSelfPermission;
import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.applyBrandToFAB;
import static it.niedermann.nextcloud.deck.ui.card.attachments.CardAttachmentAdapter.VIEW_TYPE_DEFAULT;
import static it.niedermann.nextcloud.deck.ui.card.attachments.CardAttachmentAdapter.VIEW_TYPE_IMAGE;
import static it.niedermann.nextcloud.deck.util.AttachmentUtil.copyContentUriToTempFile;
import static java.net.HttpURLConnection.HTTP_CONFLICT;

public class CardAttachmentsFragment extends BrandedFragment implements AttachmentDeletedListener, AttachmentClickedListener, CardAttachmentPickerListener {

    private FragmentCardEditTabAttachmentsBinding binding;
    private EditCardViewModel viewModel;

    private static final int REQUEST_CODE_ADD_FILE = 1;
    private static final int REQUEST_CODE_ADD_FILE_PERMISSION = 2;
    private static final int REQUEST_CODE_CAMERA = 3;
    private static final int REQUEST_CODE_CAMERA_PERMISSION = 4;
    private static final int REQUEST_CODE_PICK_CONTACT = 5;
    private static final int REQUEST_CODE_PICK_CONTACT_PERMISSION = 6;

    private SyncManager syncManager;
    private CardAttachmentAdapter adapter;
    @Nullable
    private DialogFragment picker;

    private int clickedItemPosition;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCardEditTabAttachmentsBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(EditCardViewModel.class);

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

        updateEmptyContentView();

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
            updateEmptyContentView();
        }

        if (viewModel.canEdit()) {
            binding.fab.setOnClickListener(v -> {
                picker = CardAttachmentPicker.newInstance();
                picker.show(getChildFragmentManager(), CardAttachmentPicker.class.getSimpleName());
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
        return binding.getRoot();
    }

    @Override
    @RequiresApi(LOLLIPOP)
    public void pickCamera() {
        if (isPermissionRequestNeeded(CAMERA)) {
            requestPermissions(new String[]{CAMERA}, REQUEST_CODE_CAMERA_PERMISSION);
        } else {
            startActivityForResult(TakePhotoActivity.createIntent(requireContext()), REQUEST_CODE_CAMERA);
        }
    }

    @Override
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

    @Override
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
                        uploadNewAttachmentFromUri(sourceUri);
                        if (picker != null) {
                            picker.dismiss();
                        }
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

    private void uploadNewAttachmentFromUri(@NonNull Uri sourceUri) throws UploadAttachmentFailedException, IOException {
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
                a.setMimetype(requireContext().getContentResolver().getType(sourceUri));
                a.setData(fileToUpload.getName());
                a.setFilename(fileToUpload.getName());
                a.setBasename(fileToUpload.getName());
                a.setFilesize(fileToUpload.length());
                a.setLocalPath(fileToUpload.getAbsolutePath());
                a.setLastModifiedLocal(now);
                a.setStatusEnum(DBStatus.LOCAL_EDITED);
                a.setCreatedAt(now);
                viewModel.getFullCard().getAttachments().add(a);
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
                            adapter.removeAttachment(a);
                            viewModel.getFullCard().getAttachments().add(next);
                            adapter.addAttachment(next);
                        }
                    });
                }
                updateEmptyContentView();
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
        updateEmptyContentView();
    }

    @Override
    public void onAttachmentClicked(int position) {
        this.clickedItemPosition = position;
    }

    private void updateEmptyContentView() {
        if (this.adapter == null || this.adapter.getItemCount() == 0) {
            this.binding.emptyContentView.setVisibility(View.VISIBLE);
            this.binding.attachmentsList.setVisibility(View.GONE);
        } else {
            this.binding.emptyContentView.setVisibility(View.GONE);
            this.binding.attachmentsList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void applyBrand(int mainColor) {
        applyBrandToFAB(mainColor, binding.fab);
        adapter.applyBrand(mainColor);
    }

    public static Fragment newInstance() {
        return new CardAttachmentsFragment();
    }
}
