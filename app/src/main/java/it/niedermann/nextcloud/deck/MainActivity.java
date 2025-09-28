package it.niedermann.nextcloud.deck;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import it.niedermann.nextcloud.deck.databinding.ActivityMainBinding;
import it.niedermann.nextcloud.deck.setup.ImportAccountViewModel;

public class MainActivity extends AppCompatActivity {

    private MainViewModel vm;
    private ActivityMainBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vm = new ViewModelProvider(this).get(MainViewModel.class);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_main, null, false);

        // TODO Defer the transition from splashscreen to content
        setContentView(binding.getRoot());

        final var navController = NavHostFragment.findNavController(binding.navHostFragmentApp.getFragment());
        vm.hasAccounts().observe(this, hasAccounts -> {

            if (!hasAccounts) {

                navController.getGraph().setStartDestination(it.niedermann.nextcloud.deck.setup.R.id.nav_graph_setup);
                navController.navigate(navController.getGraph().getStartDestinationId());

                final var vm = new ViewModelProvider(this).get(ImportAccountViewModel.class);
                final var isImportSuccessfulSubscription = vm.isImportSuccessful();

                isImportSuccessfulSubscription.observe(this, importSuccessful -> {
                    if (importSuccessful) {
                        isImportSuccessfulSubscription.removeObservers(this);
                        navController.getGraph().setStartDestination(R.id.main);
                        navController.navigate(navController.getGraph().getStartDestinationId());
                    }
                });

            }
        });
    }

    @Override
    protected void onDestroy() {
        binding = null;
        vm = null;
        super.onDestroy();
    }
}
