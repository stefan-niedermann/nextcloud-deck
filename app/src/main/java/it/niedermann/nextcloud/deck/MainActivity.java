package it.niedermann.nextcloud.deck;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import java.util.concurrent.atomic.AtomicBoolean;

import it.niedermann.nextcloud.deck.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private MainViewModel vm;
    private ActivityMainBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vm = new ViewModelProvider(this).get(MainViewModel.class);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_main, null, false);

        final var navHostFragment = binding.navHostFragmentApp.getFragment();
        final var navController = NavHostFragment.findNavController(navHostFragment);

        final var initial = new AtomicBoolean(true);
        vm.hasAccounts().observe(this, hasAccounts -> {
            hasAccounts = true;
            if (hasAccounts) {
                // TODO navigate to boards view
                navController.getGraph().setStartDestination(R.id.main);
            } else {
                navController.getGraph().setStartDestination(R.id.feature_import);
            }
            navController.navigate(navController.getGraph().getStartDestinationId());

            if (initial.getAndSet(false)) {
                setContentView(binding.getRoot());
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
