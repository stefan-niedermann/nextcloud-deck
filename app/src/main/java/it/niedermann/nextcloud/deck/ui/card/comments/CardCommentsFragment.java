package it.niedermann.nextcloud.deck.ui.card.comments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.time.Instant;

import it.niedermann.android.util.DimensionUtil;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.FragmentCardEditTabCommentsBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.model.ocs.comment.full.FullDeckComment;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.repository.SyncRepository;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;
import it.niedermann.nextcloud.deck.ui.card.EditCardViewModel;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.ui.viewmodel.SyncViewModel;
import it.niedermann.nextcloud.deck.util.KeyboardUtils;

public class CardCommentsFragment extends Fragment implements CommentEditedListener, CommentDeletedListener, CommentSelectAsReplyListener {

    private static final String KEY_ACCOUNT = "account";
    private FragmentCardEditTabCommentsBinding binding;
    private EditCardViewModel editCardViewModel;
    private CommentsViewModel commentsViewModel;
    private CardCommentsAdapter adapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        final var args = getArguments();

        if (args == null || !args.containsKey(KEY_ACCOUNT)) {
            throw new IllegalArgumentException(KEY_ACCOUNT + " must be provided.");
        }

        final var account = (Account) args.getSerializable(KEY_ACCOUNT);

        if (account == null) {
            throw new IllegalArgumentException(KEY_ACCOUNT + " must not be null.");
        }

        commentsViewModel = new SyncViewModel.Provider(this, requireActivity().getApplication(), account).get(CommentsViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCardEditTabCommentsBinding.inflate(inflater, container, false);

        // This might be a zombie fragment with an empty EditCardViewModel after Android killed the activity (but not the fragment instance
        // See https://github.com/stefan-niedermann/nextcloud-deck/issues/478
        if (editCardViewModel.getFullCard() == null) {
            DeckLog.logError(new IllegalStateException("Cannot populate " + CardCommentsFragment.class.getSimpleName() + " because viewModel.getFullCard() is null"));
            if (requireActivity() instanceof EditActivity) {
                Toast.makeText(getContext(), R.string.error_edit_activity_killed_by_android, Toast.LENGTH_LONG).show();
                ((EditActivity) requireActivity()).directFinish();
            } else {
                requireActivity().finish();
            }
            return binding.getRoot();
        }


        adapter = new CardCommentsAdapter(requireContext(), editCardViewModel.getAccount(), requireActivity().getMenuInflater(), this, this, getChildFragmentManager(), this);
        binding.comments.setAdapter(adapter);
        binding.replyCommentCancelButton.setOnClickListener((v) -> commentsViewModel.setReplyToComment(null));
        Glide.with(binding.avatar.getContext())
                .load(editCardViewModel.getAccount().getAvatarUrl(DimensionUtil.INSTANCE.dpToPx(binding.avatar.getContext(), R.dimen.icon_size_details)))
                .placeholder(R.drawable.ic_person_grey600_24dp)
                .error(R.drawable.ic_person_grey600_24dp)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.avatar);

        commentsViewModel.getReplyToComment().observe(getViewLifecycleOwner(), (comment) -> {
            if (comment == null) {
                binding.replyComment.setVisibility(GONE);
            } else {
                binding.replyCommentText.setMarkdownString(comment.getComment().getMessage());
                binding.replyComment.setVisibility(VISIBLE);
            }
        });
        commentsViewModel.getFullCommentsForLocalCardId(editCardViewModel.getFullCard().getLocalId()).observe(getViewLifecycleOwner(),
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
        if (editCardViewModel.canEdit()) {
            binding.addCommentLayout.setVisibility(VISIBLE);
            binding.fab.setOnClickListener(v -> {
                // Do not handle empty comments (https://github.com/stefan-niedermann/nextcloud-deck/issues/440)
                if (!TextUtils.isEmpty(binding.message.getText().toString().trim())) {
                    binding.emptyContentView.setVisibility(GONE);
                    binding.comments.setVisibility(VISIBLE);
                    final DeckComment comment = new DeckComment(binding.message.getText().toString().trim(), editCardViewModel.getAccount().getUserName(), Instant.now());
                    final FullDeckComment parent = commentsViewModel.getReplyToComment().getValue();
                    if (parent != null) {
                        comment.setParentId(parent.getId());
                        commentsViewModel.setReplyToComment(null);
                    }
                    commentsViewModel.addCommentToCard(editCardViewModel.getAccount().getId(), editCardViewModel.getFullCard().getLocalId(), comment);
                }
                binding.message.setText(null);
            });
            binding.message.setOnEditorActionListener((v, actionId, event) -> {
                if ((actionId == EditorInfo.IME_ACTION_SEND) || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)) {
                    return binding.fab.performClick();
                }
                return true;
            });
            binding.message.addTextChangedListener(new CardCommentsMentionProposer(getViewLifecycleOwner(), editCardViewModel.getAccount(), editCardViewModel.getBoardId(), binding.message, binding.mentionProposerWrapper, binding.mentionProposer));
        } else {
            binding.addCommentLayout.setVisibility(GONE);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (editCardViewModel.canEdit()) {
            KeyboardUtils.showKeyboardForEditText(binding.message);
        }
        editCardViewModel.getBoardColor().observe(getViewLifecycleOwner(), this::applyTheme);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    @Override
    public void onCommentEdited(Long id, String comment) {
        commentsViewModel.updateComment(editCardViewModel.getAccount().getId(), editCardViewModel.getFullCard().getLocalId(), id, comment);
    }

    @Override
    public void onCommentDeleted(Long localId) {
        commentsViewModel.deleteComment(editCardViewModel.getAccount().getId(), editCardViewModel.getFullCard().getLocalId(), localId, new IResponseCallback<>() {
            @Override
            public void onResponse(Void response) {
                DeckLog.info("Successfully deleted comment with localId", localId);
            }

            @Override
            public void onError(Throwable throwable) {
                if (SyncRepository.isNoOnVoidError(throwable)) {
                    IResponseCallback.super.onError(throwable);
                    requireActivity().runOnUiThread(() -> ExceptionDialogFragment.newInstance(throwable, editCardViewModel.getAccount()).show(getChildFragmentManager(), ExceptionDialogFragment.class.getSimpleName()));
                }
            }
        });
    }

    private void applyTheme(int color) {
        final var utils = ThemeUtils.of(color, requireContext());

        utils.material.themeFAB(binding.fab);
        utils.material.colorTextInputLayout(binding.messageWrapper);
    }

    @Override
    public void onSelectAsReply(FullDeckComment comment) {
        commentsViewModel.setReplyToComment(comment);
    }

    public static Fragment newInstance(@NonNull Account account) {
        final var fragment = new CardCommentsFragment();

        final var args = new Bundle();
        args.putSerializable(KEY_ACCOUNT, account);
        fragment.setArguments(args);

        return fragment;
    }
}
