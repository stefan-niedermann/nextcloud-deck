package it.niedermann.nextcloud.deck;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import it.niedermann.nextcloud.deck.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private MainViewModel vm;
    private ActivityMainBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        vm = new ViewModelProvider(this).get(MainViewModel.class);

        final var navHostFragment = binding.navHostFragment.getFragment();
        final var navController = NavHostFragment.findNavController(navHostFragment);


        vm.hasAccounts().observe(this, hasAccounts -> {
            if (hasAccounts) {
                // TODO navigate to boards view
            } else {
                navController.navigate(R.id.feature_import);
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
