package it.niedermann.nextcloud.deck.ui.card;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;
import java.util.Map;

import it.niedermann.nextcloud.deck.databinding.FragmentCardEditTabAttachmentsBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.util.FileUtils;

import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_BOARD_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_CAN_EDIT;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAttachmentAdapter.AttachmentClickedListener;
import static it.niedermann.nextcloud.deck.ui.card.CardAttachmentAdapter.AttachmentDeletedListener;
import static it.niedermann.nextcloud.deck.ui.card.CardAttachmentAdapter.VIEW_TYPE_DEFAULT;
import static it.niedermann.nextcloud.deck.ui.card.CardAttachmentAdapter.VIEW_TYPE_IMAGE;

public class CardAttachmentsFragment extends Fragment implements AttachmentDeletedListener, AttachmentClickedListener {
    private static final String TAG = CardAttachmentsFragment.class.getCanonicalName();

    private FragmentCardEditTabAttachmentsBinding binding;

    private static final int REQUEST_CODE_ADD_ATTACHMENT = 1;
    private static final int REQUEST_PERMISSION = 2;

    private SyncManager syncManager;

    private long accountId;
    private long cardId;

    private int clickedItemPosition;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCardEditTabAttachmentsBinding.inflate(inflater, container, false);

        Bundle args = getArguments();
        if (args != null) {
            accountId = args.getLong(BUNDLE_KEY_ACCOUNT_ID);
            cardId = args.getLong(BUNDLE_KEY_LOCAL_ID);
            boolean canEdit = args.getBoolean(BUNDLE_KEY_CAN_EDIT);

            syncManager = new SyncManager(requireActivity());
            syncManager.getCardByLocalId(accountId, cardId).observe(getViewLifecycleOwner(), (fullCard) -> {
                if (fullCard.getAttachments().size() == 0) {
                    this.binding.emptyContentView.setVisibility(View.VISIBLE);
                    this.binding.attachmentsList.setVisibility(View.GONE);
                } else {
                    this.binding.emptyContentView.setVisibility(View.GONE);
                    this.binding.attachmentsList.setVisibility(View.VISIBLE);
                    syncManager.readAccount(accountId).observe(getViewLifecycleOwner(), (Account account) -> {
                        RecyclerView.Adapter adapter = new CardAttachmentAdapter(
                                requireActivity().getMenuInflater(),
                                this,
                                this,
                                account,
                                fullCard.getCard().getLocalId(),
                                fullCard.getCard().getId(),
                                fullCard.getAttachments());
                        binding.attachmentsList.setAdapter(adapter);

                        // https://android-developers.googleblog.com/2018/02/continuous-shared-element-transitions.html?m=1
                        // https://github.com/android/animation-samples/blob/master/GridToPager/app/src/main/java/com/google/samples/gridtopager/fragment/ImagePagerFragment.java
                        setExitSharedElementCallback(new SharedElementCallback() {
                            @Override
                            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                                CardAttachmentAdapter.AttachmentViewHolder selectedViewHolder = (CardAttachmentAdapter.AttachmentViewHolder) binding.attachmentsList
                                        .findViewHolderForAdapterPosition(clickedItemPosition);
                                if (selectedViewHolder != null) {
                                    sharedElements.put(names.get(0), selectedViewHolder.getPreview());
                                }
                            }
                        });

                        GridLayoutManager glm = new GridLayoutManager(getActivity(), 3);

                        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                            @Override
                            public int getSpanSize(int position) {
                                switch (adapter.getItemViewType(position)) {
                                    case VIEW_TYPE_IMAGE:
                                        return 1;
                                    case VIEW_TYPE_DEFAULT:
                                        return 3;
                                    default:
                                        return 1;
                                }
                            }
                        });
                        binding.attachmentsList.setLayoutManager(glm);
                    });
                }
            });

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && canEdit) {
                binding.fab.setOnClickListener(v -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_PERMISSION);
                    } else {
                        startFilePickerIntent();
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
        }


        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void startFilePickerIntent() {
//                    Snackbar.make(coordinatorLayout, "Adding attachments is not yet implemented", Snackbar.LENGTH_LONG).show();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_CODE_ADD_ATTACHMENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_ATTACHMENT && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                Log.i(TAG, "Uri: " + uri.toString());
                String path = FileUtils.getPath(getContext(), uri);
                File uploadFile = new File(path);
                syncManager.addAttachmentToCard(accountId, cardId, Attachment.getMimetypeForUri(getContext(), uri), uploadFile);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                startFilePickerIntent();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public CardAttachmentsFragment() {
    }

    public static CardAttachmentsFragment newInstance(long accountId, long localId, long boardId, boolean canEdit) {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY_ACCOUNT_ID, accountId);
        bundle.putLong(BUNDLE_KEY_BOARD_ID, boardId);
        bundle.putLong(BUNDLE_KEY_LOCAL_ID, localId);
        bundle.putBoolean(BUNDLE_KEY_CAN_EDIT, canEdit);

        CardAttachmentsFragment fragment = new CardAttachmentsFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onAttachmentDeleted(Attachment attachment) {
        syncManager.deleteAttachmentOfCard(accountId, cardId, attachment.getLocalId());
    }

    @Override
    public void onAttachmentClicked(int position) {
        this.clickedItemPosition = position;
    }
}
