package it.niedermann.nextcloud.deck.ui;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.databinding.ActivityPickStackBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.deck.ui.pickstack.PickStackFragment;
import it.niedermann.nextcloud.deck.ui.pickstack.PickStackListener;
import it.niedermann.nextcloud.deck.ui.pickstack.PickStackViewModel;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.ui.theme.Themed;
import it.niedermann.nextcloud.deck.util.OnTextChangedWatcher;

public abstract class PickStackActivity extends AppCompatActivity implements Themed, PickStackListener {

    private ActivityPickStackBinding binding;
    private PickStackViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        binding = ActivityPickStackBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(PickStackViewModel.class);

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        final var hasAccounts$ = new ReactiveLiveData<>(viewModel.hasAccounts());

        hasAccounts$
                .filter(hasAccounts -> !hasAccounts)
                .observe(this, () -> {
                    startActivity(ImportAccountActivity.createIntent(this));
                    finish();
                });

        hasAccounts$
                .filter(hasAccounts -> hasAccounts)
                .observe(this, () -> getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, PickStackFragment.newInstance(showBoardsWithoutEditPermission()))
                        .commit());

        binding.cancel.setOnClickListener((v) -> finish());
        binding.submit.setOnClickListener((v) -> {
            viewModel.setSubmitInProgress(true);
            onSubmit(viewModel.getAccount(), viewModel.getBoardLocalId(), viewModel.getStackLocalId(), new IResponseCallback<>() {
                @Override
                public void onResponse(Void response) {
                    runOnUiThread(() -> viewModel.setSubmitInProgress(false));
                }

                @Override
                public void onError(Throwable throwable) {
                    IResponseCallback.super.onError(throwable);
                    runOnUiThread(() -> {
                        viewModel.setSubmitInProgress(false);
                        ExceptionDialogFragment
                                .newInstance(throwable, viewModel.getAccount())
                                .show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                    });
                }
            });
        });
        viewModel.submitButtonEnabled().observe(this, enabled -> binding.submit.setEnabled(enabled));
        if (requireContent()) {
            viewModel.setContentIsSatisfied(false);
            binding.inputWrapper.setVisibility(View.VISIBLE);
            binding.input.addTextChangedListener(new OnTextChangedWatcher(s -> viewModel.setContentIsSatisfied(s != null && !s.trim().isEmpty())));
        } else {
            viewModel.setContentIsSatisfied(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    @Override
    public void onStackPicked(@NonNull Account account, @Nullable Board board, @Nullable Stack stack) {
        viewModel.setSelected(account, board, stack);
        applyTheme(board == null
                ? ContextCompat.getColor(this, R.color.accent)
                : board.getColor()
        );
    }

    @Override
    public void applyTheme(int color) {
        final var utils = ThemeUtils.of(color, this);

        utils.material.colorMaterialButtonText(binding.cancel);
        utils.material.colorMaterialButtonPrimaryFilled(binding.submit);
        utils.material.colorTextInputLayout(binding.inputWrapper);
    }

    abstract protected void onSubmit(Account account, long boardLocalId, long stackId, @NonNull IResponseCallback<Void> callback);

    abstract protected boolean showBoardsWithoutEditPermission();

    protected boolean requireContent() {
        return false;
    }

    @NonNull
    protected String getContent() {
        final Editable text = binding.input.getText();
        return text == null ? "" : text.toString();
    }
}