package it.niedermann.nextcloud.deck.ui.card;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import it.niedermann.nextcloud.deck.databinding.FragmentCardEditTabActivitiesBinding;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_BOARD_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;

public class CardActivityFragment extends Fragment {

    private FragmentCardEditTabActivitiesBinding binding;

    public CardActivityFragment() {
    }

    public static CardActivityFragment newInstance(long accountId, long localId, long boardId) {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY_ACCOUNT_ID, accountId);
        bundle.putLong(BUNDLE_KEY_BOARD_ID, boardId);
        bundle.putLong(BUNDLE_KEY_LOCAL_ID, localId);

        CardActivityFragment fragment = new CardActivityFragment();
        fragment.setArguments(bundle);

        return fragment;
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

            SyncManager syncManager = new SyncManager(requireActivity());
            syncManager.getCardByLocalId(accountId, localId).observe(getViewLifecycleOwner(), (fullCard) -> {
                syncManager.syncActivitiesForCard(fullCard.getCard()).observe(getViewLifecycleOwner(), (activities -> {
                    if (activities == null || activities.size() == 0) {
                        binding.emptyContentView.setVisibility(View.VISIBLE);
                        binding.activitiesList.setVisibility(View.GONE);
                    } else {
                        binding.emptyContentView.setVisibility(View.GONE);
                        binding.activitiesList.setVisibility(View.VISIBLE);
                        RecyclerView.Adapter adapter = new CardActivityAdapter(activities, requireActivity().getMenuInflater());
                        binding.activitiesList.setAdapter(adapter);
                    }
                }));
            });
        }
        return binding.getRoot();
    }
}
