package it.niedermann.nextcloud.deck.ui.filter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.nextcloud.deck.databinding.DialogFilterLabelsBinding;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.internal.FilterInformation;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.MainViewModel;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static java.util.Objects.requireNonNull;

public class FilterLabelsFragment extends Fragment implements SelectionListener<Label> {

    private LiveData<FilterInformation> filterInformationDraft;
    private DialogFilterLabelsBinding binding;
    private MainViewModel mainViewModel;
    private FilterViewModel filterViewModel;
    private LabelFilterAdapter labelAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogFilterLabelsBinding.inflate(requireActivity().getLayoutInflater());
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        filterViewModel = new ViewModelProvider(requireActivity()).get(FilterViewModel.class);
        final SyncManager syncManager = new SyncManager(requireActivity());

        this.filterInformationDraft = filterViewModel.getFilterInformationDraft();
        observeOnce(syncManager.findProposalsForLabelsToAssign(mainViewModel.getCurrentAccount().getId(), mainViewModel.getCurrentBoardLocalId()), requireActivity(), (labels) -> {
            labelAdapter = new LabelFilterAdapter(labels, requireNonNull(this.filterInformationDraft.getValue()).getLabels(), this);
            binding.labels.setNestedScrollingEnabled(false);
            binding.labels.setAdapter(labelAdapter);
        });
        return binding.getRoot();
    }

    @Override
    public void onItemSelected(Label item) {
        filterViewModel.addFilterInformationDraftLabel(item);
    }

    @Override
    public void onItemDeselected(Label item) {
        filterViewModel.removeFilterInformationLabel(item);
    }
}
