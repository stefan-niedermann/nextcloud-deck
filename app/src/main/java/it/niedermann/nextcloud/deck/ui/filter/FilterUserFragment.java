package it.niedermann.nextcloud.deck.ui.filter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.android.util.DimensionUtil;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.DialogFilterAssigneesBinding;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.ui.MainViewModel;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static java.util.Objects.requireNonNull;

public class FilterUserFragment extends Fragment implements SelectionListener<User> {

    private FilterViewModel filterViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final DialogFilterAssigneesBinding binding = DialogFilterAssigneesBinding.inflate(requireActivity().getLayoutInflater());
        final MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        filterViewModel = new ViewModelProvider(requireActivity()).get(FilterViewModel.class);

        observeOnce(filterViewModel.findProposalsForUsersToAssign(mainViewModel.getCurrentAccount().getId(), mainViewModel.getCurrentBoardLocalId()), requireActivity(), (users) -> {
            binding.users.setNestedScrollingEnabled(false);
            binding.users.setAdapter(new FilterUserAdapter(
                    DimensionUtil.INSTANCE.dpToPx(requireContext(), R.dimen.avatar_size),
                    mainViewModel.getCurrentAccount(),
                    users,
                    requireNonNull(filterViewModel.getFilterInformationDraft().getValue()).getUsers(),
                    requireNonNull(filterViewModel.getFilterInformationDraft().getValue()).isNoAssignedUser(),
                    this));
        });

        return binding.getRoot();
    }

    @Override
    public void onItemSelected(@Nullable User item) {
        if (item == null) {
            filterViewModel.setNotAssignedUser(true);
        } else {
            filterViewModel.addFilterInformationUser(item);
        }
    }

    @Override
    public void onItemDeselected(@Nullable User item) {
        if (item == null) {
            filterViewModel.setNotAssignedUser(false);
        } else {
            filterViewModel.removeFilterInformationUser(item);
        }
    }
}
