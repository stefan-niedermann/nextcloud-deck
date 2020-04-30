package it.niedermann.nextcloud.deck.ui.card.comments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Date;

import it.niedermann.nextcloud.deck.databinding.FragmentCardEditTabCommentsBinding;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.branding.BrandedFragment;
import it.niedermann.nextcloud.deck.ui.card.EditCardViewModel;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static it.niedermann.nextcloud.deck.ui.branding.BrandedActivity.applyBrandToEditText;
import static it.niedermann.nextcloud.deck.ui.branding.BrandedActivity.applyBrandToFAB;

public class CardCommentsFragment extends BrandedFragment implements CommentEditedListener, CommentDeletedListener {

    private FragmentCardEditTabCommentsBinding binding;
    private EditCardViewModel viewModel;
    private SyncManager syncManager;
    private CardCommentsAdapter adapter;

    public static Fragment newInstance() {
        return new CardCommentsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCardEditTabCommentsBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(EditCardViewModel.class);
        syncManager = new SyncManager(requireActivity());
        adapter = new CardCommentsAdapter(requireContext(), viewModel.getAccount(), requireActivity().getMenuInflater(), this, getChildFragmentManager());
        binding.comments.setAdapter(adapter);
        syncManager.getCommentsForLocalCardId(viewModel.getFullCard().getLocalId()).observe(getViewLifecycleOwner(),
                (comments) -> {
                    if (comments != null && comments.size() > 0) {
                        binding.emptyContentView.setVisibility(GONE);
                        binding.comments.setVisibility(VISIBLE);
                        adapter.updateComments(comments);
                    } else {
                        binding.emptyContentView.setVisibility(VISIBLE);
                        binding.comments.setVisibility(GONE);
                    }
                });
        if (viewModel.canEdit()) {
            binding.addCommentLayout.setVisibility(VISIBLE);
            binding.fab.setOnClickListener(v -> {
                // Do not handle empty comments (https://github.com/stefan-niedermann/nextcloud-deck/issues/440)
                if (!TextUtils.isEmpty(binding.message.getText().toString().trim())) {
                    binding.emptyContentView.setVisibility(GONE);
                    binding.comments.setVisibility(VISIBLE);
                    final DeckComment comment = new DeckComment(binding.message.getText().toString().trim());
                    comment.setActorDisplayName(viewModel.getAccount().getUserName());
                    comment.setCreationDateTime(new Date());
                    syncManager.addCommentToCard(viewModel.getAccount().getId(), viewModel.getFullCard().getLocalId(), comment);
                }
                binding.message.setText(null);
            });
            binding.message.setOnEditorActionListener((v, actionId, event) -> {
                if ((actionId == EditorInfo.IME_ACTION_SEND) || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)) {
                    return binding.fab.performClick();
                }
                return true;
            });
        } else {
            binding.addCommentLayout.setVisibility(GONE);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (viewModel.canEdit()) {
            binding.message.requestFocus();
            requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onCommentEdited(Long id, String comment) {
        syncManager.updateComment(viewModel.getAccount().getId(), viewModel.getFullCard().getLocalId(), id, comment);
    }

    @Override
    public void onCommentDeleted(Long localId) {
        syncManager.deleteComment(viewModel.getAccount().getId(), viewModel.getFullCard().getLocalId(), localId);
    }

    @Override
    public void applyBrand(int mainColor, int textColor) {
        applyBrandToEditText(mainColor, textColor, binding.message);
        applyBrandToFAB(mainColor, textColor, binding.fab);
    }
}
