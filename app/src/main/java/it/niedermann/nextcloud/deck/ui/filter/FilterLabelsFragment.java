package it.niedermann.nextcloud.deck.ui.filter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.nextcloud.deck.databinding.DialogFilterLabelsBinding;
import it.niedermann.nextcloud.deck.model.internal.FilterInformation;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.MainViewModel;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;

public class FilterLabelsFragment extends Fragment {

    private FilterInformation filterInformation;
    private DialogFilterLabelsBinding binding;
    private MainViewModel mainViewModel;
    private LabelFilterAdapter labelAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogFilterLabelsBinding.inflate(requireActivity().getLayoutInflater());
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        final SyncManager syncManager = new SyncManager(requireActivity());

        this.filterInformation = mainViewModel.getFilterInformation().getValue();
        if (this.filterInformation == null) {
            this.filterInformation = new FilterInformation();
        }
        observeOnce(syncManager.findProposalsForLabelsToAssign(mainViewModel.getCurrentAccount().getId(), mainViewModel.getCurrentBoardLocalId()), requireActivity(), (labels) -> {
            labelAdapter = new LabelFilterAdapter(labels, this.filterInformation.getLabels());
            binding.labels.setNestedScrollingEnabled(false);
            binding.labels.setAdapter(labelAdapter);
        });
        return binding.getRoot();
    }
}
