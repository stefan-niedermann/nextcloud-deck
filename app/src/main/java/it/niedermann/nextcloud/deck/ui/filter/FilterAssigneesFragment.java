package it.niedermann.nextcloud.deck.ui.filter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogFilterAssigneesBinding;
import it.niedermann.nextcloud.deck.model.internal.FilterInformation;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.MainViewModel;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.util.DimensionUtil.dpToPx;

public class FilterAssigneesFragment extends Fragment {

    private FilterInformation filterInformation;
    private DialogFilterAssigneesBinding binding;
    private MainViewModel mainViewModel;
    private UserFilterAdapter userAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DialogFilterAssigneesBinding.inflate(requireActivity().getLayoutInflater());
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        final SyncManager syncManager = new SyncManager(requireActivity());

        this.filterInformation = mainViewModel.getFilterInformation().getValue();
        if (this.filterInformation == null) {
            this.filterInformation = new FilterInformation();
        }
        observeOnce(syncManager.findProposalsForUsersToAssign(mainViewModel.getCurrentAccount().getId(), mainViewModel.getCurrentBoardLocalId()), requireActivity(), (users) -> {
            userAdapter = new UserFilterAdapter(dpToPx(requireContext(), R.dimen.avatar_size), mainViewModel.getCurrentAccount(), users, this.filterInformation.getUsers());
            binding.users.setNestedScrollingEnabled(false);
            binding.users.setAdapter(userAdapter);
        });

        return binding.getRoot();
    }
}
