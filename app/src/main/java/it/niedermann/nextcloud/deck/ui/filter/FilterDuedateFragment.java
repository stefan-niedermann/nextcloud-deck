package it.niedermann.nextcloud.deck.ui.filter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.nextcloud.deck.databinding.DialogFilterDuedateBinding;
import it.niedermann.nextcloud.deck.model.enums.EDueType;
import it.niedermann.nextcloud.deck.model.internal.FilterInformation;

public class FilterDuedateFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private LiveData<FilterInformation> filterInformationDraft;
    private DialogFilterDuedateBinding binding;
    private FilterViewModel filterViewModel;
    private OverdueFilterAdapter overdueAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogFilterDuedateBinding.inflate(requireActivity().getLayoutInflater());
        filterViewModel = new ViewModelProvider(requireActivity()).get(FilterViewModel.class);
        overdueAdapter = new OverdueFilterAdapter(requireContext());
        binding.overdue.setAdapter(overdueAdapter);
        this.filterInformationDraft = filterViewModel.getFilterInformationDraft();
        binding.overdue.setSelection(overdueAdapter.getPosition(this.filterInformationDraft.getValue().getDueType()));
        binding.overdue.setOnItemSelectedListener(this);
        return binding.getRoot();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        filterViewModel.setFilterInformationDraftDueType(overdueAdapter.getItem(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        filterViewModel.setFilterInformationDraftDueType(EDueType.NO_FILTER);
    }
}
