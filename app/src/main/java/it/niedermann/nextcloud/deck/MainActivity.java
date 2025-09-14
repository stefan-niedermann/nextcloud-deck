package it.niedermann.nextcloud.deck;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.navigation.fragment.NavHostFragment;

import it.niedermann.nextcloud.deck.repository.AccountRepository;

public class MainActivity extends AppCompatActivity {

    private AccountRepository accountRepository;
    private ViewDataBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_main, null, false);
        setContentView(binding.getRoot());
        final var navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        final var navController = NavHostFragment.findNavController(navHostFragment);

        accountRepository = new AccountRepository(this);
        accountRepository.hasAccounts().observe(this, hasAccounts -> {
            if (hasAccounts) {
                // TODO navigate to boards view
            } else {
                navController.navigate(R.id.feature_import);
            }
        });
    }
}
