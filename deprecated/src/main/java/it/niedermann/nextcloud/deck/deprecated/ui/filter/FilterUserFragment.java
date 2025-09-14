package it.niedermann.nextcloud.deck.deprecated.ui.filter;

import static java.util.Objects.requireNonNull;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.deck.databinding.DialogFilterAssigneesBinding;
import it.niedermann.nextcloud.deck.model.User;

public class FilterUserFragment extends Fragment implements SelectionListener<User> {

    private FilterViewModel filterViewModel;
    private DialogFilterAssigneesBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogFilterAssigneesBinding.inflate(requireActivity().getLayoutInflater());

        filterViewModel = new ViewModelProvider(requireActivity()).get(FilterViewModel.class);

        new ReactiveLiveData<>(filterViewModel.findProposalsForUsersToAssign())
                .combineWith(() -> filterViewModel.getCurrentBoardColor$())
                .observeOnce(getViewLifecycleOwner(), pair -> {
                    binding.users.setNestedScrollingEnabled(false);
                    filterViewModel.getCurrentAccount().thenAcceptAsync(account -> binding.users.setAdapter(new FilterUserAdapter(
                            account, pair.first,
                            requireNonNull(filterViewModel.getFilterInformationDraft().getValue()).getUsers(),
                            requireNonNull(filterViewModel.getFilterInformationDraft().getValue()).isNoAssignedUser(),
                            this,
                            pair.second)), ContextCompat.getMainExecutor(requireContext()));
                });

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.binding = null;
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
