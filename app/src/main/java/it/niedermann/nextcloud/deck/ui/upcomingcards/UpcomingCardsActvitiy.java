package it.niedermann.nextcloud.deck.ui.upcomingcards;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.nextcloud.deck.databinding.ActivityUpcomingCardsBinding;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;

public class UpcomingCardsActvitiy extends AppCompatActivity {

    private UpcomingCardsViewModel viewModel;
    private ActivityUpcomingCardsBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        binding = ActivityUpcomingCardsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        final UpcomingCardsAdapter adapter = new UpcomingCardsAdapter(this, getSupportFragmentManager());
        binding.recyclerView.setAdapter(adapter);
        viewModel = new ViewModelProvider(this).get(UpcomingCardsViewModel.class);
        viewModel.getUpcomingCards().observe(this, adapter::setCardList);
    }

    @NonNull
    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, UpcomingCardsActvitiy.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
}
