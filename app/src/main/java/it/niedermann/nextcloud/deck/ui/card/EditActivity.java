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
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivityEditBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.ocs.Version;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.deck.util.CardUtil;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.applyBrandToPrimaryTabLayout;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.tintMenuIcon;

public class EditActivity extends AppCompatActivity {
    private static final String BUNDLE_KEY_ACCOUNT = "account";
    private static final String BUNDLE_KEY_BOARD_ID = "boardId";
    private static final String BUNDLE_KEY_STACK_ID = "stackId";
    private static final String BUNDLE_KEY_CARD_ID = "cardId";
    private static final String BUNDLE_KEY_TITLE = "title";
    private static final String BUNDLE_KEY_DESCRIPTION = "description";

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
        viewModel = new ViewModelProvider(this).get(EditCardViewModel.class);

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        viewModel.getBrandingColor().observe(this, this::applyBoardBranding);

        loadDataFromIntent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        viewModel.setBrandingColor(ContextCompat.getColor(this, R.color.primary));
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
            viewModel.setBrandingColor(fullBoard.getBoard().getColor());
            viewModel.setCanEdit(fullBoard.getBoard().isPermissionEdit());
            invalidateOptionsMenu();
            if (viewModel.isCreateMode()) {
                viewModel.initializeNewCard(boardId, args.getLong(BUNDLE_KEY_STACK_ID), account.getServerDeckVersionAsObject().isSupported());
                invalidateOptionsMenu();
                fillTitleAndDescription(viewModel.getFullCard().getCard(), viewModel.getAccount().getServerDeckVersionAsObject(),
                        args.getString(BUNDLE_KEY_TITLE), args.getString(BUNDLE_KEY_DESCRIPTION));
                setupViewPager();
                setupTitle();
            } else {
                observeOnce(viewModel.getFullCardWithProjectsByLocalId(account.getId(), cardId), EditActivity.this, (fullCard) -> {
                    if (fullCard == null) {
                        new AlertDialog.Builder(this)
                                .setTitle(R.string.card_not_found)
                                .setMessage(R.string.card_not_found_message)
                                .setPositiveButton(R.string.simple_close, (a, b) -> super.finish())
                                .show();
                    } else {
                        viewModel.initializeExistingCard(boardId, fullCard, account.getServerDeckVersionAsObject().isSupported());
                        invalidateOptionsMenu();
                        setupViewPager();
                        setupTitle();
                    }
                });
            }
        }));

        DeckLog.verbose("Finished loading intent data: { accountId =", viewModel.getAccount().getId(), "cardId =", cardId, "}");
    }

    private static void fillTitleAndDescription(@NonNull Card card, @NonNull Version version, @Nullable String title, @Nullable String description) {
        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description)) {
            assert title != null;
            if (title.length() > version.getCardTitleMaxLength()) {
                card.setTitle(title.substring(0, version.getCardTitleMaxLength()));
            } else {
                card.setTitle(title);
            }
            card.setDescription(description);
        } else if (!TextUtils.isEmpty(title)) {
            assert title != null;
            if (title.length() > version.getCardTitleMaxLength()) {
                card.setDescription(title);
                card.setTitle(title.substring(0, version.getCardTitleMaxLength()));
            } else {
                card.setTitle(title);
            }
        } else if (!TextUtils.isEmpty(description)) {
            assert description != null;
            if (description.length() > version.getCardTitleMaxLength()) {
                card.setDescription(description);
                card.setTitle(description.substring(0, version.getCardTitleMaxLength()));
            } else {
                card.setTitle(description);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (viewModel.canEdit()) {
            getMenuInflater().inflate(R.menu.card_edit_menu, menu);
            @ColorInt final int colorAccent = ContextCompat.getColor(this, R.color.accent);
            for (int i = 0; i < menu.size(); i++) {
                tintMenuIcon(menu.getItem(i), colorAccent);
            }
        } else {
            menu.clear();
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_card_save) {
            saveAndFinish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return true;
    }

    /**
     * Tries to save the current {@link FullCard} from the {@link EditCardViewModel} and then finishes this activity.
     */
    private void saveAndFinish() {
        if (!viewModel.isPendingCreation()) {
            viewModel.setPendingCreation(true);
            final String title = viewModel.getFullCard().getCard().getTitle();
            if (title == null || title.trim().isEmpty()) {
                viewModel.getFullCard().getCard().setTitle(CardUtil.generateTitleFromDescription(viewModel.getFullCard().getCard().getDescription()));
            }
            viewModel.getFullCard().getCard().setTitle(viewModel.getFullCard().getCard().getTitle().trim());
            binding.title.setText(viewModel.getFullCard().getCard().getTitle());
            if (viewModel.getFullCard().getCard().getTitle().isEmpty()) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_is_mandatory)
                        .setMessage(R.string.provide_at_least_a_title_or_description)
                        .setPositiveButton(android.R.string.ok, null)
                        .setOnDismissListener(dialog -> viewModel.setPendingCreation(false))
                        .show();
            } else {
                viewModel.saveCard(response -> DeckLog.info("Successfully saved card", response.getCard().getTitle()));
                super.finish();
            }
        }
    }

    private void setupViewPager() {
        binding.tabLayout.removeAllTabs();
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final CardTabAdapter adapter = new CardTabAdapter(this);
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
            new AlertDialog.Builder(this)
                    .setTitle(R.string.simple_save)
                    .setMessage(R.string.do_you_want_to_save_your_changes)
                    .setPositiveButton(R.string.simple_save, (dialog, whichButton) -> saveAndFinish())
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

    private void applyBoardBranding(int mainColor) {
        final Drawable navigationIcon = binding.toolbar.getNavigationIcon();
        if (navigationIcon == null) {
            DeckLog.error("Expected navigationIcon to be present.");
        } else {
            DrawableCompat.setTint(binding.toolbar.getNavigationIcon(), ContextCompat.getColor(this, R.color.accent));
        }
        applyBrandToPrimaryTabLayout(mainColor, binding.tabLayout);
    }

    @NonNull
    public static Intent createNewCardIntent(@NonNull Context context, @NonNull Account account, Long boardLocalId, Long stackId, @Nullable String title, @Nullable String description) {
        return createNewCardIntent(context, account, boardLocalId, stackId, title)
                .putExtra(BUNDLE_KEY_DESCRIPTION, description);
    }

    @NonNull
    public static Intent createNewCardIntent(@NonNull Context context, @NonNull Account account, Long boardLocalId, Long stackId, @Nullable String title) {
        return createNewCardIntent(context, account, boardLocalId, stackId)
                .putExtra(BUNDLE_KEY_TITLE, title);
    }

    @NonNull
    public static Intent createNewCardIntent(@NonNull Context context, @NonNull Account account, Long boardLocalId, Long stackId) {
        return createBasicIntent(context, account, boardLocalId)
                .putExtra(BUNDLE_KEY_STACK_ID, stackId);
    }

    @NonNull
    public static Intent createEditCardIntent(@NonNull Context context, @NonNull Account account, Long boardLocalId, Long cardId) {
        return createBasicIntent(context, account, boardLocalId)
                .putExtra(BUNDLE_KEY_CARD_ID, cardId);
    }

    private static Intent createBasicIntent(@NonNull Context context, @NonNull Account account, Long boardLocalId) {
        return new Intent(context, EditActivity.class)
                .putExtra(BUNDLE_KEY_ACCOUNT, account)
                .putExtra(BUNDLE_KEY_BOARD_ID, boardLocalId)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    }
}
