package it.niedermann.nextcloud.deck.ui.card;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import it.niedermann.nextcloud.deck.databinding.FragmentCardEditTabActivitiesBinding;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_BOARD_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_CAN_EDIT;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;

public class CardActivityFragment extends Fragment {


    private FragmentCardEditTabActivitiesBinding binding;

    public CardActivityFragment() {
    }

    public static CardActivityFragment newInstance(long accountId, long localId, long boardId, boolean canEdit) {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY_ACCOUNT_ID, accountId);
        bundle.putLong(BUNDLE_KEY_BOARD_ID, boardId);
        bundle.putLong(BUNDLE_KEY_LOCAL_ID, localId);
        bundle.putBoolean(BUNDLE_KEY_CAN_EDIT, canEdit);

        CardActivityFragment fragment = new CardActivityFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(context instanceof CommentDialogFragment.AddCommentListener)) {
            throw new ClassCastException("Caller must implement " + CommentDialogFragment.AddCommentListener.class.getCanonicalName());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCardEditTabActivitiesBinding.inflate(inflater, container, false);

        Bundle args = getArguments();
        if (args != null) {
            long accountId = args.getLong(BUNDLE_KEY_ACCOUNT_ID);
            long localId = args.getLong(BUNDLE_KEY_LOCAL_ID);
            boolean canEdit = args.getBoolean(BUNDLE_KEY_CAN_EDIT);

            SyncManager syncManager = new SyncManager(requireActivity());
            syncManager.getCardByLocalId(accountId, localId).observe(getViewLifecycleOwner(), (fullCard) -> {
                syncManager.syncActivitiesForCard(fullCard.getCard()).observe(getViewLifecycleOwner(), (activities -> {
                    if (activities == null || activities.size() == 0) {
                        binding.emptyContentView.setVisibility(View.VISIBLE);
                        binding.activitiesList.setVisibility(View.GONE);
                    } else {
                        binding.emptyContentView.setVisibility(View.GONE);
                        binding.activitiesList.setVisibility(View.VISIBLE);
                        RecyclerView.Adapter adapter = new ActivityAdapter(activities);
                        binding.activitiesList.setAdapter(adapter);
                    }
                }));
            });
            if (canEdit) {
                binding.fab.setOnClickListener(v -> {
                    Snackbar.make(binding.coordinatorLayout, "Adding comments is not yet implemented", Snackbar.LENGTH_LONG).show();
//                    CommentDialogFragment.newInstance().show(getActivity().getSupportFragmentManager(), addComment);
                });
                binding.activitiesList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        if (dy > 0)
                            binding.fab.hide();
                        else if (dy < 0)
                            binding.fab.show();
                    }
                });
            } else {
                binding.emptyContentView.hideDescription();
                binding.fab.hide();
            }
        }
        return binding.getRoot();
    }
}
