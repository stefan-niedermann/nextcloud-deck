package it.niedermann.nextcloud.deck.ui.upcomingcards;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.nextcloud.deck.databinding.ActivityUpcomingCardsBinding;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;

public class UpcomingCardsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        final ActivityUpcomingCardsBinding binding = ActivityUpcomingCardsBinding.inflate(getLayoutInflater());
        final UpcomingCardsViewModel viewModel = new ViewModelProvider(this).get(UpcomingCardsViewModel.class);

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        final UpcomingCardsAdapter adapter = new UpcomingCardsAdapter(this, getSupportFragmentManager());
        binding.recyclerView.setAdapter(adapter);
        viewModel.getUpcomingCards().observe(this, items -> {
            if (items.size() > 0) {
                binding.recyclerView.setVisibility(View.VISIBLE);
                binding.emptyContentView.setVisibility(View.GONE);
            } else {
                binding.recyclerView.setVisibility(View.GONE);
                binding.emptyContentView.setVisibility(View.VISIBLE);
            }
            adapter.setItems(items);
        });
    }

    @NonNull
    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, UpcomingCardsActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
}
