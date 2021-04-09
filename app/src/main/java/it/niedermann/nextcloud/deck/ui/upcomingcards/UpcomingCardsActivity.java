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
import it.niedermann.nextcloud.deck.api.ResponseCallback;
import it.niedermann.nextcloud.deck.databinding.ActivityUpcomingCardsBinding;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.WrappedLiveData;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.deck.ui.movecard.MoveCardListener;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;

public class UpcomingCardsActivity extends AppCompatActivity implements MoveCardListener {

    private UpcomingCardsViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        final ActivityUpcomingCardsBinding binding = ActivityUpcomingCardsBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(UpcomingCardsViewModel.class);

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        binding.loadingSpinner.show();

        final UpcomingCardsAdapter adapter = new UpcomingCardsAdapter(this, getSupportFragmentManager(),
                viewModel::assignUser,
                viewModel::unassignUser,
                (fullCard) -> viewModel.archiveCard(fullCard, new ResponseCallback<FullCard>() {
                    @Override
                    public void onResponse(FullCard response) {
                        DeckLog.info("Successfully archived", Card.class.getSimpleName(), fullCard.getCard().getTitle());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        ResponseCallback.super.onError(throwable);
                        runOnUiThread(() -> ExceptionDialogFragment.newInstance(throwable, null).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName()));
                    }
                }),
                (card) -> viewModel.deleteCard(card, new ResponseCallback<Void>() {
                    @Override
                    public void onResponse(Void response) {
                        DeckLog.info("Successfully deleted card", card.getTitle());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (!SyncManager.ignoreExceptionOnVoidError(throwable)) {
                            ResponseCallback.super.onError(throwable);
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
        final WrappedLiveData<Void> liveData = viewModel.moveCard(originAccountId, originCardLocalId, targetAccountId, targetBoardLocalId, targetStackLocalId);
        observeOnce(liveData, this, (next) -> {
            if (liveData.hasError() && !SyncManager.ignoreExceptionOnVoidError(liveData.getError())) {
                ExceptionDialogFragment.newInstance(liveData.getError(), null).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
            } else {
                DeckLog.log("Moved", Card.class.getSimpleName(), originCardLocalId, "to", Stack.class.getSimpleName(), targetStackLocalId);
            }
        });
    }
}
