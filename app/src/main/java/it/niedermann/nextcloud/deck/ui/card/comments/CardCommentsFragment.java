package it.niedermann.nextcloud.deck.ui.card.comments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.List;

import it.niedermann.nextcloud.deck.databinding.FragmentCardCommentsBinding;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_BOARD_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_CAN_EDIT;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;

public class CardCommentsFragment extends Fragment {

    private FragmentCardCommentsBinding binding;

    private SyncManager syncManager;
    private boolean canEdit = false;

    public CardCommentsFragment() {
    }

    public static CardCommentsFragment newInstance(long accountId, long localId, long boardId, boolean canEdit) {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY_ACCOUNT_ID, accountId);
        bundle.putLong(BUNDLE_KEY_BOARD_ID, boardId);
        bundle.putLong(BUNDLE_KEY_LOCAL_ID, localId);
        bundle.putBoolean(BUNDLE_KEY_CAN_EDIT, canEdit);

        CardCommentsFragment fragment = new CardCommentsFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCardCommentsBinding.inflate(inflater, container, false);

        Bundle args = getArguments();
        if (args != null) {
            long localId = args.getLong(BUNDLE_KEY_LOCAL_ID);
            if (args.containsKey(BUNDLE_KEY_CAN_EDIT)) {
                this.canEdit = args.getBoolean(BUNDLE_KEY_CAN_EDIT);
            }

            if (canEdit && getActivity() instanceof CommentAddedListener) {
                binding.addCommentLayout.setVisibility(View.VISIBLE);
                binding.fab.setOnClickListener(v -> {
                    ((CommentAddedListener) requireActivity()).onCommentAdded(binding.message.getText().toString());
                    binding.message.setText(null);
                });
            } else {
                binding.addCommentLayout.setVisibility(View.GONE);
            }

            syncManager = new SyncManager(requireActivity());
            syncManager.getCommentsForLocalCardId(localId).observe(requireActivity(), (List<DeckComment> comments) -> {
                CardCommentsAdapter adapter = new CardCommentsAdapter(requireContext(), comments);
                binding.comments.setAdapter(adapter);
            });
        }
        return binding.getRoot();
    }
}
