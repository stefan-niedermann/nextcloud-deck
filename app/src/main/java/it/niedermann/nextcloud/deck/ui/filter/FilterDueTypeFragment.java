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

import it.niedermann.nextcloud.deck.databinding.DialogFilterDuedateBinding;
import it.niedermann.nextcloud.deck.model.enums.EDueType;

public class FilterDueTypeFragment extends Fragment implements SelectionListener<EDueType> {

    private FilterViewModel filterViewModel;
    private DialogFilterDuedateBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogFilterDuedateBinding.inflate(requireActivity().getLayoutInflater());

        filterViewModel = new ViewModelProvider(requireActivity()).get(FilterViewModel.class);

        binding.dueType.setItemAnimator(null);
        binding.dueType.setAdapter(new FilterDueTypeAdapter(requireNonNull(filterViewModel.getFilterInformationDraft().getValue()).getDueType(), this));

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    @Override
    public void onItemSelected(EDueType item) {
        filterViewModel.setFilterInformationDraftDueType(item);
    }
}
