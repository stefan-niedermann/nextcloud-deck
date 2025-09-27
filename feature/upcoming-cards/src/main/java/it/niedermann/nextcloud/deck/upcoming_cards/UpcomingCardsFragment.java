package it.niedermann.nextcloud.deck.upcoming_cards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import it.niedermann.nextcloud.deck.upcoming_cards.databinding.FragmentUpcomingCardsBinding;

public class UpcomingCardsFragment extends Fragment {

    private UpcomingCardsViewModel vm;
    private FragmentUpcomingCardsBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.vm = new ViewModelProvider(requireActivity()).get(UpcomingCardsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_upcoming_cards, container, false);
        binding.setVariable(BR.vm, vm);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        NavigationUI.setupWithNavController(binding.toolbarLayout, binding.toolbar, NavHostFragment.findNavController(this));

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        vm = null;
        binding = null;
        super.onDestroy();
    }
}
