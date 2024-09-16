package it.niedermann.nextcloud.deck.ui.card;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.nextcloud.android.common.ui.theme.utils.ColorRole;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivityEditBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.ocs.Version;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.deck.ui.main.MainActivity;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.util.CardUtil;
import it.niedermann.nextcloud.deck.util.OnTextChangedWatcher;

public class EditActivity extends AppCompatActivity {

    private static final String BUNDLE_KEY_ACCOUNT = "account";
    private static final String BUNDLE_KEY_BOARD_LOCAL_ID = "boardLocalId";
    private static final String BUNDLE_KEY_CARD_LOCAL_ID = "cardLocalId";

    private ActivityEditBinding binding;
    private EditCardViewModel viewModel;

    /**
     * @deprecated This is only here to maintain compatibility with {@link Version#supportsComments()}
     */
    @Deprecated
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

    /**
     * @deprecated This is only here to maintain compatibility with {@link Version#supportsComments()}
     */
    @Deprecated
    private static final int[] tabIcons = new int[]{
            R.drawable.ic_home_24dp,
            R.drawable.ic_attach_file_24dp,
            R.drawable.ic_activity_72dp
    };

    private static final int[] tabIconsWithComments = new int[]{
            R.drawable.ic_home_24dp,
            R.drawable.ic_attach_file_24dp,
            R.drawable.type_comment_36dp,
            R.drawable.ic_activity_72dp
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));

        binding = ActivityEditBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(EditCardViewModel.class);

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        viewModel.getBoardColor().observe(this, this::applyTheme);

        loadDataFromIntent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        viewModel.setBoardColor(ContextCompat.getColor(this, R.color.primary));
        setIntent(intent);
        loadDataFromIntent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    private void loadDataFromIntent() {
        final var args = getIntent().getExtras();

        if (args == null || !args.containsKey(BUNDLE_KEY_ACCOUNT) || !args.containsKey(BUNDLE_KEY_BOARD_LOCAL_ID) || !args.containsKey(BUNDLE_KEY_CARD_LOCAL_ID)) {
            throw new IllegalArgumentException("Provide at least " + BUNDLE_KEY_ACCOUNT + " and " + BUNDLE_KEY_BOARD_LOCAL_ID + " and " + BUNDLE_KEY_CARD_LOCAL_ID + " of the card that should be edited.");
        }

        final var account = (Account) args.getSerializable(BUNDLE_KEY_ACCOUNT);
        if (account == null) {
            throw new IllegalArgumentException(BUNDLE_KEY_ACCOUNT + " must not be null.");
        }

        try {
            viewModel.setAccount(account);
        } catch (NextcloudFilesAppAccountNotFoundException e) {
            throw new RuntimeException(e);
        }

        final long cardLocalId = args.getLong(BUNDLE_KEY_CARD_LOCAL_ID);
        if (cardLocalId <= 0L) {
            throw new IllegalArgumentException(BUNDLE_KEY_CARD_LOCAL_ID + " must be a positive long but was " + cardLocalId);
        }

        final long boardLocalId = args.getLong(BUNDLE_KEY_BOARD_LOCAL_ID);
        if (boardLocalId <= 0L) {
            throw new IllegalArgumentException(BUNDLE_KEY_BOARD_LOCAL_ID + " must be a positive integer but was " + boardLocalId);
        }

        new ReactiveLiveData<>(viewModel.getFullBoardById(account.getId(), boardLocalId))
                .observeOnce(EditActivity.this, fullBoard -> {
                    viewModel.setBoardColor(fullBoard.getBoard().getColor());
                    viewModel.setCanEdit(fullBoard.getBoard().isPermissionEdit());
                    invalidateOptionsMenu();
                    new ReactiveLiveData<>(viewModel.getFullCardWithProjectsByLocalId(account.getId(), cardLocalId))
                            .observeOnce(EditActivity.this, fullCard -> {
                                if (fullCard == null) {
                                    new MaterialAlertDialogBuilder(this)
                                            .setTitle(R.string.card_not_found)
                                            .setMessage(R.string.card_not_found_message)
                                            .setPositiveButton(R.string.simple_close, (a, b) -> super.finish())
                                            .show();
                                } else {
                                    viewModel.initializeExistingCard(boardLocalId, fullCard, account.getServerDeckVersionAsObject().isSupported());
                                    invalidateOptionsMenu();
                                    setupViewPager(account);
                                    setupTitle();
                                }
                            });
                });

        DeckLog.verbose("Finished loading intent data: { accountId =", viewModel.getAccount().getId(), "cardId =", cardLocalId, "}");
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        if (viewModel.canEdit()) {
            getMenuInflater().inflate(R.menu.card_edit_menu, menu);
            @ColorInt final int color = ContextCompat.getColor(this, R.color.onSurface);
            final var utils = ThemeUtils.of(color, this);

            for (int i = 0; i < menu.size(); i++) {
                utils.platform.colorToolbarMenuIcon(this, menu.getItem(i));
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
        if (isTaskRoot()) {
            Intent intent = new Intent(EditActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            finish();
        }
        return true;
    }

    /**
     * Tries to save the current {@link FullCard} from the {@link EditCardViewModel} and then finishes this activity.
     */
    private void saveAndFinish() {
        if (!viewModel.isPendingSaveOperation()) {
            viewModel.setPendingSaveOperation(true);
            final String title = viewModel.getFullCard().getCard().getTitle();
            if (title == null || title.trim().isEmpty()) {
                viewModel.getFullCard().getCard().setTitle(CardUtil.generateTitleFromDescription(viewModel.getFullCard().getCard().getDescription()));
            }
            viewModel.getFullCard().getCard().setTitle(viewModel.getFullCard().getCard().getTitle().trim());
            binding.title.setText(viewModel.getFullCard().getCard().getTitle());
            if (viewModel.getFullCard().getCard().getTitle().isEmpty()) {
                new MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.title_is_mandatory)
                        .setMessage(R.string.provide_at_least_a_title_or_description)
                        .setPositiveButton(android.R.string.ok, null)
                        .setOnDismissListener(dialog -> viewModel.setPendingSaveOperation(false))
                        .show();
            } else {
                viewModel.saveCard((response, headers) -> DeckLog.info("Successfully saved card", response.getCard().getTitle()));
                super.finish();
            }
        }
    }

    private void setupViewPager(@NonNull Account account) {
        binding.tabLayout.removeAllTabs();
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final var adapter = new CardTabAdapter(this, account);
        final var mediator = new TabLayoutMediator(binding.tabLayout, binding.pager, (tab, position) -> {
            tab.setIcon(viewModel.hasCommentsAbility()
                    ? tabIconsWithComments[position]
                    : tabIcons[position]
            );
            tab.setContentDescription(viewModel.hasCommentsAbility()
                    ? tabTitlesWithComments[position]
                    : tabTitles[position]
            );
        });

        binding.pager.setAdapter(adapter);
        if (viewModel.hasCommentsAbility()) {
            adapter.enableComments();
            binding.pager.setOffscreenPageLimit(3);
        } else {
            binding.pager.setOffscreenPageLimit(2);
        }
        mediator.attach();
    }

    private void setupTitle() {
        binding.title.setText(viewModel.getFullCard().getCard().getTitle());
        binding.title.setFilters(new InputFilter[]{new InputFilter.LengthFilter(viewModel.getAccount().getServerDeckVersionAsObject().getCardTitleMaxLength())});
        if (viewModel.canEdit()) {
            binding.title.setHint(R.string.edit);
            binding.title.addTextChangedListener(new OnTextChangedWatcher(s -> viewModel.getFullCard().getCard().setTitle(binding.title.getText().toString())));
        } else {
            binding.title.setEnabled(false);
        }
    }

    @Override
    public void finish() {
        if (!viewModel.hasChanges() && viewModel.canEdit()) {
            new MaterialAlertDialogBuilder(this)
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

    private void applyTheme(int color) {
        final var navigationIcon = binding.toolbar.getNavigationIcon();
        if (navigationIcon == null) {
            DeckLog.error("Expected navigationIcon to be present.");
        } else {
            DrawableCompat.setTint(binding.toolbar.getNavigationIcon(), ContextCompat.getColor(this, R.color.onSurface));
        }

        final var utils = ThemeUtils.of(color, this);

        utils.platform.themeStatusBar(this);
        utils.material.themeToolbar(binding.toolbar);
        utils.platform.colorTextView(binding.title, ColorRole.ON_SURFACE);
        utils.material.themeTabLayoutOnSurface(binding.tabLayout);
    }

    @NonNull
    public static Intent createEditCardIntent(@NonNull Context context, @NonNull Account account, long boardLocalId, long cardLocalId) {
        return new Intent(context, EditActivity.class)
                .putExtra(BUNDLE_KEY_ACCOUNT, account)
                .putExtra(BUNDLE_KEY_BOARD_LOCAL_ID, boardLocalId)
                .putExtra(BUNDLE_KEY_CARD_LOCAL_ID, cardLocalId)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    }
}
