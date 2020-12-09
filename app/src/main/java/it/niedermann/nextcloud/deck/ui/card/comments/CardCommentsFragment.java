package it.niedermann.nextcloud.deck.ui.card.comments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.time.Instant;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.FragmentCardEditTabCommentsBinding;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.model.ocs.comment.full.FullDeckComment;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.WrappedLiveData;
import it.niedermann.nextcloud.deck.ui.branding.BrandedFragment;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;
import it.niedermann.nextcloud.deck.ui.card.EditCardViewModel;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.applyBrandToEditText;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.applyBrandToFAB;
import static it.niedermann.nextcloud.deck.util.ViewUtil.setupMentions;

public class CardCommentsFragment extends BrandedFragment implements CommentEditedListener, CommentDeletedListener, CommentSelectAsReplyListener {

    private FragmentCardEditTabCommentsBinding binding;
    private EditCardViewModel mainViewModel;
    private CommentsViewModel commentsViewModel;
    private CardCommentsAdapter adapter;

    public static Fragment newInstance() {
        return new CardCommentsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCardEditTabCommentsBinding.inflate(inflater, container, false);

        mainViewModel = new ViewModelProvider(requireActivity()).get(EditCardViewModel.class);

        // This might be a zombie fragment with an empty EditCardViewModel after Android killed the activity (but not the fragment instance
        // See https://github.com/stefan-niedermann/nextcloud-deck/issues/478
        if (mainViewModel.getFullCard() == null) {
            DeckLog.logError(new IllegalStateException("Cannot populate " + CardCommentsFragment.class.getSimpleName() + " because viewModel.getFullCard() is null"));
            if (requireActivity() instanceof EditActivity) {
                Toast.makeText(getContext(), R.string.error_edit_activity_killed_by_android, Toast.LENGTH_LONG).show();
                ((EditActivity) requireActivity()).directFinish();
            } else {
                requireActivity().finish();
            }
            return binding.getRoot();
        }

        commentsViewModel = new ViewModelProvider(this).get(CommentsViewModel.class);

        adapter = new CardCommentsAdapter(requireContext(), mainViewModel.getAccount(), requireActivity().getMenuInflater(), this, this, getChildFragmentManager());
        binding.comments.setAdapter(adapter);

        binding.replyCommentCancelButton.setOnClickListener((v) -> commentsViewModel.setReplyToComment(null));
        commentsViewModel.getReplyToComment().observe(getViewLifecycleOwner(), (comment) -> {
            if (comment == null) {
                binding.replyComment.setVisibility(GONE);
            } else {
                binding.replyCommentText.setText(comment.getComment().getMessage());
                binding.replyComment.setVisibility(VISIBLE);
                setupMentions(mainViewModel.getAccount(), comment.getComment().getMentions(), binding.replyCommentText);
            }
        });
        commentsViewModel.getFullCommentsForLocalCardId(mainViewModel.getFullCard().getLocalId()).observe(getViewLifecycleOwner(),
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
        if (mainViewModel.canEdit()) {
            binding.addCommentLayout.setVisibility(VISIBLE);
            binding.fab.setOnClickListener(v -> {
                // Do not handle empty comments (https://github.com/stefan-niedermann/nextcloud-deck/issues/440)
                if (!TextUtils.isEmpty(binding.message.getText().toString().trim())) {
                    binding.emptyContentView.setVisibility(GONE);
                    binding.comments.setVisibility(VISIBLE);
                    final DeckComment comment = new DeckComment(binding.message.getText().toString().trim(), mainViewModel.getAccount().getUserName(), Instant.now());
                    final FullDeckComment parent = commentsViewModel.getReplyToComment().getValue();
                    if (parent != null) {
                        comment.setParentId(parent.getId());
                        commentsViewModel.setReplyToComment(null);
                    }
                    commentsViewModel.addCommentToCard(mainViewModel.getAccount().getId(), mainViewModel.getFullCard().getLocalId(), comment);
                }
                binding.message.setText(null);
            });
            binding.message.setOnEditorActionListener((v, actionId, event) -> {
                if ((actionId == EditorInfo.IME_ACTION_SEND) || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)) {
                    return binding.fab.performClick();
                }
                return true;
            });
            binding.message.addTextChangedListener(new CardCommentsMentionProposer(getViewLifecycleOwner(), mainViewModel.getAccount(), mainViewModel.getBoardId(), binding.message, binding.mentionProposerWrapper, binding.mentionProposer));
        } else {
            binding.addCommentLayout.setVisibility(GONE);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mainViewModel.canEdit()) {
            binding.message.requestFocus();
            requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onCommentEdited(Long id, String comment) {
        commentsViewModel.updateComment(mainViewModel.getAccount().getId(), mainViewModel.getFullCard().getLocalId(), id, comment);
    }

    @Override
    public void onCommentDeleted(Long localId) {
        final WrappedLiveData<Void> deleteLiveData = commentsViewModel.deleteComment(mainViewModel.getAccount().getId(), mainViewModel.getFullCard().getLocalId(), localId);
        observeOnce(deleteLiveData, this, (next) -> {
            if (deleteLiveData.hasError() && !SyncManager.ignoreExceptionOnVoidError(deleteLiveData.getError())) {
                ExceptionDialogFragment.newInstance(deleteLiveData.getError(), mainViewModel.getAccount()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
            }
        });
    }

    @Override
    public void applyBrand(int mainColor) {
        applyBrandToEditText(mainColor, binding.message);
        applyBrandToFAB(mainColor, binding.fab);
    }

    @Override
    public void onSelectAsReply(FullDeckComment comment) {
        commentsViewModel.setReplyToComment(comment);
    }
}
