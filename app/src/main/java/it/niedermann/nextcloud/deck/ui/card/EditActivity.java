package it.niedermann.nextcloud.deck.ui.card;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivityEditBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.ui.branding.BrandedActivity;
import it.niedermann.nextcloud.deck.ui.branding.BrandedAlertDialogBuilder;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.deck.util.CardUtil;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.applyBrandToPrimaryTabLayout;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.isBrandingEnabled;

public class EditActivity extends BrandedActivity {
    private static final String BUNDLE_KEY_ACCOUNT = "account";
    private static final String BUNDLE_KEY_BOARD_ID = "boardId";
    private static final String BUNDLE_KEY_STACK_ID = "stackId";
    private static final String BUNDLE_KEY_CARD_ID = "cardId";
    private static final String BUNDLE_KEY_TITLE = "title";

    private ActivityEditBinding binding;
    private EditCardViewModel viewModel;

    private static final int[] tabTitles = new int[]{
            R.string.card_edit_details,
            R.string.card_edit_attachments,
            R.string.card_edit_activity
    };

    private static final int[] tabTitlesWithComments = new int[]{
            R.string.card_edit_details,
            R.string.card_edit_attachments,
            R.string.card_edit_comments,
            R.string.card_edit_activity
    };

    private static final int[] tabIcons = new int[]{
            R.drawable.ic_home_grey600_24dp,
            R.drawable.ic_attach_file_grey600_24dp,
            R.drawable.ic_activity_light_grey
    };

    private static final int[] tabIconsWithComments = new int[]{
            R.drawable.ic_home_grey600_24dp,
            R.drawable.ic_attach_file_grey600_24dp,
            R.drawable.type_comment_grey600_36dp,
            R.drawable.ic_activity_light_grey
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        viewModel = new ViewModelProvider(this).get(EditCardViewModel.class);

        loadDataFromIntent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        applyBrand(colorAccent);
        setIntent(intent);
        loadDataFromIntent();
    }

    private void loadDataFromIntent() {
        final Bundle args = getIntent().getExtras();

        if (args == null || !args.containsKey(BUNDLE_KEY_ACCOUNT) || !args.containsKey(BUNDLE_KEY_BOARD_ID)) {
            throw new IllegalArgumentException("Provide at least " + BUNDLE_KEY_ACCOUNT + " and " + BUNDLE_KEY_BOARD_ID + " of the card that should be edited or created.");
        }

        long cardId = args.getLong(BUNDLE_KEY_CARD_ID);

        if (cardId == 0L) {
            viewModel.setCreateMode(true);
            if (!args.containsKey(BUNDLE_KEY_STACK_ID)) {
                throw new IllegalArgumentException("When creating a card, passing the " + BUNDLE_KEY_STACK_ID + " is mandatory");
            }
        }

        final Account account = (Account) args.getSerializable(BUNDLE_KEY_ACCOUNT);
        if (account == null) {
            throw new IllegalArgumentException(BUNDLE_KEY_ACCOUNT + " must not be null.");
        }
        viewModel.setAccount(account);

        final long boardId = args.getLong(BUNDLE_KEY_BOARD_ID);

        observeOnce(viewModel.getFullBoardById(account.getId(), boardId), EditActivity.this, (fullBoard -> {
            applyBrand(fullBoard.getBoard().getColor());
            viewModel.setCanEdit(fullBoard.getBoard().isPermissionEdit());
            invalidateOptionsMenu();
            if (viewModel.isCreateMode()) {
                viewModel.initializeNewCard(boardId, args.getLong(BUNDLE_KEY_STACK_ID), account.getServerDeckVersionAsObject().isSupported(this));
                invalidateOptionsMenu();
                String title = args.getString(BUNDLE_KEY_TITLE);
                if (!TextUtils.isEmpty(title)) {
                    if (title.length() > viewModel.getAccount().getServerDeckVersionAsObject().getCardTitleMaxLength()) {
                        viewModel.getFullCard().getCard().setDescription(title);
                    } else {
                        viewModel.getFullCard().getCard().setTitle(title);
                    }
                }
                setupViewPager();
                setupTitle();
            } else {
                observeOnce(viewModel.getFullCardWithProjectsByLocalId(account.getId(), cardId), EditActivity.this, (fullCard) -> {
                    if (fullCard == null) {
                        new BrandedAlertDialogBuilder(this)
                                .setTitle(R.string.card_not_found)
                                .setMessage(R.string.card_not_found_message)
                                .setPositiveButton(R.string.simple_close, (a, b) -> super.finish())
                                .show();
                    } else {
                        viewModel.initializeExistingCard(boardId, fullCard, account.getServerDeckVersionAsObject().isSupported(this));
                        invalidateOptionsMenu();
                        setupViewPager();
                        setupTitle();
                    }
                });
            }
        }));

        DeckLog.verbose("Finished loading intent data: { accountId = " + viewModel.getAccount().getId() + " , cardId = " + cardId + " }");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (viewModel.canEdit()) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.card_edit_menu, menu);
        } else {
            menu.clear();
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_card_save) {
            saveAndRun(super::finish);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Tries to save the current {@link FullCard} from the {@link EditCardViewModel} and then runs the given {@link Runnable}
     * @param runnable
     */
    private void saveAndRun(@NonNull Runnable runnable) {
        if (!viewModel.isPendingCreation()) {
            viewModel.setPendingCreation(true);
            final String title = viewModel.getFullCard().getCard().getTitle();
            if (title == null || title.trim().isEmpty()) {
                viewModel.getFullCard().getCard().setTitle(CardUtil.generateTitleFromDescription(viewModel.getFullCard().getCard().getDescription()));
            }
            viewModel.getFullCard().getCard().setTitle(viewModel.getFullCard().getCard().getTitle().trim());
            binding.title.setText(viewModel.getFullCard().getCard().getTitle());
            if (viewModel.getFullCard().getCard().getTitle().isEmpty()) {
                new BrandedAlertDialogBuilder(this)
                        .setTitle(R.string.title_is_mandatory)
                        .setMessage(R.string.provide_at_least_a_title_or_description)
                        .setPositiveButton(android.R.string.ok, null)
                        .setOnDismissListener(dialog -> viewModel.setPendingCreation(false))
                        .show();
            } else {
                if (viewModel.isCreateMode()) {
                    observeOnce(viewModel.createFullCard(viewModel.getAccount().getId(), viewModel.getBoardId(), viewModel.getFullCard().getCard().getStackId(), viewModel.getFullCard()), EditActivity.this, (card) -> runnable.run());
                } else {
                    observeOnce(viewModel.updateCard(viewModel.getFullCard()), EditActivity.this, (card) -> runnable.run());
                }
            }
        }
    }

    private void setupViewPager() {
        binding.tabLayout.removeAllTabs();
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final CardTabAdapter adapter = new CardTabAdapter(getSupportFragmentManager(), getLifecycle());
        final TabLayoutMediator mediator = new TabLayoutMediator(binding.tabLayout, binding.pager, (tab, position) -> {
            tab.setIcon(!viewModel.isCreateMode() && viewModel.hasCommentsAbility()
                    ? tabIconsWithComments[position]
                    : tabIcons[position]
            );
            tab.setContentDescription(!viewModel.isCreateMode() && viewModel.hasCommentsAbility()
                    ? tabTitlesWithComments[position]
                    : tabTitles[position]
            );
        });
        runOnUiThread(() -> {
            binding.pager.setOffscreenPageLimit(2);
            binding.pager.setAdapter(adapter);
            mediator.attach();
        });

        if (!viewModel.isCreateMode() && viewModel.hasCommentsAbility()) {
            runOnUiThread(() -> {
                mediator.detach();
                adapter.enableComments();
                binding.pager.setOffscreenPageLimit(3);
                mediator.attach();
            });
        }
    }

    private void setupTitle() {
        binding.title.setText(viewModel.getFullCard().getCard().getTitle());
        binding.title.setFilters(new InputFilter[]{new InputFilter.LengthFilter(viewModel.getAccount().getServerDeckVersionAsObject().getCardTitleMaxLength())});
        if (viewModel.canEdit()) {
            if (viewModel.isCreateMode()) {
                binding.title.requestFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                if (viewModel.getFullCard().getCard().getTitle() != null) {
                    binding.title.setSelection(viewModel.getFullCard().getCard().getTitle().length());
                }
            }
            binding.title.setHint(getString(viewModel.isCreateMode() ? R.string.simple_add : R.string.edit));
            binding.title.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    viewModel.getFullCard().getCard().setTitle(binding.title.getText().toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        } else {
            binding.title.setEnabled(false);
        }
    }

    @Override
    public void finish() {
        if (!viewModel.hasChanges() && viewModel.canEdit()) {
            new BrandedAlertDialogBuilder(this)
                    .setTitle(R.string.simple_save)
                    .setMessage(R.string.do_you_want_to_save_your_changes)
                    .setPositiveButton(R.string.simple_save, (dialog, whichButton) -> saveAndRun(super::finish))
                    .setNegativeButton(R.string.simple_discard, (dialog, whichButton) -> super.finish()).show();
        } else {
            super.finish();
        }
    }

    /**
     * Performs a call of {@link AppCompatActivity#finish()} without checking for changes
     */
    public void directFinish() {
        super.finish();
    }

    @Override
    public void applyBrand(int mainColor) {
        if (isBrandingEnabled(this)) {
            final Drawable navigationIcon = binding.toolbar.getNavigationIcon();
            if (navigationIcon == null) {
                DeckLog.error("Expected navigationIcon to be present.");
            } else {
                DrawableCompat.setTint(binding.toolbar.getNavigationIcon(), colorAccent);
            }
            applyBrandToPrimaryTabLayout(mainColor, binding.tabLayout);
        }
    }

    @NonNull
    public static Intent createNewCardIntent(@NonNull Context context, @NonNull Account account, Long boardId, Long stackId, @NonNull String title) {
        return createNewCardIntent(context, account, boardId, stackId)
                .putExtra(BUNDLE_KEY_TITLE, title);
    }

    @NonNull
    public static Intent createNewCardIntent(@NonNull Context context, @NonNull Account account, Long boardId, Long stackId) {
        return createBasicIntent(context, account, boardId)
                .putExtra(BUNDLE_KEY_STACK_ID, stackId);
    }

    @NonNull
    public static Intent createEditCardIntent(@NonNull Context context, @NonNull Account account, Long boardId, Long cardId) {
        return createBasicIntent(context, account, boardId)
                .putExtra(BUNDLE_KEY_CARD_ID, cardId);
    }

    private static Intent createBasicIntent(@NonNull Context context, @NonNull Account account, Long boardId) {
        return new Intent(context, EditActivity.class)
                .putExtra(BUNDLE_KEY_ACCOUNT, account)
                .putExtra(BUNDLE_KEY_BOARD_ID, boardId)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    }
}
