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

import it.niedermann.nextcloud.deck.databinding.DialogFilterDoneBinding;
import it.niedermann.nextcloud.deck.model.enums.EDoneType;

public class FilterDoneTypeFragment extends Fragment implements SelectionListener<EDoneType> {

    private FilterViewModel filterViewModel;
    private DialogFilterDoneBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogFilterDoneBinding.inflate(requireActivity().getLayoutInflater());

        filterViewModel = new ViewModelProvider(requireActivity()).get(FilterViewModel.class);

        binding.doneType.setItemAnimator(null);
        filterViewModel.getCurrentBoardColor$().observe(getViewLifecycleOwner(),
                color -> binding.doneType.setAdapter(new FilterDoneTypeAdapter(requireNonNull(filterViewModel.getFilterInformationDraft().getValue()).getDoneType(), this, color)));

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    @Override
    public void onItemSelected(EDoneType item) {
        filterViewModel.setFilterInformationDraftDoneType(item);
    }
}
