package it.niedermann.nextcloud.deck;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

public class MainActivity extends AppCompatActivity {

    private MainViewModel vm;
    private ViewDataBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_main, null, false);
        setContentView(binding.getRoot());

        vm = new ViewModelProvider(this).get(MainViewModel.class);

        final var navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        final var navController = NavHostFragment.findNavController(navHostFragment);


        vm.hasAccounts().observe(this, hasAccounts -> {
            if (hasAccounts) {
                // TODO navigate to boards view
            } else {
                navController.navigate(R.id.feature_import);
            }
        });
    }
}
