package it.niedermann.nextcloud.deck.ui;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import it.niedermann.android.util.ColorUtil;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivityPickStackBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.ui.branding.Branded;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.deck.ui.pickstack.PickStackFragment;
import it.niedermann.nextcloud.deck.ui.pickstack.PickStackListener;
import it.niedermann.nextcloud.deck.ui.pickstack.PickStackViewModel;

import static androidx.lifecycle.Transformations.switchMap;
import static it.niedermann.nextcloud.deck.DeckApplication.isDarkTheme;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.getSecondaryForegroundColorDependingOnTheme;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.isBrandingEnabled;
import static it.niedermann.nextcloud.deck.util.DeckColorUtil.contrastRatioIsSufficientBigAreas;

public abstract class PickStackActivity extends AppCompatActivity implements Branded, PickStackListener {

    protected ActivityPickStackBinding binding;
    protected PickStackViewModel viewModel;

    private boolean brandingEnabled;

    private Account selectedAccount;
    private Board selectedBoard;
    private Stack selectedStack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        brandingEnabled = isBrandingEnabled(this);

        binding = ActivityPickStackBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(PickStackViewModel.class);

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        switchMap(viewModel.hasAccounts(), hasAccounts -> {
            if (hasAccounts) {
                return viewModel.readAccounts();
            } else {
                startActivityForResult(ImportAccountActivity.createIntent(this), ImportAccountActivity.REQUEST_CODE_IMPORT_ACCOUNT);
                return null;
            }
        }).observe(this, (List<Account> accounts) -> {
            if (accounts == null || accounts.size() == 0) {
                throw new IllegalStateException("hasAccounts() returns true, but readAccounts() returns null or has no entry");
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, PickStackFragment.newInstance(showBoardsWithoutEditPermission()))
                    .commit();
        });
        binding.cancel.setOnClickListener((v) -> finish());
        binding.submit.setOnClickListener((v) -> onSubmit(selectedAccount, selectedBoard.getLocalId(), selectedStack.getLocalId()));
    }

    @Override
    public void onStackPicked(@NonNull Account account, @Nullable Board board, @Nullable Stack stack) {
        this.selectedAccount = account;
        this.selectedBoard = board;
        this.selectedStack = stack;
        if (board == null) {
            binding.submit.setEnabled(false);
        } else {
            applyBrand(board.getColor());
            binding.submit.setEnabled(stack != null);
        }
    }

    @Override
    public void applyBrand(int mainColor) {
        try {
            if (brandingEnabled) {
                @ColorInt final int finalMainColor = contrastRatioIsSufficientBigAreas(mainColor, ContextCompat.getColor(this, R.color.primary))
                        ? mainColor
                        : isDarkTheme(this) ? Color.WHITE : Color.BLACK;
                DrawableCompat.setTintList(binding.submit.getBackground(), ColorStateList.valueOf(finalMainColor));
                binding.submit.setTextColor(ColorUtil.INSTANCE.getForegroundColorForBackgroundColor(finalMainColor));
                binding.cancel.setTextColor(getSecondaryForegroundColorDependingOnTheme(this, mainColor));
            }
        } catch (Throwable t) {
            DeckLog.logError(t);
        }
    }

    abstract protected void onSubmit(Account account, long boardLocalId, long stackId);

    abstract protected boolean showBoardsWithoutEditPermission();
}