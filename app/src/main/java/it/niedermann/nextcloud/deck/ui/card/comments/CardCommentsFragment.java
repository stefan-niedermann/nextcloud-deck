package it.niedermann.nextcloud.deck.ui.card.comments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Date;

import it.niedermann.nextcloud.deck.databinding.FragmentCardEditTabCommentsBinding;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_CAN_EDIT;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;

public class CardCommentsFragment extends Fragment {

    private FragmentCardEditTabCommentsBinding binding;

    private Long accountId;
    private long localId;
    private boolean canEdit = false;

    public static CardCommentsFragment newInstance(long accountId, long localId, boolean canEdit) {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY_ACCOUNT_ID, accountId);
        bundle.putLong(BUNDLE_KEY_LOCAL_ID, localId);
        bundle.putBoolean(BUNDLE_KEY_CAN_EDIT, canEdit);

        CardCommentsFragment fragment = new CardCommentsFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(requireActivity() instanceof CommentDeletedListener)) {
            throw new IllegalArgumentException("Caller must implement \"" + CommentDeletedListener.class.getCanonicalName() + "\"");
        }
        Bundle args = getArguments();
        if (args == null || !args.containsKey(BUNDLE_KEY_ACCOUNT_ID) || !args.containsKey(BUNDLE_KEY_LOCAL_ID)) {
            throw new IllegalArgumentException("Arguments must at least contain an account and the local card id");
        }
        this.accountId = args.getLong(BUNDLE_KEY_ACCOUNT_ID);
        this.localId = args.getLong(BUNDLE_KEY_LOCAL_ID);
        this.canEdit = args.getBoolean(BUNDLE_KEY_CAN_EDIT, false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCardEditTabCommentsBinding.inflate(inflater, container, false);

        SyncManager syncManager = new SyncManager(requireActivity());
        syncManager.readAccount(accountId).observe(requireActivity(), (account -> {
            syncManager.getCommentsForLocalCardId(localId).observe(requireActivity(),
                    (comments) -> {
                        if (comments != null && comments.size() > 0) {
                            binding.emptyContentView.setVisibility(GONE);
                            binding.comments.setVisibility(VISIBLE);
                            binding.comments.setAdapter(new CardCommentsAdapter(requireContext(), comments, account, requireActivity().getMenuInflater(), (CommentDeletedListener) requireActivity()));
                        } else {
                            binding.emptyContentView.setVisibility(VISIBLE);
                            binding.comments.setVisibility(GONE);
                        }
                    });
            if (canEdit && getActivity() instanceof CommentAddedListener) {
                binding.addCommentLayout.setVisibility(VISIBLE);
                binding.fab.setOnClickListener(v -> {
                    binding.emptyContentView.setVisibility(GONE);
                    binding.comments.setVisibility(VISIBLE);
                    DeckComment comment = new DeckComment(binding.message.getText().toString());
                    comment.setActorDisplayName(account.getUserName());
                    comment.setCreationDateTime(new Date());
                    ((CommentAddedListener) getActivity()).onCommentAdded(comment);
                    binding.message.setText(null);
                });
                binding.message.setOnEditorActionListener((v, actionId, event) -> binding.fab.performClick());
            } else {
                binding.addCommentLayout.setVisibility(GONE);
            }
        }));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (canEdit) {
            binding.message.requestFocus();
            requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
