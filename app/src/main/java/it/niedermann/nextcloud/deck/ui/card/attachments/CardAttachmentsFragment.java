package it.niedermann.nextcloud.deck.ui.card.attachments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.SharedElementCallback;
import androidx.core.content.FileProvider;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import it.niedermann.nextcloud.deck.BuildConfig;
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

import static android.app.Activity.RESULT_OK;
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
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 3;
    private static final int REQUEST_CODE_PICK_CONTACT = 4;
    private static final int REQUEST_CODE_PICK_CONTACT_PERMISSION = 5;

    private SyncManager syncManager;
    private CardAttachmentAdapter adapter;

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
                requireContext(),
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
            binding.fab.setOnClickListener(v -> CardAttachmentPicker.newInstance().show(getChildFragmentManager(), CardAttachmentPicker.class.getSimpleName()));
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
    public void pickCamera() {
        final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            Long localId = viewModel.getFullCard().getLocalId();
            String imageFileName = "JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + "_";
            File tempFile = new File(requireContext().getApplicationContext().getFilesDir().getAbsolutePath() + "/attachments/account-" + viewModel.getFullCard().getAccountId() + "/card-" + (localId == null ? "pending-creation" : localId) + '/' + imageFileName);

            Uri photoURI = FileProvider.getUriForFile(requireContext(),
                    BuildConfig.APPLICATION_ID + ".fileprovider",
                    tempFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_CODE_ADD_FILE);
        }
    }

    @Override
    public void pickContact() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(requireActivity(), Manifest.permission.READ_CONTACTS) != PermissionChecker.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_PICK_CONTACT_PERMISSION);
        } else {
            final Intent intent = new Intent(Intent.ACTION_PICK)
                    .setType(ContactsContract.Contacts.CONTENT_TYPE);
            if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_CODE_PICK_CONTACT);
            }
        }
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void pickFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(requireActivity(), Manifest.permission.READ_CONTACTS) != PermissionChecker.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_ADD_FILE_PERMISSION);
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                    .addCategory(Intent.CATEGORY_OPENABLE)
                    .setType("*/*");
            startActivityForResult(intent, REQUEST_CODE_ADD_FILE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_PICK_CONTACT: {
//                Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
                Uri uri = (Uri) data.getData();

                ContentResolver cr = requireContext().getContentResolver();
                // Get the Cursor of all the contacts
                Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

                // Move the cursor to first. Also check whether the cursor is empty or not.
                if (cursor.moveToFirst()) {
                    // Iterate through the cursor
                    // Get the contacts name
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    Log.e("col: ", name);



//
//                    AssetFileDescriptor fd;
//                    try
//                    {
//                        fd = requireContext().getContentResolver().openAssetFileDescriptor(uri, "r");
//                        FileInputStream fis = fd.createInputStream();
//                        byte[] buf = new byte[(int) fd.getDeclaredLength()];
//                        fis.read(buf);
//                        String VCard = new String(buf);
//                        String path = Environment.getExternalStorageDirectory().toString() + File.separator + "POContactsRestore.vcf";
//                        FileOutputStream mFileOutputStream = new FileOutputStream(path, true);
//                        mFileOutputStream.write(VCard.toString().getBytes());
//                        Log.d("Vcard",  VCard);
//                    }
//                    catch (Exception e1)
//                    {
//                        // TODO Auto-generated catch block
//                        e1.printStackTrace();
//                    }
                }
                // Close the curosor
                cursor.close();
                break;
            }
            case REQUEST_CODE_CAPTURE_IMAGE:
            case REQUEST_CODE_ADD_FILE: {
                if (resultCode == RESULT_OK) {
                    if (data == null) {
                        ExceptionDialogFragment.newInstance(new UploadAttachmentFailedException("Intent data is null"), viewModel.getAccount()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                        return;
                    }
                    final Uri sourceUri = data.getData();
                    if (sourceUri == null) {
                        ExceptionDialogFragment.newInstance(new UploadAttachmentFailedException("sourceUri is null"), viewModel.getAccount()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                        return;
                    }
                    if (!ContentResolver.SCHEME_CONTENT.equals(sourceUri.getScheme())) {
                        ExceptionDialogFragment.newInstance(new UploadAttachmentFailedException("Unknown URI scheme: " + sourceUri.getScheme()), viewModel.getAccount()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                        return;
                    }

                    DeckLog.verbose("--- found content URL " + sourceUri.getPath());
                    File fileToUpload;

                    try {
                        DeckLog.verbose("---- so, now copy & upload: " + sourceUri.getPath());
                        fileToUpload = copyContentUriToTempFile(requireContext(), sourceUri, viewModel.getAccount().getId(), viewModel.getFullCard().getCard().getLocalId());
                    } catch (IllegalArgumentException | IOException e) {
                        ExceptionDialogFragment.newInstance(new UploadAttachmentFailedException("Could not copy content URI to temporary file", e), viewModel.getAccount()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                        return;
                    }

                    for (Attachment existingAttachment : viewModel.getFullCard().getAttachments()) {
                        final String existingPath = existingAttachment.getLocalPath();
                        if (existingPath != null && existingPath.equals(fileToUpload.getAbsolutePath())) {
                            BrandedSnackbar.make(binding.coordinatorLayout, R.string.attachment_already_exists, Snackbar.LENGTH_LONG).show();
                            return;
                        }
                    }

                    final Date now = new Date();
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
                }
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ADD_FILE_PERMISSION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    pickFile();
                }
                break;
            case REQUEST_CODE_PICK_CONTACT_PERMISSION:
                pickContact();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onAttachmentDeleted(Attachment attachment) {
        adapter.removeAttachment(attachment);
        viewModel.getFullCard().getAttachments().remove(attachment);
        if (!viewModel.isCreateMode() && attachment.getLocalId() != null) {
            syncManager.deleteAttachmentOfCard(viewModel.getAccount().getId(), viewModel.getFullCard().getLocalId(), attachment.getLocalId());
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
    }

    public static Fragment newInstance() {
        return new CardAttachmentsFragment();
    }
}
