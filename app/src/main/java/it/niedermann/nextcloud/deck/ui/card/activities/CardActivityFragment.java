package it.niedermann.nextcloud.deck.ui.card.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.databinding.FragmentCardEditTabActivitiesBinding;
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

        // This might be a zombie fragment with an empty EditCardViewModel after Android killed the activity (but not the fragment instance
        // See https://github.com/stefan-niedermann/nextcloud-deck/issues/478
        if (viewModel.getFullCard() == null) {
            DeckLog.logError(new IllegalStateException("Cannot populate " + CardActivityFragment.class.getSimpleName() + " because viewModel.getFullCard() is null"));
            return binding.getRoot();
        }

        if (!viewModel.isCreateMode()) {
            viewModel.syncActivitiesForCard(viewModel.getFullCard().getCard()).observe(getViewLifecycleOwner(), (activities -> {
                if (activities == null || activities.size() == 0) {
                    binding.emptyContentView.setVisibility(View.VISIBLE);
                    binding.activitiesList.setVisibility(View.GONE);
                } else {
                    binding.emptyContentView.setVisibility(View.GONE);
                    binding.activitiesList.setVisibility(View.VISIBLE);
                    binding.activitiesList.setAdapter(new CardActivityAdapter(activities, requireActivity().getMenuInflater()));
                }
            }));
        } else {
            binding.emptyContentView.setVisibility(View.VISIBLE);
            binding.activitiesList.setVisibility(View.GONE);
        }
        return binding.getRoot();
    }
}
