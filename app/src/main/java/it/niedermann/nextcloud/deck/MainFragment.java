package it.niedermann.nextcloud.deck;

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

import it.niedermann.nextcloud.deck.databinding.FragmentMainBinding;
import it.niedermann.nextcloud.deck.setup.BR;

public class MainFragment extends Fragment {

    private MainViewModel vm;
    private FragmentMainBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        binding.setVariable(BR.vm, vm);
        binding.setVariable(BR.fragment, this);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.navigationRail.getMenu().add(0, it.niedermann.nextcloud.deck.upcoming_cards.R.id.nav_graph_upcoming_cards, 0, "Upcoming cards (i18n)");
        binding.navigationRail.getMenu().add(0, it.niedermann.nextcloud.deck.view_board.R.id.nav_graph_view_board, 0, "View board (i18n)");
        binding.navigationRail.getMenu().add(0, it.niedermann.nextcloud.deck.about.R.id.nav_graph_about, 0, "About (i18n)");

        final var navController = NavHostFragment.findNavController(binding.navHostFragmentMain.getFragment());
        NavigationUI.setupWithNavController(binding.navigationRail, navController);

        binding.navigationRail.setOnItemSelectedListener(v -> {
//            final var action = MainFragmentDirections.actionMainToViewBoard(new Long(v.getItemId()));
//            Navigation.findNavController(binding.navigationRail).navigate(action);
            return true;
        });
    }

    @Override
    public void onDestroy() {
        vm = null;
        binding = null;
        super.onDestroy();
    }

}
