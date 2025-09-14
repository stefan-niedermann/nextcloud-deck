package it.niedermann.nextcloud.deck.deprecated.ui.upcomingcards;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.nextcloud.android.common.ui.theme.utils.ColorRole;
import com.nextcloud.android.sso.api.EmptyResponse;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;

import it.niedermann.nextcloud.deck.deprecated.util.DeckLog;
import it.niedermann.nextcloud.deck.databinding.ActivityUpcomingCardsBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.repository.SyncRepository;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.deck.deprecated.ui.movecard.MoveCardListener;
import it.niedermann.nextcloud.deck.deprecated.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.deprecated.ui.theme.Themed;
import okhttp3.Headers;

public class UpcomingCardsActivity extends AppCompatActivity implements Themed, MoveCardListener {

    private static final String KEY_ACCOUNT = "account";
    private Account account;
    private UpcomingCardsViewModel viewModel;
    private ActivityUpcomingCardsBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        if (!getIntent().hasExtra(KEY_ACCOUNT)) {
            throw new IllegalArgumentException(KEY_ACCOUNT + " must be provided");
        }

        account = (Account) getIntent().getSerializableExtra(KEY_ACCOUNT);

        binding = ActivityUpcomingCardsBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(UpcomingCardsViewModel.class);

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        applyTheme(account.getColor());

        binding.loadingSpinner.show();

        final var adapter = new UpcomingCardsAdapter(this, getSupportFragmentManager(),
                (a, c) -> {
                    try {
                        viewModel.assignUser(a, c);
                    } catch (NextcloudFilesAppAccountNotFoundException e) {
                        ExceptionDialogFragment.newInstance(e, a).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                    }
                },
                (a, c) -> {
                    try {
                        viewModel.unassignUser(a, c);
                    } catch (NextcloudFilesAppAccountNotFoundException e) {
                        ExceptionDialogFragment.newInstance(e, a).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                    }
                },
                fullCard -> viewModel.archiveCard(fullCard, new IResponseCallback<>() {
                    @Override
                    public void onResponse(FullCard response, Headers headers) {
                        DeckLog.info("Successfully archived", Card.class.getSimpleName(), fullCard.getCard().getTitle());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        IResponseCallback.super.onError(throwable);
                        runOnUiThread(() -> ExceptionDialogFragment.newInstance(throwable, null).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName()));
                    }
                }),
                card -> viewModel.deleteCard(card, new IResponseCallback<>() {
                    @Override
                    public void onResponse(EmptyResponse response, Headers headers) {
                        DeckLog.info("Successfully deleted card", card.getTitle());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (SyncRepository.isNoOnVoidError(throwable)) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    @NonNull
    public static Intent createIntent(@NonNull Context context, @NonNull Account account) {
        return new Intent(context, UpcomingCardsActivity.class)
                .putExtra(KEY_ACCOUNT, account)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    @Override
    public void move(long originAccountId, long originCardLocalId, long targetAccountId, long targetBoardLocalId, long targetStackLocalId) {
        viewModel.moveCard(originAccountId, originCardLocalId, targetAccountId, targetBoardLocalId, targetStackLocalId, new IResponseCallback<>() {
            @Override
            public void onResponse(EmptyResponse response, Headers headers) {
                DeckLog.log("Moved", Card.class.getSimpleName(), originCardLocalId, "to", Stack.class.getSimpleName(), targetStackLocalId);
            }

            @Override
            public void onError(Throwable throwable) {
                IResponseCallback.super.onError(throwable);
                if (SyncRepository.isNoOnVoidError(throwable)) {
                    ExceptionDialogFragment.newInstance(throwable, null).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                }
            }
        });
    }

    @Override
    public void applyTheme(int color) {
        final var utils = ThemeUtils.of(color, this);

        utils.platform.colorCircularProgressBar(binding.loadingSpinner, ColorRole.PRIMARY);
        utils.material.themeToolbar(binding.toolbar);
        utils.deck.themeStatusBar(this, binding.appBarLayout);
    }
}
