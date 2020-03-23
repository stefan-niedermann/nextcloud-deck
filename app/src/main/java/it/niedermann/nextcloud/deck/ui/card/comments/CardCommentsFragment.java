package it.niedermann.nextcloud.deck.ui.card.comments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Date;

import it.niedermann.nextcloud.deck.databinding.FragmentCardCommentsBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_CAN_EDIT;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;

public class CardCommentsFragment extends Fragment {

    private FragmentCardCommentsBinding binding;

    private Account account;
    private long localId;
    private boolean canEdit = false;

    public static CardCommentsFragment newInstance(Account account, long localId, boolean canEdit) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_KEY_ACCOUNT, account);
        bundle.putLong(BUNDLE_KEY_LOCAL_ID, localId);
        bundle.putBoolean(BUNDLE_KEY_CAN_EDIT, canEdit);

        CardCommentsFragment fragment = new CardCommentsFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Bundle args = getArguments();
        if (args == null || !args.containsKey(BUNDLE_KEY_ACCOUNT) || !args.containsKey(BUNDLE_KEY_LOCAL_ID)) {
            throw new IllegalArgumentException("Arguments must at least contain an account and the local card id");
        }
        this.account = (Account) args.getSerializable(BUNDLE_KEY_ACCOUNT);
        this.localId = args.getLong(BUNDLE_KEY_LOCAL_ID);
        this.canEdit = args.getBoolean(BUNDLE_KEY_CAN_EDIT, false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCardCommentsBinding.inflate(inflater, container, false);

        if (canEdit && getActivity() instanceof CommentAddedListener) {
            binding.addCommentLayout.setVisibility(View.VISIBLE);
            binding.fab.setOnClickListener(v -> {
                DeckComment comment = new DeckComment(binding.message.getText().toString());
                comment.setActorDisplayName(account.getUserName());
                comment.setCreationDateTime(new Date());
                ((CommentAddedListener) getActivity()).onCommentAdded(comment);
                binding.message.setText(null);
            });
            binding.message.setOnEditorActionListener((v, actionId, event) -> binding.fab.performClick());
        } else {
            binding.addCommentLayout.setVisibility(View.GONE);
        }

        binding.comments.setNestedScrollingEnabled(false);
        new SyncManager(requireActivity())
                .getCommentsForLocalCardId(localId).observe(requireActivity(),
                (comments) -> binding.comments.setAdapter(new CardCommentsAdapter(requireContext(), comments, account)));
        return binding.getRoot();
    }
}
