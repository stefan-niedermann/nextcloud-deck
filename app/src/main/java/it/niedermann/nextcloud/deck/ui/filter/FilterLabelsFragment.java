package it.niedermann.nextcloud.deck.ui.filter;

import static java.util.Objects.requireNonNull;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.deck.databinding.DialogFilterLabelsBinding;
import it.niedermann.nextcloud.deck.model.Label;

public class FilterLabelsFragment extends Fragment implements SelectionListener<Label> {

    private FilterViewModel filterViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final var binding = DialogFilterLabelsBinding.inflate(requireActivity().getLayoutInflater());

        filterViewModel = new ViewModelProvider(requireActivity()).get(FilterViewModel.class);

        new ReactiveLiveData<>(filterViewModel.findProposalsForLabelsToAssign())
                .combineWith(() -> filterViewModel.getCurrentBoardColor$())
                .observeOnce(getViewLifecycleOwner(), pair -> {
                    binding.labels.setNestedScrollingEnabled(false);
                    binding.labels.setAdapter(new FilterLabelsAdapter(
                            pair.first,
                            requireNonNull(filterViewModel.getFilterInformationDraft().getValue()).getLabels(),
                            requireNonNull(filterViewModel.getFilterInformationDraft().getValue()).isNoAssignedLabel(),
                            this,
                            pair.second));
                });

        return binding.getRoot();
    }

    @Override
    public void onItemSelected(@Nullable Label item) {
        if (item == null) {
            filterViewModel.setNotAssignedLabel(true);
        } else {
            filterViewModel.addFilterInformationDraftLabel(item);
        }
    }

    @Override
    public void onItemDeselected(@Nullable Label item) {
        if (item == null) {
            filterViewModel.setNotAssignedLabel(false);
        } else {
            filterViewModel.removeFilterInformationLabel(item);
        }
    }
}
