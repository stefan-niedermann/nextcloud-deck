package it.niedermann.nextcloud.deck.ui.card.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import it.niedermann.nextcloud.deck.databinding.FragmentCardEditTabActivitiesBinding;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.card.EditCardViewModel;

public class CardActivityFragment extends Fragment {

    private FragmentCardEditTabActivitiesBinding binding;

    public static Fragment newInstance() {
        return new CardActivityFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCardEditTabActivitiesBinding.inflate(inflater, container, false);
        final EditCardViewModel viewModel = new ViewModelProvider(requireActivity()).get(EditCardViewModel.class);

        if (!viewModel.isCreateMode()) {
            final SyncManager syncManager = new SyncManager(requireContext());

            syncManager.syncActivitiesForCard(viewModel.getFullCard().getCard()).observe(getViewLifecycleOwner(), (activities -> {
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
        } else {
            binding.emptyContentView.setVisibility(View.VISIBLE);
            binding.activitiesList.setVisibility(View.GONE);
        }
        return binding.getRoot();
    }
}
