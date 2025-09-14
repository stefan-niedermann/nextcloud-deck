package it.niedermann.nextcloud.deck.deprecated.ui.card.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.deck.deprecated.util.DeckLog;
import it.niedermann.nextcloud.deck.databinding.FragmentCardEditTabActivitiesBinding;
import it.niedermann.nextcloud.deck.deprecated.ui.card.EditCardViewModel;
import it.niedermann.nextcloud.deck.deprecated.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.deprecated.ui.theme.Themed;

public class CardActivityFragment extends Fragment implements Themed {

    private FragmentCardEditTabActivitiesBinding binding;

    public static Fragment newInstance() {
        return new CardActivityFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCardEditTabActivitiesBinding.inflate(inflater, container, false);
        final var viewModel = new ViewModelProvider(requireActivity()).get(EditCardViewModel.class);

        // This might be a zombie fragment with an empty EditCardViewModel after Android killed the activity (but not the fragment instance
        // See https://github.com/stefan-niedermann/nextcloud-deck/issues/478
        if (viewModel.getFullCard() == null) {
            DeckLog.logError(new IllegalStateException("Cannot populate " + CardActivityFragment.class.getSimpleName() + " because viewModel.getFullCard() is null"));
            return binding.getRoot();
        }

        final var adapter = new CardActivityAdapter(requireActivity().getMenuInflater());
        binding.activitiesList.setAdapter(adapter);

        new ReactiveLiveData<>(viewModel.syncActivitiesForCard(viewModel.getFullCard().getCard()))
                .combineWith(viewModel::getBoardColor)
                .observe(getViewLifecycleOwner(), data -> {
                    applyTheme(data.second);
                    if (data.first == null || data.first.size() == 0) {
                        binding.emptyContentView.setVisibility(View.VISIBLE);
                        binding.activitiesList.setVisibility(View.GONE);
                    } else {
                        binding.emptyContentView.setVisibility(View.GONE);
                        binding.activitiesList.setVisibility(View.VISIBLE);
                    }
                    adapter.setData(data.first, ThemeUtils.of(data.second, requireContext()));
                });
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    @Override
    public void applyTheme(int color) {
        final var utils = ThemeUtils.of(color, requireContext());

        utils.deck.themeEmptyContentView(binding.emptyContentView);
    }
}
