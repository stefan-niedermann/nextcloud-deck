package it.niedermann.nextcloud.deck.ui.upcomingcards;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.databinding.ActivityUpcomingCardsBinding;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.deck.ui.movecard.MoveCardListener;

public class UpcomingCardsActivity extends AppCompatActivity implements MoveCardListener {

    private UpcomingCardsViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        final var binding = ActivityUpcomingCardsBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(UpcomingCardsViewModel.class);

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        binding.loadingSpinner.show();

        final var adapter = new UpcomingCardsAdapter(this, getSupportFragmentManager(),
                viewModel::assignUser,
                viewModel::unassignUser,
                (fullCard) -> viewModel.archiveCard(fullCard, new IResponseCallback<>() {
                    @Override
                    public void onResponse(FullCard response) {
                        DeckLog.info("Successfully archived", Card.class.getSimpleName(), fullCard.getCard().getTitle());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        IResponseCallback.super.onError(throwable);
                        runOnUiThread(() -> ExceptionDialogFragment.newInstance(throwable, null).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName()));
                    }
                }),
                (card) -> viewModel.deleteCard(card, new IResponseCallback<>() {
                    @Override
                    public void onResponse(Void response) {
                        DeckLog.info("Successfully deleted card", card.getTitle());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (!SyncManager.ignoreExceptionOnVoidError(throwable)) {
                            IResponseCallback.super.onError(throwable);
                            runOnUiThread(() -> ExceptionDialogFragment.newInstance(throwable, null).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName()));
                        }
                    }
                })
        );
        binding.recyclerView.setAdapter(adapter);
        viewModel.getUpcomingCards().observe(this, items -> {
            binding.loadingSpinner.hide();
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

    @Override
    public void move(long originAccountId, long originCardLocalId, long targetAccountId, long targetBoardLocalId, long targetStackLocalId) {
        viewModel.moveCard(originAccountId, originCardLocalId, targetAccountId, targetBoardLocalId, targetStackLocalId, new IResponseCallback<>() {
            @Override
            public void onResponse(Void response) {
                DeckLog.log("Moved", Card.class.getSimpleName(), originCardLocalId, "to", Stack.class.getSimpleName(), targetStackLocalId);
            }

            @Override
            public void onError(Throwable throwable) {
                IResponseCallback.super.onError(throwable);
                if (!SyncManager.ignoreExceptionOnVoidError(throwable)) {
                    ExceptionDialogFragment.newInstance(throwable, null).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                }
            }
        });
    }
}
