package it.niedermann.nextcloud.deck.ui.card.attachments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.view.ActionMode;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;
import java.util.Map;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.FragmentCardEditTabAttachmentsBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.EditActivity;
import it.niedermann.nextcloud.deck.util.FileUtils;

import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_BOARD_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_CAN_EDIT;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.NO_LOCAL_ID;
import static it.niedermann.nextcloud.deck.ui.card.attachments.CardAttachmentAdapter.VIEW_TYPE_DEFAULT;
import static it.niedermann.nextcloud.deck.ui.card.attachments.CardAttachmentAdapter.VIEW_TYPE_IMAGE;

public class CardAttachmentsFragment extends Fragment implements AttachmentDeletedListener, AttachmentClickedListener {
    private static final String TAG = CardAttachmentsFragment.class.getCanonicalName();

    private FragmentCardEditTabAttachmentsBinding binding;
    private SelectionTracker<Long> selectionTracker;

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
            if (cardId == NO_LOCAL_ID) {
                this.binding.saveCardBeforeAddAttachments.setVisibility(View.VISIBLE);
                this.binding.attachmentsList.setVisibility(View.GONE);
                this.binding.emptyContentView.setVisibility(View.GONE);
                this.binding.fab.setVisibility(View.GONE);
            } else {
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
                            selectionTracker = new SelectionTracker.Builder<>(
                                    "my-selection-id",
                                    binding.attachmentsList,
                                    new CardAttachmentKeyProvider(1, fullCard.getAttachments()),
                                    new CardAttachmentLookup(binding.attachmentsList),
                                    StorageStrategy.createLongStorage()
                            ).build();
                            if (savedInstanceState != null) {
                                selectionTracker.onRestoreInstanceState(savedInstanceState);
                            }
                            ((CardAttachmentAdapter) adapter).setSelectionTracker(selectionTracker);
                            if (getActivity() instanceof EditActivity) {
                                selectionTracker.addObserver(new SelectionTracker.SelectionObserver() {
                                    @Override
                                    public void onSelectionChanged() {
                                        super.onSelectionChanged();
                                        ActionMode actionMode = ((EditActivity) requireActivity()).getActionMode();
                                        if (selectionTracker.hasSelection() && actionMode == null) {
                                            ((EditActivity) requireActivity()).startSupportActionMode(new ActionModeController(requireContext(), selectionTracker));
                                        } else if (!selectionTracker.hasSelection() && actionMode != null) {
                                            actionMode.finish();
                                            ((EditActivity) requireActivity()).setActionMode(null);
                                        } else {
//                                            requireActivity().setMenuItemTitle(selectionTracker.getSelection().size());
                                        }
                                        for (Long aLong : selectionTracker.getSelection()) {
                                            Log.i(TAG, String.valueOf(aLong));
                                        }
                                    }
                                });
                            }

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

                            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                            int spanCount = (int) ((displayMetrics.widthPixels / displayMetrics.density) / getResources().getInteger(R.integer.max_dp_attachment_column));
                            GridLayoutManager glm = new GridLayoutManager(getActivity(), spanCount);

                            glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                @Override
                                public int getSpanSize(int position) {
                                    switch (adapter.getItemViewType(position)) {
                                        case VIEW_TYPE_IMAGE:
                                            return 1;
                                        case VIEW_TYPE_DEFAULT:
                                            return spanCount;
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
        }


        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void startFilePickerIntent() {
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
                if (uri != null) {
                    DeckLog.info("Uri: " + uri.toString());
                    String path = FileUtils.getPath(getContext(), uri);
                    if (path != null) {
                        File uploadFile = new File(path);
                        if (cardId == NO_LOCAL_ID) {
                            if (getActivity() instanceof AttachmentAddedToNewCardListener) {
                                Toast.makeText(getContext(), "You need to save the card first.", Toast.LENGTH_LONG).show();
//                                Attachment attachment = new Attachment();
//                                ((AttachmentAddedToNewCardListener) getActivity()).attachmentAddedToNewCard(attachment);
                            }
                        } else {
                            syncManager.addAttachmentToCard(accountId, cardId, Attachment.getMimetypeForUri(getContext(), uri), uploadFile);
                        }
                    } else {
                        DeckLog.warn("path to file is null");
                    }
                } else {
                    DeckLog.warn("data.getDate() returned null");
                }
            } else {
                DeckLog.warn("data is null");
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

    public interface AttachmentAddedToNewCardListener {
        void attachmentAddedToNewCard(Attachment attachment);
    }






    public static class ActionModeController implements ActionMode.Callback {

        private final Context context;
        private final SelectionTracker selectionTracker;

        ActionModeController(Context context, SelectionTracker selectionTracker) {
            this.context = context;
            this.selectionTracker = selectionTracker;
        }

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            selectionTracker.clearSelection();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        selectionTracker.onSaveInstanceState(outState);
    }
}
