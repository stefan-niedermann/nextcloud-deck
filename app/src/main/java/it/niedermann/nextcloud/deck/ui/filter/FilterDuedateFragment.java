package it.niedermann.nextcloud.deck.ui.filter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.nextcloud.deck.databinding.DialogFilterDuedateBinding;
import it.niedermann.nextcloud.deck.model.enums.EDueType;
import it.niedermann.nextcloud.deck.model.internal.FilterInformation;
import it.niedermann.nextcloud.deck.ui.MainViewModel;

public class FilterDuedateFragment extends Fragment {

    private FilterInformation filterInformation;
    private DialogFilterDuedateBinding binding;
    private MainViewModel mainViewModel;
    private OverdueFilterAdapter overdueAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogFilterDuedateBinding.inflate(requireActivity().getLayoutInflater());
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        overdueAdapter = new OverdueFilterAdapter(requireContext());
        binding.overdue.setAdapter(overdueAdapter);
        this.filterInformation = mainViewModel.getFilterInformation().getValue();
        if (this.filterInformation == null) {
            this.filterInformation = new FilterInformation();
        }
        binding.overdue.setSelection(overdueAdapter.getPosition(this.filterInformation.getDueType()));
        binding.overdue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterInformation.setDueType(overdueAdapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filterInformation.setDueType(EDueType.NO_FILTER);
            }
        });
        return binding.getRoot();
    }
}
